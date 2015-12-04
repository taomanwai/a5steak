package com.tommytao.a5steak.sample;

import android.app.Application;
import android.os.SystemClock;
import android.test.ApplicationTestCase;
import android.text.TextUtils;
import android.util.Log;

import com.tommytao.a5steak.util.Encyclopedia;
import com.tommytao.a5steak.util.Foundation;
import com.tommytao.a5steak.util.google.GeocodeManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class GeocodeManagerTest extends ApplicationTestCase<Application> {

    public final static int AWAIT_TIME_IN_MS = Foundation.DEFAULT_CONNECT_READ_TIMEOUT_IN_MS * GeocodeManager.DEFAULT_MAX_NUM_OF_RETRIES;

    public GeocodeManagerTest() {
        super(Application.class);

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        GeocodeManager.getInstance().removeContextB4UnitTest();

        createApplication();
    }


    public void testGetInDetail_shouldReturnEnWhenLocaleEnUs() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        GeocodeManager.getInstance().get(Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, new Locale("en", "US"), new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode != null
                        && "25 Yuen Wo Rd, Sha Tin, Hong Kong".equals(geocode.getFormattedAddress())
                        && "25".equals(geocode.getStreetNo())
                        && "Yuen Wo Road".equals(geocode.getRoute())
                        && "Sha Tin".equals(geocode.getNeighborhood())
                        && "New Territories".equals(geocode.getAdministrativeAreaLevel1())
                        && "Hong Kong".equals(geocode.getCountry())
                        && "".equals(geocode.getPostalCode())
                        && "".equals(geocode.getSublocality())
                        && "".equals(geocode.getLocality())
                        )
                    succeeds.set(0, true);

                signal.countDown();

            }
        });

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testGet_shouldReturnTcWhenLocaleZhHk() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        GeocodeManager.getInstance().get(Encyclopedia.BEIJING_LAT, Encyclopedia.BEIJING_LNG, new Locale("zh", "HK"), new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode != null
                        && !TextUtils.isEmpty(geocode.getFormattedAddress())
                        && geocode.getFormattedAddress().startsWith("中國"))
                    succeeds.set(0, true);

                signal.countDown();

            }
        });

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testGet_shouldReturnScWhenLocaleZhCn() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        GeocodeManager.getInstance().get(Encyclopedia.BEIJING_LAT, Encyclopedia.BEIJING_LNG, new Locale("zh", "CN"), new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode != null && !TextUtils.isEmpty(geocode.getFormattedAddress()) &&
                        geocode.getFormattedAddress().startsWith("中国"))
                    succeeds.set(0, true);

                signal.countDown();

            }
        });

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testGet_shouldAssumeAsLocaleUsWhenLocaleIsNull() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        GeocodeManager.getInstance().get(Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode != null && "25 Yuen Wo Rd, Sha Tin, Hong Kong".equals(geocode.getFormattedAddress()))
                    succeeds.set(0, true);

                signal.countDown();

            }
        });

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);


    }

    public void testGet_shouldReturnNullWhenOneOfLatIsNaN() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);


        GeocodeManager.getInstance().get(Double.NaN, Encyclopedia.HKSIL_LNG, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    succeeds.set(0, true);

                signal.countDown();

            }
        });
        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testGet_shouldReturnNullWhenOneOfLngIsNaN() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        GeocodeManager.getInstance().get(Encyclopedia.HKSIL_LAT, Double.NaN, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    succeeds.set(0, true);

                signal.countDown();

            }
        });

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testGet_shouldReturnNullWhenBothLatLngIsNaN() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        GeocodeManager.getInstance().get(Double.NaN, Double.NaN, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    succeeds.set(0, true);

                signal.countDown();

            }
        });

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testGet_shouldReturnNullWhenLatIsOutOfEarth() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        GeocodeManager.getInstance().get(91, 144, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    succeeds.set(0, true);

                signal.countDown();


            }
        });

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testGet_shouldReturnNullWhenLngIsOutOfEarth() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        GeocodeManager.getInstance().get(22, 190, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null) {
                    succeeds.set(0, true);
                }

                signal.countDown();


            }
        });

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testSearchByCountry_shouldReturnHkResultInEnWhenCountryHkLocaleNull() throws Exception {

        SystemClock.sleep(A5SteakTestCaseUtils.GENERAL_DELAY_IN_MS);

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        final String query = "長沙灣政府合署";
        GeocodeManager.getInstance().searchByCountry(query, "HK", null, new GeocodeManager.OnSearchListener() {
            @Override
            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword, String origJsonStr) {

                if (query.equals(keyword) &&
                        !poiPoints.isEmpty() &&
                        "Cheung Sha Wan Government Offices, 303 Cheung Sha Wan Rd, Sham Shui Po, Hong Kong".equals(
                                poiPoints.get(0).getFormattedAddress())) {
                    succeeds.set(0, true);
                }

                signal.countDown();

            }
        });

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }


    public void testSearchByCountry_shouldReturnHkResultInEnWhenCountryHkLocaleEnUs() throws Exception {

        SystemClock.sleep(A5SteakTestCaseUtils.GENERAL_DELAY_IN_MS);

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);


        final String query = "長沙灣政府合署";
        GeocodeManager.getInstance().searchByCountry(query, "HK", Locale.US, new GeocodeManager.OnSearchListener() {
            @Override
            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword, String origJsonStr) {

                if (query.equals(keyword) && !poiPoints.isEmpty() &&
                        "Cheung Sha Wan Government Offices, 303 Cheung Sha Wan Rd, Sham Shui Po, Hong Kong".equals(
                                poiPoints.get(0).getFormattedAddress()))
                    succeeds.set(0, true);

                signal.countDown();

            }
        });

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }


    public void testSearchByCountry_shouldReturnHkResultInTcWhenCountryHkLocaleZhHk() throws Exception {

        SystemClock.sleep(A5SteakTestCaseUtils.GENERAL_DELAY_IN_MS);

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        final String query = "長沙灣政府合署";
        GeocodeManager.getInstance().searchByCountry(query, "HK", new Locale("zh", "HK"), new GeocodeManager.OnSearchListener() {
            @Override
            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword, String origJsonStr) {

                if (query.equals(keyword) && !poiPoints.isEmpty() &&
                        "香港深水埗長沙灣道303號長沙灣政府合署".equals(
                                poiPoints.get(0).getFormattedAddress()))
                    succeeds.set(0, true);

                signal.countDown();

            }
        });

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testSearchByCountryInDetail_shouldReturnHkResultInScWhenCountryHkLocaleZhCn() throws Exception {

        SystemClock.sleep(A5SteakTestCaseUtils.GENERAL_DELAY_IN_MS);

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        final String query = "長沙灣政府合署";
        GeocodeManager.getInstance().searchByCountry(query, "HK", new Locale("zh", "CN"), new GeocodeManager.OnSearchListener() {
            @Override
            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword, String origJsonStr) {

                if (query.equals(keyword) && !poiPoints.isEmpty()
                        && "香港深水埗長沙灣道303號长沙湾政府合署".equals(
                        poiPoints.get(0).getFormattedAddress())
                        && poiPoints.get(0).getLatitude() == 22.3318512
                        && poiPoints.get(0).getLongitude() == 114.1603199
                        )
                    succeeds.set(0, true);

                signal.countDown();


            }
        });
        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);
    }

    public void testSearchByCountry_shouldReturnCnResultInEnWhenCountryCnLocaleEnUs() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        final String query = "天安門";
        GeocodeManager.getInstance().searchByCountry(query, "CN", new Locale("en", "US"), new GeocodeManager.OnSearchListener() {
            @Override
            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword, String origJsonStr) {

                if (query.equals(keyword) &&
                        !poiPoints.isEmpty() &&
                        "Tiananmen, Dongcheng, Beijing, China".equals(
                                poiPoints.get(0).getFormattedAddress()))
                    succeeds.set(0, true);

                signal.countDown();

            }
        });

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);
    }

    public void testSearchByCountry_shouldReturnCnResultInTcWhenCountryCnLocaleZhHk() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        final String query = "天安門";
        GeocodeManager.getInstance().searchByCountry(query, "CN", new Locale("zh", "HK"), new GeocodeManager.OnSearchListener() {
            @Override
            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword, String origJsonStr) {

                if (!query.equals(keyword)) {
                    Log.d("rtemp", "unit_test_failed_t2: query not match");
                    return;
                }

                if (poiPoints.isEmpty()) {
                    Log.d("rtemp", "unit_test_failed_t2: empty result");
                    return;
                }

                if (!"中國北京市東城區天安門".equals(
                        poiPoints.get(0).getFormattedAddress())) {
                    Log.d("rtemp", "unit_test_failed_t2: result address not match");
                    return;
                }

                if (query.equals(keyword) &&
                        !poiPoints.isEmpty() &&
                        "中國北京市東城區天安門".equals(
                                poiPoints.get(0).getFormattedAddress()))
                    succeeds.set(0, true);


                signal.countDown();

            }
        });

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }

    public void testSearchByCountry_shouldReturnCnResultInScWhenCountryCnLocaleZhCn() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        final String query = "天安門";
        GeocodeManager.getInstance().searchByCountry(query, "CN", new Locale("zh", "CN"), new GeocodeManager.OnSearchListener() {
            @Override
            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword, String origJsonStr) {

                if (query.equals(keyword) &&
                        !poiPoints.isEmpty() &&
                        "中国北京市东城区天安门".equals(
                                poiPoints.get(0).getFormattedAddress()))
                    succeeds.set(0, true);

                signal.countDown();

            }
        });

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);
    }

    public void testSearchByBounds_shouldReturnResultInTcWhenBoundsValidLocaleTc() throws Exception {

        SystemClock.sleep(A5SteakTestCaseUtils.GENERAL_DELAY_IN_MS);

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        final String query = "長沙灣政府合署";
        GeocodeManager.getInstance().searchByBounds(query,
                22.1533884, 113.835078, 22.561968, 114.4069561, new Locale("zh", "HK"), new GeocodeManager.OnSearchListener() {
                    @Override
                    public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword, String origJsonStr) {

                        if (query.equals(keyword) &&
                                !poiPoints.isEmpty() &&
                                "香港深水埗長沙灣道303號長沙灣政府合署".equals(
                                        poiPoints.get(0).getFormattedAddress()))
                            succeeds.set(0, true);


                        signal.countDown();
                    }
                }
        );

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);
    }

    public void testSearchByBounds_shouldReturnResultInScWhenBoundsValidLocaleZhCn() throws Exception {

        SystemClock.sleep(A5SteakTestCaseUtils.GENERAL_DELAY_IN_MS);

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        final String query = "長沙灣政府合署";
        GeocodeManager.getInstance().searchByBounds(query,
                22.1533884, 113.835078, 22.561968, 114.4069561, new Locale("zh", "CN"), new GeocodeManager.OnSearchListener() {
                    @Override
                    public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword, String origJsonStr) {


                        if (query.equals(keyword) &&
                                !poiPoints.isEmpty() &&
                                "香港深水埗長沙灣道303號长沙湾政府合署".equals(
                                        poiPoints.get(0).getFormattedAddress()))
                            succeeds.set(0, true);

                        signal.countDown();
                    }
                }
        );

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);
    }

    public void testSearchByBounds_shouldReturnResultInEnWhenBoundsValidLocaleEnUs() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        final String query = "長沙灣政府合署";
        GeocodeManager.getInstance().searchByBounds(query,
                22.1533884, 113.835078, 22.561968, 114.4069561, Locale.US, new GeocodeManager.OnSearchListener() {
                    @Override
                    public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword, String origJsonStr) {

                        if (query.equals(keyword) &&
                                !poiPoints.isEmpty() &&
                                "Cheung Sha Wan Government Offices, 303 Cheung Sha Wan Rd, Sham Shui Po, Hong Kong".equals(
                                        poiPoints.get(0).getFormattedAddress()))
                            succeeds.set(0, true);

                        signal.countDown();
                    }
                }
        );

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);
    }

    public void testSearchByBounds_shouldReturnResultInEnWhenBoundsValidLocaleNull() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        final String query = "長沙灣政府合署";
        GeocodeManager.getInstance().searchByBounds(query,
                22.1533884, 113.835078, 22.561968, 114.4069561, null, new GeocodeManager.OnSearchListener() {
                    @Override
                    public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword, String origJsonStr) {

                        if (query.equals(keyword) &&
                                !poiPoints.isEmpty() &&
                                "Cheung Sha Wan Government Offices, 303 Cheung Sha Wan Rd, Sham Shui Po, Hong Kong".equals(
                                        poiPoints.get(0).getFormattedAddress()))
                            succeeds.set(0, true);

                        signal.countDown();
                    }
                }
        );

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);
    }

    public void testSearchByBounds_shouldReturnEmptyResultWhenBoundsInValidLocaleEnUs() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        final String query = "長沙灣政府合署";
        GeocodeManager.getInstance().searchByBounds(query,
                99, 113.835078, Double.NaN, 114.4069561, Locale.US, new GeocodeManager.OnSearchListener() {
                    @Override
                    public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword, String origJsonStr) {
                        if (!query.equals(keyword))
                            return;

                        if (!poiPoints.isEmpty())
                            return;

                        if (query.equals(keyword) && poiPoints.isEmpty())
                            succeeds.set(0, true);

                        signal.countDown();
                    }
                }
        );

        A5SteakTestCaseUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);
    }


}