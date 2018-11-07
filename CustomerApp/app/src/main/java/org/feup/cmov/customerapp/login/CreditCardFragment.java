package org.feup.cmov.customerapp.login;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.feup.cmov.customerapp.dataStructures.CreditCard;

public class CreditCardFragment extends DialogFragment {
    /**
     * Static constructor of dialog fragment
     * @param card - credit card to be set on the dialog
     * @return credit card fragment
     */
    public static CreditCardFragment constructor(CreditCard card) {
        CreditCardFragment fragment = new CreditCardFragment();     // creates new credit card dialog fragment

        Bundle argument = new Bundle();
        argument.putSerializable("creditCard", card);               // inserts a Serializable value into the mapping of this Bundle, replacing any existing value for the given key

        fragment.setArguments(argument);                            // this allows the card to be saved even after fragment is destroyed

        return fragment;
    }

    /**
     * Creates custom Dialog container
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CreditCard card = (CreditCard) getArguments().getSerializable("creditCard");
        return new CreditCardDialog(getActivity(), card);
    }

}
