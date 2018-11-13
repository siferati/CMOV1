package org.feup.cmov.customerapp.shows;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.feup.cmov.customerapp.dataStructures.ConfirmPurchase;
import org.feup.cmov.customerapp.utils.Constants;

public class ConfirmPurchaseFragment extends DialogFragment {

    public static ConfirmPurchaseFragment constructor(String name, int quantity, double price) {
        ConfirmPurchaseFragment fragment = new ConfirmPurchaseFragment();

        ConfirmPurchase confirmPurchase = new ConfirmPurchase(name, quantity, price);

        Bundle argument = new Bundle();
        argument.putSerializable(Constants.CONFIRM_PURCHASE, confirmPurchase);

        fragment.setArguments(argument);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ConfirmPurchase confirmPurchase = (ConfirmPurchase) getArguments().getSerializable(Constants.CONFIRM_PURCHASE);
        return new ConfirmPurchaseDialog(getActivity(), confirmPurchase);
    }
}
