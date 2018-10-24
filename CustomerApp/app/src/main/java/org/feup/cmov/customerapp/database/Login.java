package org.feup.cmov.customerapp.database;

import android.util.Log;

import org.feup.cmov.customerapp.login.LoginActivity;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends ServerConnection implements Runnable {
    private LoginActivity activity;
    private String username;
    private String password;

    public Login(LoginActivity activity, String username, String password) {
        super();
        this.activity = activity;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run() {
        URL url;
        HttpURLConnection urlConnection = null;
        int responseCode = -1;

        try {
            url = new URL("http://" + address + ":" + port + "/login");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(2000);

            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);

            urlConnection.connect();

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("username", this.username);
            jsonParam.put("password", this.password);
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(jsonParam.toString());
            out.close();

            responseCode = urlConnection.getResponseCode();
            String response;
            if (responseCode == 200) {
                response = readStream(urlConnection.getInputStream());
                Log.d("connection", response);
            } else {
                response = readStream(urlConnection.getErrorStream());
                Log.d("connection", response);
            }
            activity.handleResponse(responseCode, response);
        }
        catch (Exception e) {
            if (responseCode == -1) {
                String errorMessage = "Error connecting";
                activity.handleResponse(0, errorMessage);
            }
        }
        finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
    }
}
