package com.ugochirico.crypt.ecclight;

/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 */
import com.ugochirico.math.BigInteger;

public interface ECListener
{
    public int EVENT_CURVE_INITIALIZED = 1;
    public int EVENT_PRIVATE_KEY_GENERATED = 2;
    public int EVENT_PRIVATE_KEY_CREATED = 3;
    public int EVENT_PUBLIC_KEY_CREATED = 4;
    public int EVENT_ECSVDP_DH = 5;
    public int EVENT_ECSVDP_DHC = 6;
    public int EVENT_ECSP_NR = 7;
    public int EVENT_ECSP_DSA = 8;
    public int EVENT_ECVP_NR = 9;
    public int EVENT_ECVP_DSA = 10;
    public int EVENT_VERIFY = 11;

    public int EVENT_EXCEPTION = 100;

    public void notifyECEvent(int event, Object target);

//    public void notifyCurveInitialized(ECCurve c);
//
//    public void notifyPrivateKeyGenerated(ECPrivateKey key);
//
//    public void notifyPrivateKeyCreated(ECPrivateKey key);
//    public void notifyPublicKeyCreated(ECPublicKey key);
//
//    public void notifyException(Throwable t);
//
//    public void notifyECSVDP_DH(BigInteger key);
//    public void notifyECSVDP_DHC(BigInteger key);
//
//    public void notifyECSP_NR(ECSignature sig);
//    public void notifyECSP_DSA(ECSignature sig);
//
//    public void notifyECVP_NR(BigInteger f);
//    public void notifyECVP_DSA(BigInteger f);
//
//    public void notifyVerify(boolean bVerified);
}
