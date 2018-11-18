package org.feup.cmov.validationevents.shows;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import org.feup.cmov.validationevents.Constants;
import org.feup.cmov.validationevents.R;
import org.feup.cmov.validationevents.dataStructures.Show;
import org.feup.cmov.validationevents.server.GetShows;

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

                Show show = showsAdapter.getItem(selectedShow);
                Constants.showToast(show.getDate() + " " + show.getName() + " " + show.getDescription(), this);

            } else {
                Constants.showToast(Constants.NO_SELECTED_SHOW, this);
            }
        } else {
            Constants.showToast(Constants.NO_SELECTED_SHOW, this);
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
