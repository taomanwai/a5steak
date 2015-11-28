package com.tommytao.a5steak.customview.google;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tommytao.a5steak.customview.IMapAdapter;

import java.util.ArrayList;

public class GMapAdapter implements IMapAdapter {

    private com.google.android.gms.maps.MapView mapView;

    public GMapAdapter(com.google.android.gms.maps.MapView mapView) {

        this.mapView = mapView;

    }

    @Override
    public Object getMapView() {

        return mapView;

    }

    @Override
    public Object getMap() {

        if (mapView == null)
            return null;

        GoogleMap result = mapView.getMap();
        return result;
    }

    @Override
    public void moveCameraByLatLng(double latitude, double longitude, float zoom) {

        ((GoogleMap) getMap()).moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));

    }

    public void moveCameraByBounds(double leftTopLatitude, double leftTopLongitude, double rightBottomLatitude, double rightBottomLongitude, int paddingInPx) {
        LatLngBounds bounds = new LatLngBounds.Builder().include(new LatLng(leftTopLatitude, leftTopLongitude)).include(new LatLng(rightBottomLatitude, rightBottomLongitude))
                .build();

        ((GoogleMap) getMap()).moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, paddingInPx));
    }

    @Override
    public void animateCamera(double latitude, double longitude, float zoomTo, int durationInMs, final IMapAdapter.OnAnimationCameraCallback callback) {

        ((GoogleMap) getMap()).animateCamera(
                CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(zoomTo).build()), durationInMs,
                new GoogleMap.CancelableCallback() {

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

    @Override
    public void setZoomControlsEnabled(boolean enabled) {

        ((GoogleMap) getMap()).getUiSettings().setZoomControlsEnabled(enabled);

    }

    @Override
    public void setTrafficEnabled(boolean enabled) {

        ((GoogleMap) getMap()).setTrafficEnabled(enabled);

    }

    @Override
    public Object addMarker(double latitude, double longitude, int iconResId, String title , String snippet) {

        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).title(title).snippet(snippet);

        if (iconResId != 0)
            markerOptions = markerOptions.icon(
                    BitmapDescriptorFactory.fromResource(iconResId));

        return ((GoogleMap) getMap()).addMarker(markerOptions);

    }

    @Override
    public void notifyMarkerShowInfoWindow(Object marker) {

        if (marker == null)
            return;

        if (!(marker instanceof Marker))
            return;

        ((Marker) marker).showInfoWindow();

    }

    @Override
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

    @Override
    public Location getCameraLocation() {

        LatLng latLng = ((GoogleMap) getMap()).getCameraPosition().target;

        if (latLng == null)
            return null;

        Location result = new Location("");
        result.setLatitude(latLng.latitude);
        result.setLongitude(latLng.longitude);

        return result;
    }

    @Override
    public void setOnCameraChangeListener(final IMapAdapter.OnCameraChangeCallback callback) {

        ((GoogleMap) getMap()).setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

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

    @Override
    public void init(Activity activity, final IMapAdapter.OnMapLoadedCallback callback) {

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

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (mapView == null)
            return;

        mapView.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {

        if (mapView == null)
            return;

        mapView.onResume();

    }

    @Override
    public void onDestroy() {

        if (mapView == null)
            return;

        mapView.onDestroy();

    }

    @Override
    public void onPause() {

        if (mapView == null)
            return;

        mapView.onPause();

    }

    @Override
    public void onLowMemory() {

        if (mapView == null)
            return;

        mapView.onLowMemory();


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        if (mapView == null)
            return;

        mapView.onSaveInstanceState(savedInstanceState);


    }


    @Override
    public void notifyMarkerRemoveItself(Object marker) {

        if (marker == null)
            return;

        if (!(marker instanceof Marker))
            return;

        ((Marker) marker).remove();

    }

    @Override
    public void notifyPolylineRemoveItself(Object polyline) {

        if (polyline == null)
            return;

        if (!(polyline instanceof Polyline))
            return;

        ((Polyline) polyline).remove();

    }

    @Override
    public void setInfoWindowAdapter(Object adapter) {

        if (!(adapter instanceof GoogleMap.InfoWindowAdapter))
            return;

        mapView.getMap().setInfoWindowAdapter((GoogleMap.InfoWindowAdapter) adapter);

    }

}
