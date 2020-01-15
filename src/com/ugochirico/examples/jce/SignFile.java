package com.ugochirico.examples.jce;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;


public class SignFile
{
    static final String KEYSTORE_FILE = "Baeldung.p12";
    static final String KEYSTORE_INSTANCE = "PKCS12";
    static final String KEYSTORE_PWD = "test";
    static final String KEYSTORE_ALIAS = "Key1";

    public static void main(String[] args) throws Exception {

        String text = "This is a message";

        Security.addProvider(new BouncyCastleProvider());

        char[] keystorePassword = "password".toCharArray();
		char[] keyPassword = "password".toCharArray();
		  
		KeyStore keystore = KeyStore.getInstance("PKCS12");
		keystore.load(new FileInputStream("Baeldung.p12"), keystorePassword);
		PrivateKey privKey = (PrivateKey) keystore.getKey("baeldung", keyPassword);
		
        //Build CMS
        X509Certificate cert = (X509Certificate) keystore.getCertificate("baeldung");
        List certList = new ArrayList();
        certList.add(cert);
        Store certs = new JcaCertStore(certList);
        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(privKey);
        gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().build()).build(sha1Signer, cert));
        gen.addCertificates(certs);
        CMSTypedData data = new CMSProcessableByteArray(text.getBytes());
        CMSSignedData sigData = gen.generate(data, true);

        FileOutputStream sigfos = new FileOutputStream("signature.pem");
        sigfos.write(Base64.getEncoder().encode(sigData.toASN1Structure().getEncoded("DER")));
        sigfos.close();
        
        sigfos = new FileOutputStream("signature.p7m");
        sigfos.write(sigData.toASN1Structure().getEncoded("DER"));
        sigfos.close();
    }
}