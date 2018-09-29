package com.packagesharer.blash.packagesharer;
import android.os.AsyncTask;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class Http extends AsyncTask<String, Void, Void> {
    private static final String TAG = "Http";

    public static String s = "";
    // This is the JSON body of the post
    JSONObject postData;
    // This is a constructor that allows you to pass in the JSON body
    Http(Map<String, String> postData) {
        if (postData != null) {
            this.postData = new JSONObject(postData);
        }
    }

    // This is a function that we are overriding from AsyncTask. It takes Strings as parameters because that is what we defined for the parameters of our async task
    @Override
    protected Void doInBackground(String... params) {
        return null;
    }
//
//    private void copyInputStreamToOutputStream(InputStream in, PrintStream out) {
//        try {
//            IOUtils.copyStream(in,out);
//            in.close();
//            out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//   }



}
