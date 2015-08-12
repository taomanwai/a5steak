package com.tommytao.a5steak.customview.google;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tommytao.a5steak.util.MathManager;
import com.tommytao.a5steak.util.google.DirectionsApiManager;
import com.tommytao.a5steak.util.sensor.GSensor;
import com.tommytao.a5steak.util.sensor.LocationSensor;
import com.tommytao.a5steak.util.sensor.MagneticSensor;
import com.tommytao.a5steak.util.sensor.analyzer.OrientationAnalyzer;
import com.tommytao.a5steak.util.sensor.support.DataProcessor;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by tommytao on 12/8/15.
 */
public class NavMapView extends MapView {

    public interface OnConnectListener {

        public void onConnected(boolean succeed);

    }

    public interface OnStartListener {

        public void onStarted(boolean succeed);

    }

    public interface OnStopListener {

        public void onStopped(boolean succeed);

    }

    public static final int DEFAULT_FRAME_TIME_IN_MS = 16; // i.e. 60 fps
    public static final int DEFAULT_ZOOM = 16;
    public static final int DEFAULT_PITCH = 45;
    public static final int DEFAULT_ANIM_DURATION_IN_MS = 300;

    public static final int MIN_ANGLE_OF_FREE_YAW_IN_DEGREE = 45;
    public static final int MAX_DISTANCE_APART_FROM_ROUTE_IN_METER = 30;
    public static final int TURN_PT_DIAMETER_IN_METER = 4;

    private Handler handler = new Handler(Looper.getMainLooper());

    private Runnable runnableFrameUpdate;

    private ArrayList<DirectionsApiManager.Step> steps = new ArrayList<>();
    private DirectionsApiManager.Polyline polyline;

    private ArrayList<Double> pastYaws = new ArrayList<>();

    public NavMapView(Context context) {
        super(context);
        init();
    }

    public NavMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NavMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NavMapView(Context context, GoogleMapOptions options) {
        super(context, options);
        init();

    }

    private double getFilteredYaw(){

        double lat = LocationSensor.getInstance().getLastKnownLocation().getLatitude();
        double lng = LocationSensor.getInstance().getLastKnownLocation().getLongitude();

        float gX = GSensor.getInstance().getLastKnownX();
        float gY = GSensor.getInstance().getLastKnownY();
        float gZ = GSensor.getInstance().getLastKnownZ();

        float mX = MagneticSensor.getInstance().getLastKnownXInuT();
        float mY = MagneticSensor.getInstance().getLastKnownYInuT();
        float mZ = MagneticSensor.getInstance().getLastKnownZInuT();

        OrientationAnalyzer.OrientationAnalyzed orientationAnalyzed = OrientationAnalyzer.getInstance().calculateYawPitchRoll(gX, gY, gZ, mX, mY, mZ);

        double yaw = Double.NaN;
        if (orientationAnalyzed != null) {
            yaw = orientationAnalyzed.getYaw();
        }

        // replace yaw with road yaw ?
        Location location = getClosestLocationOfPathFromLatLng(lat, lng);

        if (location!=null){

            double diffOfAngle = MathManager.getInstance().calculateAngleDerivation(location.getBearing(), yaw);
            yaw = (Math.abs(diffOfAngle) > MIN_ANGLE_OF_FREE_YAW_IN_DEGREE) ? yaw : location.getBearing();

        }


        DataProcessor.getInstance().lowPassFilterForAngle(pastYaws, 1, yaw, DataProcessor.DEFAULT_STRENGTH_OF_LPF);

        return (pastYaws.isEmpty()) ? getMap().getCameraPosition().bearing : pastYaws.get(0);




    }

    private void init() {

        runnableFrameUpdate = new Runnable() {
            @Override
            public void run() {

                long startTime = SystemClock.uptimeMillis();

                double lat = LocationSensor.getInstance().getLastKnownLocation().getLatitude();
                double lng = LocationSensor.getInstance().getLastKnownLocation().getLongitude();

                double yaw = getFilteredYaw();

                getMap().moveCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(DEFAULT_ZOOM).bearing((float) yaw).tilt((float) DEFAULT_PITCH).build()));

                long endTime = SystemClock.uptimeMillis();
                long timeUsed = endTime - startTime;

                if (timeUsed >= DEFAULT_FRAME_TIME_IN_MS) {
                    Log.d("rtemp", "frame_t: " + timeUsed);
                }


                handler.postDelayed(this, DEFAULT_FRAME_TIME_IN_MS);
            }
        };


    }

    private boolean startedNavigation;

    private boolean connectedNavigation;

    public boolean isConnectedNavigation() {

        return connectedNavigation;

    }


    public void connectNavigation(final OnConnectListener onConnectListener) {

        DirectionsApiManager.getInstance().init(getContext(), "", "");

        LocationSensor.getInstance().init(getContext());
        GSensor.getInstance().init(getContext());
        MagneticSensor.getInstance().init(getContext());

        OrientationAnalyzer.getInstance().init(getContext());


        GSensor.getInstance().connect();
        MagneticSensor.getInstance().connect();
        LocationSensor.getInstance().connect(LocationSensor.DEFAULT_UPDATE_INTERVAL_IN_MS, new LocationSensor.OnConnectListener() {
            @Override
            public void onConnect(boolean succeed) {

                connectedNavigation = succeed;

                if (onConnectListener != null)
                    onConnectListener.onConnected(succeed);
            }
        });

    }

    private void addPolylineToMap(ArrayList<Location> locations, float width, int color) {

        PolylineOptions lineOptions = new PolylineOptions();

        ArrayList<LatLng> latLngs = new ArrayList<>();

        for (Location location : locations)
            if (location != null)
                latLngs.add(new LatLng(location.getLatitude(), location.getLongitude()));

        lineOptions.addAll(latLngs);
        lineOptions.width(width); // 11.0f
        lineOptions.color(color);

        getMap().addPolyline(lineOptions);

    }

    public void disconnectNavigation() {
        connectedNavigation = false;
    }

    public void startNavigation(double latitude, double longitude, Locale locale, final OnStartListener listener) {

        getMap().getUiSettings().setCompassEnabled(false);


        DirectionsApiManager.getInstance().route(LocationSensor.getInstance().getLastKnownLocation().getLatitude(), LocationSensor.getInstance().getLastKnownLocation().getLongitude(),
                latitude, longitude, DirectionsApiManager.AVOID_NONE, locale, new DirectionsApiManager.OnRouteListener() {
                    @Override
                    public void returnSteps(ArrayList<DirectionsApiManager.Step> s, DirectionsApiManager.Polyline p) {

                        if (s.isEmpty() || p == null) {

                            listener.onStarted(false);

                            return;
                        }

                        steps = s;
                        polyline = p;

                        startedNavigation = true;

                        addPolylineToMap(p.getLocations(), 11.0f, Color.parseColor("#FB4E0A"));

                        double lat = LocationSensor.getInstance().getLastKnownLocation().getLatitude();
                        double lng = LocationSensor.getInstance().getLastKnownLocation().getLongitude();
                        double yaw = getFilteredYaw();


                        getMap().animateCamera(
                                CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(DEFAULT_ZOOM).bearing((float) yaw).tilt(DEFAULT_PITCH).build()), DEFAULT_ANIM_DURATION_IN_MS,
                                new GoogleMap.CancelableCallback() {

                                    @Override
                                    public void onCancel() {


                                    }

                                    @Override
                                    public void onFinish() {

                                        handler.post(runnableFrameUpdate);


                                    }

                                });




                    }
                });


    }

    private Location getClosestLocationOfPathFromLatLng(double latitude, double longitude){

        if (steps.isEmpty())
            return null;

        ArrayList<Location> locations = steps.get(0).getPolyline().getLocations();
        Location result = locations.get(0);
        double minDistance = LocationSensor.getInstance().calculateDistanceInMeter(locations.get(0).getLatitude(), locations.get(0).getLongitude(),
                latitude, longitude);


        for (Location location : locations){

            if (LocationSensor.getInstance().calculateDistanceInMeter(location.getLatitude(), location.getLongitude(),
                    latitude, longitude) < minDistance){

                result = location;



            }

        }

        return result;




    }

    public void stopNavigation(final OnStopListener listener) {

        handler.removeCallbacks(runnableFrameUpdate);

        getMap().animateCamera(
                CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(getMap().getCameraPosition().target).zoom(DEFAULT_ZOOM).build()), DEFAULT_ANIM_DURATION_IN_MS,
                new GoogleMap.CancelableCallback() {

                    @Override
                    public void onCancel() {
                        if (listener!=null)
                            listener.onStopped(false);
                    }

                    @Override
                    public void onFinish() {

                        startedNavigation = false;
                        pastYaws = new ArrayList<>();

                        if (listener!=null)
                            listener.onStopped(true);


                    }

                });

    }

    public boolean isStartedNavigation() {

        return startedNavigation;

    }


}
