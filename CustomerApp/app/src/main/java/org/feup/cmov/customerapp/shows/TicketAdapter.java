package org.feup.cmov.customerapp.shows;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.userOptions.ShowsActivity;

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
        Ticket s = tickets.get(position);
        ((TextView)row.findViewById(R.id.name)).setText(s.getName());                   // fill ticket name

        return (row);
    }
}
