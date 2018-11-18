package org.feup.cmov.customerapp.database;

import android.util.Log;

import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.shows.tickets.TicketValidationActivity;
import org.feup.cmov.customerapp.utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class ValidateTickets extends ServerConnection implements Runnable {

    public class ServerResponse {
        public ArrayList<Ticket> validTickets;
        public ArrayList<Ticket> invalidTickets;

        public ServerResponse() {}
    }

    private TicketValidationActivity activity;

    private int showId;

    private String userId;

    private ArrayList<Ticket> tickets;

    public ValidateTickets(TicketValidationActivity activity, int showId, String userId, ArrayList<Ticket> tickets) {
        this.activity = activity;
        this.showId = showId;
        this.userId = userId;
        this.tickets = tickets;
    }

    @Override
    public void run() {
        HttpURLConnection urlConnection = null;
        int responseCode = Constants.NO_INTERNET;

        try {
            String url = "http://" + address + ":" + port + "/shows/" + showId + "/tickets/validation";
            urlConnection = setHeaders("POST", url);

            JSONObject validationJson = getValidationJson();

            urlConnection.connect();

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(validationJson.toString());
            out.close();

            responseCode = urlConnection.getResponseCode();
            String response;

            if (responseCode == Constants.OK_RESPONSE) {
                response = readStream(urlConnection.getInputStream());

                Log.d("connection", response);

                ServerResponse serverResponse = jsonToArray(response);

            } else {
                response = readStream(urlConnection.getErrorStream());
                ServerResponse serverResponse = jsonToArray(response);

                Log.d("connection", response);

            }



        } catch (Exception e) {
            if (responseCode == Constants.NO_INTERNET) {
                e.printStackTrace();

                String errorMessage = Constants.ERROR_CONNECTING;
                // activity.handleResponseOrder(responseCode, errorMessage, null);
            }
        }
        finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
    }

    private JSONObject getValidationJson() throws JSONException {
        JSONObject validationJson = new JSONObject();

        validationJson.put("userId", userId);

        JSONArray ticketsJson = new JSONArray();
        for(int i = 0; i < tickets.size(); i++) {
            ticketsJson.put(tickets.get(i).getId());
        }

        validationJson.put("tickets", ticketsJson);

        return validationJson;
    }

    private ServerResponse jsonToArray(String jsonString) {

        ServerResponse serverResponse = new ServerResponse();
        ArrayList<Ticket> validTickets = new ArrayList<>();
        ArrayList<Ticket> invalidTickets = new ArrayList<>();

        try {
            JSONObject response = new JSONObject(jsonString);
            JSONArray valid = response.getJSONArray("valid");

            for(int i = 0; i < valid.length(); i++) {
                Ticket t = new Ticket(valid.get(i).toString());
                validTickets.add(t);
            }

            JSONArray invalid = response.getJSONArray("invalid");

            for(int i = 0; i < invalid.length(); i++) {
                Ticket t = new Ticket(invalid.get(i).toString());
                invalidTickets.add(t);
            }

            serverResponse.validTickets = validTickets;
            serverResponse.invalidTickets = validTickets;

        } catch(JSONException e) {
            Log.e("ERROR", e.getMessage());
        }

        return serverResponse;
    }
}
