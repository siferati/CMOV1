package org.feup.cmov.customerapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.login.LoginActivity;
import org.feup.cmov.customerapp.cafeteria.CafeteriaActivity;
import org.feup.cmov.customerapp.transactions.SettingsActivity;
import org.feup.cmov.customerapp.shows.ShowsActivity;
import org.feup.cmov.customerapp.transactions.TransactionsActivity;
import org.feup.cmov.customerapp.cafeteria.VouchersActivity;
import org.feup.cmov.customerapp.utils.Constants;
import org.feup.cmov.customerapp.utils.FontManager;

public class MainActivity extends AppCompatActivity {
    TextView shows_icon;
    LinearLayout shows_layout;

    TextView cafeteria_icon;
    LinearLayout cafeteria_layout;

    TextView vouchers_icon;
    LinearLayout vouchers_layout;

    TextView history_icon;
    LinearLayout history_layout;

    TextView settings_icon;
    LinearLayout settings_layout;

    TextView logout_icon;
    LinearLayout logout_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, getApplicationContext());
        setTitle(user.getUsername() + "'s options");

        this.findViews();
        this.setIcons();
        this.setOnClickListeners();
    }

    private void findViews() {
        this.shows_icon = findViewById(R.id.shows_icon);
        this.shows_layout = findViewById(R.id.shows_layout);

        this.cafeteria_icon = findViewById(R.id.cafeteria_icon);
        this.cafeteria_layout = findViewById(R.id.cafeteria_layout);

        this.vouchers_icon = findViewById(R.id.vouchers_icon);
        this.vouchers_layout = findViewById(R.id.vouchers_layout);

        this.history_icon = findViewById(R.id.history_icon);
        this.history_layout = findViewById(R.id.history_layout);

        this.settings_icon = findViewById(R.id.settings_icon);
        this.settings_layout = findViewById(R.id.settings_layout);

        this.logout_icon = findViewById(R.id.logout_icon);
        this.logout_layout = findViewById(R.id.logout_layout);
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
        this.shows_layout.setOnClickListener((View v)->init_shows());
        this.cafeteria_layout.setOnClickListener((View v)->init_cafeteria());
        this.vouchers_layout.setOnClickListener((View v)->init_vouchers());
        this.history_layout.setOnClickListener((View v)->init_history());
        this.settings_layout.setOnClickListener((View v)->init_settings());
        this.logout_layout.setOnClickListener((View v)->init_logout());
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

        Toast.makeText(this, Constants.LOGOUT_SUCCESS, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
