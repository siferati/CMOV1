package org.feup.cmov.validationcafeteria.server;

import org.feup.cmov.validationcafeteria.MainActivity;

public class MakeOrder extends ServerConnection implements Runnable {

    // main activity
    private MainActivity activity;

    public MakeOrder(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {

    }
}
