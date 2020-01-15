package com.ugochirico.examples;
/*
 *  perform HTTPS GET on SSL client verification using Java PKCS#11
 * 
 *  requirement:
 *   * PKCS#11 enable security token
 *   * the token has stored cert and key for ssl client verification,and ca cert
     * PKCS#11 library(.dll or .so) for the token
 * 
 *  usage:
 *   java -cp . -D java.security.debug=sunpkcs11 HTTPS https://www.example.com/
 */
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyStoreBuilderParameters;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class HTTPS {

    private final SSLSocketFactory factory;
    private static Provider p = null;
    
    private String pin;
    public HTTPS(String pin, String pkcs11Path) throws IOException, GeneralSecurityException {
    	
    	this.pin = pin;
    	
    	System.out.println("HTTPS");
    	
//    	factory = buildSocketFactory();
    	
		KeyStore.Builder builder = createKeyStoreBuilder(pkcs11Path);
		
        final SSLContext ctx;
        ctx = SSLContext.getInstance("TLS");        
        ctx.init(createKeyManagers(builder), new TrustManager[] { new BlindTrustManager() }, null);
        factory = ctx.getSocketFactory();
        SSLContext.setDefault(ctx);
    }

    public byte[] sendHttpsRequest(String urlString, byte[] content) throws HTTPException, IOException {
        HttpsURLConnection con;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            URL url = new URL(urlString);
            con = (HttpsURLConnection) url.openConnection();
            String protocol = url.getProtocol();
            if (protocol.equals("https")) {
                ((HttpsURLConnection) con).setSSLSocketFactory(factory);
            } else if (protocol.equals("http")) {
                // do nothing 
            } else {
                throw new IOException("bad protocol");
            }
            con.setInstanceFollowRedirects(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("SOAP", "Action");            
            con.setRequestProperty("SOAPAction", urlString);
            con.setRequestProperty("KeepAlive", "true");
            //con.setRequestProperty("X-WASP-User", "GRNCSC75P11F799Q");
            con.setRequestProperty("Content-Type", "text/xml;charset=\"utf-8\"");
            con.setRequestProperty("Accept","text/xml");
            
            con.setHostnameVerifier(new HostnameVerifier() {				
                @Override
                public boolean verify(String hostname, SSLSession sslSession) {
                    return true;
                }
            });
            
            if(content != null)
            {
            	con.setDoOutput(true);
            	OutputStream outs = con.getOutputStream();            	
            	outs.write(content);
            	outs.flush();
            }            
                     
            int respCode;
            System.out.println("RESP code = " + (respCode = con.getResponseCode()));
            //System.out.println("RESPONSE = \n" + out);
            if(respCode > HttpURLConnection.HTTP_BAD_REQUEST)
            {
                con.disconnect();
                throw new HTTPException(respCode);
                //return null;
            }
            
            BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
            int length;
            while ((length = bis.read()) != -1) {
                out.write(length);
            }
            out.close();
            
            con.disconnect();
        	return out.toByteArray();
        }
    }
    
	private KeyStore.Builder createKeyStoreBuilder(String pkcs11Path) throws IOException, GeneralSecurityException {
        /*
         *   ---- pkcs11.cfg
         *   name=test
         *   library=C:\FULLPATH\yourpkcs11.dll
         *   slot=1
         *   ----
         *
         *   see docs.oracle.com/javase/7/docs/technotes/guides/security/p11guide.html
         */

        String cnfg = "name=namirial\nlibrary=" + pkcs11Path + "\n";//c:\\windows\\system32\\bit4xpki.dll\n";
 
        System.out.println("cnfg " + cnfg);
        
        //use ByteArrayInputStream to get the bytes of the String and convert them to InputStream.
        InputStream ins = new ByteArrayInputStream(cnfg.getBytes(Charset.forName("UTF-8")));
        
        //InputStream ins = getClass().getResourceAsStream(propPath);
        
        if(p == null)
        {
        	p = new sun.security.pkcs11.SunPKCS11(ins);
        	Security.removeProvider("IAIK");
        	Security.addProvider(p);
        }
        
        KeyStore.Builder builder = KeyStore.Builder.newInstance("PKCS11", p, new KeyStore.CallbackHandlerProtection(new MyCallbackHandler()));
		return builder;
	}

    private KeyManager[] createKeyManagers(KeyStore.Builder builder) throws IOException, GeneralSecurityException {
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("NewSunX509");
        kmf.init(new KeyStoreBuilderParameters(builder));
        return kmf.getKeyManagers();
    }

    private TrustManager[] createCAFileTrustManagers(KeyStore.Builder builder) throws GeneralSecurityException, FileNotFoundException, IOException 
    {
    	// Create trust manager
//        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
//        trustManagerFactory.init(keyStore);
//        TrustManager[] tm = trustManagerFactory.getTrustManagers();

        
        KeyStore keystore = builder.getKeyStore();
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keystore);
        return trustManagerFactory.getTrustManagers();
    }

    private class MyCallbackHandler implements CallbackHandler {

        @Override
        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (Callback cb : callbacks) {
                if (cb instanceof PasswordCallback) {
                    PasswordCallback pcb = (PasswordCallback) cb;
                    
                    if(pin == null)
                    {
	                    JPasswordField pf = new JPasswordField();
	                    Object[] message = {"pin", pf};
	                    int resp = JOptionPane.showConfirmDialog(null, message, "Inserisci il pin della smart card", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
	                    if (resp == JOptionPane.OK_OPTION) {
	                        pcb.setPassword(pf.getPassword());
	                    }
                    }
                    else
                    {
                    	pcb.setPassword(pin.toCharArray());
                    }
                } else {
                    throw new UnsupportedCallbackException(callbacks[0]);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        HTTPS p11 = new HTTPS(null, "c:\\windows\\system32\\bit4xpki.dll\n");
        p11.sendHttpsRequest(args[0], null);
    }
        
    private SSLSocketFactory buildSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(null, new TrustManager[] { new BlindTrustManager() }, null);
		return ctx.getSocketFactory();
        }
    
    public static class BlindTrustManager implements X509TrustManager {

    	public X509Certificate[] getAcceptedIssuers() {
    		return null;
    	}

    	public void checkClientTrusted(X509Certificate[] chain, String authType)
    			throws CertificateException {

    	}

    	public void checkServerTrusted(X509Certificate[] chain, String authType)
    			throws CertificateException {
    		

    	}
    }
    
    public static class HTTPException extends Exception
    {
    	public int errorCode;
    	
    	public HTTPException(int errorCode)
    	{
    		super("HTTP Error: " + errorCode);
    		this.errorCode = errorCode;
    	}
    	
    	
    }
    

}