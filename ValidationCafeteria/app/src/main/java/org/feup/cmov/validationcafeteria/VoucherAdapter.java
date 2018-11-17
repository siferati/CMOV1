package org.feup.cmov.validationcafeteria;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.feup.cmov.validationcafeteria.dataStructures.Voucher;

import java.util.ArrayList;

public class VoucherAdapter extends ArrayAdapter<Voucher> {
    private ArrayList<Voucher> vouchers;

    public VoucherAdapter(Context context, ArrayList<Voucher> vouchers) {
        super(context, R.layout.row_voucher, vouchers);
        this.vouchers = vouchers;
    }

    @Override
    public @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = LayoutInflater.from(this.getContext()).inflate(R.layout.row_voucher, parent, false);    // get our custom layout
        }

        Voucher v = vouchers.get(position);

        String type = v.getType();
        ((TextView)row.findViewById(R.id.voucher_type)).setText(v.getName(type));                   // fill voucher name
        ((TextView)row.findViewById(R.id.voucher_description)).setText(v.getDescription(type));     // fill voucher description

        ImageView symbol = row.findViewById(R.id.voucher_symbol);

        if (v.getType().equals(Constants.FREE_POPCORN))
            symbol.setImageResource(R.drawable.voucher_popcorn);
        else if (v.getType().equals(Constants.FREE_COFFEE))
            symbol.setImageResource(R.drawable.voucher_coffee);
        else
            symbol.setImageResource((R.drawable.voucher_discount));

        return (row);
    }

}
