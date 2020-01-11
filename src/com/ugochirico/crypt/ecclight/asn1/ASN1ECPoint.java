/*
 * Created on 14-gen-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ugochirico.crypt.ecclight.asn1;

import java.io.IOException;
import java.io.InputStream;

import com.ugochirico.asn1.ASN1Boolean;
import com.ugochirico.asn1.ASN1Integer;
import com.ugochirico.asn1.ASN1Object;
import com.ugochirico.asn1.ASN1ObjectNotFoundException;
import com.ugochirico.asn1.ASN1Sequence;
import com.ugochirico.crypt.ecclight.ECCurve;
import com.ugochirico.crypt.ecclight.ECPoint;
import com.ugochirico.math.BigInteger;
//import com.ugochirico.midp.log.LogViewer;

/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
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
        ////#ifdef USE_YBIT
        addElement(new ASN1Integer(p.ybit()));
        ////#else
        ////addElement(new ASN1Integer(p.y.toByteArray()));
        ////#endif
        addElement(new ASN1Integer(p.E.standardFieldSize));
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
        ASN1Integer fs = new ASN1Integer(elementAt(2));

        ECPoint P;
////        #ifdef USE_YBIT
        ASN1Integer ybit  = new ASN1Integer(elementAt(1));
        P = new ECPoint(
              ECCurve.getStandardCurve(fs.intValue()),
              new BigInteger(1, x.getValue()),
              ybit.intValue());
//        //#else
//        ASN1Integer y  = new ASN1Integer(elementAt(1));
//        P = new ECPoint(
//                    ECCurve.getStandardCurve(fs.intValue()),
//                    new BigInteger(1, x.getValue()),
//                    new BigInteger(1, y.getValue()));
//
//        //#endif

//        //#ifdef LOG
//        System.out.println(P.x.toString(16));
//        System.out.println(P.y.toString(16));
//        System.out.println(fs.bigIntValue().toString(16));
//        //#endif

        return P;
    }
}
