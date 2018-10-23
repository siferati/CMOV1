package org.feup.cmov.customerapp;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.feup.cmov.customerapp.utils.FontManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView shows_icon = findViewById(R.id.shows_icon);
        TextView cafeteria_icon = findViewById(R.id.cafeteria_icon);
        TextView history_icon = findViewById(R.id.history_icon);
        TextView settings_icon = findViewById(R.id.settings_icon);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);

        shows_icon.setTypeface(iconFont);
        cafeteria_icon.setTypeface(iconFont);
        history_icon.setTypeface(iconFont);
        settings_icon.setTypeface(iconFont);
    }
}
