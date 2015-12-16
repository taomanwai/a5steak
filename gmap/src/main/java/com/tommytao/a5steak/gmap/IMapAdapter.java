package com.tommytao.a5steak.gmap;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

import java.util.ArrayList;

public interface IMapAdapter {

	public static interface OnAnimationCameraCallback {

		public void onCancel();

		public void onFinish();

	}

	public static interface OnCameraChangeCallback {

		public void onCameraChange(Location location);

	}

    public static interface OnMapLoadedCallback {

        public void onMapLoaded();

    }


    public Object getMapView();

	public Object getMap();

	public void moveCameraByLatLng(double latitude, double longitude, float zoom);

	public void moveCameraByBounds(double leftTopLatitude, double leftTopLongitude,
                                   double rightBottomLatitude, double rightBottomLongitude, int paddingInPx);

	public void animateCamera(double latitude, double longitude, float zoomTo,
                              int durationInMs, final OnAnimationCameraCallback callback);

	public void setZoomControlsEnabled(boolean enabled);

	public void setTrafficEnabled(boolean enabled);

	public Object addMarker(double latitude, double longitude,
                            final int markerIconResId, final String title, final String snippet);

	public Object addPolyline(ArrayList<Location> locations, float width,
                              int color);

	public Location getCameraLocation();

	public void setOnCameraChangeListener(final OnCameraChangeCallback callback);

	public void onCreate(Bundle savedInstanceState);

	public void onResume();

	public void onDestroy();

	public void onPause();

	public void onLowMemory();

	public void setInfoWindowAdapter(Object adapter);

	public void onSaveInstanceState(Bundle savedInstanceState);

	public void init(Activity activity, final OnMapLoadedCallback callback);

	public void notifyMarkerRemoveItself(Object marker);

	public void notifyPolylineRemoveItself(Object polyline);

	public void notifyMarkerShowInfoWindow(Object marker);

}
