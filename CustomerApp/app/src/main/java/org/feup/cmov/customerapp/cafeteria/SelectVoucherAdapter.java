package org.feup.cmov.customerapp.cafeteria;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Voucher;
import org.feup.cmov.customerapp.utils.Constants;

import java.util.ArrayList;

public class SelectVoucherAdapter extends ArrayAdapter<Voucher> {
    public SelectVoucherActivity activity;
    public ArrayList<Voucher> vouchers;
    boolean[] checkedStates;

    SelectVoucherAdapter (SelectVoucherActivity activity, ArrayList<Voucher> vouchers, ArrayList<Voucher> selectedVouchers) {
        super(activity, R.layout.row_voucher, vouchers);

        this.activity = activity;
        this.vouchers = vouchers;
        checkStates(selectedVouchers);
    }

    private void checkStates(ArrayList<Voucher> vs) {
        checkedStates = new boolean[vouchers.size()];

        for(int i = 0; i < vouchers.size(); i++) {
            if (vs.contains(vouchers.get(i))) {
                checkedStates[i] = true;
            }
        }
    }

    @Override
    public @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.row_select_voucher, parent, false);
        }
        Voucher v = vouchers.get(position);

        String type = v.getType();
        ((TextView)row.findViewById(R.id.select_voucher_type)).setText(v.getName(type));               // fill voucher name
        ((TextView)row.findViewById(R.id.select_voucher_description)).setText(v.getDescription(type)); // fill voucher description

        ImageView symbol = row.findViewById(R.id.voucher_symbol);

        if (v.getType().equals(Constants.FREE_POPCORN))
            symbol.setImageResource(R.drawable.voucher_popcorn);
        else if (v.getType().equals(Constants.FREE_COFFEE))
            symbol.setImageResource(R.drawable.voucher_coffee);
        else
            symbol.setImageResource((R.drawable.voucher_discount));

        CheckBox selected = row.findViewById(R.id.select_voucher);
        selected.setChecked(checkedStates[position]);

        selected.setOnCheckedChangeListener((CompoundButton btnView, boolean isCheck)->checkListener(position, activity, v, isCheck));

        return (row);
    }

    private void checkListener(int position, SelectVoucherActivity activity, Voucher voucher, boolean isChecked) {
        checkedStates[position] = !checkedStates[position];

        if (isChecked) {
            activity.addVoucher(voucher);
        } else {
            activity.removeVoucher(voucher);
        }
    }

}
