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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {

    // cafeteria activity
    public CafeteriaActivity activity;

    // list of products
    public List<Product> products;

    ProductAdapter(CafeteriaActivity activity, List<Product> products) {
        super(activity, R.layout.row_product, products);

        this.activity = activity;
        this.products = products;
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

        String no_products = Integer.toString(p.getQuantity());
        numberProduct.setText(no_products);

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
        int quantity = product.getQuantity();
        quantity++;

        updateQuantity(product, quantity, numberProduct, true);
        updateSelectedProducts(product, quantity, true);
    }

    private void decreaseQuantity(Product product, TextView numberProduct) {
        int quantity = product.getQuantity();

        if (quantity > 0) {
            quantity--;

            updateQuantity(product, quantity, numberProduct, true);
            updateSelectedProducts(product, quantity, false);
        } else {
            Constants.showToast(Constants.DECREASE_FAILED_PRODUCT, activity);
        }
    }

    private void updateQuantity(Product p, int quantity, TextView numberProduct, boolean changeAdapter) {
        String no_products = Integer.toString(quantity);
        numberProduct.setText(no_products);

        if (changeAdapter) {
            p.setQuantity(quantity);
            notifyDataSetChanged();
        }
    }

    private void updateSelectedProducts(Product product, int quantity, boolean increase) {
        List<Product> selectedProducts = activity.getSelectedProducts();

        if (selectedProducts.contains(product)) {
            Product existingProduct = null;

            // if list contains the product, then update it with the new quantity
            for (Product p : selectedProducts) {
                if (p.getId() == product.getId()) {
                    p.setQuantity(quantity);
                    existingProduct = p;

                    break;
                }
            }

            if (existingProduct != null) {
                // if the product's quantity is zero, then remove it from the list
                if (existingProduct.getQuantity() == 0) {
                    selectedProducts.remove(existingProduct);
                }
            }

        } else {
            // if list doesn't contain product and the user has increased the product's quantity then add the product
            if (increase) {
                selectedProducts.add(product);
            }
        }

        activity.setSelectedProducts(selectedProducts);
    }

}
