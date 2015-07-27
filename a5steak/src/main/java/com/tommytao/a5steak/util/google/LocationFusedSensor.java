package com.tommytao.a5steak.util.google;

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

    public static interface Listener {

        public void onConnected();

        public void onError();

    }

    public final static String PREFS_LAT_E6 = "LocationFusedSensor.PREFS_LAT_E6";
    public final static String PREFS_LNG_E6 = "LocationFusedSensor.PREFS_LNG_E6";

    private final long INVALID_LAT_E6_EXAMPLE = 999999999;
    private final long INVALID_LNG_E6_EXAMPLE = 999999999;

    private ArrayList<Listener> listeners = new ArrayList<Listener>();

    private GoogleApiClient client;

    private Location lastKnownLocation;


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

    public void connect(Listener listener) {

        if (listener != null)
            listeners.add(listener);

        lastKnownLocation = null;

        this.getClient().connect();

    }

    public long lat2LatE6(double lat) {
        return super.lat2LatE6(lat);
    }

    public double latE62Lat(long latE6) {
        return super.latE62Lat(latE6);
    }

    public long lng2LngE6(double lng) {
        return super.lat2LatE6(lng);
    }

    public double lngE62Lng(long lngE6) {
        return super.latE62Lat(lngE6);
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

    }

    private void triggerAndClearListeners() {

        ArrayList<Listener> pendingListeners = new ArrayList<>(listeners);

        listeners.clear();

        for (Listener pendingListener : pendingListeners)
            pendingListener.onConnected();
    }

    @Override
    public void onConnected(Bundle arg0) {

        LocationServices.FusedLocationApi.requestLocationUpdates(client,
                LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY), this);

        triggerAndClearListeners();
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
