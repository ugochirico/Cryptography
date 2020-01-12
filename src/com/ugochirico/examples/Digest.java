package com.ugochirico.examples;

import com.ugochirico.crypt.hash.SHA1;
import com.ugochirico.util.Encoder;

public class Digest {
	public static void main(String args[])
	{
		SHA1 sha1 = new SHA1();
		
		byte[] plaintext = "this is a plaintext string".getBytes();
		
		byte[] digest =	sha1.getSHA1Hash(plaintext);
		
		System.out.println(Encoder.bytesToHexString(digest));
	}
}
