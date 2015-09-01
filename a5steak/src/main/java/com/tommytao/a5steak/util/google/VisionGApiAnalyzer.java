package com.tommytao.a5steak.util.google;

import android.content.Context;
import android.os.Bundle;

/**
 * Responsible to vision operations, e.g. recognize face, smile, barcode, etc.
 */
public class VisionGApiAnalyzer extends GFoundation {

    private static VisionGApiAnalyzer instance;

    public static VisionGApiAnalyzer getInstance() {

        if (instance == null)
            instance = new VisionGApiAnalyzer();

        return instance;
    }

    private VisionGApiAnalyzer() {
        // do nothing
    }

    // --

    @Override
    public boolean init(Context context) {
        return super.init(appContext);
    }

    public boolean isConnecting() {
       return super.isConnecting();
    }

    public boolean isConnected() {
        return super.isConnected();
    }

    public void connect(final OnConnectListener onConnectListener) {

        if (isConnected()) {

            if (onConnectListener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onConnectListener.onConnected(true);
                    }
                });
            }

            return;
        }

        onConnectListeners.add(onConnectListener);

        if (!isConnecting())
            getClient().connect();

    }

    public void disconnect() {
        super.disconnect();
    }

    @Override
    protected void customOnConnected(Bundle bundle) {
        // do nothing
    }
}
