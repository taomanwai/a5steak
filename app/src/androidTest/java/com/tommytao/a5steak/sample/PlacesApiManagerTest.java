package com.tommytao.a5steak.sample;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.tommytao.a5steak.util.Encyclopedia;
import com.tommytao.a5steak.util.google.PlacesApiManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class PlacesApiManagerTest extends ApplicationTestCase<Application> {

    public static int AWAIT_TIME_IN_SECOND = 8; // 8

    public PlacesApiManagerTest() {
        super(Application.class);


    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createApplication();
    }

    public void testGetPlaceFromLatLng_shouldReturnEnWhenLocaleNull() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        PlacesApiManager.getInstance().getPlaceFromLatLng(Encyclopedia.BEIJING_LAT, Encyclopedia.BEIJING_LNG, null, new PlacesApiManager.OnGetPlaceListener() {
            @Override
            public void returnPlace(PlacesApiManager.Place place) {

                if (place == null)
                    return;

                if (!"Ren Da Hui Tang Xi Lu".equals(place.getName()))
                    return;

                signal.countDown();

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testGetPlaceFromLatLng_shouldReturnEnWhenLocaleEn() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        PlacesApiManager.getInstance().getPlaceFromLatLng(Encyclopedia.BEIJING_LAT, Encyclopedia.BEIJING_LNG, Locale.US, new PlacesApiManager.OnGetPlaceListener() {
            @Override
            public void returnPlace(PlacesApiManager.Place place) {

                if (place == null)
                    return;

                if (!"Ren Da Hui Tang Xi Lu".equals(place.getName()))
                    return;

                signal.countDown();

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testGetPlaceFromLatLng_shouldReturnChineseWhenLocaleChinese() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        PlacesApiManager.getInstance().getPlaceFromLatLng(Encyclopedia.BEIJING_LAT, Encyclopedia.BEIJING_LNG, new Locale("zh", "HK"), new PlacesApiManager.OnGetPlaceListener() {
            @Override
            public void returnPlace(PlacesApiManager.Place place) {

                if (place == null)
                    return;

                if (!"人大会堂西路".equals(place.getName()))
                    return;

                signal.countDown();

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testGetPlaceFromLatLng_shouldReturnNullWhenLatLngInvalid() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        PlacesApiManager.getInstance().getPlaceFromLatLng(Encyclopedia.BEIJING_LAT, Double.NaN, new Locale("zh", "HK"), new PlacesApiManager.OnGetPlaceListener() {
            @Override
            public void returnPlace(PlacesApiManager.Place place) {

                if (place != null)
                    return;

                signal.countDown();

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testGetPlaceFromPlaceId_shouldReturnEnWhenLocaleEn() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        PlacesApiManager.getInstance().getPlaceFromPlaceId("ChIJO39LEZdS8DURbd3-N2hFGcM", Locale.US, new PlacesApiManager.OnGetPlaceListener() {
            @Override
            public void returnPlace(PlacesApiManager.Place place) {
                if (place == null)
                    return;

                if (!"Ren Da Hui Tang Xi Lu".equals(place.getName()))
                    return;

                signal.countDown();
            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testGetPlaceFromPlaceId_shouldReturnChineseWhenLocaleChinese() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        PlacesApiManager.getInstance().getPlaceFromPlaceId("ChIJO39LEZdS8DURbd3-N2hFGcM", new Locale("zh", "HK"), new PlacesApiManager.OnGetPlaceListener() {
            @Override
            public void returnPlace(PlacesApiManager.Place place) {
                if (place == null)
                    return;

                if (!"人大会堂西路".equals(place.getName()))
                    return;

                signal.countDown();
            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testSearchPlaces_shouldReturnTcWhenLocaleTc() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        PlacesApiManager.getInstance().searchPlaces(
                "Sport", Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, 50, new Locale("zh", "HK"), true, new PlacesApiManager.OnSearchPlacesListener() {
                    @Override
                    public void returnPlaces(ArrayList<PlacesApiManager.Place> places, String keyword, String status) {

                        if (places.isEmpty())
                            return;

                        if (!"Sport".equals(keyword))
                            return;

                        if (!"香港體育學院".equals(places.get(0).getName())) {
                            return;
                        }

                        signal.countDown();
                    }
                });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testSearchPlaces_shouldReturnScWhenLocaleSc() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        PlacesApiManager.getInstance().searchPlaces(
                "Sport", Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, 50, new Locale("zh", "CN"), true, new PlacesApiManager.OnSearchPlacesListener() {
                    @Override
                    public void returnPlaces(ArrayList<PlacesApiManager.Place> places, String keyword, String status) {

                        if (places.isEmpty())
                            return;

                        if (!"Sport".equals(keyword))
                            return;

                        if (!"香港体育学院".equals(places.get(0).getName())) {
                            return;
                        }

                        signal.countDown();
                    }
                });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testSearchPlaces_shouldReturnEnWhenLocaleEn() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        PlacesApiManager.getInstance().searchPlaces(
                "Sport", Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, 50, Locale.US, true, new PlacesApiManager.OnSearchPlacesListener() {
                    @Override
                    public void returnPlaces(ArrayList<PlacesApiManager.Place> places, String keyword, String status) {

                        if (places.isEmpty())
                            return;

                        if (!"Sport".equals(keyword))
                            return;

                        if (!"Hong Kong Sports Institute".equals(places.get(0).getName())) {
                            return;
                        }

                        signal.countDown();
                    }
                });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testSearchPlaces_shouldReturnEnWhenLocaleNull() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        PlacesApiManager.getInstance().searchPlaces(
                "Sport", Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, 50, null, true, new PlacesApiManager.OnSearchPlacesListener() {
                    @Override
                    public void returnPlaces(ArrayList<PlacesApiManager.Place> places, String keyword, String status) {

                        if (places.isEmpty())
                            return;

                        if (!"Sport".equals(keyword))
                            return;

                        if (!"Hong Kong Sports Institute".equals(places.get(0).getName())) {
                            return;
                        }

                        signal.countDown();
                    }
                });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

    public void testSearchPlaces_shouldReturnEmptyResultWhenLatLngInvalid() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        PlacesApiManager.getInstance().searchPlaces(
                "Sport", Double.NaN, Encyclopedia.HKSIL_LNG, 50, Locale.US, true, new PlacesApiManager.OnSearchPlacesListener() {
                    @Override
                    public void returnPlaces(ArrayList<PlacesApiManager.Place> places, String keyword, String status) {

                        if (!"Sport".equals(keyword))
                            return;

                        if (!places.isEmpty())
                            return;

                        signal.countDown();
                    }
                });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }
    




}