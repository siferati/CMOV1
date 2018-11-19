package org.feup.cmov.customerapp.shows.tickets;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.utils.Constants;
import org.feup.cmov.customerapp.utils.MyQRCode;
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

        ImageView qrQode = findViewById(R.id.qrCodeImageView);
        new Thread(() -> {
            try {
                Bitmap bitmap = MyQRCode.create(ticketsJson, 500);
                runOnUiThread(() -> qrQode.setImageBitmap(bitmap));
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public String getTicketsJson(User user) {

        JSONObject ticketsJson = new JSONObject();

        try {
            ticketsJson.put("id", user.getId());
            ticketsJson.put("size", tickets.size());
            ticketsJson.put("showid", tickets.get(0).getShowId());

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
