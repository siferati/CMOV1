package org.feup.cmov.customerapp.userOptions;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Show;
import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.database.GetShows;
import org.feup.cmov.customerapp.shows.ShowAdapter;
import org.feup.cmov.customerapp.shows.TicketAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShowsActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    List<Show> shows = new ArrayList<>();
    public ArrayAdapter<Show> showsAdapter;
    List<Ticket> tickets = new ArrayList<>();
    ArrayAdapter<Ticket> ticketsAdapter;

    TabLayout.Tab listShowsTab, boughtTicketsTab;
    View tab1, tab2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shows);

        GetShows getShows = new GetShows(this);
        Thread thr = new Thread(getShows);
        thr.start();

        setTicketsList();
        setTabs();
    }

    public void setShowsList() {
        ListView list_shows = findViewById(R.id.list_shows);
        showsAdapter = new ShowAdapter(this, shows);
        list_shows.setAdapter(showsAdapter);
        //list_shows.setOnItemClickListener(this);

        tab1 = list_shows;
    }

    public void setTicketsList() {
        ListView list_tickets = findViewById(R.id.list_tickets);
        ticketsAdapter = new TicketAdapter(this, tickets);
        list_tickets.setAdapter(ticketsAdapter);

        tab2 = findViewById(R.id.tickets);
    }

    public void setTabs() {
        TabLayout tabs = findViewById(R.id.tabs);
        listShowsTab = tabs.newTab().setText("Shows").setIcon(R.drawable.calendar);
        tabs.addTab(listShowsTab);
        boughtTicketsTab = tabs.newTab().setText("Validate").setIcon(R.drawable.check);
        tabs.addTab(boughtTicketsTab);
        tabs.addOnTabSelectedListener(this);
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;

        setShowsList();
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
