package org.feup.cmov.customerapp.shows.tickets;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.shows.ShowsActivity;

import java.util.List;

public class TicketAdapter extends ArrayAdapter<Ticket> {
    private ShowsActivity activity;
    private List<Ticket> tickets;

    public TicketAdapter(ShowsActivity activity, List<Ticket> tickets) {
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

        CheckBox selected = row.findViewById(R.id.validate_ticket);

        if (!t.isAvailable()) {
            row.setBackgroundColor(activity.getResources().getColor(R.color.iron));
            selected.setClickable(false);

            available.setImageResource(R.drawable.available_not);
        } else {
            selected.setOnCheckedChangeListener((CompoundButton btnView, boolean isCheck)->checkListener(activity, t, isCheck));
            available.setImageResource(R.drawable.available_yes);
        }

        return (row);
    }

    private void checkListener(ShowsActivity activity, Ticket ticket, boolean isChecked) {
        if (isChecked) {
            activity.addTicket(ticket);
        } else {
            activity.removeTicket(ticket);
        }
    }
}
