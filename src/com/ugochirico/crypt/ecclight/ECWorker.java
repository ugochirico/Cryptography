package com.ugochirico.crypt.ecclight;


import com.ugochirico.math.BigInteger;
import com.ugochirico.util.Random;
//import com.ugochirico.midp.log.LogViewer;

/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 */
public class ECWorker implements Runnable
{
    private int m_nFieldSize;
    private ECListener m_listener;
    private ECPrivateKey m_priKey;
    private ECPublicKey m_pubKey;
    private BigInteger m_f;
    private BigInteger m_y;
    private boolean m_ybit;
    private ECCurve m_E;
    private Random m_rand;
    private ECSignature m_sig;

    private int m_op;
    private static final int OP_GENKEY = 0;
    private static final int OP_ECSVDP_DH = 1;
    private static final int OP_ECSVDP_DHC = 2;
    private static final int OP_ECSP_NR = 3;
    private static final int OP_ECSP_DSA = 4;
    private static final int OP_INITCURVE = 5;
    private static final int OP_VERIFY = 6;
    private static final int OP_ECVP_NR = 7;
    private static final int OP_ECVP_DSA = 8;
    private static final int OP_MAKEPRIKEY = 9;
    private static final int OP_MAKEPUBKEY = 10;
    private static final int OP_MAKEPUBKEYYBIT = 11;


    public static int NORMAL_PRIORITY = Thread.NORM_PRIORITY;
    public static int MIN_PRIORITY = Thread.MIN_PRIORITY;
    public static int MAX_PRIORITY = Thread.MAX_PRIORITY;

    public static final Thread initializeCurve(int fieldSize, int priority, ECListener listener)
    {
        //#ifdef LOG
        //LogViewer.logEnter("initializeCurve");
        //#endif

        ECWorker eci = new ECWorker();
        eci.m_op = OP_INITCURVE;
        eci.m_listener = listener;
        eci.m_nFieldSize = fieldSize;


        //#ifdef LOG
        //LogViewer.logExit("initializeCurve");
        //#endif

        return eci.start(priority);
    }

    public static final Thread generatePrivateKey(ECCurve curve, Random rand, int priority, ECListener listener)
    {
        ECWorker eci = new ECWorker();
        eci.m_op = OP_GENKEY;
        eci.m_listener = listener;
        eci.m_rand = rand;
        eci.m_E = curve;
        return eci.start(priority);
    }

    public static final Thread createPrivateKey(ECCurve curve, BigInteger s, int priority, ECListener listener)
    {
        ECWorker eci = new ECWorker();
        eci.m_op = OP_MAKEPRIKEY;
        eci.m_listener = listener;
        eci.m_E = curve;
        eci.m_f = s;
        return eci.start(priority);
    }

    public static final Thread createPublicKey(ECCurve curve, BigInteger x, BigInteger y, int priority, ECListener listener)
    {
        ECWorker eci = new ECWorker();
        eci.m_op = OP_MAKEPUBKEY;
        eci.m_listener = listener;
        eci.m_E = curve;
        eci.m_f = x;
        eci.m_y = y;
        return eci.start(priority);
    }

    public static final Thread createPublicKey(ECCurve curve, BigInteger x, boolean ybit, int priority, ECListener listener)
    {
        ECWorker eci = new ECWorker();
        eci.m_op = OP_MAKEPUBKEYYBIT;
        eci.m_listener = listener;
        eci.m_E = curve;
        eci.m_f = x;
        eci.m_ybit = ybit;
        return eci.start(priority);
    }

    public static final Thread ECSVDP_DH(ECPrivateKey priKey, ECPublicKey pubKey, int priority, ECListener listener)
    {
        ECWorker eci = new ECWorker();

        eci.m_priKey = priKey;
        eci.m_pubKey = pubKey;
        eci.m_op = OP_ECSVDP_DH;
        eci.m_listener = listener;
        return eci.start(priority);
    }

    public static final Thread ECSVDP_DHC(ECPrivateKey priKey, ECPublicKey pubKey, int priority, ECListener listener)
    {
        ECWorker eci = new ECWorker();

        eci.m_priKey = priKey;
        eci.m_pubKey = pubKey;
        eci.m_op = OP_ECSVDP_DHC;
        eci.m_listener = listener;
        return eci.start(priority);

    }

    public static final Thread ECSP_NR(ECPrivateKey priKey, BigInteger f, int priority, ECListener listener)
    {
        ECWorker eci = new ECWorker();

        eci.m_priKey = priKey;
        eci.m_f = f;
        eci.m_op = OP_ECSP_NR;
        eci.m_listener = listener;
        return eci.start(priority);
    }

    public static final Thread ECSP_DSA(ECPrivateKey priKey, BigInteger f, int priority, ECListener listener)
    {
        ECWorker eci = new ECWorker();

        eci.m_priKey = priKey;
        eci.m_f = f;
        eci.m_op = OP_ECSP_DSA;
        eci.m_listener = listener;
        return eci.start(priority);
    }

    public static final Thread ECVP_NR(ECPublicKey pubKey, ECSignature sig, int priority, ECListener listener)
    {
        ECWorker eci = new ECWorker();

        eci.m_pubKey = pubKey;

        eci.m_sig = sig;
        eci.m_op = OP_ECVP_NR;
        eci.m_listener = listener;
        return eci.start(priority);
    }

    public static final Thread ECVP_DSA(ECPublicKey pubKey, ECSignature sig, int priority, ECListener listener)
    {
        ECWorker eci = new ECWorker();

        eci.m_pubKey = pubKey;

        eci.m_sig = sig;
        eci.m_op = OP_ECVP_DSA;
        eci.m_listener = listener;
        return eci.start(priority);
    }

    public static final Thread verify(ECPublicKey pubKey, BigInteger f, ECSignature sig, int priority, ECListener listener)
    {
        ECWorker eci = new ECWorker();

        eci.m_pubKey = pubKey;
        eci.m_f = f;
        eci.m_sig = sig;
        eci.m_op = OP_VERIFY;
        eci.m_listener = listener;
        return eci.start(priority);
    }


    private Thread start(int priority)
    {
        //#ifdef LOG
        //LogViewer.logEnter("start");
        //#endif

        Thread t = new Thread(this);
        t.setPriority(priority);
        t.start();

        //#ifdef LOG
        //LogViewer.logExit("start");
        //#endif

        return t;

    }

    public void run()
    {
//        System.out.println("ECWorker run " + m_op);
        try
        {
            switch(m_op)
            {
            case OP_ECSVDP_DH:
                m_listener.notifyECEvent(ECListener.EVENT_ECSVDP_DH, m_priKey.ECSVDP_DH(m_pubKey));
//                m_listener.notifyECSVDP_DH(m_priKey.ECSVDP_DH(m_pubKey));
                break;

            case OP_ECSVDP_DHC:
                m_listener.notifyECEvent(ECListener.EVENT_ECSVDP_DHC, m_priKey.ECSVDP_DHC(m_pubKey, true));
//                m_listener.notifyECSVDP_DHC(m_priKey.ECSVDP_DHC(m_pubKey, true));
                break;

            case OP_ECSP_NR:
                m_listener.notifyECEvent(ECListener.EVENT_ECSP_NR, m_priKey.ECSP_NR(m_f));
//                m_listener.notifyECSP_NR(m_priKey.ECSP_NR(m_f));
                break;

            case OP_ECSP_DSA:
                m_listener.notifyECEvent(ECListener.EVENT_ECSP_DSA, m_priKey.ECSP_DSA(m_f));
//                m_listener.notifyECSP_DSA(m_priKey.ECSP_DSA(m_f));
                break;

            case OP_INITCURVE:
                //#ifdef LOG
//                LogViewer.logEnter("OP_INITCURVE");
                //#endif

                m_listener.notifyECEvent(ECListener.EVENT_CURVE_INITIALIZED, ECCurve.getStandardCurve(m_nFieldSize));
//                m_listener.notifyCurveInitialized(ECCurve.getStandardCurve(m_nFieldSize));
                //#ifdef LOG
//                LogViewer.logExit("OP_INITCURVE");
                //#endif
                break;

            case OP_GENKEY:
                m_listener.notifyECEvent(ECListener.EVENT_PRIVATE_KEY_GENERATED, ECPrivateKey.generate(m_E, m_rand));
//                m_listener.notifyPrivateKeyGenerated(ECPrivateKey.generate(m_E, m_rand));
                break;

            case OP_ECVP_NR:
                m_listener.notifyECEvent(ECListener.EVENT_ECVP_NR, m_pubKey.ECVP_NR(m_sig));
//                m_listener.notifyECVP_NR(m_pubKey.ECVP_NR(m_sig));
                break;

            case OP_ECVP_DSA:
                m_listener.notifyECEvent(ECListener.EVENT_ECVP_DSA, new Boolean(m_pubKey.ECVP_DSA(m_f, m_sig)));
//                        m_pubKey.ECVP_DSA(f, sig)_DSA(m_sig));
//                m_listener.notifyECVP_DSA(m_pubKey.ECVP_NR(m_sig));
                break;

//            case OP_VERIFY:
//                m_listener.notifyVerify(m_pubKey.verify(m_f, m_sig));
//                break;

            case OP_MAKEPRIKEY:
                m_priKey = new ECPrivateKey(m_E, m_f);
                m_listener.notifyECEvent(ECListener.EVENT_PRIVATE_KEY_CREATED, m_priKey);
//                m_listener.notifyPrivateKeyCreated(m_priKey);
                break;

            case OP_MAKEPUBKEY:
                m_pubKey = new ECPublicKey(new ECPoint(m_E, m_f, m_y));
                m_listener.notifyECEvent(ECListener.EVENT_PUBLIC_KEY_CREATED, m_pubKey);
//                m_listener.notifyPublicKeyCreated(m_pubKey);
                break;

            case OP_MAKEPUBKEYYBIT:
                m_pubKey = new ECPublicKey(new ECPoint(m_E, m_f, m_ybit ? 1 : 0));
                m_listener.notifyECEvent(ECListener.EVENT_PUBLIC_KEY_CREATED, m_pubKey);
//                m_listener.notifyPublicKeyCreated(m_pubKey);
                break;

            }
        }
        catch(Throwable t)
        {
            try
            {
                m_listener.notifyECEvent(ECListener.EVENT_EXCEPTION, t);
            }
            catch(Throwable t1)
            {
            }
        }
    }
}
