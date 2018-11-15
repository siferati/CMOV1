package org.feup.cmov.customerapp.cafeteria;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Voucher;
import org.feup.cmov.customerapp.database.LocalDatabase;
import org.feup.cmov.customerapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class VouchersActivity extends AppCompatActivity {

    // vouchers list
    List<Voucher> vouchers = new ArrayList<>();

    // adapter to tickets' list
    ArrayAdapter<Voucher> voucherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vouchers);

        setVouchers();
    }

    /**
     * Show empty message if there's no vouhcers
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty_vouchers);

        ListView list = findViewById(R.id.list_vouchers);
        list.setEmptyView(empty);
    }

    private void setVouchers() {
        ListView list_tickets = findViewById(R.id.list_vouchers);
        voucherAdapter = new VoucherAdapter(this, vouchers);
        list_tickets.setAdapter(voucherAdapter);

        loadVouchersDatabase();
    }

    public void loadVouchersDatabase() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LocalDatabase db = LocalDatabase.getInstance(getApplicationContext());

                if (LocalDatabase.checkDataBase(getApplicationContext())) {
                    List<Voucher> localVouchers = db.getAllVouchers(getApplicationContext());

                    int free_popcorn = Voucher.getQuantityFromList(Constants.FREE_POPCORN, localVouchers);
                    int free_coffee = Voucher.getQuantityFromList(Constants.FREE_COFFEE, localVouchers);
                    int discount = Voucher.getQuantityFromList(Constants.DISCOUNT, localVouchers);

                    List<Voucher> vouchers = new ArrayList<>();

                    if (free_popcorn > 0) {
                        Voucher v = new Voucher(Constants.FREE_POPCORN, free_popcorn);
                        vouchers.add(v);
                    }

                    if (free_coffee > 0) {
                        Voucher v = new Voucher(Constants.FREE_COFFEE, free_coffee);
                        vouchers.add(v);
                    }

                    if (discount > 0) {
                        Voucher v = new Voucher(Constants.DISCOUNT, discount);
                        vouchers.add(v);
                    }

                    if (vouchers.size() > 0) voucherAdapter.addAll(vouchers);
                }
            }
        });
    }
}
