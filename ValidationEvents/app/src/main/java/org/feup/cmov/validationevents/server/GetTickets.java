package org.feup.cmov.validationevents.server;

import android.app.Activity;
import android.util.Log;

import org.feup.cmov.validationevents.shows.TicketsActivity;
import org.feup.cmov.validationevents.utils.Constants;
import org.feup.cmov.validationevents.dataStructures.Ticket;
import org.feup.cmov.validationevents.shows.ShowsActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetTickets extends ServerConnection implements Runnable {

    // tickets activity
    private Activity activity;

    // current user's id
    private String userID;

    public GetTickets(Activity activity, String userID) {
        this.activity = activity;
        this.userID = userID;
    }

    @Override
    public void run() {
        HttpURLConnection urlConnection = null;
        int responseCode = Constants.NO_INTERNET;

        try {
            String serverURL = "http://" + address + ":" + port + "/users/" + userID + "/tickets";

            URL url = new URL(serverURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(Constants.SERVER_TIMEOUT);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);

            urlConnection.connect();

            responseCode = urlConnection.getResponseCode();
            String response;

            if (responseCode == Constants.OK_RESPONSE) {
                response = readStream(urlConnection.getInputStream());

                ArrayList<Ticket> tickets = jsonToArray(response);
                if (activity instanceof ShowsActivity) {
                    ShowsActivity act = (ShowsActivity)activity;
                    act.handleResponseTickets(responseCode, response, tickets);
                } else {
                    TicketsActivity act = (TicketsActivity)activity;
                    act.handleResponseTickets(responseCode, response, tickets);
                }
            } else {
                response = readStream(urlConnection.getErrorStream());
                if (activity instanceof ShowsActivity) {
                    ShowsActivity act = (ShowsActivity)activity;
                    act.handleResponseTickets(responseCode, response, null);
                } else {
                    TicketsActivity act = (TicketsActivity)activity;
                    act.handleResponseTickets(responseCode, response, null);
                }
            }
        } catch (Exception e) {
            if (responseCode == Constants.NO_INTERNET) {
                String errorMessage = Constants.ERROR_CONNECTING;
                if (activity instanceof ShowsActivity) {
                    ShowsActivity act = (ShowsActivity)activity;
                    act.handleResponseTickets(responseCode, errorMessage, null);
                } else {
                    TicketsActivity act = (TicketsActivity)activity;
                    act.handleResponseTickets(responseCode, errorMessage, null);
                }
            }
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    private ArrayList<Ticket> jsonToArray(String jsonString) {
        ArrayList<Ticket> ticketList = new ArrayList<>();

        try {
            JSONArray jArray = new JSONArray(jsonString);

            for(int i=0; i<jArray.length(); i++){
                JSONObject ticket = jArray.getJSONObject(i);

                String id = ticket.getString("id");
                int showId = ticket.getInt("showId");
                int available = ticket.getInt("available");
                String name = ticket.getString("name");
                String date = ticket.getString("date");
                int seatNumber = ticket.getInt("seatNumber");
                double price = ticket.getDouble("price");


                Ticket t = new Ticket(id, showId, name, date, seatNumber, price);
                if (available == 0) {
                    t.setAvailable(false);
                } else t.setAvailable(true);

                ticketList.add(t);
            }
        } catch(JSONException e) {
            Log.e("ERROR", e.getMessage());
        }

        return ticketList;
    }
}
