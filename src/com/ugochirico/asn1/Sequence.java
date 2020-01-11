package com.ugochirico.asn1;
	
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Vector;

import com.ugochirico.util.ArrayUtil;

public class Sequence extends ASN1Object
{
	protected Sequence(byte[] btTag, byte[] btValue)
	{
	    super(btTag, btValue);
	}
	
	public Sequence(InputStream in)
		throws ASN1EOFException, IOException, ASN1ObjectNotFoundException
	{
		super(in);
	}
	
	public Sequence(byte[] btContent, int offset, int length)
		throws ASN1EOFException, ASN1ObjectNotFoundException
	{
		super(btContent, offset, length);		
	}
		
	public void addElement(ASN1Object obj)
	{
	    byte serializedObj[] = obj.toByteArray();
	    byte btOldVal[] = getValue();
	    if(btOldVal == null)
	    {
	        setValue(serializedObj);
	    }
	    else
	    {
		    byte btNewVal[] = new byte[btOldVal.length + serializedObj.length];
		    // copy old val
		    System.arraycopy(btOldVal, 0, btNewVal, 0, btOldVal.length);
		    // copy val to add
		    System.arraycopy(serializedObj, 0, btNewVal, btOldVal.length, serializedObj.length);
		    // set new val
	        setValue(btNewVal);
	    }
	}
	
    public void setElementAt(ASN1Object obj, int nPos) throws ASN1EOFException
    {
//        if(nPos < 0 || nPos > size())
//            throw new IllegalArgumentException("Invalid position:" + nPos);
//
//        byte btOldVal[] = getValue();
//        ByteArrayOutputStream bouts = new ByteArrayOutputStream();                
//
//        if(nPos == 0)
//        {            
//            int offset = getOffset(1);
//            
////          riscrive il primo elemento
//            bouts.write(obj.getValue(), 0, obj.getLength());
//            
//            // riscrive gli altri elementi a partire dall offset
//            bouts.write(btOldVal, offset, btOldVal.length - offset);
//        }
//        else
//        {
//            int offset  = getOffset(nPos);
//            int offset1 = getOffset(nPos + 1);
//            
//            // copy old val fino all'offset
//            bouts.write(btOldVal, 0, offset);
//
//            // riscrive l'elemento da impostare 
//            bouts.write(obj.getValue(), 0, obj.getLength());
//            
//            // copy the rest of the old val skipping the nPos-th
//            bouts.write(btOldVal, offset1, btOldVal.length - offset1);
//        }
//        
//        //    set new val
//        setValue(bouts.toByteArray());
//        
        
        removeElementAt(nPos);
        addElementAt(obj, nPos);
    }
    
    public void removeElementAt(int nPos) throws ASN1EOFException
    {
        if(nPos < 0 || nPos > size())
            throw new IllegalArgumentException("Invalid position:" + nPos);

        byte btOldVal[] = getValue();
        ByteArrayOutputStream bouts = new ByteArrayOutputStream();                

        if(btOldVal.length == 0)
        {
            // do nothing
        }   
        else if(nPos == 0)
        {
            // elimina la prima
            int offset = getOffset(1);

            bouts.write(btOldVal, offset, btOldVal.length - offset);
        }
        else
        {
            int offset  = getOffset(nPos);
            int offset1 = getOffset(nPos + 1);
            
            // copy old val fino all'offset
            bouts.write(btOldVal, 0, offset);

            // copy the rest of the old val skipping the nPos-th
            bouts.write(btOldVal, offset1, btOldVal.length - offset1);
        }
        
        //    set new val
        setValue(bouts.toByteArray());
    }
    
	public void addElementAt(ASN1Object obj, int nPos) throws ASN1EOFException
	{
	    if(nPos < 0 || nPos > size())
	        throw new IllegalArgumentException("Invalid position:" + nPos);

	    byte serializedObj[] = obj.toByteArray();
	    byte btOldVal[] = getValue();
	    byte btNewVal[];
	    
	    if(btOldVal == null)
	    {
	        btNewVal = serializedObj;
	    }
	    else if(nPos == 0)
	    {
		    btNewVal = new byte[btOldVal.length + serializedObj.length];
		    
		    // copy new val 
		    System.arraycopy(serializedObj, 0, btNewVal, 0, serializedObj.length);
		    // copy old val
		    System.arraycopy(btOldVal, 0, btNewVal, serializedObj.length, btOldVal.length);    
	    }
	    else
	    {
		    int offset = getOffset(nPos);

		    btNewVal = new byte[btOldVal.length + serializedObj.length];
		    // copy old val
		    System.arraycopy(btOldVal, 0, btNewVal, 0, offset);
		    // copy val to add
		    System.arraycopy(serializedObj, 0, btNewVal, offset, serializedObj.length);
		    // copy the rest of the old val
		    System.arraycopy(btOldVal, offset, btNewVal, offset + serializedObj.length, btOldVal.length - offset);
	    }
	    
	    //	  set new val
        setValue(btNewVal);
	}
	
	public ASN1Object elementAt(int nPos) throws ASN1EOFException
	{
//        System.out.println(Encoder.bytesToHexString(getValue()));
        
        int offset = getOffset(nPos);
//        System.out.println("offset: " + offset);
        
//        System.out.println(Encoder.bytesToHexString(getValue(), offset, getLength() - offset));
                
        return new ASN1Object(getValue(), offset, getLength() - offset);
	}
	
	public ASN1Object elementWithTag(byte[] tag) throws ASN1EOFException
	{
//        System.out.println(Encoder.bytesToHexString(getValue()));
        int count = size();
        
        for(int i = 0; i < count; i++)
        {
        	int offset = getOffset(i);
        	
        	ASN1Object obj = new ASN1Object(getValue(), offset, getLength() - offset);
        	
        	if(ArrayUtil.arrayCompare(tag, 0, obj.getTag(), 0, tag.length) == 0)
        		return obj;        	
        }
        
        return null;
	}
	
    public void removeAll() throws ASN1EOFException
    {
        while(size() > 0)
            removeElementAt(0);
    }
    
	public int size()
	{
		return makeComponentVect().size();
	}
	
	public boolean isPresent(int nPos)
	{
	    if(nPos < 0)
	        throw new IllegalArgumentException("Invalid position: " + nPos);
	    
	    return nPos < size();
	}
	
	private Vector makeComponentVect()
	{
	    Vector componentVect = new Vector();
	    
	    try
        {
            byte btContent[] = getValue();
            int offset = 0;
            byte objVal[];
            while(offset < btContent.length)
            {
                objVal = new ASN1Object(btContent, offset, btContent.length).toByteArray();
                componentVect.addElement(objVal);
                offset += objVal.length;
            }
        }
        catch (ASN1EOFException e)
        {
            // TODO Auto-generated catch block
//            e.printStackTrace();
        }
	    
	    return componentVect;
	}
	
	private int getOffset(int nPos) throws ASN1EOFException
	{	
	    byte btContent[] = getValue();
	    int offset = 0;
	    int objLen;
	    int i = 0;
        while(i < nPos)
        {
            objLen = (new ASN1Object(btContent, offset, btContent.length - offset)).getSerializedLength();
            offset += objLen;
            i++;
        }
        
        return offset;
	}
	
	
	
	
	
	//funzione aggiunta il 1/08/2001 da Denise
//	public Vector getSequenceVect()
//	{
//		return m_componentVect;
//	}
//
//	//funzione aggiunta il 1/08/2001 da Denise
//	protected ASN1Object getSequenceValueAt(int i)
//	{
//		return (ASN1Object)m_componentVect.elementAt(i);
//	}
//
}
