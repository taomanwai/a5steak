package com.tommytao.a5steak.util.google;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;

import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class GeocodeManager extends GFoundation {

    private static GeocodeManager instance;

    public static GeocodeManager getInstance() {
        if (instance == null)
            instance = new GeocodeManager();

        return instance;
    }

    private GeocodeManager() {

    }


    // --

    public static interface OnGetListener {

        public void returnGeocode(Geocode geocode);

    }

    public static interface OnSearchListener {

        public void returnPOIPoints(ArrayList<POIPoint> poiPoints, String keyword, String origJsonStr);

    }

    public class POIPoint {

        private int id;

        private String formattedAddress;

        private double latitude = Double.NaN;

        private double longitude = Double.NaN;

        public POIPoint(int id, String formattedAddress, double latitude, double longitude) {

            super();
            this.id = id;
            this.formattedAddress = formattedAddress;
            this.latitude = latitude;
            this.longitude = longitude;

        }

        public int getId() {
            return id;
        }

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public float distanceBetween(double otherLatitude, double otherLongitude) {

            float[] results = new float[3];

            Location.distanceBetween(latitude, longitude, otherLatitude, otherLongitude, results);

            return results[0];

        }

        @Override
        public String toString() {
            return "POIPoint [id=" + id + ", formattedAddress=" + formattedAddress + ", latitude=" + latitude + ", longitude=" + longitude + "]";
        }

    }

    public static class Geocode {

        private String streetNo = "";
        private String route = "";
        private String neighborhood = "";
        private String sublocality = "";
        private String locality = "";
        private String administrativeAreaLevel1 = "";
        private String country = "";
        private String postalCode = "";
        private String formattedAddress = "";

        private String addressInHKFormat = "";

        private Locale locale;

        private String failureReason = "";

        public Geocode(String streetNo, String route, String neighborhood, String sublocality, String locality, String administrativeAreaLevel1,
                       String country, String postalCode, String formattedAddress, Locale locale) {

            this.streetNo = streetNo;
            this.route = route;
            this.neighborhood = neighborhood;
            this.sublocality = sublocality;
            this.locality = locality;
            this.administrativeAreaLevel1 = administrativeAreaLevel1;
            this.country = country;
            this.postalCode = postalCode;
            this.formattedAddress = formattedAddress;
            this.locale = locale == null ? null : new Locale(locale.getLanguage(), locale.getCountry(), locale.getVariant());

        }

        public Geocode(String failureReason) {
            this.failureReason = failureReason;
        }

        public String getFailureReason() {
            return failureReason;
        }

        public String getStreetNo() {
            return streetNo;
        }

        public String getRoute() {
            return route;
        }

        public String getNeighborhood() {
            return neighborhood;
        }

        public String getSublocality() {
            return sublocality;
        }

        public String getLocality() {
            return locality;
        }

        public String getAdministrativeAreaLevel1() {
            return administrativeAreaLevel1;
        }

        public String getCountry() {
            return country;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public String getAddressInHKFormat() {

            if (addressInHKFormat.isEmpty()) {

                if (locale.getLanguage().equals("zh"))
                    addressInHKFormat = country + " " + administrativeAreaLevel1 + " " + " " + route + " " + streetNo;
                else
                    addressInHKFormat = streetNo + " " + route + ", " + administrativeAreaLevel1 + ", " + country;

            }

            return addressInHKFormat;
        }

        @Override
        public String toString() {
            return "Geocode [streetNo=" + streetNo + ", route=" + route + ", neighborhood=" + neighborhood + ", sublocality=" + sublocality + ", locality="
                    + locality + ", administrativeAreaLevel1=" + administrativeAreaLevel1 + ", country=" + country + ", postalCode=" + postalCode
                    + ", formattedAddress=" + formattedAddress + ", addressInHKFormat=" + addressInHKFormat + ", locale=" + locale + ", failureReason="
                    + failureReason + "]";
        }
    }

    public static final String WORLD_BOUNDS = "-90,-180|90,180";
    public static final String HK_BOUNDS = "22.1533884,113.835078|22.561968,114.4069561";

    public static final int DEFAULT_MAX_NUM_OF_RETRIES = 3;

    @Deprecated
    public boolean init(Context context) {
        return super.init(context);
    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }

    public boolean init(Context appContext, RequestQueue requestQueue, String clientIdForWork, String cryptoForWork) {

        if (!super.init(appContext, requestQueue)) {
            return false;
        }

        this.clientIdForWork = clientIdForWork;
        this.cryptoForWork = cryptoForWork;

        return true;

    }


    private String genGetLink(double latitude, double longitude, Locale locale) {

        String localeStr = localeToLocaleStr(locale);

        String result = String.format("http://maps.google.com/maps/api/geocode/json?latlng=%.6f,%.6f&language=%s", latitude, longitude,
                localeStr);

        result = isForWork() ? signToForWork(result, API_DOMAIN_FOR_WORK, clientIdForWork, cryptoForWork) : result;

        if (result.endsWith("\r\n"))
            result = result.substring(0, result.length() - 3);

        return result;

    }

    private boolean isForWork() {
        return !TextUtils.isEmpty(clientIdForWork) && !TextUtils.isEmpty(cryptoForWork);
    }

    private String localeToLocaleStr(Locale locale) {
        if (locale == null)
            locale = Locale.US;

        return locale.getLanguage() + "-" + locale.getCountry();
    }

    private String genSearchByBoundsLink(String address, String bounds, Locale locale) {
        String encodedAddress = address;
        try {
            encodedAddress = URLEncoder.encode(address, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String localeStr = localeToLocaleStr(locale);

        String result = String.format("http://maps.google.com/maps/api/geocode/json?address=%s&bounds=%s&language=%s", encodedAddress, bounds,
                localeStr);

        return isForWork() ? signToForWork(result, API_DOMAIN_FOR_WORK, clientIdForWork, cryptoForWork) : result;
    }

    private String genSearchByCountryLink(String address, String country, Locale locale) {
        String encodedAddress = address;
        try {
            encodedAddress = URLEncoder.encode(address, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String localeStr = localeToLocaleStr(locale);

        String result = String.format("http://maps.google.com/maps/api/geocode/json?address=%s&components=country:%s&language=%s",
                encodedAddress, country, localeStr);

        return isForWork() ? signToForWork(result, API_DOMAIN_FOR_WORK, clientIdForWork, cryptoForWork) : result;
    }

    private void response2Get(JSONObject responseJObj, Locale locale, OnGetListener listener) {

        if (listener == null)
            return;

        if (responseJObj == null)
            listener.returnGeocode(new Geocode("No response, maybe network error"));

        String streetNo = "";
        String route = "";
        String neighborhood = "";
        String sublocality = "";
        String locality = "";
        String administrativeAreaLevel1 = "";
        String country = "";
        String postalCode = "";
        String formattedAddress = "";

        String status = "";

        boolean hasException = false;
        try {

            status = responseJObj.getString("status");
            JSONArray resultsJArray = responseJObj.getJSONArray("results");
            formattedAddress = resultsJArray.getJSONObject(0).getString("formatted_address");
            JSONArray addressComponentsJArray = resultsJArray.getJSONObject(0).getJSONArray("address_components");
            JSONArray typesJArray = null;
            String type = "";
            for (int i = 0; i < addressComponentsJArray.length(); i++) {
                typesJArray = addressComponentsJArray.getJSONObject(i).getJSONArray("types");
                for (int j = 0; j < typesJArray.length(); j++) {
                    type = typesJArray.getString(j);

                    if (type.equals("street_number")) {
                        try {
                            streetNo = addressComponentsJArray.getJSONObject(i).getString("long_name");
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                        continue;
                    }

                    if (type.equals("route")) {
                        try {
                            route = addressComponentsJArray.getJSONObject(i).getString("long_name");
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                        continue;
                    }

                    if (type.equals("neighborhood")) {
                        try {
                            neighborhood = addressComponentsJArray.getJSONObject(i).getString("long_name");
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                        continue;
                    }

                    if (type.equals("administrative_area_level_1")) {
                        try {
                            administrativeAreaLevel1 = addressComponentsJArray.getJSONObject(i).getString("long_name");
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                        continue;
                    }

                    if (type.equals("country")) {
                        try {
                            country = addressComponentsJArray.getJSONObject(i).getString("long_name");
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                        continue;
                    }

                }
            }
        } catch (Exception e) {

            e.printStackTrace();

            hasException = true;

        }

        listener.returnGeocode(hasException ? new Geocode(status) : new Geocode(streetNo, route, neighborhood, sublocality, locality, administrativeAreaLevel1,
                country, postalCode, formattedAddress, locale));

    }

    public void get(double latitude, double longitude, final Locale locale, final OnGetListener listener) {

        if (listener == null)
            return;

        String link = genGetLink(latitude, longitude, locale);

        // Can use handler post runnable coz GeocodeGMapManager is Singleton
        // while
        // listener should have weak ref to its parent (e.g. activity)
        if (!isLatLngValid(latitude, longitude) || link.isEmpty()) {

            handler.post(new Runnable() {

                @Override
                public void run() {
                    listener.returnGeocode(null);
                }

            });

            return;

        }

        httpGetJSON(link, DEFAULT_MAX_NUM_OF_RETRIES, new OnHttpGetJSONListener() {

            @Override
            public void onComplete(JSONObject response) {

                response2Get(response, locale, listener);

            }

        });

    }

    /**
     * Search nearby location based on Google Geocode protocol
     *
     * @param keyword  Search query
     * @param leftTopLat Latitude of left top of bounds
     * @param leftTopLng Longitude of left top of bounds
     * @param rightBottomLat Latitude of right bottom of bounds
     * @param rightBottomLng Longitude of right bottom of bounds
     * @param locale   Locale of search query
     * @param listener Listener which will be triggered when search completed,
     *                 listener will also return search result
     */
    public void searchByBounds(final String keyword,
                               final double leftTopLat, final double leftTopLng,
                               final double rightBottomLat, final double rightBottomLng,
                               final Locale locale, final OnSearchListener listener) {

        if (listener == null)
            return;

        if (!isLatLngValid(leftTopLat, leftTopLng) || !isLatLngValid(rightBottomLat, rightBottomLng)){

            handler.post(new Runnable() {

                @Override
                public void run() {
                    listener.returnPOIPoints(new ArrayList<POIPoint>(), keyword, "");
                }

            });

            return;
        }


        final String bounds = leftTopLat + "," + leftTopLng + "|" + rightBottomLat + "," + rightBottomLng; // 22.1533884,113.835078|22.561968,114.4069561

        String link = genSearchByBoundsLink(keyword, bounds, locale);

        httpGetJSON(link, DEFAULT_MAX_NUM_OF_RETRIES, new OnHttpGetJSONListener() {

            @Override
            public void onComplete(JSONObject response) {
                response2Search(response, keyword, bounds, locale, listener);

            }

        });

    }

    /**
     *
     * Search nearby location based on Google Geocode protocol
     *
     * @param keyword Search query
     * @param country Country code (2 letters) follows ISO 3166-1 standard Ref: https://en.wikipedia.org/wiki/ISO_3166-1
     * @param locale Locale of search query
     * @param listener Listener which will be triggered when search completed,
     *                 listener will also return search result
     */
    public void searchByCountry(final String keyword, final String country, final Locale locale, final OnSearchListener listener) {

        if (listener == null)
            return;

        String link = genSearchByCountryLink(keyword, country, locale);

        httpGetJSON(link, DEFAULT_MAX_NUM_OF_RETRIES, new OnHttpGetJSONListener() {

            @Override
            public void onComplete(JSONObject response) {
                response2Search(response, keyword, WORLD_BOUNDS, locale, listener);

            }

        });

    }

    private double[] boundsStr2DoubleArray(String bounds) {

        bounds = bounds.replace("|", ",");

        String[] resultStrings = bounds.split(",");

        if (resultStrings.length != 4)
            return new double[4];

        double[] result = new double[4];
        boolean isSucceed = true;
        int i = 0;
        for (String resultString : resultStrings) {
            try {
                result[i] = Double.valueOf(resultString);
            } catch (Exception e) {
                isSucceed = false;
                break;
            }
            i++;
        }

        if (!isSucceed)
            return new double[4];

        return result;

    }

    private boolean isInsideBounds(double latitude, double longitude, String bounds) {

        double[] boundsInDoubleArray = boundsStr2DoubleArray(bounds);

        return (boundsInDoubleArray[0] <= latitude) && (latitude <= boundsInDoubleArray[2]) && (boundsInDoubleArray[1] <= longitude)
                && (longitude <= boundsInDoubleArray[3]);

    }

    private void response2Search(JSONObject responseJObj, String keyword, String bounds, Locale locale, final OnSearchListener listener) {

        if (listener == null)
            return;

        ArrayList<POIPoint> poiPoints = new ArrayList<>();

        String origJsonStr = "" + responseJObj;

        try {
            JSONArray resultsJArray = responseJObj.getJSONArray("results");

            for (int i = 0; i < resultsJArray.length(); i++) {
                String formattedAddress = resultsJArray.getJSONObject(i).getString("formatted_address");
                JSONObject geometryJObj = resultsJArray.getJSONObject(i).getJSONObject("geometry");
                JSONObject locationJObj = geometryJObj.getJSONObject("location");
                double lat = locationJObj.getDouble("lat");
                double lng = locationJObj.getDouble("lng");

                if (!isInsideBounds(lat, lng, bounds))
                    continue;

                poiPoints.add(new POIPoint(i, formattedAddress, lat, lng));

            }

            listener.returnPOIPoints(poiPoints, keyword, origJsonStr);

        } catch (Exception e) {
            e.printStackTrace();

            listener.returnPOIPoints(new ArrayList<POIPoint>(), keyword, origJsonStr);
        }

    }

}
