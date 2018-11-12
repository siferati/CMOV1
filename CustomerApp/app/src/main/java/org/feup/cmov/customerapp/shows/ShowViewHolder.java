package org.feup.cmov.customerapp.shows;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.feup.cmov.customerapp.R;

public class ShowViewHolder extends RecyclerView.ViewHolder {

    // show's name
    public TextView name;

    // show's date
    public TextView date;

    // show's price
    public TextView price;

    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    ShowViewHolder(View itemView) {
        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
        super(itemView);

        name = itemView.findViewById(R.id.name);
        date = itemView.findViewById(R.id.date);
        price = itemView.findViewById(R.id.price);
    }
}
