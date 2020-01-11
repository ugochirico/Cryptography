package com.ugochirico.asn1;

import java.io.*;
import java.util.*;
import com.ugochirico.util.*;

public class ASN1ObjectIdentifier extends ASN1Object
{
	public static final byte TAG[] = {0x06};
		
	public ASN1ObjectIdentifier(ASN1Object obj) 
		throws ASN1ObjectNotFoundException
	{
	    super(TAG, obj.getValue());
	    
//	    if(obj.getTag()[0] != TAG[0])
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + obj.getTag()[0] + " while needed: " + TAG[0]);
//	    }
	}
	
	public ASN1ObjectIdentifier(InputStream in)
		throws ASN1EOFException, IOException, ASN1ObjectNotFoundException
	{
		super(in);
		
//		if(getTag() != TAG)
//	    {
//	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
//	    }
	}
	
	public ASN1ObjectIdentifier(ASN1ObjectIdentifier objId)
	{
	    super(TAG, objId.getValue());
	}
	
	public ASN1ObjectIdentifier(String strObjId)
	{
		super(TAG, null);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int nVal;
//		int nAux;
		
		com.ugochirico.util.StringTokenizer stTok = new com.ugochirico.util.StringTokenizer(strObjId, ".");
					
		int nFirst = 40 * Integer.valueOf(stTok.nextToken()).intValue() + Integer.valueOf(stTok.nextToken()).intValue();
		if(nFirst > 0xff)
		throw new ASN1BadObjectIdException(strObjId);
		
		try
		{
			out.write((byte)nFirst);
		
//			int i = 0;
			
			while (stTok.hasMoreTokens())
			{
				nVal = Integer.valueOf(stTok.nextToken()).intValue();
				if(nVal == 0)
				{
					out.write(0x00);
				}
				else if (nVal == 1)
				{
					out.write(0x01);
				}
				else
				{
				    // TO DO da risolvere per MIDP 20
//					i = (int)Math.ceil((Math.log(Math.abs(nVal)) / Math.log(2)) / 7); // base 128
//					while (nVal != 0)
//					{
//						nAux = (int)(Math.floor(nVal / Math.pow(128, i - 1)));
//						nVal = nVal - (int)(Math.pow(128, i - 1) * nAux);
//
//						// next value (or with 0x80)
//						if(nVal != 0)
//							nAux |= 0x80;
//														
//						out.write((byte)nAux);
//							
//						i--;
//					}
				}
			}
			
			out.close();
		}
		catch(IOException ex)
		{}
					
		setValue(out.toByteArray());
	}
					
	public ASN1ObjectIdentifier(byte[] btContent, int offset, int length)
		throws ASN1EOFException, ASN1ObjectNotFoundException
	{
		super(btContent, offset, length);
		
		 if(getTag() != TAG)
	    {
	        throw new ASN1ObjectNotFoundException("TAG: " + getTag() + " while needed: " + TAG);
	    }
	}
	
	public boolean equals(ASN1ObjectIdentifier objId)
	{
		int i = 0;
		byte[] btVal    = getValue();
		byte[] btObjVal = objId.getValue();
	
		if(btVal.length != btObjVal.length)
			return false;
		
		while (i < btVal.length && btVal[i] == btObjVal[i])
		{
			i++;
		}
		
		return i == btObjVal.length;
	}
	
	
	
	public boolean equals(String strObjId)
		throws ASN1BadObjectIdException
	{
		return equals(new ASN1ObjectIdentifier(strObjId));
	}
	
}
