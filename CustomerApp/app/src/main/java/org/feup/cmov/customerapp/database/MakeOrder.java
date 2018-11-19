package org.feup.cmov.customerapp.database;

import android.util.Log;

import org.feup.cmov.customerapp.cafeteria.ShoppingCartActivity;
import org.feup.cmov.customerapp.dataStructures.Product;
import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.dataStructures.Voucher;
import org.feup.cmov.customerapp.utils.MyCrypto;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MakeOrder extends ServerConnection {

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

    public String getSignedOrder() {
        JSONObject orderJson = new JSONObject();
        String signedMessage;
        try {
            orderJson = getOrderJson();
            signedMessage = MyCrypto.signMessage(this.user.getUsername(), orderJson);

            orderJson.put("signature", signedMessage);
            orderJson.put("userId", user.getId());

            JSONArray vouchersJson = new JSONArray();

            for(int i = 0; i < vouchers.size(); i++) {
                JSONObject voucher = new JSONObject();

                voucher.put("id", vouchers.get(i).getId());
                voucher.put("type", vouchers.get(i).getType());
                voucher.put("discount", vouchers.get(i).getDiscount());

                vouchersJson.put(voucher);
            }

            orderJson.put("voucherInfo", vouchersJson);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orderJson.toString();
    }

    private JSONObject getOrderJson() throws JSONException {
        JSONObject orderJson = new JSONObject();
        JSONArray productsJson = new JSONArray();

        for (int i = 0; i < products.size(); i++)
        {
            JSONObject product = new JSONObject();
            product.put("id", products.get(i).getId());
            product.put("name", products.get(i).getName());
            product.put("quantity", products.get(i).getQuantity());
            product.put("image", products.get(i).getImage());
            product.put("price", products.get(i).getPrice());

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

}
