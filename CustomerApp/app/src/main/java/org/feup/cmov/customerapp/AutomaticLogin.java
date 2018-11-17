package org.feup.cmov.customerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.feup.cmov.customerapp.dataStructures.User;
import org.feup.cmov.customerapp.database.LocalDatabase;
import org.feup.cmov.customerapp.login.LoginActivity;

import java.io.File;

public class AutomaticLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //resetLocalFiles();

        User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, getApplicationContext());

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void resetLocalFiles() {
        File dir = getFilesDir();

        File file_users = new File(dir, User.USER_PATH);
        file_users.delete();

        File file_loggedin = new File(dir, User.LOGGEDIN_USER_PATH);
        file_loggedin.delete();

        getApplicationContext().deleteDatabase(LocalDatabase.DATABASE_NAME);
    }
}
