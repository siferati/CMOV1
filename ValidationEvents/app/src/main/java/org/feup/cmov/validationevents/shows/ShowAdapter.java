package org.feup.cmov.validationevents.shows;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import org.feup.cmov.validationevents.R;
import org.feup.cmov.validationevents.dataStructures.Show;

import java.util.ArrayList;
import java.util.List;

public class ShowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // index of selected item
    public int mSelectedItem = -1;

    class ShowViewHolder extends RecyclerView.ViewHolder {

        // radio to select show
        public RadioButton button;

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

            button = itemView.findViewById(R.id.select_show);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            price = itemView.findViewById(R.id.price);

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedItem = getAdapterPosition();
                    notifyDataSetChanged();
                }
            };

            itemView.setOnClickListener(clickListener);
            button.setOnClickListener(clickListener);
        }
    }

    // current application context
    private Context mContext;

    // Store a member variable for the shows
    public List<Show> shows;

    // Footer's view holder
    private FooterViewHolder footerViewHolder;

    // If footer (loading progress bar) is to be shown
    private boolean isFooterAdded = false;

    // Show's row
    private static final int SHOW = 0;

    // Footer's row
    private static final int FOOTER = 1;

    // Constructor creates new shows array
    public ShowAdapter(Context context) {
        this.mContext = context;
        this.shows = new ArrayList<>();
    }

    /**
     * Inflates a layout from XML and returns the holder, depending on the view's type
     * @param parent - view's parent
     * @param viewType - view's type
     * @return suitable view holder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case SHOW:
                viewHolder = createShowViewHolder(parent);
                break;
            case FOOTER:
                viewHolder = createFooterViewHolder(parent);
                break;
            default:
                break;
        }

        return viewHolder;
    }

    /**
     * Inflates show's layout and returns the holder
     * @param parent - view's parent
     * @return show's view holder
     */
    private RecyclerView.ViewHolder createShowViewHolder(ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        final View showView = inflater.inflate(R.layout.row_show, parent, false);

        // Return a new holder instance
        RecyclerView.ViewHolder viewHolder = new ShowViewHolder(showView);
        return viewHolder;
    }

    /**
     * Inflates footer's layout and returns the holder
     * @param parent - view's parent
     * @return footer's view holder
     */
    private RecyclerView.ViewHolder createFooterViewHolder(ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View footerView = inflater.inflate(R.layout.adapter_footer, parent, false);

        // Return a new holder instance
        RecyclerView.ViewHolder viewHolder = new FooterViewHolder(footerView);
        return viewHolder;
    }

    /**
     * Populates data into the item through holder
     * @param viewHolder - current view holder
     * @param position - row position in the array
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case SHOW:
                final ShowViewHolder holder = (ShowViewHolder) viewHolder;
                bindShowViewHolder(viewHolder, position);

                break;
            case FOOTER:
                bindFooterViewHolder(viewHolder);
            default:
                break;
        }

    }

    /**
     * Populates data into show row
     * @param viewHolder - show's view holder
     * @param position - show's position
     */
    private void bindShowViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        final ShowViewHolder holder = (ShowViewHolder) viewHolder;

        // Get the data model based on position
        Show show = shows.get(position);

        // Set item views based on your views and data model
        holder.name.setText(show.getName());
        holder.date.setText(show.getDate());

        String price = show.getPrice()  + " €";
        holder.price.setText(price);

        holder.button.setChecked(position == mSelectedItem);
    }

    /**
     * Sets footer view holder
     * @param viewHolder - footer's view holder
     */
    private void bindFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        footerViewHolder = (FooterViewHolder) viewHolder;
    }

    /**
     * Gets items' count
     * @return returns the total count of items in the list
     */
    @Override
    public int getItemCount() {
        return shows.size();
    }

    /**
     * Gets row's view type depending on array position
     * @param position - row position
     * @return returns int representing view type
     */
    @Override
    public int getItemViewType(int position) {
        return (isLastPosition(position) && isFooterAdded) ? FOOTER : SHOW;
    }

    /**
     * Get array's show depending on index
     * @param position - index of desired show
     * @return returns a show
     */
    public Show getItem(int position) {
        return shows.get(position);
    }

    /**
     * Adds all shows in the list to shows' array
     * @param showList - shows's list to be added to current shows' array
     */
    public void addAll(List<Show> showList) {
        shows.addAll(showList);
    }

    /**
     * Checks if shows' array is empty
     * @return true if array is empty, false if not
     */
    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    /**
     * Checks if current position is the last one
     * @param position - position to check
     * @return true if position is the last one, false if not
     */
    public boolean isLastPosition(int position) {
        return (position == shows.size()-1);
    }

    /**
     * Add footer showing progress bar
     */
    public void addFooter() {
        isFooterAdded = true;
    }

    /**
     * Removes footer
     */
    public void removeFooter() {
        isFooterAdded = false;
    }
}
