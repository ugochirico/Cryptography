/**
 * RSAPublicKey.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.ugochirico.crypt.rsa;

import com.ugochirico.util.*;
import com.ugochirico.math.*;

import java.io.*;
import java.util.*;

/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * Customized and ported on J2ME MIDP1.0
 */
public class RSAPublicKey //implements Serializable
{
    private final static long serialVersionUID = 100000001L;

    private BigInteger m_e;
    private BigInteger m_m;

    public RSAPublicKey(BigInteger e, BigInteger m)
    {
        m_e = e;
        m_m = m;
    }

//  public static RSAPublicKey create(String eHex, String mHex)
//    {
//      return new RSAPublicKey(new BigInteger(1, Encoder.hexStringToBytes(eHex)),
//                              new BigInteger(1, Encoder.hexStringToBytes(mHex)));
//    }

    public BigInteger apply(BigInteger data)
    {
        if(data.compareTo(m_m) > 0)
            throw new IllegalArgumentException("data is greater then modulus");

        return data.modPow(m_e, m_m);
    }

//  public boolean verify(BigInteger signedData, BigInteger data)
//  {
//      if(data.bitLength() > m_m.bitLength() - 11)
//          throw new IllegalArgumentException("data is greater then modulus");
//
//      BigInteger signed = apply(data);
//
//      byte toSign[] = removePadding(data.toByteArray());
//
//      return signed;
//  }
//
//  public boolean verify(byte signedData[], byte data[])
//  {
//      return verify(new BigInteger(data)).toByteArray();
//  }

    public final void save(Hashtable props) throws IOException
    {
        props.put("modulus", Encoder.bytesToHexString(m_m.toByteArray()));
        props.put("e-exponent", Encoder.bytesToHexString(m_e.toByteArray()));
    }

    public final void save(String strFileName) throws IOException
    {
//        Properties props = new Properties();
//
//        save(props);
//
//        FileOutputStream outs = new FileOutputStream(strFileName);
//        props.save(outs, "Public Key");
//        outs.close();
    }

//    public static RSAPublicKey load(InputStream ins) throws IOException
//    {
//        Properties props = new Properties();
//        props.load(ins);
//
//        return load(props);
//    }
//
//    public static RSAPublicKey load(String strFileName) throws IOException
//    {
//        Properties props = new Properties();
////        FileInputStream ins = new FileInputStream(strFileName);
////        props.load(ins);
////        ins.close();
//
//        return load(props);
//    }

    public static RSAPublicKey load(Hashtable props)
    {
        return new RSAPublicKey(
            new BigInteger(1, Encoder.hexStringToBytes((String)props.get("e-exponent"))),
            new BigInteger(1, Encoder.hexStringToBytes((String)props.get("modulus"))));
    }

    public String getEExponent()
    {
        return Encoder.bytesToHexString(m_e.toByteArray());
    }

    public String getModulus()
    {
        return Encoder.bytesToHexString(m_m.toByteArray());
    }
}

