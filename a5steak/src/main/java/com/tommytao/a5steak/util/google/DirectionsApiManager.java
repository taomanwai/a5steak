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

import com.android.volley.RequestQueue;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tommytao.a5steak.util.Foundation;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

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
public class DirectionsApiManager extends GFoundation {

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

    private class ResponseRoute {

        public class Step {

            @SerializedName("distance")
            @Expose
            private Distance distance;
            @SerializedName("duration")
            @Expose
            private Duration duration;
            @SerializedName("end_location")
            @Expose
            private EndLocation endLocation;
            @SerializedName("html_instructions")
            @Expose
            private String htmlInstructions;

            @SerializedName("maneuver")
            @Expose
            private String maneuver;

            @SerializedName("polyline")
            @Expose
            private Polyline polyline;
            @SerializedName("start_location")
            @Expose
            private StartLocation startLocation;
            @SerializedName("travel_mode")
            @Expose
            private String travelMode;

            public String getManeuver() {
                if (maneuver==null)
                    return "";
                return maneuver;
            }

            public void setManeuver(String maneuver) {
                this.maneuver = maneuver;
            }

            public Distance getDistance() {
                return distance;
            }


            public void setDistance(Distance distance) {
                this.distance = distance;
            }

            public Duration getDuration() {
                return duration;
            }

            public void setDuration(Duration duration) {
                this.duration = duration;
            }


            public EndLocation getEndLocation() {
                return endLocation;
            }


            public void setEndLocation(EndLocation endLocation) {
                this.endLocation = endLocation;
            }


            public String getHtmlInstructions() {

                if (htmlInstructions==null)
                    return "";
                return htmlInstructions;
            }


            public void setHtmlInstructions(String htmlInstructions) {
                this.htmlInstructions = htmlInstructions;
            }


            public Polyline getPolyline() {
                return polyline;
            }


            public void setPolyline(Polyline polyline) {
                this.polyline = polyline;
            }


            public StartLocation getStartLocation() {
                return startLocation;
            }


            public void setStartLocation(StartLocation startLocation) {
                this.startLocation = startLocation;
            }


            public String getTravelMode() {
                if (travelMode==null)
                    return "";
                return travelMode;
            }


            public void setTravelMode(String travelMode) {
                this.travelMode = travelMode;
            }

        }

        public class StartLocation {

            @SerializedName("lat")
            @Expose
            private Double lat;
            @SerializedName("lng")
            @Expose
            private Double lng;


            public Double getLat() {
                if (lat==null)
                    return Double.NaN;
                return lat;
            }


            public void setLat(Double lat) {
                this.lat = lat;
            }


            public Double getLng() {
                if (lng==null)
                    return Double.NaN;
                return lng;
            }

            public void setLng(Double lng) {
                this.lng = lng;
            }

        }

        public class Polyline {

            @SerializedName("points")
            @Expose
            private String points;


            public String getPoints() {
                if (points==null)
                    return "";
                return points;
            }


            public void setPoints(String points) {
                this.points = points;
            }

        }

        public class OverviewPolyline {

            @SerializedName("points")
            @Expose
            private String points;


            public String getPoints() {
                if (points==null)
                    return "";
                return points;
            }

            public void setPoints(String points) {
                this.points = points;
            }

        }

        public class Leg {

            @SerializedName("steps")
            @Expose
            private List<Step> steps = new ArrayList<Step>();


            public List<Step> getSteps() {
                if (steps==null)
                    return new ArrayList<>();
                return steps;
            }


            public void setSteps(List<Step> steps) {
                this.steps = steps;
            }

        }

        public class EndLocation {

            @SerializedName("lat")
            @Expose
            private Double lat;
            @SerializedName("lng")
            @Expose
            private Double lng;

            public Double getLat() {
                if (lat==null)
                    return Double.NaN;
                return lat;
            }

            public void setLat(Double lat) {
                this.lat = lat;
            }

            public Double getLng() {
                if (lng==null)
                    return Double.NaN;
                return lng;
            }


            public void setLng(Double lng) {
                this.lng = lng;
            }

        }

        public class Duration {

            @SerializedName("text")
            @Expose
            private String text;
            @SerializedName("value")
            @Expose
            private Integer value;


            public String getText() {
                if (text==null)
                    return "";
                return text;
            }


            public void setText(String text) {
                this.text = text;
            }


            public Integer getValue() {
                if (value==null)
                    return -1;
                return value;
            }


            public void setValue(Integer value) {
                this.value = value;
            }

        }

        public class Distance {

            @SerializedName("text")
            @Expose
            private String text;
            @SerializedName("value")
            @Expose
            private Integer value;


            public String getText() {
                if (text==null)
                    return "";
                return text;
            }


            public void setText(String text) {
                this.text = text;
            }


            public Integer getValue() {
                if (value==null)
                    return -1;
                return value;
            }


            public void setValue(Integer value) {
                this.value = value;
            }

        }

        public class Route {

            @SerializedName("legs")
            @Expose
            private List<Leg> legs = new ArrayList<Leg>();
            @SerializedName("overview_polyline")
            @Expose
            private OverviewPolyline overviewPolyline;


            public List<Leg> getLegs() {
                if(legs==null)
                    return new ArrayList<>();
                return legs;
            }


            public void setLegs(List<Leg> legs) {
                this.legs = legs;
            }


            public OverviewPolyline getOverviewPolyline() {
                return overviewPolyline;
            }


            public void setOverviewPolyline(OverviewPolyline overviewPolyline) {
                this.overviewPolyline = overviewPolyline;
            }

        }

        @SerializedName("routes")
        @Expose
        private List<Route> routes = new ArrayList<Route>();
        @SerializedName("status")
        @Expose
        private String status;


        public List<Route> getRoutes() {
            if (routes==null)
                return new ArrayList<>();
            return routes;
        }


        public void setRoutes(List<Route> routes) {
            this.routes = routes;
        }


        public String getStatus() {
            if (status==null)
                return "";
            return status;
        }


        public void setStatus(String status) {
            this.status = status;
        }

    }


    public static interface OnRouteListener {

        public void returnSteps(ArrayList<Step> steps, Polyline polyline);

    }

    public static class Polyline extends Foundation {

        String encodedPolyline = "";
        ArrayList<Location> locations = new ArrayList<>();

        @Deprecated
        public boolean init(Context context) {
            return super.init(context);
        }

        @Deprecated
        public boolean init(Context context, RequestQueue requestQueue) {
            return super.init(context, requestQueue);
        }

        @Deprecated
        public boolean isInitialized() {
            return super.isInitialized();
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


            boolean hasException = false;
            try {
                for (int i = start; i <= end; i++) {
                    distanceInTesting = calculateDistanceInMeter(locations.get(i).getLatitude(), locations.get(i).getLongitude(), latitude, longitude);
                    if (distanceInTesting < closestDistance) {

                        closestPtIndex = i;
                        closestDistance = (int) distanceInTesting;

                    } else {
                        // do nothing
                    }


                }
            } catch (Exception e){
                e.printStackTrace();
                hasException = true;
            }

            if (hasException)
                return -1;

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

        public static final int MANEUVER_DESTINATION_ARRIVED = 99;

        private int distanceInMeter;
        private String distanceInText = "";

        private int durationInMs;
        private String durationInText = "";

        private Location startLocation;
        private Location endLocation;

        private Polyline polyline = new Polyline("");

        private String instructionsInHtml = "";
        private String instructionsInText = "";

        private int maneuver = MANEUVER_NONE;

        private String travelMode = "";

        private boolean spoken;

        public Step(int distanceInMeter, String distanceInText, int durationInMs, String durationInText, double startLatitude, double startLongitude, double endLatitude, double endLongitude, String encodedPolyline, String instructionsInHtml, int maneuver, String travelMode) {
            this.distanceInMeter = distanceInMeter;
            this.distanceInText = distanceInText;
            this.durationInMs = durationInMs;

            this.startLocation = latLngToLocation(startLatitude, startLongitude);
            this.endLocation = latLngToLocation(endLatitude, endLongitude);

            this.polyline = new Polyline(encodedPolyline);
            this.instructionsInHtml = instructionsInHtml;
            this.instructionsInText = htmlToText(instructionsInHtml);
            this.maneuver = maneuver;
            this.travelMode = travelMode;
        }

        @Deprecated
        public boolean init(Context context) {
            return super.init(context);
        }

        @Deprecated
        public boolean init(Context context, RequestQueue requestQueue) {
            return super.init(context, requestQueue);
        }

        @Deprecated
        public boolean isInitialized() {
            return super.isInitialized();
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

        public String getInstructionsInText() {
            return instructionsInText;
        }

        public String getTravelMode() {
            return travelMode;
        }

        public boolean isSpoken() {
            return spoken;
        }

        public void setSpoken(boolean spoken) {
            this.spoken = spoken;
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
    public boolean init(Context context) {
        return super.init(context);
    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }

    public boolean init(Context appContext, RequestQueue requestQueue, String clientIdForWork, String cryptoForWork) {

        if (!super.init(appContext)) {
            return false;
        }

        this.requestQueue = requestQueue;

        this.clientIdForWork = clientIdForWork;
        this.cryptoForWork = cryptoForWork;

        getPrefs().edit().clear().apply();

        return true;
    }

    private boolean forWork() {
        return !TextUtils.isEmpty(clientIdForWork) && !TextUtils.isEmpty(cryptoForWork);
    }

    private String genRouteLink(double startLatitude, double startLongitude, double endLatitude, double endLongitude, String avoid, final Locale locale) {

        String localeString = locale.getLanguage() + "-" + locale.getCountry();

        String result = String.format("https://maps.google.com/maps/api/directions/json?origin=%.6f,%.6f&destination=%.6f,%.6f&avoid=%s&language=%s", startLatitude, startLongitude, endLatitude, endLongitude, avoid,
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
            listener.returnSteps(new ArrayList<Step>(), new Polyline(""));
            return;
        }

        // == run in bg ==

        // init GSON in main thread
        getGson();

        new AsyncTask<JSONObject, Void, Pair<ArrayList<Step>, Polyline>>() {

            @Override
            protected Pair<ArrayList<Step>, Polyline> doInBackground(JSONObject... responsesJObj) {

                Pair<ArrayList<Step>, Polyline> resultPair = null;

                if (responsesJObj.length != 1) {
                    return resultPair;
                }

                JSONObject responseJObj = responsesJObj[0];

                ResponseRoute responseRoute = getGson().fromJson("" + responseJObj, ResponseRoute.class);


                try{

                    if (!"OK".equals(responseRoute.getStatus())) {
                        return resultPair;
                    }

                    ArrayList<Step> steps = new ArrayList<>();
                    String overviewPolylinePoints = responseRoute.getRoutes().get(0).getOverviewPolyline().getPoints();


                    ResponseRoute.Step step = null;
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
                    String maneuverStr = "";
                    int maneuver = Step.MANEUVER_NONE;
                    String travelMode = "";

                    for (int i=0; i<responseRoute.getRoutes().get(0).getLegs().get(0).getSteps().size(); i++){
                        step = responseRoute.getRoutes().get(0).getLegs().get(0).getSteps().get(i);
                        distanceInMeter = step.getDistance().getValue();
                        distanceInText = step.getDistance().getText();
                        durationInMs = step.getDuration().getValue() * 1000;
                        durationInText = step.getDuration().getText();
                        startLatitude = step.getStartLocation().getLat();
                        startLongitude = step.getStartLocation().getLng();
                        endLatitude = step.getEndLocation().getLat();
                        endLongitude = step.getEndLocation().getLng();
                        encodedPolyline = step.getPolyline().getPoints();
                        instructionsInHtml = step.getHtmlInstructions();
                        travelMode = step.getTravelMode();

                        maneuverStr = step.getManeuver();
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

                        steps.add(new Step(distanceInMeter, distanceInText, durationInMs, durationInText,
                                startLatitude, startLongitude, endLatitude, endLongitude,
                                encodedPolyline, instructionsInHtml,
                                maneuver, travelMode));

                    }

                    resultPair =  new Pair<>(steps, new Polyline(overviewPolylinePoints) );


                } catch (Exception e){
                    e.printStackTrace();
                }

                return resultPair;


            }

            @Override
            protected void onPostExecute(Pair<ArrayList<Step>, Polyline> result) {

                if (result == null) {
                    listener.returnSteps(new ArrayList<Step>(), new Polyline(""));
                    return;
                }

                listener.returnSteps(result.first, result.second);
            }

        }.executeOnExecutor(Executors.newCachedThreadPool(), responseJObj);

        // == End of run in bg ==


    }

    public void cache(double startLatitude, double startLongitude, double endLatitude, double endLongitude, String avoid, final Locale locale) {

        route(startLatitude, startLongitude, endLatitude, endLongitude, avoid, locale, null);

    }

    public String getCombinedAvoids(ArrayList<String> avoids){

        StringBuffer sbResult = new StringBuffer();


        int i=0;
        for (String avoid : avoids){

            sbResult.append(avoid + ((i==(avoids.size()-1)) ? "" : "|"));

            i++;
        }

        return "" + sbResult;

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
