package org.feup.cmov.validationcafeteria.order;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.feup.cmov.validationcafeteria.R;
import org.feup.cmov.validationcafeteria.dataStructures.Product;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    // order activity
    public OrderActivity activity;

    // list of products
    public ArrayList<Product> products;

    ProductAdapter(OrderActivity activity, ArrayList<Product> products) {
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

        ((TextView) row.findViewById(R.id.product_name)).setText(p.getName());

        String quantity = "Quantity: " + p.getQuantity();
        ((TextView) row.findViewById(R.id.product_quantity)).setText(quantity);

        String price = p.getTotalPriceRounded()  + " €";
        ((TextView)row.findViewById(R.id.total_price)).setText(price);

        ImageView image = row.findViewById(R.id.product_image);
        int image_id = activity.getResources().getIdentifier(p.getImage() , "drawable", activity.getPackageName());

        if (image_id > 0) {
            image.setImageResource(image_id);
        }

        return (row);
    }
}
