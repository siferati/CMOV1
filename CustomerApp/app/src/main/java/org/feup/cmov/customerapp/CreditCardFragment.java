package org.feup.cmov.customerapp;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.feup.cmov.customerapp.dataStructures.CreditCard;

public class CreditCardFragment extends DialogFragment {
    // static constructor
    public static CreditCardFragment constructor(CreditCard card) {
        CreditCardFragment fragment = new CreditCardFragment();

        Bundle argument = new Bundle();
        argument.putSerializable("creditCard", card);
        fragment.setArguments(argument);

        return fragment;
    }

    // Override to build your own custom Dialog container. When doing so, onCreateView does not need to be implemented
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CreditCard card = (CreditCard) getArguments().getSerializable("creditCard");
        return new CreditCardDialog(getActivity(), card);
    }

}
