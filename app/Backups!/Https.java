package com.packagesharer.blash.packagesharer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class Https {
    private static String s;
    private static JSONArray j;
    public static String message = "";

    public static JSONArray go(String[][] Requests){
        try{

        // Load CAs from an InputStream
// (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509"); // Changed from X.509 to JKS
// From https://www.washington.edu/itconnect/security/ca/load-der.crt
        InputStream caInput = new BufferedInputStream(Utility.assetManager.openFd("server.crt").createInputStream());
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
// Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

// Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

// Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), new SecureRandom());


// Tell the URLConnection to use a SocketFactory from our SSLContext
        URL url = new URL("https://13.238.226.98/test.php");
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        urlConnection.setRequestMethod("POST");
        urlConnection.setSSLSocketFactory(context.getSocketFactory());
        urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        urlConnection.setDoOutput(true);
        OutputStream os = urlConnection.getOutputStream();
        StringBuilder PostRequest = new StringBuilder();

        for(String[] req : Requests) {
            PostRequest.append(req[0]).append("=").append(req[1]).append("&");
        }
        PostRequest.setLength(PostRequest.length() - 1);
        os.write(PostRequest.toString().getBytes());
        os.flush();
        os.close();

        int code;
        if((code = urlConnection.getResponseCode()) == HttpsURLConnection.HTTP_OK) {
            InputStream in = urlConnection.getInputStream();
            s = convertInputStreamToString(in);
            if("".equals(s)){
                s = "NULL Response";
            }
        }else{
            s = "Https Error: " + convertInputStreamToString(urlConnection.getInputStream());
        }
        j = new JSONArray(s);


        } catch(JSONException | IOException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException | CertificateException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            s = sw.toString();
        }

        message = s;

        if(j != null)
            return j;
        else
            return null;

}
    private static String convertInputStreamToString(InputStream in) {
        BufferedReader rd = new BufferedReader(new InputStreamReader(in));
        String line;
        StringBuilder sb =  new StringBuilder();
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();

        } catch (IOException e) {
            return null;
        }
        return sb.toString();
    }
}
