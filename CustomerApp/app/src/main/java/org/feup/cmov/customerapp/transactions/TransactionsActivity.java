package org.feup.cmov.customerapp.transactions;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Order;
import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.dataStructures.Voucher;
import org.feup.cmov.customerapp.database.GetOrders;
import org.feup.cmov.customerapp.database.GetTickets;
import org.feup.cmov.customerapp.database.GetVouchers;
import org.feup.cmov.customerapp.database.LocalDatabase;
import org.feup.cmov.customerapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    // API to get available vouchers from server
    GetVouchers vouchersAPI;

    // API to get available tickets from server
    GetTickets ticketsAPI;

    // API to get available orders from server
    GetOrders ordersAPI;

    // checks if vouchers were updated
    boolean updatedVouchers = false;

    // checks if tickets were updated
    boolean updatedTickets = false;

    // orders list
    ArrayList<Order> orders = new ArrayList<>();

    // adapter to tickets' list
    ArrayAdapter<Order> ordersAdapter;

    // tickets list
    ArrayList<Ticket> tickets = new ArrayList<>();

    // adapter to tickets' list
    ArrayAdapter<Ticket> ticketsAdapter;

    // tabs for orders and bought tickets
    TabLayout.Tab listOrdersTab, boughtTicketsTab;

    // tab1 - orders' list, tab2 - bought tickets' list
    View tab1, tab2;

    // user's id
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        ListView list_tickets = findViewById(R.id.list_tickets_transactions);
        ticketsAdapter = new TicketAdapter(this, tickets);
        list_tickets.setAdapter(ticketsAdapter);

        ListView list_orders = findViewById(R.id.list_orders_transactions);
        ordersAdapter = new OrderAdapter(this, orders);
        list_orders.setAdapter(ordersAdapter);
        list_orders.setOnItemClickListener((AdapterView<?> adapterView, View view, int pos, long id)->showOrder(pos));

        User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, getApplicationContext());
        userId = user.getId();

        setTabsTransactions();
        updateTickets();

        ordersAPI = new GetOrders(this, userId);
        Thread thrOrders = new Thread(ordersAPI);
        thrOrders.start();
    }

    private void showOrder(int pos) {
        Order order = orders.get(pos);

        Intent intent = new Intent(this, OrderActivity.class);
        Bundle argument = new Bundle();

        argument.putSerializable(Constants.SHOW_ORDER, order);

        intent.putExtras(argument);
        startActivity(intent);
    }

    /**
     * Shows empty messages
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty_orders = findViewById(R.id.empty_orders_transactions);
        ListView list_orders = findViewById(R.id.list_orders_transactions);
        list_orders.setEmptyView(empty_orders);

        View empty_tickets = findViewById(R.id.empty_tickets_transactions);
        ListView list_tickets = findViewById(R.id.list_tickets_transactions);
        list_tickets.setEmptyView(empty_tickets);
    }

    private void setTabsTransactions() {
        LinearLayout ordersLayout = findViewById(R.id.orders_transactions);
        LinearLayout ticketsLayout = findViewById(R.id.tickets_transactions);

        tab1 = ordersLayout;
        tab2 = ticketsLayout;

        TabLayout tabs = findViewById(R.id.tabs);
        listOrdersTab = tabs.newTab().setText("Orders").setIcon(R.drawable.cafeteria);
        tabs.addTab(listOrdersTab);
        boughtTicketsTab = tabs.newTab().setText("Tickets").setIcon(R.drawable.calendar);
        tabs.addTab(boughtTicketsTab);
        tabs.addOnTabSelectedListener(this);
    }

    private void updateTickets() {
        Log.d("responsehttp", "here");
        ticketsAPI = new GetTickets(this, userId);
        Thread thrTickets = new Thread(ticketsAPI);
        thrTickets.start();
    }


    public void handleResponseVouchers(int code, String response, List<Voucher> vouchers) {
        if (code == Constants.OK_RESPONSE) {
            addLostVouchers(vouchers, this);
        } else {
            Constants.showToast(response, this);
        }
    }

    public void handleResponseTickets(int code, String response, ArrayList<Ticket> tickets) {
        if (code == Constants.OK_RESPONSE) {
            for(Ticket t : tickets) {
                ticketsAdapter.add(t);
            }
            ticketsAdapter.notifyDataSetChanged();
            deleteUsedTickets(tickets, this);

            vouchersAPI = new GetVouchers(this, userId);
            Thread thrVouchers = new Thread(vouchersAPI);
            thrVouchers.start();
        } else {
            Constants.showToast(response, this);
        }
    }

    public void handleResponseOrders(int code, String response, ArrayList<Order> orders) {
        if (code == Constants.OK_RESPONSE) {
            ordersAdapter.addAll(orders);
            ordersAdapter.notifyDataSetChanged();

            vouchersAPI = new GetVouchers(this, userId);
            Thread thrVouchers = new Thread(vouchersAPI);
            thrVouchers.start();
        } else {
            Constants.showToast(response, this);
        }
    }

    public void deleteUsedTickets(List<Ticket> tickets, TransactionsActivity activity) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LocalDatabase db = LocalDatabase.getInstance(getApplicationContext());

                if (LocalDatabase.checkDataBase(getApplicationContext())) {
                    if(tickets.size() > 0) {
                        for(Ticket t : tickets) {
                            db.deleteTicket(getApplicationContext(), t);
                        }

                        List<Ticket> ticketList = db.getAllTickets(getApplicationContext());

                        for(Ticket t : ticketList) {
                            t.setAvailable(true);
                            db.updateTicket(t);
                        }
                    } else {
                        List<Ticket> ticketList = db.getAllTickets(getApplicationContext());

                        for(Ticket t : ticketList) {
                            t.setAvailable(true);
                            db.updateTicket(t);
                        }
                    }
                }

                updatedTickets = true;

                if (updatedVouchers) {
                    Constants.showToast(Constants.UPDATED_V_T, activity);
                }
            }
        });
    }

    public void addLostVouchers(List<Voucher> vouchers, TransactionsActivity activity) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LocalDatabase db = LocalDatabase.getInstance(getApplicationContext());

                if (LocalDatabase.checkDataBase(getApplicationContext())) {
                    List<Voucher> localVouchers = db.getAllVouchers(getApplicationContext());

                    List<Voucher> lostVouchers = new ArrayList<>(vouchers);
                    lostVouchers.removeAll(localVouchers);

                    if (lostVouchers.size() > 0) {
                        for(Voucher voucher : lostVouchers) {
                            db.addVoucher(getApplicationContext(), voucher);
                        }
                    }
                }

                updatedVouchers = true;
                if (updatedTickets) {
                    Constants.showToast(Constants.UPDATED_V_T, activity);
                }
            }
        });
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
