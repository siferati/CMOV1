package org.feup.cmov.customerapp.cafeteria;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Product;
import org.feup.cmov.customerapp.database.GetProducts;
import org.feup.cmov.customerapp.utils.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CafeteriaActivity extends AppCompatActivity {

    // API to get products from server
    public GetProducts productsAPI;

    // adapter to products' list
    public ArrayAdapter<Product> productsAdapter;

    // list of selected products to order
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

    /**
     * Show empty message if there's no products
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty_products);

        ListView list = findViewById(R.id.list_products);
        list.setEmptyView(empty);
    }

    /**
     * Called when shopping cart button is clicked. Starts ShoppingCartActivity and saves user's order on SharedPreferences
     */
    private void shoppingCart() {
        if(selectedProducts.size() > 0) {
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            startActivity(intent);

            saveSelectedProducts();
        } else {
            Constants.showToast(Constants.NO_PRODUCTS, this);
        }
    }

    /**
     * Handles response from server
     * @param code - response code from server
     * @param response - response message given by server
     * @param products - products that we got from the server
     */
    public void handleResponse(int code, String response, List<Product> products) {
        if (code == Constants.OK_RESPONSE) {
            List<Product> productsList = restoreSelectedProducts(products);

            ListView list_products = findViewById(R.id.list_products);
            productsAdapter = new ProductAdapter(this, productsList);
            list_products.setAdapter(productsAdapter);
        } else {
            // show error response
            Constants.showToast(response, this);
        }
    }

    /**
     * Saves selected products locally (id + quantity)
     */
    public void saveSelectedProducts() {
        Set<String> productSet = new HashSet<>();

        for(Product p: selectedProducts) {
            String product = p.getId() + " " + p.getQuantity();
            productSet.add(product);
        }

        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putStringSet(Constants.PREF_PRODUCTS, productSet);

        editor.apply();
    }

    /**
     * Restores selected products when this activity is created
     * @param products - products that we got from the server
     * @return updated products's info according to the user's order (with correct quantities)
     */
    public List<Product> restoreSelectedProducts(List<Product> products) {
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);

        if (prefs.contains(Constants.PREF_PRODUCTS)) {
            // get set from shared preferences
            Set<String> productSet = prefs.getStringSet(Constants.PREF_PRODUCTS, null);

            // get array stored in shared preferences
            List<Product> prefsProducts = new ArrayList<>();

            if (productSet != null) {
                for (String p : productSet) {
                    if (p.contains(" ")) {
                        // id is first part of string before first space
                        String id = p.substring(0, p.indexOf(" "));

                        // quantity is second part of string after first space
                        String quantity = p.substring(p.indexOf(" ") + 1, p.length());

                        Product product = new Product(Integer.parseInt(id), Integer.parseInt(quantity));
                        prefsProducts.add(product);
                    }
                }

                // goes through all products stored in shared preferences (id, quantity) and updates products' quantity
                for (Product sp : prefsProducts) {
                    for (Product p : products) {
                        if (p.getId() == sp.getId()) {
                            p.setQuantity(sp.getQuantity());
                            selectedProducts.add(p);
                        }
                    }
                }
            }
        }

        return products;
    }

    /**
     * Resets products in shared preferences
     * @param activity - activity in which this function is called
     */
    public static void resetSharedPrefs(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.PREF_PRODUCTS);
        editor.apply();
    }

    public void setSelectedProducts(List<Product> selectedProducts) {
        this.selectedProducts = selectedProducts;
    }

    public List<Product> getSelectedProducts() {
        return selectedProducts;
    }
}
