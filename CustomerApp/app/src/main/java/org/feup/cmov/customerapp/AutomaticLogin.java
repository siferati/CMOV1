package org.feup.cmov.customerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.login.LoginActivity;

public class AutomaticLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, getApplicationContext());

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}
