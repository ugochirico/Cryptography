/*
 *   File:  ElGamalCipher.java
 *
 *   The ElGamal Public key algorithm.
 *
 *   version 1.032 v1a
 *   Copyright 1998, 1999 by Hush Communications Corporation, BWI
 *
 *   Copyright (C) 1995, 1996, 1997 Systemics Ltd on behalf
 *   of the Cryptix Development Team. All rights reserved.
 */

package com.ugochirico.crypt.elgamal;

import com.ugochirico.util.Random;

import com.ugochirico.math.BigInteger;

/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * Customized and ported on J2ME MIDP1.0
 */

public final class ElGamalCipher
{
   // 1024-bit pre-generated p and g (actually 1038 bit, but who's counting?)

   private BigInteger p = new BigInteger (
      "20f2221cdf61a1ccf248858184cbaf20119daba6044d9370837e8e7c2d67efd81758066"+
      "9408903f0fcebb2f08f3789365020bbeb116736bca1d09f5a5d9d7e85b8e750dd24149d"+
      "ec8be0cd6c039386112d3924fa07d1b3eaad4196f6dad0c6bba953477a466504ca62a4e"+
      "44f1a22cc704857c742913f971451e787601bd9fd41e6f5" , 16 );

   private BigInteger g = new BigInteger (
      "20ba729a42aacbdcf18103276f16980068c06649be0ac0891f0e23b2b79cf7e4eb31a3d"+
      "07f0444348327ee1042d81f568dbd4397652b9685175bcbc4355ee8e07bb2d349ed55c2"+
      "609e9b6f7bb972eedbaee1c07e3a5e01ccd18fc88ce67a883a11a4016b9f09a3a5ae1b5"+
      "102c298bdbaee16a06b670eec1518ba57bce6b6685c96e4" , 16 );

   private BigInteger x;
   private BigInteger y;

   private BigInteger ONE = new BigInteger("1");

   private static final int plainblocklength  = 128;     // 1024 bits for plaintext.
   private static final int cipherblocklength = 130*2;   // 1040 bits long, times 2 (a&b)

   private Random rand;


public    ElGamalCipher()
   {
      /* Do nothing here */
      /* Don't forget to set the keys and 'rand' before use... */
   }


   /*  This method accepts a byte array and sets y.
    */
public   void setPublicKey(byte[] key)
   {
      y = new BigInteger(1,key);
   }


   /*  This method accepts a byte array and sets and x.
    */
public   void setPrivateKey(byte[] key)
   {
      x = new BigInteger(1,key);
   }


public   void setRandomStream(Random passedRand)
   {
      rand = passedRand;
   }


   /*  For padding and encrypting one block of a known length */
 public  byte[] hushEncrypt(byte[] M, int ptextlength)
   {
      com.ugochirico.util.Random rand = new com.ugochirico.util.Random();
      byte[] randBytes = new byte[plainblocklength-ptextlength];
      rand.nextBytes(randBytes);

      byte[] paddedM = new byte[plainblocklength];

      System.arraycopy(M, 0, paddedM, 0, ptextlength);
      System.arraycopy(randBytes, 0, paddedM, ptextlength, plainblocklength-ptextlength);

      byte[] cbytes = encryptBlock(paddedM);
      return cbytes;
   }


  public byte[] hushDecrypt(byte[] ab, int ptextlength)
   {
      byte[] pbytes = decryptBlock(ab);
      byte[] finalPbytes = new byte[ptextlength];
      System.arraycopy(pbytes, 0, finalPbytes, 0, ptextlength);
      return finalPbytes;
   }


  public byte[] encryptBlock(byte[] M)
   {
      BigInteger MM = new BigInteger(1,M);
      BigInteger p_minus_1 = p.subtract(ONE);
      BigInteger k;
      do                // find a good k (relatively prime)
      {
         k = new BigInteger(p.bitLength()-1, rand);
         if (!(k.testBit(0)))
            k = k.setBit(0);
      }  while (!(k.gcd(p_minus_1).equals(ONE)));

      BigInteger aa = g.modPow(k,p);
      BigInteger bb = y.modPow(k,p).multiply(MM).mod(p);

      byte[] ta = getBytes(aa);
      byte[] tb = getBytes(bb);
      int la = ta.length;
      int lb = tb.length;

      byte[] a = new byte[cipherblocklength/2];
      byte[] b = new byte[cipherblocklength/2];

      for (int n=0; n<cipherblocklength/2; n++)
      {
         a[n] = (n >= cipherblocklength/2-la) ? ta[n-(cipherblocklength/2-la)] : 0;
         b[n] = (n >= cipherblocklength/2-lb) ? tb[n-(cipherblocklength/2-lb)] : 0;
      }

      byte[] ab = new byte[cipherblocklength];
      System.arraycopy(a, 0, ab, 0, cipherblocklength/2);
      System.arraycopy(b, 0, ab, cipherblocklength/2, cipherblocklength/2);
      return ab;
   }


   /*  This method accepts a block of cipher text
    *  consisting of parts a and b, divides it in half,
    *  and decrypts it, returning a byte array one less
    *  half the length of the ciphertext array
    */
public   byte[] decryptBlock(byte[] ab)
   {
      int l = ab.length/2;
      byte[] a = new byte[l];
      byte[] b = new byte[l];
      System.arraycopy(ab,0,a,0,l);
      System.arraycopy(ab,l,b,0,l);
      BigInteger aa = new BigInteger(1,a);
      BigInteger bb = new BigInteger(1,b);

      byte[] pbytes = getBytes(bb.multiply(aa.modPow(x,p).modInverse(p)).mod(p));

      /* Ensure that the plaintext block is exactly the length
       * of the modulus minus one byte.
       */
      if (pbytes.length < plainblocklength)
      {
         byte[] finalPbytes = new byte[plainblocklength];
         for (int n=0; n<plainblocklength-pbytes.length; n++)
            finalPbytes[n]=0;
         System.arraycopy(pbytes, 0, finalPbytes,
            plainblocklength-pbytes.length, pbytes.length);
         return finalPbytes;
      }
      if (pbytes.length > plainblocklength)      // Decrypt failed, give 0's.
      {
         byte[] finalPbytes = new byte[plainblocklength];
         return finalPbytes;
      }
      return pbytes;
   }


   //  Helper function beacuse we only want to deal with positive values
   private byte[] getBytes(BigInteger big)
   {
      byte[] bigBytes = big.toByteArray();
      if ((big.bitLength()%8) != 0)
         return bigBytes;
      else
      {
         byte[] smallerBytes = new byte[big.bitLength()/8];
         System.arraycopy(bigBytes, 1, smallerBytes, 0, smallerBytes.length);
         return smallerBytes;
      }
   }

}   //  end ElGamalCipher

