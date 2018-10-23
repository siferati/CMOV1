package org.feup.cmov.customerapp.database;

import android.util.Log;

import org.feup.cmov.customerapp.LoginActivity;
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
        try {
            url = new URL("http://" + address + ":" + port + "/login");
            urlConnection = (HttpURLConnection) url.openConnection();
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

            int responseCode = urlConnection.getResponseCode();
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
            //activity.handleResponse(0, e.getMessage());
        }
        finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
    }
}
