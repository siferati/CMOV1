package org.feup.cmov.customerapp.cafeteria;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Voucher;
import org.feup.cmov.customerapp.utils.Constants;

import java.util.List;

public class VoucherAdapter extends ArrayAdapter<Voucher> {
    public VouchersActivity activity;
    public List<Voucher> vouchers;

    public VoucherAdapter(VouchersActivity activity, List<Voucher> vouchers) {
        super(activity, R.layout.row_voucher, vouchers);

        this.activity = activity;
        this.vouchers = vouchers;
    }

    @Override
    public @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.row_voucher, parent, false);    // get our custom layout
        }
        Voucher v = vouchers.get(position);

        String type = v.getType();
        ((TextView)row.findViewById(R.id.voucher_type)).setText(v.getName(type));               // fill voucher name
        ((TextView)row.findViewById(R.id.voucher_description)).setText(v.getDescription(type)); // fill voucher description

        String quantity = "QUANTITY: " + v.getQuantity();
        ((TextView)row.findViewById(R.id.voucher_quantity)).setText(quantity);

        ImageView symbol = row.findViewById(R.id.symbol);

        if (v.getType().equals(Constants.FREE_POPCORN))
            symbol.setImageResource(R.drawable.voucher_popcorn);
        else if (v.getType().equals(Constants.FREE_COFFEE))
            symbol.setImageResource(R.drawable.voucher_coffee);
        else
            symbol.setImageResource((R.drawable.voucher_discount));

        return (row);
    }


}