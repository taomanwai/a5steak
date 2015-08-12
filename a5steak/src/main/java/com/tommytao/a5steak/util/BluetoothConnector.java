package com.tommytao.a5steak.util;

import android.content.Context;

/**
 * Responsible for getting gravity field reading
 *
 */
public class BluetoothConnector extends Foundation  {

    private static BluetoothConnector instance;

    public static BluetoothConnector getInstance() {

        if (instance == null)
            instance = new BluetoothConnector();

        return instance;
    }

    private BluetoothConnector() {

    }

    // --

    @Override
    public boolean init(Context context) {
        return super.init(context);
    }





}
