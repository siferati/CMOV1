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
        HttpURLConnection urlConnection = null;
        int responseCode = Constants.NO_INTERNET;

        try {
            String url = "http://" + address + ":" + port + "/users/" + userID + "/vouchers";

            urlConnection = setHeaders("GET", url);

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
                JSONObject voucher = jArray.getJSONObject(i);

                String id = voucher.getString("id");
                boolean available = false;

                if (voucher.isNull("orderId")) {
                    available = true;
                }

                String name = Constants.DISCOUNT;               // default is discount voucher
                double discount = Constants.DEFAULT_DISCOUNT;   // default is discount voucher

                if(voucher.has("promotions")) {
                    JSONArray promotions = voucher.getJSONArray("promotions");

                    if (promotions.length() < 2) {
                        JSONObject promotion = promotions.getJSONObject(0);

                        name = promotion.getString("name");
                        discount = promotion.getDouble("discount");
                    }
                }

                if (available) {
                    Voucher v = new Voucher(id, name, discount);
                    vouchersList.add(v);
                }
            }
        } catch(JSONException e) {
            Log.e("ERROR", e.getMessage());
        }

        return vouchersList;
    }
}
