package org.feup.cmov.validationcafeteria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button initBtn = findViewById(R.id.btn_init);
        initBtn.setOnClickListener((View v)->initScan());
    }

    private void initScan() {
        Intent intent = new Intent(this, OrderActivity.class);
        startActivity(intent);
    }
}
