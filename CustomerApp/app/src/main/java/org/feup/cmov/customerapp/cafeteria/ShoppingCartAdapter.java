package org.feup.cmov.customerapp.cafeteria;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Product;

import java.util.ArrayList;

public class ShoppingCartAdapter extends ArrayAdapter<Product> {

    // activity
    public Activity activity;

    // list of products
    public ArrayList<Product> products;

    public ShoppingCartAdapter(Activity activity, ArrayList<Product> products) {
        super(activity, R.layout.row_shopping_cart, products);

        this.activity = activity;
        this.products = products;
    }

    @Override
    public @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.row_shopping_cart, parent, false);    // get our custom layout
        }
        Product p = products.get(position);

        ((TextView) row.findViewById(R.id.product_name_SC)).setText(p.getName());

        String quantity = "Quantity: " + p.getQuantity();
        ((TextView) row.findViewById(R.id.product_quantity_SC)).setText(quantity);

        String price = p.getTotalPriceRounded()  + " €";
        ((TextView)row.findViewById(R.id.total_price_product)).setText(price);

        ImageView image = row.findViewById(R.id.product_image_SC);
        int image_id = activity.getResources().getIdentifier(p.getImage() , "drawable", activity.getPackageName());

        if (image_id > 0) {
            image.setImageResource(image_id);
        }

        return (row);
    }
}
