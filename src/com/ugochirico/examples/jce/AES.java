package com.ugochirico.examples.jce;

import java.security.GeneralSecurityException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.ugochirico.util.Encoder;

public class AES {

	public static SecretKey generateKey() throws GeneralSecurityException
	{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(256);
		return keyGenerator.generateKey();
	}

	public static SecretKey defineKey(byte[] keyBytes)
	{
		if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32)
		{
			throw new IllegalArgumentException("keyBytes wrong length for AES key");
		}
		return new SecretKeySpec(keyBytes, "AES");
	}
	
	public static byte[] ecbEncrypt(SecretKey key, byte[] data) throws GeneralSecurityException
	{
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(data);
	}
	
	public static byte[] ecbDecrypt(SecretKey key, byte[] cipherText) throws GeneralSecurityException
	{
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(cipherText);
	}
	
	public static byte[] cbcEncrypt(SecretKey key, byte[] iv, byte[] data) throws GeneralSecurityException
	{
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
		return cipher.doFinal(data);
	}
	
	public static byte[] cbcDecrypt(SecretKey key, byte[] iv, byte[] cipherText) throws GeneralSecurityException
	{
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		return cipher.doFinal(cipherText);
	}
	
	public static byte[] cfbEncrypt(SecretKey key, byte[] iv, byte[] data) throws GeneralSecurityException
	{
		Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
		return cipher.doFinal(data);
	}
	public static byte[] cfbDecrypt(SecretKey key, byte[] iv, byte[] cipherText) throws GeneralSecurityException
	{
		Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		return cipher.doFinal(cipherText);
	}

	public static byte[][] ctrEncrypt(SecretKey key, byte[] iv, byte[] data) throws GeneralSecurityException
	{
		Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "BCFIPS");
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
		return new byte[][] { cipher.getIV(), cipher.doFinal(data) };
	}
	
	public static byte[] ctrDecrypt(SecretKey key, byte[] iv, byte[] cipherText) throws GeneralSecurityException
	{
		Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "BCFIPS");
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		return cipher.doFinal(cipherText);
	}
	
	public static void main(String[] args)
	{		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		try 
		{
			SecretKey key = generateKey();
					
			System.out.println(Encoder.bytesToHexString(key.getEncoded()));
			
			byte[] plaintext = "this is a plaintext string".getBytes();
			
			// ECB Encryption / Decrypion
			byte[] ciphertext = ecbEncrypt(key, plaintext);

			System.out.println(Encoder.bytesToHexString(ciphertext));
			System.out.println(new String(ciphertext));
							
			// ECB Decryption
			byte[] plaintext1 = ecbDecrypt(key, ciphertext);
			
			System.out.println(Encoder.bytesToHexString(plaintext1));		
			System.out.println(new String(plaintext1));
					
			byte[] iv = new byte[16];
			
			System.out.println(Encoder.bytesToHexString(iv));
					
			// ECB Encryption / Decrypion
			byte[] ciphertext1 = cbcEncrypt(key, iv, plaintext);

			System.out.println(Encoder.bytesToHexString(ciphertext1));
			System.out.println(new String(ciphertext1));
			
			// ECB Decryption
			byte[] plaintext2 = cbcDecrypt(key, iv, ciphertext1);
			
			System.out.println(Encoder.bytesToHexString(plaintext2));		
			System.out.println(new String(plaintext2));
		} 
		catch (GeneralSecurityException e) 
		{
			e.printStackTrace();
		}
 	}
}
