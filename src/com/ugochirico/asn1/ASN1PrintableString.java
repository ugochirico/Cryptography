package com.ugochirico.asn1;
import java.io.IOException;
import java.io.InputStream;

// ASN1PrintableString.java: implementation of the ASN1PrintableString class.
//
//////////////////////////////////////////////////////////////////////
public class ASN1PrintableString extends ASN1Object
{
	private static final byte TAG[] = {0x13};
   	
	// costruttori
	public ASN1PrintableString(ASN1Object obj) throws ASN1ObjectNotFoundException
	{
	    super(TAG, obj.getValue());
	    
//	    if(obj.getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + obj.getTag() + " while needed: " + TAG);
//	    }
	}
	
	public ASN1PrintableString(InputStream in)
		throws ASN1EOFException, IOException, ASN1ObjectNotFoundException
	{
		super(in);
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}

	public ASN1PrintableString(String szPrintableString)
	{
	    super(TAG, szPrintableString.getBytes());	    
	}

    public ASN1PrintableString(byte[] btContent, int offset, int length)
		throws ASN1EOFException, ASN1ObjectNotFoundException
	{
		super(btContent, offset, length);	
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}
    
    public String toStringValue()
    {
        return new String(getValue());
    }
}
