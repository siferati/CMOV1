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

        String data = "{\"orderid\":1,\"userid\":\"79b7dc38-9320-42e1-8c15-488e18cb5a3b\",\"price\":5.13,\"products\":[{\"id\":2,\"name\":\"Coffee\",\"quantity\":3,\"image\":\"coffee\",\"price\":1.5},{\"id\":3,\"name\":\"Popcorn\",\"quantity\":2,\"image\":\"popcorn\",\"price\":2},{\"id\":4,\"name\":\"Soda Drink\",\"quantity\":3,\"image\":\"soda_drink\",\"price\":2.4}],\"vouchers\":[{\"id\":\"62e833fb-9d47-4b43-8446-9f762a7a85cc\",\"type\":\"Coffee\",\"discount\":1},{\"id\":\"bf94fa90-3e5f-4097-810b-5a7535126542\",\"type\":\"Total\",\"discount\":0.05}]}";
        Order order1 = getOrderJson(data);

        Intent intent = new Intent(this, OrderActivity.class);
        Bundle argument = new Bundle();

        argument.putSerializable(Constants.SEND_ORDER, order1);

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
