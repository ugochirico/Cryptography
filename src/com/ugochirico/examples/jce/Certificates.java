package com.ugochirico.examples.jce;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Certificates {

	public static void main(String arg[])
	{
		try {
			Security.addProvider(new BouncyCastleProvider());
			CertificateFactory certFactory= CertificateFactory
			  .getInstance("X.509");
			  
			X509Certificate certificate = (X509Certificate) certFactory
			  .generateCertificate(new FileInputStream("Baeldung.cer"));
			  
			char[] keystorePassword = "password".toCharArray();
			char[] keyPassword = "password".toCharArray();
			  
			KeyStore keystore = KeyStore.getInstance("PKCS12");
			keystore.load(new FileInputStream("Baeldung.p12"), keystorePassword);
			PrivateKey key = (PrivateKey) keystore.getKey("baeldung", keyPassword);
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
