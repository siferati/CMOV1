package org.feup.cmov.customerapp.cafeteria;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Product;
import org.feup.cmov.customerapp.utils.Constants;

import java.util.HashMap;
import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {

    // cafeteria activity
    public CafeteriaActivity activity;

    // list of products
    public List<Product> products;

    public HashMap<Product, Integer> productsQuantity;

    ProductAdapter(CafeteriaActivity activity, List<Product> products) {
        super(activity, R.layout.row_product, products);

        this.activity = activity;
        this.products = products;
        productsQuantity = new HashMap<Product, Integer>();
    }

    @Override
    public @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.row_product, parent, false);    // get our custom layout
        }
        Product p = products.get(position);

        ((TextView)row.findViewById(R.id.product_name)).setText(p.getName());

        TextView increaseTickets = row.findViewById(R.id.increase_product);
        TextView decreaseTickets = row.findViewById(R.id.decrease_product);

        TextView numberProduct = row.findViewById(R.id.number_product);
        increaseTickets.setOnClickListener((View v)->increaseQuantity(p, numberProduct));
        decreaseTickets.setOnClickListener((View v)->decreaseQuantity(p, numberProduct));

        String price = p.getPrice()  + " €";
        ((TextView)row.findViewById(R.id.price_product)).setText(price);

        ImageView image = row.findViewById(R.id.product_image);
        int image_id = activity.getResources().getIdentifier(p.getImage() , "drawable", activity.getPackageName());

        if (image_id > 0) {
            image.setImageResource(image_id);
        }

        return (row);
    }

    private void increaseQuantity(Product product, TextView numberProduct) {
        Integer quantity = productsQuantity.get(product);
        if (quantity != null) {
            quantity++;

            updateQuantity(product, quantity, numberProduct, true);
        } else {
            int quant = 1;

            updateQuantity(product, quant, numberProduct, true);
        }
    }

    private void decreaseQuantity(Product product, TextView numberProduct) {
        Integer quantity = productsQuantity.get(product);
        if (quantity != null) {

            if (quantity > 0) {
                quantity--;

                updateQuantity(product, quantity, numberProduct, true);
            } else {
                activity.showToast(Constants.DECREASE_FAILED_PRODUCT);
            }

        } else {
            int quant = 0;

            updateQuantity(product, quant, numberProduct, false);

            activity.showToast(Constants.DECREASE_FAILED_PRODUCT);
        }
    }

    private void updateQuantity(Product p, int quantity, TextView numberProduct, boolean changeAdapter) {
        productsQuantity.put(p, quantity);
        numberProduct.setText(quantity);

        if (changeAdapter) {
            p.setQuantity(quantity);
            notifyDataSetChanged();
        }
    }
}
