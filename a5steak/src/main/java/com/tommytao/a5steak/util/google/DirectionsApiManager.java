package com.tommytao.a5steak.util.google;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.Toast;

import com.tommytao.a5steak.util.Foundation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Responsible for Google Directions API
 * <p/>
 * Note:
 * Users of the free API:
 * 2500 directions requests per 24 hour period.
 * Up to 8 waypoints allowed in each request. Waypoints are not available for transit directions.
 * 2 requests per second.
 * <p/>
 * Google Maps API for Work customers:
 * 100 000 directions requests per 24 hour period.
 * 23 waypoints allowed in each request. Waypoints are not available for transit directions.
 * 10 requests per second.
 */
public class DirectionsApiManager extends Foundation {

    private static DirectionsApiManager instance;

    public static DirectionsApiManager getInstance() {
        if (instance != null)
            return instance;
        else
            return instance = new DirectionsApiManager();

    }

    private DirectionsApiManager() {

    }

    // --

    public static interface OnRouteListener {

        public void returnSteps(ArrayList<Step> steps, Polyline polyline);

    }

    public static class Polyline extends Foundation {

        String encodedPolyline = "";
        ArrayList<Location> locations = new ArrayList<>();

        @Deprecated
        public boolean init(Context appContext) {
            return super.init(appContext);
        }

        public Polyline(String encodedPolyline) {
            this.encodedPolyline = encodedPolyline;
            this.locations = encodedPolylineToLocations(this.encodedPolyline);
            addBearingToLocations();
        }

        @Override
        public String toString() {
            return encodedPolyline;
        }

        public ArrayList<Location> getLocations() {
            return locations;
        }

        public Location getLocationAtFraction(float fraction) {

            Location result = null;

            if (getLocations().isEmpty())
                return result;

            result = getLocations().get((int) ((getLocations().size() - 1) * fraction));

            return result;

        }

        public int getClosestPointIndexFromLatLng(double latitude, double longitude, int start, int end) {

            if (locations.isEmpty())
                return -1;

            int closestPtIndex = -1;
            double closestDistance = WHOLE_WORLD_RADIUS_IN_METER + 1; // plus 1 to make it as max impossible to reach value !
            double distanceInTesting = -1;

//            int i = 0;
//            for (Location location : locations) {
//
//                distanceInTesting = calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), latitude, longitude);
//                if (distanceInTesting < closestDistance) {
//
//                    closestPtIndex = i;
//                    closestDistance = (int) distanceInTesting;
//
//                } else {
//                    // do nothing
//                }
//
//                i++;
//            }

            for (int i = start; i <= end; i++) {
                distanceInTesting = calculateDistanceInMeter(locations.get(i).getLatitude(), locations.get(i).getLongitude(), latitude, longitude);
                if (distanceInTesting < closestDistance) {

                    closestPtIndex = i;
                    closestDistance = (int) distanceInTesting;

                } else {
                    // do nothing
                }


            }

            return closestPtIndex;


        }

        public boolean isPassing(double latitude, double longitude, double toleranceInMeter) {

            // TODO MVP too much copy & paste
            double closestDistance = WHOLE_WORLD_RADIUS_IN_METER + 1; // plus 1 to make it as max impossible to reach value !
            double distanceInTesting = -1;

            for (Location location : locations) {

                distanceInTesting = calculateDistanceInMeter(location.getLatitude(), location.getLongitude(), latitude, longitude);
                if (distanceInTesting < closestDistance) {

                    closestDistance = (int) distanceInTesting;

                } else {
                    // do nothing
                }

                if (closestDistance <= toleranceInMeter) {
                    return true;
                }
            }

            return false;


        }


        private void addBearingToLocations() {

            if (locations.size() == 1) {

                locations.get(0).setBearing(Float.NaN);

                return;
            }


            int index = 0;
            for (Location location : locations) {

                Location targetLocation = getLocations().get(index);

                Location previousLocation = null;
                Location nextLocation = null;

                try {
                    previousLocation = getLocations().get(index - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    nextLocation = getLocations().get(index + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                float rotation = Float.NaN;
                if (previousLocation != null && nextLocation != null) {

                    float previousRotation = calculateBearingInDegree(previousLocation.getLatitude(), previousLocation.getLongitude(), targetLocation.getLatitude(), targetLocation.getLongitude());
                    float nextRotation = calculateBearingInDegree(targetLocation.getLatitude(), targetLocation.getLongitude(), nextLocation.getLatitude(), nextLocation.getLongitude());


                    if (!Float.isNaN(previousRotation) && !Float.isNaN(nextRotation))
                        rotation = (float) (previousRotation + calculateAngleDerivation(previousRotation, nextRotation) / 2);
                    else if (!Float.isNaN(previousRotation)) {
                        rotation = previousRotation;
                    } else if (!Float.isNaN(nextRotation)) {
                        rotation = nextRotation;
                    } else {
                        // do nothing, i.e. rotation = Float.NaN
                    }


                } else if (previousLocation != null) {
                    rotation = calculateBearingInDegree(previousLocation.getLatitude(), previousLocation.getLongitude(), targetLocation.getLatitude(), targetLocation.getLongitude());
                } else if (nextLocation != null) {
                    rotation = calculateBearingInDegree(targetLocation.getLatitude(), targetLocation.getLongitude(), nextLocation.getLatitude(), nextLocation.getLongitude());
                } else {
                    // do nothing, i.e. rotation = Float.NaN
                }

                location.setBearing(rotation);

                index++;

            }
        }


    }

    public static class Step extends Foundation {

        public static final int MANEUVER_NONE = -1;

        public static final int MANEUVER_TURN_SHARP_LEFT = 0;
        public static final int MANEUVER_UTURN_RIGHT = 1;
        public static final int MANEUVER_TURN_SLIGHT_RIGHT = 2;
        public static final int MANEUVER_MERGE = 3;
        public static final int MANEUVER_ROUNDABOUT_LEFT = 4;
        public static final int MANEUVER_ROUNDABOUT_RIGHT = 5;
        public static final int MANEUVER_UTURN_LEFT = 6;
        public static final int MANEUVER_TURN_SLIGHT_LEFT = 7;
        public static final int MANEUVER_TURN_LEFT = 8;
        public static final int MANEUVER_RAMP_RIGHT = 9;
        public static final int MANEUVER_TURN_RIGHT = 10;
        public static final int MANEUVER_FORK_RIGHT = 11;
        public static final int MANEUVER_STRAIGHT = 12;
        public static final int MANEUVER_FORK_LEFT = 13;
        public static final int MANEUVER_FERRY_TRAIN = 14;
        public static final int MANEUVER_TURN_SHARP_RIGHT = 15;
        public static final int MANEUVER_RAMP_LEFT = 16;
        public static final int MANEUVER_FERRY = 17;
        public static final int MANEUVER_KEEP_LEFT = 18;
        public static final int MANEUVER_KEEP_RIGHT = 19;

        private int distanceInMeter;
        private String distanceInText = "";

        private int durationInMs;
        private String durationInText = "";

        private Location startLocation;
        private Location endLocation;

        private Polyline polyline;
        private ArrayList<Location> polylineLocations = new ArrayList<>();

        private String instructionsInHtml = "";
        private int maneuver = MANEUVER_NONE;

        private String travelMode = "";

        public Step(int distanceInMeter, String distanceInText, int durationInMs, String durationInText, double startLatitude, double startLongitude, double endLatitude, double endLongitude, String encodedPolyline, String instructionsInHtml, int maneuver, String travelMode) {
            this.distanceInMeter = distanceInMeter;
            this.distanceInText = distanceInText;
            this.durationInMs = durationInMs;

            this.startLocation = latLngToLocation(startLatitude, startLongitude);
            this.endLocation = latLngToLocation(endLatitude, endLongitude);

            this.polyline = new Polyline(encodedPolyline);
            this.instructionsInHtml = instructionsInHtml;
            this.maneuver = maneuver;
            this.travelMode = travelMode;
        }

        @Deprecated
        public boolean init(Context appContext) {
            return super.init(appContext);
        }

        public int getDistanceInMeter() {
            return distanceInMeter;
        }

        public String getDistanceInText() {
            return distanceInText;
        }

        public Location getStartLocation() {
            return startLocation;
        }

        public Location getEndLocation() {
            return endLocation;
        }

        public int getDurationInMs() {
            return durationInMs;
        }

        public String getDurationInText() {
            return durationInText;
        }

        public String getInstructionsInHtml() {
            return instructionsInHtml;
        }

        public String getTravelMode() {
            return travelMode;
        }

        public Polyline getPolyline() {
            return polyline;
        }


        public int getManeuver() {
            return maneuver;
        }


    }

    public static final String AVOID_NONE = "";
    public static final String AVOID_TOLLS = "tolls";
    public static final String AVOID_HIGHWAYS = "highways";
    public static final String AVOID_FERRIES = "ferries";
    public static final String AVOID_INDOOR = "indoor";


    public final int DEFAULT_MAX_NUM_OF_RETRIES = 3;

    public final int EXPIRY_PERIOD_OF_CACHED_ROUTE_RESPONSE_IN_MS = 15 * 60 * 1000; // 15 min

    public final String PREFS_NAME = "DirectionsApiManager";

    public final String PREFS_JSON_PREFIX = "DirectionsApiManager.JSON.LatLngLocale_";

    public final String PREFS_TIMESTAMP_PREFIX = "DirectionsApiManager.Timestamp.LatLngLocale_";

    private SharedPreferences prefs;

    public SharedPreferences getPrefs() {

        if (prefs == null)
            prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        return prefs;
    }

    @Deprecated
    public boolean init(Context appContext) {
        return super.init(appContext);
    }

    public boolean init(Context appContext, String clientIdForWork, String cryptoForWork) {

        if (!super.init(appContext)) {
            return false;
        }

        this.clientIdForWork = clientIdForWork;
        this.cryptoForWork = cryptoForWork;

        getPrefs().edit().clear().commit();

        return true;
    }

    private boolean forWork() {
        return !TextUtils.isEmpty(clientIdForWork) && !TextUtils.isEmpty(cryptoForWork);
    }

    private String genRouteLink(double startLatitude, double startLongitude, double endLatitude, double endLongitude, String avoid, final Locale locale) {

        String localeString = locale.getLanguage() + "-" + locale.getCountry();

        String result = String.format("https://maps.googleapis.com/maps/api/directions/json?origin=%.6f,%.6f&destination=%.6f,%.6f&avoid=%s&language=%s", startLatitude, startLongitude, endLatitude, endLongitude, avoid,
                localeString);

        if (!TextUtils.isEmpty(avoid)) {
            result += "&avoid=" + avoid;
        }

        result = forWork() ? signToForWork(result, API_DOMAIN_FOR_WORK, clientIdForWork, cryptoForWork) : result;

        if (result.endsWith("\r\n"))
            result = result.substring(0, result.length() - 3);

        return result;

    }

    private void response2Route(JSONObject responseJObj, final OnRouteListener listener) {

        if (listener == null)
            return;

        if (responseJObj == null) {
            listener.returnSteps(new ArrayList<Step>(), null);
            return;
        }

        // == run in bg ==

        new AsyncTask<JSONObject, Void, Pair<ArrayList<Step>, Polyline>>() {

            @Override
            protected Pair<ArrayList<Step>, Polyline> doInBackground(JSONObject... responsesJObj) {

                if (responsesJObj.length != 1) {
                    return null;
                }

                JSONObject responseJObj = responsesJObj[0];

                String overviewPolylinePoints = "";
                ArrayList<Step> steps = new ArrayList<>();

                boolean hasException = false;
                try {

                    String status = "";
                    Step step = null;
                    int distanceInMeter = -1;
                    String distanceInText = "";
                    int durationInMs = -1;
                    String durationInText = "";
                    double startLatitude = Double.NaN;
                    double startLongitude = Double.NaN;
                    double endLatitude = Double.NaN;
                    double endLongitude = Double.NaN;
                    String encodedPolyline = "";
                    String instructionsInHtml = "";
                    int maneuver = Step.MANEUVER_NONE;
                    String maneuverStr = "";
                    String travelMode = "";
                    JSONObject stepJObj = null;

                    status = responseJObj.getString("status");

                    if (!"OK".equals(status)) {
                        return null;
                    }

                    JSONArray routesJArray = responseJObj.getJSONArray("routes");
                    JSONArray legsJArray = routesJArray.getJSONObject(0).getJSONArray("legs");
                    JSONArray stepsJArray = legsJArray.getJSONObject(0).getJSONArray("steps");
                    overviewPolylinePoints = routesJArray.getJSONObject(0).getJSONObject("overview_polyline").getString("points");


                    for (int i = 0; i < stepsJArray.length(); i++) {

                        stepJObj = stepsJArray.getJSONObject(i);
                        distanceInMeter = stepJObj.getJSONObject("distance").getInt("value");
                        distanceInText = stepJObj.getJSONObject("distance").getString("text");
                        durationInMs = stepJObj.getJSONObject("duration").getInt("value") * 1000;
                        durationInText = stepJObj.getJSONObject("duration").getString("text");

                        startLatitude = stepJObj.getJSONObject("start_location").getDouble("lat");
                        startLongitude = stepJObj.getJSONObject("start_location").getDouble("lng");
                        endLatitude = stepJObj.getJSONObject("end_location").getDouble("lat");
                        endLongitude = stepJObj.getJSONObject("end_location").getDouble("lng");

                        encodedPolyline = stepJObj.getJSONObject("polyline").getString("points");
                        instructionsInHtml = stepJObj.getString("html_instructions");

                        maneuverStr = stepJObj.optString("maneuver", "");
                        switch (maneuverStr) {
                            case "turn-sharp-left":
                                maneuver = Step.MANEUVER_TURN_SHARP_LEFT;
                                break;

                            case "uturn-right":
                                maneuver = Step.MANEUVER_UTURN_RIGHT;
                                break;

                            case "turn-slight-right":
                                maneuver = Step.MANEUVER_TURN_SLIGHT_RIGHT;
                                break;

                            case "merge":
                                maneuver = Step.MANEUVER_MERGE;
                                break;

                            case "roundabout-left":
                                maneuver = Step.MANEUVER_ROUNDABOUT_LEFT;
                                break;

                            case "roundabout-right":
                                maneuver = Step.MANEUVER_ROUNDABOUT_RIGHT;
                                break;

                            case "uturn-left":
                                maneuver = Step.MANEUVER_UTURN_LEFT;
                                break;

                            case "turn-slight-left":
                                maneuver = Step.MANEUVER_TURN_SLIGHT_LEFT;
                                break;

                            case "turn-left":
                                maneuver = Step.MANEUVER_TURN_LEFT;
                                break;

                            case "ramp-right":
                                maneuver = Step.MANEUVER_RAMP_RIGHT;
                                break;

                            case "turn-right":
                                maneuver = Step.MANEUVER_TURN_RIGHT;
                                break;

                            case "fork-right":
                                maneuver = Step.MANEUVER_FORK_RIGHT;
                                break;

                            case "straight":
                                maneuver = Step.MANEUVER_STRAIGHT;
                                break;

                            case "fork-left":
                                maneuver = Step.MANEUVER_FORK_LEFT;
                                break;


                            case "ferry-train":
                                maneuver = Step.MANEUVER_FERRY_TRAIN;
                                break;

                            case "turn-sharp-right":
                                maneuver = Step.MANEUVER_TURN_SHARP_RIGHT;
                                break;

                            case "ramp-left":
                                maneuver = Step.MANEUVER_RAMP_LEFT;
                                break;

                            case "ferry":
                                maneuver = Step.MANEUVER_FERRY;
                                break;

                            case "keep-left":
                                maneuver = Step.MANEUVER_KEEP_LEFT;
                                break;

                            case "keep-right":
                                maneuver = Step.MANEUVER_KEEP_RIGHT;
                                break;


                            default:
                                maneuver = Step.MANEUVER_NONE;
                                break;
                        }

                        travelMode = stepJObj.getString("travel_mode");

                        step = new Step(distanceInMeter, distanceInText, durationInMs, durationInText,
                                startLatitude, startLongitude, endLatitude, endLongitude,
                                encodedPolyline, instructionsInHtml,
                                maneuver, travelMode);

                        steps.add(step);

                    }

                } catch (Exception e) {

                    e.printStackTrace();
                    hasException = true;

                }

                Pair<ArrayList<Step>, Polyline> result =
                        new Pair<ArrayList<Step>, Polyline>(hasException ? new ArrayList<Step>() : steps, new Polyline(overviewPolylinePoints) );

                return result;

            }

            @Override
            protected void onPostExecute(Pair<ArrayList<Step>, Polyline> result) {

                if (result == null) {
                    listener.returnSteps(new ArrayList<Step>(), null);
                    return;
                }

                listener.returnSteps(result.first, result.second);
            }

        }.execute(responseJObj);

        // == End of run in bg ==


//        String overviewPolylinePoints = "";
//        ArrayList<Step> steps = new ArrayList<>();
//
//        boolean hasException = false;
//        try {
//
//            String status = "";
//            Step step = null;
//            int distanceInMeter = -1;
//            String distanceInText = "";
//            int durationInMs = -1;
//            String durationInText = "";
//            double startLatitude = Double.NaN;
//            double startLongitude = Double.NaN;
//            double endLatitude = Double.NaN;
//            double endLongitude = Double.NaN;
//            String encodedPolyline = "";
//            String instructionsInHtml = "";
//            int maneuver = Step.MANEUVER_NONE;
//            String maneuverStr = "";
//            String travelMode = "";
//            JSONObject stepJObj = null;
//
//            status = responseJObj.getString("status");
//
//            if (!"OK".equals(status)) {
//                listener.returnSteps(new ArrayList<Step>(), null);
//                return;
//            }
//
//            JSONArray routesJArray = responseJObj.getJSONArray("routes");
//            JSONArray legsJArray = routesJArray.getJSONObject(0).getJSONArray("legs");
//            JSONArray stepsJArray = legsJArray.getJSONObject(0).getJSONArray("steps");
//            overviewPolylinePoints = routesJArray.getJSONObject(0).getJSONObject("overview_polyline").getString("points");
//
//
//            for (int i = 0; i < stepsJArray.length(); i++) {
//
//                stepJObj = stepsJArray.getJSONObject(i);
//                distanceInMeter = stepJObj.getJSONObject("distance").getInt("value");
//                distanceInText = stepJObj.getJSONObject("distance").getString("text");
//                durationInMs = stepJObj.getJSONObject("duration").getInt("value") * 1000;
//                durationInText = stepJObj.getJSONObject("duration").getString("text");
//
//                startLatitude = stepJObj.getJSONObject("start_location").getDouble("lat");
//                startLongitude = stepJObj.getJSONObject("start_location").getDouble("lng");
//                endLatitude = stepJObj.getJSONObject("end_location").getDouble("lat");
//                endLongitude = stepJObj.getJSONObject("end_location").getDouble("lng");
//
//                encodedPolyline = stepJObj.getJSONObject("polyline").getString("points");
//                instructionsInHtml = stepJObj.getString("html_instructions");
//
//                maneuverStr = stepJObj.optString("maneuver", "");
//                switch (maneuverStr) {
//                    case "turn-sharp-left":
//                        maneuver = Step.MANEUVER_TURN_SHARP_LEFT;
//                        break;
//
//                    case "uturn-right":
//                        maneuver = Step.MANEUVER_UTURN_RIGHT;
//                        break;
//
//                    case "turn-slight-right":
//                        maneuver = Step.MANEUVER_TURN_SLIGHT_RIGHT;
//                        break;
//
//                    case "merge":
//                        maneuver = Step.MANEUVER_MERGE;
//                        break;
//
//                    case "roundabout-left":
//                        maneuver = Step.MANEUVER_ROUNDABOUT_LEFT;
//                        break;
//
//                    case "roundabout-right":
//                        maneuver = Step.MANEUVER_ROUNDABOUT_RIGHT;
//                        break;
//
//                    case "uturn-left":
//                        maneuver = Step.MANEUVER_UTURN_LEFT;
//                        break;
//
//                    case "turn-slight-left":
//                        maneuver = Step.MANEUVER_TURN_SLIGHT_LEFT;
//                        break;
//
//                    case "turn-left":
//                        maneuver = Step.MANEUVER_TURN_LEFT;
//                        break;
//
//                    case "ramp-right":
//                        maneuver = Step.MANEUVER_RAMP_RIGHT;
//                        break;
//
//                    case "turn-right":
//                        maneuver = Step.MANEUVER_TURN_RIGHT;
//                        break;
//
//                    case "fork-right":
//                        maneuver = Step.MANEUVER_FORK_RIGHT;
//                        break;
//
//                    case "straight":
//                        maneuver = Step.MANEUVER_STRAIGHT;
//                        break;
//
//                    case "fork-left":
//                        maneuver = Step.MANEUVER_FORK_LEFT;
//                        break;
//
//
//                    case "ferry-train":
//                        maneuver = Step.MANEUVER_FERRY_TRAIN;
//                        break;
//
//                    case "turn-sharp-right":
//                        maneuver = Step.MANEUVER_TURN_SHARP_RIGHT;
//                        break;
//
//                    case "ramp-left":
//                        maneuver = Step.MANEUVER_RAMP_LEFT;
//                        break;
//
//                    case "ferry":
//                        maneuver = Step.MANEUVER_FERRY;
//                        break;
//
//                    case "keep-left":
//                        maneuver = Step.MANEUVER_KEEP_LEFT;
//                        break;
//
//                    case "keep-right":
//                        maneuver = Step.MANEUVER_KEEP_RIGHT;
//                        break;
//
//
//                    default:
//                        maneuver = Step.MANEUVER_NONE;
//                        break;
//                }
//
//                travelMode = stepJObj.getString("travel_mode");
//
//                step = new Step(distanceInMeter, distanceInText, durationInMs, durationInText,
//                        startLatitude, startLongitude, endLatitude, endLongitude,
//                        encodedPolyline, instructionsInHtml,
//                        maneuver, travelMode);
//
//                steps.add(step);
//
//            }
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//            hasException = true;
//
//        }
//
//        listener.returnSteps(hasException ? new ArrayList<Step>() : steps, new Polyline(overviewPolylinePoints));

    }

    public void cache(double startLatitude, double startLongitude, double endLatitude, double endLongitude, String avoid, final Locale locale) {

        route(startLatitude, startLongitude, endLatitude, endLongitude, avoid, locale, null);

    }


    public void route(final double startLatitude, final double startLongitude, final double endLatitude, final double endLongitude, final String avoid, final Locale locale, final OnRouteListener listener) {

        final JSONObject cachedRouteResponse = loadRouteResponseFromPrefs(startLatitude, startLongitude, endLatitude, endLongitude, avoid, locale);

        if (cachedRouteResponse != null) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    response2Route(cachedRouteResponse, listener);
                }
            });

            return;
        }


        String link = genRouteLink(startLatitude, startLongitude, endLatitude, endLongitude, avoid, locale);

        httpGetJSON(link, DEFAULT_MAX_NUM_OF_RETRIES, new OnHttpGetJSONListener() {

            @Override
            public void onComplete(JSONObject response) {

                saveRouteResponseToPrefs(response, startLatitude, startLongitude, endLatitude, endLongitude, avoid, locale);
                response2Route(response, listener);

            }

        });


    }

    private String buildPrefsSuffix(double startLatitude, double startLongitude, double endLatitude, double endLongitude, String avoid, Locale locale) {
        return String.format("%.6f", startLatitude) + "," +
                String.format("%.6f", startLongitude) + "," +
                String.format("%.6f", endLatitude) + "," +
                String.format("%.6f", endLongitude) + "," +
                avoid + "," +
                locale.getLanguage() + "-" + locale.getCountry();
    }

    private void saveRouteResponseToPrefs(JSONObject response, double startLatitude, double startLongitude, double endLatitude, double endLongitude, String avoid, Locale locale) {

        String suffix = buildPrefsSuffix(startLatitude, startLongitude, endLatitude, endLongitude, avoid, locale);
        String jsonKey = PREFS_JSON_PREFIX + suffix;
        String timestampKey = PREFS_TIMESTAMP_PREFIX + suffix;

        SharedPreferences.Editor edit = getPrefs().edit();
        edit.putString(jsonKey, "" + response);
        edit.putLong(timestampKey, System.currentTimeMillis());
        edit.commit();

    }

    private JSONObject loadRouteResponseFromPrefs(double startLatitude, double startLongitude, double endLatitude, double endLongitude, String avoid, Locale locale) {

        String suffix = buildPrefsSuffix(startLatitude, startLongitude, endLatitude, endLongitude, avoid, locale);
        String jsonKey = PREFS_JSON_PREFIX + suffix;
        String timestampKey = PREFS_TIMESTAMP_PREFIX + suffix;

        long timestamp = getPrefs().getLong(timestampKey, -1);

        if ((System.currentTimeMillis() - timestamp) > EXPIRY_PERIOD_OF_CACHED_ROUTE_RESPONSE_IN_MS)
            return null;

        JSONObject result = null;
        String responseStr = getPrefs().getString(jsonKey, "");
        try {
            result = new JSONObject(responseStr);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;


    }


    public void goToNav(Activity activity, double latitude, double longitude, String errMsgWhenNoNavApp) {


        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:ll=" + latitude + "," + longitude));

        // Check intent
        PackageManager packageManager = activity.getPackageManager();
        List activities = packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY);

        if (activities.isEmpty()) {
            Toast.makeText(activity, errMsgWhenNoNavApp, Toast.LENGTH_SHORT).show();
            return;
        }

        activity.startActivity(intent);

    }


}
