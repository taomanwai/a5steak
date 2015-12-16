package com.tommytao.a5steak.bluetooth;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.common.Foundation;

/**
 * Responsible for getting gravity field reading
 *
 */
public class BluetoothConnector extends Foundation {

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

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }



}
