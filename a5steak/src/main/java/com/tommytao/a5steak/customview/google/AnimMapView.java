package com.tommytao.a5steak.customview.google;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.location.Location;
import android.util.AttributeSet;

import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tommytao.a5steak.R;
import com.tommytao.a5steak.util.Converter;
import com.tommytao.a5steak.util.Foundation;
import com.tommytao.a5steak.util.LocaleManager;
import com.tommytao.a5steak.util.MathManager;
import com.tommytao.a5steak.util.google.DirectionsApiManager;
import com.tommytao.a5steak.util.google.MapViewAnimator;
import com.tommytao.a5steak.util.sensor.LocationSensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by tommytao on 30/7/15.
 */
public class AnimMapView extends MapView {

    public static interface Listener {

        public void onUpdate();

        public void onComplete();

    }

    public static class AnimMarker {

        private String id = "";
        private Location location = new Location("");
        private float rotation;
        private Marker marker;

        public AnimMarker(String id, double latitude, double longitude, float rotation, Marker marker) {

            this.id = id;
            this.marker = marker;

            if (isRecycled())
                return;

            Location location = new Location("");
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            this.location = location;
            this.rotation = rotation;

            this.marker.setPosition(new LatLng(latitude, longitude));
            this.marker.setRotation(rotation);


        }

        public String getId() {
            return id;
        }

        public Location getLocation() {
            return location;
        }


        public float getRotation() {
            return rotation;
        }

        public void setRotation(float rotation) {
            if (isRecycled())
                return;

            this.rotation = rotation;


            marker.setRotation(rotation);
        }


        public void setLocation(double latitude, double longitude) {
            if (isRecycled())
                return;

            Location location = new Location("");
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            this.location = location;

            marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        public void syncMarkerToAnimMarker() {
            location = new Location("");
            location.setLatitude(marker.getPosition().latitude);
            location.setLongitude(marker.getPosition().longitude);

            rotation = marker.getRotation();

        }

        public boolean isRecycled() {
            return marker == null;
        }

        public void recycle() {

            if (isRecycled())
                return;

            marker.remove();
            marker = null;


        }


    }

    public static class PolylineInterpolator {

        public Location interpolate(float fraction, DirectionsApiManager.Polyline polyline) {

            return polyline.getLocationAtFraction(fraction);

        }

    }

    public static final float MAX_RATIO_ROUTE_DISTANCE_TO_ST_LINE_DISTANCE = 10;

    public static final int MAX_DURATION_TURN_TO_PATH_IN_MS = 300;
    public static final double MAX_RATIO_TURN_TO_PATH = 0.05;

    public static final int MAX_DURATION_TURN_TO_FINAL_ROTATION_IN_MS = 300;
    public static final double MAX_RATIO_TURN_TO_FINAL_ROTATION = 0.05;

    private String clientIdForWork = "";
    private String cryptoForWork = "";

    private HashMap<String, AnimMarker> hashMapAnimMarker = new HashMap<>();

    public AnimMapView(Context context) {
        super(context);
        init();
    }

    public AnimMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        obtainAttrs(context, attrs, 0);
        init();
    }

    public AnimMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        obtainAttrs(context, attrs, defStyle);
        init();
    }

    public AnimMapView(Context context, GoogleMapOptions options) {
        super(context, options);
        init();
    }

    private void obtainAttrs(Context context, AttributeSet attrs, int defStyle){

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnimMapView, defStyle, 0);

        clientIdForWork = a.getString(R.styleable.AnimMapView_clientIdForWork);
        cryptoForWork = a.getString(R.styleable.AnimMapView_cryptoForWork);

        a.recycle();
    }

    public boolean addAnimMarker(String id, double lat, double lng, float rotation, int iconResId) {

        if (containAnimMarker(id))
            return false;

        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(lat, lng));

        if (iconResId != 0) {
            markerOptions = markerOptions.icon(
                    BitmapDescriptorFactory.fromResource(iconResId));
            markerOptions.anchor(0.5f, 0.5f);
        }


        Marker marker = ((GoogleMap) getMap()).addMarker(markerOptions);


        AnimMarker animMarker = new AnimMarker(id, lat, lng, rotation, marker);

        hashMapAnimMarker.put(id, animMarker);

        return true;

    }

    private void init() {

        LocaleManager.getInstance().init(getContext());
        LocationSensor.getInstance().init(getContext());
        Converter.getInstance().init(getContext());

        DirectionsApiManager.getInstance().init(getContext(), Volley.newRequestQueue(getContext(), new Foundation.OkHttpStack()), clientIdForWork, cryptoForWork);

    }

    public boolean removeAnimMarker(String id) {

        if (!containAnimMarker(id))
            return false;

        hashMapAnimMarker.get(id).recycle();
        hashMapAnimMarker.remove(id);

        return true;


    }

    public float getMinDistanceTriggerDirectionsApiInMeter(float rotation) {

        rotation = (float) MathManager.getInstance().wholeToHalfCircleBearing(rotation);

        float result = 4.5f; // 4.5

        double ratio = Math.abs(Math.cos(Math.toRadians(rotation)));
        if (ratio < ((float) 1 / 3))
            ratio = ((float) 1 / 3);

        result *= ratio;

        return result;


    }


    public HashMap<String, AnimMarker> getHashMapAnimMarker() {
        return new HashMap<>(hashMapAnimMarker);
    }

    public void clearAllAnimMarkers() {

        Set<String> keys = hashMapAnimMarker.keySet();
        for (String key : keys) {

            hashMapAnimMarker.remove(key);

        }
    }

    private boolean isRouteReasonable(ArrayList<DirectionsApiManager.Step> steps) {

        if (steps.isEmpty())
            return false;

        int lastIndex = steps.size() - 1;
        Location startLocation = LocationSensor.getInstance().latLngToLocation(steps.get(0).getStartLocation().getLatitude(), steps.get(0).getStartLocation().getLongitude());
        Location endLocation = LocationSensor.getInstance().latLngToLocation(steps.get(lastIndex).getEndLocation().getLatitude(), steps.get(lastIndex).getEndLocation().getLongitude());

        int routeDistance = 0;
        for (DirectionsApiManager.Step step : steps) {
            routeDistance += step.getDistanceInMeter();
        }

        float straightLineDistance = LocationSensor.getInstance().calculateDistanceInMeter(
                startLocation.getLatitude(), startLocation.getLongitude(),
                endLocation.getLatitude(), endLocation.getLongitude());

        return !(((float) routeDistance / straightLineDistance) > MAX_RATIO_ROUTE_DISTANCE_TO_ST_LINE_DISTANCE);


    }

    public boolean flyAnimMarker(String id, double latitude, double longitude, float rotation, int durationInMs, final Listener listener) {

        if (!containAnimMarker(id))
            return false;

        final AnimMarker animMarker = hashMapAnimMarker.get(id);


        MapViewAnimator.getInstance().slideAndRotateMarker(animMarker.marker, latitude, longitude, rotation, durationInMs, new MapViewAnimator.LinearLocationInterpolator(),
                new MapViewAnimator.Listener() {
                    @Override
                    public void onUpdate() {

                        animMarker.syncMarkerToAnimMarker();

                        if (listener != null)
                            listener.onUpdate();


                    }

                    @Override
                    public void onComplete() {


                        if (listener != null)
                            listener.onComplete();

                    }
                });

        return true;

    }

    private int getDurationTurnToPathInMs(int totalDriveDuration, float from, float to) {

        if (Float.isNaN(from) || Float.isNaN(to)) {
            return 0;
        }


        final float rotationDiff = (float) MathManager.getInstance().calculateAngleDerivation(from, to);
        int result = (int) (totalDriveDuration * MAX_RATIO_TURN_TO_PATH);
        result *= Math.abs(rotationDiff) / 180;
        if (result > MAX_DURATION_TURN_TO_PATH_IN_MS)
            result = MAX_DURATION_TURN_TO_PATH_IN_MS;

        return result;

    }

    private int getDurationTurnToFinalRotationInMs(int totalDriveDuration, float from, float to) {

        if (Float.isNaN(from) || Float.isNaN(to)) {
            return 0;
        }


        final float rotationDiff = (float) MathManager.getInstance().calculateAngleDerivation(from, to);
        int result = (int) (totalDriveDuration * MAX_RATIO_TURN_TO_FINAL_ROTATION);
        result *= Math.abs(rotationDiff) / 180;
        if (result > MAX_DURATION_TURN_TO_FINAL_ROTATION_IN_MS)
            result = MAX_DURATION_TURN_TO_FINAL_ROTATION_IN_MS;

        return result;

    }

    public boolean shotAnimMarker(final String id,
                                  final double latitude, final double longitude,
                                  final int durationInMs,
                                  final Listener listener) {

        if (!containAnimMarker(id))
            return false;

        final AnimMarker animMarker = hashMapAnimMarker.get(id);

        final Location startLocation = animMarker.getLocation();
        final Location endLocation = LocationSensor.getInstance().latLngToLocation(latitude, longitude);

        final float targetRotation = LocationSensor.getInstance().calculateBearingInDegree(startLocation.getLatitude(), startLocation.getLongitude(), endLocation.getLatitude(), endLocation.getLongitude());
        int rotationTime = getDurationTurnToPathInMs(durationInMs, animMarker.getRotation(), targetRotation);
        final int guideTime = durationInMs - rotationTime;

        flyAnimMarker(id, startLocation.getLatitude(), startLocation.getLongitude(), targetRotation, rotationTime, new Listener() {
            @Override
            public void onUpdate() {
                if (listener != null)
                    listener.onUpdate();
            }

            @Override
            public void onComplete() {

                flyAnimMarker(id, endLocation.getLatitude(), endLocation.getLongitude(), targetRotation, guideTime, new Listener() {
                    @Override
                    public void onUpdate() {
                        if (listener != null)
                            listener.onUpdate();
                    }

                    @Override
                    public void onComplete() {
                        if (listener != null)
                            listener.onComplete();
                    }
                });

            }
        });

        return true;
    }

    private boolean shouldBypassDirectionsApi(AnimMarker animMarker, double targetLatitude, double targetLongitude, double targetRotation) {


        float distanceDiff = LocationSensor.getInstance().calculateDistanceInMeter(animMarker.getLocation().getLatitude(), animMarker.getLocation().getLongitude(),
                targetLatitude, targetLongitude);
        float eyeSightBearingDiffFromCurrentAnimMarkerBearing = (float) MathManager.getInstance().calculateAngleDerivation(animMarker.getRotation(), LocationSensor.getInstance().calculateBearingInDegree(animMarker.getLocation().getLatitude(), animMarker.getLocation().getLongitude(),
                targetLatitude, targetLongitude));

        if (distanceDiff==0)
            return true;

        float rotationDiffAbs = Math.abs((float) MathManager.getInstance().calculateAngleDerivation(animMarker.getRotation(), targetRotation));

        float distanceLimit = getMinDistanceTriggerDirectionsApiInMeter(eyeSightBearingDiffFromCurrentAnimMarkerBearing);


        if (rotationDiffAbs >= 90)
            distanceLimit = 0;
        else {
            distanceLimit *= (90 - rotationDiffAbs) / 90;
        }

        return (distanceDiff < distanceLimit);

    }


    public boolean driveAnimMarker(final String id,
                                   final double latitude, final double longitude, final float rotation,
                                   final int durationInMs, final boolean filterOutUnreasonableRoute,
                                   final Listener listener) {

        if (!containAnimMarker(id))
            return false;

        final AnimMarker animMarker = hashMapAnimMarker.get(id);

        final Location startLocation = animMarker.getLocation();

        if (!Float.isNaN(rotation) && shouldBypassDirectionsApi(animMarker, latitude, longitude, rotation)) {


            flyAnimMarker(id, latitude, longitude, rotation, durationInMs, new Listener() {
                @Override
                public void onUpdate() {

                    if (listener != null)
                        listener.onUpdate();

                }

                @Override
                public void onComplete() {
                    if (listener != null)
                        listener.onComplete();

                }
            });


            return true;
        }

        DirectionsApiManager.getInstance().route(
                startLocation.getLatitude(), startLocation.getLongitude(),
                latitude, longitude, DirectionsApiManager.AVOID_NONE,
                LocaleManager.getInstance().getSystemLocale(), new DirectionsApiManager.OnRouteListener() {
                    @Override
                    public void returnSteps(final ArrayList<DirectionsApiManager.Step> steps, final DirectionsApiManager.Polyline polyline) {


                        // Route cannot be got
                        if (steps.isEmpty() || polyline == null || polyline.getLocations().isEmpty()) {

                            shotAnimMarker(id, latitude, longitude,
                                    durationInMs,
                                    new Listener() {

                                        @Override
                                        public void onUpdate() {
                                            if (listener != null)
                                                listener.onUpdate();

                                        }

                                        @Override
                                        public void onComplete() {

                                            if (Float.isNaN(rotation)) {
                                                if (listener != null)
                                                    listener.onComplete();
                                            } else {

                                                int rotationTime = getDurationTurnToFinalRotationInMs(durationInMs, animMarker.getRotation(), rotation);

                                                flyAnimMarker(id, animMarker.getLocation().getLatitude(), animMarker.getLocation().getLongitude(), rotation, rotationTime, new Listener() {
                                                    @Override
                                                    public void onUpdate() {
                                                        if (listener != null)
                                                            listener.onUpdate();
                                                    }

                                                    @Override
                                                    public void onComplete() {
                                                        if (listener != null)
                                                            listener.onComplete();

                                                    }
                                                });

                                            }

                                        }
                                    });

                            return;
                        }


                        // Unreasonable route
                        if (filterOutUnreasonableRoute && !isRouteReasonable(steps)) {

                            // abnormal case occurs
                            flyAnimMarker(id, latitude, longitude, Float.isNaN(rotation) ? polyline.getLocationAtFraction(1).getBearing() : rotation, durationInMs, new Listener() {
                                @Override
                                public void onUpdate() {
                                    if (listener != null)
                                        listener.onUpdate();
                                }

                                @Override
                                public void onComplete() {


                                    if (listener != null)
                                        listener.onComplete();

                                }
                            });

                            return;
                        }


                        // Normal
                        float targetRotation = polyline.getLocationAtFraction(0).getBearing();
                        int rotationTime = getDurationTurnToPathInMs(durationInMs, animMarker.getRotation(), targetRotation);
                        final int guideTime = durationInMs - rotationTime;

                        flyAnimMarker(id, animMarker.getLocation().getLatitude(), animMarker.getLocation().getLongitude(), targetRotation, rotationTime, new AnimMapView.Listener() {

                            @Override
                            public void onUpdate() {
                                if (listener != null)
                                    listener.onUpdate();
                            }

                            @Override
                            public void onComplete() {

                                guideAnimMarker(id, polyline, guideTime, true, new Listener() {

                                    @Override
                                    public void onUpdate() {

                                        if (listener != null)
                                            listener.onUpdate();

                                    }

                                    @Override
                                    public void onComplete() {

                                        if (Float.isNaN(rotation)) {
                                            if (listener != null)
                                                listener.onComplete();
                                        } else {
                                            int rotationTime = getDurationTurnToFinalRotationInMs(durationInMs, animMarker.getRotation(), rotation);

                                            flyAnimMarker(id, animMarker.getLocation().getLatitude(), animMarker.getLocation().getLongitude(), rotation, rotationTime, new Listener() {
                                                @Override
                                                public void onUpdate() {
                                                    if (listener != null)
                                                        listener.onUpdate();
                                                }

                                                @Override
                                                public void onComplete() {
                                                    if (listener != null)
                                                        listener.onComplete();

                                                }
                                            });
                                        }


                                    }
                                });

                            }

                        });


                    }
                });

        return true;


    }

    public boolean containAnimMarker(String id) {

        return hashMapAnimMarker.containsKey(id);

    }

    public boolean guideAnimMarker(String id, final DirectionsApiManager.Polyline polyline, int durationInMs, final boolean followRotation, final Listener listener) {
        if (!containAnimMarker(id))
            return false;

        final AnimMarker animMarker = hashMapAnimMarker.get(id);

        final PolylineInterpolator interpolator = new PolylineInterpolator();

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setFloatValues(0, 1); // Ignored.
        valueAnimator.setDuration(durationInMs);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Location newLocation = interpolator.interpolate(animation.getAnimatedFraction(), polyline);
                animMarker.setLocation(newLocation.getLatitude(), newLocation.getLongitude());
                if (followRotation) {
                    if (!Float.isNaN(newLocation.getBearing()))
                        animMarker.setRotation(newLocation.getBearing());
                }

                if (listener != null)
                    listener.onUpdate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {


                if (listener != null)
                    listener.onComplete();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        valueAnimator.start();

        return true;

    }


}
