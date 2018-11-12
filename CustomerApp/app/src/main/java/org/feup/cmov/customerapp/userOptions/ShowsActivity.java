package org.feup.cmov.customerapp.userOptions;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.database.GetShows;
import org.feup.cmov.customerapp.shows.ShowAdapter;
import org.feup.cmov.customerapp.shows.TicketAdapter;
import org.feup.cmov.customerapp.utils.Constants;
import org.feup.cmov.customerapp.utils.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

public class ShowsActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    // API to get shows from server
    public GetShows showsAPI;

    // shows adapter to manage shows array
    public ShowAdapter showsAdapter;

    // Service Handler allows to notify the adapter of its list's changes using threads
    public class ServiceHandler {
        public ServiceHandler() {}

        public void run() {
            ShowsActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showsAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    // measures and positions item views within the recyclerView
    LinearLayoutManager layoutManager;

    // layout view containing all shows
    RecyclerView recyclerView;

    // shows' current page
    private int page = 1;

    // shows' page limit
    private int pageSize = Constants.SHOWS_PER_LOAD;

    // checks if server is loading shows or not
    private boolean isLoading = false;

    // tickets list
    List<Ticket> tickets = new ArrayList<>();

    // adapter to tickets' list
    ArrayAdapter<Ticket> ticketsAdapter;

    // tabs for lists and bought tickets
    TabLayout.Tab listShowsTab, boughtTicketsTab;

    // tab1 - shows' list, tab2 - bought tickets' list
    View tab1, tab2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shows);

        setRecyclerView();

        showsAPI = new GetShows(this, page, pageSize);
        Thread thr = new Thread(showsAPI);
        thr.start();

        setTicketsList();
        setTabs();
    }

    /**
     * Handles response from server
     * @param code - response code from server
     * @param response - response message given by server
     */
    public void handleResponse(int code, String response) {
        if (code != Constants.OK_RESPONSE) {
            // show error response
            showToast(response);
        } else {
            isLoading = false;
        }
    }

    /**
     * Shows toast message
     * @param toast - message to show
     */
    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(ShowsActivity.this, toast, Toast.LENGTH_LONG).show());
    }

    /**
     * Notifies shows' adapter of new changes to shows' array
     */
    public void notifyRV() {
        ServiceHandler sh = new ServiceHandler();
        sh.run();
    }

    /**
     * Sets the recycler view, its layout manager and adapter
     */
    public void setRecyclerView() {
        recyclerView = findViewById(R.id.list_shows);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        showsAdapter = new ShowAdapter();
        recyclerView.setAdapter(showsAdapter);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

        view_single_show(recyclerView);

        tab1 = recyclerView;
    }

    /**
     * Sets scroll listener on the recycler view (endless scroll)
     */
    public RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!isLoading && !showsAPI.isLastPage()) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    loadMoreItems();
                }
            }
        }
    };

    /**
     * Load more shows from server
     */
    private void loadMoreItems() {
        isLoading = true;

        page++;

        showsAPI = new GetShows(this, page, pageSize);
        Thread thr = new Thread(showsAPI);
        thr.start();
    }

    /**
     * Sets click listener on each show
     * @param list_shows - layout containing all shows
     */
    public void view_single_show(RecyclerView list_shows) {
        ItemClickSupport.addTo(list_shows).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // show view
            }
        });
    }

    /**
     * Sets tickets' list and its adapter
     */
    public void setTicketsList() {
        ListView list_tickets = findViewById(R.id.list_tickets);
        ticketsAdapter = new TicketAdapter(this, tickets);
        list_tickets.setAdapter(ticketsAdapter);

        tab2 = findViewById(R.id.tickets);
    }

    /**
     * Sets layout tabs
     */
    public void setTabs() {
        TabLayout tabs = findViewById(R.id.tabs);
        listShowsTab = tabs.newTab().setText("Shows").setIcon(R.drawable.calendar);
        tabs.addTab(listShowsTab);
        boughtTicketsTab = tabs.newTab().setText("Validate").setIcon(R.drawable.check);
        tabs.addTab(boughtTicketsTab);
        tabs.addOnTabSelectedListener(this);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = findViewById(R.id.list_tickets);
        list.setEmptyView(empty);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                tab1.setVisibility(View.VISIBLE);
                break;
            case 1:
                tab2.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                tab1.setVisibility(View.INVISIBLE);
                break;
            case 1:
                tab2.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }
}
