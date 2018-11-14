package org.feup.cmov.customerapp.cafeteria;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Voucher;

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

        return (row);
    }
}