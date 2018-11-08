package org.feup.cmov.customerapp.dataStructures;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class User {
    // locally saves users registered on the app
    public static final String USER_PATH = "users.dat";

    // locally saves currently logged in user (username or null)
    public static final String LOGGEDIN_USER_PATH = "login_user.dat";

    private String id;
    private String username;
    private String password;
    private String name;
    private String nifNumber;
    private CreditCard creditCard;

    public User(String id, String username, String password, String name, String nifNumber, CreditCard creditCard) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.nifNumber = nifNumber;
        this.creditCard = creditCard;
    }

    /**
     * Converts user to string to represent it in app's internal storage
     * @param user - registered user
     * @return string representing user
     */
    private static String userToString(User user) {
        if (user == null) return "";

        String userString = user.getId() + "\n" + user.getUsername() + "\n" + user.getPassword() + "\n";
        userString += user.getName() + "\n" + user.getNifNumber() + "\n";

        String creditCardString = user.getCreditCard().getType() + "\n" + user.getCreditCard().getNumber() + "\n";
        creditCardString += user.getCreditCard().getMonthValidity() + "\n" + user.getCreditCard().getYearValidity();

        return userString + creditCardString;
    }

    /**
     * Saves registered user locally
     * @param user - registered user
     * @param path - path to save user
     * @param context - current application context
     */
    public static synchronized void saveUser(User user, String path, Context context) {
       FileOutputStream fos;
       try {
           fos = context.openFileOutput(path, Context.MODE_APPEND | Context.MODE_PRIVATE);     // opens in append mode to append new registered user

           String userString = User.userToString(user);
           fos.write(userString.getBytes());
           fos.write(System.getProperty("line.separator").getBytes());

           fos.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
    }

    /**
     * Loads users that were saved locally
     * @param path - path in which registered users were saved
     * @param context - current context
     * @return list of saved users
     */
    public static synchronized List<User> loadUsers(String path, Context context) {

        FileInputStream fis;
        List<User> users = new ArrayList<>();

        try {
            fis = context.openFileInput(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String tmpLine;

            while ((tmpLine = reader.readLine()) != null) {
                String id = tmpLine;
                String username = reader.readLine();
                String pass = reader.readLine();
                String name = reader.readLine();
                String nif = reader.readLine();

                String cc_type = reader.readLine();
                String cc_number = reader.readLine();
                String cc_month = reader.readLine();
                String cc_year = reader.readLine();

                CardType cardType = CardType.getCardType(cc_type);
                CreditCard cc = new CreditCard(cardType, cc_number, Integer.parseInt(cc_month), Integer.parseInt(cc_year));
                User u = new User(id, username, pass, name, nif, cc);

                users.add(u);
            }

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Saves currently logged in user locally
     * @param username - username of currently logged in user
     * @param path - path in which to save user
     * @param context - current context
     */
    public static synchronized void setLoggedinUser(String username, String path, Context context) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(path, Context.MODE_PRIVATE);
            fos.write(username.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads currently logged in user
     * @param path - path in which to load user
     * @param context - current context
     * @return user class representing currently logged in user or null if none is logged in
     */
    public static synchronized User loadLoggedinUser(String path, Context context) {

        FileInputStream fis;
        String username = "";

        try {
            fis = context.openFileInput(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String tmpLine;

            while ((tmpLine = reader.readLine()) != null) {
                username = tmpLine;
            }

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<User> users = loadUsers(USER_PATH, context);

        return getUser(username, users);
    }

    /**
     * Get user by its username in the list of registered users
     * @param username - user's username
     * @param users - list of users
     * @return user class representing wanted user
     */
    public static User getUser(String username, List<User> users) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                return users.get(i);
            }
        }
        return null;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNifNumber() {
        return nifNumber;
    }

    public void setNifNumber(String nifNumber) {
        this.nifNumber = nifNumber;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }
}
