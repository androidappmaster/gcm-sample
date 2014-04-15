package es.appmaster.gcmmaster.api;

import org.apache.http.NameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Really simple HTTP client
 *
 * @author manolovn
 */
public class RestClient {

    private String BASE_URL = "http://10.10.60.183:9999";

    public RestClient() {

    }

    public void setBaseUrl(String baseUrl) {
        this.BASE_URL = baseUrl;
    }

    public String get(String urlString) throws IOException {

        String response = "";

        URL url = new URL(BASE_URL + urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        int statusCode = connection.getResponseCode();
        switch (statusCode) {

            case 200:
                // ok!
                response = inputStreamToString(connection.getInputStream());
                break;

        }

        connection.disconnect();
        return response;
    }


    public String post(String urlString, List<NameValuePair> params) throws IOException {

        String response = "";

        URL url = new URL(BASE_URL + urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(paramsToQuery(params));
        writer.flush();
        writer.close();
        os.close();

        int statusCode = connection.getResponseCode();
        switch (statusCode) {

            case 200:
                // ok!
                response = inputStreamToString(connection.getInputStream());
                break;

        }

        connection.disconnect();
        return response;
    }



    private String paramsToQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    private String inputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader r = null;
        r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        if (r != null) {
            r.close();
        }
        inputStream.close();
        return total.toString();
    }

}
