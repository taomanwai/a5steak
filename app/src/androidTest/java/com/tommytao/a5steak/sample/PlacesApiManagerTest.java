package com.tommytao.a5steak.sample;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.tommytao.a5steak.util.Encyclopedia;
import com.tommytao.a5steak.util.Foundation;
import com.tommytao.a5steak.util.google.PlacesApiManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class PlacesApiManagerTest extends ApplicationTestCase<Application> {

    public final static int AWAIT_TIME_IN_MS = Foundation.DEFAULT_CONNECT_READ_TIMEOUT_IN_MS * PlacesApiManager.DEFAULT_MAX_NUM_OF_RETRIES;

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

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        PlacesApiManager.getInstance().getPlaceFromLatLng(Encyclopedia.BEIJING_LAT, Encyclopedia.BEIJING_LNG, null, new PlacesApiManager.OnGetPlaceListener() {
            @Override
            public void returnPlace(PlacesApiManager.Place place) {

                if (place!=null && "Ren Da Hui Tang Xi Lu".equals(place.getName()))
                    succeeds.set(0, true);

                signal.countDown();

            }
        });

        TestUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testGetPlaceFromLatLng_shouldReturnEnWhenLocaleEn() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        PlacesApiManager.getInstance().getPlaceFromLatLng(Encyclopedia.BEIJING_LAT, Encyclopedia.BEIJING_LNG, Locale.US, new PlacesApiManager.OnGetPlaceListener() {
            @Override
            public void returnPlace(PlacesApiManager.Place place) {


                if (place!=null && "Ren Da Hui Tang Xi Lu".equals(place.getName()))
                    succeeds.set(0, true);

                signal.countDown();

            }
        });

        TestUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testGetPlaceFromLatLng_shouldReturnChineseWhenLocaleChinese() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        PlacesApiManager.getInstance().getPlaceFromLatLng(Encyclopedia.BEIJING_LAT, Encyclopedia.BEIJING_LNG, new Locale("zh", "HK"), new PlacesApiManager.OnGetPlaceListener() {
            @Override
            public void returnPlace(PlacesApiManager.Place place) {



                if (place!=null && "人大会堂西路".equals(place.getName()))
                    succeeds.set(0, true);

                signal.countDown();

            }
        });

        TestUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }


    public void testGetPlaceFromLatLng_shouldReturnNullWhenLatLngInvalid() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        PlacesApiManager.getInstance().getPlaceFromLatLng(Encyclopedia.BEIJING_LAT, Double.NaN, new Locale("zh", "HK"), new PlacesApiManager.OnGetPlaceListener() {
            @Override
            public void returnPlace(PlacesApiManager.Place place) {


                if (place==null)
                    succeeds.set(0, true);

                signal.countDown();

            }
        });

        TestUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testGetPlaceFromPlaceId_shouldReturnEnWhenLocaleEn() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        PlacesApiManager.getInstance().getPlaceFromPlaceId("ChIJO39LEZdS8DURbd3-N2hFGcM", Locale.US, new PlacesApiManager.OnGetPlaceListener() {
            @Override
            public void returnPlace(PlacesApiManager.Place place) {


                if (place!=null && "Ren Da Hui Tang Xi Lu".equals(place.getName()))
                    succeeds.set(0, true);


                signal.countDown();
            }
        });

        TestUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testGetPlaceFromPlaceId_shouldReturnChineseWhenLocaleChinese() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        PlacesApiManager.getInstance().getPlaceFromPlaceId("ChIJO39LEZdS8DURbd3-N2hFGcM", new Locale("zh", "HK"), new PlacesApiManager.OnGetPlaceListener() {
            @Override
            public void returnPlace(PlacesApiManager.Place place) {


                if (place!=null && "人大会堂西路".equals(place.getName()))
                    succeeds.set(0, true);

                signal.countDown();
            }
        });

        TestUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testSearchPlaces_shouldReturnTcWhenLocaleTc() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        PlacesApiManager.getInstance().searchPlaces(
                "Sport", Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, 50, new Locale("zh", "HK"), true, new PlacesApiManager.OnSearchPlacesListener() {
                    @Override
                    public void returnPlaces(ArrayList<PlacesApiManager.Place> places, String keyword, String status) {


                        if (!places.isEmpty() && "Sport".equals(keyword) && "香港體育學院".equals(places.get(0).getName()))
                            succeeds.set(0, true);

                        signal.countDown();
                    }
                });

        TestUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testSearchPlaces_shouldReturnScWhenLocaleSc() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        PlacesApiManager.getInstance().searchPlaces(
                "Sport", Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, 50, new Locale("zh", "CN"), true, new PlacesApiManager.OnSearchPlacesListener() {
                    @Override
                    public void returnPlaces(ArrayList<PlacesApiManager.Place> places, String keyword, String status) {

                        if (!places.isEmpty() && "Sport".equals(keyword) && "香港体育学院".equals(places.get(0).getName()))
                            succeeds.set(0, true);

                        signal.countDown();
                    }
                });

        TestUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testSearchPlaces_shouldReturnEnWhenLocaleEn() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        PlacesApiManager.getInstance().searchPlaces(
                "Sport", Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, 50, Locale.US, true, new PlacesApiManager.OnSearchPlacesListener() {
                    @Override
                    public void returnPlaces(ArrayList<PlacesApiManager.Place> places, String keyword, String status) {

                        if (!places.isEmpty() && "Sport".equals(keyword) && "Hong Kong Sports Institute".equals(places.get(0).getName()))
                            succeeds.set(0, true);

                        signal.countDown();

                    }
                });

        TestUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testSearchPlaces_shouldReturnEnWhenLocaleNull() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        PlacesApiManager.getInstance().searchPlaces(
                "Sport", Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, 50, null, true, new PlacesApiManager.OnSearchPlacesListener() {
                    @Override
                    public void returnPlaces(ArrayList<PlacesApiManager.Place> places, String keyword, String status) {

                        if (!places.isEmpty() && "Sport".equals(keyword) && "Hong Kong Sports Institute".equals(places.get(0).getName()))
                            succeeds.set(0, true);

                        signal.countDown();
                    }
                });

        TestUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testSearchPlaces_shouldReturnEmptyResultWhenLatLngInvalid() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        PlacesApiManager.getInstance().searchPlaces(
                "Sport", Double.NaN, Encyclopedia.HKSIL_LNG, 50, Locale.US, true, new PlacesApiManager.OnSearchPlacesListener() {
                    @Override
                    public void returnPlaces(ArrayList<PlacesApiManager.Place> places, String keyword, String status) {

                        if ("Sport".equals(keyword) && places.isEmpty())
                            succeeds.set(0, true);

                        signal.countDown();
                    }
                });

        TestUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }
    




}