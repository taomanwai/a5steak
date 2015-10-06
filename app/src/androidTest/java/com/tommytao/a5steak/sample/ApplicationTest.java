package com.tommytao.a5steak.sample;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;
import android.text.TextUtils;
import android.util.Log;

import com.tommytao.a5steak.util.Encyclopedia;
import com.tommytao.a5steak.util.google.GeocodeManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    
    public static int AWAIT_TIME_IN_SECOND = 60; // 8

    public ApplicationTest() {
        super(Application.class);


    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createApplication();
    }


    // == GeocodeManager ==

    public void testGeocodeManager_Get_shouldShowEnWhenLocaleEnUsIsSet() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);


        GeocodeManager.getInstance().get(Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, new Locale("en", "US"), new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    return;

                if (!"25 Yuen Wo Rd, Sha Tin, Hong Kong".equals(geocode.getFormattedAddress()))
                    return;

                signal.countDown();

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testGeocodeManager_Get_shouldShowTcWhenLocaleZhHkIsSet() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);


        GeocodeManager.getInstance().get(Encyclopedia.BEIJING_LAT, Encyclopedia.BEIJING_LNG, new Locale("zh", "HK"), new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    return;

                if (TextUtils.isEmpty(geocode.getFormattedAddress()))
                    return;

                if (!geocode.getFormattedAddress().startsWith("中國"))
                    return;

                signal.countDown();

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testGeocodeManager_Get_shouldShowScWhenLocaleZhCnIsSet() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        GeocodeManager.getInstance().get(Encyclopedia.BEIJING_LAT, Encyclopedia.BEIJING_LNG, new Locale("zh", "CN"), new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    return;

                if (TextUtils.isEmpty(geocode.getFormattedAddress()))
                    return;

                if (!geocode.getFormattedAddress().startsWith("中国"))
                    return;

                signal.countDown();

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testGeocodeManager_Get_shouldAssumeAsLocaleUsWhenLocaleIsNull() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        GeocodeManager.getInstance().get(Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    return;

                if (!"25 Yuen Wo Rd, Sha Tin, Hong Kong".equals(geocode.getFormattedAddress()))
                    return;

                signal.countDown();

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));


    }

    public void testGeocodeManager_Get_shouldReturnNullWhenOneOfLatIsNaN() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        GeocodeManager.getInstance().get(Double.NaN, Encyclopedia.HKSIL_LNG, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    signal.countDown();
                else {
                    // strange
                }

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testGeocodeManager_Get_shouldReturnNullWhenOneOfLngIsNaN() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        GeocodeManager.getInstance().get(Encyclopedia.HKSIL_LAT, Double.NaN, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    signal.countDown();
                else {
                    // strange
                }

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testGeocodeManager_Get_shouldReturnNullWhenBothLatLngIsNaN() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        GeocodeManager.getInstance().get(Double.NaN, Double.NaN, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    signal.countDown();
                else {
                    // strange
                }

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testGeocodeManager_Get_shouldReturnNullWhenLatIsOutOfEarth() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        GeocodeManager.getInstance().get(91, 144, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    signal.countDown();
                else {
                    // strange
                }

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testGeocodeManager_Get_shouldReturnNullWhenLngIsOutOfEarth() throws Exception  {

        final CountDownLatch signal = new CountDownLatch(1);

        GeocodeManager.getInstance().get(22, 190, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    signal.countDown();
                else {
                    // strange
                }

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testGeocodeManager_SearchByCountry_shouldReturnHkResultInEnWhenCountryHkLocaleNull() throws Exception  {
        final CountDownLatch signal = new CountDownLatch(1);

        final String query = "長沙灣政府合署";
        GeocodeManager.getInstance().searchByCountry(query, "HK", null, new GeocodeManager.OnSearchListener() {
            @Override
            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {

                if (!query.equals(keyword))
                    return;

                if (poiPoints.isEmpty())
                    return;

                if (!"Cheung Sha Wan Government Offices, 303 Cheung Sha Wan Rd, Sham Shui Po, Hong Kong".equals(
                        poiPoints.get(0).getFormattedAddress()))
                    return;

                signal.countDown();

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
    }

    public void testGeocodeManager_SearchByCountry_shouldReturnHkResultInEnWhenCountryHkLocaleEnUs() throws Exception  {
        final CountDownLatch signal = new CountDownLatch(1);

        final String query = "長沙灣政府合署";
        GeocodeManager.getInstance().searchByCountry(query, "HK", Locale.US, new GeocodeManager.OnSearchListener() {
            @Override
            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {

                if (!query.equals(keyword))
                    return;

                if (poiPoints.isEmpty())
                    return;

                if (!"Cheung Sha Wan Government Offices, 303 Cheung Sha Wan Rd, Sham Shui Po, Hong Kong".equals(
                        poiPoints.get(0).getFormattedAddress()))
                    return;

                signal.countDown();

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testGeocodeManager_SearchByCountry_shouldReturnHkResultInTcWhenCountryHkLocaleZhHk() throws Exception  {

        final CountDownLatch signal = new CountDownLatch(1);

        final String query = "長沙灣政府合署";
        GeocodeManager.getInstance().searchByCountry(query, "HK", new Locale("zh", "HK"), new GeocodeManager.OnSearchListener() {
            @Override
            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {

                if (!query.equals(keyword))
                    return;

                if (poiPoints.isEmpty())
                    return;

                if (!"香港深水埗長沙灣道303號長沙灣政府合署".equals(
                        poiPoints.get(0).getFormattedAddress()))
                    return;

                signal.countDown();

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testGeocodeManager_SearchByCountry_shouldReturnHkResultInScWhenCountryHkLocaleZhCn() throws Exception  {
        final CountDownLatch signal = new CountDownLatch(1);

        final String query = "長沙灣政府合署";
        GeocodeManager.getInstance().searchByCountry(query, "HK", new Locale("zh", "CN"), new GeocodeManager.OnSearchListener() {
            @Override
            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {

                if (!query.equals(keyword))
                    return;

                if (poiPoints.isEmpty())
                    return;

                if (!"香港深水埗長沙灣道303號长沙湾政府合署".equals(
                        poiPoints.get(0).getFormattedAddress()))
                    return;

                signal.countDown();

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
    }

    public void testGeocodeManager_SearchByCountry_shouldReturnCnResultInEnWhenCountryCnLocaleEnUs() throws Exception  {
        final CountDownLatch signal = new CountDownLatch(1);

        final String query = "天安門";
        GeocodeManager.getInstance().searchByCountry(query, "CN", new Locale("en", "US"), new GeocodeManager.OnSearchListener() {
            @Override
            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {

                if (!query.equals(keyword))
                    return;

                if (poiPoints.isEmpty())
                    return;

                if (!"Tiananmen, Dongcheng, Beijing, China".equals(
                        poiPoints.get(0).getFormattedAddress()))
                    return;

                signal.countDown();

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
    }

    public void testGeocodeManager_SearchByCountry_shouldReturnCnResultInTcWhenCountryCnLocaleZhHk() throws Exception  {
        final CountDownLatch signal = new CountDownLatch(1);

        final String query = "天安門";
        GeocodeManager.getInstance().searchByCountry(query, "CN", new Locale("zh", "HK"), new GeocodeManager.OnSearchListener() {
            @Override
            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {

                if (!query.equals(keyword))
                    return;

                if (poiPoints.isEmpty())
                    return;

                if (!"中國北京市東城區天安門".equals(
                        poiPoints.get(0).getFormattedAddress()))
                    return;

                signal.countDown();

            }
        });

        boolean b = signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS);
        assertTrue(b);
    }

    public void testGeocodeManager_SearchByCountry_shouldReturnCnResultInScWhenCountryCnLocaleZhCn() throws Exception  {
        final CountDownLatch signal = new CountDownLatch(1);

        final String query = "天安門";
        GeocodeManager.getInstance().searchByCountry(query, "CN", new Locale("zh", "CN"), new GeocodeManager.OnSearchListener() {
            @Override
            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {

                if (!query.equals(keyword))
                    return;

                if (poiPoints.isEmpty())
                    return;

                if (!"中国北京市东城区天安门".equals(
                        poiPoints.get(0).getFormattedAddress()))
                    return;

                signal.countDown();

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
    }

    public void testGeocodeManager_SearchByBounds_shouldReturnResultInTcWhenBoundsValidLocaleTc() throws Exception  {
        final CountDownLatch signal = new CountDownLatch(1);

        final String query = "長沙灣政府合署";

        GeocodeManager.getInstance().searchByBounds(query,
                22.1533884, 113.835078, 22.561968, 114.4069561, new Locale("zh", "HK"), new GeocodeManager.OnSearchListener() {
                    @Override
                    public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
                        if (!query.equals(keyword))
                            return;

                        if (poiPoints.isEmpty())
                            return;

                        if (!"香港深水埗長沙灣道303號長沙灣政府合署".equals(
                                poiPoints.get(0).getFormattedAddress()))
                            return;

                        signal.countDown();
                    }
                }
        );

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
    }

    public void testGeocodeManager_SearchByBounds_shouldReturnResultInScWhenBoundsValidLocaleZhCn() throws Exception  {
        final CountDownLatch signal = new CountDownLatch(1);

        final String query = "長沙灣政府合署";

        GeocodeManager.getInstance().searchByBounds(query,
                22.1533884, 113.835078, 22.561968, 114.4069561, new Locale("zh", "CN"), new GeocodeManager.OnSearchListener() {
                    @Override
                    public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
                        if (!query.equals(keyword))
                            return;

                        if (poiPoints.isEmpty())
                            return;

                        if (!"香港深水埗長沙灣道303號长沙湾政府合署".equals(
                                poiPoints.get(0).getFormattedAddress()))
                            return;

                        signal.countDown();
                    }
                }
        );

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
    }

    public void testGeocodeManager_SearchByBounds_shouldReturnResultInEnWhenBoundsValidLocaleEnUs() throws Exception  {
        final CountDownLatch signal = new CountDownLatch(1);

        final String query = "長沙灣政府合署";

        GeocodeManager.getInstance().searchByBounds(query,
                22.1533884, 113.835078, 22.561968, 114.4069561, Locale.US, new GeocodeManager.OnSearchListener() {
                    @Override
                    public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
                        if (!query.equals(keyword))
                            return;

                        if (poiPoints.isEmpty())
                            return;

                        if (!"Cheung Sha Wan Government Offices, 303 Cheung Sha Wan Rd, Sham Shui Po, Hong Kong".equals(
                                poiPoints.get(0).getFormattedAddress()))
                            return;

                        signal.countDown();
                    }
                }
        );

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
    }

    public void testGeocodeManager_SearchByBounds_shouldReturnResultInEnWhenBoundsValidLocaleNull() throws Exception  {
        final CountDownLatch signal = new CountDownLatch(1);

        final String query = "長沙灣政府合署";

        GeocodeManager.getInstance().searchByBounds(query,
                22.1533884, 113.835078, 22.561968, 114.4069561, null, new GeocodeManager.OnSearchListener() {
                    @Override
                    public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
                        if (!query.equals(keyword))
                            return;

                        if (poiPoints.isEmpty())
                            return;

                        if (!"Cheung Sha Wan Government Offices, 303 Cheung Sha Wan Rd, Sham Shui Po, Hong Kong".equals(
                                poiPoints.get(0).getFormattedAddress()))
                            return;

                        signal.countDown();
                    }
                }
        );

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
    }

    public void testGeocodeManager_SearchByBounds_shouldReturnEmptyResultWhenBoundsInValidLocaleEnUs() throws Exception  {
        final CountDownLatch signal = new CountDownLatch(1);

        final String query = "長沙灣政府合署";

        GeocodeManager.getInstance().searchByBounds(query,
                99, 113.835078, Double.NaN, 114.4069561, Locale.US, new GeocodeManager.OnSearchListener() {
                    @Override
                    public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
                        if (!query.equals(keyword))
                            return;

                        if (!poiPoints.isEmpty())
                            return;

                        signal.countDown();
                    }
                }
        );

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
    }

    // == PlacesApiManager ==


}