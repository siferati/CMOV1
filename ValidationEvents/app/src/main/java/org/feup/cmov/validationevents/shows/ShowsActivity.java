package org.feup.cmov.validationevents.shows;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.feup.cmov.validationevents.utils.Constants;
import org.feup.cmov.validationevents.R;
import org.feup.cmov.validationevents.dataStructures.Ticket;
import org.feup.cmov.validationevents.server.GetShows;
import org.feup.cmov.validationevents.server.GetTickets;
import org.feup.cmov.validationevents.utils.MyQRCode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShowsActivity extends AppCompatActivity {

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

    // tickets that we got from QR code
    private ArrayList<Ticket> tickets = new ArrayList<>();

    // show selected by the user
    private int showSelected;

    // tickets api
    private GetTickets ticketsAPI;

    // tickets that we got from server
    private ArrayList<Ticket> ticketsList;

    // user id that we got from the QR code
    private String userId;

    // show id that we got from the QR code
    private int showId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shows);

        recyclerView = findViewById(R.id.list_shows);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        showsAdapter = new ShowAdapter(getApplicationContext());
        recyclerView.setAdapter(showsAdapter);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        showsAPI = new GetShows(this, page, pageSize);
        Thread thr = new Thread(showsAPI);
        thr.start();

        Button btn = findViewById(R.id.btn_show);
        btn.setOnClickListener((View v)->selectShow());
    }

    public void selectShow() {
        int selectedShow = showsAdapter.mSelectedItem;

        if (selectedShow > -1) {
            if(showsAdapter.getItemCount() > selectedShow) {
                showSelected = showsAdapter.getItem(selectedShow).getId();

                SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                prefEditor.putInt("SELECTED_SHOW", showSelected);
                prefEditor.apply();

                MyQRCode.scan(this);

            } else {
                Constants.showToast(Constants.NO_SELECTED_SHOW, this);
            }
        } else {
            Constants.showToast(Constants.NO_SELECTED_SHOW, this);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String jsonResult = MyQRCode.onScanResult(requestCode, resultCode, data);
            validateTickets(jsonResult);
        }
    }

    public void validateTickets(String data) {
        try {
            JSONObject response = new JSONObject(data);
            userId = response.getString("id");
            showId = response.getInt("showid");

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            int selected = prefs.getInt("SELECTED_SHOW", 1);

            if (showId != selected) {
                Constants.showToast(Constants.INVALID_SHOW, this);
            } else {

                tickets = new ArrayList<>();
                JSONArray ticketsJson = response.getJSONArray("tickets");

                for (int i = 0; i < ticketsJson.length(); i++) {
                    Ticket ticket = new Ticket(ticketsJson.get(i).toString());
                    tickets.add(ticket);
                }

                ticketsAPI = new GetTickets(this, userId);
                Thread thrTickets = new Thread(ticketsAPI);
                thrTickets.start();
            }
        } catch(JSONException e) {
            Log.e("ERROR", e.getMessage());
        }

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

    public void handleResponseTickets(int code, String response, ArrayList<Ticket> allTickets) {
        if (code == Constants.OK_RESPONSE) {
            ArrayList<Ticket> completeTickets = new ArrayList<>();

            for(Ticket t : tickets) {
                if (allTickets.indexOf(t) > -1) {
                    Ticket ticket = allTickets.get(allTickets.indexOf(t));
                    completeTickets.add(ticket);
                }
            }

            Intent intent = new Intent(getApplicationContext(), TicketsActivity.class);

            Bundle argument = new Bundle();

            argument.putString(Constants.USER_ID, userId);
            argument.putInt(Constants.SHOW_ID, showId);
            argument.putSerializable(Constants.VALIDATE_TICKETS, completeTickets);

            intent.putExtras(argument);
            startActivity(intent);
            finish();
        } else {
            // show error response
            Constants.showToast(response, this);
        }
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
     * Notifies shows' adapter of new changes to shows' array
     */
    public void notifyShowsAdapter() {
        ServiceHandler sh = new ServiceHandler();
        sh.run();
    }
}
