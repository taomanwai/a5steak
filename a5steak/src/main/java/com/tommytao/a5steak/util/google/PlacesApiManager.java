package com.tommytao.a5steak.util.google;

import android.content.Context;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

/**
 *
 * Responsible for get or search location info through Google Places API
 *
 * Ref: <a href="https://developers.google.com/places/webservice/autocomplete#location_biasing">here</a>
 *
 */
public class PlacesApiManager extends GFoundation {

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

	public static interface OnGetPlaceListener {

		public void returnPlace(Place place);

	}

	public static interface OnSearchPlacesListener {

		public void returnPlaces(ArrayList<Place> places, String keyword, String status);

	}

    public static interface OnAutoCompleteListener {

        public void returnAutoCompletes(ArrayList<AutoComplete> autoCompletes, String input, JSONObject response );

    }

    public final int DEFAULT_MAX_NUM_OF_RETRIES = 3;

    public static final int GET_PLACE_FROM_LAT_LNG_IN_METER = 5;

    private String key = "";

    public String getKey() {
        return key;
    }

    @Deprecated
    public boolean init(Context context) {
        return super.init(context);
    }

	/**
	 *
	 * @param appContext Application context
	 * @param key Places API key, to know how to get it, please browse <a href="http://stackoverflow.com/questions/24302920/how-do-i-get-a-google-places-api-key-for-my-android-app">here</a>
	 * @return TRUE when init succeed
	 *
	 */
    public boolean init(Context appContext, String key) {

        if (!super.init(appContext)){
            return false;
        }

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

	private String genLocaleStr(Locale locale) {

		return locale.getLanguage() + "-" + locale.getCountry();
	}

	private String genPlaceIdLink(String placeId, Locale locale) {

		// https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4&key=AddYourOwnKeyHere
		// Ref:
		// https://developers.google.com/places/documentation/details#PlaceDetailsRequests

		String localeStr = genLocaleStr(locale);

		String result = String.format("https://maps.googleapis.com/maps/api/place/details/json?placeid=%s&language=%s&key=%s", placeId, localeStr, getKey());

		return result;

	}

	private String genQueryLink(double latitude, double longitude, int radiusInMeter, String keyword, Locale locale, boolean isRankByDistance) {

		String localeStr = genLocaleStr(locale);

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

    private String genAutoCompleteLink(String input, Locale locale ) {

        String localeStr = genLocaleStr(locale);

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
	 * @param keyword
	 *            Search keyword
	 * @param latitude
	 *            Latitude of location
	 * @param longitude
	 *            Longitude of location
	 * @param radiusInMeter
	 *            Radius in meter. It should be at least 1. Note: if
	 *            rankByDistance is true, original value will be ignored and
	 *            auto-set to be 50km
	 * @param locale
	 *            Expected locale of result
	 * @param rankByDistance
	 *            Whether result is sorted based on distance between latLng and
	 *            searched result. Nearest=first. Note: if rankByDistance is
	 *            true, radiusInMeter will be ignored and auto-set to be 50km
	 * @param listener
	 *            Listener which will be triggered when search results are
	 *            returned
	 */
	public void searchPlaces(final String keyword, double latitude, double longitude, int radiusInMeter, final Locale locale,
			boolean rankByDistance, final OnSearchPlacesListener listener) {

		if (listener == null)
			return;

		if (keyword.isEmpty()) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					listener.returnPlaces(new ArrayList<Place>(), keyword, "");

				}

			});

			return;
		}

		String link = genQueryLink(latitude, longitude, radiusInMeter, keyword, locale, rankByDistance);

		if (link.isEmpty()) {
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

    public void autoComplete(final String input, final Locale locale, final OnAutoCompleteListener listener){

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

		if (responseJObj == null)
			listener.returnPlaces(new ArrayList<Place>(), keyword, "");

		String status = "";
		JSONArray resultsJArray = null;
		JSONObject resultJObj = null;

		ArrayList<Place> results = new ArrayList<Place>();

		boolean hasException = false;
		try {

			status = responseJObj.getString("status");
			if (!"OK".equals(status)) {
				listener.returnPlaces(new ArrayList<Place>(), keyword, status);

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
			resultsJArray = responseJObj.getJSONArray("results");

			Location location = null;
			String placeId = "";
			String name = "";
			String formattedAddress = "";

			for (int i = 0; i < resultsJArray.length(); i++) {
				resultJObj = resultsJArray.getJSONObject(i);

				placeId = resultJObj.optString("place_id");
				name = resultJObj.optString("name");
				location = obtainLocationFromGeometryJObj(resultJObj.optJSONObject("geometry"));
				formattedAddress = resultJObj.optString("vicinity");

				if (location != null)
					results.add(new Place(placeId, name, formattedAddress, location.getLatitude(), location.getLongitude() ));


			}

		} catch (Exception e) {
			e.printStackTrace();
			hasException = true;
		}

		listener.returnPlaces((!hasException) ? results : new ArrayList<Place>(), keyword, status);

	}

	private void response2GetPlaceFromLatLng(JSONObject responseJObj, Locale locale, OnGetPlaceListener listener) {

		if (listener == null)
			return;

		if (responseJObj == null)
			listener.returnPlace(null);

		String status = "";
		JSONArray resultsJArray = null;
		JSONObject resultJObj = null;

		String placeId = "";
		String name = "";
		Location location = null;
		String formattedAddress = "";

		boolean hasException = false;

		try {

			status = responseJObj.getString("status");
			if (!"OK".equals(status)) {
				listener.returnPlace(null);
				return;
			}

			resultsJArray = responseJObj.getJSONArray("results");



			for (int i = 0; i < resultsJArray.length(); i++) {
				resultJObj = resultsJArray.getJSONObject(i);

				placeId = resultJObj.optString("place_id", "");
				name = resultJObj.optString("name", "");
				formattedAddress = resultJObj.optString("vicinity", "");
				location = obtainLocationFromGeometryJObj(resultJObj.optJSONObject("geometry"));


				if (location != null)
					break;

			}

		} catch (Exception e) {
			e.printStackTrace();
			hasException = true;
		}

		boolean isResultValid = !placeId.isEmpty() && !name.isEmpty() && location!=null;

		listener.returnPlace((hasException || !isResultValid) ? null : new Place(placeId, name, formattedAddress, location.getLatitude(), location.getLongitude()));

	}

	private void response2GetPlaceFromPlaceId(JSONObject responseJObj, Locale locale, OnGetPlaceListener listener) {

		if (listener == null)
			return;

		if (responseJObj == null)
			listener.returnPlace(null);

		String status = "";
		JSONObject resultJObj = null;

		String placeId = "";
		String name = "";
		String formattedAddress = "";

		Location location = null;

		boolean hasException = false;

		try {

			status = responseJObj.getString("status");
			if (!"OK".equals(status)) {
				listener.returnPlace(null);
				return;
			}

			resultJObj = responseJObj.getJSONObject("result");

			placeId = resultJObj.optString("place_id");
			name = resultJObj.optString("name");
			formattedAddress = resultJObj.optString("formatted_address");
			location = obtainLocationFromGeometryJObj(resultJObj.optJSONObject("geometry"));


		} catch (Exception e) {
			e.printStackTrace();
			hasException = true;
		}

		boolean isResultValid = !placeId.isEmpty() && !name.isEmpty() && location!=null;

		listener.returnPlace((hasException || !isResultValid) ? null : new Place(placeId, name, formattedAddress, location.getLatitude(), location.getLongitude()));

	}

    private void response2AutoComplete(JSONObject response, String input, Locale locale, OnAutoCompleteListener listener) {

        if (listener == null)
            return;

        if (response == null)
            listener.returnAutoCompletes(new ArrayList<AutoComplete>(), input, response);

		ArrayList<AutoComplete> results = new ArrayList<AutoComplete>();

        JSONArray predictionsJArray = null;
		JSONObject predictionJObj = null;
		JSONObject firstMatchedSubstring = null;
		JSONArray termJArray = null;

        String status = "";
        String placeId = "";
        String description = "";
        int offset = -1;
        int length = -1;
		ArrayList<String> terms = new ArrayList<>();

        boolean hasException = false;
        try {

            status = response.getString("status");
            if (!"OK".equals(status)) {
                listener.returnAutoCompletes(new ArrayList<AutoComplete>(), input, response);
                return;
            }

            predictionsJArray = response.getJSONArray("predictions");

			for (int i = 0; i < predictionsJArray.length(); i++) {
				predictionJObj = predictionsJArray.getJSONObject(i);

				placeId = predictionJObj.optString("place_id", "");
				description = predictionJObj.optString("description", "");


				try {
					firstMatchedSubstring = predictionJObj.getJSONArray("matched_substrings").getJSONObject(0);
					offset = firstMatchedSubstring.getInt("offset");
					length = firstMatchedSubstring.getInt("length");
				} catch (Exception e){
					e.printStackTrace();
					offset = 0;
					length = 0;
				}

				try {

					terms = new ArrayList<>();
					termJArray = predictionJObj.getJSONArray("terms");
					for (int j=0; j <termJArray.length(); j++) {
						terms.add(termJArray.getJSONObject(j).getString("value"));
					}


				} catch (Exception e){
					e.printStackTrace();
					terms = new ArrayList<>();
				}



				results.add(new AutoComplete(placeId, description, offset, length, terms));

			}

        } catch (Exception e) {
            e.printStackTrace();
            hasException = true;
        }

//        boolean isResultValid = !placeId.isEmpty() && !description.isEmpty() && location!=null;
//        listener.returnPlace((hasException || !isResultValid) ? null : new Place(placeId, description, location.getLatitude(), location.getLongitude()));

		listener.returnAutoCompletes(hasException ? new ArrayList<AutoComplete>() : results, input, response);

    }

}
