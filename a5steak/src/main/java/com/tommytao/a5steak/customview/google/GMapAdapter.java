package com.tommytao.a5steak.customview.google;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tommytao.a5steak.customview.IMapAdapter;

import java.util.ArrayList;

public class GMapAdapter implements IMapAdapter {

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

    private com.google.android.gms.maps.MapView mapView;

    public GMapAdapter(com.google.android.gms.maps.MapView mapView) {

        this.mapView = mapView;

    }

    // == Get map core ==

    public Object getMapView() {

        return mapView;

    }

    public Object getMap() {

        if (mapView == null)
            return null;

        GoogleMap result = mapView.getMap();
        return result;
    }

    // == Movement ==

    public void moveCameraByLatLng(double latitude, double longitude, float zoom, double bearing, double tilt) {

        ((GoogleMap) getMap()).moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(zoom).bearing((float) bearing).tilt((float) tilt).build()));
        // CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom)

    }

    public void moveCameraByBounds(double leftTopLatitude, double leftTopLongitude, double rightBottomLatitude, double rightBottomLongitude, int paddingInPx) {
        LatLngBounds bounds = new LatLngBounds.Builder().include(new LatLng(leftTopLatitude, leftTopLongitude)).include(new LatLng(rightBottomLatitude, rightBottomLongitude))
                .build();

        ((GoogleMap) getMap()).moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, paddingInPx));
    }

    public void animateCamera(double latitude, double longitude, float zoomTo, int durationInMs, final OnAnimationCameraCallback callback) {

        ((GoogleMap) getMap()).animateCamera(
                CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(zoomTo).build()), durationInMs,
                new CancelableCallback() {

                    @Override
                    public void onCancel() {

                        if (callback != null)
                            callback.onCancel();

                    }

                    @Override
                    public void onFinish() {

                        if (callback != null)
                            callback.onFinish();

                    }

                });

    }

    // == Control map ==

    public void setZoomControlsEnabled(boolean enabled) {

        ((GoogleMap) getMap()).getUiSettings().setZoomControlsEnabled(enabled);

    }

    public void setTrafficEnabled(boolean enabled) {

        ((GoogleMap) getMap()).setTrafficEnabled(enabled);

    }

    public void setCompassEnabled(boolean enabled) {

        ((GoogleMap) getMap()).getUiSettings().setCompassEnabled(enabled);



    }

    public void setMyLocationButtonEnabled(boolean enabled){


        ((GoogleMap) getMap()).setMyLocationEnabled(enabled);
        ((GoogleMap) getMap()).getUiSettings().setMyLocationButtonEnabled(enabled);

    }


    // == Marker ==
    public Object addMarker(double latitude, double longitude, int iconResId, String title, String snippet) {

        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).title(title).snippet(snippet);

        if (iconResId != 0)
            markerOptions = markerOptions.icon(
                    BitmapDescriptorFactory.fromResource(iconResId));

        return ((GoogleMap) getMap()).addMarker(markerOptions);

    }

    public void setInfoWindowAdapter(Object adapter) {

        if (!(adapter instanceof InfoWindowAdapter))
            return;

        mapView.getMap().setInfoWindowAdapter((InfoWindowAdapter) adapter);

    }


    // == Polyline ==
    public Object addPolyline(ArrayList<Location> locations, float width, int color) {

        PolylineOptions lineOptions = new PolylineOptions();

        ArrayList<LatLng> latLngs = new ArrayList<>();

        for (Location location : locations)
            if (location != null)
                latLngs.add(new LatLng(location.getLatitude(), location.getLongitude()));

        lineOptions.addAll(latLngs);
        lineOptions.width(width); // 11.0f
        lineOptions.color(color);

        return ((GoogleMap) getMap()).addPolyline(lineOptions);

    }

    // == Camera ==

    public Location getCameraLocation() {

        LatLng latLng = ((GoogleMap) getMap()).getCameraPosition().target;

        if (latLng == null)
            return null;

        Location result = new Location("");
        result.setLatitude(latLng.latitude);
        result.setLongitude(latLng.longitude);

        return result;
    }

    public void setOnCameraChangeListener(final OnCameraChangeCallback callback) {

        ((GoogleMap) getMap()).setOnCameraChangeListener(new OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                if (callback == null)
                    return;

                LatLng latLng = cameraPosition.target;
                Location result = null;

                if (latLng != null) {
                    result = new Location("");
                    result.setLatitude(latLng.latitude);
                    result.setLongitude(latLng.longitude);
                }

                callback.onCameraChange(result);

            }

        });

    }

    // == Setup & init ==

    public void init(Activity activity, final OnMapLoadedCallback callback) {

        mapView.getMap().setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                if (callback != null)
                    callback.onMapLoaded();

            }
        });

        try {
            MapsInitializer.initialize(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onCreate(Bundle savedInstanceState) {

        if (mapView == null)
            return;

        mapView.onCreate(savedInstanceState);

    }

    public void onResume() {

        if (mapView == null)
            return;

        mapView.onResume();

    }

    public void onDestroy() {

        if (mapView == null)
            return;

        mapView.onDestroy();

    }

    public void onPause() {

        if (mapView == null)
            return;

        mapView.onPause();

    }

    public void onLowMemory() {

        if (mapView == null)
            return;

        mapView.onLowMemory();

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {

        if (mapView == null)
            return;

        mapView.onSaveInstanceState(savedInstanceState);


    }


//    public void notifyMarkerRemoveItself(Object marker) {
//
//        if (marker == null)
//            return;
//
//        if (!(marker instanceof Marker))
//            return;
//
//        ((Marker) marker).remove();
//
//    }
//
//    public void notifyPolylineRemoveItself(Object polyline) {
//
//        if (polyline == null)
//            return;
//
//        if (!(polyline instanceof Polyline))
//            return;
//
//        ((Polyline) polyline).remove();
//
//    }
//
//    public void notifyMarkerShowInfoWindow(Object marker) {
//
//     if (marker == null)
//        return;
//
//     if (!(marker instanceof Marker))
//        return;
//
//     ((Marker) marker).showInfoWindow();
//
//    }

}
