package org.feup.cmov.customerapp.shows;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Show;
import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.dataStructures.Voucher;
import org.feup.cmov.customerapp.database.BuyTickets;
import org.feup.cmov.customerapp.database.LocalDatabase;
import org.feup.cmov.customerapp.shows.tickets.ConfirmPurchaseDialog;
import org.feup.cmov.customerapp.shows.tickets.ConfirmPurchaseFragment;
import org.feup.cmov.customerapp.shows.tickets.LocalLoginDialog;
import org.feup.cmov.customerapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends AppCompatActivity implements ConfirmPurchaseDialog.MyDialogCloseListener, LocalLoginDialog.MyDialogCloseListener {

    // show to be shown on the dialog
    private Show show;

    // layout's number of tickets
    private TextView numberTickets;

    // number of tickets
    private int tickets = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        Bundle b = getIntent().getExtras();
        if(b != null)
            show = (Show) b.getSerializable(Constants.GET_SHOW);

        setShow();
        setTickets();

        Button buyBtn = findViewById(R.id.btn_buy);

        // sets click listener on buy tickets button
        buyBtn.setOnClickListener((View v)->confirmPurchase());
    }

    /**
     * Set show to display on this activity
     */
    private void setShow() {
        TextView name = findViewById(R.id.show_name);
        name.setText(show.getName());

        TextView description = findViewById(R.id.show_description);
        description.setText(show.getDescription());

        TextView date = findViewById(R.id.show_date);
        date.setText(show.getDate());

        TextView price = findViewById(R.id.show_price);
        String priceText = show.getPrice() + " €";
        price.setText(priceText);
    }

    /**
     * Set listeners for text views that increase or decrease number of tickets to buy
     */
    private void setTickets() {
        numberTickets = findViewById(R.id.number_tickets);
        TextView increaseTickets = findViewById(R.id.increase);
        TextView decreaseTickets = findViewById(R.id.decrease);

        increaseTickets.setOnClickListener((View v)->increaseTickets());
        decreaseTickets.setOnClickListener((View v)->decreaseTickets());
    }

    /**
     * If user tries to buy tickets, a confirm purchase dialog appears
     */
    private void confirmPurchase() {
        if (tickets > 0) {
            ConfirmPurchaseFragment dialog = ConfirmPurchaseFragment.constructor(show.getName(), tickets, show.getPrice());
            dialog.show(getSupportFragmentManager(), "confirm_purchase");
        } else {
            showToast(Constants.BUY_FAILED);
        }
    }

    /**
     * Decrease number of tickets to buy
     */
    private void decreaseTickets() {
        if (tickets > 0) {
            tickets--;

            String no_tickets = Integer.toString(tickets);
            numberTickets.setText(no_tickets);
        } else {
            showToast(Constants.DECREASE_FAILED);
        }
    }

    /**
     * Increase number of tickets to buy
     */
    private void increaseTickets() {
        tickets++;

        String no_tickets = Integer.toString(tickets);
        numberTickets.setText(no_tickets);
    }

    /**
     * Handles response from server
     * @param code - response code from server
     * @param response - response message given by server
     * @param tickets - list of tickets received from server
     * @param vouchers - list of vouchers received from server
     */
    public void handleResponse(int code, String response, ArrayList<Ticket> tickets, ArrayList<Voucher> vouchers) {
        if (code == Constants.OK_RESPONSE) {
            saveTicketsAndVouchers(tickets, vouchers);
        } else {
            // show error response
            showToast(response);
        }
    }

    /**
     * Save tickets and vouchers received from server
     * @param tickets - list of tickets
     * @param vouchers - list of vouchers
     */
    private void saveTicketsAndVouchers(ArrayList<Ticket> tickets, ArrayList<Voucher> vouchers) {
        saveVouchersDatabase(vouchers);

        // save tickets locally and add them to tickets adapter
        Intent intent = new Intent();
        intent.putExtra("tickets", tickets);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Saves vouchers to the local database
     * @param vouchers - vouchers to save
     */
    private void saveVouchersDatabase(ArrayList<Voucher> vouchers) {
        String no_vouchers = "You got " + vouchers.size() + " free vouchers!";
        showToast(no_vouchers);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LocalDatabase db = LocalDatabase.getInstance(getApplicationContext());

                for(int i = 0; i < vouchers.size(); i++) {
                    db.addVoucher(getApplicationContext(), vouchers.get(i));
                }
            }
        });
    }

    /**
     * Shows toast message
     * @param toast - message to show
     */
    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(ShowActivity.this, toast, Toast.LENGTH_LONG).show());
    }

    /**
     * Call fragment to initiate local user authentication
     */
    @Override
    public void handleDialogClose() {
        LocalLoginDialog dialog = new LocalLoginDialog(ShowActivity.this, this);
        dialog.show();
    }

    /**
     * Return from local user authentication and buy tickets
     */
    @Override
    public void handleLocalLogin() {
        showToast(Constants.BUYING_TICKETS);
        buyTickets();
    }

    /**
     * Connect to server to buy tickets
     */
    private void buyTickets() {
        User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, getApplicationContext());

        BuyTickets buyTicketsAPI = new BuyTickets(this, show.getId(), user, tickets);
        Thread thr = new Thread(buyTicketsAPI);
        thr.start();
    }
}
