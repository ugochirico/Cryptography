/*
 * Created on 14-gen-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ugochirico.crypt.ecc.asn1;

import java.io.IOException;
import java.io.InputStream;

import com.ugochirico.asn1.ASN1Boolean;
import com.ugochirico.asn1.ASN1Integer;
import com.ugochirico.asn1.ASN1Object;
import com.ugochirico.asn1.ASN1ObjectNotFoundException;
import com.ugochirico.asn1.ASN1Sequence;
import com.ugochirico.crypt.ecc.*;
import com.ugochirico.crypt.ecc.gfp.*;
import com.ugochirico.math.BigInteger;

/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * Customized and ported on J2ME MIDP1.0
  
 * ECPoint : SEQUENCE {
 * 		x: ASN1Integer
 * 		y: ASN1Integer
 * 		standardCurveFieldSize: ASN1Integer
 */

public class ASN1ECPoint extends ASN1Sequence
{
    public ASN1ECPoint(ASN1Object seq) throws ASN1ObjectNotFoundException
    {
        super(seq);
    }
    
    public ASN1ECPoint(ECPoint p)
    {
        p = p.normalize();
        addElement(new ASN1Integer(p.x.toByteArray()));
        addElement(new ASN1Integer(p.y.toByteArray()));
//        addElement(new ASN1Integer(p.yBit()));
        addElement(new ASN1Integer(p.E.standardCurveName));
        
//        System.out.println("ASN1ECPoint");
//        System.out.println(p.x.toString(16));
//        System.out.println(p.yBit());        
//        System.out.println(p.E.standardCurveName);
        
    }
    
    public ASN1ECPoint(byte[] btVal) throws ASN1ObjectNotFoundException 
    {
        super(btVal, 0, btVal.length);        
    }
    
    public ASN1ECPoint(InputStream ins) throws ASN1ObjectNotFoundException, IOException 
    {
        super(ins);        
    }
        
    public ECPoint getECPoint() throws ASN1ObjectNotFoundException
    {
        ASN1Integer x  = new ASN1Integer(elementAt(0));
        ASN1Integer y  = new ASN1Integer(elementAt(1));
//        ASN1Integer ybit  = new ASN1Integer(elementAt(1));
        ASN1Integer fs = new ASN1Integer(elementAt(2));
        
//        System.out.println("getECPoint");
//        System.out.println(x.bigIntValue().toString(16));
//        System.out.println(ybit.intValue());        
//        System.out.println(fs.intValue());
        
//        ECPoint P = 
//            new ECPoint(
//                    ECCurve.getStandardCurve(fs.intValue()),
//                    new BigInteger(1, x.getValue()),
//        			new BigInteger(1, y.getValue()));
        
        EC ec = ECp.getNamedCurve(fs.intValue());
        
//        return ec.pointFactory(x.bigIntValue(), ybit.intValue());
        return ec.pointFactory(x.bigIntValue(), y.bigIntValue());
        
//        EPoint P = 
//            new EPointp(
//                    ECp.getNamedCurve(fs.intValue()),
//                    new GFp(new BigInteger(1, x.getValue())),
//        			ybit.intValue());
//        return P;
    }
}
