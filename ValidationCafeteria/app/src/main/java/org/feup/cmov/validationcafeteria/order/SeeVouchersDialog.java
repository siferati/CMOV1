package org.feup.cmov.validationcafeteria.order;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.feup.cmov.validationcafeteria.R;
import org.feup.cmov.validationcafeteria.dataStructures.Voucher;

import java.util.ArrayList;

public class SeeVouchersDialog extends Dialog {

    // vouchers to show
    private ArrayList<Voucher> vouchers;

    // adapter to tickets' list
    ArrayAdapter<Voucher> vouchersAdapter;

    public SeeVouchersDialog(Context context, ArrayList<Voucher> vouchers) {
        super(context);
        this.vouchers = vouchers;

        setTitle(R.string.accepted_vouchers);
        setOwnerActivity((Activity) context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_see_vouchers);

        ListView list_tickets = findViewById(R.id.list_vouchers);
        vouchersAdapter = new VoucherAdapter(getContext(), vouchers);
        list_tickets.setAdapter(vouchersAdapter);

        Button closeBtn = findViewById(R.id.btn_close_frag);

        // sets click listener on close button
        closeBtn.setOnClickListener((View v)->dismiss());
    }


}
