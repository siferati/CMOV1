package org.feup.cmov.validationcafeteria;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.feup.cmov.validationcafeteria.dataStructures.Order;
import org.feup.cmov.validationcafeteria.dataStructures.Product;
import org.feup.cmov.validationcafeteria.dataStructures.Voucher;
import org.feup.cmov.validationcafeteria.order.OrderActivity;
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
        // TEST !!!!!!!!!!!!

        String userId = "74490d30-6c6c-4049-b83f-13e7e54b4637";

        Product p1 = new Product(2, "Coffee", 0.50, "coffee", 1);
        Product p2 = new Product(3, "Popcorn", 1.00, "popcorn", 2);
        Product p3 = new Product(4, "Soda Drink", 0.80, "soda_drink", 1);
        Product p4 = new Product(5, "Sandwich", 1.50, "sandwich", 1);

        products.add(p1);
        products.add(p2);
        products.add(p3);
        products.add(p4);

        Voucher v1 = new Voucher("e1748fd6-95f0-4799-82dd-a7ac90b2664f", Constants.FREE_COFFEE, 1.0);
        Voucher v2 = new Voucher("071c9175-46f6-45fd-96a6-980f8063b69b", Constants.FREE_COFFEE, 1.0);

        vouchers.add(v1);
        vouchers.add(v2);

        order.setUserId(userId);
        order.setOrderId(1);
        order.setPrice(20.22);
        order.setProducts(products);
        order.setVouchers(vouchers);

        Intent intent = new Intent(this, OrderActivity.class);
        Bundle argument = new Bundle();

        argument.putSerializable(Constants.SEND_ORDER, this.order);

        intent.putExtras(argument);
        startActivity(intent);
        finish();


        // TEST !!!!!!!!!!!! END!!

        /*try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        }
        catch (ActivityNotFoundException anfe) {
            showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }*/
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                act.startActivity(intent);
            }
        });
        downloadDialog.setNegativeButton(buttonNo, null);
        return downloadDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");

                order = getOrderJson(contents);

                Intent intent = new Intent(this, OrderActivity.class);
                Bundle argument = new Bundle();

                argument.putSerializable(Constants.SEND_ORDER, this.order);

                intent.putExtras(argument);
                startActivity(intent);
                finish();
            }
        }
    }

    public Order getOrderJson(String data) {
        int orderId = -1;
        String userId = "";
        double orderPrice = 0.0;
        ArrayList<Product> productsList = new ArrayList<>();
        ArrayList<Voucher> vouchersList = new ArrayList<>();

        try {
            JSONObject response = new JSONObject(data);
            orderId = response.getInt("orderid");
            userId = response.getString("userid");
            orderPrice = response.getDouble("price");

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

            JSONArray vouchersJson = response.getJSONArray("vouchers");
            for(int i = 0; i < vouchersJson.length(); i++) {
                JSONObject voucher = vouchersJson.getJSONObject(i);

                String voucherId = voucher.getString("id");
                String type = voucher.getString("type");
                double discount = voucher.getDouble("discount");

                Voucher v = new Voucher(voucherId, type, discount);
                vouchersList.add(v);
            }

        } catch(JSONException e) {
            Log.e("ERROR", e.getMessage());
        }

        order = new Order(orderId, userId, orderPrice, productsList, vouchersList);

        return order;
    }

}
