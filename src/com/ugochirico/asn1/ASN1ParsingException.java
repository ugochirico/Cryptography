package com.ugochirico.asn1;

public class ASN1ParsingException extends ASN1Exception
{
	public ASN1ParsingException()
	{
		super("Bad ASN1Object parsed");
	}
	
}
