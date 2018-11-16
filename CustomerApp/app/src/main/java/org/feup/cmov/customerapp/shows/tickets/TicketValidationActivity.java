package org.feup.cmov.customerapp.shows.tickets;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.utils.Constants;

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
            tickets = (ArrayList<Ticket>) argument.getSerializable(Constants.VALIDATION_QR);
        }

        ImageView qr_code = findViewById(R.id.qrCodeImageView);

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
