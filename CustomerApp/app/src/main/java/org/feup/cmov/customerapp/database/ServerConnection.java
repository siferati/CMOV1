package org.feup.cmov.customerapp.database;

import org.feup.cmov.customerapp.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerConnection {
    protected final String address = "10.0.2.2";
    protected final int port = 8080;

    protected String readStream(InputStream in) {
        BufferedReader reader = null;
        String line;
        StringBuilder response = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        catch (IOException e) {
            return e.getMessage();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    response = new StringBuilder(e.getMessage());
                }
            }
        }
        return response.toString();
    }

    protected HttpURLConnection setHeaders(String verb, String serverURL) throws IOException {
        URL url = new URL(serverURL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(Constants.SERVER_TIMEOUT);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setUseCaches(false);

        if (verb.equals("POST")) {
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
        }

        return urlConnection;
    }
}
