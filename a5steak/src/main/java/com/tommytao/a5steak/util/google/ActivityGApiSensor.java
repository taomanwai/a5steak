package com.tommytao.a5steak.util.google;

import android.app.IntentService;
import android.app.PendingIntent;
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
 *
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

    private GoogleApiClient apiClient;

    public GoogleApiClient getApiClient() {

        if (apiClient == null) {

            apiClient = new GoogleApiClient.Builder(appContext)
                    .addApi(ActivityRecognition.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

        }

        return apiClient;
    }

    private boolean connected;

    private PendingIntent pendingIntent;

    private DetectedActivity lastKnownDetectedActivity;

    private ArrayList<OnConnectListener> onConnectListenerList = new ArrayList<>();

    public boolean isConnecting() {

        if (isConnected())
            return false;

        return !onConnectListenerList.isEmpty();
    }

    public boolean isConnected() {
        return connected;
    }

    public void connect(OnConnectListener onConnectListener) {

        if (isConnecting()) {

            if (onConnectListener != null)
                onConnectListenerList.add(onConnectListener);

            return;
        }

        if (onConnectListener != null)
            onConnectListenerList.add(onConnectListener);

        lastKnownDetectedActivity = null;
        getApiClient().connect();

    }

    public void disconnect() {

        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(getApiClient(), pendingIntent);

        connected = false;

    }

    private void setLastKnownDetectedActivity(DetectedActivity lastKnownDetectedActivity) {
        this.lastKnownDetectedActivity = lastKnownDetectedActivity;
    }

    public DetectedActivity getLastKnownDetectedActivity() {
        return lastKnownDetectedActivity;
    }

    private void clearAndTriggerOnConnectListenerList(boolean succeed) {

        ArrayList<OnConnectListener> pendingOnConnectListenerList = new ArrayList<>(onConnectListenerList);

        onConnectListenerList.clear();

        for (OnConnectListener pendingOnConnectListener : pendingOnConnectListenerList) {

            if (pendingOnConnectListener != null)
                pendingOnConnectListener.onConnected(succeed);
        }
    }


    @Override
    public void onConnected(Bundle bundle) {


        Intent intent = new Intent(appContext, SenseService.class);

        pendingIntent = PendingIntent
                .getService(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        ActivityRecognition.
                ActivityRecognitionApi.requestActivityUpdates(getApiClient(), 0, pendingIntent);

        connected = true;

        clearAndTriggerOnConnectListenerList(true);



    }

    @Override
    public void onConnectionSuspended(int i) {
        // do nothing
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {


        clearAndTriggerOnConnectListenerList(false);

    }


}
