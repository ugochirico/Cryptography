/*
 *   File:  ElGamalKeys.java
 *
 *   ElGamal key generation routines and constants.
 *
 *   version 1.032 v1a
 *   Copyright 1998, 1999 by Hush Communications Corporation, BWI
 *
 *   Copyright (C) 1995, 1996, 1997 Systemics Ltd on behalf
 *   of the Cryptix Development Team. All rights reserved.
 */

package com.ugochirico.crypt.elgamal;

import com.ugochirico.math.BigInteger;

/**
 * @author  Ugo Chirico <ugo.chirico@ugosweb.com>
 * Customized and ported on J2ME MIDP1.0
 */
public final class ElGamalKeys
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

   private BigInteger x;  // the Private key
   private BigInteger y;  // the Public key
   private int s = 130;   // Length of modulus in bytes (1040 bits)
   private int strength = 1024;  // Length of private key in bits


   /* Class constructor.
    * Holds p, g, x, and y.
    * p and g are constants.  Given x (totally random), we generate y.
    */
public    ElGamalKeys(byte[] privKey)
   {
      privKey[0] = (byte) ( privKey[0] & 0x7f);
      x = new BigInteger(privKey);         //  Make sure x is < p, btw...
      x = x.setBit(strength - 1);          //  give x 1024 significant bits...
      y = g.modPow(x,p);                   //  Create y, the Public key.
   }


   //  Returns y in a byte array the length of the modulus.
public   byte[] publicKey()
   {
      byte[] yy = y.toByteArray();
      int yl = yy.length;
      if (yl == s)  return yy;

      //  Copy y to key, making sure it's s bytes in length
      byte[] key = new byte[s];
      for (int n=0; n<s; n++)
      {
         if (n >= s-yl)
            key[n] = yy[n-(s-yl)];
         else
            key[n]=0;
      }
      return key;
   }


   //  Returns x in a byte array the length of the modulus.
public    byte[] privateKey()
   {
      byte[] xx = x.toByteArray();
      int xl = xx.length;
      if (xl == s)  return xx;

      //  Copy x to key, making sure it's s bytes in length
      byte[] key = new byte[s];
      for (int n=0; n<s; n++)
      {
         if (n >= s-xl)
            key[n] = xx[n-(s-xl)];
         else
            key[n]=0;
      }
      return key;
   }


}  //  end ElGamalKeys

