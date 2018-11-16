package org.feup.cmov.customerapp.database;

import android.app.Activity;
import android.util.Log;

import org.feup.cmov.customerapp.cafeteria.CafeteriaActivity;
import org.feup.cmov.customerapp.cafeteria.ShoppingCartActivity;
import org.feup.cmov.customerapp.dataStructures.Product;
import org.feup.cmov.customerapp.utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetProducts  extends ServerConnection implements Runnable {

    // products' activity
    private Activity activity;

    public GetProducts(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {

        URL url;
        HttpURLConnection urlConnection = null;

        int responseCode = Constants.NO_INTERNET;

        try {
            url = new URL("http://" + address + ":" + port + "/products");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(Constants.SERVER_TIMEOUT);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);

            urlConnection.connect();

            responseCode = urlConnection.getResponseCode();
            String response;

            if (responseCode == Constants.OK_RESPONSE) {
                response = readStream(urlConnection.getInputStream());

                // get products from server
                List<Product> products = jsonToArray(response);

                if (activity instanceof CafeteriaActivity) {
                    CafeteriaActivity act = (CafeteriaActivity) activity;
                    act.handleResponse(responseCode, response, products);
                } else {
                    ShoppingCartActivity act = (ShoppingCartActivity) activity;
                    act.handleResponse(responseCode, response, products);
                }
            } else {
                response = readStream(urlConnection.getErrorStream());

                if (activity instanceof CafeteriaActivity) {
                    CafeteriaActivity act = (CafeteriaActivity) activity;
                    act.handleResponse(responseCode, response, null);
                } else {
                    ShoppingCartActivity act = (ShoppingCartActivity) activity;
                    act.handleResponse(responseCode, response, null);
                }
            }

        } catch (Exception e) {
            if (responseCode == Constants.NO_INTERNET) {
                String errorMessage = Constants.ERROR_CONNECTING;

                if (activity instanceof CafeteriaActivity) {
                    CafeteriaActivity act = (CafeteriaActivity) activity;
                    act.handleResponse(responseCode, errorMessage, null);
                } else {
                    ShoppingCartActivity act = (ShoppingCartActivity) activity;
                    act.handleResponse(responseCode, errorMessage, null);
                }
            }
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }


    private List<Product> jsonToArray(String jsonString) {
        List<Product> products_list = new ArrayList<>();

        try {
            JSONArray jArray = new JSONArray(jsonString);

            for(int i=0; i<jArray.length(); i++){
                JSONObject product = jArray.getJSONObject(i);

                int id = product.getInt("id");
                String name = product.getString("name");
                double price = product.getDouble("price");
                String image = product.getString("image");

                Product p = new Product(id, name, price, image);
                products_list.add(p);
            }
        } catch(JSONException e) {
            Log.e("ERROR", e.getMessage());
        }

        return products_list;
    }
}
