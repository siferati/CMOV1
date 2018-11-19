package org.feup.cmov.customerapp.transactions;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Order;

import java.util.ArrayList;

public class OrderAdapter extends ArrayAdapter<Order> {
    private TransactionsActivity activity;
    private ArrayList<Order> orders;

    public OrderAdapter(TransactionsActivity activity, ArrayList<Order> orders) {
        super(activity, R.layout.row_order, orders);

        this.activity = activity;
        this.orders = orders;
    }

    @Override
    public @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.row_order, parent, false);            // get our custom layout
        }

        Order o = orders.get(position);

        String order_id = "Order ID: " + o.getId();
        ((TextView)row.findViewById(R.id.order_id)).setText(order_id);
        ((TextView)row.findViewById(R.id.order_date)).setText(o.getDate());

        String price = o.getPrice()  + " €";
        ((TextView)row.findViewById(R.id.order_price)).setText(price);

        return (row);
    }

    @Override
    public void add(Order item) {
        orders.add(item);
        notifyDataSetChanged();
    }
}
