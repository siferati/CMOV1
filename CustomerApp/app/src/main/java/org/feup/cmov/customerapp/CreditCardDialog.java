package org.feup.cmov.customerapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.feup.cmov.customerapp.dataStructures.CreditCard;

import java.util.ArrayList;
import java.util.Calendar;

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

        populateTypeSpinner(R.id.type_card);
        populateYearsSpinner(R.id.year_date);
        populateMonthSpinner(R.id.month_date);
    }

    private void populateTypeSpinner(int type_id) {
        Spinner spinner = findViewById(type_id);
        ArrayAdapter<CharSequence> type_adapter = ArrayAdapter.createFromResource(context, R.array.type_cards_array, android.R.layout.simple_spinner_item);  // Create an ArrayAdapter using the string array and a default spinner layout
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);                                                                 // Specify the layout to use when the list of choices appears
        spinner.setAdapter(type_adapter);                                                                                                               // Apply the adapter to the spinner
    }

    private void populateYearsSpinner(int years_id) {
        ArrayList<String> years = new ArrayList<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        int limitYear = thisYear + 50;
        for (int i = thisYear; i <= limitYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> years_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, years);

        Spinner spinYear = findViewById(years_id);
        spinYear.setAdapter(years_adapter);
    }

    private void populateMonthSpinner(int months_id) {
        ArrayList<String> months = new ArrayList<>();

        int limitMonth = 12;
        for (int i = 1; i <= limitMonth; i++) {
            months.add(Integer.toString(i));
        }
        ArrayAdapter<String> years_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, months);

        Spinner spinYear = findViewById(months_id);
        spinYear.setAdapter(years_adapter);
    }
}
