package com.tommytao.a5steak.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

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
 * 
 * @author tommytao
 * 
 */

public class LBSManager extends Foundation implements LocationListener {

	private static LBSManager instance;

	public static LBSManager getInstance() {

		if (instance == null)
			instance = new LBSManager();

		return instance;
	}

	private LBSManager() {

		super();

		log("lbs: " + "create");

	}

	// --

	public static interface Listener {

		public void onConnected();

		public void onError();

	}

	private boolean connected;

	private LocationManager locationManager;

	public final static int NETWORK_LOCATION_PROVIDER_UPDATE_INTERVAL_IN_SECOND = 15;

	public final static String PREFS_LAT_E6 = "LBSManager.PREFS_LAT_E6";
	public final static String PREFS_LNG_E6 = "LBSManager.PREFS_LNG_E6";

	private final long INVALID_LAT_E6_EXAMPLE = 999999999;
    private final long INVALID_LNG_E6_EXAMPLE = 999999999;

	@Override
	public boolean init(Context appContext) {

		if (!super.init(appContext)) {

			log("lbs: " + "init REJECTED: already initialized");

			return false;

		}

		log("lbs: " + "init");

		return true;

	}

	public void disconnect() {

		if (!isConnected()) {

			log("lbs: " + "disconnect rejected: " + "NOT connected yet");

			return;
		}

		log("lbs: " + "disconnect");

		this.getLocationManager().removeUpdates(this);

		connected = false;

	}

    public float calculateDistance(double lat1, double lng1, double lat2, double lng2){

        float[] distance = new float[3];
        Location.distanceBetween(lat1, lng1, lat2, lng2, distance);

        return distance[0];

    }

	public float distanceFromLastKnownLatLng(double lat, double lng) {

		Location location = getLocationManager().getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		if (location == null)
			return -1;

//		float[] distance = new float[3];
//		Location.distanceBetween(location.getLatitude(), location.getLongitude(), latitude, longitude, distance);
//
//		return distance[0];

        return calculateDistance(location.getLatitude(), location.getLongitude(), lat, lng);

	}

	public void goToLocationSourceSettings(Activity activity) {

		activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}

	private void triggerListener(final Listener listener, final boolean isConnected) {

		if (listener == null)
			return;

		Handler h = new Handler(Looper.getMainLooper());

		h.post(new Runnable() {

			@Override
			public void run() {

				if (isConnected)
					listener.onConnected();
				else
					listener.onError();

			}

		});

	}

	public void connect(final Listener listener) {

		if (!this.isAvailable()) {

			log("lbs: " + "connect rejected: provider NOT available");

			triggerListener(listener, false);

			return;
		}

		if (isConnected()) {

			log("lbs: " + "connect rejected: already connected");

			triggerListener(listener, true);

			return;
		}

		log("lbs: " + "connect");

		this.getLocationManager().requestLocationUpdates(LocationManager.NETWORK_PROVIDER, NETWORK_LOCATION_PROVIDER_UPDATE_INTERVAL_IN_SECOND * 1000, 0, this);

		connected = true;

		triggerListener(listener, true);

	}

	public Location getLastKnownLocation() {

		Location result = getLocationManager().getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		// TODO MVP to read Genymotion emulator GPS value
		if (result == null){
			result = getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}

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

		boolean result = (this.getLocationManager() != null && this.getLocationManager().isProviderEnabled(LocationManager.NETWORK_PROVIDER));

		return result;

	}

	public boolean isConnected() {
		return this.connected;
	}

	public long lat2LatE6(double lat) {
		return (long) (lat * 1000000);
	}

	public double latE62Lat(long latE6) {
		return (double) latE6 / 1000000;
	}

	public long lng2LngE6(double lng) {
		return lat2LatE6(lng); // just use back lat2LatE6()
	}

	public double lngE62Lng(long lngE6) {
		return latE62Lat(lngE6); // just use back latE62Lat()
	}

//	public boolean isLatValid(double lat) {
//
//		if (Double.isNaN(lat))
//			return false;
//
//		if (lat >= -90 || lat <= 90)
//			return true;
//
//		return false;
//
//	}
//
//	public boolean isLngValid(double lng) {
//
//		if (Double.isNaN(lng))
//			return false;
//
//		if (lng >= -180 || lng <= 180)
//			return true;
//
//		return false;
//
//	}

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
