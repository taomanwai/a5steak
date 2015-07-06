package com.tommytao.a5steak.util.google;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

import com.tommytao.a5steak.util.Foundation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class GeocodeManager extends Foundation {

	public static interface OnGetListener {

		public void returnGeocode(Geocode geocode);

	}

	public static interface OnSearchListener {

		public void returnPOIPoints(ArrayList<POIPoint> poiPoints, String keyword);

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
			this.locale = new Locale(locale.getLanguage(), locale.getCountry(), locale.getVariant());

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

	public static class UrlSigner {

		// Note: Generally, you should store your private key someplace safe
		// and read them into your code

		private static String keyString = "YOUR_PRIVATE_KEY";

		// The URL shown in these examples must be already
		// URL-encoded. In practice, you will likely have code
		// which assembles your URL from user or web service input
		// and plugs those values into its parameters.
		private static String urlString = "YOUR_URL_TO_SIGN";

		// This variable stores the binary key, which is computed from the
		// string
		// (Base64) key
		private static byte[] key;

		public UrlSigner(String keyString) throws IOException {
			// Convert the key from 'web safe' base 64 to binary
			keyString = keyString.replace('-', '+');
			keyString = keyString.replace('_', '/');
			System.out.println("Key: " + keyString);
			key = Base64.decode(keyString, Base64.DEFAULT);

		}

		public String signRequest(String path, String query) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException,
                URISyntaxException {

			// Retrieve the proper URL components to sign
			String resource = path + '?' + query;

			// Get an HMAC-SHA1 signing key from the raw key bytes
			SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");

			// Get an HMAC-SHA1 Mac instance and initialize it with the
			// HMAC-SHA1
			// key
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(sha1Key);

			// compute the binary signature for the request
			byte[] sigBytes = mac.doFinal(resource.getBytes());

			// base 64 encode the binary signature
			String signature = Base64.encodeToString(sigBytes, Base64.DEFAULT);

			// convert the signature to 'web safe' base 64
			signature = signature.replace('+', '-');
			signature = signature.replace('/', '_');

			return resource + "&signature=" + signature;
		}

	}

	public final int DEFAULT_MAX_NO_OF_RETRIES = 3;

	public static final String CLIENT_ID_FOR_BUSINESS = "gme-easyvanhongkonglimited";
	public static final String CRYPTO_FOR_BUSINESS = "RglSWAR2KO9R2OghAMwyj4WqIXg=";
	public static final String API_DOMAIN = "https://maps.google.com";

	public static final String WORLD_BOUNDS = "-90,-180|90,180";

	private static GeocodeManager instance = null;

	private GeocodeManager() {

	}

	public static GeocodeManager getInstance() {
		if (instance != null)
			return instance;
		else
			return instance = new GeocodeManager();

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

	private String genGetLink(double latitude, double longitude, Locale locale) {

		String localeStr = locale.getLanguage() + "-" + locale.getCountry();

		// if (locale.getLanguage().equals("zh"))
		// localeString = "zh-tw"; // coz if "zh" or "zh-CN" means Simplified
		// // Chinese

		String result = businessNize(String.format("http://maps.google.com/maps/api/geocode/json?latlng=%.6f,%.6f&language=%s&client=%s", latitude, longitude,
                localeStr, CLIENT_ID_FOR_BUSINESS));

		if (result.endsWith("\r\n"))
			result = result.substring(0, result.length() - 3);

		return result;

	}

	private String genSearchByBoundsLink(String address, String bounds, Locale locale) {
		String encodedAddress = address;
		try {
			encodedAddress = URLEncoder.encode(address, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String localeString = locale.getLanguage() + "-" + locale.getCountry();

		// if (locale.getLanguage().equals("zh"))
		// localeString = "zh-tw";

		return businessNize(String.format("http://maps.google.com/maps/api/geocode/json?address=%s&bounds=%s&language=%s&client=%s", encodedAddress, bounds,
                localeString, CLIENT_ID_FOR_BUSINESS));
	}

	private String genSearchByCountryLink(String address, String country, Locale locale) {
		String encodedAddress = address;
		try {
			encodedAddress = URLEncoder.encode(address, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String localeString = locale.getLanguage() + "-" + locale.getCountry();

		// if (locale.getLanguage().equals("zh"))
		// localeString = "zh-tw";

		return businessNize(String.format("http://maps.google.com/maps/api/geocode/json?address=%s&components=country:%s&language=%s&client=%s",
                encodedAddress, country, localeString, CLIENT_ID_FOR_BUSINESS));
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
		if (link.isEmpty()) {

			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					listener.returnGeocode(null);
				}

			});

			return;

		}

		// JSONObject params = new JSONObject();
		// ForeverCacheJsonObjectRequest req = new
		// ForeverCacheJsonObjectRequest(Method.GET, link, params, new
		// Response.Listener<JSONObject>() {
		//
		// @Override
		// public void onResponse(JSONObject response) {
		// response2Get(response, locale, listener);
		// }
		// }, new Response.ErrorListener() {
		//
		// @Override
		// public void onErrorResponse(VolleyError error) {
		// response2Get(null, locale, listener);
		// }
		// });
		//
		// volleyReqQueue.add(req);

		httpGetJSON(link, DEFAULT_MAX_NO_OF_RETRIES, new OnHttpGetJSONListener() {

			@Override
			public void onComplete(JSONObject response) {

				response2Get(response, locale, listener);

			}

		});

	}

	/**
	 * Search nearby location based on Google Geocode protocol
	 * 
	 * @param keyword
	 *            Search query
	 * @param bounds
	 *            Boundary of search region (format: e.g.:
	 *            22.1533884,113.835078|22.561968,114.4069561)
	 * @param locale
	 *            Locale of search query
	 * @param listener
	 *            Listener which will be triggered when search completed,
	 *            listener will also return search result
	 */
	public void searchByBounds(final String keyword, final String bounds, final Locale locale, final OnSearchListener listener) {

		if (listener == null)
			return;

		String link = genSearchByBoundsLink(keyword, bounds, locale);

		// JSONObject params = new JSONObject();
		// ForeverCacheJsonObjectRequest req = new
		// ForeverCacheJsonObjectRequest(Method.GET, link, params, new
		// Response.Listener<JSONObject>() {
		//
		// @Override
		// public void onResponse(JSONObject response) {
		// response2Search(response, keyword, bounds, locale, listener);
		// }
		//
		// }, new Response.ErrorListener() {
		//
		// @Override
		// public void onErrorResponse(VolleyError error) {
		// response2Search(null, keyword, bounds, locale, listener);
		// }
		// });
		//
		// volleyReqQueue.add(req);

		httpGetJSON(link, DEFAULT_MAX_NO_OF_RETRIES, new OnHttpGetJSONListener() { 

			@Override
			public void onComplete(JSONObject response) {
				response2Search(response, keyword, bounds, locale, listener);

			}

		});

	}

	public void searchByCountry(final String keyword, final String country, final Locale locale, final OnSearchListener listener) {

		if (listener == null)
			return;

		String link = genSearchByCountryLink(keyword, country, locale);

		// JSONObject params = new JSONObject();
		// ForeverCacheJsonObjectRequest req = new
		// ForeverCacheJsonObjectRequest(Method.GET, link, params, new
		// Response.Listener<JSONObject>() {
		//
		// @Override
		// public void onResponse(JSONObject response) {
		// response2Search(response, keyword, WORLD_BOUNDS, locale, listener);
		// }
		//
		// }, new Response.ErrorListener() {
		//
		// @Override
		// public void onErrorResponse(VolleyError error) {
		// response2Search(null, keyword, WORLD_BOUNDS, locale, listener);
		// }
		// });
		//
		// volleyReqQueue.add(req);

		httpGetJSON(link, DEFAULT_MAX_NO_OF_RETRIES, new OnHttpGetJSONListener() {

			@Override
			public void onComplete(JSONObject response) {
				response2Search(null, keyword, WORLD_BOUNDS, locale, listener);

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

		ArrayList<POIPoint> poiPoints = new ArrayList<POIPoint>();

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

			listener.returnPOIPoints(poiPoints, keyword);

		} catch (Exception e) {
			e.printStackTrace();

			listener.returnPOIPoints(new ArrayList<POIPoint>(), keyword);
		}

	}

}
