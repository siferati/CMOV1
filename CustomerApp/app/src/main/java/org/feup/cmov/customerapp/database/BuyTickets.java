package org.feup.cmov.customerapp.database;

import android.util.Log;

import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.dataStructures.Voucher;
import org.feup.cmov.customerapp.shows.ShowActivity;
import org.feup.cmov.customerapp.utils.Constants;
import org.feup.cmov.customerapp.utils.MyCrypto;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BuyTickets extends ServerConnection implements Runnable {

    public class ServerResponse {
        public ArrayList<Ticket> tickets;
        public ArrayList<Voucher> vouchers;

        public ServerResponse() {}
    }

    // shows' activity
    private ShowActivity activity;

    // id of show to get tickets from
    private int showID;

    // id of user buy tickets
    private User user;

    // quantity of tickets to buy
    private int quantity;

    public BuyTickets(ShowActivity activity, int showID, User user, int quantity) {
        this.activity = activity;
        this.showID = showID;
        this.user = user;
        this.quantity = quantity;
    }

    @Override
    public void run() {
        URL url;
        HttpURLConnection urlConnection = null;

        int responseCode = Constants.NO_INTERNET;

        try {
            url = new URL("http://" + address + ":" + port + "/shows/" + showID + "/tickets");
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setConnectTimeout(Constants.SERVER_TIMEOUT);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("POST");

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("userId", this.user.getId());
            jsonParam.put("quantity", this.quantity);

            String signedMessage = MyCrypto.signMessage(this.user.getUsername(), jsonParam);
            urlConnection.setRequestProperty("signature", signedMessage);

            urlConnection.connect();

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(jsonParam.toString());
            out.close();

            responseCode = urlConnection.getResponseCode();
            String response;

            if (responseCode == Constants.OK_RESPONSE) {
                response = readStream(urlConnection.getInputStream());

                // get new tickets from server
                ServerResponse serverResponse = jsonToArray(response);

                // notifies activity that loading finished
                activity.handleResponse(responseCode, response, serverResponse.tickets, serverResponse.vouchers);

            } else {
                response = readStream(urlConnection.getErrorStream());
                activity.handleResponse(responseCode, response, null, null);
            }
        } catch (Exception e) {
            if (responseCode == Constants.NO_INTERNET) {
                String errorMessage = Constants.ERROR_CONNECTING;
                activity.handleResponse(responseCode, errorMessage, null, null);
            }
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

    }

    private ServerResponse jsonToArray(String jsonString) {
        ArrayList<Ticket> tickets_list = new ArrayList<>();
        ArrayList<Voucher> vouchers_list = new ArrayList<>();

        try {
            JSONObject response = new JSONObject(jsonString);
            JSONArray tickets = response.getJSONArray("tickets");

            for(int i = 0; i < tickets.length(); i++){
                JSONObject ticket = tickets.getJSONObject(i);

                String id = ticket.getString("id");
                String name = ticket.getString("name");
                String date = ticket.getString("date");
                int seatNumber = ticket.getInt("seatNumber");
                double price = ticket.getDouble("price");

                Ticket t = new Ticket(id, name, date, seatNumber, price);
                tickets_list.add(t);
            }

            JSONArray vouchers = response.getJSONArray("vouchers");

            for(int i = 0; i < vouchers.length(); i++) {
                JSONObject voucher = vouchers.getJSONObject(i);

                String id = voucher.getString("id");
                String name = voucher.getString("name");
                double discount = voucher.getDouble("discount");

                Voucher v = new Voucher(id, name, discount);
                vouchers_list.add(v);
            }
        } catch(JSONException e) {
            Log.e("ERROR", e.getMessage());
        }

        ServerResponse serverResponse = new ServerResponse();
        serverResponse.tickets = tickets_list;
        serverResponse.vouchers = vouchers_list;

        return serverResponse;
    }
}
