package org.feup.cmov.validationcafeteria;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.feup.cmov.validationcafeteria.dataStructures.Voucher;

import java.util.ArrayList;

public class SeeVouchersFragment extends DialogFragment {

    public static SeeVouchersFragment constructor(ArrayList<Voucher> vouchersList) {
        SeeVouchersFragment fragment = new SeeVouchersFragment();

        Bundle argument = new Bundle();
        argument.putSerializable(Constants.SEE_VOUCHERS, vouchersList);

        fragment.setArguments(argument);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ArrayList<Voucher> vouchers = (ArrayList<Voucher>) getArguments().getSerializable(Constants.SEE_VOUCHERS);

        return new SeeVouchersDialog(getContext(), vouchers);
    }
}
