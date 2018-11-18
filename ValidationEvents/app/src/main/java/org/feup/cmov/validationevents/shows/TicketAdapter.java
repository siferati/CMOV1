package org.feup.cmov.validationevents.shows;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.feup.cmov.validationevents.R;
import org.feup.cmov.validationevents.dataStructures.Ticket;

import java.util.ArrayList;

public class TicketAdapter extends ArrayAdapter<Ticket> {
    private TicketsActivity activity;
    private ArrayList<Ticket> tickets;

    public TicketAdapter(TicketsActivity activity, ArrayList<Ticket> tickets) {
        super(activity, R.layout.row_ticket, tickets);

        this.activity = activity;
        this.tickets = tickets;
    }

    @Override
    public @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.row_ticket, parent, false);    // get our custom layout
        }
        Ticket t = tickets.get(position);
        ((TextView)row.findViewById(R.id.ticket_name)).setText(t.getName());                   // fill ticket name
        ((TextView)row.findViewById(R.id.ticket_date)).setText(t.getDate());                   // fill ticket date

        String price = t.getPrice()  + " €";
        ((TextView)row.findViewById(R.id.ticket_price)).setText(price);

        String seat = "Seat: " + t.getSeatNumber();
        ((TextView)row.findViewById(R.id.ticket_seatNumber)).setText(seat);

        ImageView available = row.findViewById(R.id.available);
        if (!t.isAvailable()) {
            row.setBackgroundColor(activity.getResources().getColor(R.color.iron));
            available.setImageResource(R.drawable.available_not);
        } else {
            row.setBackgroundColor(activity.getResources().getColor(R.color.white));
            available.setImageResource(R.drawable.available_yes);
        }

        return (row);
    }
}
