package com.tommytao.a5steak.util.google;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.tommytao.a5steak.util.Foundation;

import java.util.ArrayList;

/**
 * Created by tommytao on 1/9/15.
 */
public class GFoundation extends Foundation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // == GPlayManager ==
    protected boolean isGPlayExistAndUpToDate() {
        return (GooglePlayServicesUtil.isGooglePlayServicesAvailable(appContext) == ConnectionResult.SUCCESS);
    }

    protected boolean isGPlayExist() {
        return (isGPlayExistAndUpToDate() || GooglePlayServicesUtil.isGooglePlayServicesAvailable(appContext) == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED);
    }


    // == G Api Client ==
    public static interface OnConnectListener {
        public void onConnected(boolean succeed);
        public void onIgnored();
    }

    protected GoogleApiClient client;

    protected GoogleApiClient getClient() {
        if (client == null) {
            this.client = new GoogleApiClient.Builder(appContext).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        }

        return client;
    }

    protected boolean connected;

    protected ArrayList<OnConnectListener> onConnectListeners = new ArrayList<>();

    protected void connect(final OnConnectListener onConnectListener) {

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

        if (!isConnecting())
            getClient().connect();

        onConnectListeners.add(onConnectListener);


    }

    protected void disconnect() {

        getClient().disconnect();
        connected = false;
        clearAndTriggerOnConnectListeners(false);

    }

    protected boolean isConnecting() {

        if (isConnected())
            return false;

        return !onConnectListeners.isEmpty();
    }

    protected boolean isConnected() {
        return connected;
    }

    protected void clearAndTriggerOnConnectListeners(boolean succeed) {

        ArrayList<OnConnectListener> pendingOnConnectListeners = new ArrayList<>(onConnectListeners);

        onConnectListeners.clear();

        for (OnConnectListener pendingOnConnectListener : pendingOnConnectListeners) {
            if (pendingOnConnectListener != null)
                pendingOnConnectListener.onConnected(succeed);
        }
    }

    @Override
    public void onConnected(final Bundle bundle) {
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
