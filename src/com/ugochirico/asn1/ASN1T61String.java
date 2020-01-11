package com.ugochirico.asn1;
import java.io.*;
import java.lang.*;

// ASN1PrintableString.java: implementation of the ASN1PrintableString class.
//
//////////////////////////////////////////////////////////////////////
public class ASN1T61String extends ASN1Object
{
    public static final byte TAG[] = {0x14};
       
    // costruttori
    public ASN1T61String(ASN1Object obj) throws ASN1ObjectNotFoundException
	{
	    super(TAG, obj.getValue());
	    
//	    if(obj.getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + obj.getTag() + " while needed: " + TAG);
//	    }
	}
    
    public ASN1T61String(InputStream in)
        throws ASN1EOFException, IOException, ASN1ObjectNotFoundException
    {
        super(in);
        
//        if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
    }

    public ASN1T61String(String szPrintableString)
    {
        super(TAG, szPrintableString.getBytes());        
    }

    public ASN1T61String(byte[] btContent, int offset, int length)
		throws ASN1EOFException, ASN1ObjectNotFoundException
	{
		super(btContent, offset, length);
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}
}
