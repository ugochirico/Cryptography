package com.ugochirico.crypt.ecclight;

import java.util.Hashtable;

import com.ugochirico.math.BigInteger;
import com.ugochirico.util.Random;
/**
 * @author  Paulo S. L. M. Barreto <pbarreto@cryptix.org>
 * Customized and Ported on J2ME MIDP1.0 by Ugo Chirico <ugo.chirico@ugosweb.com>
 */
public class ECCurve
{
    private static final Hashtable s_curveTbl = new Hashtable(8);

    /**
     * Convenient BigInteger constants
     */
    protected static final BigInteger
        _0 = BigInteger.ZERO,
        _1 = BigInteger.ONE,
        _3 = BigInteger.valueOf(3L),
        _5 = BigInteger.valueOf(5L);

    /**
     * Rabin-Miller certainty used for primality testing
     */
    protected static final int PRIMALITY_CERTAINTY = 50;

	/**
	 * Invalid ECurve parameters error message
	 */
    public static final String invalidECParams =
        "The specified parameters do not properly define a suitable elliptic curve";

    /**
     * Size of the underlying finite field GF(q)
     */
    public BigInteger q;

    /**
     * Coefficient of the elliptic curve equation
     */
    public BigInteger a;

    /**
     * Coefficient of the elliptic curve equation
     */
    public BigInteger b;

    /**
     * Cofactor of the base point (curve order u = h*n)
     */
    public BigInteger k;

    /**
     * Prime order of the base point (curve order u = h*n)
     */
    public BigInteger r;

    /**
     * The base point of large prime order n
     */
    public ECPoint G;

    /**
     * The point at infinity
     */
    public ECPoint infinity;

	/**
	 * Multiples of the base point G by powers of two.
	 */
	protected ECPoint[] p2G;

	public int standardFieldSize;

    /**
     * Create a full description of the elliptic curve over GF(p) satisfying
     * the equation y^2 = x^3 + ax + b with near-prime group order u = h*r
     * with a specified base point of prime order r.
     *
     * @param   p   an approximation for the size q of the underlying
     *              finite field GF(q) (q is taken to be the nearest odd prime
     *              not smaller than p or 3)
     * @param   a   curve equation coefficient
     * @param   b   curve equation coefficient
     * @param   h   cofactor of the curve group order
     * @param   n   prime order of the cryptographic subgroup
     * @param	Gx	x-coordinate of the base point of the curve
     * @param	Gy	x-coordinate of the base point of the curve
     *
     * @exception	IllegalArgumentException	if the selected parameters don't define a proper curve
     */
    private ECCurve(BigInteger p, BigInteger a, BigInteger b, BigInteger h, BigInteger n, BigInteger Gx, BigInteger Gy, int fieldSize)
    {
//        if (!p.isProbablePrime(PRIMALITY_CERTAINTY)) {
//            throw new IllegalArgumentException(invalidECParams + ": " + "The underlying field size is not prime");
//        }
//        if (!n.isProbablePrime(PRIMALITY_CERTAINTY)) {
//            throw new IllegalArgumentException(invalidECParams + ": " + "The order of the base point is not prime");
//        }

        this.q = p;

        infinity = new ECPoint(this); // caveat: must be set *after* this.q

        this.a = a;
        this.b = b;
        this.k = h;
        this.r = n;

        this.G = new ECPoint(this, Gx, Gy);


        this.standardFieldSize = fieldSize;

		if (!G.multiply(n).isZero()) {
            throw new IllegalArgumentException(invalidECParams + ": " + "Wrong order");
        }

		p2G = new ECPoint[n.bitLength()];
		p2G[0] = G;

		for (int m = 1; m < p2G.length; m++) {
			p2G[m] = p2G[m - 1].twice(1);
		}
    }

    /**
     * Get a random nonzero point on this curve, given a fixed base point.
     *
     * @param   rand    a cryptographically strong PRNG
     *
     * @return  a random nonzero point on this curve
     */
    public ECPoint pointFactory(Random rand)
    {
		BigInteger kk;
		do
        {
			kk = new BigInteger(q.bitLength(), rand).mod(q);
		}
        while (kk.signum() == 0);
        return G.multiply(kk);
    }

    /**
     * Check whether this curve contains a given point
     * (i.e. whether that point satisfies the curve equation)
     *
     * @param   P   the point whose pertinence or not to this curve is to be determined
     *
     * @return  true if this curve contains P, otherwise false
     */
    public boolean contains(ECPoint P)
    {
		if (P.q.compareTo(q) != 0)
			return false;


        if (P.isZero())
        {
            return true; // the point at infinity does not satisfy the equation but is obviously on the curve
        }
        // check the projective equation y^2 == x^3 + a.x.z^4 + b.z^6,
		// i.e. x.x^2 + [a.x + b.z^2].(z^2)^2 - y^2 == 0;
		// the computation below never uses intermediate values larger than 3q^2:
		BigInteger
			x  = P.x,
			y  = P.y,
			z  = P.z,
			x2 = x.multiply(x).mod(q),
			z2 = z.multiply(z).mod(q),
			z4 = z2.multiply(z2).mod(q),
			br = a.multiply(x).add(b.multiply(z2)).mod(q); // bracketed expression [a.x + b.z^2]
		return x.multiply(x2).add(br.multiply(z4)).subtract(y.multiply(y)).mod(q).signum() == 0;
    }

    /**
     * Build a standard curve
     *
     * @param   fieldSize   underlying field size
     *
     * @return  the desired curve, or null if the field size is not supported
     */
    public static ECCurve getStandardCurve(int fieldSize)
    {
        if(s_curveTbl.containsKey("" + fieldSize))
        {
            return (ECCurve)s_curveTbl.get("" + fieldSize);
        }

        ECCurve curve = null;

        try
        {
            switch (fieldSize) {
            case 160:
                curve = new ECCurve(
                	new BigInteger("00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF7FFFFFFF", 16), // p
                    new BigInteger("00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF7FFFFFFC", 16), // a
                    new BigInteger("1C97BEFC54BD7A8B65ACF89F81D4D4ADC565FA45", 16),   // b
                    _1, // h
                    new BigInteger("0100000000000000000001F4C8F927AED3CA752257", 16), // n
                    new BigInteger("4A96B5688EF573284664698968C38BB913CBFC82", 16),   // x
                    new BigInteger("23A628553168947D59DCC912042351377AC5FB32", 16),  // y
                    fieldSize);
                break;
            case 192:
                curve =  new ECCurve(
                	new BigInteger("00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFF", 16), // p
                    new BigInteger("00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFC", 16), // a
                    new BigInteger("64210519E59C80E70FA7E9AB72243049FEB8DEECC146B9B1", 16),   // b
                    _1, // h
                    new BigInteger("00FFFFFFFFFFFFFFFFFFFFFFFF99DEF836146BC9B1B4D22831", 16), // n
                    new BigInteger("188DA80EB03090F67CBF20EB43A18800F4FF0AFD82FF1012", 16),   // x
                    new BigInteger("07192B95FFC8DA78631011ED6B24CDD573F977A11E794811", 16),  // y
                	fieldSize);
                break;
//            case 224:
//                curve =  new ECCurve(
//                	new BigInteger("00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF000000000000000000000001", 16), // p
//                    new BigInteger("00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFE", 16), // a
//                    new BigInteger("00B4050A850C04B3ABF54132565044B0B7D7BFD8BA270B39432355FFB4", 16), // b
//                    _1, // h
//                    new BigInteger("00FFFFFFFFFFFFFFFFFFFFFFFFFFFF16A2E0B8F03E13DD29455C5C2A3D", 16), // n
//                    new BigInteger("00B70E0CBD6BB4BF7F321390B94A03C1D356C21122343280D6115C1D21", 16), // x
//                    new BigInteger("00BD376388B5F723FB4C22DFE6CD4375A05A07476444D5819985007E34", 16), // y
//                	fieldSize);
//                break;
//
            case 256:
                curve = new ECCurve(
                	new BigInteger("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF", 16), // p
                    new BigInteger("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC", 16), // a
                    new BigInteger("5AC635D8AA3A93E7B3EBBD55769886BC651D06B0CC53B0F63BCE3C3E27D2604B", 16), // b
                    _1, // h
                    new BigInteger("FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551", 16), // n
                    new BigInteger("6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296", 16), // x
                    new BigInteger("4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5", 16), // y
                	fieldSize);
                break;
//            case 384:
//                curve =  new ECCurve(
//                	new BigInteger("00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFF", 16), // p
//                    new BigInteger("00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFC", 16), // a
//                    new BigInteger("00B3312FA7E23EE7E4988E056BE3F82D19181D9C6EFE8141120314088F5013875AC656398D8A2ED19D2A85C8EDD3EC2AEF", 16), // b
//                    _1, // h
//                    new BigInteger("00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC7634D81F4372DDF581A0DB248B0A77AECEC196ACCC52973", 16), // n
//                    new BigInteger("00AA87CA22BE8B05378EB1C71EF320AD746E1D3B628BA79B9859F741E082542A385502F25DBF55296C3A545E3872760AB7", 16), // x
//                    new BigInteger("3617DE4A96262C6F5D9E98BF9292DC29F8F41DBD289A147CE9DA3113B5F0B8C00A60B1CE1D7E819D7A431D7C90EA0E5F", 16),   // y
//                	fieldSize);
//                break;
//            case 521:
//                curve =  new ECCurve(
//                	new BigInteger("01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16), // p
//                	new BigInteger("01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC", 16), // a
//                    new BigInteger("51953EB9618E1C9A1F929A21A0B68540EEA2DA725B99B315F3B8B489918EF109E156193951EC7E937B1652C0BD3BB1BF073573DF883D2C34F1EF451FD46B503F00", 16),   // b
//                    _1, // h
//                    new BigInteger("01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFA51868783BF2F966B7FCC0148F709A5D03BB5C9B8899C47AEBB6FB71E91386409", 16), // n
//                    new BigInteger("00C6858E06B70404E9CD9E3ECB662395B4429C648139053FB521F828AF606B4D3DBAA14B5E77EFE75928FE1DC127A2FFA8DE3348B3C1856A429BF97E7E31C2E5BD66", 16), // x
//                    new BigInteger("011839296A789A3BC0045C8A5FB42C7D1BD998F54449579B446817AFBD17273E662C97EE72995EF42640C550B9013FAD0761353C7086A272C24088BE94769FD16650", 16), // y
//                	fieldSize);
//                break;
            default:
                return null;
            }
        }
        catch (Throwable e)
        {
//            e.printStackTrace();
        }

        s_curveTbl.put("" + fieldSize, curve);

        return curve;
    }

    public static boolean isInitialized(int fieldSize)
    {
        return s_curveTbl.containsKey("" + fieldSize);
    }

    /**
     * Compute k*G
     *
     * @param   k   scalar by which the base point G is to be multiplied
     *
     * @return  k*G
     */
    public ECPoint kG(BigInteger k)
    {
        /*
         * This method implements the the quaternary window multiplication algorithm.
         *
         * References:
         *
         * Alfred J. Menezes, Paul C. van Oorschot, Scott A. Vanstone,
         *      "Handbook of Applied Cryptography", CRC Press (1997),
         *      section 14.6 (Exponentiation), especially algorithm 14.109
         */
		k = k.mod(r); // reduce k mod n
        ECPoint A = infinity;
		for (int i = k.bitLength() - 1; i >= 0; i--) {
			if (k.testBit(i)) {
				A = A.add(p2G[i]);
			}
		}
        return A;
    }
}
