package org.feup.cmov.customerapp.cafeteria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Product;
import org.feup.cmov.customerapp.dataStructures.Voucher;
import org.feup.cmov.customerapp.database.LocalDatabase;
import org.feup.cmov.customerapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class SelectVoucherActivity extends AppCompatActivity {

    ArrayList<Voucher> vouchers = new ArrayList<>();

    ArrayList<Voucher> selectedVouchers = new ArrayList<>();

    // adapter to products' list
    public ArrayAdapter<Voucher> vouchersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_voucher);

        Bundle argument = getIntent().getExtras();
        if (argument != null) {
            selectedVouchers = (ArrayList<Voucher>) argument.getSerializable(Constants.SELECTED_VOUCHERS);
        }

        ListView list_vouchers = findViewById(R.id.list_select_vouchers);
        vouchersAdapter = new SelectVoucherAdapter(this, vouchers);
        list_vouchers.setAdapter(vouchersAdapter);

        loadVouchersDatabase();

        Button selectVouchers = findViewById(R.id.btn_vouchers);
        selectVouchers.setOnClickListener((View v)->selectVouchers());
    }

    /**
     * Show empty message if there's no vouchers
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty_select_vouchers);

        ListView list = findViewById(R.id.list_select_vouchers);
        list.setEmptyView(empty);
    }

    public void loadVouchersDatabase() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LocalDatabase db = LocalDatabase.getInstance(getApplicationContext());

                if (LocalDatabase.checkDataBase(getApplicationContext())) {
                    List<Voucher> localVouchers = db.getAllVouchers(getApplicationContext());

                    if (selectedVouchers.size() > 0) {
                        for (Voucher v : selectedVouchers) {
                            for (Voucher local : localVouchers) {
                                if (v.getId().equals(local.getId())) {
                                    local.selected = true;
                                }
                            }
                        }
                    }

                    if (localVouchers.size() > 0) {
                        vouchersAdapter.addAll(localVouchers);
                    }
                }
            }
        });
    }

    /**
     * Called when select vouchers' button is clicked. Checks if selected vouchers are valid and if they are, returns them to ShoppingCartActivity
     */
    private void selectVouchers() {
        if (selectedVouchers.size() <= 2) {
            if (validVouchers()) {
                Intent intent = new Intent();
                intent.putExtra(Constants.REQUESTED_VOUCHERS, selectedVouchers);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                showToast(Constants.TWO_DISCOUNTS);
            }
        } else {
            showToast(Constants.MAX_VOUCHERS);
        }
    }

    /**
     * Checks if vouchers selected are valid (if there are two discount vouchers, they aren't)
     * @return true of valid vouchers, false if not!
     */
    public boolean validVouchers() {
        boolean valid = true;

        if (selectedVouchers.size() == 2) {
            String typeFirst = selectedVouchers.get(0).getType();
            String typeSecond = selectedVouchers.get(1).getType();

            if (typeFirst.equals(Constants.DISCOUNT) && typeSecond.equals(Constants.DISCOUNT)) {
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Shows toast message
     * @param toast - message to show
     */
    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(SelectVoucherActivity.this, toast, Toast.LENGTH_LONG).show());
    }

    public void addVoucher(Voucher v) {
        selectedVouchers.add(v);
    }

    public void removeVoucher(Voucher v) {
        selectedVouchers.remove(v);
    }
}
