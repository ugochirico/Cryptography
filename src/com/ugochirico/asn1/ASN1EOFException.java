package com.ugochirico.asn1;

public class ASN1EOFException extends ASN1ObjectNotFoundException
{
    public ASN1EOFException()
    {
        super("EOF reached");
    }
    
}
