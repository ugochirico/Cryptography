/*
 * Created on 14-gen-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ugochirico.crypt.ecclight.asn1;

import java.io.IOException;
import java.io.InputStream;

import com.ugochirico.asn1.ASN1Object;
import com.ugochirico.asn1.ASN1ObjectNotFoundException;
import com.ugochirico.crypt.ecclight.ECPublicKey;

/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * ECPoint : SEQUENCE {
 * 		s: ASN1Integer
 * 		W: ASN1ECPoint
 */
public class ASN1ECPublicKey extends ASN1ECPoint
{
    public ASN1ECPublicKey(ASN1Object seq) throws ASN1ObjectNotFoundException
    {
        super(seq);
    }
    
    public ASN1ECPublicKey(ECPublicKey p)
    {
        super(p.m_W);
    }
    
    public ASN1ECPublicKey(byte[] btVal) throws ASN1ObjectNotFoundException 
    {
        super(btVal);        
    }
    
    public ASN1ECPublicKey(InputStream ins) throws ASN1ObjectNotFoundException, IOException 
    {
        super(ins);        
    }
        
    public ECPublicKey getECPublicKey() throws ASN1ObjectNotFoundException
    {
        return new ECPublicKey(getECPoint());        
    }
}
