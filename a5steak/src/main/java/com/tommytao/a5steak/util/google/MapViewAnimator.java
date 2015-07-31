package com.tommytao.a5steak.util.google;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.tommytao.a5steak.util.Foundation;

/**
 * Created by tommytao on 29/7/15.
 */
public class MapViewAnimator extends Foundation {

    private static MapViewAnimator instance;

    public static MapViewAnimator getInstance() {

        if (instance == null)
            instance = new MapViewAnimator();

        return instance;
    }

    private MapViewAnimator() {

    }

    // --

    public static interface Listener {

        public void onUpdate();

        public void onComplete();

    }


    public static interface BaseLocationInterpolator {
        public Location interpolate(float fraction, Location from, Location to);

    }


    public static class LinearLocationInterpolator implements BaseLocationInterpolator {
        @Override
        public Location interpolate(float fraction, Location from, Location to) {
            double lat = (to.getLatitude() - from.getLatitude()) * fraction + from.getLatitude();
            double lngDelta = to.getLongitude() - from.getLongitude();

            // Take the shortest path across the 180th meridian.
            if (Math.abs(lngDelta) > 180) {
                lngDelta -= Math.signum(lngDelta) * 360;
            }
            double lng = lngDelta * fraction + from.getLongitude();

            Location result = new Location("");
            result.setLatitude(lat);
            result.setLongitude(lng);
            return result;
        }
    }

    public static class SphericalShortestLocationInterpolator implements BaseLocationInterpolator {

        /* From github.com/googlemaps/android-maps-utils */
        @Override
        public Location interpolate(float fraction, Location from, Location to) {
            // http://en.wikipedia.org/wiki/Slerp
            double fromLat = Math.toRadians(from.getLatitude());
            double fromLng = Math.toRadians(from.getLongitude());
            double toLat = Math.toRadians(to.getLatitude());
            double toLng = Math.toRadians(to.getLongitude());
            double cosFromLat = Math.cos(fromLat);
            double cosToLat = Math.cos(toLat);

            // Computes Spherical interpolation coefficients.
            double angle = computeAngleBetween(fromLat, fromLng, toLat, toLng);
            double sinAngle = Math.sin(angle);
            if (sinAngle < 1E-6) {
                return from;
            }
            double a = Math.sin((1 - fraction) * angle) / sinAngle;
            double b = Math.sin(fraction * angle) / sinAngle;

            // Converts from polar to vector and interpolate.
            double x = a * cosFromLat * Math.cos(fromLng) + b * cosToLat * Math.cos(toLng);
            double y = a * cosFromLat * Math.sin(fromLng) + b * cosToLat * Math.sin(toLng);
            double z = a * Math.sin(fromLat) + b * Math.sin(toLat);

            // Converts interpolated vector back to polar.
            double lat = Math.atan2(z, Math.sqrt(x * x + y * y));
            double lng = Math.atan2(y, x);

            Location result = new Location("");
            result.setLatitude(Math.toDegrees(lat));
            result.setLongitude(Math.toDegrees(lng));

            return result;
        }

        private double computeAngleBetween(double fromLat, double fromLng, double toLat, double toLng) {
            // Haversine's formula
            double dLat = fromLat - toLat;
            double dLng = fromLng - toLng;
            return 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(dLat / 2), 2) +
                    Math.cos(fromLat) * Math.cos(toLat) * Math.pow(Math.sin(dLng / 2), 2)));
        }
    }

    public static class RotationInterpolator extends Foundation {

        public float interpolate(float fraction, float from, float to) {

            double angleDerivation = calculateAngleDerivation(from, to);

            return (float) wholeToHalfCircleBearing(halfToWholeCircleBearing(from) + angleDerivation * fraction);

        }
    }

    @Deprecated
    public boolean init(Context appContext) {
        return super.init(appContext);
    }


    public void slideAndRotateMarker(final Marker marker, double latitude, double longitude, final float rotation, int durationInMs, final BaseLocationInterpolator baseLocationInterpolator, final Listener listener) {

        final LatLng startPosition = marker.getPosition();
        final LatLng finalPosition = new LatLng(latitude, longitude);

        final Location startLocation = new Location("");
        startLocation.setLatitude(startPosition.latitude);
        startLocation.setLongitude(startPosition.longitude);

        final Location finalLocation = new Location("");
        finalLocation.setLatitude(finalPosition.latitude);
        finalLocation.setLongitude(finalPosition.longitude);

        final float startRotation = marker.getRotation();

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = animation.getAnimatedFraction();
                Location newLocation = baseLocationInterpolator.interpolate(v, startLocation, finalLocation);
                LatLng newPosition = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
                marker.setPosition(newPosition);
                marker.setRotation(new RotationInterpolator().interpolate(v, startRotation, rotation));

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

//    public void rotateMarker(final Marker marker, final float rotation, int durationInMs, final OnMapAnimListener listener) {
//
//        final float startRotation = marker.getRotation();
//
//        ValueAnimator valueAnimator = new ValueAnimator();
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float v = animation.getAnimatedFraction();
//                marker.setRotation(new RotationInterpolator().interpolate(v, startRotation, rotation));
//
//            }
//        });
//        valueAnimator.setFloatValues(0, 1); // Ignored.
//        valueAnimator.setDuration(durationInMs);
//        valueAnimator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                if (listener != null)
//                    listener.onComplete();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        valueAnimator.start();
//
//    }


}
