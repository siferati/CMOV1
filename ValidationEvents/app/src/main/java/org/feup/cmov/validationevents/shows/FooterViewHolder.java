package org.feup.cmov.validationevents.shows;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.feup.cmov.validationevents.R;

public class FooterViewHolder extends RecyclerView.ViewHolder {

    // loading view containing progress bar
    public FrameLayout loadingView;

    // rotating progress bar
    public ProgressBar loadingPB;

    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    FooterViewHolder(View itemView) {

        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
        super(itemView);

        loadingView = itemView.findViewById(R.id.loading_fl);
        loadingPB = itemView.findViewById(R.id.loading_pb);
    }
}
