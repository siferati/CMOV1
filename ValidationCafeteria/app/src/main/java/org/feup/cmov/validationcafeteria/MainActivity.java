package org.feup.cmov.validationcafeteria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.feup.cmov.validationcafeteria.dataStructures.Order;
import org.feup.cmov.validationcafeteria.dataStructures.Product;
import org.feup.cmov.validationcafeteria.dataStructures.Voucher;
import org.feup.cmov.validationcafeteria.order.OrderActivity;
import org.feup.cmov.validationcafeteria.util.Constants;
import org.feup.cmov.validationcafeteria.util.MyQRCode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    // products to validate
    ArrayList<Product> products = new ArrayList<>();

    // accepted vouchers
    ArrayList<Voucher> vouchers = new ArrayList<>();

    // order received through QR code
    Order order = new Order();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button initBtn = findViewById(R.id.btn_init);
        initBtn.setOnClickListener((View v)->initScan());
    }

    private void initScan() {
        MyQRCode.scan(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String jsonResult = MyQRCode.onScanResult(requestCode, resultCode, data);

            order = getOrderJson(jsonResult);

            Intent intent = new Intent(this, OrderActivity.class);
            Bundle argument = new Bundle();

            argument.putSerializable(Constants.SEND_ORDER, this.order);

            intent.putExtras(argument);
            startActivity(intent);
        }
    }

    public Order getOrderJson(String data) {
        String userId = "";
        String signedMessage = "";
        ArrayList<Product> productsList = new ArrayList<>();
        ArrayList<Voucher> vouchersList = new ArrayList<>();
        String makeOrderJson = "";

        try {
            JSONObject response = new JSONObject(data);
            userId = response.getString("userId");
            signedMessage = response.getString("signature");

            JSONArray productsJson = response.getJSONArray("products");

            for (int i = 0; i < productsJson.length(); i++)
            {
                JSONObject product = productsJson.getJSONObject(i);

                int productId = product.getInt("id");
                String name = product.getString("name");
                int quantity = product.getInt("quantity");
                String image = product.getString("image");
                double price = product.getDouble("price");

                Product p = new Product(productId, name, price, image, quantity);
                productsList.add(p);
            }

            JSONObject makeOrder = new JSONObject();
            makeOrder.put("products", response.getJSONArray("products"));
            makeOrder.put("vouchers", response.get("vouchers"));
            makeOrderJson = makeOrder.toString();

            Log.d("claudia", makeOrderJson);

            JSONArray voucherInfoJson = response.getJSONArray("voucherInfo");
            for(int i = 0; i < voucherInfoJson.length(); i++) {
                JSONObject voucher = voucherInfoJson.getJSONObject(i);

                String voucherId = voucher.getString("id");
                String type = voucher.getString("type");
                double discount = voucher.getDouble("discount");

                Voucher v = new Voucher(voucherId, type, discount);
                vouchersList.add(v);
            }

        } catch(JSONException e) {
            Log.e("ERROR", e.getMessage());
        }

        order = new Order(userId, signedMessage, makeOrderJson, productsList, vouchersList);

        return order;
    }

}
