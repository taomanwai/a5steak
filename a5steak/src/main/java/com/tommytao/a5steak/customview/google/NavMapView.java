package com.tommytao.a5steak.customview.google;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tommytao.a5steak.util.Foundation;
import com.tommytao.a5steak.util.MathManager;
import com.tommytao.a5steak.util.google.DirectionsApiManager;
import com.tommytao.a5steak.util.google.TextSpeaker;
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

        public void onIgnored();

        public void onAnimEnded();
    }

    public interface OnStopListener {
        public void onStopped(boolean succeed);

        public void onAnimEnded();
    }

    public interface OnMockRouteListener {
        public void onComplete(Route route, double queryStartLatitude, double queryStartLongitude, double queryDestLatitude, double queryDestLongitude, Locale queryLocale);
    }

    public interface OnResumeListener {

        public void onAnimEnded();

        public void onResumed();

    }

    public class Route extends Foundation {

        public static final int MAX_DERIVATION_ALLOWED_IN_METER = 30;
        public static final int MIN_ANGLE_FROM_ROUTE_FOR_FREE_ROTATION_IN_DEGREE = 45;
        public static final int MAX_DISTANCE_BEFORE_SPEAK_IN_METER = 200;
        private DirectionsApiManager.Polyline polyline; // just for backup
        private ArrayList<DirectionsApiManager.Step> steps = new ArrayList<>();
        private Location currentRouteLocation;
        private Location currentLocation;
        private Location startLocation;
        private Location destLocation;
        private int currentRouteStepIndex = -1;
        private int currentRouteLocationIndex = -1;
        private Locale locale;
        private boolean prepareToBeReplaced;

        public Route(ArrayList<DirectionsApiManager.Step> steps, double startLatitude, double startLongitude, double endLatitude, double endLongitude, Locale locale, DirectionsApiManager.Polyline polyline) {

            this.currentLocation = latLngToLocation(startLatitude, startLongitude);
            this.startLocation = latLngToLocation(startLatitude, startLongitude);
            this.destLocation = latLngToLocation(endLatitude, endLongitude);
            this.polyline = polyline;

            this.locale = locale;

            if (steps.isEmpty())
                return;

            this.steps = new ArrayList<>(steps);

            int index = this.steps.get(0).getPolyline().getClosestPointIndexFromLatLng(startLatitude, startLongitude, 0, steps.get(0).getPolyline().getLocations().size() - 1);

            if (index < 0)
                return;

            Location approxLocation = this.steps.get(0).getPolyline().getLocations().get(index);
            double approxLat = approxLocation.getLatitude();
            double approxLng = approxLocation.getLongitude();
            double derivation = LocationSensor.getInstance().calculateDistanceInMeter(
                    approxLat, approxLng,
                    startLatitude, startLongitude
            );

            if (derivation > MAX_DERIVATION_ALLOWED_IN_METER)
                return;

            this.currentRouteStepIndex = 0;
            this.currentRouteLocationIndex = index;
            this.currentRouteLocation = approxLocation;


        }

        @Deprecated
        public boolean init(Context context) {
            return super.init(context);
        }

        public boolean isPrepareToBeReplaced() {
            return prepareToBeReplaced;
        }

        public void setPrepareToBeReplaced(boolean prepareToBeReplaced) {
            this.prepareToBeReplaced = prepareToBeReplaced;
        }

        public Locale getLocale() {
            return locale;
        }

        public Location getStartLocation() {
            return startLocation;
        }

        public Location getDestLocation() {
            return destLocation;
        }

        public DirectionsApiManager.Polyline getPolyline() {
            return polyline;
        }

        public int getCurrentRouteStepIndex() {
            return currentRouteStepIndex;
        }

        public int getCurrentRouteLocationIndex() {
            return currentRouteLocationIndex;
        }

        public ArrayList<DirectionsApiManager.Step> getSteps() {
            return steps;
        }

        public Location getCurrentLocation() {
            return currentLocation;
        }

        public Location getCurrentRouteLocation() {
            return currentRouteLocation;
        }

        public float getCurrentRouteRotation() {

            if (currentRouteLocation == null)
                return Float.NaN;

            return currentRouteLocation.getBearing();
        }

        public DirectionsApiManager.Step getCurrentRouteStep() {
            DirectionsApiManager.Step result = null;

            try {
                result = getSteps().get(getCurrentRouteStepIndex());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        public boolean isCurrentRouteStepSpoken() {

            boolean result = false;

            try {
                result = getCurrentRouteStep().isSpoken();
            } catch (Exception e) {
                e.printStackTrace();
            }



            return result;
        }

        public void speakCurrentRouteStep(boolean withoutRepeat) {

            DirectionsApiManager.Step step = getCurrentRouteStep();

            if (step == null)
                return;

            if (step.isSpoken() && withoutRepeat)
                return;

            TextSpeaker.getInstance().setLocale(getLocale());
            TextSpeaker.getInstance().speak(step.getInstructionsInHtml(), null);
            step.setSpoken(true);

        }


        public double getCurrentRouteDerivation() {

            if (currentRouteLocation == null || currentLocation == null)
                return Float.NaN;

            return LocationSensor.getInstance().calculateDistanceInMeter(currentRouteLocation.getLatitude(), currentRouteLocation.getLongitude(),
                    currentLocation.getLatitude(), currentLocation.getLongitude());

        }

        public boolean isCurrentlyPassing() {

            if (getCurrentRouteLocation() == null || getCurrentLocation() == null)
                return false;

            // fit path?
            double derivationFromRoute = calculateDistanceInMeter(
                    getCurrentRouteLocation().getLatitude(), getCurrentRouteLocation().getLongitude(),
                    getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude()
            );

            if (derivationFromRoute <= MAX_DERIVATION_ALLOWED_IN_METER)
                return true;

            // fit st. pt.?
            double derivationFromStartLocation = calculateDistanceInMeter(
                    getStartLocation().getLatitude(), getStartLocation().getLongitude(),
                    getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude()
            );

            if (derivationFromStartLocation <= MAX_DERIVATION_ALLOWED_IN_METER)
                return true;

            // fit dest.?
            double derivationFromDestLocation = calculateDistanceInMeter(
                    getDestLocation().getLatitude(), getDestLocation().getLongitude(),
                    getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude()
            );

            if (derivationFromDestLocation <= MAX_DERIVATION_ALLOWED_IN_METER)
                return true;

            return false;

        }


        public double getRouteFittedRotation(double rotation) {

            if (Double.isNaN(rotation))
                return Double.NaN;

            double diffOfAngle = MathManager.getInstance().calculateAngleDerivation(getCurrentRouteRotation(), rotation);
            return (Double.isNaN(diffOfAngle) || Math.abs(diffOfAngle) >= MIN_ANGLE_FROM_ROUTE_FOR_FREE_ROTATION_IN_DEGREE) ? rotation : getCurrentRouteRotation();

        }

        private void drawToMap(GoogleMap gmap, float width, int color) {

            PolylineOptions lineOptions = new PolylineOptions();

            ArrayList<LatLng> latLngs = new ArrayList<>();

            for (Location location : getPolyline().getLocations())
                if (location != null)
                    latLngs.add(new LatLng(location.getLatitude(), location.getLongitude()));

            lineOptions.addAll(latLngs);
            lineOptions.width(width); // 11.0f
            lineOptions.color(color);

            gmap.addPolyline(lineOptions);
        }

        public double getCurrentRouteDistanceFromEndOfStep() {

            int sizeOfLocations = getSteps().get(getCurrentRouteStepIndex()).getPolyline().getLocations().size();

            if (sizeOfLocations == 0)
                return 0;

            int sizeOfPassedLocations = getCurrentRouteLocationIndex() + 1;
            int sizeOfRemainedLocations = sizeOfLocations - sizeOfPassedLocations;

            return (double) getSteps().get(getCurrentRouteStepIndex()).getDistanceInMeter() * sizeOfRemainedLocations / sizeOfLocations;

        }


        public void update(double latitude, double longitude) {
            currentLocation = latLngToLocation(latitude, longitude);

            if (steps.isEmpty())
                return;

            if (currentRouteStepIndex < 0 || currentRouteLocationIndex < 0)
                return;

            // TODO can further optimized (e.g. skip batch 2 when batch 1 is just started
            // Batch 1 scanning
            int batch1StepIndex = currentRouteStepIndex;
            int batch1StartIndex = currentRouteLocationIndex;
            int batch1EndIndex = steps.get(currentRouteStepIndex).getPolyline().getLocations().size() - 1;
            int batch1ApproxLocationIndex = steps.get(batch1StepIndex).getPolyline().getClosestPointIndexFromLatLng(latitude, longitude, batch1StartIndex, batch1EndIndex);
            double batch1Derivation = Double.NaN;
            Location batch1ApproxLocation = null;
            if (batch1ApproxLocationIndex >= 0) {
                batch1ApproxLocation = steps.get(batch1StepIndex).getPolyline().getLocations().get(batch1ApproxLocationIndex);
                batch1Derivation = calculateDistanceInMeter(
                        batch1ApproxLocation.getLatitude(), batch1ApproxLocation.getLongitude(),
                        latitude, longitude
                );
            }

            // Batch 2 scanning
            int batch2StepIndex = currentRouteStepIndex + 1;
            int batch2StartIndex = 0;
            int batch2EndIndex = steps.get(currentRouteStepIndex).getPolyline().getLocations().size() / 2 - 1;
            int batch2ApproxLocationIndex = -1;
            if (batch2StepIndex >= steps.size()) {
                batch2StepIndex = -1;
            } else {
                batch2ApproxLocationIndex = steps.get(batch2StepIndex).getPolyline().getClosestPointIndexFromLatLng(latitude, longitude, batch2StartIndex, batch2EndIndex);
            }
            double batch2Derivation = Double.NaN;
            Location batch2ApproxLocation = null;
            if (batch2StepIndex >= 0 && batch2ApproxLocationIndex >= 0) {
                batch2ApproxLocation = steps.get(batch2StepIndex).getPolyline().getLocations().get(batch2ApproxLocationIndex);
                batch2Derivation = calculateDistanceInMeter(
                        batch2ApproxLocation.getLatitude(), batch2ApproxLocation.getLongitude(),
                        latitude, longitude
                );
            }

            // Compare batch 1 & 2, pick the most appropriate one
            int targetStepIndex = -1;
            int targetApproxLocationIndex = -1;
            double targetDerivation = Double.NaN;
            Location targetApproxLocation = null;
            if (Double.isNaN(batch1Derivation) && Double.isNaN(batch2Derivation)) {
                // do nothing
            } else if (Double.isNaN(batch1Derivation) && !Double.isNaN(batch2Derivation)) {
                targetStepIndex = batch1StepIndex;
                targetApproxLocationIndex = batch1ApproxLocationIndex;
                targetDerivation = batch1Derivation;
                targetApproxLocation = batch1ApproxLocation;

            } else if (!Double.isNaN(batch1Derivation) && Double.isNaN(batch2Derivation)) {
                targetStepIndex = batch2StepIndex;
                targetApproxLocationIndex = batch2ApproxLocationIndex;
                targetDerivation = batch2Derivation;
                targetApproxLocation = batch2ApproxLocation;

            } else {
                // TODO MVP too much copy
                // both are valid real num
                if (batch1Derivation <= batch2Derivation) {
                    targetStepIndex = batch1StepIndex;
                    targetApproxLocationIndex = batch1ApproxLocationIndex;
                    targetDerivation = batch1Derivation;
                    targetApproxLocation = batch1ApproxLocation;
                } else {
                    targetStepIndex = batch2StepIndex;
                    targetApproxLocationIndex = batch2ApproxLocationIndex;
                    targetDerivation = batch2Derivation;
                    targetApproxLocation = batch2ApproxLocation;

                }

            }

            if (Double.isNaN(targetDerivation) || targetDerivation > MAX_DERIVATION_ALLOWED_IN_METER)
                return;

            currentRouteStepIndex = targetStepIndex;
            currentRouteLocationIndex = targetApproxLocationIndex;
            currentRouteLocation = targetApproxLocation;

        }


    }


    public static final int DEFAULT_ZOOM = 16;
    public static final int DEFAULT_PITCH = 45;
    public static final int DEFAULT_ANIM_DURATION_IN_MS = 300;

    private Handler handler = new Handler(Looper.getMainLooper());

    private Location destLocation;
    private Location startLocation;
    private Location rerouteLocation;
    private Locale locale;
    private Route route;

    public NavMapView(Context context) {
        super(context);
    }

    public NavMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NavMapView(Context context, GoogleMapOptions options) {
        super(context, options);
    }

    // == Mock route ==
    public void mockRoute(final double startLatitude, final double startLongitude,
                          final double endLatitude, final double endLongitude, final Locale locale, final OnMockRouteListener listener) {

        DirectionsApiManager.getInstance().route(startLatitude, startLongitude,
                endLatitude, endLongitude, DirectionsApiManager.AVOID_NONE, locale, new DirectionsApiManager.OnRouteListener() {
                    @Override
                    public void returnSteps(ArrayList<DirectionsApiManager.Step> steps, DirectionsApiManager.Polyline polyline) {

                        if (steps.isEmpty() || polyline == null) {
                            if (listener != null)
                                listener.onComplete(null, startLatitude, startLongitude, endLatitude, endLongitude, locale);
                            return;
                        }

                        Route route = new Route(steps, startLatitude, startLongitude, endLatitude, endLongitude, locale, polyline);

                        if (listener != null)
                            listener.onComplete(route, startLatitude, startLongitude, endLatitude, endLongitude, locale);


                    }
                });

    }

    // == Process yaw ==
    private ArrayList<Double> pastYaws = new ArrayList<>();

    private double getProcessedRotation(boolean fitToRouteAndFilter) {

        float gX = GSensor.getInstance().getLastKnownX();
        float gY = GSensor.getInstance().getLastKnownY();
        float gZ = GSensor.getInstance().getLastKnownZ();

        float mX = MagneticSensor.getInstance().getLastKnownXInuT();
        float mY = MagneticSensor.getInstance().getLastKnownYInuT();
        float mZ = MagneticSensor.getInstance().getLastKnownZInuT();

        OrientationAnalyzer.OrientationAnalyzed orientationAnalyzed = OrientationAnalyzer.getInstance().calculateYawPitchRoll(gX, gY, gZ, mX, mY, mZ);

        double rotation = Double.NaN;
        if (orientationAnalyzed != null) {
            rotation = orientationAnalyzed.getYaw();
        }

        // replace yaw with road yaw ?
        if (fitToRouteAndFilter && route != null)
            rotation = route.getRouteFittedRotation(rotation);

        if (fitToRouteAndFilter) {
            DataProcessor.getInstance().lowPassFilterForAngle(pastYaws, 1, rotation, DataProcessor.DEFAULT_STRENGTH_OF_LPF);
            return (pastYaws.isEmpty()) ? getMap().getCameraPosition().bearing : pastYaws.get(0);
        } else {
            return Double.isNaN(rotation) ? getMap().getCameraPosition().bearing : rotation;
        }

    }

    // == Frame ==
    public static final int DEFAULT_FRAME_TIME_IN_MS = 16; // i.e. 60 fps

    private Runnable runnableFrameUpdate;

    private boolean isFrameUpdating() {
        return runnableFrameUpdate != null;
    }

    private void disableFrameUpdate() {

        if (!isFrameUpdating())
            return;

        handler.removeCallbacks(runnableFrameUpdate);
        runnableFrameUpdate = null;
        pastYaws = new ArrayList<>();

        // TODO MVP try catch
        try {
            route.setPrepareToBeReplaced(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void enableFrameUpdate() {

        if (isFrameUpdating())
            return;

        runnableFrameUpdate = new Runnable() {
            @Override
            public void run() {

                double lat = LocationSensor.getInstance().getLastKnownLocation().getLatitude();
                double lng = LocationSensor.getInstance().getLastKnownLocation().getLongitude();

                try {
                    route.update(lat, lng);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                double rotation = getProcessedRotation(true);

                getMap().moveCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(DEFAULT_ZOOM).bearing((float) rotation).tilt((float) DEFAULT_PITCH).build()));

                if (route == null) {
                    return;
                }


                if (route.getCurrentRouteDistanceFromEndOfStep() < Route.MAX_DISTANCE_BEFORE_SPEAK_IN_METER){
                    route.speakCurrentRouteStep(true);
                }

                if (!route.isCurrentlyPassing() && !route.isPrepareToBeReplaced()) {

                    route.setPrepareToBeReplaced(true);
                    rerouteLocation = LocationSensor.getInstance().getLastKnownLocation();

                    mockRoute(rerouteLocation.getLatitude(), rerouteLocation.getLongitude(),
                            destLocation.getLatitude(), destLocation.getLongitude(), locale, new OnMockRouteListener() {

                                @Override
                                public void onComplete(Route r, double queryStartLatitude, double queryStartLongitude, double queryDestLatitude, double queryDestLongitude, Locale queryLocale) {

                                    if (route == null)
                                        return;

                                    if (!route.isPrepareToBeReplaced())
                                        return;

                                    if (queryStartLatitude != rerouteLocation.getLatitude() ||
                                            queryStartLongitude != rerouteLocation.getLongitude() ||
                                            queryDestLatitude != destLocation.getLatitude() ||
                                            queryDestLongitude != destLocation.getLongitude() ||
                                            !queryLocale.equals(locale))
                                        return;

                                    if (r == null) {
                                        route.setPrepareToBeReplaced(false);
                                        return;
                                    }

                                    route = r;

                                    getMap().clear();
                                    route.drawToMap(getMap(), 11.0f, Color.parseColor("#FB4E0A"));

                                }

                            });
                }

                handler.postDelayed(this, DEFAULT_FRAME_TIME_IN_MS);

            }
        };

        pastYaws.clear();
        pastYaws.add((double) getMap().getCameraPosition().bearing);
        handler.post(runnableFrameUpdate);

    }

    // == Getter ==
    public Location getStartLocation() {
        return startLocation;
    }

    public Location getDestLocation() {
        return destLocation;
    }

    public Locale getLocale() {
        return locale;
    }

    public Route getRoute() {
        return route;
    }


    // == Connect ==

    private ArrayList<OnConnectListener> onConnectListeners = new ArrayList<>();
    private boolean connectedNavigation;

    public void connectNavigation(final OnConnectListener onConnectListener) {

        if (isConnectedNavigation()) {
            if (onConnectListener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onConnectListener.onConnected(true);
                    }
                });
            }
            return;
        }

        if (isConnectingNavigation()) {
            onConnectListeners.add(onConnectListener);
            return;
        }

        onConnectListeners.add(onConnectListener);

        DirectionsApiManager.getInstance().init(getContext(), "", "");
        LocationSensor.getInstance().init(getContext());
        GSensor.getInstance().init(getContext());
        MagneticSensor.getInstance().init(getContext());
        OrientationAnalyzer.getInstance().init(getContext());
        TextSpeaker.getInstance().init(getContext());

        GSensor.getInstance().connect();
        MagneticSensor.getInstance().connect();

        final ArrayList<Integer> numOfConnections = new ArrayList<>();
        numOfConnections.add(0);
        numOfConnections.add(1);
        LocationSensor.getInstance().connect(LocationSensor.DEFAULT_UPDATE_INTERVAL_IN_MS, new LocationSensor.OnConnectListener() {
            @Override
            public void onConnect(boolean succeed) {

                if (numOfConnections.size() > 0) {
                    numOfConnections.remove(numOfConnections.size() - 1);
                }

                if (numOfConnections.isEmpty()) {
                    connectedNavigation = succeed;
                    triggerAndClearOnConnectListeners(succeed);
                }

            }
        });
        TextSpeaker.getInstance().connect(new TextSpeaker.OnConnectListener() {
            @Override
            public void onConnected(boolean succeed) {



                // TODO MVP too much copy
                if (numOfConnections.size() > 0) {
                    numOfConnections.remove(numOfConnections.size() - 1);
                }

                if (numOfConnections.isEmpty()) {
                    connectedNavigation = succeed;
                    triggerAndClearOnConnectListeners(succeed);
                }

            }
        });


    }

    public void disconnectNavigation() {

        if (isStartingNavigation() || isStartedNavigation()) {
            disableFrameUpdate();
            startLocation = null;
            destLocation = null;
            rerouteLocation = null;
            locale = null;
            route = null;
            getMap().clear();
        }

        connectedNavigation = false;
        GSensor.getInstance().disconnect();
        MagneticSensor.getInstance().disconnect();
        LocationSensor.getInstance().disconnect();
        handler.post(new Runnable() {
            @Override
            public void run() {
                triggerAndClearOnConnectListeners(false);
            }
        });

    }

    public boolean isConnectedNavigation() {
        return connectedNavigation;
    }

    public boolean isConnectingNavigation() {
        return (!isConnectedNavigation() && !onConnectListeners.isEmpty());
    }

    private void triggerAndClearOnConnectListeners(boolean succeed) {
        ArrayList<OnConnectListener> pendingOnConnectListeners = new ArrayList<>(onConnectListeners);
        onConnectListeners.clear();
        for (OnConnectListener pendingOnConnectListener : pendingOnConnectListeners) {
            if (pendingOnConnectListener != null)
                pendingOnConnectListener.onConnected(succeed);
        }
    }


    // == Start ==

    public void startNavigation(final double latitude, final double longitude, final Locale locale, final OnStartListener listener) {

        if (!isConnectedNavigation()) {
            if (listener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onStarted(false);
                    }
                });
            }
            return;
        }

        this.startLocation = LocationSensor.getInstance().getLastKnownLocation();
        this.destLocation = LocationSensor.getInstance().latLngToLocation(latitude, longitude);
        this.rerouteLocation = null;
        this.locale = locale;
        this.route = null;
        getMap().clear();

        mockRoute(startLocation.getLatitude(), startLocation.getLongitude(),
                destLocation.getLatitude(), destLocation.getLongitude(), locale, new OnMockRouteListener() {

                    @Override
                    public void onComplete(Route r, double queryStartLatitude, double queryStartLongitude, double queryDestLatitude, double queryDestLongitude, Locale queryLocale) {

                        if (queryStartLatitude != startLocation.getLatitude() ||
                                queryStartLongitude != startLocation.getLongitude() ||
                                queryDestLatitude != destLocation.getLatitude() ||
                                queryDestLongitude != destLocation.getLongitude() ||
                                !queryLocale.equals(locale)) {

                            if (listener != null)
                                listener.onIgnored();

                            return;
                        }

                        if (r == null) {

                            NavMapView.this.startLocation = null;
                            NavMapView.this.destLocation = null;
                            NavMapView.this.rerouteLocation = null;
                            NavMapView.this.locale = null;

                            if (listener != null)
                                listener.onStarted(false);

                            return;

                        }

                        route = r;

                        getMap().clear();
                        route.drawToMap(getMap(), 11.0f, Color.parseColor("#FB4E0A"));

                        if (listener != null)
                            listener.onStarted(true);

                        if (isFrameUpdating()) {

                            if (listener != null)
                                listener.onAnimEnded();

                            return;
                        }

                        // anim map to current
                        double lat = LocationSensor.getInstance().getLastKnownLocation().getLatitude();
                        double lng = LocationSensor.getInstance().getLastKnownLocation().getLongitude();
                        final double rotation = getProcessedRotation(false);

                        getMap().animateCamera(
                                CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(DEFAULT_ZOOM).bearing((float) rotation).tilt(DEFAULT_PITCH).build()), DEFAULT_ANIM_DURATION_IN_MS,
                                new GoogleMap.CancelableCallback() {

                                    @Override
                                    public void onCancel() {
                                        // do nothing
                                    }

                                    @Override
                                    public void onFinish() {

                                        enableFrameUpdate();

                                        if (listener != null)
                                            listener.onAnimEnded();

                                    }
                                });

                    }
                });
    }


    public void stopNavigation(final OnStopListener listener) {

        if (!isConnectedNavigation()) {
            if (listener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onStopped(false);
                    }
                });
            }
            return;
        }

        disableFrameUpdate();

        startLocation = null;
        destLocation = null;
        rerouteLocation = null;
        locale = null;
        route = null;

        getMap().clear();

        handler.post(new Runnable() {
            @Override
            public void run() {

                if (listener != null)
                    listener.onStopped(true);

            }
        });

        getMap().animateCamera(
                CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(getMap().getCameraPosition().target).zoom(DEFAULT_ZOOM).build()), DEFAULT_ANIM_DURATION_IN_MS,
                new GoogleMap.CancelableCallback() {

                    @Override
                    public void onCancel() {
                        // do nothing
                    }

                    @Override
                    public void onFinish() {

                        if (listener != null)
                            listener.onAnimEnded();

                    }

                });

    }

    public boolean isStartingNavigation() {
        return startLocation != null && destLocation != null && locale != null && route == null;
    }

    public boolean isStartedNavigation() {
        return startLocation != null && destLocation != null && locale != null && route != null;
    }

    // == Resume & pause ==

    private boolean resumeAnimRunning;

    public void resumeNavigation(final OnResumeListener listener) {

        if (!isStartedNavigation())
            return;

        if (!isPausedNavigation())
            return;

        double lat = LocationSensor.getInstance().getLastKnownLocation().getLatitude();
        double lng = LocationSensor.getInstance().getLastKnownLocation().getLongitude();
        final double bearing = getProcessedRotation(false);
        resumeAnimRunning = true;
        getMap().animateCamera(
                CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(DEFAULT_ZOOM).bearing((float) bearing).tilt((float) DEFAULT_PITCH).build()), DEFAULT_ANIM_DURATION_IN_MS,
                new GoogleMap.CancelableCallback() {

                    @Override
                    public void onCancel() {
                        resumeAnimRunning = false;
                    }

                    @Override
                    public void onFinish() {

                        resumeAnimRunning = false;

                        if (listener != null)
                            listener.onAnimEnded();

                        enableFrameUpdate();

                        if (listener != null)
                            listener.onResumed();

                    }

                });

    }

    public void pauseNavigation() {
        if (!isStartedNavigation() && !isStartingNavigation()) // totally stopped
            return;

        if (isPausedNavigation())
            return;

        disableFrameUpdate();
    }

    public boolean isPausedNavigation() {
        return !isFrameUpdating() && !resumeAnimRunning;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
            pauseNavigation();
        return super.dispatchTouchEvent(ev);
    }
}
