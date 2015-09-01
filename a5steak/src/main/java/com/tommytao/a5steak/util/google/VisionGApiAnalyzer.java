package com.tommytao.a5steak.util.google;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.tommytao.a5steak.util.Foundation;

import java.util.ArrayList;

/**
 * Responsible to vision operations, e.g. recognize face, smile, barcode, etc.
 */
public class VisionGApiAnalyzer extends Foundation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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

    public static interface OnConnectListener {
        public void onConnected(boolean succeed);
    }

    private GoogleApiClient client;

    public GoogleApiClient getClient() {

        if (client == null) {
            client = new GoogleApiClient.Builder(appContext)
                    .addApi(ActivityRecognition.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        return client;
    }

    private boolean connected;

    private ArrayList<OnConnectListener> onConnectListeners = new ArrayList<>();

    @Override
    public boolean init(Context context) {
        return super.init(appContext);
    }

    public boolean isConnecting() {

        if (isConnected())
            return false;

        return !onConnectListeners.isEmpty();
    }

    public boolean isConnected() {
        return connected;
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

        getClient().disconnect();
        connected = false;
        clearAndTriggerOnConnectListeners(false);

    }

    private void clearAndTriggerOnConnectListeners(boolean succeed) {

        ArrayList<OnConnectListener> pendingOnConnectListeners = new ArrayList<>(onConnectListeners);

        onConnectListeners.clear();

        for (OnConnectListener pendingOnConnectListener : pendingOnConnectListeners) {
            if (pendingOnConnectListener != null)
                pendingOnConnectListener.onConnected(succeed);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        // coz onConnected will be run in async style. Ref: https://developers.google.com/android/reference/com/google/android/gms/common/api/GoogleApiClient.ConnectionCallbacks

        handler.post(new Runnable() {
            @Override
            public void run() {
                connected = true;
                clearAndTriggerOnConnectListeners(true);
            }
        });

    }

    @Override
    public void onConnectionSuspended(int cause) {
        getClient().connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        clearAndTriggerOnConnectListeners(false);
    }

}
