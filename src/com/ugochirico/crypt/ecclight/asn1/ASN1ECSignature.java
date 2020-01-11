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
import com.ugochirico.crypt.ecclight.ECSignature;

/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * Customized and ported on J2ME
 * ECPoint : SEQUENCE {
 * 		s: ASN1Integer
 * 		W: ASN1ECPoint
 */
public class ASN1ECSignature extends ASN1Sequence
{
    public ASN1ECSignature(ASN1Object seq) throws ASN1ObjectNotFoundException
    {
        super(seq);
    }
    
    public ASN1ECSignature(ECSignature s)
    {
        addElement(new ASN1Integer(s.m_nType));
        addElement(new ASN1Integer(s.m_c.toByteArray()));
        addElement(new ASN1Integer(s.m_d.toByteArray()));
    }
    
    public ASN1ECSignature(byte[] btVal) throws ASN1ObjectNotFoundException 
    {
        super(btVal, 0, btVal.length);        
    }
    
    public ASN1ECSignature(InputStream ins) throws ASN1ObjectNotFoundException, IOException 
    {
        super(ins);        
    }
        
    public ECSignature getECSignature() throws ASN1ObjectNotFoundException
    {
        ASN1Integer type  = new ASN1Integer(elementAt(0));
        ASN1Integer c  	  = new ASN1Integer(elementAt(1));
        ASN1Integer d  	  = new ASN1Integer(elementAt(2));
        
        ECSignature sig = new ECSignature(
                				c.bigIntValue(),
                				d.bigIntValue(), 
                				type.intValue());
        			      
        return sig;
    }
}
