package com.tommytao.a5steak.util.google;

import com.tommytao.a5steak.util.Foundation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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

    public static interface OnRouteListener {

        public void returnStepList(ArrayList<Step> stepList);

    }

    public static class Step {

        public static int MANEUVER_NONE = -1;

        public static int MANEUVER_TURN_SHARP_LEFT = 0;
        public static int MANEUVER_UTURN_RIGHT = 1;
        public static int MANEUVER_TURN_SLIGHT_RIGHT = 2;
        public static int MANEUVER_MERGE = 3;
        public static int MANEUVER_ROUNDABOUT_LEFT = 4;
        public static int MANEUVER_ROUNDABOUT_RIGHT = 5;
        public static int MANEUVER_UTURN_LEFT = 6;
        public static int MANEUVER_TURN_SLIGHT_LEFT = 7;
        public static int MANEUVER_TURN_LEFT = 8;
        public static int MANEUVER_RAMP_RIGHT = 9;
        public static int MANEUVER_TURN_RIGHT = 10;
        public static int MANEUVER_FORK_RIGHT = 11;
        public static int MANEUVER_STRAIGHT = 12;
        public static int MANEUVER_FORK_LEFT = 13;
        public static int MANEUVER_FERRY_TRAIN = 14;
        public static int MANEUVER_TURN_SHARP_RIGHT = 15;
        public static int MANEUVER_RAMP_LEFT = 16;
        public static int MANEUVER_FERRY = 17;
        public static int MANEUVER_KEEP_LEFT = 18;
        public static int MANEUVER_KEEP_RIGHT = 19;

        private int distanceInMeter;
        private String distanceInText = "";

        private int durationInMs;
        private String durationInText = "";

        private double startLatitude = Double.NaN;
        private double startLongitude = Double.NaN;

        private double endLatitude = Double.NaN;
        private double endLongitude = Double.NaN;

        private String polylinePoints = "";

        private String instructionsInHtml = "";
        private int maneuver = MANEUVER_NONE;

        private String travelMode = "";

        public Step(int distanceInMeter, String distanceInText, int durationInMs, String durationInText, double startLatitude, double startLongitude, double endLatitude, double endLongitude, String polylinePoints, String instructionsInHtml, int maneuver, String travelMode) {
            this.distanceInMeter = distanceInMeter;
            this.distanceInText = distanceInText;
            this.durationInMs = durationInMs;
            this.durationInText = durationInText;
            this.startLatitude = startLatitude;
            this.startLongitude = startLongitude;
            this.endLatitude = endLatitude;
            this.endLongitude = endLongitude;
            this.polylinePoints = polylinePoints;
            this.instructionsInHtml = instructionsInHtml;
            this.maneuver = maneuver;
            this.travelMode = travelMode;
        }

        public int getDistanceInMeter() {
            return distanceInMeter;
        }

        public String getDistanceInText() {
            return distanceInText;
        }

        public int getDurationInMs() {
            return durationInMs;
        }

        public String getDurationInText() {
            return durationInText;
        }


        public double getStartLatitude() {
            return startLatitude;
        }

        public double getStartLongitude() {
            return startLongitude;
        }


        public double getEndLatitude() {
            return endLatitude;
        }

        public double getEndLongitude() {
            return endLongitude;
        }

        public String getInstructionsInHtml() {
            return instructionsInHtml;
        }

        public String getTravelMode() {
            return travelMode;
        }

        public String getPolylinePoints() {
            return polylinePoints;
        }

        public int getManeuver() {
            return maneuver;
        }

        @Override
        public String toString() {
            return "Step{" +
                    "distanceInMeter=" + distanceInMeter +
                    ", distanceInText='" + distanceInText + '\'' +
                    ", durationInMs=" + durationInMs +
                    ", durationInText='" + durationInText + '\'' +
                    ", startLatitude=" + startLatitude +
                    ", startLongitude=" + startLongitude +
                    ", endLatitude=" + endLatitude +
                    ", endLongitude=" + endLongitude +
                    ", polylinePoints='" + polylinePoints + '\'' +
                    ", instructionsInHtml='" + instructionsInHtml + '\'' +
                    ", maneuver=" + maneuver +
                    ", travelMode='" + travelMode + '\'' +
                    '}';
        }
    }


//    public static class UrlSigner {
//
//        // Note: Generally, you should store your private key someplace safe
//        // and read them into your code
//
//        private static String keyString = "YOUR_PRIVATE_KEY";
//
//        // The URL shown in these examples must be already
//        // URL-encoded. In practice, you will likely have code
//        // which assembles your URL from user or web service input
//        // and plugs those values into its parameters.
//        private static String urlString = "YOUR_URL_TO_SIGN";
//
//        // This variable stores the binary key, which is computed from the
//        // string
//        // (Base64) key
//        private static byte[] key;
//
//        public UrlSigner(String keyString) throws IOException {
//            // Convert the key from 'web safe' base 64 to binary
//            keyString = keyString.replace('-', '+');
//            keyString = keyString.replace('_', '/');
//            System.out.println("Key: " + keyString);
//            key = Base64.decode(keyString, Base64.DEFAULT);
//
//        }
//
//        public String signRequest(String path, String query) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException,
//                URISyntaxException {
//
//            // Retrieve the proper URL components to sign
//            String resource = path + '?' + query;
//
//            // Get an HMAC-SHA1 signing key from the raw key bytes
//            SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");
//
//            // Get an HMAC-SHA1 Mac instance and initialize it with the
//            // HMAC-SHA1
//            // key
//            Mac mac = Mac.getInstance("HmacSHA1");
//            mac.init(sha1Key);
//
//            // compute the binary signature for the request
//            byte[] sigBytes = mac.doFinal(resource.getBytes());
//
//            // base 64 encode the binary signature
//            String signature = Base64.encodeToString(sigBytes, Base64.DEFAULT);
//
//            // convert the signature to 'web safe' base 64
//            signature = signature.replace('+', '-');
//            signature = signature.replace('/', '_');
//
//            return resource + "&signature=" + signature;
//        }
//
//    }

    public final int DEFAULT_MAX_NO_OF_RETRIES = 3;

    public static final String CLIENT_ID_FOR_BUSINESS = "gme-easyvanhongkonglimited";
    public static final String CRYPTO_FOR_BUSINESS = "RglSWAR2KO9R2OghAMwyj4WqIXg=";
    public static final String API_DOMAIN = "https://maps.google.com";

    public static final String WORLD_BOUNDS = "-90,-180|90,180";

    private static DirectionsApiManager instance = null;

    private DirectionsApiManager() {

    }

    public static DirectionsApiManager getInstance() {
        if (instance != null)
            return instance;
        else
            return instance = new DirectionsApiManager();

    }

    private static String businessNize(String tmpString) {
        UrlSigner signer;
        try {
            URL url = new URL(tmpString);
            signer = new UrlSigner(CRYPTO_FOR_BUSINESS);
            String request = signer.signRequest(url.getPath(), url.getQuery());
            tmpString = API_DOMAIN + request;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return tmpString;
    }

    private String genRouteLink(double startLatitude, double startLongitude, double endLatitude, double endLongitude, final Locale locale) {

        String localeString = locale.getLanguage() + "-" + locale.getCountry();

        String result = businessNize(String.format("https://maps.googleapis.com/maps/api/directions/json?origin=%.6f,%.6f&destination=%.6f,%.6f&language=%s&client=%s", startLatitude, startLongitude, endLatitude, endLongitude,
                localeString, CLIENT_ID_FOR_BUSINESS));

        if (result.endsWith("\r\n"))
            result = result.substring(0, result.length() - 3);

        return result;

    }

    private void response2Route(JSONObject responseJObj, OnRouteListener listener) {

        if (listener == null)
            return;

        if (responseJObj == null) {
            listener.returnStepList(new ArrayList<Step>());
            return;
        }


        ArrayList<Step> stepList = new ArrayList<Step>();
        boolean hasException = false;
        try {

            String status = "";

            Step step;

            int distanceInMeter;
            String distanceInText = "";
            int durationInMs;
            String durationInText = "";
            double startLatitude = Double.NaN;
            double startLongitude = Double.NaN;
            double endLatitude = Double.NaN;
            double endLongitude = Double.NaN;
            String polylinePoints = "";
            String instructionsInHtml = "";
            int maneuver = Step.MANEUVER_NONE;
            String maneuverStr = "";
            String travelMode = "";

            JSONObject stepJObj;

            status = responseJObj.getString("status");

            if (!"OK".equals(status)) {
                listener.returnStepList(new ArrayList<Step>());
                return;
            }

            JSONArray routesJArray = responseJObj.getJSONArray("routes");
            JSONArray legsJArray = routesJArray.getJSONObject(0).getJSONArray("legs");
            JSONArray stepsJArray = legsJArray.getJSONObject(0).getJSONArray("steps");


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

                polylinePoints = stepJObj.getJSONObject("polyline").getString("points");
                instructionsInHtml = stepJObj.getString("html_instructions");

                maneuverStr = stepJObj.optString("maneuver", "");
                switch (maneuverStr){
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
                        polylinePoints, instructionsInHtml,
                        maneuver, travelMode);

                stepList.add(step);

            }

        } catch (Exception e) {

            e.printStackTrace();
            hasException = true;

        }

        listener.returnStepList(hasException ? new ArrayList<Step>() : stepList);

    }


    public void route(double startLatitude, double startLongitude, double endLatitude, double endLongitude, final Locale locale, final OnRouteListener listener) {

        if (listener == null)
            return;

        String link = genRouteLink(startLatitude, startLongitude, endLatitude, endLongitude, locale);

        httpGetJSON(link, DEFAULT_MAX_NO_OF_RETRIES, new OnHttpGetJSONListener() {

            @Override
            public void onComplete(JSONObject response) {

                response2Route(response, listener);

            }

        });


    }


}
