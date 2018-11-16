package org.feup.cmov.customerapp.database;

import android.util.Log;

import org.feup.cmov.customerapp.dataStructures.Show;
import org.feup.cmov.customerapp.dataStructures.Voucher;
import org.feup.cmov.customerapp.transactions.TransactionsActivity;
import org.feup.cmov.customerapp.utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetVouchers extends ServerConnection implements Runnable {

    // transactions' activity
    private TransactionsActivity activity;

    // current user's id
    private String userID;

    public GetVouchers(TransactionsActivity activity, String userID) {
        this.activity = activity;
        this.userID = userID;
    }

    @Override
    public void run() {
        URL url;
        HttpURLConnection urlConnection = null;
        int responseCode = Constants.NO_INTERNET;

        try {
            url = new URL("http://" + address + ":" + port + "/users/" + userID + "/vouchers");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(Constants.SERVER_TIMEOUT);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);

            urlConnection.connect();

            responseCode = urlConnection.getResponseCode();
            String response;

            if (responseCode == Constants.OK_RESPONSE) {
                response = readStream(urlConnection.getInputStream());

                List<Voucher> vouchers = jsonToArray(response);
                activity.handleResponseVouchers(responseCode, response, vouchers);
            } else {
                response = readStream(urlConnection.getErrorStream());
                activity.handleResponseVouchers(responseCode, response, null);
            }
        } catch (Exception e) {
            if (responseCode == Constants.NO_INTERNET) {
                String errorMessage = Constants.ERROR_CONNECTING;
                activity.handleResponseVouchers(responseCode, errorMessage, null);
            }
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    private List<Voucher> jsonToArray(String jsonString) {
        List<Voucher> vouchersList = new ArrayList<>();

        try {
            JSONArray jArray = new JSONArray(jsonString);

            for(int i=0; i<jArray.length(); i++){
                JSONObject show = jArray.getJSONObject(i);

                String id = show.getString("id");
                boolean available;
                if (show.has("orderId")) {
                    available = false;
                } else {
                    available = true;
                }

                if (available) {
                    Voucher v = new Voucher(id, available);
                    vouchersList.add(v);
                }
            }
        } catch(JSONException e) {
            Log.e("ERROR", e.getMessage());
        }

        return vouchersList;
    }
}
