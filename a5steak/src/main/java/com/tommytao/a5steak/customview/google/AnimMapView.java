package com.tommytao.a5steak.customview.google;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Pair;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tommytao.a5steak.util.LocaleManager;
import com.tommytao.a5steak.util.MathManager;
import com.tommytao.a5steak.util.google.DirectionsApiManager;
import com.tommytao.a5steak.util.google.MapViewAnimator;

import java.util.ArrayList;

/**
 * Created by tommytao on 30/7/15.
 */
public class AnimMapView extends MapView {

    public static interface Listener {

        public void onUpdate();

        public void onComplete();

    }

    public static class AnimMarker {

        private Location location = new Location("");
        private float rotation;
        private Marker marker;

        public AnimMarker(double latitude, double longitude, float rotation, Marker marker) {

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

    public static class StepInterpolator {

        private ArrayList<DirectionsApiManager.Step> steps = new ArrayList<>();
        private int totalDistance;
        private boolean interpolateRotation;

        public StepInterpolator(ArrayList<DirectionsApiManager.Step> steps, boolean interpolateRotation) {

            this.steps = new ArrayList<>(steps);
            this.interpolateRotation = interpolateRotation;

            totalDistance = 0;
            int totalDistanceB4InMeter = 0;
            for (DirectionsApiManager.Step step : steps) {
                totalDistanceB4InMeter = totalDistance;
                totalDistance += step.getDistanceInMeter();
                step.setTag(new Pair<Integer, Integer>(totalDistanceB4InMeter, totalDistance));
            }

        }

        private boolean isWithinStep(int distance, DirectionsApiManager.Step step, boolean includeEndDistance) {
            int startDistance = ((Pair<Integer, Integer>) step.getTag()).first;
            int endDistance = ((Pair<Integer, Integer>) step.getTag()).second;

            boolean result = (startDistance <= distance) && (includeEndDistance ? (distance <= endDistance) : (distance < endDistance));
            return result;
        }

        private int getStepIndexAtDistance(int distance) {

            if (distance < 0 || distance > totalDistance)
                return -1;

            if (steps.isEmpty())
                return -1;

            if (steps.size() == 1)
                return (distance <= steps.get(0).getDistanceInMeter()) ? 0 : -1;


            // use binary search to search step index

            int leftBound = 0;
            int rightBound = steps.size() - 1;
            int guessIndex = (rightBound + leftBound) / 2;
            while (true) {

                if (isWithinStep(distance, steps.get(guessIndex), guessIndex == (steps.size() - 1))) {
                    break;
                }

                // update left & right bound, oh also guessIndex !
                if (distance < ((Pair<Integer, Integer>) steps.get(guessIndex).getTag()).first)
                    rightBound = guessIndex - 1;
                else if (distance > ((Pair<Integer, Integer>) steps.get(guessIndex).getTag()).second)
                    leftBound = guessIndex + 1;

                guessIndex = (rightBound + leftBound) / 2;

            }

            return guessIndex;


        }

        public Pair<Location, Float> interpolate(float fraction) {

            int targetDistance = (int) (totalDistance * fraction);
            int stepIndex = getStepIndexAtDistance(targetDistance);

            if (stepIndex == -1) {
                return null;
            }

            Location stepFrom = new Location("");
            stepFrom.setLatitude(steps.get(stepIndex).getStartLatitude());
            stepFrom.setLongitude(steps.get(stepIndex).getStartLongitude());
            Location stepTo = new Location("");
            stepTo.setLatitude(steps.get(stepIndex).getEndLatitude());
            stepTo.setLongitude(steps.get(stepIndex).getEndLongitude());
            ArrayList<Location> stepPolylineLocations = steps.get(stepIndex).getPolylineLocations();

            int stepStartDistance = ((Pair<Integer, Integer>) steps.get(stepIndex).getTag()).first;
            int stepEndDistance = ((Pair<Integer, Integer>) steps.get(stepIndex).getTag()).second;
            float fractionInsideStep = (float) (targetDistance - stepStartDistance) / (stepEndDistance - stepStartDistance);

            int targetLocationIndex = (int) ((stepPolylineLocations.size() - 1) * fractionInsideStep);
            Location targetLocation = stepPolylineLocations.get(targetLocationIndex);

            Location previousLocation = null;
            Location nextLocation = null;

            try {
                previousLocation = stepPolylineLocations.get(targetLocationIndex - 1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                nextLocation = stepPolylineLocations.get(targetLocationIndex + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }


            // TODO rotation at boundary of Step may not be accurate, fix it later
            float rotation = 0;
            if (interpolateRotation) {
                if (previousLocation != null && nextLocation != null) {
                    float previousRotation = previousLocation.bearingTo(targetLocation);
                    float nextRotation = targetLocation.bearingTo(nextLocation);
                    rotation = (float) (previousRotation + MathManager.getInstance().calculateAngleDerivation(previousRotation, nextRotation) / 2);
                } else if (previousLocation != null) {
                    rotation = previousLocation.bearingTo(targetLocation);
                } else if (nextLocation != null) {
                    rotation = targetLocation.bearingTo(nextLocation);
                } else {
                    // do nothing, i.e. rotation = 0
                }
            }

            return new Pair<>(targetLocation, rotation);
        }
    }


    private ArrayList<AnimMarker> animMarkers = new ArrayList<>();

    public AnimMapView(Context context) {
        super(context);
        init();
    }

    public AnimMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public AnimMapView(Context context, GoogleMapOptions options) {
        super(context, options);
        init();
    }


    public void addAnimMarker(double lat, double lng, float rotation, int iconResId) {


        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(lat, lng));

        if (iconResId != 0)
            markerOptions = markerOptions.icon(
                    BitmapDescriptorFactory.fromResource(iconResId));

        Marker marker = ((GoogleMap) getMap()).addMarker(markerOptions);


        AnimMarker animMarker = new AnimMarker(lat, lng, rotation, marker);

        animMarkers.add(animMarker);

    }

    private void init() {

        LocaleManager.getInstance().init(getContext());

    }

    public void removeAnimMarker(int index) {

        animMarkers.get(index).recycle();
        animMarkers.remove(index);


    }

    public void clearAllAnimMarkers() {

        for (int i = 0; i < animMarkers.size(); i++) {

            animMarkers.remove(i);

        }
    }

    public void slideAndRotateAnimMarker(int index, double latitude, double longitude, float rotation, int durationInMs, final Listener listener) {

        final AnimMarker animMarker = animMarkers.get(index);

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

    }

    public void slideAnimMarkerFollowingDrivingRoad(final int index,
                                                    double latitude, double longitude,
                                                    int durationInMs,
                                                    final Listener listener) {

        final AnimMarker animMarker = animMarkers.get(index);

        final Location startLocation = animMarker.getLocation();

        DirectionsApiManager.getInstance().route(
                startLocation.getLatitude(), startLocation.getLongitude(),
                latitude, longitude,
                LocaleManager.getInstance().getSystemLocale(), new DirectionsApiManager.OnRouteListener() {
                    @Override
                    public void returnStepList(ArrayList<DirectionsApiManager.Step> stepList, ArrayList<Location> overviewPolylineLocationList) {

                        slideAnimMarkerFollowingSteps(index, stepList, 3000, true, new Listener() {

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


    }

    public void slideAnimMarkerFollowingSteps(int index, ArrayList<DirectionsApiManager.Step> steps, int durationInMs, final boolean followRotation, final Listener listener) {

        final AnimMarker animMarker = animMarkers.get(index);

        final StepInterpolator stepInterpolator = new StepInterpolator(steps, followRotation);

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = animation.getAnimatedFraction();
                Pair<Location, Float> locationRotation = stepInterpolator.interpolate(v);
                Location newLocation = locationRotation.first;
                float rotation = locationRotation.second;
                animMarker.setLocation(newLocation.getLatitude(), newLocation.getLongitude());
                if (followRotation)
                    animMarker.setRotation(rotation);

                if (listener != null)
                    listener.onUpdate();
            }
        });
        valueAnimator.setFloatValues(0, 1); // Ignored.
        valueAnimator.setDuration(durationInMs);
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

    }


}
