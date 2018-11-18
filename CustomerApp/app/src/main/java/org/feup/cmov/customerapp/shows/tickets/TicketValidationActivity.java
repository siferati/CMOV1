package org.feup.cmov.customerapp.shows.tickets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.database.ValidateTickets;
import org.feup.cmov.customerapp.utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TicketValidationActivity extends AppCompatActivity {

    // tickets to validate
    ArrayList<Ticket> tickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_validation);

        Bundle argument = getIntent().getExtras();

        tickets = new ArrayList<>();
        if (argument != null) {
            tickets = (ArrayList<Ticket>) argument.getSerializable(Constants.VALIDATE_TICKETS_QR);
        }

        User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, getApplicationContext());

        String ticketsJson = getTicketsJson(user);
        Log.d("jsonstuff", ticketsJson);

        ImageView qr_code = findViewById(R.id.qrCodeImageView);

        ValidateTickets validateTickets = new ValidateTickets(this, tickets.get(0).getShowId(), user.getId(), tickets);
        Thread thr = new Thread(validateTickets);
        thr.start();
    }

    public String getTicketsJson(User user) {

        JSONObject ticketsJson = new JSONObject();

        try {
            ticketsJson.put("id", user.getId());
            ticketsJson.put("tickets_size", tickets.size());
            ticketsJson.put("date", tickets.get(0).getDate());

            JSONArray ticketsId = new JSONArray();
            for (int i = 0; i < tickets.size(); i++)
            {
                ticketsId.put(tickets.get(i).getId());
            }
            ticketsJson.put("tickets", ticketsId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ticketsJson.toString();
    }

    /*@Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("Cancel Ticket Validation")
                .setMessage("Are you sure?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // The user wants to leave - so dismiss the dialog and exit
                        finish();
                        dialog.dismiss();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // The user is not sure, so you can exit or just stay
                dialog.dismiss();
            }
        }).show();
    }*/
}
