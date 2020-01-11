package com.ugochirico.asn1;

import java.io.*;

public class ASN1Null extends ASN1Object
{
	public static final byte TAG[] = {0x05};
	
	public ASN1Null(ASN1Object obj) throws ASN1ObjectNotFoundException
	{
	    super(TAG, obj.getValue());
	    
//	    if(obj.getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + obj.getTag() + " while needed: " + TAG);
//	    }
	}
	
	public ASN1Null(InputStream in)
		throws ASN1EOFException, IOException, ASN1ObjectNotFoundException
	{
		super(in);		
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}
	
	public ASN1Null()
	{
		super(TAG, new byte[0]);
	}
	
	public ASN1Null(byte[] btContent, int offset, int length)
		throws ASN1EOFException, ASN1ObjectNotFoundException
	{
		super(btContent, offset, length);	
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}
}
