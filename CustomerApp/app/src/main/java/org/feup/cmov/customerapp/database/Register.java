package org.feup.cmov.customerapp.database;

import android.app.Activity;
import android.util.Log;

import org.feup.cmov.customerapp.RegisterActivity;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Register extends ServerConnection implements Runnable {
    private RegisterActivity activity = null;
    private String username = null;
    private String password = null;
    private String name = null;
    private String nifNumber = null;

    public Register(RegisterActivity activity, String username, String password, String name, String nifNumber) {
        super();
        this.activity = activity;
        this.username = username;
        this.password = password;
        this.name = name;
        this.nifNumber = nifNumber;
    }
    @Override
    public void run() {
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL("http://" + address + ":" + port + "/users");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);

            urlConnection.connect();

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("name", this.name);
            jsonParam.put("username", this.username);
            jsonParam.put("password", this.password);
            jsonParam.put("nif", this.nifNumber);
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
            activity.handleResponse(0, e.getMessage());
        }
        finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
    }


}