package gnotus.inoveplastika;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class HttpHandler {


    private static final String TAG = HttpHandler.class.getSimpleName();
    private static final String basedados = "";


    public HttpHandler() {
    }

    public String makeServiceCall(String reqUrl) {
        String response = null;

        try {
            URL url = new URL(reqUrl);

            System.out.println("URL: " + reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // conn.setRequestProperty("Base-Dados", basedados);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(60000);
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            //URLConnection conn = url.openConnection();
            //BufferedReader a = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            //InputStream resposta = conn.getInputStream();

            response = convertStreamToString(in);

            conn.disconnect();

        } catch (SocketTimeoutException e) {

        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());

        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

        return response;


    }

    public String makeServiceCallPost(String reqUrl, String json) {
        String response = null;

        try {
            URL url = new URL(reqUrl);
            byte[] postData       = json.getBytes( StandardCharsets.UTF_8 );
            System.out.println("URL: " + reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Base-Dados", basedados);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(60000);
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            //URLConnection conn = url.openConnection();
            //BufferedReader a = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            //InputStream resposta = conn.getInputStream();
            conn.setDoInput(true);
            conn.setDoOutput(true);

            try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
                wr.write( postData );
            }

            response = convertStreamToString(in);

            conn.disconnect();

        } catch (SocketTimeoutException e) {

        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());

        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

        return response;


    }

    private String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}