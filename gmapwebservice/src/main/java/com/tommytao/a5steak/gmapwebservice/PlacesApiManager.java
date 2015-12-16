package com.tommytao.a5steak.gmapwebservice;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tommytao.a5steak.common.Foundation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Responsible for get or search location info through Google Places API
 * <p/>
 * Ref: <a href="https://developers.google.com/places/webservice/autocomplete#location_biasing">here</a>
 */
public class PlacesApiManager extends Foundation {

    private static PlacesApiManager instance;

    public static PlacesApiManager getInstance() {

        if (instance == null)
            instance = new PlacesApiManager();

        return instance;
    }

    private PlacesApiManager() {

    }

    // --

    public static class Place {

        private String placeId = "";
        private String name = "";
        private String formattedAddress = "";

        private double latitude = Double.NaN;
        private double longitude = Double.NaN;


        public String getName() {
            return name;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public Place(String placeId, String name, String formattedAddress, double latitude, double longitude) {
            this.placeId = placeId;
            this.name = name;
            this.formattedAddress = formattedAddress;

            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getPlaceId() {
            return placeId;
        }

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public float distanceBetween(double anotherLatitude, double anotherLongitude) {

            float[] results = new float[3];

            Location.distanceBetween(latitude, longitude, anotherLatitude, anotherLongitude, results);

            return results[0];

        }


    }

    public static class AutoComplete {


        private String placeId = "";
        private String description = "";
        private int offset = -1;
        private int length = -1;
        private ArrayList<String> terms = new ArrayList<String>();

        public AutoComplete(String placeId, String description, int offset, int length, ArrayList<String> terms) {
            this.placeId = placeId;
            this.description = description;
            this.offset = offset;
            this.length = length;
            this.terms = new ArrayList<>(terms);
        }

        public String getPlaceId() {
            return placeId;
        }

        public String getDescription() {
            return description;
        }

        public int getOffset() {
            return offset;
        }

        public int getLength() {
            return length;
        }

        public ArrayList<String> getTerms() {
            return terms;
        }
    }

    private class ResponseAutoComplete {

        public class MatchedSubstring {

            @SerializedName("length")
            @Expose
            private Integer length;
            @SerializedName("offset")
            @Expose
            private Integer offset;

            public Integer getLength() {

                if (length == null)
                    return -1;

                return length;
            }

            public void setLength(Integer length) {
                this.length = length;
            }

            public Integer getOffset() {

                if (offset == null)
                    return -1;
                return offset;
            }

            public void setOffset(Integer offset) {
                this.offset = offset;
            }

        }

        public class Prediction {

            @SerializedName("description")
            @Expose
            private String description;
            @SerializedName("matched_substrings")
            @Expose
            private List<MatchedSubstring> matchedSubstrings = new ArrayList<MatchedSubstring>();
            @SerializedName("place_id")
            @Expose
            private String placeId;
            @SerializedName("terms")
            @Expose
            private List<Term> terms = new ArrayList<Term>();

            public String getDescription() {

                if (description == null)
                    return "";
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public List<MatchedSubstring> getMatchedSubstrings() {

                if (matchedSubstrings == null)
                    return new ArrayList<>();

                return matchedSubstrings;
            }


            public void setMatchedSubstrings(List<MatchedSubstring> matchedSubstrings) {
                this.matchedSubstrings = matchedSubstrings;
            }


            public String getPlaceId() {

                if (placeId == null)
                    return "";
                return placeId;
            }

            public void setPlaceId(String placeId) {
                this.placeId = placeId;
            }


            public List<Term> getTerms() {

                if (terms == null)
                    return new ArrayList<>();

                return terms;
            }


            public void setTerms(List<Term> terms) {
                this.terms = terms;
            }

            public ArrayList<String> getTermsInStringArrayList() {

                ArrayList<String> results = new ArrayList<>();

                for (Term term : getTerms()) {

                    results.add(term.getValue());

                }


                return results;


            }

        }

        public class Term {

            @SerializedName("offset")
            @Expose
            private Integer offset;
            @SerializedName("value")
            @Expose
            private String value;


            public Integer getOffset() {

                if (offset == null)
                    return -1;
                return offset;
            }


            public void setOffset(Integer offset) {
                this.offset = offset;
            }


            public String getValue() {
                if (value == null)
                    return "";
                return value;
            }


            public void setValue(String value) {
                this.value = value;
            }

        }

        @SerializedName("predictions")
        @Expose
        private List<Prediction> predictions = new ArrayList<>();
        @SerializedName("status")
        @Expose
        private String status;

        public List<Prediction> getPredictions() {

            if (predictions == null)
                return new ArrayList<>();

            return predictions;
        }

        public void setPredictions(List<Prediction> predictions) {
            this.predictions = predictions;
        }

        public String getStatus() {

            if (status == null)
                return "";

            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }

    private class ResponseGetPlaceFromPlaceId {

        public class Geometry {

            @SerializedName("location")
            @Expose
            private Location location;


            public Location getLocation() {
                return location;
            }


            public void setLocation(Location location) {
                this.location = location;
            }

        }

        public class Location {

            @SerializedName("lat")
            @Expose
            private Double lat;
            @SerializedName("lng")
            @Expose
            private Double lng;


            public Double getLat() {

                if (lat == null)
                    return Double.NaN;
                return lat;
            }


            public void setLat(Double lat) {
                this.lat = lat;
            }


            public Double getLng() {

                if (lng == null)
                    return Double.NaN;
                return lng;
            }


            public void setLng(Double lng) {
                this.lng = lng;
            }

        }

        public class Result {

            @SerializedName("adr_address")
            @Expose
            private String adrAddress;
            @SerializedName("formatted_address")
            @Expose
            private String formattedAddress;
            @SerializedName("geometry")
            @Expose
            private Geometry geometry;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("place_id")
            @Expose
            private String placeId;
            @SerializedName("vicinity")
            @Expose
            private String vicinity;


            public String getAdrAddress() {
                if (adrAddress == null)
                    return "";
                return adrAddress;
            }


            public void setAdrAddress(String adrAddress) {
                this.adrAddress = adrAddress;
            }


            public String getFormattedAddress() {
                if (formattedAddress == null)
                    return "";
                return formattedAddress;
            }


            public void setFormattedAddress(String formattedAddress) {
                this.formattedAddress = formattedAddress;
            }


            public Geometry getGeometry() {
                return geometry;
            }

            public void setGeometry(Geometry geometry) {
                this.geometry = geometry;
            }


            public String getName() {
                if (name == null)
                    return "";
                return name;
            }


            public void setName(String name) {
                this.name = name;
            }


            public String getPlaceId() {
                if (placeId == null)
                    return "";
                return placeId;
            }


            public void setPlaceId(String placeId) {
                this.placeId = placeId;
            }


            public String getVicinity() {
                if (vicinity == null)
                    return "";
                return vicinity;
            }


            public void setVicinity(String vicinity) {
                this.vicinity = vicinity;
            }

        }


        @SerializedName("result")
        @Expose
        private Result result;
        @SerializedName("status")
        @Expose
        private String status;

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }

        public String getStatus() {

            if (status == null)
                return "";

            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }


    private class ResponseGetPlaceFromLatLng {

        public class Geometry {

            @SerializedName("location")
            @Expose
            private Location location;


            public Location getLocation() {
                return location;
            }


            public void setLocation(Location location) {
                this.location = location;
            }

        }

        public class Location {

            @SerializedName("lat")
            @Expose
            private Double lat;
            @SerializedName("lng")
            @Expose
            private Double lng;


            public Double getLat() {

                if (lat == null)
                    return Double.NaN;

                return lat;
            }

            public void setLat(Double lat) {
                this.lat = lat;
            }


            public Double getLng() {
                if (lng == null)
                    return Double.NaN;

                return lng;
            }


            public void setLng(Double lng) {
                this.lng = lng;
            }

        }

        public class Result {

            @SerializedName("geometry")
            @Expose
            private Geometry geometry;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("place_id")
            @Expose
            private String placeId;
            @SerializedName("vicinity")
            @Expose
            private String vicinity;

            public Geometry getGeometry() {
                return geometry;
            }


            public void setGeometry(Geometry geometry) {
                this.geometry = geometry;
            }


            public String getName() {
                if (name == null)
                    return "";
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }


            public String getPlaceId() {
                if (placeId == null)
                    return "";
                return placeId;
            }


            public void setPlaceId(String placeId) {
                this.placeId = placeId;
            }

            public String getVicinity() {
                if (vicinity == null)
                    return "";
                return vicinity;
            }


            public void setVicinity(String vicinity) {
                this.vicinity = vicinity;
            }

        }

        @SerializedName("results")
        @Expose
        private List<Result> results = new ArrayList<Result>();
        @SerializedName("status")
        @Expose
        private String status;

        public List<Result> getResults() {
            if (results == null)
                return new ArrayList<>();
            return results;
        }


        public void setResults(List<Result> results) {
            this.results = results;
        }


        public String getStatus() {
            if (status == null)
                return "";
            return status;
        }


        public void setStatus(String status) {
            this.status = status;
        }

    }


    private class ResponseSearch {

        public class Location {

            @SerializedName("lat")
            @Expose
            private Double lat;
            @SerializedName("lng")
            @Expose
            private Double lng;

            public Double getLat() {

                if (lat == null)
                    return Double.NaN;

                return lat;
            }

            public void setLat(Double lat) {
                this.lat = lat;
            }

            public Double getLng() {


                if (lng == null)
                    return Double.NaN;


                return lng;
            }

            public void setLng(Double lng) {
                this.lng = lng;
            }

        }

        public class Geometry {

            @SerializedName("location")
            @Expose
            private Location location;

            public Location getLocation() {
                return location;
            }

            public void setLocation(Location location) {
                this.location = location;
            }

        }

        public class Result {

            @SerializedName("geometry")
            @Expose
            private Geometry geometry;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("place_id")
            @Expose
            private String placeId;
            @SerializedName("vicinity")
            @Expose
            private String vicinity;

            public Geometry getGeometry() {
                return geometry;
            }

            public void setGeometry(Geometry geometry) {
                this.geometry = geometry;
            }

            public String getName() {

                if (name == null)
                    return "";

                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPlaceId() {


                if (placeId == null)
                    return "";

                return placeId;
            }

            public void setPlaceId(String placeId) {
                this.placeId = placeId;
            }

            public String getVicinity() {


                if (vicinity == null)
                    return "";
                return vicinity;
            }

            public void setVicinity(String vicinity) {
                this.vicinity = vicinity;
            }

        }


        @SerializedName("results")
        @Expose
        private List<Result> results = new ArrayList<Result>();

        @SerializedName("status")
        @Expose
        private String status;

        public List<Result> getResults() {

            if (results == null)
                return new ArrayList<>();

            return results;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }

        public String getStatus() {

            if (status == null)
                return "";

            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static interface OnGetPlaceListener {

        public void returnPlace(Place place);

    }

    public static interface OnSearchPlacesListener {

        public void returnPlaces(ArrayList<Place> places, String keyword, String status);

    }

    public static interface OnAutoCompleteListener {

        public void returnAutoCompletes(ArrayList<AutoComplete> autoCompletes, String input, JSONObject response);

    }

    public static final int DEFAULT_MAX_NUM_OF_RETRIES = 3;

    public static final int GET_PLACE_FROM_LAT_LNG_IN_METER = 5;

    private String key = "";

    public String getKey() {
        return key;
    }

    @Deprecated
    public boolean init(Context context) {
        return super.init(context);
    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }


    /**
     * @param context Application context
     * @param key        Places API key, to know how to get it, please browse <a href="http://stackoverflow.com/questions/24302920/how-do-i-get-a-google-places-api-key-for-my-android-app">here</a>
     * @return TRUE when init succeed
     */
    public boolean init(Context context, RequestQueue requestQueue, String key) {

        if (!super.init(context)) {
            return false;
        }

        this.requestQueue = requestQueue;

        this.key = key;

        return true;

    }


    private Location obtainLocationFromGeometryJObj(JSONObject geometryJObj) {

        double lat = 0;
        double lng = 0;

        boolean succeed = false;

        try {
            JSONObject locationJObj = geometryJObj.getJSONObject("location");
            lat = locationJObj.getDouble("lat");
            lng = locationJObj.getDouble("lng");
            succeed = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!succeed)
            return null;

        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lng);
        return location;

    }

    // TODO change match to matches (ArrayList<String>)
    private boolean isResultType(JSONObject result, String match) {

        if (result == null)
            return false;

        JSONArray types = null;
        try {
            types = result.getJSONArray("types");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (types == null)
            return false;

        for (int i = 0; i < types.length(); i++) {
            try {
                if (match.equals(types.getString(i)))
                    return true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;

    }

    private String localeToLocaleStr(Locale locale) {
        if (locale == null)
            locale = Locale.US;

        return locale.getLanguage() + "-" + locale.getCountry();
    }

    private String genPlaceIdLink(String placeId, Locale locale) {

        // https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4&key=AddYourOwnKeyHere
        // Ref:
        // https://developers.google.com/places/documentation/details#PlaceDetailsRequests

        String localeStr = localeToLocaleStr(locale);

        String result = String.format("https://maps.googleapis.com/maps/api/place/details/json?placeid=%s&language=%s&key=%s", placeId, localeStr, getKey());

        return result;

    }

    private String genQueryLink(double latitude, double longitude, int radiusInMeter, String keyword, Locale locale, boolean isRankByDistance) {

        String localeStr = localeToLocaleStr(locale);

        String keywordURLEncoded = "";
        try {
            keywordURLEncoded = URLEncoder.encode(keyword, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO testing purpose, del later
        // radiusInMeter = 50000;
        // localeStr = "th";
        // ===========================

        String result = String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%.6f,%.6f&keyword=%s&language=%s&key=%s",
                latitude, longitude, keywordURLEncoded, localeStr, getKey());

        result += !isRankByDistance ? ("&radius=" + radiusInMeter) : "&rankby=distance";

        return result;

    }

    private String genAutoCompleteLink(String input, Locale locale) {

        String localeStr = localeToLocaleStr(locale);

        String inputURLEncoded = "";
        try {
            inputURLEncoded = URLEncoder.encode(input, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String result = String.format("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=%s&language=%s&key=%s",
                inputURLEncoded, localeStr, getKey());

        result += "&components=country:" + locale.getCountry().toString().toLowerCase();

        return result;

    }


    public void getPlaceFromLatLng(double latitude, double longitude, final Locale locale, final OnGetPlaceListener listener) {

        if (listener == null)
            return;

        String link = genQueryLink(latitude, longitude, GET_PLACE_FROM_LAT_LNG_IN_METER, "", locale, false);

        if (!isLatLngValid(latitude, longitude) || link.isEmpty()) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    listener.returnPlace(null);
                }

            });
            return;
        }


        httpGetJSON(link, DEFAULT_MAX_NUM_OF_RETRIES, new OnHttpGetJSONListener() {

            @Override
            public void onComplete(JSONObject response) {

                response2GetPlaceFromLatLng(response, locale, listener);

            }

        });

    }

    public void getPlaceFromPlaceId(String placeId, final Locale locale, final OnGetPlaceListener listener) {

        if (listener == null)
            return;

        String link = genPlaceIdLink(placeId, locale);

        if (link.isEmpty()) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    listener.returnPlace(null);
                }

            });
            return;
        }

        httpGetJSON(link, DEFAULT_MAX_NUM_OF_RETRIES, new OnHttpGetJSONListener() {

            @Override
            public void onComplete(JSONObject response) {

                response2GetPlaceFromPlaceId(response, locale, listener);

            }

        });
    }

//	public static String returnPlacesStr(ArrayList<Place> places) {
//
//		StringBuffer sb = new StringBuffer();
//		for (Place place : places)
//			sb.append(place.getName() + " , ");
//
//		return sb.toString();
//	}

    /**
     * Search nearby places by Google Places API
     *
     * @param keyword        Search keyword
     * @param latitude       Latitude of location
     * @param longitude      Longitude of location
     * @param radiusInMeter  Radius in meter. It should be at least 1. Note: if
     *                       rankByDistance is true, original value will be ignored and
     *                       auto-set to be 50km
     * @param locale         Expected locale of result
     * @param rankByDistance Whether result is sorted based on distance between latLng and
     *                       searched result. Nearest=first. Note: if rankByDistance is
     *                       true, radiusInMeter will be ignored and auto-set to be 50km
     * @param listener       Listener which will be triggered when search results are
     *                       returned
     */
    public void searchPlaces(final String keyword, double latitude, double longitude, int radiusInMeter, final Locale locale,
                             boolean rankByDistance, final OnSearchPlacesListener listener) {

        if (listener == null)
            return;

        String link = genQueryLink(latitude, longitude, radiusInMeter, keyword, locale, rankByDistance);

        if (!isLatLngValid(latitude, longitude) || TextUtils.isEmpty(keyword) || link.isEmpty()) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    listener.returnPlaces(new ArrayList<Place>(), keyword, "");
                }

            });
            return;
        }

        httpGetJSON(link, DEFAULT_MAX_NUM_OF_RETRIES, new OnHttpGetJSONListener() {

            @Override
            public void onComplete(JSONObject response) {

                response2SearchPlaces(response, keyword, locale, listener);

            }

        });

    }

    public void autoComplete(final String input, final Locale locale, final OnAutoCompleteListener listener) {

        if (listener == null)
            return;

        if (input.isEmpty()) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    listener.returnAutoCompletes(new ArrayList<AutoComplete>(), input, null);

                }

            });

            return;
        }

        String link = genAutoCompleteLink(input, locale);

        if (link.isEmpty()) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    listener.returnAutoCompletes(new ArrayList<AutoComplete>(), input, null);

                }

            });

            return;
        }

        httpGetJSON(link, DEFAULT_MAX_NUM_OF_RETRIES, new OnHttpGetJSONListener() {

            @Override
            public void onComplete(JSONObject response) {

                response2AutoComplete(response, input, locale, listener);

            }

        });

    }

    private void response2SearchPlaces(JSONObject responseJObj, String keyword, Locale locale, OnSearchPlacesListener listener) {

        if (listener == null)
            return;

        ArrayList<Place> results = new ArrayList<>();
        String status = "";

        if (responseJObj == null)
            listener.returnPlaces(results, keyword, "");


        try {

            ResponseSearch responseSearch = getGson().fromJson("" + responseJObj, ResponseSearch.class);

            status = responseSearch.getStatus();

            if (!"OK".equals(responseSearch.getStatus())) {
                listener.returnPlaces(results, keyword, status);

                return;
            }

             /*
             * OK indicates that no errors occurred; the place was successfully
			 * detected and at least one result was returned. ZERO_RESULTS
			 * indicates that the search was successful but returned no results.
			 * This may occur if the search was passed a latlng in a remote
			 * location. OVER_QUERY_LIMIT indicates that you are over your
			 * quota. REQUEST_DENIED indicates that your request was denied,
			 * generally because of lack of an invalid key parameter.
			 * INVALID_REQUEST generally indicates that a required query
			 * parameter (location or radius) is missing.
			 */

            String placeId = "";
            String name = "";
            String formattedAddress = "";
            double lat = Double.NaN;
            double lng = Double.NaN;

            for (int i = 0; i < responseSearch.getResults().size(); i++) {

                try {
                    placeId = responseSearch.getResults().get(i).getPlaceId();
                    name = responseSearch.getResults().get(i).getName();
                    formattedAddress = responseSearch.getResults().get(i).getVicinity();
                    lat = responseSearch.getResults().get(i).getGeometry().getLocation().getLat();
                    lng = responseSearch.getResults().get(i).getGeometry().getLocation().getLng();

                    results.add(new Place(placeId, name, formattedAddress, lat, lng));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        } catch (Exception e) {
            e.printStackTrace();

            results = new ArrayList<>();
        }


        listener.returnPlaces(results, keyword, status);

    }

    private void response2GetPlaceFromLatLng(JSONObject responseJObj, Locale locale, OnGetPlaceListener listener) {

        if (listener == null)
            return;

        Place place = null;

        if (responseJObj == null)
            listener.returnPlace(place);

        try {

            ResponseGetPlaceFromLatLng responseGetPlaceFromLatLng = getGson().fromJson("" + responseJObj, ResponseGetPlaceFromLatLng.class);

            if (!"OK".equals(responseGetPlaceFromLatLng.getStatus())) {
                listener.returnPlace(place);
            }


            String placeId = "";
            String name = "";
            String formattedAddress = "";
            double lat = Double.NaN;
            double lng = Double.NaN;
            for (int i = 0; i < responseGetPlaceFromLatLng.getResults().size(); i++) {

                try {
                    placeId = responseGetPlaceFromLatLng.getResults().get(i).getPlaceId();
                    name = responseGetPlaceFromLatLng.getResults().get(i).getName();
                    formattedAddress = responseGetPlaceFromLatLng.getResults().get(i).getVicinity();

                    if (responseGetPlaceFromLatLng.getResults().get(i).getGeometry() != null) {
                        lat = responseGetPlaceFromLatLng.getResults().get(i).getGeometry().getLocation().getLat();
                        lng = responseGetPlaceFromLatLng.getResults().get(i).getGeometry().getLocation().getLng();
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            place = new Place(placeId, name, formattedAddress, lat, lng);


        } catch (Exception e) {
            e.printStackTrace();
        }

        listener.returnPlace(place);

//        // ===
//
//
//        String status = "";
//        JSONArray resultsJArray = null;
//        JSONObject resultJObj = null;
//
//        String placeId = "";
//        String name = "";
//        Location location = null;
//        String formattedAddress = "";
//
//        boolean hasException = false;
//
//        try {
//
//            status = responseJObj.getString("status");
//            if (!"OK".equals(status)) {
//                listener.returnPlace(null);
//                return;
//            }
//
//            resultsJArray = responseJObj.getJSONArray("results");
//
//
//            for (int i = 0; i < resultsJArray.length(); i++) {
//                resultJObj = resultsJArray.getJSONObject(i);
//
//                placeId = resultJObj.optString("place_id", "");
//                name = resultJObj.optString("name", "");
//                formattedAddress = resultJObj.optString("vicinity", "");
//                location = obtainLocationFromGeometryJObj(resultJObj.optJSONObject("geometry"));
//
//
//                if (location != null)
//                    break;
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            hasException = true;
//        }
//
//        boolean isResultValid = !placeId.isEmpty() && !name.isEmpty() && location != null;
//
//        listener.returnPlace((hasException || !isResultValid) ? null : new Place(placeId, name, formattedAddress, location.getLatitude(), location.getLongitude()));

    }

    private void response2GetPlaceFromPlaceId(JSONObject responseJObj, Locale locale, OnGetPlaceListener listener) {

        if (listener == null)
            return;

        Place place = null;

        if (responseJObj == null)
            listener.returnPlace(place);

        try {

            ResponseGetPlaceFromPlaceId responseGetPlaceFromPlaceId = getGson().fromJson("" + responseJObj, ResponseGetPlaceFromPlaceId.class);

            if (!"OK".equals(responseGetPlaceFromPlaceId.getStatus())) {
                listener.returnPlace(place);
                return;
            }

            String placeId = "";
            String name = "";
            String formattedAddress = "";
            double lat = Double.NaN;
            double lng = Double.NaN;

            placeId = responseGetPlaceFromPlaceId.getResult().getPlaceId();
            name = responseGetPlaceFromPlaceId.getResult().getName();
            formattedAddress = responseGetPlaceFromPlaceId.getResult().getFormattedAddress();
            lat = responseGetPlaceFromPlaceId.getResult().getGeometry().getLocation().getLat();
            lng = responseGetPlaceFromPlaceId.getResult().getGeometry().getLocation().getLng();

            if (!TextUtils.isEmpty(placeId) && !TextUtils.isEmpty(name))
                place = new Place(placeId, name, formattedAddress, lat, lng);


        } catch (Exception e) {
            e.printStackTrace();
        }

        listener.returnPlace(place);


    }

    private void response2AutoComplete(JSONObject response, String input, Locale locale, OnAutoCompleteListener listener) {

        if (listener == null)
            return;

        ArrayList<AutoComplete> results = new ArrayList<>();

        if (response == null)
            listener.returnAutoCompletes(results, input, response);

        try {
            ResponseAutoComplete responseAutoComplete = getGson().fromJson("" + response, ResponseAutoComplete.class);

            String placeId = "";
            String description = "";
            int offset = -1;
            int length = -1;
            ArrayList<String> terms = new ArrayList<>();

            for (int i = 0; i < responseAutoComplete.getPredictions().size(); i++) {

                try {

                    placeId = responseAutoComplete.getPredictions().get(i).getPlaceId();
                    description = responseAutoComplete.getPredictions().get(i).getDescription();
                    offset = responseAutoComplete.getPredictions().get(i).getMatchedSubstrings().get(0).getOffset();
                    length = responseAutoComplete.getPredictions().get(i).getMatchedSubstrings().get(0).getLength();
                    terms = responseAutoComplete.getPredictions().get(i).getTermsInStringArrayList();

                    results.add(new AutoComplete(placeId, description, offset, length, terms));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();

            results = new ArrayList<>();

        }

        listener.returnAutoCompletes(results, input, response);

    }

}
