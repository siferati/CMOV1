package org.feup.cmov.customerapp.transactions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.dataStructures.Voucher;
import org.feup.cmov.customerapp.database.GetTickets;
import org.feup.cmov.customerapp.database.GetVouchers;
import org.feup.cmov.customerapp.database.LocalDatabase;
import org.feup.cmov.customerapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    // API to get available vouchers from server
    GetVouchers vouchersAPI;

    // API to get available tickets from server
    GetTickets ticketsAPI;

    // available vouchers
    List<Voucher> availableVouchers;

    // used tickets
    List<Ticket> unavailableTickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

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
            availableVouchers = vouchers;

        } else {
            Constants.showToast(response, this);
        }
    }

    public void handleResponseTickets(int code, String response, List<Ticket> tickets) {
        if (code == Constants.OK_RESPONSE) {
            unavailableTickets = tickets;
            deleteUsedTickets(unavailableTickets);
        } else {
            Constants.showToast(response, this);
        }
    }

    public void deleteUsedTickets(List<Ticket> tickets) {
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
            }
        });
    }

}
