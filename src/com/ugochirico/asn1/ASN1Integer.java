package com.ugochirico.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.ugochirico.math.BigInteger;

public class ASN1Integer extends ASN1Object
{
	public static final byte TAG[] = {0x02};
		
	public ASN1Integer(ASN1Object obj) throws ASN1ObjectNotFoundException
	{
	    super(TAG, obj.getValue());
	    
//	    if(obj.getTag()[0] != TAG[0])
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + obj.getTag()[0] + " while needed: " + TAG);
//	    }
	}
	
	public ASN1Integer(InputStream in)
		throws ASN1EOFException, IOException, ASN1ObjectNotFoundException
	{
		super(in);	
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}
	
	public ASN1Integer(byte[] btContent, int offset, int length)
		throws ASN1EOFException, ASN1ObjectNotFoundException
	{
		super(btContent, offset, length);	
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}
	
	public ASN1Integer(long nVal)
	{
	    super(TAG, null);
	 
	    ByteArrayOutputStream outs = new ByteArrayOutputStream();

		if (nVal == 0)
		{
		    outs.write(0x00);
		}
		else if (nVal == 1)
		{
		    outs.write(0x01);
		}
//		else if (nVal == 0x80)
//		{
//		    outs.write(0x00);
//		    outs.write(0x80);
//		}
		else
		{			
		    BigInteger biVal = new BigInteger("" + nVal);
		    try
            {
                outs.write(biVal.toByteArray());
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
//                e.printStackTrace();
            }
		}
		
//		byte btValue[] = outs.toByteArray();
//		btVal = new byte[btValue.length];
//		
//		// reverse
//		for(int i = 0; i < btValue.length; i++)
//        {
//		    btVal[btVal.length - 1] = btValue[i];                
//        }
		
		setValue(outs.toByteArray());
	}
	
	public ASN1Integer(byte btVal[])
	{
	    super(TAG, btVal);
	}
		
	public int intValue()
	{
	    BigInteger bi = new BigInteger(1, getValue());
	
		return bi.intValue();
	}
	
	public long longValue()
	{
	    BigInteger bi = new BigInteger(1, getValue());
	
		return bi.longValue();
	}
	
	public BigInteger bigIntValue()
	{
	    return new BigInteger(1, getValue());
	}
		
	public String stringValue()
	{
	   return "" + longValue();
	}
}
