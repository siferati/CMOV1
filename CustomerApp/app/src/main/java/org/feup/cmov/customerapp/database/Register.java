package org.feup.cmov.customerapp.database;

import android.util.Log;
import org.feup.cmov.customerapp.login.RegisterActivity;
import org.feup.cmov.customerapp.utils.Constants;
import org.json.JSONObject;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Register extends ServerConnection implements Runnable {
    private RegisterActivity activity;
    private String username;
    private String password;
    private String name;
    private String nifNumber;
    private String keyN;
    private String keyE;

    public Register(RegisterActivity activity, String username, String password, String name, String nifNumber, String keyN, String keyE) {
        super();
        this.activity = activity;
        this.username = username;
        this.password = password;
        this.name = name;
        this.nifNumber = nifNumber;
        this.keyN = keyN;
        this.keyE = keyE;
    }

    @Override
    public void run() {
        URL url;
        HttpURLConnection urlConnection = null;
        int responseCode = Constants.NO_INTERNET;

        try {
            url = new URL("http://" + address + ":" + port + "/users");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(Constants.SERVER_TIMEOUT);

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
            jsonParam.put("keyN", keyN);
            jsonParam.put("keyE", keyE);

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(jsonParam.toString());
            out.close();

            responseCode = urlConnection.getResponseCode();
            String response;
            if (responseCode == Constants.OK_RESPONSE) {
                response = readStream(urlConnection.getInputStream());
                Log.d("connection", response);
            } else {
                response = readStream(urlConnection.getErrorStream());
                Log.d("connection", response);
            }
            activity.handleResponse(responseCode, response);
        }
        catch (Exception e) {
            if (responseCode == Constants.NO_INTERNET) {
                String errorMessage = Constants.ERROR_CONNECTING;
                activity.handleResponse(responseCode, errorMessage);
            }
        }
        finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
    }


}