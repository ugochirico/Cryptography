/*
 * Created on 10-gen-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ugochirico.crypt.ecc;

import com.ugochirico.crypt.ecc.*;
import com.ugochirico.math.*;
import com.ugochirico.util.Random;

/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * Customized and ported on J2ME MIDP1.0
 */
public class ECPrivateKey
{
    public BigInteger  m_s;
    public ECPoint 	   m_W;

    public static ECPrivateKey generate(EC curve, Random rand)
    {
        return new ECPrivateKey(curve, rand);
    }

    // private key is s in the range [1, r-1]
    // public key is W = s * G
    private ECPrivateKey(EC E, Random rand)
    {
		if (E == null)
		{
			throw new IllegalArgumentException("invalid argument: curve");
		}

		if (rand == null)
		{
			throw new IllegalArgumentException("invalid argument: rand");
		}

		BigInteger x;

		// generate suitable private key x:
		int t = E.r.bitLength();
		do
		{
			x = new BigInteger(t, rand);
			if (x.compareTo(E.r) >= 0)
			{
				x = x.subtract(E.r);
			}

			/* invariant: 0 <= x < n */
		}
		while (x.signum() == 0);

		/* invariant: 0 < x < n */

		// compute the corresponding public key:
		//m_W = E.kG(x).normalize();
        m_W = E.G.multiply(x).normalize();
		m_s = x;
    }

    // private key is s in the range [1, r-1]
    // public key is W = s * G
    public ECPrivateKey(EC curve, BigInteger s)
    {
        if (s == null)
		{
			throw new IllegalArgumentException("invalid argument: s");
		}

        if (curve == null)
		{
			throw new IllegalArgumentException("invalid argument: curve");
		}

        m_W = curve.G.multiply(s).normalize();
        m_s = s;
    }

    public ECPublicKey getPublicKey()
    {
        return new ECPublicKey(m_W);
    }

    /** Elliptic Curve Secret Value Derivation Primitive, Diffie-Hellman version */
    public BigInteger ECSVDP_DH(ECPublicKey otherPubKey)
    {
        // check if the public key stays on the same curve
        if(!m_W.E.contains(otherPubKey.m_W))
            throw new IllegalArgumentException("Public key don't stay on the same curve");

        ECPoint P = otherPubKey.m_W.multiply(m_s);

        if(P.isZero())
            throw new RuntimeException("P is zero");

        P = P.normalize();
        return P.x.toBigInteger();
    }

    /** Elliptic Curve Secret Value Derivation Primitive, Diffie-Hellman version with cofactor */
    public BigInteger ECSVDP_DHC(ECPublicKey otherPubKey, boolean bDHcompatible)
    {
        // check if the public key stays on the same curve
        if(!m_W.E.contains(otherPubKey.m_W))
            throw new IllegalArgumentException("Public key don't stay on the same curve");

        BigInteger t;

        //1.
        if(bDHcompatible)
        {
            // t = k^–1s mod r;
            t = m_s.divide(m_W.E.k).mod(m_W.E.r);
        }
        else
        {
            t = m_s;
        }

        //2.
        BigInteger kt = t.multiply(m_W.E.k);
        ECPoint P = otherPubKey.m_W.multiply(kt);

        // 3.
        if(P.isZero())
            throw new RuntimeException("P is zero");

        P = P.normalize();

        //4.
        return P.x.toBigInteger();
    }

  /** ECSP-NR is Elliptic Curve Signature Primitive, Nyberg-Rueppel version. It is based on the work of
	[Mil86], [Kob87] and [NR93]. It can be invoked in a scheme to compute a signature on a message
	representative with the private key of the signer, in such a way that the message representative can be
	recovered from the signature using the public key of the signer by the ECVP-NR primitive. Note,
	however, that no EC signature schemes with message recovery are defined in this version of the standard
	(see Annex C.3.4). ECSP-NR may also be used in a signature scheme with appendix and can be invoked
	in the scheme DLSSA as part of signature generation.
	*/
    public ECSignature ECSP_NR(BigInteger f)
    {
//        System.out.println(f.toString(16));

        // check f > 0
        if(f.signum() <= 0)
            throw new IllegalArgumentException("Invalid message representative: f < 0");

        // check f < r
        if(f.compareTo(m_W.E.r) > 0)
            throw new IllegalArgumentException("Invalid message representative: f > r");

        ECPrivateKey sessionPriKey = null;
        BigInteger c = BigInteger.ZERO;

        // 1.
        sessionPriKey = new ECPrivateKey(m_W.E, new Random());

        // 2.
        //int [] ip = Utils.FE2OSP(sessionPriKey.m_W.x, m_curve.q);
        //BigInteger i = Utils.OS2IP(ip);

//      3. c = (i + f) mod r.
        c = sessionPriKey.m_W.x.toBigInteger().add(f).mod(m_W.E.r);

        // 3. c = i + f mod r.
        //c = i.add(f.mod(m_curve.r));

        if(c.equals(BigInteger.ZERO))
            return ECSP_NR(f);

//      4. d = (u – sc) mod r.
        BigInteger d =  sessionPriKey.m_s.subtract(m_s.multiply(c)).mod(m_W.E.r);

        // 4. d = u – sc mod r.
        //BigInteger d =  sessionPriKey.m_s.subtract(m_s.multiply(c).mod(m_curve.r));

        // 5.
        ECSignature sig = new ECSignature(c,d, ECSignature.TYPE_ECSP_NR);

        return sig;
    }

    /**
    * ECSP-DSA is Elliptic Curve Signature Primitive, DSA version. It is based on the work of [Mil86],
	[Kob87] and [Kra93]. It can be invoked in a scheme to compute a signature on a message representative
	with the private key of the signer. The message representative cannot be recovered from the signature, but
	ECVP-DSA can be used in the scheme ECSSA to verify the signature.
    * @author chirico
    */

    public ECSignature ECSP_DSA(BigInteger f)
    {
        // check f > 0
        if(f.signum() <= 0)
            throw new IllegalArgumentException("Invalid message representative: f < 0");

        // check f < r
        if(f.compareTo(m_W.E.r) > 0)
            throw new IllegalArgumentException("Invalid message representative: f > r");

        ECPrivateKey sessionPriKey = null;
        BigInteger c = BigInteger.ZERO;

        // 1.
        sessionPriKey = new ECPrivateKey(m_W.E, new Random());

        // 2.
        //int [] ip = Utils.FE2OSP(sessionPriKey.m_W.x, m_curve.q);
        //BigInteger i = Utils.OS2IP(ip);

        // 3. c = abscissa(k*G) mod r
        c = sessionPriKey.m_W.x.toBigInteger().mod(m_W.E.r);
//      c = i.mod(m_curve.r);
        if(c.equals(BigInteger.ZERO))
            return ECSP_DSA(f);

        // 4. d = u^-1(f + sc) mod r.
        BigInteger d = f.add(c.multiply(m_s)).multiply(sessionPriKey.m_s.modInverse(m_W.E.r)).mod(m_W.E.r);
        if(d.equals(BigInteger.ZERO))
            return ECSP_DSA(f);

        // 5.
        ECSignature sig = new ECSignature(c,d, ECSignature.TYPE_ECSP_DSA);

        return sig;
    }



}
