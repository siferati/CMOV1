package org.feup.cmov.customerapp.cafeteria;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.feup.cmov.customerapp.R;
import org.feup.cmov.customerapp.dataStructures.Product;

import java.util.ArrayList;
import java.util.List;

public class CafeteriaActivity extends AppCompatActivity {

    // tickets list
    List<Product> products = new ArrayList<>();

    // adapter to products' list
    public ArrayAdapter<Product> productsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafeteria);
    }

    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(CafeteriaActivity.this, toast, Toast.LENGTH_LONG).show());
    }
}
