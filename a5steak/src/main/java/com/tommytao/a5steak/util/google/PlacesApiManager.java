package com.tommytao.a5steak.util.google;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.tommytao.a5steak.util.Foundation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class PlacesApiManager extends Foundation {

	public static class Place {

		private String placeId = "";
		private String name = "";

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

		public Place(String placeId, String name, double latitude, double longitude) {
			this.placeId = placeId;
			this.name = name;

			this.latitude = latitude;
			this.longitude = longitude;
		}

		public String getPlaceId() {
			return placeId;
		}

		public float distanceBetween(double anotherLatitude, double anotherLongitude) {

			float[] results = new float[3];

			Location.distanceBetween(latitude, longitude, anotherLatitude, anotherLongitude, results);

			return results[0];

		}

	}
	
	public final int DEFAULT_MAX_NO_OF_RETRIES = 3;

	public static final String KEY = "AIzaSyDho8iArjPHWI7GiY1xGhefeB6LplFucdI";
	public static final int GET_PLACE_FROM_LAT_LNG_IN_METER = 5;

	public static interface OnGetPlaceListener {

		public void returnPlace(Place place);

	}

	public static interface OnSearchPlacesListener {

		public void returnPlaces(ArrayList<Place> places, String keyword, String status);

	}

	private static PlacesApiManager instance;

	public static PlacesApiManager getInstance() {

		if (instance == null)
			instance = new PlacesApiManager();

		return instance;
	}

	private PlacesApiManager() {

	}

	// --

	private Location obtainLocationFromGeometryJObj(JSONObject geometryJObj) {

		double lat = 0;
		double lng = 0;

		boolean isSucceed = false;

		try {
			JSONObject locationJObj = geometryJObj.getJSONObject("location");
			lat = locationJObj.getDouble("lat");
			lng = locationJObj.getDouble("lng");
			isSucceed = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!isSucceed)
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
		// String lang = locale.getLanguage();
		// String country = locale.getCountry();
		//
		// String localeStr = lang + "-" + country;

		return locale.getLanguage() + "-" + locale.getCountry();
	}

	private String genPlaceIdLink(String placeId, Locale locale) {

		// https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4&key=AddYourOwnKeyHere
		// Ref:
		// https://developers.google.com/places/documentation/details#PlaceDetailsRequests

		String localeStr = genLocaleStr(locale);

		String result = String.format("https://maps.googleapis.com/maps/api/place/details/json?placeid=%s&language=%s&key=%s", placeId, localeStr, KEY);

		return result;

	}

	private String genQueryLink(double latitude, double longitude, int radiusInMeter, String keyword, Locale locale, boolean isRankByDistance) {

		String localeStr = genLocaleStr(locale);

		String keywordURLEncoded = "";
		try {
			keywordURLEncoded = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// TODO testing purpose, del later
		// radiusInMeter = 50000;
		// localeStr = "th";
		// ===========================

		String result = String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%.6f,%.6f&keyword=%s&language=%s&key=%s",
                latitude, longitude, keywordURLEncoded, localeStr, KEY);

		result += !isRankByDistance ? ("&radius=" + radiusInMeter) : "&rankby=distance";

		return result;

	}

	public void getPlaceFromLatLng(double latitude, double longitude, final Locale locale, final OnGetPlaceListener listener) {

		if (listener == null)
			return;

		String link = genQueryLink(latitude, longitude, GET_PLACE_FROM_LAT_LNG_IN_METER, "", locale, false);

		if (link.isEmpty()) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					listener.returnPlace(null);
				}

			});
			return;
		}

//		JSONObject params = new JSONObject();
//		ForeverCacheJsonObjectRequest req = new ForeverCacheJsonObjectRequest(Method.GET, link, params, new Response.Listener<JSONObject>() {
//
//			@Override
//			public void onResponse(JSONObject response) {
//
//				response2GetPlace(response, locale, listener);
//
//			}
//
//		}, new Response.ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//
//				response2GetPlace(null, locale, listener);
//
//			}
//		});
//
//		volleyReqQueue.add(req);
		
		httpGetJSON(link, DEFAULT_MAX_NO_OF_RETRIES, new OnHttpGetJSONListener() {

			@Override
			public void onComplete(JSONObject response) {

				response2GetPlace(null, locale, listener);

			}

		});

	}

	public void getPlaceFromPlaceId(String placeId, final Locale locale, final OnGetPlaceListener listener) {

		if (listener == null)
			return;

		String link = genPlaceIdLink(placeId, locale);

		if (link.isEmpty()) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					listener.returnPlace(null);
				}

			});
			return;
		}

//		JSONObject params = new JSONObject();
//		ForeverCacheJsonObjectRequest req = new ForeverCacheJsonObjectRequest(Method.GET, link, params, new Response.Listener<JSONObject>() {
//
//			@Override
//			public void onResponse(JSONObject response) {
//
//				response2GetPlace(response, locale, listener);
//
//			}
//		}, new Response.ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//
//				response2GetPlace(null, locale, listener);
//
//			}
//		});
//
//		volleyReqQueue.add(req);
		
		httpGetJSON(link, DEFAULT_MAX_NO_OF_RETRIES, new OnHttpGetJSONListener() {

			@Override
			public void onComplete(JSONObject response) {

				response2GetPlace(null, locale, listener);

			}

		});
	}

	public static String returnPlacesStr(ArrayList<Place> places) {

		StringBuffer sb = new StringBuffer();
		for (Place place : places)
			sb.append(place.getName() + " , ");

		return sb.toString();
	}

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
	 *            isRankByDistance is true, original value will be ignored and
	 *            auto-set to be 50km
	 * @param locale
	 *            Expected locale of result
	 * @param isRankByDistance
	 *            Whether result is sorted based on distance between latLng and
	 *            searched result. Nearest=first. Note: if isRankByDistance is
	 *            true, radiusInMeter will be ignored and auto-set to be 50km
	 * @param listener
	 *            Listener which will be triggered when search results are
	 *            returned
	 */
	public void searchPlaces(final String keyword, double latitude, double longitude, int radiusInMeter, final Locale locale,
			boolean isRankByDistance, final OnSearchPlacesListener listener) {

		if (listener == null)
			return;

		if (keyword.isEmpty()) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					listener.returnPlaces(new ArrayList<Place>(), keyword, "");

				}

			});

			return;
		}

		String link = genQueryLink(latitude, longitude, radiusInMeter, keyword, locale, isRankByDistance);

		if (link.isEmpty()) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					listener.returnPlaces(new ArrayList<Place>(), keyword, "");
				}

			});

			return;
		}

//		JSONObject params = new JSONObject();
//		ForeverCacheJsonObjectRequest req = new ForeverCacheJsonObjectRequest(Method.GET, link, params, new Response.Listener<JSONObject>() {
//
//			@Override
//			public void onResponse(JSONObject response) {
//				response2SearchPlaces(response, keyword, locale, listener);
//			}
//
//		}, new Response.ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				response2SearchPlaces(null, keyword, locale, listener);
//			}
//
//		});
//
//		volleyReqQueue.add(req);
		
		httpGetJSON(link, DEFAULT_MAX_NO_OF_RETRIES, new OnHttpGetJSONListener() {

			@Override
			public void onComplete(JSONObject response) {

				response2SearchPlaces(null, keyword, locale, listener);

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

			for (int i = 0; i < resultsJArray.length(); i++) {
				resultJObj = resultsJArray.getJSONObject(i);

				// if (isResultType(resultJObj, "establishment") ||
				// isResultType(resultJObj, "neighborhood")) {

				// NO FILTERING! Follow iOS!
				String placeId = resultJObj.getString("place_id");
				String name = resultJObj.getString("name");
				location = obtainLocationFromGeometryJObj(resultJObj.getJSONObject("geometry"));

				if (location != null)
					results.add(new Place(placeId, name, location.getLatitude(), location.getLongitude()));

				// }

			}

		} catch (Exception e) {
			e.printStackTrace();
			hasException = true;
		}

		listener.returnPlaces((!hasException) ? results : new ArrayList<Place>(), keyword, status);

	}

	private void response2GetPlace(JSONObject responseJObj, Locale locale, OnGetPlaceListener listener) {

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

				// NO FILTERING! Follow iOS! As per requested by Milan
				// if (isResultType(resultJObj, "establishment")) {
				placeId = resultJObj.optString("place_id", "");
				name = resultJObj.optString("name", "");
				location = obtainLocationFromGeometryJObj(resultJObj.getJSONObject("geometry"));

				if (location != null)
					break;
				// }
			}

		} catch (Exception e) {
			e.printStackTrace();
			hasException = true;
		}
		
		boolean isResultValid = !placeId.isEmpty() && !name.isEmpty() && location!=null;

		listener.returnPlace((hasException || !isResultValid) ? null : new Place(placeId, name, location.getLatitude(), location.getLongitude()));

	}

}
