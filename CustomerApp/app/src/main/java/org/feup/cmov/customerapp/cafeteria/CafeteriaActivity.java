package org.feup.cmov.customerapp.cafeteria;

import android.app.Activity;
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
import android.widget.Toast;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Product;
import org.feup.cmov.customerapp.database.GetProducts;
import org.feup.cmov.customerapp.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class CafeteriaActivity extends AppCompatActivity {

    // API to get shows from server
    public GetProducts productsAPI;

    // tickets list
    List<Product> products = new ArrayList<>();

    // adapter to products' list
    public ArrayAdapter<Product> productsAdapter;

    // list of selected tickets to validate
    private List<Product> selectedProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafeteria);

        productsAPI = new GetProducts(this);
        Thread thr = new Thread(productsAPI);
        thr.start();

        Button shoppingBtn = findViewById(R.id.btn_shopping_cart);
        shoppingBtn.setOnClickListener((View v)->shoppingCart());
    }

    private void shoppingCart() {
        if(selectedProducts.size() > 0) {
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            Bundle argument = new Bundle();

            ArrayList<Product> productsList = new ArrayList<>(selectedProducts);
            argument.putSerializable(Constants.SHOPPING_CART, productsList);

            intent.putExtras(argument);
            startActivity(intent);

            Set<String> productSet = new HashSet<>();

            for(Product p: selectedProducts) {
                String product = p.getId() + " " + p.getQuantity();
                productSet.add(product);
            }

            SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putStringSet(Constants.PREF_PRODUCTS, productSet);

            editor.apply();
        } else {
            showToast(Constants.NO_PRODUCTS);
        }
    }

    /**
     * Handles response from server
     * @param code - response code from server
     * @param response - response message given by server
     */
    public void handleResponse(int code, String response, List<Product> products) {
        if (code == Constants.OK_RESPONSE) {
            List<Product> productsList = restoreSelectedProducts(products);

            ListView list_products = findViewById(R.id.list_products);
            productsAdapter = new ProductAdapter(this, productsList);
            list_products.setAdapter(productsAdapter);

            //productsAdapter.addAll(productsList);
        } else {
            // show error response
            showToast(response);
        }
    }

    public List<Product> restoreSelectedProducts(List<Product> products) {
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);

        if (prefs.contains(Constants.PREF_PRODUCTS)) {
            Set<String> productSet = prefs.getStringSet(Constants.PREF_PRODUCTS, null);
            List<Product> selectedProds = new ArrayList<>();

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
                        p.setQuantity(sp.getQuantity());
                        selectedProducts.add(p);
                    }
                }
            }
        }

        return products;
    }

    public static void resetSharedPrefs(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.PREF_PRODUCTS);
        editor.apply();
    }

    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(CafeteriaActivity.this, toast, Toast.LENGTH_LONG).show());
    }

    public void setSelectedProducts(List<Product> selectedProducts) {
        this.selectedProducts = selectedProducts;
    }

    public List<Product> getSelectedProducts() {
        return selectedProducts;
    }
}
