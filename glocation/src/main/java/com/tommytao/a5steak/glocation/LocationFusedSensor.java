package com.tommytao.a5steak.glocation;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tommytao.a5steak.common.Foundation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Responsible for getting latitude & longitude and setting geofence
 * <p/>
 * Note:
 * Google Play services are required. If it is not available, use
 * LocationSensor instead
 * <p/>
 * <p/>
 * Ref: http://stackoverflow.com/questions/29671039/geofences-not-working-when-app-is-killed (Fix geofence disappeared when location service mode/provider are changed)
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

    public static class GeofenceService extends IntentService {

        public GeofenceService() {
            super(GeofenceService.class.getName());
        }

        public GeofenceService(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(Intent intent) {

            GeofencingEvent event = GeofencingEvent.fromIntent(intent);

            if (event.hasError()) {

                int errCode = event.getErrorCode();
                String errStr = GeofenceStatusCodes.getStatusCodeString(errCode);

                Log.d("rtemp", "test_t: err " + "in sensor class");
                LocationFusedSensor.getInstance().triggerListenerOnError(errCode, errStr);

                return;
            }

            int transitionMode = event.getGeofenceTransition();
            ArrayList<Geofence> geofences = new ArrayList<>(event.getTriggeringGeofences().isEmpty() ? new ArrayList<Geofence>() : event.getTriggeringGeofences());

            switch (transitionMode) {

                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    Log.d("rtemp", "test_t: enter " + "in sensor class");
                    LocationFusedSensor.getInstance().triggerListenerOnEnter(geofences);
                    break;

                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    Log.d("rtemp", "test_t: exit " + "in sensor class");
                    LocationFusedSensor.getInstance().triggerListenerOnExit(geofences);
                    break;

                default:
                    Log.d("rtemp", "test_t: err " + "in sensor class");
                    LocationFusedSensor.getInstance().triggerListenerOnError(-1, "");
                    break;

            }

        }

    }

    public static interface OnConnectListener {

        public void onConnected(boolean succeed);

        public void onIgnored();

    }

    public static interface OnGeofenceListener {

        public void onEnter(ArrayList<Geofence> geofences);

        public void onExit(ArrayList<Geofence> geofences);

        public void onError(int errorCode, String errorString);

    }

    private class CircularRegion {

        private double latitude = Double.NaN;
        private double longitude = Double.NaN;
        private float radius = Float.NaN;


        public CircularRegion(double latitude, double longitude, float radius) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.radius = radius;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public float getRadius() {
            return radius;
        }
    }

    public final static int PRIORITY_HIGH_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    public final static int PRIORITY_BALANCED_POWER_ACCURACY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    public final static int PRIORITY_LOW_POWER = LocationRequest.PRIORITY_LOW_POWER;
    public final static int PRIORITY_NO_POWER = LocationRequest.PRIORITY_NO_POWER;


    public final static int DEFAULT_PRIORITY = PRIORITY_BALANCED_POWER_ACCURACY; // PRIORITY_BALANCED_POWER_ACCURACY
    public final static int DEFAULT_INTERVAL_IN_MS = 5000; // 5s (5000ms) = Google Maps interval

    private int intervalInMs = DEFAULT_INTERVAL_IN_MS;
    private int priority = DEFAULT_PRIORITY;

    private HashMap<String, CircularRegion> circularRegions = new HashMap<>();

    private ArrayList<OnGeofenceListener> onGeofenceListeners = new ArrayList<>();

    private boolean connected;

    public final static String PREFS_LAT_E6 = "LocationFusedSensor.PREFS_LAT_E6";
    public final static String PREFS_LNG_E6 = "LocationFusedSensor.PREFS_LNG_E6";
    public final static String PREFS_SPEED = "LocationFusedSensor.PREFS_SPEED";

    private final long INVALID_LAT_E6_EXAMPLE = 999999999;
    private final long INVALID_LNG_E6_EXAMPLE = 999999999;

    private GoogleApiClient client;

    @Override
    public boolean init(Context context) {
        return super.init(context);
    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }

    public void addGeofenceListener(OnGeofenceListener onGeofenceListener) {
        onGeofenceListeners.add(onGeofenceListener);
    }

    public void removeGeofenceListener(OnGeofenceListener onGeofenceListener) {
        onGeofenceListeners.remove(onGeofenceListener);
    }

    private void triggerListenerOnError(int errorCode, String errorString) {
        for (OnGeofenceListener onGeofenceListener : onGeofenceListeners)
            if (onGeofenceListener != null)
                onGeofenceListener.onError(errorCode, errorString);
    }

    private void triggerListenerOnEnter(ArrayList<Geofence> geofences) {
        for (OnGeofenceListener onGeofenceListener : onGeofenceListeners)
            if (onGeofenceListener != null)
                onGeofenceListener.onEnter(geofences);
    }

    private void triggerListenerOnExit(ArrayList<Geofence> geofences) {
        for (OnGeofenceListener onGeofenceListener : onGeofenceListeners)
            if (onGeofenceListener != null)
                onGeofenceListener.onExit(geofences);
    }

    @Override
    public void disconnect() {

        if (!isConnected() && !isConnecting())
            return;

        if (isConnected())
            LocationServices.GeofencingApi.removeGeofences(getClient(), getGeofencePendingIntent());

        circularRegions.clear();

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

    @Deprecated
    protected void connect() {
        super.connect();
    }

    public void connect(int priority, int intervalInMs, final OnConnectListener onConnectListener) {

        if (isConnected()) {
            this.priority = priority;
            this.intervalInMs = intervalInMs;
            startDetectingLocation(priority, intervalInMs, intervalInMs);

            if (onConnectListener != null)
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onConnectListener.onConnected(true);
                    }
                });

            return;
        }

        this.priority = priority;
        this.intervalInMs = intervalInMs;

        if (!isConnecting())
            this.getClient().connect();

        onConnectListeners.add(onConnectListener);


    }

    @Override
    protected boolean isLatLngValid(double lat, double lng) {
        return super.isLatLngValid(lat, lng);
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
            String speedStr = PreferenceManager.getDefaultSharedPreferences(appContext).getString(PREFS_SPEED, "");

            if (latE6 != INVALID_LAT_E6_EXAMPLE && lngE6 != INVALID_LNG_E6_EXAMPLE) {

                result = new Location("");
                result.setLatitude(latE62Lat(latE6));
                result.setLongitude(lngE62Lng(lngE6));

                try {
                    float speed = Float.valueOf(speedStr);
                    if (!Float.isNaN(speed))
                        result.setSpeed(speed);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } else {
            PreferenceManager.getDefaultSharedPreferences(appContext)
                    .edit()
                    .putLong(PREFS_LAT_E6, lat2LatE6(result.getLatitude()))
                    .putLong(PREFS_LNG_E6, lng2LngE6(result.getLongitude()))
                    .putString(PREFS_SPEED, "" + (result.hasSpeed() ? result.getSpeed() : Float.NaN))
                    .commit();
        }


        return result;

    }

    public float getLastKnownSpeed() {

        Location location = getLastKnownLocation();
        float result = location.getSpeed();
        return location.hasSpeed() ? result : Float.NaN;

    }


    protected GoogleApiClient getClient() {

        if (client == null) {
            this.client = new GoogleApiClient.Builder(appContext).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        }

        return client;

    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isConnecting() {

        if (isConnected())
            return false;

        return !onConnectListeners.isEmpty();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
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

                connected = true;

                startDetectingLocation(priority, intervalInMs, intervalInMs);

                clearAndTriggerOnConnectListeners(true);
            }
        });

    }

    @Override
    public void onConnectionSuspended(int cause) {
        getClient().connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location == null)
            return;

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(appContext).edit();
        editor.putLong(PREFS_LAT_E6, lat2LatE6(location.getLatitude()))
                .putLong(PREFS_LNG_E6, lng2LngE6(location.getLongitude()))
                .putString(PREFS_SPEED, "" + (location.hasSpeed() ? location.getSpeed() : Float.NaN))
                .commit();


    }

    // Geofence

    private PendingIntent geofencePendingIntent;

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent == null) {
            Intent intent = new Intent(appContext, GeofenceService.class);
            geofencePendingIntent = PendingIntent.getService(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return geofencePendingIntent;
    }

    public void addGeofence(String id, double latitude, double longitude, float radiusInMeter) {

        if (!isConnected())
            return;

        // build geofence
        Geofence geofence = new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(latitude, longitude, radiusInMeter)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        // build geofencingrequest
        GeofencingRequest request = new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();

        LocationServices.
                GeofencingApi.addGeofences(getClient(), request, getGeofencePendingIntent());

        // TODO should check err
        circularRegions.put(id, new CircularRegion(latitude, longitude, radiusInMeter));


    }

    public void removeGeofence(String id) {

        if (!isConnected())
            return;

        ArrayList<String> geofenceIds = new ArrayList<>();

        geofenceIds.add(id);

        LocationServices.GeofencingApi.removeGeofences(getClient(), geofenceIds);

        // TODO should check err
        circularRegions.remove(id);


    }


}
