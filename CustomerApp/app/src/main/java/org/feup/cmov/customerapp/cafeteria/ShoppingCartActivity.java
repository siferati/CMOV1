package org.feup.cmov.customerapp.cafeteria;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Product;
import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.dataStructures.Voucher;
import org.feup.cmov.customerapp.database.GetProducts;
import org.feup.cmov.customerapp.login.RegisterActivity;
import org.feup.cmov.customerapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ShoppingCartActivity extends AppCompatActivity {

    private static final int REQUEST_VOUCHERS = 0;

    // API to get shows from server
    public GetProducts productsAPI;

    // tickets to validate
    ArrayList<Product> products;

    // adapter to products' list
    public ArrayAdapter<Product> productsAdapter;

    // selected vouchers
    ArrayList<Voucher> selectedVouchers = new ArrayList<>();

    TextView totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        Bundle argument = getIntent().getExtras();

        /*products = new ArrayList<>();
        if (argument != null) {
            products = (ArrayList<Product>) argument.getSerializable(Constants.SHOPPING_CART);
        }*/

        productsAPI = new GetProducts(this);
        Thread thr = new Thread(productsAPI);
        thr.start();

        /*ListView list_products = findViewById(R.id.list_shopping_cart);
        productsAdapter = new ShoppingCartAdapter(this, products);
        list_products.setAdapter(productsAdapter);*/

        Button selectVouchers = findViewById(R.id.btn_select_vouchers);
        selectVouchers.setOnClickListener((View v)->selectVouchers());

        Button buy = findViewById(R.id.btn_buy_SC);
        buy.setOnClickListener((View v)->buyOrder());

        totalPrice = findViewById(R.id.total_price_SC);
    }

    public void handleResponse(int code, String response, List<Product> prods) {
        ArrayList<Product> productsList = new ArrayList<>(prods);
        if (code == Constants.OK_RESPONSE) {
            products = restoreSelectedProducts(productsList);

            ListView list_products = findViewById(R.id.list_shopping_cart);
            productsAdapter = new ShoppingCartAdapter(this, products);
            list_products.setAdapter(productsAdapter);

            setPriceText();
        } else {
            // show error response
            showToast(response);
        }
    }

    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(ShoppingCartActivity.this, toast, Toast.LENGTH_LONG).show());
    }

    public void setPriceText() {
        runOnUiThread(() -> {
            String price = "TOTAL PRICE (without vouchers): " + getTotalPrice() + " €";
            totalPrice.setText(price);
        });
    }

    public ArrayList<Product> restoreSelectedProducts(ArrayList<Product> products) {
        ArrayList<Product> productList = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);

        if (prefs.contains(Constants.PREF_PRODUCTS)) {
            Set<String> productSet = prefs.getStringSet(Constants.PREF_PRODUCTS, null);
            ArrayList<Product> selectedProds = new ArrayList<>();

            for(String p : productSet) {
                if(p.contains(" ")){
                    String id = p.substring(0, p.indexOf(" "));
                    String quantity = p.substring(p.indexOf(" ")+1, p.length());

                    Product product = new Product(Integer.parseInt(id), Integer.parseInt(quantity));
                    selectedProds.add(product);
                }
            }

            for (Product sp : selectedProds) {
                for (Product p : products) {
                    if (p.getId() == sp.getId()) {
                        Product product = p;
                        product.setQuantity(sp.getQuantity());

                        productList.add(product);
                    }
                }
            }
        }

        return productList;
    }

    private String getTotalPrice() {
        double price = 0.0;

        for(Product p : products) {
            price += p.getTotalPrice();
        }

        double rounded = (double) Math.round(price * 100.0) / 100.0;
        return Double.toString(rounded);
    }

    private void selectVouchers() {
        Intent intent = new Intent(getApplicationContext(), SelectVoucherActivity.class);

        if (selectedVouchers.size() > 0) {
            Bundle argument = new Bundle();
            argument.putSerializable(Constants.SELECTED_VOUCHERS, selectedVouchers);
            intent.putExtras(argument);
        }

        startActivityForResult(intent, REQUEST_VOUCHERS);
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_VOUCHERS) {
            selectedVouchers = (ArrayList<Voucher>) data.getSerializableExtra(Constants.REQUESTED_VOUCHERS);

            TextView vouchersTV = findViewById(R.id.text_vouchers_selected);

            String vouchers_selected = selectedVouchers.size() + " vouchers selected";
            vouchersTV.setText(vouchers_selected);
        }
    }

    private void buyOrder() {
        CafeteriaActivity.resetSharedPrefs(this);

        // TODO: delete vouchers and send order to validation terminal!!!!!!!!!!!!!!!!!!!!!!
    }

}
