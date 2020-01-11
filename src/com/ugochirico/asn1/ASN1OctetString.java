package com.ugochirico.asn1;

import java.io.IOException;
import java.io.InputStream;

public class ASN1OctetString extends ASN1Object
{
    public static final byte TAG[] = {0x04};
    
    public ASN1OctetString(ASN1Object obj) throws ASN1ObjectNotFoundException
	{
	    super(TAG, obj.getValue());
	    
//	    if(obj.getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + obj.getTag() + " while needed: " + TAG);
//	    }
	}
    
    public ASN1OctetString(InputStream in)
        throws ASN1EOFException, IOException, ASN1ObjectNotFoundException
    {
        super(in);
        
//        if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("" + TAG);
//	    }
    }
    
    public ASN1OctetString(byte[] pbtOctetString)
    {
        super(TAG, pbtOctetString);
    }
    
    public ASN1OctetString(byte[] btContent, int offset, int length)
		throws ASN1EOFException, ASN1ObjectNotFoundException
	{
		super(btContent, offset, length);	
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("" + TAG);
//	    }
	}
    
    public String toStringValue()
    {
        return new String(getValue());
    }
}
