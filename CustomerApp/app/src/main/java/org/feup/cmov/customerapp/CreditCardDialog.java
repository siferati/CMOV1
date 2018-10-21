package org.feup.cmov.customerapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.feup.cmov.customerapp.dataStructures.CreditCard;

public class CreditCardDialog extends Dialog {
    private Context context;
    private CreditCard card;

    public CreditCardDialog(Context context, CreditCard card) {
        super(context);

        this.context = context;
        this.card = card;

        setTitle(R.string.credit_card);
        setOwnerActivity((Activity) context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_credit_card);

        Spinner spinner = (Spinner) findViewById(R.id.type_card);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }
}
