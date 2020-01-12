package com.ugochirico.examples.jce;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.RSAKeyGenParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import com.ugochirico.util.Encoder;

public class RSA {

	public static void main(String[] args) 
	{	
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		byte[] plaintext = "this is a plaintext string".getBytes();
		
		
		try 
		{
			KeyPair keyPair = generateKeyPair();
			
			System.out.println(Encoder.bytesToHexString(keyPair.getPrivate().getEncoded()));
			System.out.println(Encoder.bytesToHexString(keyPair.getPublic().getEncoded()));			
			
			FileOutputStream fouts = new FileOutputStream("rsa.priv");
			
			fouts.write(keyPair.getPrivate().getEncoded());
			
			fouts.close();
			// encryption/decryption
			
			byte[] ciphertext = encrypt(keyPair.getPublic(), plaintext);
			
			System.out.println(Encoder.bytesToHexString(ciphertext));		
			
			byte[] plaintext1 = decrypt(keyPair.getPrivate(), ciphertext);
			
			System.out.println(Encoder.bytesToHexString(plaintext1));		
			System.out.println(new String(plaintext1));
			
			// digital signature
			byte[] signature = generatePKCS1Signature(keyPair.getPrivate(), plaintext);
						
			System.out.println(Encoder.bytesToHexString(signature));
			
			boolean verified = verifyPKCS1Signature(keyPair.getPublic(), plaintext, signature);
			
			System.out.println("verified: " + verified);
			
		}
		catch (GeneralSecurityException e) 
		{			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		

	}

	public static KeyPair generateKeyPair() throws GeneralSecurityException
	{
		KeyPairGenerator keyPair = KeyPairGenerator.getInstance("RSA");
		keyPair.initialize(new RSAKeyGenParameterSpec(3072, RSAKeyGenParameterSpec.F4));
		return keyPair.generateKeyPair();
	}
	
	public static byte[] encrypt(PublicKey rsaPublic, byte[] input) throws GeneralSecurityException
	{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, rsaPublic);
		
		return cipher.doFinal(input);		
	}
	
	public static byte[] decrypt(PrivateKey rsaPrivate, byte[] ciphertext) throws GeneralSecurityException
	{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, rsaPrivate);
		
		return cipher.doFinal(ciphertext);		
	}
	
	public static byte[] generatePKCS1Signature(PrivateKey rsaPrivate, byte[] input) throws GeneralSecurityException
	{
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(rsaPrivate);
		signature.update(input);
		return signature.sign();
	}
	
	public static boolean verifyPKCS1Signature(PublicKey rsaPublic, byte[] input, byte[] encSignature) throws GeneralSecurityException
	{
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initVerify(rsaPublic);
		signature.update(input);
		return signature.verify(encSignature);
	}
	
	
}

