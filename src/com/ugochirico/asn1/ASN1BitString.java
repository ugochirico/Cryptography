package com.ugochirico.asn1;


import java.io.*;

public class ASN1BitString extends ASN1Object
{
	private static final byte TAG[] = {0x03};

	private boolean m_bEncapsulated;

	private int m_unusedbit;//originariamente era un byte e non un byte[]
	
	public ASN1BitString(ASN1Object obj) throws ASN1ObjectNotFoundException
	{
	    super(TAG, obj.getValue());
	    
//	    if(obj.getTag()[0] != TAG[0])
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + obj.getTag()[0] + " while needed: " + TAG);
//	    }
	}
	
	public ASN1BitString(InputStream in)
		throws ASN1EOFException, IOException, ASN1ObjectNotFoundException
	{
		super(in);
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
		
		// check unused bit
		m_unusedbit = getValue()[0];
		
		// try encapsulated first
		if(getValue()[1] == ASN1Sequence.TAG[0])
		{
			m_bEncapsulated = true;
		}
		else
		{
			m_bEncapsulated = false;
		}
	}

	public ASN1BitString(byte[] btContent, int offset, int length)
		throws ASN1EOFException, ASN1ObjectNotFoundException
	{
		super(btContent, offset, length);	
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}
	
	public ASN1BitString(byte unusedbit, byte[] btVal, boolean bEncapsulated)
	{
	    super(TAG, null);
	    
		m_bEncapsulated = bEncapsulated;
        if(bEncapsulated)
        {
    		byte[] btBuffer;

            ASN1Sequence seq = new ASN1Sequence();
            seq.addElement(new ASN1Integer(btVal));
            
            byte btContent[] = seq.toByteArray();

            btBuffer = new byte[btContent.length + 1];
            btBuffer[0] = unusedbit;
            System.arraycopy(btContent, 0, btBuffer, 1, btContent.length);
            setValue(btBuffer);
        }
        else
        {
            setValue(btVal);
        }
	}

//	public ASN1BitString(boolean bEncapsulated)
//	{
//		m_unusedbit = new byte[1];
//		m_bEncapsulated = bEncapsulated;
//	}
//
//	public void addBitString(byte[] bitString)
//	{
//		if(m_bEncapsulated)
//		{
//			m_pSeq.addInteger(new ASN1Integer(bitString));
//			
//			m_unusedbit[0] = 0x00;
//		}
	/*	else
			throw new ASN1Exception("BITString is defined as not encapsulated");
	*///MOMENTANEAMENTE ELIMINATO
//	}

//	public void addBitString(ASN1Integer obj)
//	{
//		if(m_bEncapsulated)
//		{
//			m_pSeq.addInteger(obj);
//			
//			m_unusedbit[0] = 0x00;
//		}
//	/*	else
//			throw new ASN1Exception("BITString is defined as not encapsulated");
//*/ //MOMENTANEAMENTE ELIMINATO
//	}
}
