package org.feup.cmov.customerapp.shows;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.ConfirmPurchase;

public class ConfirmPurchaseDialog extends Dialog {
    /**
     * Called when dialog is closed
     */
    public interface MyDialogCloseListener
    {
        /**
         * After confirming purchase, go back to shows activity
         */
        void handleDialogClose();
    }

    // interface object
    private MyDialogCloseListener dialogListener;

    // parent context
    private Context context;

    // confirm purchase data
    private ConfirmPurchase confirmPurchase;

    public ConfirmPurchaseDialog(Context context, ConfirmPurchase confirmPurchase) {
        super(context);

        this.context = context;
        this.confirmPurchase = confirmPurchase;

        setTitle(R.string.confirm_purchase);
        setOwnerActivity((Activity) context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_confirm_purchase);

        setData();

        Button confirm = findViewById(R.id.btn_buy_CP);
        Button closeBtn = findViewById(R.id.btn_close_CP);

        // getOwnerActivity() returns the Activity that owns this Dialog
        dialogListener = (MyDialogCloseListener) getOwnerActivity();

        // sets click listener on save button
        confirm.setOnClickListener((View v)->confirm());

        // sets click listener on close button
        closeBtn.setOnClickListener((View v)->dismiss());

    }

    private void setData() {
        TextView title = findViewById(R.id.show_name_CP);
        TextView noTickets = findViewById(R.id.buy_no_tickets);
        TextView totalPrice = findViewById(R.id.total_price);

        title.setText(confirmPurchase.name);

        String quantity = confirmPurchase.quantity + "";
        noTickets.setText(quantity);

        String priceText = confirmPurchase.getTotalPrice() + " €";
        totalPrice.setText(priceText);
    }

    private void confirm() {
        dismiss();
        dialogListener.handleDialogClose();
    }
}
