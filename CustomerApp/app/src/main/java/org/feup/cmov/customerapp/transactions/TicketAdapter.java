package org.feup.cmov.customerapp.transactions;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Ticket;

import java.util.ArrayList;

public class TicketAdapter extends ArrayAdapter<Ticket> {
    private TransactionsActivity activity;
    private ArrayList<Ticket> tickets;

    public TicketAdapter(TransactionsActivity activity, ArrayList<Ticket> tickets) {
        super(activity, R.layout.row_ticket_transaction, tickets);

        this.activity = activity;
        this.tickets = tickets;
    }

    @Override
    public @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.row_ticket_transaction, parent, false);            // get our custom layout
        }
        Ticket t = tickets.get(position);
        ((TextView)row.findViewById(R.id.ticket_name_trans)).setText(t.getName());                   // fill ticket name
        ((TextView)row.findViewById(R.id.ticket_date_trans)).setText(t.getDate());                   // fill ticket date

        String price = t.getPrice()  + " €";
        ((TextView)row.findViewById(R.id.ticket_price_trans)).setText(price);

        String seat = "Seat: " + t.getSeatNumber();
        ((TextView)row.findViewById(R.id.ticket_seatNumber_trans)).setText(seat);

        return (row);
    }

    @Override
    public void add(Ticket item) {
        tickets.add(item);
        notifyDataSetChanged();
    }
}
