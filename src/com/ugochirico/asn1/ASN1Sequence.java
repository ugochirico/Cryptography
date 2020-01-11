package com.ugochirico.asn1;
	
import java.io.IOException;
import java.io.InputStream;

public class ASN1Sequence extends Sequence
{
	public static final byte TAG[] = {0x30};
		
	public ASN1Sequence(ASN1Object obj) throws ASN1ObjectNotFoundException
	{
	    super(TAG, obj.getValue());
	    
//	    if(obj.getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + obj.getTag() + " while needed: " + TAG);
//	    }
	}
	
	public ASN1Sequence()
	{
	    super(TAG, null);
	}
	
	public ASN1Sequence(InputStream in)
		throws ASN1EOFException, IOException, ASN1ObjectNotFoundException
	{
		super(in);
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}
	
	public ASN1Sequence(byte[] btContent, int offset, int length)
		throws ASN1EOFException, ASN1ObjectNotFoundException
	{
		super(btContent, offset, length);
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}
}
