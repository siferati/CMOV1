package org.feup.cmov.customerapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.login.LoginActivity;
import org.feup.cmov.customerapp.userOptions.CafeteriaActivity;
import org.feup.cmov.customerapp.userOptions.SettingsActivity;
import org.feup.cmov.customerapp.userOptions.ShowsActivity;
import org.feup.cmov.customerapp.userOptions.TransactionsActivity;
import org.feup.cmov.customerapp.userOptions.VouchersActivity;
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

        User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, getApplicationContext());
        setTitle(user.getUsername());

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

        this.vouchers_icon.setOnClickListener((View v)->init_vouchers());
        this.vouchers_text.setOnClickListener((View v)->init_vouchers());

        this.history_icon.setOnClickListener((View v)->init_history());
        this.history_text.setOnClickListener((View v)->init_history());

        this.settings_icon.setOnClickListener((View v)->init_settings());
        this.settings_text.setOnClickListener((View v)->init_settings());

        this.logout_icon.setOnClickListener((View v)->init_logout());
        this.logout_text.setOnClickListener((View v)->init_logout());
    }

    private void init_shows() {
        Intent intent = new Intent(this, ShowsActivity.class);
        startActivity(intent);
    }

    private void init_cafeteria() {
        Intent intent = new Intent(this, CafeteriaActivity.class);
        startActivity(intent);
    }

    private void init_vouchers() {
        Intent intent = new Intent(this, VouchersActivity.class);
        startActivity(intent);
    }

    private void init_history() {
        Intent intent = new Intent(this, TransactionsActivity.class);
        startActivity(intent);
    }

    private void init_settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void init_logout() {
        User.setLoggedinUser("\n", User.LOGGEDIN_USER_PATH, getApplicationContext());

        Toast.makeText(this, "Logout success", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
