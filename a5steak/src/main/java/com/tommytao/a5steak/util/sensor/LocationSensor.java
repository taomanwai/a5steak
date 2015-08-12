package com.tommytao.a5steak.util.sensor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.tommytao.a5steak.util.Foundation;

import java.util.ArrayList;

/**
 * Responsible for getting latitude and longitude
 * 
 * At or before Android OS 2.3, SIM card is required to get location from
 * NETWORK_PROVIDER, otherwise null is returned for both latitude and longitude.
 * 
 * Note: LBSManager advantage is that Google Play services is not required at
 * all, but some China phones may not have NETWORK_PROVIDER (In this case, AMap is needed)
 * 
 * Seem min time interval is 45s, hard coded in Android (in 2012). Ref:
 * http://stackoverflow
 * .com/questions/9507557/android-requestlocationupdates-updates
 * -location-at-most-every-45-seconds
 *
 * But tested in Android 4.x in 2015, these bug cannot be reproduced
 * 
 * 
 * @author tommytao
 * 
 */

public class LocationSensor extends Foundation implements LocationListener {

	private static LocationSensor instance;

	public static LocationSensor getInstance() {

		if (instance == null)
			instance = new LocationSensor();

		return instance;
	}

	private LocationSensor() {

		super();

		log("lbs: " + "create");

	}

	// --

	public static interface OnConnectListener {

		public void onConnect(boolean succeed);


	}

	public static interface OnLocationChangeListener {

		public void onLocationChange(Location location);

	}




	public final static String PROVIDER = LocationManager.NETWORK_PROVIDER;
	public final static int DEFAULT_UPDATE_INTERVAL_IN_MS = 5 * 1000;

	public final static String PREFS_LAT_E6 = "LBSManager.PREFS_LAT_E6";
	public final static String PREFS_LNG_E6 = "LBSManager.PREFS_LNG_E6";

	private final long INVALID_LAT_E6_EXAMPLE = 999999999;
    private final long INVALID_LNG_E6_EXAMPLE = 999999999;

	private boolean connected;

	private int updateIntervalInMs = DEFAULT_UPDATE_INTERVAL_IN_MS;

	private LocationManager locationManager;

	private ArrayList<OnLocationChangeListener> onLocationChangeListeners = new ArrayList<>();


	@Override
	public boolean init(Context appContext) {

		if (!super.init(appContext)) {

			log("lbs: " + "init REJECTED: already initialized");

			return false;

		}

		log("lbs: " + "init");

		return true;

	}


	public void addOnLocationChangeListener(OnLocationChangeListener onLocationChangeListener){
		onLocationChangeListeners.add(onLocationChangeListener);
	}

	public boolean removeOnLocationChangeListener(OnLocationChangeListener onLocationChangeListener){
		return onLocationChangeListeners.remove(onLocationChangeListener);
	}

	public void disconnect() {

		if (!isConnected()) {

			log("lbs: " + "disconnect rejected: " + "NOT connected yet");

			return;
		}

		log("lbs: " + "disconnect");

		this.getLocationManager().removeUpdates(this);

		connected = false;

		updateIntervalInMs = DEFAULT_UPDATE_INTERVAL_IN_MS;

	}

	public int getUpdateIntervalInMs() {
		return updateIntervalInMs;
	}

	public float calculateDistanceInMeter(double lat1, double lng1, double lat2, double lng2){

		return super.calculateDistanceInMeter(lat1, lng1, lat2, lng2);

    }

	@Override
	public float calculateBearingInDegree(double lat1, double lng1, double lat2, double lng2) {
		return super.calculateBearingInDegree(lat1, lng1, lat2, lng2);
	}

	public float distanceFromLastKnownLatLng(double lat, double lng) {

		Location location = getLocationManager().getLastKnownLocation(PROVIDER);

		if (location == null)
			return -1;

        return calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), lat, lng);

	}

	public void goToLocationSourceSettings(Activity activity) {

		activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}

	private void triggerListener(final OnConnectListener onConnectListener, final boolean isConnected) {

		if (onConnectListener == null)
			return;


		handler.post(new Runnable() {

			@Override
			public void run() {

				onConnectListener.onConnect(isConnected);


			}

		});

	}

	public void connect(int updateIntervalInMs, final OnConnectListener onConnectListener) {

		if (!this.isAvailable()) {

			log("lbs: " + "connect rejected: provider NOT available");

			triggerListener(onConnectListener, false);

			return;
		}

		if (isConnected()) {

			if (this.updateIntervalInMs == updateIntervalInMs) {
				log("lbs: " + "connect rejected: already connected");

				triggerListener(onConnectListener, true);

				return;
			} else {

				disconnect();

			}
		}


		log("lbs: " + "connect");

		getLocationManager().requestLocationUpdates(PROVIDER, updateIntervalInMs, 0, this);

		this.updateIntervalInMs = updateIntervalInMs;

		this.connected = true;


		triggerListener(onConnectListener, true);

	}

	public Location getLastKnownLocation() {

		Location result = getLocationManager().getLastKnownLocation(PROVIDER);



		if (result == null) {

			long latE6 = PreferenceManager.getDefaultSharedPreferences(appContext).getLong(PREFS_LAT_E6, INVALID_LAT_E6_EXAMPLE);
			long lngE6 = PreferenceManager.getDefaultSharedPreferences(appContext).getLong(PREFS_LNG_E6, INVALID_LNG_E6_EXAMPLE);

			if (latE6 != INVALID_LAT_E6_EXAMPLE && lngE6 != INVALID_LNG_E6_EXAMPLE) {

				result = new Location("");
				result.setLatitude(latE62Lat(latE6));
				result.setLongitude(lngE62Lng(lngE6));

			}

		}

		if (result != null)
			log("lbs: " + "last known location: " + String.format("%.6f", result.getLatitude()) + " " + String.format("%.6f", result.getLongitude()));
		else
			log("lbs: " + "last known location: NOT found. Providers: " + getLocationManager().getAllProviders().toString());


		return result;

	}

	private LocationManager getLocationManager() {
		if (locationManager == null)
			this.locationManager = (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);

		return locationManager;
	}

	public boolean isAvailable() {

		boolean result = (this.getLocationManager() != null && this.getLocationManager().isProviderEnabled(PROVIDER));

		return result;

	}

	public boolean isConnected() {
		return this.connected;
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


	@Override
	public void onLocationChanged(Location location) {

		if (location == null) {

			log("lbs: " + "location changed but NULL");

			return;
		}

		log("lbs: " + "location changed to: " + String.format("%.6f", location.getLatitude()) + " " + String.format("%.6f", location.getLongitude()));

		Editor editor = PreferenceManager.getDefaultSharedPreferences(appContext).edit();
		editor.putLong(PREFS_LAT_E6, lat2LatE6(location.getLatitude()));
		editor.putLong(PREFS_LNG_E6, lng2LngE6(location.getLongitude()));
		editor.commit();

		for (OnLocationChangeListener onLocationChangeListener : onLocationChangeListeners){
			onLocationChangeListener.onLocationChange(location);
		}

	}

	@Override
	public void onProviderDisabled(String provider) {
		// do nothing

	}

	@Override
	public void onProviderEnabled(String provider) {
		// do nothing

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// do nothing

	}

}
