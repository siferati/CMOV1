package org.feup.cmov.customerapp.shows;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Show;
import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.database.BuyTickets;
import org.feup.cmov.customerapp.userOptions.ShowsActivity;
import org.feup.cmov.customerapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends AppCompatActivity implements ConfirmPurchaseDialog.MyDialogCloseListener{

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

    private void setTickets() {
        numberTickets = findViewById(R.id.number_tickets);
        TextView increaseTickets = findViewById(R.id.increase);
        TextView decreaseTickets = findViewById(R.id.decrease);

        increaseTickets.setOnClickListener((View v)->increaseTickets());
        decreaseTickets.setOnClickListener((View v)->decreaseTickets());
    }

    private void confirmPurchase() {
        if (tickets > 0) {
            ConfirmPurchaseFragment dialog = ConfirmPurchaseFragment.constructor(show.getName(), tickets, show.getPrice());
            dialog.show(getSupportFragmentManager(), "confirm_purchase");
        } else {
            showToast(Constants.BUY_FAILED);
        }
    }

    private void buyTickets() {
        User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, getApplicationContext());

        BuyTickets buyTicketsAPI = new BuyTickets(this, show.getId(), user, tickets);
        Thread thr = new Thread(buyTicketsAPI);
        thr.start();
    }

    private void decreaseTickets() {
        if (tickets > 0) {
            tickets--;

            String no_tickets = Integer.toString(tickets);
            numberTickets.setText(no_tickets);
        } else {
            showToast(Constants.DECREASE_FAILED);
        }
    }

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
     */
    public void handleResponse(int code, String response, ArrayList<Ticket> tickets) {
        if (code == Constants.OK_RESPONSE) {
            saveTickets(tickets);

            /*Intent intent = new Intent(this, ShowsActivity.class);
            startActivity(intent);*/
        } else {
            // show error response
            showToast(response);
        }
    }

    private void saveTickets(ArrayList<Ticket> tickets) {
        // save tickets locally and add them to tickets adapter

        Intent intent = new Intent();
        intent.putExtra("tickets", tickets);
        setResult(RESULT_OK, intent);
        finish();

        /*for(int i = 0; i < tickets.size(); i++) {
            Ticket t = tickets.get(i);
            Log.d("http", t.getId() + " " + t.getSeatNumber() + " " + t.getName());
        }*/
    }

    /**
     * Shows toast message
     * @param toast - message to show
     */
    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(ShowActivity.this, toast, Toast.LENGTH_LONG).show());
    }

    @Override
    public void handleDialogClose() {
        showToast(Constants.BUYING_TICKETS);
        buyTickets();
    }
}
