package org.feup.cmov.customerapp.database;

import android.util.Log;

import org.feup.cmov.customerapp.cafeteria.ShoppingCartActivity;
import org.feup.cmov.customerapp.dataStructures.Order;
import org.feup.cmov.customerapp.dataStructures.Product;
import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.dataStructures.Voucher;
import org.feup.cmov.customerapp.utils.Constants;
import org.feup.cmov.customerapp.utils.MyCrypto;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class MakeOrder extends ServerConnection implements Runnable {

    // main activity
    private ShoppingCartActivity activity;

    // user
    private User user;

    // products list
    private ArrayList<Product> products;

    // vouches list
    private ArrayList<Voucher> vouchers;

    public MakeOrder(ShoppingCartActivity activity, User user, ArrayList<Product> products, ArrayList<Voucher> vouchers) {
        this.activity = activity;
        this.user = user;
        this.products = products;
        this.vouchers = vouchers;
    }

    @Override
    public void run() {
        HttpURLConnection urlConnection = null;
        int responseCode = Constants.NO_INTERNET;

        try {
            String url = "http://" + address + ":" + port + "/users/" + user.getId() + "/orders";
            Log.d("jsonstuff", url);

            urlConnection = setHeaders("POST", url);
            JSONObject orderJson = getOrderJson();
            String signedMessage = MyCrypto.signMessage(this.user.getUsername(), orderJson);
            urlConnection.setRequestProperty("signature", signedMessage);

            urlConnection.connect();

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(orderJson.toString());
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

    private JSONObject getOrderJson() throws JSONException {
        JSONObject orderJson = new JSONObject();
        JSONArray productsJson = new JSONArray();

        for (int i = 0; i < products.size(); i++)
        {
            JSONObject product = new JSONObject();
            product.put("id", products.get(i).getId());
            product.put("quantity", products.get(i).getQuantity());

            productsJson.put(product);
        }

        JSONArray vouchersJson = new JSONArray();
        for(int i = 0; i < vouchers.size(); i++) {
            vouchersJson.put(vouchers.get(i).getId());
        }

        orderJson.put("products", productsJson);
        orderJson.put("vouchers", vouchersJson);

        return orderJson;
    }

    private Order jsonToArray(String jsonString) {
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
