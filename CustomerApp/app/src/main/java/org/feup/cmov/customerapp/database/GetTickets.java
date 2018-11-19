package org.feup.cmov.customerapp.database;

import android.util.Log;

import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.transactions.TransactionsActivity;
import org.feup.cmov.customerapp.utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class GetTickets extends ServerConnection implements Runnable {

    // transactions' activity
    private TransactionsActivity activity;

    // current user's id
    private String userID;

    public GetTickets(TransactionsActivity activity, String userID) {
        this.activity = activity;
        this.userID = userID;
    }

    @Override
    public void run() {
        HttpURLConnection urlConnection = null;
        int responseCode = Constants.NO_INTERNET;

        try {
            String url = "http://" + address + ":" + port + "/users/" + userID + "/tickets";
            Log.d("responsehttp", url);

            urlConnection = setHeaders("GET", url);

            urlConnection.connect();

            responseCode = urlConnection.getResponseCode();
            String response;

            if (responseCode == Constants.OK_RESPONSE) {
                response = readStream(urlConnection.getInputStream());

                ArrayList<Ticket> tickets = jsonToArray(response);
                activity.handleResponseTickets(responseCode, response, tickets);
            } else {
                response = readStream(urlConnection.getErrorStream());
                activity.handleResponseTickets(responseCode, response, null);
            }
        } catch (Exception e) {
            if (responseCode == Constants.NO_INTERNET) {
                String errorMessage = Constants.ERROR_CONNECTING;
                activity.handleResponseTickets(responseCode, errorMessage, null);
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

                if (available == 0) // if not available
                {
                    Ticket t = new Ticket(id, showId, name, date, seatNumber, price);
                    ticketList.add(t);
                }
            }
        } catch(JSONException e) {
            Log.e("ERROR", e.getMessage());
        }

        return ticketList;
    }
}
