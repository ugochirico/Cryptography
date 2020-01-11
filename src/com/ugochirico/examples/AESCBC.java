package com.ugochirico.examples;

import com.ugochirico.crypt.AES.AESFast;
import com.ugochirico.util.Encoder;
import com.ugochirico.util.Random;

public class AESCBC {

	public static void main(String[] args)
	{
		AESFast aes = new AESFast();
		
		byte[] key128 = new byte[16];
		Random r = new Random();		
		r.nextBytes(key128);
				
		System.out.println(Encoder.bytesToHexString(key128));
		
		aes.init(true, key128);
		
		byte[] plaintext = "this is a plaintext string".getBytes();
		
		// ECB Encryption / Decrypion
		byte[] ciphertext = aes.encryptECB(plaintext, 0);

		System.out.println(Encoder.bytesToHexString(ciphertext));
		System.out.println(new String(ciphertext));
		
		aes = new AESFast();			
		aes.init(false, key128);
				
		// ECB Decryption
		byte[] plaintext1 = aes.decryptECB(ciphertext, 0);
		
		System.out.println(Encoder.bytesToHexString(plaintext1));		
		System.out.println(new String(plaintext1));
		
		
		
		
 	}
}
