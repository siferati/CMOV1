package org.feup.cmov.customerapp.database;

import android.util.Log;

import org.feup.cmov.customerapp.shows.ShowsActivity;
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

    // shows' activity
    private ShowsActivity activity;

    // checks if we got the last page of shows from server
    private boolean isLastPage = false;

    // shows' current page
    private int currentPage;

    // shows' page limit
    private int pageSize;

    public GetShows(ShowsActivity activity, int currentPage, int pageSize) {
        this.activity = activity;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    @Override
    public void run() {
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

                // get new shows from server
                List<Show> shows = jsonToArray(response);

                // add new shows to adapter
                activity.showsAdapter.addAll(shows);

                // notify adapter that new shows have been added
                activity.notifyShowsAdapter();

                // removes footer because loading finished
                activity.showsAdapter.removeFooter();

                // notifies activity that loading finished
                activity.handleResponse(responseCode, response);

                // if the amount of shows we get from the server is equal to the limit...
                if (shows.size() >= pageSize) {
                    // then there is more shows to load
                    activity.showsAdapter.addFooter();
                } else {
                    // otherwise we've reached the end
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

}
