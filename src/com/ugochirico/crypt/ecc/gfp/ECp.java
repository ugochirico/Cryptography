/* $Id$
 *
 * Copyright (C) 1995-1999 Systemics Ltd.
 * on behalf of the Cryptix Development Team. All rights reserved.
 *
 * Use, modification, copying and distribution of this software is subject to
 * the terms and conditions of the Cryptix General Licence. You should have
 * received a copy of the Cryptix General License along with this library;
 * if not, you can download a copy from http://www.cryptix.org/ .
 */

package com.ugochirico.crypt.ecc.gfp;

import com.ugochirico.math.*;
import com.ugochirico.util.Random;

import com.ugochirico.crypt.ecc.EC;
import com.ugochirico.crypt.ecc.ECPoint;
import com.ugochirico.crypt.ecc.GF;
import com.ugochirico.crypt.ecc.InvalidECParamsException;
import com.ugochirico.crypt.ecc.PointNotOnCurveException;
import com.ugochirico.crypt.ecc.gf2m.ECPoint2m;
import com.ugochirico.crypt.ecc.gf2m.GF2m;

/**
 * @author  Paulo S. L. M. Barreto <pbarreto@cryptix.org>
 * Porting on J2ME MIDP1.0 by Ugo Chirico <ugo.chirico@ugosweb.com>
 */
/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * Customized and ported on J2ME MIDP1.0
 */
public class ECp extends EC {

//    private static int count = 0;
    /**
     * Convenient GF(p) constant
     */
    GFp gfZero;

    /**
     * Convenient GF(p) constant
     */
    GFp gfOne;

    /**
     * Convenient GF(p) constant
     */
    GFp qMinus3;

    /**
     * Create a partial description of the elliptic curve over GF(p) satisfying
     * the equation y^2 = x^3 + ax + b with near-prime group order u = k*r
     * with a specified base point of prime order r.  The base point is left undefined.
     *
     * @param   p   an approximation for the size q of the underlying
     *              finite field GF(q) (q is taken to be the nearest odd prime
     *              not smaller than p or 3)
     * @param   a   curve equation coefficient
     * @param   b   curve equation coefficient
     * @param   k   cofactor of the curve group order
     * @param   r   prime order of the cryptographic subgroup
     *
     * @exception    InvalidECParamsException    if the selected parameters don't define a proper curve
     */
    private ECp(BigInteger p, GFp a, GFp b, BigInteger k, BigInteger r)
        throws InvalidECParamsException
        {
//        MIABO.m_waitForm.append("ECp 5");

        this.q = p;//nextPrime(p); // eliminato perche su P900 passRabinMiller non funziona
        this.a = a;
        this.b = b;
        this.k = k;
        this.r = r;
        this.G = null;
//        if (!r.isProbablePrime(GFp.PRIMALITY_CERTAINTY)) {
//            throw new InvalidECParamsException("The order of the base point is not prime");
//        }
        gfZero = new GFp(q, ZERO);
        gfOne  = new GFp(q, ONE);
        // CAVEAT: the infinity attribute MUST be set AFTER gfZero and gfOne!
        infinity = new ECPointp(this);
        qMinus3  = new GFp(q, q.subtract(THREE));
    }

    /**
     * Create a description of the elliptic curve over GF(p) satisfying
     * the equation y^2 = x^3 + ax + b with near-prime group order u = k*r
     * with a specified base point of prime order r.
     *
     * @param   p   an approximation for the size q of the underlying
     *              finite field GF(q) (q is taken to be the nearest odd prime
     *              not smaller than p or 3)
     * @param   a   curve equation coefficient
     * @param   b   curve equation coefficient
     * @param   k   cofactor of the curve group order
     * @param   r   prime order of the cryptographic subgroup
     * @param   G   description of base point of order r on the curve
     *
     * @exception    InvalidECParamsException    if the selected parameters don't define a proper curve
     */
    public ECp(BigInteger p, GFp a, GFp b, BigInteger k, BigInteger r, String G)
        throws InvalidECParamsException
        {
        this(p, a, b, k, r);

        int pc = Integer.parseInt(G.substring(0, 2), 16);
        int octetCount, coordLen, xPos, yPos, zPos;
        switch (pc) {
        case 0x02:
        case 0x03:
            // compressed form:
            try {
//                String x = G.substring(2);
                this.G = new ECPointp(this,
                    new GFp(q, G.substring(2)), // x coordinate
                    pc & 1);
            } catch (PointNotOnCurveException e) {
                throw new InvalidECParamsException("Invalid base point description");
            }
            break;
        case 0x04:
            // expanded form:
            try {
                octetCount  = (G.length() >>> 1);
                coordLen    = octetCount - 1; // 2*((octetCount - 1)/2)
                xPos        = 2;
                yPos        = xPos + coordLen;
                zPos        = yPos + coordLen;
                this.G = new ECPointp(this,
                    new GFp(q, G.substring(xPos, yPos)),    // x-coordinate
                    new GFp(q, G.substring(yPos, zPos)));    // y coordinate
            } catch (PointNotOnCurveException e) {
                throw new InvalidECParamsException("Invalid base point description");
            }
            break;
        case 0x06:
        case 0x07:
            // hybrid form:
            try {
                octetCount    = (G.length() >>> 1);
                coordLen    = octetCount - 1; // 2*((octetCount - 1)/2)
                xPos        = 2;
                yPos        = xPos + coordLen;
                zPos        = yPos + coordLen;
                this.G = new ECPointp(this,
                    new GFp(q, G.substring(xPos, yPos)),    // x-coordinate
                    new GFp(q, G.substring(yPos, zPos)));    // y coordinate
                // TODO: compare compressed and expanded forms in hybrid representation
            } catch (PointNotOnCurveException e) {
                throw new InvalidECParamsException("Invalid base point description");
            }
            break;
        default:
            throw new InvalidECParamsException("Invalid base point description");
        }
        /*
        if (!this.G.multiply(r).isZero()) {
            throw new InvalidECParamsException("Wrong order");
        }
        */
    }

    /**
     * Create a description of the elliptic curve over GF(p) satisfying
     * the equation y^2 = x^3 + ax + b with near-prime group order u = k*r
     * with a random base point of prime order r.
     *
     * @param   p   an approximation for the size q of the underlying
     *              finite field GF(q) (q is taken to be the nearest odd prime
     *              not smaller than p or 3)
     * @param   a   curve equation coefficient
     * @param   b   curve equation coefficient
     * @param   k   cofactor of the curve group order
     * @param   r   prime order of the cryptographic subgroup
     * @param   rand    cryptographically strong PRNG
     *
     * @exception    InvalidECParamsException    if the selected parameters don't define a proper curve
     */
    public ECp(BigInteger p, GFp a, GFp b, BigInteger k, BigInteger r, Random rand)
        throws InvalidECParamsException {
        this(p, a, b, k, r);
        // generate random base point of order r:
        do {
            ECPointp P = new ECPointp(this, rand);
            this.G = P.multiply(k);
        } while (this.G.isZero());
        if (!this.G.multiply(r).isZero()) {
            throw new InvalidECParamsException("Wrong order");
        }
    }

//    /**
//     * Create a description of random a elliptic curve over GF(p)
//     *
//     * @param   p   an approximation for the size q of the underlying
//     *              finite field GF(q) (q is taken to be the nearest odd prime
//     *              not smaller than p or 3)
//     * @param   rand    cryptographically strong PRNG
//     */
//    public ECp(BigInteger p, Random rand) throws InvalidECParamsException {
//        // TODO: implement Schoof-Elkies-Atkin-Lercier curve parameter generation method
//        throw new InvalidECParamsException("Constructor not yet implemented");
//    }

    public ECPoint pointFactory(BigInteger x, BigInteger y)
    {
        return new ECPointp(this, new GFp(this.q, x), new GFp(this.q, y));
    }

    public ECPoint pointFactory(BigInteger x, int ybit)
    {
        return new ECPointp(this, new GFp(this.q, x), ybit);
    }

    public ECPoint pointFactory(byte[] data)
    {
    	int ybit;
    	BigInteger x;
    	BigInteger y;
    	
    	if(data.length == 1 && data[0] == 0)
    	{
    		x = BigInteger.ZERO;
    		ybit = 0;
    		return new ECPointp(this, new GFp(this.q, x), ybit);
    	}
    	else
    	{
    		int pc = data[0];
    		
    		if ((pc & ECPoint.COMPRESSED) != 0) 
    		{
    			ybit = pc & 0x01;
    			int len = (data.length - 1);
    			byte[] xdata = new byte[len];
    			System.arraycopy(data, 1, xdata, 0, len);
    			x = new BigInteger(1, xdata);    			
    			return new ECPointp(this, new GFp(this.q, x), ybit);
    	    }
//    		else if ((pc & ECPoint.EXPANDED) != 0) 
//    		{
//    			int len = (data.length - 1) / 2;
//    			byte xdata
//    			x = new BigInteger(1, data)
//    			osY = y.toByteArray();
//    	            resLen += osY.length;
//    	    }
//    	        result = new byte[resLen];
//    	        result[0] = (byte)pc;
//    	        System.arraycopy(osX, 0, result, 1, osX.length);
//    	        if (osY != null) {
//    	            System.arraycopy(osY, 0, result, 1 + osX.length, osY.length);
//    	        }
    	}
    	
    	return null;
//        return new ECPoint2m(this, new GF2m(this.q.getLowestSetBit(), x), ybit);        
    }
    /**
     * Check whether this curve contains a given point
     * (i.e. whether that point satisfies the curve equation)
     *
     * @param   P   the point whose pertinence or not to this curve is to be determined
     *
     * @return  true if this curve contains P, otherwise false
     */
    public boolean contains(ECPoint P) {
        if (!(P instanceof ECPointp)) {
            return false;
        }
        if (P.isZero()) {
            return true;
        }
        ECPointp Q = (ECPointp)P; // shorthand to make the expressions more readable
        // check the projective equation y^2 = x^3 + a.x.z^4 + b.z^6:
        GF z2 = Q.z.square();
        GF z4 = z2.square();
        GF z6 = z4.multiply(z2);
        // y^2 = x(x^2 + a.z4) + b.z6
        return Q.y.square().equals(Q.x.multiply(Q.x.square().add(a.multiply(z4))).add(b.multiply(z6)));
    }

    /**
     * Compute the nearest odd prime not smaller than a given BigInteger
     *
     * @param   q   the lower bound for prime search
     *
     * @return  the smallest odd prime not smaller than q
     */
    public static BigInteger nextPrime(BigInteger q)
    {
//        MIABO.m_waitForm.append("next prime");

        BigInteger p = q;
        if (!p.testBit(0))
        {
//            MIABO.m_waitForm.append("test bit 0");
            p = p.add(ONE); // p must be an odd prime
        }

        while (p.equals(THREE))
        {
//            MIABO.m_waitForm.append("equals 3");
            p = p.add(TWO); // p must be larger than 3
        }
        while (!p.isProbablePrime(GFp.PRIMALITY_CERTAINTY))
        {
//            count++;
//            MIABO.m_waitForm.update(count + " !isProbalePrime ",1);
//            MIABO.m_waitForm.append("!isProbalePrime");
            p = p.add(TWO);
        }

//        MIABO.m_waitForm.append("nextPrimeOk");
        return p;
    }

    private static final int primeCurve = 0x0100;

    /**
     * X9F1 named curves
     */
    public static final int
        prime192v1 = primeCurve |  1, // J.5.1, example 1
        prime192v2 = primeCurve |  2, // J.5.1, example 2
        prime192v3 = primeCurve |  3, // J.5.1, example 3
        prime239v1 = primeCurve |  4, // J.5.2, example 1
        prime239v2 = primeCurve |  5, // J.5.2, example 2
        prime239v3 = primeCurve |  6, // J.5.2, example 3
        prime256v1 = primeCurve |  7; // J.5.3, example 1

    /**
     * Build an X9.62 named curve
     *
     * @param   curveName   a constant representing the X9.62 curve
     *
     * @return  the desired X9.62 curve, or null if the curve name is invalid or unsupported
     */
    public static ECp getNamedCurve(int curveName)
    {
        if(curveTbl.containsKey("" + curveName))
            return (ECp)curveTbl.get("" + curveName);

//        MIABO.m_waitForm.append("getNamedCurve ");

        BigInteger p;
        ECp ec;
        switch (curveName)
        {
        case prime192v1:
            p = new BigInteger("6277101735386680763835789423207666416083908700390324961279");
            ec = new ECp(p,
                            new GFp(p, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFC"),
                            new GFp(p, "64210519E59C80E70FA7E9AB72243049FEB8DEECC146B9B1"),
                            BigInteger.valueOf(1L),
                            new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFF99DEF836146BC9B1B4D22831", 16),
                            "03188DA80EB03090F67CBF20EB43A18800F4FF0AFD82FF1012");
            break;

        case prime192v2:
            p = new BigInteger("6277101735386680763835789423207666416083908700390324961279");
            ec = new ECp(p,
                            new GFp(p, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFC"),
                            new GFp(p, "CC22D6DFB95C6B25E49C0D6364A4E5980C393AA21668D953"),
                            BigInteger.valueOf(1L),
                            new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFE5FB1A724DC80418648D8DD31", 16),
                            "03EEA2BAE7E1497842F2DE7769CFE9C989C072AD696F48034A");
            break;

        case prime192v3:
            p = new BigInteger("6277101735386680763835789423207666416083908700390324961279");
            ec =  new ECp(p,
                            new GFp(p, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFC"),
                            new GFp(p, "22123DC2395A05CAA7423DAECCC94760A7D462256BD56916"),
                            BigInteger.valueOf(1L),
                            new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFF7A62D031C83F4294F640EC13", 16),
                            "027D29778100C65A1DA1783716588DCE2B8B4AEE8E228F1896");
            break;

        case prime239v1:
            p = new BigInteger("883423532389192164791648750360308885314476597252960362792450860609699839");
            ec =  new ECp(p,
                            new GFp(p, "7FFFFFFFFFFFFFFFFFFFFFFF7FFFFFFFFFFF8000000000007FFFFFFFFFFC"),
                            new GFp(p, "6B016C3BDCF18941D0D654921475CA71A9DB2FB27D1D37796185C2942C0A"),
                            BigInteger.valueOf(1L),
                            new BigInteger("7FFFFFFFFFFFFFFFFFFFFFFF7FFFFF9E5E9A9F5D9071FBD1522688909D0B", 16),
                            "020FFA963CDCA8816CCC33B8642BEDF905C3D358573D3F27FBBD3B3CB9AAAF");
            break;

        case prime239v2:
            p = new BigInteger("883423532389192164791648750360308885314476597252960362792450860609699839");
            ec =  new ECp(p,
                            new GFp(p, "7FFFFFFFFFFFFFFFFFFFFFFF7FFFFFFFFFFF8000000000007FFFFFFFFFFC"),
                            new GFp(p, "617FAB6832576CBBFED50D99F0249C3FEE58B94BA0038C7AE84C8C832F2C"),
                            BigInteger.valueOf(1L),
                            new BigInteger("7FFFFFFFFFFFFFFFFFFFFFFF800000CFA7E8594377D414C03821BC582063", 16),
                            "0238AF09D98727705120C921BB5E9E26296A3CDCF2F35757A0EAFD87B830E7");
            break;

        case prime239v3:
            p = new BigInteger("883423532389192164791648750360308885314476597252960362792450860609699839");
            ec =  new ECp(p,
                            new GFp(p, "7FFFFFFFFFFFFFFFFFFFFFFF7FFFFFFFFFFF8000000000007FFFFFFFFFFC"),
                            new GFp(p, "255705FA2A306654B1F4CB03D6A750A30C250102D4988717D9BA15AB6D3E"),
                            BigInteger.valueOf(1L),
                            new BigInteger("7FFFFFFFFFFFFFFFFFFFFFFF7FFFFF975DEB41B3A6057C3C432146526551", 16),
                            "036768AE8E18BB92CFCF005C949AA2C6D94853D0E660BBF854B1C9505FE95A");
            break;

        case prime256v1:
            p = new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951");
            ec =  new ECp(p,
                            new GFp(p, "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC"),
                            new GFp(p, "5AC635D8AA3A93E7B3EBBD55769886BC651D06B0CC53B0F63BCE3C3E27D2604B"),
                            BigInteger.valueOf(1L),
                            new BigInteger("FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551", 16),
                            "036B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296");
            break;

        default:
            return null;
        }

        ec.setStandardCurveName(curveName);
        curveTbl.put(""+curveName, ec);

        return ec;
    }
}
