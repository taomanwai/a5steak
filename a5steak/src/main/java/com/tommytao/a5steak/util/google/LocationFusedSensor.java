package com.tommytao.a5steak.util.google;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tommytao.a5steak.util.Foundation;

import java.util.ArrayList;

/**
 * Responsible for getting latitude & longitude
 * <p/>
 * Note: Google Play services are required. If it is not available, use
 * LocationSensor instead
 *
 * @author tommytao
 */

public class LocationFusedSensor extends Foundation implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static LocationFusedSensor instance;

    public static LocationFusedSensor getInstance() {

        if (instance == null)
            instance = new LocationFusedSensor();

        return instance;

    }

    private LocationFusedSensor() {

    }

    // --

    public static interface OnConnectListener {

        public void onConnected(boolean succeed);

        public void onIgnored();

    }

    public final static int PRIORITY_HIGH_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    public final static int PRIORITY_BALANCED_POWER_ACCURACY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    public final static int PRIORITY_LOW_POWER = LocationRequest.PRIORITY_LOW_POWER;
    public final static int PRIORITY_NO_POWER = LocationRequest.PRIORITY_NO_POWER;



    public final static int DEFAULT_PRIORITY = PRIORITY_BALANCED_POWER_ACCURACY;
    public final static int DEFAULT_INTERVAL_IN_MS = 5000; // 5s (5000ms) = Google Maps interval

    private int intervalInMs = DEFAULT_INTERVAL_IN_MS;

    private boolean connected;

    public final static String PREFS_LAT_E6 = "LocationFusedSensor.PREFS_LAT_E6";
    public final static String PREFS_LNG_E6 = "LocationFusedSensor.PREFS_LNG_E6";

    private final long INVALID_LAT_E6_EXAMPLE = 999999999;
    private final long INVALID_LNG_E6_EXAMPLE = 999999999;

    private GoogleApiClient client;

    @Override
    public boolean init(Context context) {
        return super.init(context);
    }

    public void disconnect() {
        getClient().disconnect();
        connected = false;
        clearAndOnUiThreadTriggerOnConnectListeners(false);
    }

    @Override
    public float calculateDistanceInMeter(double lat1, double lng1, double lat2, double lng2) {
        return super.calculateDistanceInMeter(lat1, lng1, lat2, lng2);
    }

    public float distanceFromLastKnownLatLng(double lat, double lng) {

        Location location = this.getLastKnownLocation();

        if (location == null)
            return Float.NaN;

        return calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), lat, lng);

    }

    // == onConnectListener ==

    private ArrayList<OnConnectListener> onConnectListeners = new ArrayList<>();

    private void clearAndTriggerOnConnectListeners(boolean succeed) {

        ArrayList<OnConnectListener> pendingOnConnectListeners = new ArrayList<>(onConnectListeners);
        onConnectListeners.clear();
        int i = 0;
        for (OnConnectListener pendingOnConnectListener : pendingOnConnectListeners) {
            if (pendingOnConnectListener != null) {
                if (i == (pendingOnConnectListeners.size() - 1))
                    pendingOnConnectListener.onConnected(succeed);
                else
                    pendingOnConnectListener.onIgnored();
            }
            i++;
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
                int i = 0;
                for (OnConnectListener pendingOnConnectListener : pendingOnConnectListeners) {
                    if (pendingOnConnectListener != null) {
                        if (i == (pendingOnConnectListeners.size() - 1))
                            pendingOnConnectListener.onConnected(succeed);
                        else
                            pendingOnConnectListener.onIgnored();
                    }
                    i++;
                }
            }
        });

    }

    // == END of onConnectListener ==

    public void connect(int intervalInMs, final OnConnectListener onConnectListener) {

        if (isConnected()) {
            this.intervalInMs = intervalInMs;
            startDetectingLocation(DEFAULT_PRIORITY, intervalInMs, intervalInMs);

            if (onConnectListener != null)
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onConnectListener.onConnected(true);
                    }
                });

            return;
        }

        this.intervalInMs = intervalInMs;

        if (!isConnecting())
            this.getClient().connect();

        onConnectListeners.add(onConnectListener);


    }

    public long lat2LatE6(double latitude) {
        return super.lat2LatE6(latitude);
    }

    public double latE62Lat(long latitudeE6) {
        return super.latE62Lat(latitudeE6);
    }

    public long lng2LngE6(double longitude) {
        return super.lat2LatE6(longitude);
    }

    public double lngE62Lng(long longitudeE6) {
        return super.latE62Lat(longitudeE6);
    }

    public Location getLastKnownLocation() {

        Location result = LocationServices.FusedLocationApi.getLastLocation(client);

        if (result == null) {

            long latE6 = PreferenceManager.getDefaultSharedPreferences(appContext).getLong(PREFS_LAT_E6, INVALID_LAT_E6_EXAMPLE);
            long lngE6 = PreferenceManager.getDefaultSharedPreferences(appContext).getLong(PREFS_LNG_E6, INVALID_LNG_E6_EXAMPLE);

            if (latE6 != INVALID_LAT_E6_EXAMPLE && lngE6 != INVALID_LNG_E6_EXAMPLE) {

                result = new Location("");
                result.setLatitude(latE62Lat(latE6));
                result.setLongitude(lngE62Lng(lngE6));

            }

        } else {
            PreferenceManager.getDefaultSharedPreferences(appContext)
                    .edit()
                    .putLong(PREFS_LAT_E6, lat2LatE6(result.getLatitude()))
                    .putLong(PREFS_LNG_E6, lng2LngE6(result.getLongitude()))
                    .commit();
        }


        // ==


        return result;

    }

    private GoogleApiClient getClient() {

        if (client == null) {
            this.client = new GoogleApiClient.Builder(appContext).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

        }

        return client;
    }

    public boolean isConnected() {

//        return (this.getClient() != null && this.getClient().isConnected());

        return connected;
    }

    public boolean isConnecting() {

        return !isConnected() && !onConnectListeners.isEmpty();

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        clearAndTriggerOnConnectListeners(false);
    }

    @Override
    public void goToLocationSourceSettings(Activity activity) {
        super.goToLocationSourceSettings(activity);
    }

    public void showResolveLocationSourceDialog() {
        // TODO under construction
    }


    private void startDetectingLocation(int priority, int intervalInMs, int fastestIntervalInMs) {

        if (!isConnected())
            return;

        LocationServices.FusedLocationApi.requestLocationUpdates(client,
                LocationRequest.create()
                        .setPriority(priority)
                        .setInterval(intervalInMs)
                        .setFastestInterval(fastestIntervalInMs), this);
    }

    public Location latLngToLocation(double latitude, double longitude) {
        return super.latLngToLocation(latitude, longitude);
    }

    @Override
    public void onConnected(Bundle arg0) {

        // coz onConnected will be run in async style. Ref: https://developers.google.com/android/reference/com/google/android/gms/common/api/GoogleApiClient.ConnectionCallbacks
        handler.post(new Runnable() {
            @Override
            public void run() {
                startDetectingLocation(DEFAULT_PRIORITY, intervalInMs, intervalInMs);

                connected = true;

                clearAndTriggerOnConnectListeners(true);
            }
        });


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (location == null)
            return;

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(appContext).edit();
        editor.putLong(PREFS_LAT_E6, lat2LatE6(location.getLatitude()));
        editor.putLong(PREFS_LNG_E6, lng2LngE6(location.getLongitude()));
        editor.commit();


    }


}
