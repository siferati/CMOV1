package org.feup.cmov.customerapp.shows.tickets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Ticket;

import java.util.ArrayList;

public class ValidateTicketsDialog extends Dialog {
    /**
     * Called when dialog is closed
     */
    public interface MyDialogCloseListener
    {
        /**
         * After confirming tickets to validate, go back to shows activity
         */
        void handleValidateTickets();
    }

    // interface object
    private MyDialogCloseListener dialogListener;

    // parent context
    private Context context;

    // tickets to validate
    private ArrayList<Ticket> tickets;

    // adapter to tickets' list
    ArrayAdapter<Ticket> ticketsAdapter;

    private FragmentActivity fragmentActivity;

    public ValidateTicketsDialog(Context context, ArrayList<Ticket> tickets, FragmentActivity fragmentActivity) {
        super(context);

        this.context = context;
        this.tickets = tickets;
        this.fragmentActivity = fragmentActivity;

        setTitle(R.string.confirm_validation);
        setOwnerActivity((Activity) context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_validate_tickets);

        setTickets();

        // getOwnerActivity() returns the Activity that owns this Dialog
        dialogListener = (MyDialogCloseListener) getOwnerActivity();

        Button closeBtn = findViewById(R.id.btn_close_validate);
        Button validateBtn = findViewById(R.id.btn_validate_confirm);

        // sets click listener on close button
        closeBtn.setOnClickListener((View v)->dismiss());

        // sets click listener on validate button
        validateBtn.setOnClickListener((View v)->validate());

    }

    private void setTickets() {
        ListView list_tickets = findViewById(R.id.list_tickets_validate);
        ticketsAdapter = new ValidateTicketAdapter(getContext(), tickets);
        list_tickets.setAdapter(ticketsAdapter);
    }

    private void validate() {
        dismiss();
        dialogListener.handleValidateTickets();
    }
}
