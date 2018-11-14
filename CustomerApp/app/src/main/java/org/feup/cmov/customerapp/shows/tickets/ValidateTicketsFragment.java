package org.feup.cmov.customerapp.shows.tickets;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.utils.Constants;

import java.util.ArrayList;

public class ValidateTicketsFragment extends DialogFragment {

    public static ValidateTicketsFragment constructor(ArrayList<Ticket> ticketList) {
        ValidateTicketsFragment fragment = new ValidateTicketsFragment();

        Bundle argument = new Bundle();
        argument.putSerializable(Constants.VALIDATE_TICKETS, ticketList);

        fragment.setArguments(argument);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ArrayList<Ticket> tickets = (ArrayList<Ticket>) getArguments().getSerializable(Constants.VALIDATE_TICKETS);

        return new ValidateTicketsDialog(getContext(), tickets, getActivity());
    }

}
