package com.ugochirico.asn1;

import java.io.IOException;
import java.io.InputStream;

public class ASN1Boolean extends ASN1Object
{
	public static final byte TAG[] = {0x01};
	
	public ASN1Boolean(ASN1Object obj) throws ASN1ObjectNotFoundException
	{
	    super(TAG, obj.getValue());
	    
//	    if(obj.getTag()[0] != TAG[0])
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + obj.getTag()[0] + " while needed: " + TAG);
//	    }
	}
	
	public ASN1Boolean(InputStream in)
		throws ASN1EOFException, IOException, ASN1ObjectNotFoundException
	{
		super(in);
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}
	
	public ASN1Boolean(boolean b)
	{
	    super(TAG, null);
		byte[] pbtVal = new byte[1];
		pbtVal[0] = (byte)(b ? 0x01 : 0x00);
		setValue(pbtVal);
	}
	
	public ASN1Boolean(byte[] btContent, int offset, int length)
		throws ASN1EOFException, ASN1ObjectNotFoundException
	{
		super(btContent, offset, length);	
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}	
	
	public boolean getBooleanValue()
	{
	    return getValue()[0] == 0x01;
	}
}
