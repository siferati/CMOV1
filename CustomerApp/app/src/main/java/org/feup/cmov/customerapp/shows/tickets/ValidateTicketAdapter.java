package org.feup.cmov.customerapp.shows.tickets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Ticket;

import java.util.ArrayList;

public class ValidateTicketAdapter extends ArrayAdapter<Ticket> {
    private ArrayList<Ticket> tickets;

    public ValidateTicketAdapter(Context context, ArrayList<Ticket> tickets) {
        super(context, R.layout.row_validate_ticket, tickets);
        this.tickets = tickets;
    }

    @Override
    public @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = LayoutInflater.from(this.getContext()).inflate(R.layout.row_validate_ticket, parent, false);    // get our custom layout
        }
        Ticket t = tickets.get(position);

        ((TextView)row.findViewById(R.id.ticket_name_validate)).setText(t.getName());                   // fill ticket name
        ((TextView)row.findViewById(R.id.ticket_date_validate)).setText(t.getDate());                   // fill ticket date

        String price = t.getPrice()  + " €";
        ((TextView)row.findViewById(R.id.ticket_price_validate)).setText(price);

        String seat = "Seat: " + t.getSeatNumber();
        ((TextView)row.findViewById(R.id.ticket_seatNumber_validate)).setText(seat);

        return (row);
    }
}
