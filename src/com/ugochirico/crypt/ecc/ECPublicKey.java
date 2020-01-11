/*
 * Created on 10-gen-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ugochirico.crypt.ecc;
import com.ugochirico.crypt.ecc.*;
import com.ugochirico.math.*;

/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * Customized and ported on J2ME MIDP1.0
 */
public class ECPublicKey
{
    public ECPoint m_W;
    
    public ECPublicKey(ECPoint W)
    {
        m_W = W;
    }
    
    public boolean verify(BigInteger f, ECSignature sig)
    {
        switch(sig.m_nType)
        {
        case ECSignature.TYPE_ECSP_DSA:
        	return ECVP_DSA(f, sig);
        
        case ECSignature.TYPE_ECSP_NR:    
            BigInteger f1 = ECVP_NR(sig);
        	
        	return f1.equals(f);
        	
        default:
            throw new IllegalArgumentException("Invalid Signature");
        }
    }
    
    /**
    ECVP-NR is Elliptic Curve Verification Primitive, Nyberg-Rueppel version. It is based on the work of
    [Mil86], [Kob87] and [NR93]. This primitive recovers the message representative that was signed with
    ECSP-NR, given only the signature and public key of the signer. It can be invoked in a scheme as part of
    signature verification and, possibly, message recovery. Note, however, that no EC signature schemes with
    message recovery are defined in this version of the standard (see Annex C.3.4). ECVP-NR may also be
    used in a signature scheme with appendix and can be invoked in the scheme DLSSA as part of signature
    verification.
    */ 
    public BigInteger ECVP_NR(ECSignature sig)
    {
        // 1.
        if(sig.m_c.compareTo(BigInteger.ONE) < 0) 
            throw new IllegalArgumentException("Invalid Signature: c < 1");
        
        if(sig.m_c.compareTo(m_W.E.r.subtract(BigInteger.ONE)) > 0)
            throw new IllegalArgumentException("Invalid Signature: c > r - 1");
        
        if(sig.m_d.signum() <= 0) 
            throw new IllegalArgumentException("Invalid Signature: d < 0");
       
        if(sig.m_d.compareTo(m_W.E.r.subtract(BigInteger.ONE)) > 0 )
            throw new IllegalArgumentException("Invalid Signature: d > r - 1");
        
//      2. P = d G + c W
        //EPointp P = EPointp.simultaneous(sig.m_c, m_W, sig.m_d, m_W.E.G);
        ECPoint P = (m_W.multiply(sig.m_c).add(m_W.E.G.multiply(sig.m_d))).normalize();
        
		// 2. P = d G + c W
//        ECPoint P = m_W.E.G.multiply(sig.m_d).add(m_W.multiply(sig.m_c));
        if(P.isZero())
            throw new IllegalArgumentException("P is zero");
        
        // 3.
        //int ip[] = Utils.FE2OSP(P.x, m_W.q);
        //BigInteger i = Utils.OS2IP(ip);
        
        // 4. f = (c - Px) mod n
        BigInteger f = sig.m_c.subtract(P.x.toBigInteger()).mod(m_W.E.r);
        //BigInteger f = sig.m_c.subtract(i.mod(m_W.E.r));
        
        return f;
    }
    /**
    ECVP-DSA is Elliptic Curve Verification Primitive, DSA version. It is based on the work of [Mil86],
    [Kob87] and [Kra93]. This primitive verifies whether the message representative and the signature are
    consistent given the key and the domain parameters. It can be invoked in the scheme ECSSA as part of
    signature verification.
    */
    public boolean ECVP_DSA(BigInteger f, ECSignature sig)
    {
        // 1.
        if(sig.m_c.compareTo(BigInteger.ONE) < 0) 
            throw new IllegalArgumentException("Invalid Signature: c < 1");
        
        if(sig.m_c.compareTo(m_W.E.r.subtract(BigInteger.ONE)) > 0)
            throw new IllegalArgumentException("Invalid Signature: c > r - 1");
        
        if(sig.m_d.signum() < 0) 
            throw new IllegalArgumentException("Invalid Signature: d < 0");
       
        if(sig.m_d.compareTo(m_W.E.r.subtract(BigInteger.ONE)) > 0 )
            throw new IllegalArgumentException("Invalid Signature: d > r - 1");
       
        // 2. 
        // h = d^–1 mod r; 
        BigInteger h = sig.m_d.modInverse(m_W.E.r);
        
        // h1 = f h mod r; 
        BigInteger h1 = f.multiply(h).mod(m_W.E.r);
        
        // h2 = c h mod r.
        BigInteger h2 = sig.m_c.multiply(h).mod(m_W.E.r);
        
        // 3. P = h1G + h2W
        ECPoint P = (m_W.E.G.multiply(h1).add(m_W.multiply(h2))).normalize();
        //EPoint P = ECPoint.simultaneous(h1, m_W.E.G, h2, m_W);
        
        // 4.
        //int ip[] = Utils.FE2OSP(P.x, m_W.q);
        //BigInteger i = Utils.OS2IP(ip);
        
        // 5. c1 = i mod r.
        BigInteger c1 = P.x.toBigInteger().mod(m_W.E.r);
        
        // 6.
        return c1.equals(sig.m_c);
    }
}
