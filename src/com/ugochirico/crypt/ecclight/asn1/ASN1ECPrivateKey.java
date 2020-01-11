/*
 * Created on 14-gen-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ugochirico.crypt.ecclight.asn1;

import java.io.IOException;
import java.io.InputStream;

import com.ugochirico.asn1.ASN1Integer;
import com.ugochirico.asn1.ASN1Object;
import com.ugochirico.asn1.ASN1ObjectNotFoundException;
import com.ugochirico.asn1.ASN1Sequence;
import com.ugochirico.crypt.ecclight.ECCurve;
import com.ugochirico.crypt.ecclight.ECPrivateKey;
import com.ugochirico.math.BigInteger;

/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * ECPoint : SEQUENCE {
 * 		s: ASN1Integer
 * 		standardFieldSize: ASN1Integer
 */
public class ASN1ECPrivateKey extends ASN1Sequence
{
    public ASN1ECPrivateKey(ASN1Object seq) throws ASN1ObjectNotFoundException
    {
        super(seq);
    }
    
    public ASN1ECPrivateKey(ECPrivateKey p)
    {
        addElement(new ASN1Integer(p.m_s.toByteArray()));
        addElement(new ASN1Integer(p.m_W.E.standardFieldSize));
    }
    
    public ASN1ECPrivateKey(byte[] btVal) throws ASN1ObjectNotFoundException 
    {
        super(btVal, 0, btVal.length);        
    }
    
    public ASN1ECPrivateKey(InputStream ins) throws ASN1ObjectNotFoundException, IOException 
    {
        super(ins);        
    }
        
    public ECPrivateKey getECPrivateKey() throws ASN1ObjectNotFoundException
    {
        ASN1Integer s  = new ASN1Integer(elementAt(0));
        ASN1Integer fs  = new ASN1Integer(elementAt(1));
        
        ECPrivateKey pk = new ECPrivateKey(
                				ECCurve.getStandardCurve(fs.intValue()),
                				new BigInteger(1, s.getValue()));
        			      
        return pk;
    }
}
