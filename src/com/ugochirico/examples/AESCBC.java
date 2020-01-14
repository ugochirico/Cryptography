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
				
		byte[] iv = new byte[aes.getBlockSize()];
		
		System.out.println(Encoder.bytesToHexString(key128));
		System.out.println(Encoder.bytesToHexString(iv));
		
		aes.init(true, key128);
		
		byte[] plaintext = "this is a plaintext string".getBytes();
		
		// CBC Encryption / Decrypion
		byte[] ciphertext = aes.encryptCBC(plaintext, 0, iv);

		System.out.println(Encoder.bytesToHexString(ciphertext));
		System.out.println(new String(ciphertext));
		
		aes = new AESFast();			
		aes.init(false, key128);
				
		// CBC Decryption
		byte[] plaintext1 = aes.decryptCBC(ciphertext, 0, iv);
		
		System.out.println(Encoder.bytesToHexString(plaintext1));		
		System.out.println(new String(plaintext1));
		
		
		
		
 	}
}
