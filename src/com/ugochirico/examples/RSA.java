package com.ugochirico.examples;

import com.ugochirico.crypt.rsa.RSAPrivateKey;
import com.ugochirico.crypt.rsa.RSAPublicKey;
import com.ugochirico.math.BigInteger;
import com.ugochirico.util.Encoder;
import com.ugochirico.util.Random;

public class RSA {

	public static void main(String[] args)
	{
		Random r = new Random();		
		
		RSAPrivateKey priKey = RSAPrivateKey.generate(2048, r);		
		RSAPublicKey pubKey = priKey.getPublicKey();
			
		byte[] plaintext = "this is a plaintext string".getBytes();
		
		BigInteger plaintextData = new BigInteger(plaintext);
		
		// Encrypt with public key
		
		BigInteger ciphetextData = pubKey.apply(plaintextData);
		
		byte[] ciphertext = ciphetextData.toByteArray();

		System.out.println(Encoder.bytesToHexString(ciphertext));
		System.out.println(new String(ciphertext));
		
		// Decrypt with private key
		
		BigInteger plaintextData1 = priKey.apply(ciphetextData);
		byte[] plaintext1 = plaintextData.toByteArray();
		
		System.out.println(Encoder.bytesToHexString(plaintext1));
		System.out.println(new String(plaintext1));				
 	}
}
