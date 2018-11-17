package org.feup.cmov.customerapp.transactions;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Order;
import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.dataStructures.Voucher;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        setTabsTransactions();
        updateVouchersAndTickets();
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

    private void updateVouchersAndTickets() {
        User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, getApplicationContext());

        vouchersAPI = new GetVouchers(this, user.getId());
        Thread thrVouchers = new Thread(vouchersAPI);
        thrVouchers.start();

        ticketsAPI = new GetTickets(this, user.getId());
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

    public void handleResponseTickets(int code, String response, List<Ticket> tickets) {
        if (code == Constants.OK_RESPONSE) {
            deleteUsedTickets(tickets, this);
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
