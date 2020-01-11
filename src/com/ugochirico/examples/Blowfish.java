package com.ugochirico.examples;

import com.ugochirico.crypt.AES.AESFast;
import com.ugochirico.util.Encoder;
import com.ugochirico.util.Random;

public class Blowfish {

	public static void main(String[] args)
	{
		com.ugochirico.crypt.blowfish.Blowfish blowfish = new com.ugochirico.crypt.blowfish.Blowfish();
		
		byte[] key128 = new byte[16];
		Random r = new Random();		
		r.nextBytes(key128);
				
		System.out.println(Encoder.bytesToHexString(key128));
		
		blowfish.setKey(key128);
		
		byte[] plaintext = "this is a plaintext string".getBytes();
		
		// ECB Encryption / Decrypion
		byte[] ciphertext = blowfish.encrypt(plaintext);

		System.out.println(Encoder.bytesToHexString(ciphertext));
		System.out.println(new String(ciphertext));
				
		// ECB Decryption
		byte[] plaintext1 = blowfish.decrypt(ciphertext);
		
		System.out.println(Encoder.bytesToHexString(plaintext1));		
		System.out.println(new String(plaintext1));
		
		
		
		
 	}
}
