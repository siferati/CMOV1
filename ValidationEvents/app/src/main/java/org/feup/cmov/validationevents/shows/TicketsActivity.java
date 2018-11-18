package org.feup.cmov.validationevents.shows;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.feup.cmov.validationevents.Constants;
import org.feup.cmov.validationevents.MainActivity;
import org.feup.cmov.validationevents.R;
import org.feup.cmov.validationevents.dataStructures.Ticket;
import org.feup.cmov.validationevents.dataStructures.User;
import org.feup.cmov.validationevents.server.GetUser;
import org.feup.cmov.validationevents.server.ValidateTickets;

import java.util.ArrayList;

public class TicketsActivity extends AppCompatActivity {

    // user id
    String userId = "";

    // show id
    int showId = -1;

    // tickets to validate
    ArrayList<Ticket> tickets = new ArrayList<>();

    // valid tickets
    ArrayList<Ticket> validTickets = new ArrayList<>();

    // invalid tickets
    ArrayList<Ticket> invalidTickets = new ArrayList<>();

    // validate tickets api
    ValidateTickets ticketsAPI;

    // adapter to tickets' list
    ArrayAdapter<Ticket> validAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        Bundle argument = getIntent().getExtras();

        tickets = new ArrayList<>();
        if (argument != null) {
            userId = argument.getString(Constants.USER_ID);
            showId = argument.getInt(Constants.SHOW_ID);
            tickets = (ArrayList<Ticket>) argument.getSerializable(Constants.VALIDATE_TICKETS);
        }

        if(tickets.size() > 0) setTitle("Tickets for " + tickets.get(0).getName());

        Button terminateBtn = findViewById(R.id.btn_close);
        terminateBtn.setOnClickListener((View v)->terminate());

        ticketsAPI = new ValidateTickets(this, showId, userId, tickets);
        Thread thr1 = new Thread(ticketsAPI);
        thr1.start();

        GetUser userAPI = new GetUser(this, userId);
        Thread thr2 = new Thread(userAPI);
        thr2.start();
    }

    public void terminate() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Handles response from server
     * @param code - response code from server
     * @param response - response message given by server
     */
    public void handleResponse(int code, String response, ArrayList<Ticket> valid, ArrayList<Ticket> invalid) {
        if (code == Constants.OK_RESPONSE) {
            for(Ticket t : valid) {
                if (tickets.indexOf(t) > -1) {
                    Ticket ticket = tickets.get(tickets.indexOf(t));
                    ticket.setAvailable(true);
                    validTickets.add(ticket);
                }
            }

            for(Ticket t : invalid) {
                if (tickets.indexOf(t) > -1) {
                    Ticket ticket = tickets.get(tickets.indexOf(t));
                    ticket.setAvailable(false);
                    invalidTickets.add(ticket);
                }
            }

            ArrayList<Ticket> ticketList = new ArrayList<>();

            ticketList.addAll(validTickets);
            ticketList.addAll(invalidTickets);

            ListView list_valid_tickets = findViewById(R.id.list_tickets);
            validAdapter = new TicketAdapter(this, ticketList);
            list_valid_tickets.setAdapter(validAdapter);
        } else {
            // show error response
            Constants.showToast(response, this);
        }
    }

    public void handleResponseUser(int code, String response, User user) {
        if (code == Constants.OK_RESPONSE) {
            TextView user_tickets = findViewById(R.id.user);
            String username = user.getUsername() + "'s Tickets";
            user_tickets.setText(username);
        } else {
            // show error response
            Constants.showToast(response, this);
        }
    }
}
