package org.feup.cmov.customerapp.shows;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Show;
import org.feup.cmov.customerapp.userOptions.ShowsActivity;

import java.util.List;

public class ShowAdapter extends ArrayAdapter<Show> {
    private ShowsActivity activity;
    private List<Show> shows;

    public ShowAdapter(ShowsActivity activity, List<Show> shows) {
        super(activity, R.layout.row_show, shows);

        this.activity = activity;
        this.shows = shows;
    }

    @Override
    public @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.row_show, parent, false);    // get our custom layout
        }

        Show s = shows.get(position);
        ((TextView)row.findViewById(R.id.name)).setText(s.getName());      // fill show name
        ((TextView)row.findViewById(R.id.date)).setText(s.getDate());      // fill show address

        return (row);
    }


}
