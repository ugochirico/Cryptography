package com.ugochirico.crypt.ecclight;
import com.ugochirico.math.BigInteger;
import com.ugochirico.util.Random;

/**
 * The EPoint class is an abstraction for the arithmetic group of points
 * on an elliptic curve over GF(p).<BR>
 * <BR>
 * A point of an elliptic curve is only meaningful when suitably attached
 * to some curve.  Hence, there must be no public means to create a point
 * by itself (i.e. concrete subclasses of EPoint shall have no public
 * constructor); the proper way to do this is to invoke the factory method
 * pointFactory() of the desired ECurve subclass.<BR>
 * <BR>
 * This is a direct application of the "Factory Method" design pattern
 * as described by E. Gamma, R. Helm, R. Johnson and J. Vlissides in
 * "Design Patterns - Elements of Reusable Object-Oriented Software",
 * Addison-Wesley (1995), pp. 107-116, especially Consequence #2
 * ("Connects parallel class hierarchies", pp. 109-110).<BR>
 * <BR>
 * This class must inherit from Cloneable to allow for the application of
 * the "Prototype" design pattern (see reference above) for uses of EPoint
 * where the actual nature of a curve point does not matter (e.g. in
 * general tests of implementation correctness).
 *
 *
 * @author    Paulo S.L.M. Barreto <pbarreto@cryptix.org>
 * @author    Customized and Ported on J2ME MIDP1.0 by Ugo Chirico <ugo.chirico@ugosweb.com>
 */
public class ECPoint
{
    private static final BigInteger
		_0 = BigInteger.valueOf(0L),
		_1 = BigInteger.valueOf(1L),
		_3 = BigInteger.valueOf(3L);

	public static final String differentCurves =
        "Cannot combine points from different elliptic curves";
    public static final String invalidCPSyntax =
        "Syntax error in curve point description";
    public static final String pointNotOnCurve =
        "The given point does not belong to the given elliptic curve";

    /**
     * The underlying elliptic curve, given by its parameters
     */
    public ECCurve E;

    /**
     * Size of the underlying finite field GF(q)
     */
    public BigInteger q;

    /**
     * The projective x-coordinate
     */
    public BigInteger x;

    /**
     * The projective y-coordinate
     */
    public BigInteger y;

    /**
     * The projective z-coordinate
     */
    public BigInteger z;

    /**
     * Flag/mask for compressed, expanded, or hybrid point representation
     */
    public static final int
		COMPRESSED	= 2,
		EXPANDED	= 4,
		HYBRID		= COMPRESSED | EXPANDED;

    /**
     * Create an instance of the ECurve point at infinity on curve E.
     *
     * @param	E	the elliptic curve where the created point is located.
     */
    public ECPoint(ECCurve E)
    {
        this.E = E;
		this.q = E.q;
        /*
         * the point at infinity is represented as (1, 1, 0) after P1363
         * since such a point does not satisfy any projective curve equation
         * of the form (y^2)z = x^3 + ax(z^2) + b(z^3) for 4a^3 + 27b^2 != 0
         */
        this.x = _1;
        this.y = _1;
        this.z = _0;
    }

    /**
     * Create a normalized ECurve point from given affine coordinates and a curve
     *
     * @param    E    the underlying elliptic curve.
     * @param    x    the affine x-coordinate (mod q).
     * @param    y    the affine y-coordinate (mod q).
     */
    public ECPoint(ECCurve E, BigInteger x, BigInteger y)
    {
        this.E = E;
		this.q = E.q;
        this.x = x.mod(q);
        this.y = y.mod(q);
        this.z = _1; // normalized
        if (!E.contains(this)) {
            throw new IllegalArgumentException(pointNotOnCurve);
        }
    }

//    public ECPoint(BigInteger x, BigInteger y, ECCurve E) {
//        this.E = E;
//		this.q = E.q;
//        this.x = x;
//        this.y = y;
//        this.z = _1; // normalized
//        if (!E.contains(this)) {
//            throw new IllegalArgumentException(pointNotOnCurve);
//        }
//    }

    /**
     * Create an ECurve point from a given affine x-coordinate, a y-bit and a curve
     *
     * @param	E		the underlying elliptic curve.
     * @param	x		the affine x-coordinate.
     * @param	yBit	the least significant bit of the y-coordinate.
     */
    public ECPoint(ECCurve E, BigInteger x, int yBit)
    {
        this.E = E;
		this.q = E.q;
        this.x = x;

        // alpha = x^3 + ax + b mod p = (x^2 + a)x + b mod p
		BigInteger x2 = x.multiply(x);
		BigInteger alpha = ((x2.add(E.a)).multiply(x)).add(E.b.mod(q));
        BigInteger beta = sqrt(alpha.mod(q));

        if (beta == null)
        {
            throw new IllegalArgumentException(pointNotOnCurve);
        }

        yBit &= 1; // take only LSB
        this.y = (beta.testBit(0) == (yBit == 1)) ? beta : beta.negate().mod(q);

        this.z = _1; // normalized

        // the following test is redundant: if alpha has a square root,
        // then (x, y, z) does belong to the curve:
		/*
        if (!E.contains(this)) {
            throw new IllegalArgumentException(pointNotOnCurve);
        }
		*/
    }
//    public ECPoint(ECCurve E, BigInteger x, boolean yBit) {
//        this.E = E;
//		this.q = E.q;
//        this.x = x.mod(q);
//        if (this.x.signum() == 0)
//        {
////            Sender.m_waitForm.update("signum > 0", 5);
////            try
////            {
////                Thread.sleep(3000);
////            }
////            catch (InterruptedException e)
////            {
////                e.printStackTrace();
////            }
//            this.y = sqrt(E.b); // square root always defined
//        }
//        else
//        {
//            // alpha = x^3 + ax + b = (x^2 + a)x + b
//			BigInteger x2 = x.multiply(x).mod(q);
//			BigInteger alpha = x2.add(E.a).multiply(x).add(E.b).mod(q);
//            // beta  = sqrt(alpha)
//            BigInteger beta = sqrt(alpha);
//            if (beta == null)
//            {
////                Sender.m_waitForm.update("IllegalArgumentException", 5);
////                try
////                {
////                    Thread.sleep(3000);
////                }
////                catch (InterruptedException e)
////                {
////                    e.printStackTrace();
////                }
//                throw new IllegalArgumentException(pointNotOnCurve);
//            }
//			//yBit &= 1; // take only LSB
//            //this.y = (beta.testBit(0) == (yBit == 1)) ? beta : beta.negate().mod(q);
//            this.y = (beta.testBit(0) == (yBit)) ? beta : beta.negate().mod(q);
//        }
//        this.z = _1; // normalized
//        // the following test is redundant: if alpha has a square root,
//        // then (x, y, z) does belong to the curve:
//		/*
//        if (!E.contains(this)) {
//            throw new IllegalArgumentException(pointNotOnCurve);
//        }
//		*/
//    }

    /**
     * Create an ECurve point from given projective coordinates and a curve.
     *
     * @param    E    the underlying elliptic curve.
     * @param    x    the affine x-coordinate (mod q).
     * @param    y    the affine y-coordinate (mod q).
     * @param    z    the affine z-coordinate (mod q).
     */
    private ECPoint(ECCurve E, BigInteger x, BigInteger y, BigInteger z)
    {
        this.E = E;
		this.q = E.q;
        this.x = x;
        this.y = y;
        this.z = z;
    }

	/**
	 * Create a clone of a given point.
	 *
	 * @param	Q	the point to be cloned.
	 */
//	private ECPoint(ECPoint Q)
//    {
//        this.E = Q.E;
//		this.q = E.q;
//		this.x = Q.x;
//		this.y = Q.y;
//		this.z = Q.z;
//	}

	/*     * performing arithmetic operations on elliptic curve points
     * generally implies knowing the nature of these points (more precisely,
     * the nature of the finite field to which their coordinates belong),
     * hence they are done by the underlying elliptic curve.
     */

    /**
     * Check whether this is the point at infinity (i.e. the ECurve group zero element).     *     * @return  true if this is the point at infinity, otherwise false.
     */
    public boolean isZero()
    {
        return z.signum() == 0;
    }

    /**
     * Compare this point to a given object.
     *
     * @param   Q   the elliptic curve point to be compared to this.     *     * @return  true if this point and Q are equal, otherwise false.
     */
    public boolean equals(Object Q)
    {
        if (Q instanceof ECPoint && this.isOnSameCurve((ECPoint)Q))
        {
            ECPoint P = (ECPoint)Q;
            if (z.signum() == 0 || P.isZero())
            {
                return z.equals(P.z);
            }
            else
            {
                BigInteger
					z2 = z.multiply(z ).mod(q),
					z3 = z.multiply(z2).mod(q),
					pz2 = P.z.multiply(P.z).mod(q),
					pz3 = P.z.multiply(pz2).mod(q);
                return
                    x.multiply(pz2).subtract(P.x.multiply(z2)).mod(q).signum() == 0 &&
                    y.multiply(pz3).subtract(P.y.multiply(z3)).mod(q).signum() == 0;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Check whether Q lays on the same curve as this point.
     *
     * @param   Q   an elliptic curve point.
     *
     * @return  true if Q lays on the same curve as this point, otherwise false.
     */
    public boolean isOnSameCurve(ECPoint Q)
    {
        return E.q.equals(Q.E.q)
            && E.a.equals(Q.E.a)
            && E.b.equals(Q.E.b)
            && E.k.equals(Q.E.k)
            && E.r.equals(Q.E.r)
            //  && E.G.equals(Q.E.G) // caveat: resist the temptation to uncomment this line! :-)            ;
    }

    public int ybit()
    {
        return y.testBit(0) ? 1 : 0;
    }

    /**
     * Compute a random point on the same curve as this.     *
     * @param	rand	a cryptographically strong pseudo-random number generator.
     *     * @return  a random point on the same curve as this.
     */
    public ECPoint randomize(Random rand)
    {
		return E.pointFactory(rand);
    }

    /**
     * Normalize this point.
     *
     * @return  a normalized point equivalent to this.
     */
    public ECPoint normalize()
    {
        if (this.isZero()) {
            return E.infinity;
        } else if (this.z.compareTo(_1) == 0) {
            return this; // already normalized
        } else {
            BigInteger z2 = z.multiply(z), z3 = z2.multiply(z);
            return new ECPoint(E,
                       x.multiply(z2.modInverse(q)).mod(q),
                       y.multiply(z3.modInverse(q)).mod(q),
                       _1);
        }
    }

    /**
     * Compute -this.     *     * @return  -this.
     */
    public ECPoint negate()
    {
        return new ECPoint(E, x, y.negate(), z);
    }

    /**
     * Compute this + Q.
     *
     * @return  this + Q.
     *
     * @param   Q   an elliptic curve point.     */
    public ECPoint add(ECPoint Q)
    {
		/*
        if (!this.isOnSameCurve(Q)) {
            throw new IllegalArgumentException(differentCurves);
        }
		*/
        if (this.isZero()) {
            return Q;
        }
        if (Q.isZero()) {
            return this;
        }
        // P1363 section A.10.5
        BigInteger t1, t2, t3, t4, t5, t6, t7, t8;
        t1 = x;
        t2 = y;
        t3 = z;
        t4 = Q.x;
        t5 = Q.y;
        t6 = Q.z;
        if (t6.compareTo(_1) != 0) {
            t7 = t6.multiply(t6); // t7 = z1^2
            // u0 = x0.z1^2
            t1 = t1.multiply(t7).mod(q);
            // s0 = y0.z1^3 = y0.z1^2.z1
            t2 = t2.multiply(t7).multiply(t6).mod(q);
        }
        if (t3.compareTo(_1) != 0) {
            t7 = t3.multiply(t3); // t7 = z0^2
            // u1 = x1.z0^2
            t4 = t4.multiply(t7).mod(q);
            // s1 = y1.z0^3 = y1.z0^2.z0
            t5 = t5.multiply(t7).multiply(t3).mod(q);
        }
        // W = u0 - u1
        t7 = t1.subtract(t4).mod(q);
        // R = s0 - s1
        t8 = t2.subtract(t5).mod(q);
        if (t7.signum() == 0) {
            return (t8.signum() == 0) ? Q.twice(1) : E.infinity;
        }
        // T = u0 + u1
        t1 = t1.add(t4).mod(q);
        // M = s0 + s1
        t2 = t2.add(t5).mod(q);
        // z2 = z0.z1.W
        if (!t6.equals(_1)) {
            t3 = t3.multiply(t6); // no need to reduce here
        }
        t3 = t3.multiply(t7).mod(q);
        // x2 = R^2 - T.W^2
        t5 = t7.multiply(t7).mod(q); // t5 = W^2
        t6 = t1.multiply(t5).mod(q); // t6 = T.W^2
        t1 = t8.multiply(t8).subtract(t6).mod(q);
        // 2.y2 = (T.W^2 - 2.x2).R - M.W^2.W
        t2 = t6.subtract(t1.shiftLeft(1)).multiply(t8).subtract(t2.multiply(t5).multiply(t7)).mod(q);
        t2 = (t2.testBit(0) ? t2.add(q) : t2).shiftRight(1).mod(q);
        return new ECPoint(E, t1, t2, t3);
    }

    /**
     * Left-shift this point by a given distance n, i.e. compute (2^^n)*this.     *
     * @param	n	the shift amount.
     *     * @return (2^^n)*this.
     */
    public ECPoint twice(int n)
    {
        // P1363 section A.10.4
        BigInteger t1, t2, t3, t4, t5;
        t1 = x;
        t2 = y;
        t3 = z;
		while (n-- > 0) {
			if (t2.signum() == 0 || t3.signum() == 0) {
			    return E.infinity;
			}
			t4 = t3.multiply(t3); // t4 = z^2 (no need to reduce: z is often 1)
			if (E.a.add(_3).signum() == 0) { // a == -3
			    // M = 3(x^2 - z^4) = 3(x - z^2)(x + z^2)
			    t4 = _3.multiply(t1.subtract(t4).multiply(t1.add(t4))).mod(q);
			} else {
			    // M = 3.x^2 + a.(z^2)^2
			    t4 = _3.multiply(t1.multiply(t1)).add(E.a.multiply(t4).multiply(t4)).mod(q);
			}
			// z2 = 2.y.z
			t3 = t3.multiply(t2).shiftLeft(1).mod(q);
			// S = 4.x.y^2
			t2 = t2.multiply(t2).mod(q); // t2 = y^2
			t5 = t1.multiply(t2).shiftLeft(2).mod(q);
			// x2 = M^2 - 2.S
			t1 = t4.multiply(t4).subtract(t5.shiftLeft(1)).mod(q);
			// T = 8.(y^2)^2
			t2 = t2.multiply(t2).shiftLeft(3).mod(q);
			// y2 = M(S - x2) - T
			t2 = t4.multiply(t5.subtract(t1)).subtract(t2).mod(q);
		}
		return new ECPoint(E, t1, t2, t3);
    }

    /**
     * Compute k*this
     *
     * @param   k   scalar by which the base point G is to be multiplied
     *
     * @return  k*this
     */
    public ECPoint multiply(BigInteger k)
    {
        /*
         * This method implements the the quaternary window multiplication algorithm.
         *
         * Reference:
         *
         * Alfred J. Menezes, Paul C. van Oorschot, Scott A. Vanstone,
         *      "Handbook of Applied Cryptography", CRC Press (1997),
         *      section 14.6 (Exponentiation), algorithm 14.82
         */
		ECPoint P = this.normalize();
		if (k.signum() < 0) {
			k = k.negate();
			P = P.negate();
		}
		byte[] e = k.toByteArray();
		ECPoint[] mP = new ECPoint[256];
		mP[0] = E.infinity;
		mP[1] = P;
		for (int m = 1; m <= 7; m++) {
			mP[2*m    ] = mP[  m].twice(1);
			mP[2*m + 1] = mP[2*m].add(P);
		}
        ECPoint A = E.infinity;
		for (int i = 0; i < e.length; i++) {
			int z = e[i] & 0xff;
			A = A.twice(4).add(mP[z >>> 4]).twice(4).add(mP[z & 0xf]);
		}
        return A;
    }

	/**
	 * Compute ks*eg + kr*ey.  This is useful in the verification part
	 * of several signature algorithms, and (hopely) faster than
	 * two scalar multiplications.
	 *
	 * @param   ks	scalar by which eg is to be multiplied.
	 * @param	eg	a curve point.
	 * @param	kr	scalar by which ey is to be multiplied.
	 * @param	ey	a curve point.
	 *
	 * @return  ks*eg + kr*ey
	 */
	public static ECPoint simultaneous(BigInteger ks, ECPoint eg, BigInteger kr, ECPoint ey)
    {
		// TODO: implement fast simultaneous scalar multiplication.
		return eg.multiply(ks).add(ey.multiply(kr)).normalize();
		/*
		G_0 = 1;
		G_1 = eg;
		G_2 = ey;
		G_3 = eg*ey;
		for (i = 4; i < 16; i += 4) {
			G_i = G_{i >> 2}^2;
			G_{i + 1} = G_i*G_1;
			G_{i + 2} = G_i*G_2;
			G_{i + 3} = G_i*G_3;
		}
		A = 1;
		for (i = ((t >> 1) << 1) - 1; i >= 0; i -= 2) {
			A = (A^2)^2*G_{kr.bit(i)*8 + ks.bit(i)*4 + kr.bit(i-1)*2 + ks.bit(i-1)};
		}
		return A;
		*/
		/*
		// C implementation:
		int i, t;

		assert(epIsOnSameCurve(eg, ey)); // points to be added must reside on the same curve.
		assert(epIsOnSameCurve(eg, &ept->v[0]));

		epInfinity(&ept->v[0]);
		epCopy(&ept->v[1], eg);
		epCopy(&ept->v[2], ey);
		epAdd(&ept->v[3], eg, ey, ctx);
		for (i = 4; i < 16; i += 4) {
			epDouble(&ept->v[i], &ept->v[i >> 2], 1, ctx);
			epAdd(&ept->v[i + 1], &ept->v[i], &ept->v[1], ctx);
			epAdd(&ept->v[i + 2], &ept->v[i], &ept->v[2], ctx);
			epAdd(&ept->v[i + 3], &ept->v[i], &ept->v[3], ctx);
		}

		t = BN_num_bits(kr);
		i = BN_num_bits(ks);
		if (i > t) {
			t = i;
		}
		epInfinity(er);
		for (i = ((t >> 1) << 1) - 1; i >= 0; i -= 2) {
			int m = (BN_is_bit_set(kr,     i) << 3) +
					(BN_is_bit_set(ks,     i) << 2) +
					(BN_is_bit_set(kr, i - 1) << 1) +
					(BN_is_bit_set(ks, i - 1));
			epDouble(er, er, 2, ctx);
			epAdd(er, er, &ept->v[m], ctx);
		}
		*/
	}

    /**
     * Convert this curve point to a byte array.
     * This is the ANSI X9.62 Point-to-Octet-String Conversion primitive
     *
     * @param   formFlags   the desired form of the octet string representation
     *                      (EPoint.COMPRESSED, EPoint.EXPANDED, EPoint.HYBRID)
     *
     * @return  this point converted to a byte array using
     *          the algorithm defined in section 4.3.6 of ANSI X9.62
     */
//    public byte[] toByteArray(int formFlags) {
//        byte[] result;
//        if (this.isZero()) {
//            result = new byte[1];
//            result[0] = (byte)0;
//            return result;
//        }
//        ECPoint thisNorm = (ECPoint)this.normalize();
//        byte[] osX = null, osY = null;
//        osX = thisNorm.x.toByteArray();
//        int pc = 0, resLen = 1 + osX.length;
//        if ((formFlags & COMPRESSED) != 0) {
//			pc |= COMPRESSED | (this.y.testBit(0) ? 1 : 0);
//        }
//        if ((formFlags & EXPANDED) != 0) {
//            pc |= EXPANDED;
//            osY = thisNorm.y.toByteArray();
//            resLen += osY.length;
//        }
//        result = new byte[resLen];
//        result[0] = (byte)pc;
//        System.arraycopy(osX, 0, result, 1, osX.length);
//        if (osY != null) {
//            System.arraycopy(osY, 0, result, 1 + osX.length, osY.length);
//        }
//        return result;
//    }

    /**
     * Compute a square root of this element (null if none exists)
     *
     * @return  a square root of this element, if one exists, or null otherwise
     *
     * @exception	IllegalArgumentException	if the size of the underlying finite field is not a prime p such that p mod 4 == 3.
     */
//    protected BigInteger sqrt(BigInteger v)
//    {
//        // case I: q mod 4 == 3 (just test bit 1, since bit 0 is 1 because q is odd):
//        if (q.mod(4) == 3)
//        {
//			throw new IllegalArgumentException("This implementation is optimized for, and only works with, prime fields GF(p) where p mod 4 == 3");
//        }
//
//        if (q.mod(8) == 5)
//        {
//			throw new IllegalArgumentException("This implementation is optimized for, and only works with, prime fields GF(p) where p mod 4 == 3");
//        }
//
//        /* Algorithm P1363 A.2.5 - Finding Square Roots Modulo a Prime */
//        if (v.signum() == 0) {
//            return _0;
//        }
//
//        BigInteger gamma =
//        BigInteger z = v.modPow(q.shiftRight(2).add(_1), q);
//        // test solution:
//        return z.multiply(z).subtract(v).mod(q).signum() == 0 ? z : null;
//    }

    //Customized and Ported on J2ME MIDP1.0 by Ugo Chirico <ugo.chirico@ugosweb.com>
    protected BigInteger sqrt(BigInteger v)
    {
        // case I: q mod 4 == 3 (just test bit 1, since bit 0 is 1 because q is odd):
        if (!q.testBit(1)) {
			throw new IllegalArgumentException("This implementation is optimized for, and only works with, prime fields GF(p) where p mod 4 == 3");
        }
        /* Algorithm P1363 A.2.5 - Finding Square Roots Modulo a Prime */
        if (v.signum() == 0) {
            return _0;
        }
        BigInteger z = v.modPow(q.shiftRight(2).add(_1), q);
        // test solution:
        return z.multiply(z).subtract(v).mod(q).signum() == 0 ? z : null;
    }

}
