package org.feup.cmov.customerapp.shows;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Show;
import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.database.GetShows;
import org.feup.cmov.customerapp.database.LocalDatabase;
import org.feup.cmov.customerapp.shows.tickets.TicketAdapter;
import org.feup.cmov.customerapp.shows.tickets.TicketValidationActivity;
import org.feup.cmov.customerapp.shows.tickets.ValidateTicketsDialog;
import org.feup.cmov.customerapp.shows.tickets.ValidateTicketsFragment;
import org.feup.cmov.customerapp.utils.Constants;
import org.feup.cmov.customerapp.utils.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

public class ShowsActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, ValidateTicketsDialog.MyDialogCloseListener {

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

    // API to get shows from server
    public GetShows showsAPI;

    // shows adapter to manage shows array
    public ShowAdapter showsAdapter;

    // request code for each show
    public final int REQUEST_TICKETS = 1;

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

    // list of selected tickets to validate
    private List<Ticket> selectedTickets = new ArrayList<>();

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

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(itemClickListener);

        tab1 = recyclerView;
    }

    /**
     * Sets tickets' list and its adapter
     */
    public void setTicketsList() {
        ListView list_tickets = findViewById(R.id.list_tickets);
        ticketsAdapter = new TicketAdapter(this, tickets);
        list_tickets.setAdapter(ticketsAdapter);

        loadTicketsDatabase();

        Button validateBtn = findViewById(R.id.btn_validate);
        validateBtn.setOnClickListener((View v)->validateTickets());

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

    /**
     * Callback from show activity
     * @param requestCode - request code used to request this callback
     * @param resultCode - checks result code sent from callback activity
     * @param data - data sent from callback activity
     */
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            ArrayList<Ticket> ts = (ArrayList<Ticket>) data.getSerializableExtra("tickets");

            if (ts != null) {
                saveTicketsDatabase(ts);
            } else {
                Log.d("http", "No tickets");
            }
        }
    }


    /**
     * Saves tickets to the local database
     * @param ts - tickets to save
     */
    private void saveTicketsDatabase(ArrayList<Ticket> ts) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LocalDatabase db = LocalDatabase.getInstance(getApplicationContext());

                for(int i = 0; i < ts.size(); i++) {
                    db.addTicket(getApplicationContext(), ts.get(i));
                }

                List<Ticket> ticketsList = db.getAllTickets(getApplicationContext());

                ticketsAdapter.clear();
                ticketsAdapter.addAll(ticketsList);
            }
        });
    }

    /**
     * Loads tickets from local database to tickets' array adapter
     */
    private void loadTicketsDatabase() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LocalDatabase db = LocalDatabase.getInstance(getApplicationContext());

                if (LocalDatabase.checkDataBase(getApplicationContext())) {
                    List<Ticket> localTickets = db.getAllTickets(getApplicationContext());
                    if (localTickets.size() > 0) ticketsAdapter.addAll(localTickets);
                }
            }
        });
    }

    /**
     * Show empty message if there's no bought tickets
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);

        ListView list = findViewById(R.id.list_tickets);
        list.setEmptyView(empty);
    }


    /**
     * Handles response from server
     * @param code - response code from server
     * @param response - response message given by server
     */
    public void handleResponse(int code, String response) {
        if (code != Constants.OK_RESPONSE) {
            // show error response
            Constants.showToast(response, this);
        } else {
            // if loading shows was successful, then set isLoading to false
            isLoading = false;
        }
    }

    /**
     * Notifies shows' adapter of new changes to shows' array
     */
    public void notifyShowsAdapter() {
        ServiceHandler sh = new ServiceHandler();
        sh.run();
    }

    /**
     * Adds ticket to selected tickets' list
     * @param ticket - ticket to add
     */
    public void addTicket(Ticket ticket) {
        selectedTickets.add(ticket);
    }

    /**
     * Removes ticket from selected tickets' list
     * @param ticket - ticket to remove
     */
    public void removeTicket(Ticket ticket) {
        selectedTickets.remove(ticket);
    }

    /**
     * Calls fragment to confirm tickets validation
     */
    public void validateTickets() {
        if(checkIfSingleShow(selectedTickets)) {
            checkIfValidNumber();
        } else {
            Constants.showToast(Constants.SINGLE_SHOW, this);
        }
    }

    public boolean checkIfSingleShow(List<Ticket> tickets) {
        boolean single_show = true;

        if (tickets.size() > 0) {
            Ticket t = tickets.get(0);

            if (tickets.size() > 1) {
                for(int i = 1; i < tickets.size(); i++) {
                    String ticketIndexName = tickets.get(i).getName();
                    if (!ticketIndexName.equals(t.getName())) {
                        single_show = false;
                        return single_show;
                    }
                }
            }
        }

        return single_show;
    }

    public void checkIfValidNumber() {
        if (selectedTickets.size() <= 4) {
            if (selectedTickets.size() > 0) {
                ArrayList<Ticket> selected_ticks = new ArrayList<>(selectedTickets);

                ValidateTicketsFragment dialog = ValidateTicketsFragment.constructor(selected_ticks);
                dialog.show(getSupportFragmentManager(), "validate_tickets");
            } else {
                Constants.showToast(Constants.NO_TICKETS, this);
            }
        } else {
            Constants.showToast(Constants.VALIDATE_FAILED, this);
        }
    }

    /**
     * Calls new activity that shows QR code to validate tickets
     */
    @Override
    public void handleValidateTickets(ArrayList<Ticket> tickets) {
        Intent intent = new Intent(this, TicketValidationActivity.class);
        Bundle argument = new Bundle();

        argument.putSerializable(Constants.VALIDATION_QR, tickets);

        intent.putExtras(argument);
        startActivity(intent);

        // TODO: this update will probably happen when the terminal replies
        updateTicketsDatabase(tickets);
    }

    public void updateTicketsDatabase(ArrayList<Ticket> tickets) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LocalDatabase db = LocalDatabase.getInstance(getApplicationContext());

                for(int i = 0; i < tickets.size(); i++) {
                    tickets.get(i).setAvailable(false);
                    db.updateTicket(tickets.get(i));
                }

                List<Ticket> ticketsList = db.getAllTickets(getApplicationContext());

                ticketsAdapter.clear();
                ticketsAdapter.addAll(ticketsList);
            }
        });
    }

    /**
     * Sets click listener on each show
     */
    public ItemClickSupport.OnItemClickListener itemClickListener = new ItemClickSupport.OnItemClickListener() {
        @Override
        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
            Show s = showsAdapter.getItem(position);

            Intent intent = new Intent(getApplicationContext(), ShowActivity.class);

            Bundle b = new Bundle();
            b.putSerializable(Constants.GET_SHOW, s);

            intent.putExtras(b);

            startActivityForResult(intent, REQUEST_TICKETS);
        }
    };

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
     * Called when a tab is selected
     * @param tab - selected tab
     */
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

    /**
     * Called when a tab is unselected
     * @param tab - unselected tab
     */
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

    /**
     * Called when a tab is reselected
     * @param tab - reselected tab
     */
    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }
}
