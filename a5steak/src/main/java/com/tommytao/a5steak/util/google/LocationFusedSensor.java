package com.tommytao.a5steak.util.google;

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
        // do nothing
    }


    // --

    public static interface OnConnectListener {

        public void onConnected(boolean succeed);

    }

    public final static int DEFAULT_PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    public final static int DEFAULT_INTERVAL_IN_MS = 5000; // 5s (5000ms) = Google Maps interval
    public final static int DEFAULT_FASTEST_INTERVAL_IN_MS = 1000;


    public final static String PREFS_LAT_E6 = "LocationFusedSensor.PREFS_LAT_E6";
    public final static String PREFS_LNG_E6 = "LocationFusedSensor.PREFS_LNG_E6";

    private final long INVALID_LAT_E6_EXAMPLE = 999999999;
    private final long INVALID_LNG_E6_EXAMPLE = 999999999;

    private ArrayList<OnConnectListener> onConnectListeners = new ArrayList<OnConnectListener>();

    private GoogleApiClient client;

    private Location lastKnownLocation;

    @Override
    public boolean init(Context context) {
        return super.init(context);
    }

    public void disconnect() {
        getClient().disconnect();
    }

    public float distanceFromLastKnownLatLng(double latitude, double longitude) {

        Location location = this.getLastKnownLocation();

        if (location == null)
            return Float.NaN;

        float[] distance = new float[3];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), latitude, longitude, distance);

        return distance[0];

    }

    public void connect(OnConnectListener onConnectListener) {

        if (onConnectListener != null)
            onConnectListeners.add(onConnectListener);

        lastKnownLocation = null;

        this.getClient().connect();

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

        // ==

        if (result == null)
            result = lastKnownLocation;


        // ==


        if (result == null) {

            long latE6 = PreferenceManager.getDefaultSharedPreferences(appContext).getLong(PREFS_LAT_E6, INVALID_LAT_E6_EXAMPLE);
            long lngE6 = PreferenceManager.getDefaultSharedPreferences(appContext).getLong(PREFS_LNG_E6, INVALID_LNG_E6_EXAMPLE);

            if (latE6 != INVALID_LAT_E6_EXAMPLE && lngE6 != INVALID_LNG_E6_EXAMPLE) {

                result = new Location("");
                result.setLatitude(latE62Lat(latE6));
                result.setLongitude(lngE62Lng(lngE6));

            }

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

        return (this.getClient() != null && this.getClient().isConnected());

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {

        triggerAndClearListeners(false);

    }

    private void triggerAndClearListeners(boolean succeed) {

        ArrayList<OnConnectListener> pendingOnConnectListeners = new ArrayList<>(onConnectListeners);

        onConnectListeners.clear();

        for (OnConnectListener pendingOnConnectListener : pendingOnConnectListeners)
            pendingOnConnectListener.onConnected(succeed);
    }

    private void startDetectingLocation(int priority, int intervalInMs, int fastestIntervalInMs){

        if (!isConnected())
            return;

        LocationServices.FusedLocationApi.requestLocationUpdates(client,
                LocationRequest.create()
                        .setPriority(priority)
                        .setInterval(intervalInMs)
                        .setFastestInterval(fastestIntervalInMs), this);
    }

    @Override
    public void onConnected(Bundle arg0) {

        startDetectingLocation(DEFAULT_PRIORITY, DEFAULT_INTERVAL_IN_MS, DEFAULT_FASTEST_INTERVAL_IN_MS);

        triggerAndClearListeners(true);
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        lastKnownLocation = location;

        if (location == null)
            return;

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(appContext).edit();
        editor.putLong(PREFS_LAT_E6, lat2LatE6(location.getLatitude()));
        editor.putLong(PREFS_LNG_E6, lng2LngE6(location.getLongitude()));
        editor.commit();


    }


}
