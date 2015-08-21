package com.tommytao.a5steak.customview.google;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tommytao.a5steak.R;
import com.tommytao.a5steak.util.Foundation;
import com.tommytao.a5steak.util.MathManager;
import com.tommytao.a5steak.util.google.DirectionsApiManager;
import com.tommytao.a5steak.util.google.TextSpeaker;
import com.tommytao.a5steak.util.sensor.GSensor;
import com.tommytao.a5steak.util.sensor.LocationSensor;
import com.tommytao.a5steak.util.sensor.MagneticSensor;
import com.tommytao.a5steak.util.sensor.analyzer.OrientationAnalyzer;
import com.tommytao.a5steak.util.sensor.support.DataProcessor;

import java.lang.ref.WeakReference;
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

        public void onIgnoredByInvalidLatLng();

    }

    public interface OnStopListener {
        public void onStopped(boolean succeed);

        public void onAnimEnded();
    }

    public interface OnResumeListener {
        public void onResumed();
    }

    public interface OnPauseListener {
        public void onPaused();
    }

    public interface OnSwipeAndPauseListener {
        public void onSwipeAndPaused();
    }

    private interface OnMockRouteListener {
        public void onComplete(Route route, double queryStartLatitude, double queryStartLongitude, double queryDestLatitude, double queryDestLongitude, String queryAvoid, Locale queryLocale, long queryElapsedTimestamp);
    }


    public interface OnUpdateListener {

        /**
         * Note: to check if update succeed or not, check if distanceFromEndOfStepInMeter being Double.NaN
         *
         * @param maneuver
         * @param distanceFromEndOfStepInMeter
         * @param instructionsInHtml
         * @param instructionsInText
         * @param route
         */
        public void onUpdate(int maneuver, double distanceFromEndOfStepInMeter, String instructionsInHtml, String instructionsInText, long etaInMs, Route route);
    }


    public static class ResponseToConnect implements LocationSensor.OnConnectListener, TextSpeaker.OnConnectListener {

        private WeakReference<NavMapView> navMapViewWeakReference;
        private ArrayList<Integer> numOfConnections;

        public ResponseToConnect(NavMapView navMapView, ArrayList<Integer> numOfConnections) {
            this.navMapViewWeakReference = new WeakReference<>(navMapView);
            this.numOfConnections = numOfConnections;
        }

        @Override
        public void onConnected(boolean succeed) {
            final NavMapView navMapView = navMapViewWeakReference.get();

            if (navMapView == null)
                return;

            if (!succeed) {

                GSensor.getInstance().disconnect();
                MagneticSensor.getInstance().disconnect();
                LocationSensor.getInstance().disconnect();
                TextSpeaker.getInstance().disconnect();

                numOfConnections.clear();

                navMapView.connectedNavigation = false;
                navMapView.clearAndTriggerOnConnectListeners(false);

                return;
            }

            if (numOfConnections.isEmpty()) {
                return;
            }

            numOfConnections.remove(numOfConnections.size() - 1);

            if (numOfConnections.isEmpty()) {
                navMapView.connectedNavigation = true;
                navMapView.clearAndTriggerOnConnectListeners(true);
            }

        }

        @Override
        public void onIgnored() {

        }
    }

    public static class ResponseToStartNavigationMockRoute implements OnMockRouteListener {

        private WeakReference<NavMapView> navMapViewWeakReference;
        private WeakReference<OnStartListener> onStartListenerWeakReference;

        public ResponseToStartNavigationMockRoute(NavMapView navMapView, OnStartListener onStartListener) {

            navMapViewWeakReference = new WeakReference<>(navMapView);
            onStartListenerWeakReference = new WeakReference<>(onStartListener);

        }

        @Override
        public void onComplete(Route r, double queryStartLatitude, double queryStartLongitude, double queryDestLatitude, double queryDestLongitude, String queryAvoid, Locale queryLocale, long queryElapsedTimestamp) {

            final NavMapView navMapView = navMapViewWeakReference.get();
            OnStartListener onStartListener = onStartListenerWeakReference.get();

            if (navMapView == null)
                return;


            navMapView.onStartListeners.remove(onStartListener);

            if (queryStartLatitude != navMapView.startLocation.getLatitude() ||
                    queryStartLongitude != navMapView.startLocation.getLongitude() ||
                    queryDestLatitude != navMapView.destLocation.getLatitude() ||
                    queryDestLongitude != navMapView.destLocation.getLongitude() ||
                    !queryLocale.equals(navMapView.locale) ||
                    queryElapsedTimestamp != navMapView.latestMockRouteElapsedTimestamp) {

                if (onStartListener != null)
                    onStartListener.onIgnored();

                return;
            }

            if (r == null) {

                navMapView.startLocation = null;
                navMapView.destLocation = null;
                navMapView.rerouteLocation = null;
                navMapView.avoid = "";
                navMapView.locale = null;
                navMapView.latestMockRouteElapsedTimestamp = -1;
                navMapView.route = null;


                if (onStartListener != null)
                    onStartListener.onStarted(false);

                return;

            }

            navMapView.route = r;

            try {
                navMapView.routePolyline.remove();
            } catch (Exception e) {
                e.printStackTrace();
            }
            navMapView.routePolyline = navMapView.route.drawRouteToMap(navMapView.getMap(), 11.0f, Color.parseColor("#FB4E0A"));

            double lat = LocationSensor.getInstance().getLastKnownLocation().getLatitude();
            double lng = LocationSensor.getInstance().getLastKnownLocation().getLongitude();
            final double rotation = navMapView.getProcessedRotation(false);

            try {
                navMapView.currentLocationGroundOverlay.remove();
            } catch (Exception e) {
                e.printStackTrace();
            }
            navMapView.currentLocationGroundOverlay = navMapView.drawGroundOverlayAtLatLngRotation(lat, lng, (float) rotation, R.drawable.ic_marker_current_location);


            if (onStartListener != null)
                onStartListener.onStarted(true);

            if (navMapView.isFrameUpdating()) {
                return;
            }

            // anim map to current

            navMapView.getMap().animateCamera(
                    CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(DEFAULT_ZOOM).bearing((float) rotation).tilt(DEFAULT_PITCH).build()), DEFAULT_ANIM_DURATION_IN_MS,
                    new GoogleMap.CancelableCallback() {

                        @Override
                        public void onCancel() {
                            // do nothing
                        }

                        @Override
                        public void onFinish() {
                            navMapView.enableFrameUpdate();
                        }
                    });


        }
    }

    public static class ResponseToRerouteMockRoute implements OnMockRouteListener {

        private WeakReference<NavMapView> navMapViewWeakReference;

        public ResponseToRerouteMockRoute(NavMapView navMapView) {

            navMapViewWeakReference = new WeakReference<>(navMapView);

        }

        @Override
        public void onComplete(Route r, double queryStartLatitude, double queryStartLongitude, double queryDestLatitude, double queryDestLongitude, String queryAvoid, Locale queryLocale, long queryElapsedTimestamp) {

            NavMapView navMapView = navMapViewWeakReference.get();

            if (navMapView == null)
                return;

            if (r == null)
                return;

            if (!r.isPrepareToBeReplaced())
                return;

            if (queryStartLatitude != navMapView.rerouteLocation.getLatitude() ||
                    queryStartLongitude != navMapView.rerouteLocation.getLongitude() ||
                    queryDestLatitude != navMapView.destLocation.getLatitude() ||
                    queryDestLongitude != navMapView.destLocation.getLongitude() ||
                    !queryLocale.equals(navMapView.locale) ||
                    queryElapsedTimestamp != navMapView.latestMockRouteElapsedTimestamp
                    )
                return;

            if (r == null) {
                navMapView.route.setPrepareToBeReplaced(false);
                return;
            }

            navMapView.route = r;

            try {
                navMapView.routePolyline.remove();
            } catch (Exception e) {
                e.printStackTrace();
            }
            navMapView.routePolyline = navMapView.route.drawRouteToMap(navMapView.getMap(), 11.0f, Color.parseColor("#FB4E0A"));


        }
    }


    public class Route extends Foundation {

        public static final int MAX_DERIVATION_ALLOWED_IN_METER = 30000;
        public static final int MIN_ANGLE_FROM_ROUTE_FOR_FREE_ROTATION_IN_DEGREE = 45;
        public static final int MAX_DISTANCE_BEFORE_SPEAK_IN_METER = 200;
        private DirectionsApiManager.Polyline polyline = new DirectionsApiManager.Polyline("");
        private ArrayList<DirectionsApiManager.Step> steps = new ArrayList<>();
        private Location currentRouteLocation;
        private Location currentLocation;
        private Location startLocation;
        private Location destLocation;
        private int currentRouteStepIndex = -1;
        private int currentRouteLocationIndex = -1;
        private String avoid = "";
        private Locale locale;
        private boolean prepareToBeReplaced;

        public Route(ArrayList<DirectionsApiManager.Step> steps, double startLatitude, double startLongitude, double endLatitude, double endLongitude, String avoid, Locale locale, DirectionsApiManager.Polyline polyline) {

            this.currentLocation = latLngToLocation(startLatitude, startLongitude);
            this.startLocation = latLngToLocation(startLatitude, startLongitude);
            this.destLocation = latLngToLocation(endLatitude, endLongitude);
            this.avoid = avoid;
            this.locale = locale;

            if (polyline != null)
                this.polyline = polyline;

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

        public float getCurrentRotation() {

            if (currentLocation == null)
                return Float.NaN;

            return currentLocation.getBearing();
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

            final DirectionsApiManager.Step step = getCurrentRouteStep();

            if (step == null)
                return;

            if (step.isSpoken() && withoutRepeat)
                return;

            TextSpeaker.getInstance().setLocale(getLocale());
            TextSpeaker.getInstance().speak(step.getInstructionsInText(), new TextSpeaker.OnSpeakListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onComplete(boolean succeed) {

                    if (!succeed)
                        step.setSpoken(false);

                }
            });
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
            return (Math.abs(diffOfAngle) < MIN_ANGLE_FROM_ROUTE_FOR_FREE_ROTATION_IN_DEGREE) ? getCurrentRouteRotation() : rotation;


        }

        private com.google.android.gms.maps.model.Polyline drawRouteToMap(GoogleMap gmap, float width, int color) {

            if (gmap == null)
                return null;

            PolylineOptions lineOptions = new PolylineOptions();

            ArrayList<LatLng> latLngs = new ArrayList<>();

            if (getPolyline().getLocations().isEmpty())
                return null;


            for (Location location : getPolyline().getLocations())
                if (location != null)
                    latLngs.add(new LatLng(location.getLatitude(), location.getLongitude()));

            lineOptions.addAll(latLngs);
            lineOptions.width(width); // 11.0f
            lineOptions.color(color);

            return gmap.addPolyline(lineOptions);

        }

        public GroundOverlay drawCurrentLocationToMap(GoogleMap gmap, int resId) {

            if (gmap == null)
                return null;

            if (currentLocation == null)
                return null;

            final int markerSize = 120;

            final GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromResource(resId))
                    .position(new LatLng(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude()), markerSize, markerSize)
                    .bearing(getCurrentRotation()).zIndex(1.0f);
            return gmap.addGroundOverlay(newarkMap);

        }


        public double getCurrentRouteRatioFromEndOfStep() {

            DirectionsApiManager.Step step = getCurrentRouteStep();

            if (step == null)
                return Double.NaN;

            int sizeOfLocations = step.getPolyline().getLocations().size();

            if (sizeOfLocations == 0)
                return 0;

            int sizeOfPassedLocations = getCurrentRouteLocationIndex() + 1;
            int sizeOfRemainedLocations = sizeOfLocations - sizeOfPassedLocations;

            return (double) sizeOfRemainedLocations / sizeOfLocations;

        }

        public double getCurrentRouteDistanceFromEndOfStepInMeter() {

            DirectionsApiManager.Step step = getCurrentRouteStep();

            if (step == null)
                return Double.NaN;

            return step.getDistanceInMeter() * getCurrentRouteRatioFromEndOfStep();

        }

        public long getCurrentRouteDurationFromEndOfStepInMs() {

            DirectionsApiManager.Step step = getCurrentRouteStep();

            if (step == null)
                return -1;

            double resultInDouble = step.getDurationInMs() * getCurrentRouteRatioFromEndOfStep();


            return Double.isNaN(resultInDouble) ? -1 : (long) resultInDouble;

        }


        public void update(double latitude, double longitude, double rotation) {
            currentLocation = latLngToLocation(latitude, longitude);
            currentLocation.setBearing((float) rotation);

            if (steps.isEmpty())
                return;

            if (currentRouteStepIndex < 0 || currentRouteLocationIndex < 0)
                return;

            // Batch 1 scanning
            double batch1Derivation = Double.NaN;
            int batch1StepIndex = -1;
            int batch1StartIndex = -1;
            int batch1EndIndex = -1;
            int batch1ApproxLocationIndex = -1;
            Location batch1ApproxLocation = null;
            try {
                batch1StepIndex = currentRouteStepIndex;
                batch1StartIndex = 0;
                batch1EndIndex = steps.get(currentRouteStepIndex).getPolyline().getLocations().size() - 1;
                batch1ApproxLocationIndex = steps.get(batch1StepIndex).getPolyline().getClosestPointIndexFromLatLng(latitude, longitude, batch1StartIndex, batch1EndIndex);
                if (batch1ApproxLocationIndex >= 0) {
                    batch1ApproxLocation = steps.get(batch1StepIndex).getPolyline().getLocations().get(batch1ApproxLocationIndex);
                    batch1Derivation = calculateDistanceInMeter(
                            batch1ApproxLocation.getLatitude(), batch1ApproxLocation.getLongitude(),
                            latitude, longitude
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Batch 2 scanning
            double batch2Derivation = Double.NaN;
            int batch2StepIndex = -1;
            int batch2StartIndex = -1;
            int batch2EndIndex = -1;
            int batch2ApproxLocationIndex = -1;
            Location batch2ApproxLocation = null;
            try {
                batch2StepIndex = currentRouteStepIndex + 1;
                batch2StartIndex = 0;
                batch2EndIndex = steps.get(batch2StepIndex).getPolyline().getLocations().size() - 1;
                batch2ApproxLocationIndex = -1;
                batch2ApproxLocationIndex = steps.get(batch2StepIndex).getPolyline().getClosestPointIndexFromLatLng(latitude, longitude, batch2StartIndex, batch2EndIndex);
                if (batch2StepIndex >= 0 && batch2ApproxLocationIndex >= 0) {
                    batch2ApproxLocation = steps.get(batch2StepIndex).getPolyline().getLocations().get(batch2ApproxLocationIndex);
                    batch2Derivation = calculateDistanceInMeter(
                            batch2ApproxLocation.getLatitude(), batch2ApproxLocation.getLongitude(),
                            latitude, longitude
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
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

//            batch1StepIndex
//                    batch1ApproxLocationIndex
//            // batch1Derivation
//
//            batch2StepIndex
//                    batch2ApproxLocationIndex
//            // batch2Derivation


            Log.d("rtemp", "nav_t: batch1StepIndex: " + batch1StepIndex + " batch1ApproxLocationIndex: " + batch1ApproxLocationIndex + " batch1Derivation:" + batch1Derivation +
                    " batch2StepIndex: " + batch2StepIndex + " batch2ApproxLocationIndex: " + batch2ApproxLocationIndex + " batch2Derivation:" + batch2Derivation);



//            // TODO can further optimized (e.g. skip batch 2 when batch 1 is just started
//            // Batch 1 scanning
//            int batch1StepIndex = currentRouteStepIndex;
//            int batch1StartIndex = currentRouteLocationIndex;
//            int batch1EndIndex = steps.get(currentRouteStepIndex).getPolyline().getLocations().size() - 1;
//            int batch1ApproxLocationIndex = steps.get(batch1StepIndex).getPolyline().getClosestPointIndexFromLatLng(latitude, longitude, batch1StartIndex, batch1EndIndex);
//            double batch1Derivation = Double.NaN;
//            Location batch1ApproxLocation = null;
//            if (batch1ApproxLocationIndex >= 0) {
//                batch1ApproxLocation = steps.get(batch1StepIndex).getPolyline().getLocations().get(batch1ApproxLocationIndex);
//                batch1Derivation = calculateDistanceInMeter(
//                        batch1ApproxLocation.getLatitude(), batch1ApproxLocation.getLongitude(),
//                        latitude, longitude
//                );
//            }
//
//            // Batch 2 scanning
//            int batch2StepIndex = currentRouteStepIndex + 1;
//            int batch2StartIndex = 0;
//            int batch2EndIndex = steps.get(currentRouteStepIndex).getPolyline().getLocations().size() / 2 - 1;
//            int batch2ApproxLocationIndex = -1;
//            if (batch2StepIndex >= steps.size()) {
//                batch2StepIndex = -1;
//            } else {
//                batch2ApproxLocationIndex = steps.get(batch2StepIndex).getPolyline().getClosestPointIndexFromLatLng(latitude, longitude, batch2StartIndex, batch2EndIndex);
//            }
//            double batch2Derivation = Double.NaN;
//            Location batch2ApproxLocation = null;
//            if (batch2StepIndex >= 0 && batch2ApproxLocationIndex >= 0) {
//                batch2ApproxLocation = steps.get(batch2StepIndex).getPolyline().getLocations().get(batch2ApproxLocationIndex);
//                batch2Derivation = calculateDistanceInMeter(
//                        batch2ApproxLocation.getLatitude(), batch2ApproxLocation.getLongitude(),
//                        latitude, longitude
//                );
//            }
//
//            // Compare batch 1 & 2, pick the most appropriate one
//            int targetStepIndex = -1;
//            int targetApproxLocationIndex = -1;
//            double targetDerivation = Double.NaN;
//            Location targetApproxLocation = null;
//            if (Double.isNaN(batch1Derivation) && Double.isNaN(batch2Derivation)) {
//                // do nothing
//            } else if (Double.isNaN(batch1Derivation) && !Double.isNaN(batch2Derivation)) {
//                targetStepIndex = batch1StepIndex;
//                targetApproxLocationIndex = batch1ApproxLocationIndex;
//                targetDerivation = batch1Derivation;
//                targetApproxLocation = batch1ApproxLocation;
//
//            } else if (!Double.isNaN(batch1Derivation) && Double.isNaN(batch2Derivation)) {
//                targetStepIndex = batch2StepIndex;
//                targetApproxLocationIndex = batch2ApproxLocationIndex;
//                targetDerivation = batch2Derivation;
//                targetApproxLocation = batch2ApproxLocation;
//
//            } else {
//                // TODO MVP too much copy
//                // both are valid real num
//                if (batch1Derivation <= batch2Derivation) {
//                    targetStepIndex = batch1StepIndex;
//                    targetApproxLocationIndex = batch1ApproxLocationIndex;
//                    targetDerivation = batch1Derivation;
//                    targetApproxLocation = batch1ApproxLocation;
//                } else {
//                    targetStepIndex = batch2StepIndex;
//                    targetApproxLocationIndex = batch2ApproxLocationIndex;
//                    targetDerivation = batch2Derivation;
//                    targetApproxLocation = batch2ApproxLocation;
//                }
//            }

            if (targetDerivation <= MAX_DERIVATION_ALLOWED_IN_METER) {
                currentRouteStepIndex = targetStepIndex;
                currentRouteLocationIndex = targetApproxLocationIndex;
                currentRouteLocation = targetApproxLocation;
            }

        }

        public long getCurrentRouteEtaInMs() {

            int currentStepIndex = getCurrentRouteStepIndex();

            if (currentStepIndex < 0 || currentStepIndex >= getSteps().size())
                return -1;

            long result = 0;

            for (int i = currentStepIndex; i < (getSteps().size() - 1); i++) {

                if (i == currentStepIndex) {

                    result += getCurrentRouteDurationFromEndOfStepInMs();

                    continue;
                }
                result += getSteps().get(i).getDurationInMs();

            }

            return result;

        }
    }

    public static final int DEFAULT_ZOOM = 16;
    public static final int DEFAULT_PITCH = 45;
    public static final int DEFAULT_ANIM_DURATION_IN_MS = 300;

    private Handler handler = new Handler(Looper.getMainLooper());

    private Location destLocation;
    private Location startLocation;
    private String avoid = "";
    private Locale locale;
    private long latestMockRouteElapsedTimestamp = -1;
    private Route route;

    private com.google.android.gms.maps.model.Polyline routePolyline;
    private GroundOverlay currentLocationGroundOverlay;


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

    private GroundOverlay drawGroundOverlayAtLatLngRotation(double lat, double lng, float rotation, int resId) {

        final int markerSize = 120;

        final GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(resId))
                .position(new LatLng(lat, lng), markerSize, markerSize)
                .bearing(rotation).zIndex(1.0f);
        return getMap().addGroundOverlay(groundOverlayOptions);

    }

    // == OnUpdateListener ==
    private ArrayList<OnUpdateListener> onUpdateListeners = new ArrayList<>();

    public void addOnUpdateListener(OnUpdateListener listener) {
        onUpdateListeners.add(listener);

    }

    public void removeOnUpdateListener(OnUpdateListener listener) {
        onUpdateListeners.remove(listener);
    }

    private void triggerOnUpdateListeners(int maneuver, double distanceFromEndOfStepInMeter, String instructionsInHtml, String instructionsInText, long etaInMs, Route route) {
        for (OnUpdateListener onUpdateListener : onUpdateListeners) {
            if (onUpdateListener != null)
                onUpdateListener.onUpdate(maneuver, distanceFromEndOfStepInMeter, instructionsInHtml, instructionsInText, etaInMs, route);
        }
    }


    // == Mock route ==
    private long mockRoute(final double startLatitude, final double startLongitude,
                           final double endLatitude, final double endLongitude, final String avoid, final Locale locale, final OnMockRouteListener listener) {

        final long elapsedTimestamp = SystemClock.elapsedRealtime();
        DirectionsApiManager.getInstance().route(startLatitude, startLongitude,
                endLatitude, endLongitude, avoid, locale, new DirectionsApiManager.OnRouteListener() {
                    @Override
                    public void returnSteps(ArrayList<DirectionsApiManager.Step> steps, DirectionsApiManager.Polyline polyline) {

                        if (steps.isEmpty() || polyline == null) {
                            if (listener != null)
                                listener.onComplete(null, startLatitude, startLongitude, endLatitude, endLongitude, avoid, locale, elapsedTimestamp);
                            return;
                        }

                        Route route = new Route(steps, startLatitude, startLongitude, endLatitude, endLongitude, avoid, locale, polyline);

                        if (listener != null)
                            listener.onComplete(route, startLatitude, startLongitude, endLatitude, endLongitude, avoid, locale, elapsedTimestamp);
                    }
                });

        return elapsedTimestamp;
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
                double rotation = getProcessedRotation(false);
                try {
                    route.update(lat, lng, rotation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                double processedRotation = getProcessedRotation(true);

                getMap().moveCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(DEFAULT_ZOOM).bearing((float) processedRotation).tilt((float) DEFAULT_PITCH).build()));


                if (currentLocationGroundOverlay == null)
                    currentLocationGroundOverlay = drawGroundOverlayAtLatLngRotation(lat, lng, (float) processedRotation, R.drawable.ic_marker_current_location);
                else {
                    currentLocationGroundOverlay.setPosition(new LatLng(lat, lng));
                    currentLocationGroundOverlay.setBearing((float) processedRotation);
                }


                if (route == null) {
                    triggerOnUpdateListeners(DirectionsApiManager.Step.MANEUVER_NONE, Double.NaN, "", "", -1, route);
                    handler.postDelayed(this, DEFAULT_FRAME_TIME_IN_MS);
                    return;
                }

                if (route.getCurrentRouteDistanceFromEndOfStepInMeter() < Route.MAX_DISTANCE_BEFORE_SPEAK_IN_METER) {
                    route.speakCurrentRouteStep(true);
                }

                if (!route.isCurrentlyPassing() && !route.isPrepareToBeReplaced()) {

                    Log.d("rtemp", "nav_t: not_isCurrentlyPassing");

//                    route.setPrepareToBeReplaced(true);
//                    rerouteLocation = LocationSensor.getInstance().getLastKnownLocation();
//
//                    latestMockRouteElapsedTimestamp = mockRoute(rerouteLocation.getLatitude(), rerouteLocation.getLongitude(),
//                            destLocation.getLatitude(), destLocation.getLongitude(), avoid, locale, new ResponseToRerouteMockRoute(NavMapView.this));
//
//                    triggerOnRerouteListeners();

                }

                DirectionsApiManager.Step currentStep = route.getCurrentRouteStep();

                if (currentStep == null) {
                    triggerOnUpdateListeners(DirectionsApiManager.Step.MANEUVER_NONE, Double.NaN, "", "", -1, route);
                    handler.postDelayed(this, DEFAULT_FRAME_TIME_IN_MS);
                    return;
                }

                int maneuver = currentStep.getManeuver();
                double distanceFromEndOfStepInMeter = route.getCurrentRouteDistanceFromEndOfStepInMeter();
                String instructionsInHtml = currentStep.getInstructionsInHtml();
                String instructionsInText = currentStep.getInstructionsInText();
                long etaInMs = route.getCurrentRouteEtaInMs();

                if (!route.isPrepareToBeReplaced())
                    triggerOnUpdateListeners(maneuver, distanceFromEndOfStepInMeter, instructionsInHtml, instructionsInText, etaInMs, route);


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

    // == Init ==
    private void init() {
        DirectionsApiManager.getInstance().init(getContext(), "", "");
        LocationSensor.getInstance().init(getContext());
        GSensor.getInstance().init(getContext());
        MagneticSensor.getInstance().init(getContext());
        OrientationAnalyzer.getInstance().init(getContext());
        TextSpeaker.getInstance().init(getContext());
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


        GSensor.getInstance().connect();
        MagneticSensor.getInstance().connect();
        final ArrayList<Integer> numOfConnections = new ArrayList<>();
        numOfConnections.add(0);
        numOfConnections.add(1);
        LocationSensor.getInstance().connect(DEFAULT_FRAME_TIME_IN_MS, new ResponseToConnect(this, numOfConnections));
        TextSpeaker.getInstance().connect(new ResponseToConnect(this, numOfConnections));

    }

    private void removeRoutePolylineAndCurrentLocationGroundOverlay() {
        try {
            routePolyline.remove();
            routePolyline = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            currentLocationGroundOverlay.remove();
            currentLocationGroundOverlay = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnect all resources (e.g. Singletons) used by NavMapView
     * <p/>
     * Note: It will close all Singletons (e.g. GSensor, MagneticSensor, LocationSensor, TextSpeaker), use it before ensuring such Singletons are totally not in use
     */
    public void disconnectNavigation() {

        if (isStartingNavigation() || isStartedNavigation()) {
            disableFrameUpdate();
            startLocation = null;
            destLocation = null;
            rerouteLocation = null;
            avoid = "";
            locale = null;
            latestMockRouteElapsedTimestamp = -1;
            route = null;

            removeRoutePolylineAndCurrentLocationGroundOverlay();
        }

        connectedNavigation = false;
        GSensor.getInstance().disconnect();
        MagneticSensor.getInstance().disconnect();
        LocationSensor.getInstance().disconnect();
        TextSpeaker.getInstance().disconnect();
        clearAndOnUiThreadTriggerOnConnectListeners(false);

    }

    public boolean isConnectedNavigation() {
        return connectedNavigation;
    }

    public boolean isConnectingNavigation() {
        return (!isConnectedNavigation() && !onConnectListeners.isEmpty());
    }

    private void clearAndTriggerOnConnectListeners(boolean succeed) {
        ArrayList<OnConnectListener> pendingOnConnectListeners = new ArrayList<>(onConnectListeners);
        onConnectListeners.clear();
        for (OnConnectListener pendingOnConnectListener : pendingOnConnectListeners) {
            if (pendingOnConnectListener != null) {
                pendingOnConnectListener.onConnected(succeed);
            }
        }
    }

    private void clearAndOnUiThreadTriggerOnConnectListeners(final boolean succeed) {
        final ArrayList<OnConnectListener> pendingOnConnectListeners = new ArrayList<>(onConnectListeners);
        onConnectListeners.clear();

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (OnConnectListener pendingOnConnectListener : pendingOnConnectListeners) {
                    if (pendingOnConnectListener != null) {
                        pendingOnConnectListener.onConnected(succeed);
                    }
                }
            }
        });


    }


    // == Start ==

    /**
     * To hold onStartListener by NavMapView not by DirectionsApiManager (to avoid potential OOM)
     */
    private ArrayList<OnStartListener> onStartListeners = new ArrayList<>();

    public void startNavigation(double latitude, double longitude, String avoid, Locale locale, final OnStartListener listener) {

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

        if (LocationSensor.getInstance().getLastKnownLocation() == null) {

            if (listener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onIgnoredByInvalidLatLng();
                    }
                });
            }

            return;
        }

        this.startLocation = LocationSensor.getInstance().getLastKnownLocation();
        this.destLocation = LocationSensor.getInstance().latLngToLocation(latitude, longitude);
        this.rerouteLocation = null;
        this.avoid = avoid;
        this.locale = locale;
//        this.latestMockRouteElapsedTimestamp = -1;
        this.route = null;

//        removeRoutePolylineAndCurrentLocationGroundOverlay();
        try {
            routePolyline.remove();
            routePolyline = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        onStartListeners.add(listener);
        latestMockRouteElapsedTimestamp = mockRoute(startLocation.getLatitude(), startLocation.getLongitude(),
                destLocation.getLatitude(), destLocation.getLongitude(), avoid, locale, new ResponseToStartNavigationMockRoute(NavMapView.this, listener));
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
        avoid = "";
        locale = null;
        latestMockRouteElapsedTimestamp = -1;
        route = null;

        removeRoutePolylineAndCurrentLocationGroundOverlay();

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

    private ArrayList<OnResumeListener> onResumeListeners = new ArrayList<>();
    private ArrayList<OnPauseListener> onPauseListeners = new ArrayList<>();
    private ArrayList<OnSwipeAndPauseListener> onSwipeAndPauseListeners = new ArrayList<>();

    private boolean resumeAnimRunning;

    public void addOnResumeListener(OnResumeListener listener) {
        onResumeListeners.add(listener);
    }

    public void removeOnResumeListener(OnResumeListener listener) {
        onResumeListeners.remove(listener);
    }

    public void addOnPauseListener(OnPauseListener listener) {
        onPauseListeners.add(listener);
    }

    public void removeOnPauseListener(OnPauseListener listener) {
        onPauseListeners.remove(listener);
    }

    public void addOnSwipeAndPauseListener(OnSwipeAndPauseListener listener) {
        onSwipeAndPauseListeners.add(listener);
    }

    public void removeOnSwipeAndPauseListener(OnSwipeAndPauseListener listener) {
        onSwipeAndPauseListeners.remove(listener);
    }

    private void keepAndOnUiThreadTriggerOnResumeListeners() {

        final ArrayList<OnResumeListener> pendingOnResumeListeners = new ArrayList<>(onResumeListeners);

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (OnResumeListener onResumeListener : pendingOnResumeListeners) {
                    if (onResumeListener != null)
                        onResumeListener.onResumed();
                }
            }
        });

    }

    private void keepAndOnUiThreadTriggerOnPauseListeners() {
        final ArrayList<OnPauseListener> pendingOnPauseListeners = new ArrayList<>(onPauseListeners);

        for (OnPauseListener onPauseListener : pendingOnPauseListeners) {
            if (onPauseListener != null)
                onPauseListener.onPaused();
        }
    }

    private void keepAndOnUiThreadTriggerOnSwipeAndPauseListeners() {
        final ArrayList<OnSwipeAndPauseListener> pendingOnSwipeAndPauseListeners = new ArrayList<>(onSwipeAndPauseListeners);

        for (OnSwipeAndPauseListener onSwipeAndPauseListener : pendingOnSwipeAndPauseListeners) {
            if (onSwipeAndPauseListener != null)
                onSwipeAndPauseListener.onSwipeAndPaused();
        }
    }


    public void resumeNavigation() {

        if (isResumedNavigation())
            return;

        // NavMapView not ready to resume (no route).
        // PS: When no route, NavMapView may still following clients (e.g. car), it happens when clients change dest during moving.
        // In this situation, resume is cancelled by "!isPausedNavigation() checking"
        if (!isStartedNavigation())
            return;

        GSensor.getInstance().connect();
        MagneticSensor.getInstance().connect();
        // LocationSensor and TextSpeaker may not be fully connected,
        // but it is Ok!
        // Coz getLastKnownLocation() must not be null (coz checked in startNavigation) and
        // TextSpeaker will skip speaking and speak again in the upcoming frame or when TextSpeaker is fully connected
        LocationSensor.getInstance().connect(DEFAULT_FRAME_TIME_IN_MS, null);
        TextSpeaker.getInstance().connect(null);

        keepAndOnUiThreadTriggerOnResumeListeners();

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
                        enableFrameUpdate();
                    }

                });

    }

    public void pauseNavigation() {


        if (!isResumedNavigation())
            return;

        disableFrameUpdate();

        GSensor.getInstance().disconnect();
        MagneticSensor.getInstance().disconnect();
        LocationSensor.getInstance().disconnect();
        TextSpeaker.getInstance().disconnect();

        keepAndOnUiThreadTriggerOnPauseListeners();


    }

    public boolean isResumedNavigation() {
        return isFrameUpdating() || resumeAnimRunning;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            pauseNavigation();
            keepAndOnUiThreadTriggerOnSwipeAndPauseListeners();

        }
        return super.dispatchTouchEvent(ev);
    }

    // == Reroute ==
    public interface OnRerouteListener {
        public void onReroute();
    }

    private Location rerouteLocation;

    private ArrayList<OnRerouteListener> onRerouteListeners = new ArrayList<>();

    public void addOnRerouteListener(OnRerouteListener listener) {
        onRerouteListeners.add(listener);
    }

    public void removeOnRerouteListener(OnRerouteListener listener) {
        onRerouteListeners.remove(listener);
    }

    private void triggerOnRerouteListeners() {
        for (OnRerouteListener onRerouteListener : onRerouteListeners) {
            if (onRerouteListener != null)
                onRerouteListener.onReroute();
        }
    }

}