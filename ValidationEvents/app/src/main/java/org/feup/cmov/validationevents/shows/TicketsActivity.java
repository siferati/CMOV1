package org.feup.cmov.validationevents.shows;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.feup.cmov.validationevents.server.GetTickets;
import org.feup.cmov.validationevents.utils.Constants;
import org.feup.cmov.validationevents.MainActivity;
import org.feup.cmov.validationevents.R;
import org.feup.cmov.validationevents.dataStructures.Ticket;
import org.feup.cmov.validationevents.dataStructures.User;
import org.feup.cmov.validationevents.server.GetUser;
import org.feup.cmov.validationevents.server.ValidateTickets;
import org.feup.cmov.validationevents.utils.MyQRCode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    ArrayAdapter<Ticket> ticketsAdapter;

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

        Button same = findViewById(R.id.btn_again);
        same.setOnClickListener((View v)->sameShow());

        Button terminateBtn = findViewById(R.id.btn_close);
        terminateBtn.setOnClickListener((View v)->terminate());

        ArrayList<Ticket> ticketArrayList = new ArrayList<>();
        ListView list_valid_tickets = findViewById(R.id.list_tickets);
        ticketsAdapter = new TicketAdapter(this, ticketArrayList);
        list_valid_tickets.setAdapter(ticketsAdapter);

        ticketsAPI = new ValidateTickets(this, showId, userId, tickets);
        Thread thr1 = new Thread(ticketsAPI);
        thr1.start();

        GetUser userAPI = new GetUser(this, userId);
        Thread thr2 = new Thread(userAPI);
        thr2.start();
    }

    public void terminate() {
        Intent intent = new Intent(getApplicationContext(), ShowsActivity.class);
        startActivity(intent);
        finish();
    }

    public void sameShow() {
        MyQRCode.scan(this);
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

                GetTickets ticketsAPI = new GetTickets(this, userId);
                Thread thrTickets = new Thread(ticketsAPI);
                thrTickets.start();
            }
        } catch(JSONException e) {
            Log.e("ERROR", e.getMessage());
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
     * Handles response from server
     * @param code - response code from server
     * @param response - response message given by server
     */
    public void handleResponse(int code, String response, ArrayList<Ticket> valid, ArrayList<Ticket> invalid) {
        runOnUiThread(() -> {
            if (code == Constants.OK_RESPONSE) {
                for (Ticket t : valid) {
                    if (tickets.indexOf(t) > -1) {
                        Ticket ticket = tickets.get(tickets.indexOf(t));

                        ticket.setAvailable(true);
                        validTickets.add(ticket);
                    }
                }

                for (Ticket t : invalid) {
                    if (tickets.indexOf(t) > -1) {
                        Ticket ticket = tickets.get(tickets.indexOf(t));

                        ticket.setAvailable(false);
                        invalidTickets.add(ticket);
                    }
                }

                ArrayList<Ticket> ticketArrayList = new ArrayList<>(validTickets);
                ticketArrayList.addAll(invalidTickets);

                for (Ticket t : ticketArrayList) {
                    ticketsAdapter.add(t);
                }
            } else {
                // show error response
                Constants.showToast(response, this);
            }
        });
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
