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

package com.ugochirico.crypt.ecc;

import java.util.Hashtable;

import com.ugochirico.math.*;
import com.ugochirico.util.Random;


/**
 * The EC class is an abstraction of elliptic curves considered as a whole,
 * i.e. sets of coordinate pairs satisfying the curve equation with certain parameters.
 *
 * @author  Paulo S. L. M. Barreto <pbarreto@cryptix.org>
/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * Customized and ported on J2ME MIDP1.0
 */
public abstract class EC {

    /**
     * Convenient BigInteger constants
     */
    protected static final BigInteger
        ZERO  = BigInteger.valueOf(0L),
        ONE   = BigInteger.valueOf(1L),
        TWO   = BigInteger.valueOf(2L),
        THREE = BigInteger.valueOf(3L);

    public static Hashtable curveTbl = new Hashtable();

    /**
     * Size of the underlying finite field GF(q)
     */
    public BigInteger q;

    /**
     * Coefficient of the elliptic curve equation
     */
    public GF a;

    /**
     * Coefficient of the elliptic curve equation
     */
    public GF b;

    /**
     * Cofactor of the base point (curve order u = k*r)
     */
    public BigInteger k;

    /**
     * Prime order of the base point (curve order u = k*r)
     */
    public BigInteger r;

    /**
     * The base point of large prime order r
     */
    public ECPoint G;

    /**
     * The point at infinity
     */
    public ECPoint infinity;


    public int standardCurveName;

    /**
     * Return the size q of the field GF(q) over which this curve is defined
     *
     * @return  the size q of the field GF(q) over which this curve is defined
     */
    public BigInteger getFieldSize() {
        return q;
    }

    /**
     * Set the standard curve name
     */
    public void setStandardCurveName(int curveName)
    {
        standardCurveName = curveName;
    }

    /**
     * Return the standard curve name
     *
     * @return  the standard curve name of this curve
     */
    public int getStandardCurveName()
    {
        return standardCurveName ;
    }


    /**
     * Return the A coefficient of the equation defining this curve
     *
     * @return  the A coefficient of the equation defining this curve
     */
    public GF getA() {
        return a;
    }

    /**
     * Return the B coefficient of the equation defining this curve
     *
     * @return  the B coefficient of the equation defining this curve
     */
    public GF getB() {
        return b;
    }

    /**
     * Return the number of points in this curve (i.e. the curve order)
     *
     * @return  number of points in this curve (i.e. the curve order)
     */
    public BigInteger getCurveOrder() {
        return k.multiply(r);
    }

    /**
     * Return the large prime factor r of the curve order u = k*r
     *
     * @return  the large prime factor r of the curve order u = k*r
     */
    public BigInteger getBasePointOrder() {
        return r;
    }

    /**
     * Return the cofactor k of the curve order u = k*r
     *
     * @return  the cofactor k of the curve order u = k*r
     */
    public BigInteger getCofactor() {
        return k;
    }

    /**
     * Return the base point of order r on this elliptic curve
     *
     * @return  the base point of order r on this elliptic curve
     */
    public ECPoint getBasePoint() {
        return G;
    }

    /**
     * Get a random nonzero point on this curve
     *
     * @param   rand    a cryptographically strong PRNG
     *
     * @return  a random nonzero point on this curve
     */
    public ECPoint pointFactory(Random rand) {
         return G.randomize(rand); // using the Prototype design pattern
    }

    public abstract ECPoint pointFactory(BigInteger x, BigInteger y);

    public abstract ECPoint pointFactory(BigInteger x, int ybit);
    
    public abstract ECPoint pointFactory(byte[] data);


    /**
     * Check is this curve is defined of the same field a given field element
     *
     * @param   P   the field element whose source field is to be compared
     *              against the defining field of this curve
     *
     * @return  true if this curve is defined on the same field as P, otherwise false
     */
    public boolean overFieldOf(GF P) {
        /*
         * Kronecker's theorem states that comparing the field sizes
         * is enough to determine if two fields are isomorphic;
         * this implies they are equal if the same representation is used.
         */
//        System.out.println("overFieldOf ");
//        System.out.println(P.fieldSize().toString(16));
//        System.out.println(q.toString(16));

        return P.fieldSize().equals(q);
    }

    /**
     * Check whether this curve contains a given point
     * (i.e. whether that point satisfies the curve equation)
     *
     * @param   P   the point whose pertinence or not to this curve is to be determined
     *
     * @return  true if this curve contains P, otherwise false
     */
    public abstract boolean contains(ECPoint P);


}
