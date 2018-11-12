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
import java.util.concurrent.TimeUnit;

public class GetShows extends ServerConnection implements Runnable {
    private ShowsActivity activity;

    private boolean isLastPage = false;

    private int currentPage;
    private int pageSize;

    public GetShows(ShowsActivity activity, int currentPage, int pageSize) {
        this.activity = activity;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    @Override
    public void run() {
        Log.d("scroll", "GET SHOWS " + currentPage + " " + pageSize);
        URL url;
        HttpURLConnection urlConnection = null;

        int responseCode = Constants.NO_INTERNET;

        try {
            String page = currentPage + "";
            String limit = pageSize + "";

            url = new URL("http://" + address + ":" + port + "/shows?page=" + page + "&limit=" + limit);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(Constants.SERVER_TIMEOUT);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);

            urlConnection.connect();

            responseCode = urlConnection.getResponseCode();
            String response;

            if (responseCode == Constants.OK_RESPONSE) {


                response = readStream(urlConnection.getInputStream());
                List<Show> shows = jsonToArray(response);

                Log.d("scroll", "GET SHOWS! " + shows.size());

                activity.showsAdapter.addAll(shows);
                activity.notifyRV();

                Log.d("scroll", "ADAPTER " + activity.showsAdapter.getItemCount());

                activity.showsAdapter.removeFooter();
                activity.handleResponse(responseCode, response);

                if (shows.size() >= pageSize) {
                    activity.showsAdapter.addFooter();
                } else {
                    isLastPage = true;
                }

            } else {
                response = readStream(urlConnection.getErrorStream());
                activity.handleResponse(responseCode, response);
            }
        } catch (Exception e) {
            if (responseCode == Constants.NO_INTERNET) {
                String errorMessage = Constants.ERROR_CONNECTING;

                activity.handleResponse(responseCode, errorMessage);
            }
        } finally {
            if (urlConnection != null)
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
                String description = show.getString("description");
                String date = show.getString("date");
                double price = show.getDouble("price");

                Show s = new Show(id, name, description, date, price);
                shows_list.add(s);
            }
        } catch(JSONException e) {
            Log.e("ERROR", e.getMessage());
        }

        return shows_list;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public int getPageSize() {
        return pageSize;
    }

}
