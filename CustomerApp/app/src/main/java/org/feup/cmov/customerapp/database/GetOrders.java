package org.feup.cmov.customerapp.database;

import android.util.Log;

import org.feup.cmov.customerapp.dataStructures.Order;
import org.feup.cmov.customerapp.dataStructures.Product;
import org.feup.cmov.customerapp.dataStructures.Voucher;
import org.feup.cmov.customerapp.transactions.TransactionsActivity;
import org.feup.cmov.customerapp.utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class GetOrders extends ServerConnection implements Runnable {

    // transactions' activity
    private TransactionsActivity activity;

    // current user's id
    private String userID;

    public GetOrders(TransactionsActivity activity, String userID) {
        this.activity = activity;
        this.userID = userID;
    }

    @Override
    public void run() {
        HttpURLConnection urlConnection = null;
        int responseCode = Constants.NO_INTERNET;

        try {
            String url = "http://" + address + ":" + port + "/users/" + userID + "/orders";
            Log.d("responsehttp", url);

            urlConnection = setHeaders("GET", url);

            urlConnection.connect();

            responseCode = urlConnection.getResponseCode();
            String response;

            if (responseCode == Constants.OK_RESPONSE) {
                response = readStream(urlConnection.getInputStream());
                Log.d("jsonstuff", response);

                ArrayList<Order> orders = jsonToArray(response);
                activity.handleResponseOrders(responseCode, response, orders);
            } else {
                response = readStream(urlConnection.getErrorStream());
                activity.handleResponseOrders(responseCode, response, null);
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

    private ArrayList<Order> jsonToArray(String jsonString) {

        ArrayList<Order> orders = new ArrayList<>();

        try {
            JSONArray jArray = new JSONArray(jsonString);

            for(int i=0; i<jArray.length(); i++) {
                JSONObject orderJson = jArray.getJSONObject(i);

                ArrayList<Voucher> vouchers_list = new ArrayList<>();
                ArrayList<Product> products_list = new ArrayList<>();

                int orderId = orderJson.getInt("id");
                String date = orderJson.getString("date");
                double price = orderJson.getDouble("price");

                JSONArray products = orderJson.getJSONArray("products");

                for (int j = 0; j < products.length(); j++) {
                    JSONObject product = products.getJSONObject(j);

                    int id = product.getInt("id");
                    String name = product.getString("name");
                    int quantity = product.getInt("quantity");
                    String image = product.getString("image");
                    double priceProduct = product.getDouble("price");

                    Product p = new Product(id, quantity, priceProduct, name, image);
                    products_list.add(p);
                }

                JSONArray vouchers = orderJson.getJSONArray("vouchers");

                for (int j = 0; j < vouchers.length(); j++) {
                    Voucher v = new Voucher(vouchers.get(j).toString());
                    vouchers_list.add(v);
                }

                Order order = new Order(orderId, date, price, products_list, vouchers_list);
                Log.d("jsonstuff", order.getId() + " " + order.getDate() + " " + order.getPrice());

                orders.add(order);
            }

        } catch(JSONException e) {
            Log.e("ERROR", e.getMessage());
        }

        return orders;
    }
}
