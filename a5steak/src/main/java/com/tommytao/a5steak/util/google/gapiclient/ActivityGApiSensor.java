package com.tommytao.a5steak.util.google.gapiclient;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.tommytao.a5steak.util.Foundation;

import java.util.ArrayList;

/**
 * Responsible to recognize activity of users
 * <p/>
 * Note:
 * 1. Permission com.google.android.gms.permission.ACTIVITY_RECOGNITION is needed
 * 2. In manifest, must declare <service android:name="com.tommytao.a5steak.util.google.ActivitySensor$SenseService" />, A5Steak has already done so
 * 3. Reading may have few seconds delay
 * 4. Reading may stick to old values for long time
 * 5. Network is required, it may consume significant amount of battery
 */
public class ActivityGApiSensor extends Foundation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static ActivityGApiSensor instance;

    public static ActivityGApiSensor getInstance() {

        if (instance == null)
            instance = new ActivityGApiSensor();

        return instance;
    }

    private ActivityGApiSensor() {
        // do nothing
    }


    // --

    public static class SenseService extends IntentService {

        public SenseService() {
            super(SenseService.class.getName());
        }

        public SenseService(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(Intent intent) {

            if (!ActivityRecognitionResult.hasResult(intent)) {
                return;
            }

            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            ActivityGApiSensor.getInstance().setLastKnownDetectedActivity(result.getMostProbableActivity());

        }

    }

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

    private PendingIntent pendingIntent;

    private DetectedActivity lastKnownDetectedActivity;

    private ArrayList<OnConnectListener> onConnectListeners = new ArrayList<>();

    @Override
    public boolean init(Context context) {
        return super.init(context);
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

        if (isConnected()){

            if (onConnectListener!=null){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onConnectListener.onConnected(true);
                    }
                });
            }

            return;
        }

        if (!isConnecting()) {
            lastKnownDetectedActivity = null;
            getClient().connect();
        }

        onConnectListeners.add(onConnectListener);



    }

    public void disconnect() {

        if (!isConnected() && !isConnecting())
            return;

        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(getClient(), pendingIntent);
        getClient().disconnect();
        connected = false;
        clearAndOnUiThreadTriggerOnConnectListeners(false);


}

    private void setLastKnownDetectedActivity(DetectedActivity lastKnownDetectedActivity) {
        this.lastKnownDetectedActivity = lastKnownDetectedActivity;
    }

    public DetectedActivity getLastKnownDetectedActivity() {
        return lastKnownDetectedActivity;
    }

    private void clearAndTriggerOnConnectListeners(boolean succeed) {

        ArrayList<OnConnectListener> pendingOnConnectListeners = new ArrayList<>(onConnectListeners);

        onConnectListeners.clear();

        for (OnConnectListener pendingOnConnectListener : pendingOnConnectListeners) {
            if (pendingOnConnectListener != null)
                pendingOnConnectListener.onConnected(succeed);
        }
    }

    private void clearAndOnUiThreadTriggerOnConnectListeners(final boolean succeed) {

        final ArrayList<OnConnectListener> pendingOnConnectListeners = new ArrayList<>(onConnectListeners);

        onConnectListeners.clear();

        if (pendingOnConnectListeners.isEmpty())
            return;

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (OnConnectListener pendingOnConnectListener : pendingOnConnectListeners) {
                    if (pendingOnConnectListener != null) {
                        pendingOnConnectListener.onConnected(succeed);
                    }
                }
            }
        });

    }




    @Override
    public void onConnected(Bundle bundle) {

        // coz onConnected will be run in async style. Ref: https://developers.google.com/android/reference/com/google/android/gms/common/api/GoogleApiClient.ConnectionCallbacks

        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(appContext, SenseService.class);

                connected = true;

                pendingIntent = PendingIntent
                        .getService(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                ActivityRecognition.
                        ActivityRecognitionApi.requestActivityUpdates(getClient(), 0, pendingIntent);

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
