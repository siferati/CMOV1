package org.feup.cmov.customerapp.dataStructures;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class User implements Serializable {
    public static final String USER_PATH = "user.dat";

    private String id;
    private String name;
    private String username;
    private String password;
    private String nifNumber;
    private CreditCard creditCard;

    private static User instance = null;

    private User(){}

    public static User getInstance(){
        if (instance == null) {
            instance = new User();
        }

        return instance;
    }

    public static void setInstance(User user) {
        instance = user;
    }

    public void setUser(String id, String name, String username, String password, String nifNumber, CreditCard creditCard) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.nifNumber = nifNumber;
        this.creditCard = creditCard;
    }

    public static synchronized void saveUser(User user, String path, Context context) {

        FileOutputStream fos;
        try {
            fos = context.openFileOutput(path, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);

            os.writeObject(user);

            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized User loadUser(String path, Context context) {

        FileInputStream fis;
        User user = null;
        try {
            fis = context.openFileInput(path);
            ObjectInputStream is = new ObjectInputStream(fis);
            user = (User) is.readObject();
            is.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
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
