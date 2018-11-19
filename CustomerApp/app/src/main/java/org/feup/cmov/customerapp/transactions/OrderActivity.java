package org.feup.cmov.customerapp.transactions;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.cafeteria.ShoppingCartAdapter;
import org.feup.cmov.customerapp.dataStructures.Order;
import org.feup.cmov.customerapp.dataStructures.Product;
import org.feup.cmov.customerapp.utils.Constants;

public class OrderActivity extends AppCompatActivity {

    Order order = new Order();

    // adapter to products' list
    public ArrayAdapter<Product> productsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Bundle argument = getIntent().getExtras();

        if (argument != null) {
            order = (Order) argument.getSerializable(Constants.SHOW_ORDER);
        }

        setTitle("Order No. " + order.getId());

        ListView list_products = findViewById(R.id.list_products);
        productsAdapter = new ShoppingCartAdapter(this, order.getProducts());
        list_products.setAdapter(productsAdapter);

        TextView vouchersTV = findViewById(R.id.text_used_vouchers);

        String used_vouchers = order.getVouchers().size() + " vouchers selected";
        vouchersTV.setText(used_vouchers);

        String price = "Total price: " + order.getPrice()  + " €";
        TextView priceTV = findViewById(R.id.total_price);
        priceTV.setText(price);
    }
}
