package com.ugochirico.examples.jce;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Security;

import com.ugochirico.util.Encoder;

public class Digest {

	public static void main(String[] args) {

		try 
		{
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			
			byte[] plaintext = "this is a plaintext string".getBytes();
			
			byte[] digest =	calculateSHA256(plaintext);
			
			System.out.println(Encoder.bytesToHexString(digest));
			
			byte[] digest1 = calculateSHA1Digest(plaintext);
			
			System.out.println(Encoder.bytesToHexString(digest1));
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	public static byte[] calculateSHA256(byte[] data) throws GeneralSecurityException
	{
		MessageDigest hash = MessageDigest.getInstance("SHA256");
		return hash.digest(data);
	}
	
	public static byte[] calculateSHA1Digest(byte[] data) throws GeneralSecurityException
	{
		MessageDigest hash = MessageDigest.getInstance("SHA1");
		return hash.digest(data);
	}
}
