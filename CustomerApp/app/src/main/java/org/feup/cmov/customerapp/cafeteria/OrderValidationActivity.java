package org.feup.cmov.customerapp.cafeteria;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Order;
import org.feup.cmov.customerapp.dataStructures.Product;
import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.dataStructures.Voucher;
import org.feup.cmov.customerapp.utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderValidationActivity extends AppCompatActivity {

    // tickets to validate
    ArrayList<Product> products = new ArrayList<>();

    // vouchers to validate
    ArrayList<Voucher> vouchers = new ArrayList<>();

    // order
    Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_validation);

        String orderJson = getOrderJson();
        Log.d("jsonstuff", orderJson);

        ImageView qr_code = findViewById(R.id.qrCodeCafeteria);
    }


    public String getOrderJson() {
        Bundle argument = getIntent().getExtras();

        if (argument != null) {
            order = (Order) argument.getSerializable(Constants.ORDER_VALIDATION);
        }

        products = order.getProducts();
        vouchers = order.getVouchers();

        User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, getApplicationContext());
        JSONObject orderJson = new JSONObject();

        try {
            orderJson.put("orderid", order.getId());
            orderJson.put("userid", user.getId());
            orderJson.put("price", order.getPrice());

            JSONArray productsJson = new JSONArray();
            for (int i = 0; i < products.size(); i++)
            {
                JSONObject product = new JSONObject();

                product.put("id", products.get(i).getId());
                product.put("name", products.get(i).getName());
                product.put("quantity", products.get(i).getQuantity());
                product.put("image", products.get(i).getImage());
                product.put("price", products.get(i).getTotalPriceRounded());

                productsJson.put(product);
            }

            JSONArray vouchersJson = new JSONArray();
            for(int i = 0; i < vouchers.size(); i++) {
                JSONObject voucher = new JSONObject();

                voucher.put("id", vouchers.get(i).getId());
                voucher.put("type", vouchers.get(i).getType());
                voucher.put("discount", vouchers.get(i).getDiscount());

                vouchersJson.put(voucher);
            }

            orderJson.put("products", productsJson);
            orderJson.put("vouchers", vouchersJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return orderJson.toString();
    }
}
