package com.ugochirico.asn1;
	
import java.io.*;
import java.util.*;

public class ASN1OptSequence extends Sequence
{
	public static final byte TAG[] = {(byte)0xA0};
	
	public ASN1OptSequence(ASN1Object obj) throws ASN1ObjectNotFoundException
	{
	    super(TAG, obj.getValue());
	    
//	    if(obj.getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + obj.getTag() + " while needed: " + TAG);
//	    }
	}
	
	public ASN1OptSequence(InputStream in)
		throws ASN1EOFException, IOException, ASN1ObjectNotFoundException
	{
	    super(in);
	    
//	    if((getTag() & 0xF0) != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	    
	    
//		// � optional?
//		if(getTag() & 0xA0 != 0xA0)
//		{
//			// No!
//			throw new ASN1ObjectNotFoundException("errore");
//		}
			
		// Si!		
	}

	public ASN1OptSequence(byte btClass)
	{
	    super(new byte[] { (byte)((TAG[0] | btClass) & 0x00000000FF)}, null);
	}
	
	public ASN1OptSequence(byte[] btContent, int offset, int length)
		throws ASN1EOFException, ASN1ObjectNotFoundException
	{
		super(btContent, offset, length);
		
//		 if((getTag() & 0xF0) != TAG)
//		    {
//		        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//		    }
	}
}

