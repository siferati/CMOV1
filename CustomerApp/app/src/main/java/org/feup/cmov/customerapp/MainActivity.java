package org.feup.cmov.customerapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.feup.cmov.customerapp.utils.FontManager;

public class MainActivity extends AppCompatActivity {
    TextView shows_icon;
    TextView shows_text;

    TextView cafeteria_icon;
    TextView cafeteria_text;

    TextView vouchers_icon;
    TextView vouchers_text;

    TextView history_icon;
    TextView history_text;

    TextView settings_icon;
    TextView settings_text;

    TextView logout_icon;
    TextView logout_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.findViews();
        this.setIcons();
        this.setOnClickListeners();
    }

    private void findViews() {
        this.shows_icon = findViewById(R.id.shows_icon);
        this.shows_text = findViewById(R.id.shows);

        this.cafeteria_icon = findViewById(R.id.cafeteria_icon);
        this.cafeteria_text = findViewById(R.id.cafeteria);

        this.vouchers_icon = findViewById(R.id.vouchers_icon);
        this.vouchers_text = findViewById(R.id.vouchers);

        this.history_icon = findViewById(R.id.history_icon);
        this.history_text = findViewById(R.id.history);

        this.settings_icon = findViewById(R.id.settings_icon);
        this.settings_text = findViewById(R.id.settings);

        this.logout_icon = findViewById(R.id.logout_icon);
        this.logout_text = findViewById(R.id.logout);
    }

    private void setIcons() {
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);

        this.shows_icon.setTypeface(iconFont);
        this.cafeteria_icon.setTypeface(iconFont);
        this.vouchers_icon.setTypeface(iconFont);
        this.history_icon.setTypeface(iconFont);
        this.settings_icon.setTypeface(iconFont);
        this.logout_icon.setTypeface(iconFont);
    }

    private void setOnClickListeners() {
        this.shows_icon.setOnClickListener((View v)->init_shows());
        this.shows_text.setOnClickListener((View v)->init_shows());

        this.cafeteria_icon.setOnClickListener((View v)->init_cafeteria());
        this.cafeteria_text.setOnClickListener((View v)->init_cafeteria());

        this.history_icon.setOnClickListener((View v)->init_history());
        this.history_text.setOnClickListener((View v)->init_history());

        this.settings_icon.setOnClickListener((View v)->init_settings());
        this.settings_text.setOnClickListener((View v)->init_settings());
    }

    private void init_shows() {
        Intent intent = new Intent(this, Shows.class);
        startActivity(intent);
    }

    private void init_cafeteria() {
        Intent intent = new Intent(this, Cafeteria.class);
        startActivity(intent);
    }

    private void init_history() {
        Intent intent = new Intent(this, Transactions.class);
        startActivity(intent);
    }

    private void init_settings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}
