package org.feup.cmov.validationcafeteria.server;

import android.util.Log;

import org.feup.cmov.validationcafeteria.util.Constants;
import org.feup.cmov.validationcafeteria.dataStructures.Order;
import org.feup.cmov.validationcafeteria.dataStructures.Voucher;
import org.feup.cmov.validationcafeteria.order.OrderActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MakeOrder extends ServerConnection implements Runnable {

    // order activity
    private OrderActivity activity;

    // user id
    private String userId;

    // signed message
    private String signedMessage;

    // message to send to server
    private String jsonString;

    public MakeOrder(OrderActivity activity, String userId, String signedMessage, String jsonString) {
        this.activity = activity;
        this.userId = userId;
        this.signedMessage = signedMessage;
        this.jsonString = jsonString;
    }

    @Override
    public void run() {
        HttpURLConnection urlConnection = null;
        int responseCode = Constants.NO_INTERNET;

        try {
            String serverUrl = "http://" + address + ":" + port + "/users/" + userId + "/orders";

            URL url = new URL(serverUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(Constants.SERVER_TIMEOUT);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("signature", signedMessage);

            urlConnection.connect();

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(jsonString);
            out.close();

            responseCode = urlConnection.getResponseCode();
            String response;

            if (responseCode == Constants.OK_RESPONSE) {
                response = readStream(urlConnection.getInputStream());

                Order order = jsonToArray(response);
                activity.handleResponseOrder(responseCode, response, order);
            } else {
                response = readStream(urlConnection.getErrorStream());
                activity.handleResponseOrder(responseCode, response, null);
            }


        } catch (Exception e) {
            if (responseCode == Constants.NO_INTERNET) {
                e.printStackTrace();

                String errorMessage = Constants.ERROR_CONNECTING;

                activity.handleResponseOrder(responseCode, errorMessage, null);
            }
        }
        finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
    }

    private Order jsonToArray(String jsonString) throws JSONException {
        Order order = new Order();
        ArrayList<Voucher> vouchers_list = new ArrayList<>();

        try {
            JSONObject response = new JSONObject(jsonString);

            int orderId = response.getInt("id");
            double price = response.getDouble("price");

            JSONArray vouchers = response.getJSONArray("usedVoucherIds");

            for(int i = 0; i < vouchers.length(); i++) {
                Voucher v = new Voucher(vouchers.get(i).toString());
                vouchers_list.add(v);
            }

            order = new Order(orderId, price, vouchers_list);

        } catch(JSONException e) {
            Log.e("ERROR", e.getMessage());
        }

        return order;
    }
}
