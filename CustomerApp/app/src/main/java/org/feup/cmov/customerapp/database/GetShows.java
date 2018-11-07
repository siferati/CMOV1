package org.feup.cmov.customerapp.database;

import android.util.Log;

import org.feup.cmov.customerapp.userOptions.ShowsActivity;
import org.feup.cmov.customerapp.dataStructures.Show;
import org.feup.cmov.customerapp.utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetShows extends ServerConnection implements Runnable {
    private ShowsActivity activity;
    private List<Show> shows;

    public GetShows(ShowsActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        URL url;
        HttpURLConnection urlConnection = null;
        int responseCode = Constants.NO_INTERNET;

        try {
            url = new URL("http://" + address + ":" + port + "/shows");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(Constants.SERVER_TIMEOUT);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);

            urlConnection.connect();

            responseCode = urlConnection.getResponseCode();
            String response;
            if (responseCode == Constants.OK_RESPONSE) {
                response = readStream(urlConnection.getInputStream());
                Log.d("http", response);

                shows = jsonToArray(response);
                activity.setShows(shows);
            } else {
                response = readStream(urlConnection.getErrorStream());
                Log.d("http", response);
            }
            //activity.handleResponseCC(responseCode, response);
        }
        catch (Exception e) {
            Log.d("http", "ERROR");

            if (responseCode == Constants.NO_INTERNET) {
                String errorMessage = Constants.ERROR_CONNECTING;
                //activity.handleResponse(0, errorMessage);
            }
        }
        finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
    }

    private List<Show> jsonToArray(String jsonString) {
        List<Show> shows_list = new ArrayList<>();

        try {
            JSONArray jArray = new JSONArray(jsonString);

            for(int i=0; i<jArray.length(); i++){
                JSONObject show = jArray.getJSONObject(i);

                int id = show.getInt("id");
                String name = show.getString("name");
                String date = show.getString("date");

                Show s = new Show(id, name, date);
                shows_list.add(s);
            }
        } catch(JSONException e) {
            Log.e("ERROR", e.getMessage());
        }

        return shows_list;
    }
}
