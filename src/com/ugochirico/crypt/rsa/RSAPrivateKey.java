/**
 * RSAPrivateKey.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.ugochirico.crypt.rsa;

import com.ugochirico.util.*;
import com.ugochirico.util.Random;
import com.ugochirico.math.*;

import java.util.*;
import java.io.*;

/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * Customized and ported on J2ME MIDP1.0
 */
public class RSAPrivateKey
{
    final BigInteger m_d;
    final BigInteger m_m;
    final BigInteger m_e;

    private final static long serialVersionUID = 100000001L;

    private static final BigInteger ONE = BigInteger.valueOf(1);

//    public static void main(String args[])
//    {
//        try
//        {
//            org.logi.crypto.keys.KeyPair tkdKeyPair  =
//                org.logi.crypto.keys.RSAPrivateKey.createKeys(new BigInteger(1, Encoder.hexStringToBytes("00010001")),
//                                                              new BigInteger(1, Encoder.hexStringToBytes("2E53C1EBAD129BACE0E074FE730DD3FD81E689FB7CFC833018D512C24FEB9C2917046AB93ADDE99EE1C6F121FFDCB998F95A5CBD9B324E77E92B49B34FBEC9CB63F020868E4E4E0A3251D54AA5EA268DC0FE7DEFE2AA6527E30FBD90CEB9BBA1E8065CD7ACFB5ED1C0EDACCA81CB9A3FFFF09914D0D7F6318B87D1D2FF698B0B6E6E2E4751952743F1B7DC038B71582F25F5AE563145888C1537AF346ED0C70D911F8B1F79ACE93B9AA2E92B6DF0AA77FC775CCAE104E549BFADFA900F36D54EB5AED8A2DF03727F5D119F2A282E4929A51FE2D6BD15CA11AB9F2097F5B964E928E9A1D32ADAA4638E0DCFA800B5F3D76FDE95E970735C9F63E8C424F059B081")),
//                                                              new BigInteger(1, Encoder.hexStringToBytes("EF4AE67C44677AFC611DD17DBDBEEE792D9994DCF3E4E2E09B0F68193C00E3AFAE466931814624BCC2753E7C4EBE55A9DD866B0408B6E9EE6613C00D6D283905C3DD3998FECE88B9F44C5717783007959EC10711C47C1F5C2BBC382EBC18BE60EBD431ACE8D16DB3CEEB4B0F7791839FAE56604F9D7933855C844E04DE089ABDAFAC028D0814891F70687B3233FB60A2FFB819CAEC26A2431E2A7E5051EC085BE4639D8D22A4FA96B72E5DA358F8BE49DD344A4AE71F22764F0380C937478C8C3F704AF23DB0E538BA37A6A524E22841FE2F5164971C069531BAA1AA052AD966B1FF53F924419F6C9064F1166D7BEA490151CCED94CA46091FEEAD469A58C8A3")));
//
//            org.logi.crypto.keys.RSAPrivateKey tkdPriKey  = (org.logi.crypto.keys.RSAPrivateKey)tkdKeyPair.getPrivate();
//            org.logi.crypto.keys.RSAPublicKey tkdPubKey  = (org.logi.crypto.keys.RSAPublicKey)tkdKeyPair.getPublic();
//
//            System.out.println("tkdPriKey: " + Encoder.bytesToHexString(tkdPriKey.getEncoded()).toUpperCase());
//            System.out.println("tkdPubKey: " + Encoder.bytesToHexString(tkdPubKey.getEncoded()).toUpperCase());
//
//
//            // chiave TKD2
////            keygen = KeyPairGenerator.getInstance("RSA");
////            keygen.initialize(2048);
////            KeyPair tkd2Pair = keygen.generateKeyPair();
////          outs = new ObjectOutputStream(new FileOutputStream("tkd2.pair"));
////          outs.writeObject(tkd2Pair);
////          outs.close();
//
////            PublicKey tkd2PubKey  = tkd2Pair.getPublic();
////            PrivateKey tkd2PriKey = tkd2Pair.getPrivate();
////            System.out.println("tkd2PriKey: " + Encoder.bytesToHexString(tkd2PriKey.getEncoded()).toUpperCase());
////            System.out.println("tkd2PubKey: " + Encoder.bytesToHexString(tkd2PubKey.getEncoded()).toUpperCase());
//
//            // prepara la stringa da firmare
//            String strToSign = "Ugo";
////                "TKD" +
////                Encoder.bytesToHexString(tkd2PriKey.getEncoded()).toUpperCase().substring(76, 588) +
////                "00" +
////                Encoder.bytesToHexString(tkd2PriKey.getEncoded()).toUpperCase().substring(592, 598) +
////                "TKD-RFI"+"TKDRFIPUBKEY2";
//
//            strToSign = strToSign.toUpperCase();
//            System.out.println("strToSign: " + strToSign);
//            Fingerprint finger = Fingerprint.create(strToSign, "SHA1");
//            System.out.println("Fingerprint " + Encoder.bytesToHexString(finger.getBytes()).toUpperCase());
//            Signature sig = tkdPriKey.sign(finger);
//
//            byte btSig[] = sig.getBytes();
//
//
////            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
////          keygen.initialize(2048);
////
////          // chiave TKD
////          KeyPair tkdPair = keygen.generateKeyPair();
//////          ObjectOutputStream outs = new ObjectOutputStream(new FileOutputStream("tkd.pair"));
//////          outs.writeObject(tkdPair);
//////          outs.close();
////
////          PrivateKey tkdPriKey  = tkdPair.getPrivate();
////
////            PublicKey tkdPubKey  = tkdPair.getPublic();
////            System.out.println("tkdPriKey: " + Encoder.bytesToHexString(tkdPriKey.getEncoded()).toUpperCase());
////            System.out.println("tkdPubKey: " + Encoder.bytesToHexString(tkdPubKey.getEncoded()).toUpperCase());
////
////
////            // chiave TKD2
////            keygen = KeyPairGenerator.getInstance("RSA");
////            keygen.initialize(2048);
////            KeyPair tkd2Pair = keygen.generateKeyPair();
//////          outs = new ObjectOutputStream(new FileOutputStream("tkd2.pair"));
//////          outs.writeObject(tkd2Pair);
//////          outs.close();
////
////            PublicKey tkd2PubKey  = tkd2Pair.getPublic();
////            PrivateKey tkd2PriKey = tkd2Pair.getPrivate();
////            System.out.println("tkd2PriKey: " + Encoder.bytesToHexString(tkd2PriKey.getEncoded()).toUpperCase());
////            System.out.println("tkd2PubKey: " + Encoder.bytesToHexString(tkd2PubKey.getEncoded()).toUpperCase());
////
////            // prepara la stringa da firmare
////            String strToSign = "Ugo";
//////                "TKD" +
//////                Encoder.bytesToHexString(tkd2PriKey.getEncoded()).toUpperCase().substring(76, 588) +
//////                "00" +
//////                Encoder.bytesToHexString(tkd2PriKey.getEncoded()).toUpperCase().substring(592, 598) +
//////                "TKD-RFI"+"TKDRFIPUBKEY2";
////
////            strToSign = strToSign.toUpperCase();
////            System.out.println("strToSign: " + strToSign);
////
////            Signature sig = Signature.getInstance("SHA1withRSA");
////            sig.initSign(tkdPriKey);
////            sig.update(strToSign.getBytes());
////
////            byte btSig[] = sig.sign();
//
//            System.out.println("sig : " + Encoder.bytesToHexString(btSig).toUpperCase());
//        }
//        catch(Throwable th)
//        {
//            th.printStackTrace();
//        }
//    }
//    public static void main1(String args[])
//    {
//        try
//        {
//            // genera TKD
//            //RSAPrivateKey tkd = RSAPrivateKey.generate(2048, new Random(System.currentTimeMillis()));
//            //tkd.save("tkd_2.0.pri");
//
//            // carica TKD
//            //RSAPrivateKey tkd = RSAPrivateKey.load("tkd_2.0.pri");
//
////          RSAPrivateKey tkd = RSAPrivateKey.create(new BigInteger(Encoder.hexStringToBytes("E901C04F1F6D6BBD132AF80785AAF96E8FE157383551C962FB428306E05EFA80B2D17ABE2D72F352F46E4506245F9ECAEED99B93EA3530C4F7A10A464661A34AD5E7E88746AB3FE052DBCEFB89526A67FC4E4D6AECED5414782876222AF2F43B38BF2495DF5BFAA949A856D2CC9736B321CF6EA50D0E8F0C2D13C3F25CE1D801")),
////                                                   new BigInteger(Encoder.hexStringToBytes("CEACA28FBA9C4FFD26A394984A98CCBB910539862FD395FDE076C2897412655C328E5AB866FAB48C7D672265558945AC3B876E0390F781E2227DC5751F0F745696E4ED8DE669EAC76245AFEEE006AD122005CFF91D7075D8E06D3D13903A71644562429C8B606A66764A7D51816F79FFFE33787F45B2FA8C380DE657F5F71A9B")),
////                                                   new BigInteger(Encoder.hexStringToBytes("00010001")),
////                                                   new BigInteger(Encoder.hexStringToBytes("BC1C89E2CDF933E4F60D8EF644432E53FB30D7482054009179CBE49D60F7C791AA36F848194662E91D91EE8F58525270E8B26F5DE75358742FF1343DF95C6F526EFF7612D6D1E22FC2E3A7F485A6A9ADCCB487EB7CDB9C3AB37FDA8E87F234D43F3ECBFC0D5B12A35B46B3DEB5498602FFC16D18938D19BDD3D16F9D75AA6DBCC07D4CC7B0B3EA5FCACD2B813A5720C75376BBE5870D39FD30714ECC2F7BC26A73F3323A667DCF13ECCA47CE3EC0FB27E114947269B93C740569594669811A1D75061A441D8F760EAD29D614079EE1DFE9E06DFC4823F8CAB6C0E5E6985003E0C04617924CBE957ACF915144AF4D03172FBA5FB4E7C3D1A0F77851248AA4E29B")));
////
////          RSAPrivateKey tkd2 = RSAPrivateKey.create(new BigInteger(Encoder.hexStringToBytes("F4C970BAD8A2AE4D77799DDF524B775388661B733466C80D6F5E574C31BAE9823740F8E7BA41C2C65528FC97BF569E24E83CA55633F61C7C845F8591137FFF25139083EE03891CE1CA98678CAFCBA33222523F136EF443515DDAA937DEB8753EAA987383582E2D5C9EE6173AF5C752E2A8924E97812632DB81E6CCC9361278E3")),
////                                                   new BigInteger(Encoder.hexStringToBytes("CF7A0558E29F7D5E049B6CCE56A58CA1D09E54C1FEE142728FBFF1FB5969612F52B51CEEF9CDDFD2B0BA90877AC086D641235FF8FF4B099552A4B8953184EDD515EAB5228A97763CAD6117A1A2E6ED4F09180DA394960B0D62DFF53DC163A3462603FC23B12F690E3A57B54F3C55960DC4A14981B40CA3059F54240DB6C66CBF")),
////                                                   new BigInteger(Encoder.hexStringToBytes("00010001")),
////                                                   new BigInteger(Encoder.hexStringToBytes("C6638F43BB5B856F939043A5574F7BA8A5B390D41FC0C5281DB0E06DEB2C2AB27F2E101AAFBBD0DB990DEF31D283F791F43747C9770B8E5E68E6B5E8EDB5E9516CAD1246F7CD8F8279EFD20B60530F0ABD325A29A474E9D7E32C116DD82BBB52C83F07D0C8EF1183309386927315848A0B8497D291F94FBDA81D77C30A78B7F9C84257E067BF9EE8DFF48B7E192095B66D49135ABE5302511E21630A99DE7065256432D9B8A59E8D8ABE9170FC003A429D9FA14988DDA8C1DFCEFE036B5CA24096F6E79C170ADCE875A25589168ACE9947DA8D5A02115A386975315B83AD71EF240F6E72543D755BE93A308E53C63F8CD9004E2B2D2DE912B914B0B10459F55D")));
//
//
//            // genera una nuova chiave TK2
//            //RSAPrivateKey tkd2 = RSAPrivateKey.generate(2048, new Random(System.currentTimeMillis()));
//            //tkd2.save("tkd2.pri");
//
//            // carica TKD2
//            //RSAPrivateKey tkd2 = RSAPrivateKey.load("tkd2.pri");
//            RSAPrivateKey tkd1 = new RSAPrivateKey(new BigInteger(1, Encoder.hexStringToBytes("EF4AE67C44677AFC611DD17DBDBEEE792D9994DCF3E4E2E09B0F68193C00E3AFAE466931814624BCC2753E7C4EBE55A9DD866B0408B6E9EE6613C00D6D283905C3DD3998FECE88B9F44C5717783007959EC10711C47C1F5C2BBC382EBC18BE60EBD431ACE8D16DB3CEEB4B0F7791839FAE56604F9D7933855C844E04DE089ABDAFAC028D0814891F70687B3233FB60A2FFB819CAEC26A2431E2A7E5051EC085BE4639D8D22A4FA96B72E5DA358F8BE49DD344A4AE71F22764F0380C937478C8C3F704AF23DB0E538BA37A6A524E22841FE2F5164971C069531BAA1AA052AD966B1FF53F924419F6C9064F1166D7BEA490151CCED94CA46091FEEAD469A58C8A3")),
//                                                   new BigInteger(1, Encoder.hexStringToBytes("00010001")),
//                                                   new BigInteger(1, Encoder.hexStringToBytes("2E53C1EBAD129BACE0E074FE730DD3FD81E689FB7CFC833018D512C24FEB9C2917046AB93ADDE99EE1C6F121FFDCB998F95A5CBD9B324E77E92B49B34FBEC9CB63F020868E4E4E0A3251D54AA5EA268DC0FE7DEFE2AA6527E30FBD90CEB9BBA1E8065CD7ACFB5ED1C0EDACCA81CB9A3FFFF09914D0D7F6318B87D1D2FF698B0B6E6E2E4751952743F1B7DC038B71582F25F5AE563145888C1537AF346ED0C70D911F8B1F79ACE93B9AA2E92B6DF0AA77FC775CCAE104E549BFADFA900F36D54EB5AED8A2DF03727F5D119F2A282E4929A51FE2D6BD15CA11AB9F2097F5B964E928E9A1D32ADAA4638E0DCFA800B5F3D76FDE95E970735C9F63E8C424F059B081")));
//
//
//            // produce la stringa da firmare
//            // parte per chiave pubblica TKD SKD
//            String strToSign = "Ugo";//"TKDD41E684D42FE509DADE18F19E81551F388C53AC1AFD446BCB94A13FED2E653D9261ABA19433B0AC4957B67FAA9B3915B4B119A7FCF45C5A899E7273EA7BD419BB48A3EA8DC95688729C70FBCE674B60445722C68EC506DD28FAE7FDAE25FB48194D75132DF05E4EC3F33372A1C337176D636E994025E1A0F8660024043DA926EE372B90E0300431F9AE3170E80D1865C624A54AB4A003AA5B8D4B07D95D589AF22774F0ED5B705511BBC2D86310CB9005FCF4181AD708CB04FC91CBC78EC172C47181B6D4FACDD901A1CEB782D3F9CA5961F2A1CE5CCFCB41093967F8E9A42E4F56DF404568655ABA27BC6579C9C8C69C2CD2097FCB1575D3F7B362868D6762B00010001TKD-RFITKDRFIPUBKEY2";
//                //"TKD" + tkd2.getModulus() + "00" + tkd2.getEExponent()+"TKD-RFI"+"TKDRFIPUBKEY2";
//            strToSign = strToSign.toUpperCase();
//
//            System.out.println("strToSign: " + strToSign);
//
//            // parte per masterkey
//            //String strToSign = "00010001"+ "CAAC975AEF3E2C08710A2D482A81E2DB4A3D823F6F23608AB8E095594B68CF3B5A1EC034CAF5018182CB02D56592EF52529B96552F649EDF271F350056391BC30E58A0C1F49FB229D5DCE378DFB3475377DADD054BE9A18E96BD6C59C81C6C5B5D3562ED7D18759C3768B42780B0B5FF963E1CDA1361631F19CF12A51CC939C18059195C3AB39F862DF2C878F632C4F47E2CDB1B8360AABB8A8A41DE997915474D5C43A575CA2D1596C585CF75BE66CA299BDBD86668E856960B835EB270FC2AE518F2F563AB3BD4C34D9B755E2A0A0A75B1864DD03BEA9E9A272034DD8191B4049D598374DCA659C01C40164D7A53F857E8C58F6EE9313FCE9A12625868ED6F";
//
//            // genera lo sha1
//            SHA1 sha1 = new SHA1();
//            byte btSha1[] = sha1.getSHA1Hash(strToSign);
//
//            System.out.println("btSha1: " + Encoder.bytesToHexString(btSha1).toUpperCase());
//            System.out.println("btSha1 len: " + btSha1.length);
//
//            // prepara il digest info
//            byte btDigestInfo1[] =  {0x30, 0x21, 0x30, 0x09 , 0x06 , 0x05 , 0x2b , 0x0e , 0x03 , 0x02 , 0x1a , 0x05 , 0x00 , 0x04 , 0x14};
//
//            // ricopia il digestinfo
//            byte btDigestInfo[] = new byte[btDigestInfo1.length + btSha1.length];
//            System.arraycopy(btDigestInfo1,0,btDigestInfo, 0, btDigestInfo1.length);
//
//            // concatena la stringa hash
//            System.arraycopy(btSha1, 0, btDigestInfo, btDigestInfo1.length, btSha1.length);
//
//            System.out.println("btDigestInfo: " + Encoder.bytesToHexString(btDigestInfo).toUpperCase());
//
//            // applica il padding PKCS#1
//
//            // calcola la paddins string
//            int nPSLen = (2048 / 8) - 2 - btDigestInfo.length;
//
//            System.out.println("btPS len :" + nPSLen);
//
//            byte btPS[] = new byte[nPSLen];
//            for(int i = 0; i < nPSLen; i++)
//                btPS[i] = (byte)0xFF;
//
//            System.out.println("PS: " + Encoder.bytesToHexString(btPS).toUpperCase());
//
//            byte btEM[] = new byte[(2048 / 8)];
//
//            // concatena la stringa hash
//            btEM[0] = 0x01;
//
//            // copia la PS
//            System.arraycopy(btPS, 0, btEM, 1, nPSLen);
//
//            System.out.println("EM 1: " + Encoder.bytesToHexString(btEM).toString());
//
//            // separatore
//            btEM[nPSLen + 1] = 0x00;
//
//            System.out.println("EM 2: " + Encoder.bytesToHexString(btEM).toUpperCase());
//
//            System.out.println("btDigestInfo Len: " + btDigestInfo.length);
//
//            // copia digestinfo
//            System.arraycopy(btDigestInfo, 0, btEM, nPSLen + 2, btDigestInfo.length);
//
//            System.out.println("EM 3: " + Encoder.bytesToHexString(btEM).toUpperCase());
//
//            // applica la firma
//            BigInteger biToSign = new BigInteger(1, btEM);
//
//            System.out.println("biToSign : " + biToSign);
//            System.out.println("biToSign : " + Encoder.bytesToHexString(biToSign.toByteArray()).toUpperCase());
//
//            // firma con la vecchia chiave
//            BigInteger sig = tkd1.apply(biToSign);
//
//            // firma con la nuova chiave
//            //BigInteger sig = tkd2.apply(biToSign);
//
//            System.out.println("SIG: " + Encoder.bytesToHexString(sig.toByteArray()).toUpperCase());
//        }
//        catch(Exception ex)
//        {
//            ex.printStackTrace();
//        }
//    }
//    public static RSAPrivateKey load(String strFileName) throws IOException
//    {
//        Properties props = new Properties();
//        FileInputStream ins = new FileInputStream(strFileName);
//        props.load(ins);
//        ins.close();
//
//        return load(props);
//    }

    public static RSAPrivateKey load(Hashtable props)
    {
        return new RSAPrivateKey(
            new BigInteger(1, Encoder.hexStringToBytes((String)props.get("d-exponent"))),
            new BigInteger(1, Encoder.hexStringToBytes((String)props.get("e-exponent"))),
            new BigInteger(1, Encoder.hexStringToBytes((String)props.get("modulus"))));
    }

//    public static RSAPrivateKey load(InputStream ins) throws IOException
//    {
//        Properties props = new Properties();
//        props.load(ins);
//
//        return load(props);
//    }

    public static RSAPrivateKey generate(int strength, Random rand)
    {
        if(strength < 8)
            throw new IllegalArgumentException("Modulus too small");

        boolean end = false;
        BigInteger  p = null,
                    q = null,
                    e = null,
                    d = null;

        // Select public exponent
        e = BigInteger.valueOf(65537);

        // Select p and q

        p = new BigInteger(strength / 2, 99, rand);
        while(!p.isProbablePrime(100))
            p = new BigInteger(strength / 2, 99, rand);

//      System.out.println("found p:" + p.toString());

        q = new BigInteger(strength / 2, 99, rand);
        while(!q.isProbablePrime(100))
            q = new BigInteger(strength / 2, 99, rand);

//      System.out.println("found q:" + q.toString());
//      System.out.println("found fi:" + fi.toString());

        while (!p.subtract(ONE).gcd(e).equals(ONE) || !q.subtract(ONE).gcd(e).equals(ONE))
            e = e.add(BigInteger.valueOf(2));

        d = e.modInverse((p.subtract(ONE)).multiply(q.subtract(ONE)));

//      System.out.println("OK");

//      System.out.println("found m:" + m.toString());
//      System.out.println("found d:" + d.toString());
//      System.out.println("found e:" + e.toString());

        return new RSAPrivateKey(d, e, p.multiply(q));
    }

    public static RSAPrivateKey create(final BigInteger p, final BigInteger q, final BigInteger e, final BigInteger m)
    {
        BigInteger  d = null;
        //BigInteger  mTest = null;

        d = e.modInverse((p.subtract(ONE)).multiply(q.subtract(ONE)));

//        mTest = p.multiply(q);

        //if(!mTest.equals(m))
        //  throw new IllegalArgumentException("bad modulus");

//      System.out.println("OK");

//      System.out.println("found m:" + m.toString());
//      System.out.println("found d:" + d.toString());
//      System.out.println("found e:" + e.toString());

        //return new RSAPrivateKey(d, e, p.multiply(q));
        return new RSAPrivateKey(d, e, m);
    }

    RSAPrivateKey(BigInteger d, BigInteger e, BigInteger m)
    {
        m_d = d;
        m_e = e;
        m_m = m;
    }

//  public static RSAPrivateKey create(String eHex, String dHex, String mHex)
//    {
//      return new RSAPrivateKey(new BigInteger(1, Encoder.hexStringToBytes(dHex)),
//                               new BigInteger(1, Encoder.hexStringToBytes(eHex)),
//                               new BigInteger(1, Encoder.hexStringToBytes(mHex)));
//    }

    public BigInteger apply(BigInteger data)
    {
        if(data.compareTo(m_m) > 0)
        {
            //System.out.println("Data: " + Encoder.bytesToHexString(data.toByteArray()));
            //System.out.println("modulus: " + Encoder.bytesToHexString(m_m.toByteArray()));
            throw new IllegalArgumentException("data is greater then modulus");
        }

        return data.modPow(m_d, m_m);
    }

    public int getLength()
    {
        return m_m.bitLength() / 8;
    }

    public RSAPublicKey getPublicKey()
    {
        return new RSAPublicKey(m_e, m_m);
    }

//    public final void save(String strFileName) throws IOException
//    {
//        Properties props = new Properties();
//
//        save(props);
//
//        FileOutputStream outs = new FileOutputStream(strFileName);
//
//        props.save(outs, "Private Key");
//        outs.close();
//    }

    public final void save(Hashtable props)
    {
        props.put("modulus",     Encoder.bytesToHexString(m_m.toByteArray()).toUpperCase());
        props.put("d-exponent", Encoder.bytesToHexString(m_d.toByteArray()).toUpperCase());
        props.put("e-exponent", Encoder.bytesToHexString(m_e.toByteArray()).toUpperCase());
    }

    public String getDExponent()
    {
        return Encoder.bytesToHexString(m_d.toByteArray());
    }

    public String getModulus()
    {
        return Encoder.bytesToHexString(m_m.toByteArray());
    }
    public String getEExponent()
    {
        return Encoder.bytesToHexString(m_e.toByteArray());
    }
}



