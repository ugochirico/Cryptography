package com.ugochirico.asn1;

import java.io.*;





// ASN1IA5String.java: implementation of the CASN1IA5String class.
//
//////////////////////////////////////////////////////////////////////
public class ASN1IA5String extends ASN1Object
{
	public static final byte TAG[] = {0x16};
		
	// costruttori
	
	public ASN1IA5String(ASN1Object obj) throws ASN1ObjectNotFoundException
	{
	    super(TAG, obj.getValue());
	    
//	    if(obj.getTag()[0] != TAG[0])
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + obj.getTag()[0] + " while needed: " + TAG);
//	    }
	}
	
	public ASN1IA5String(InputStream in)
		throws ASN1EOFException, IOException, ASN1ObjectNotFoundException
	{
		super(in);
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}

	public ASN1IA5String(String szIA5String)
	{
		super(TAG, szIA5String.getBytes());
	}
	
	public ASN1IA5String(byte[] btContent, int offset, int length)
		throws ASN1EOFException, ASN1ObjectNotFoundException
	{
		super(btContent, offset, length);	
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}
}
