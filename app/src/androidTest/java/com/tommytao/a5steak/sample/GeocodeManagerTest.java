package com.tommytao.a5steak.sample;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.tommytao.a5steak.util.google.GeocodeManager;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class GeocodeManagerTest extends ApplicationTestCase<Application> {

    public GeocodeManagerTest() {
        super(Application.class);

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        GeocodeManager.getInstance().removeContextB4UnitTest();

        createApplication();
    }
    

//    public void testGet_shouldReturnEnWhenLocaleEnUs() throws Exception {
//
//        final CountDownLatch signal = new CountDownLatch(1);
//
//
//        GeocodeManager.getInstance().get(Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, new Locale("en", "US"), new GeocodeManager.OnGetListener() {
//            @Override
//            public void returnGeocode(GeocodeManager.Geocode geocode) {
//
//                if (geocode == null)
//                    return;
//
//                if (!"25 Yuen Wo Rd, Sha Tin, Hong Kong".equals(geocode.getFormattedAddress()))
//                    return;
//
//                signal.countDown();
//
//            }
//        });
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//
//    }
//
//    public void testGet_shouldReturnTcWhenLocaleZhHk() throws Exception {
//
//        final CountDownLatch signal = new CountDownLatch(1);
//
//
//        GeocodeManager.getInstance().get(Encyclopedia.BEIJING_LAT, Encyclopedia.BEIJING_LNG, new Locale("zh", "HK"), new GeocodeManager.OnGetListener() {
//            @Override
//            public void returnGeocode(GeocodeManager.Geocode geocode) {
//
//                if (geocode == null)
//                    return;
//
//                if (TextUtils.isEmpty(geocode.getFormattedAddress()))
//                    return;
//
//                if (!geocode.getFormattedAddress().startsWith("中國"))
//                    return;
//
//                signal.countDown();
//
//            }
//        });
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//
//    }
//
//    public void testGet_shouldReturnScWhenLocaleZhCn() throws Exception {
//
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        GeocodeManager.getInstance().get(Encyclopedia.BEIJING_LAT, Encyclopedia.BEIJING_LNG, new Locale("zh", "CN"), new GeocodeManager.OnGetListener() {
//            @Override
//            public void returnGeocode(GeocodeManager.Geocode geocode) {
//
//                if (geocode == null)
//                    return;
//
//                if (TextUtils.isEmpty(geocode.getFormattedAddress()))
//                    return;
//
//                if (!geocode.getFormattedAddress().startsWith("中国"))
//                    return;
//
//                signal.countDown();
//
//            }
//        });
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//
//    }
//
//    public void testGet_shouldAssumeAsLocaleUsWhenLocaleIsNull() throws Exception {
//
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        GeocodeManager.getInstance().get(Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, null, new GeocodeManager.OnGetListener() {
//            @Override
//            public void returnGeocode(GeocodeManager.Geocode geocode) {
//
//                if (geocode == null)
//                    return;
//
//                if (!"25 Yuen Wo Rd, Sha Tin, Hong Kong".equals(geocode.getFormattedAddress()))
//                    return;
//
//                signal.countDown();
//
//            }
//        });
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//
//
//    }
//
//    public void testGet_shouldReturnNullWhenOneOfLatIsNaN() throws Exception {
//
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        GeocodeManager.getInstance().get(Double.NaN, Encyclopedia.HKSIL_LNG, null, new GeocodeManager.OnGetListener() {
//            @Override
//            public void returnGeocode(GeocodeManager.Geocode geocode) {
//
//                if (geocode == null)
//                    signal.countDown();
//                else {
//                    // strange
//                }
//
//            }
//        });
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//
//    }
//
//    public void testGet_shouldReturnNullWhenOneOfLngIsNaN() throws Exception {
//
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        GeocodeManager.getInstance().get(Encyclopedia.HKSIL_LAT, Double.NaN, null, new GeocodeManager.OnGetListener() {
//            @Override
//            public void returnGeocode(GeocodeManager.Geocode geocode) {
//
//                if (geocode == null)
//                    signal.countDown();
//                else {
//                    // strange
//                }
//
//            }
//        });
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//
//    }
//
//    public void testGet_shouldReturnNullWhenBothLatLngIsNaN() throws Exception {
//
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        GeocodeManager.getInstance().get(Double.NaN, Double.NaN, null, new GeocodeManager.OnGetListener() {
//            @Override
//            public void returnGeocode(GeocodeManager.Geocode geocode) {
//
//                if (geocode == null)
//                    signal.countDown();
//                else {
//                    // strange
//                }
//
//            }
//        });
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//
//    }
//
//    public void testGet_shouldReturnNullWhenLatIsOutOfEarth() throws Exception {
//
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        GeocodeManager.getInstance().get(91, 144, null, new GeocodeManager.OnGetListener() {
//            @Override
//            public void returnGeocode(GeocodeManager.Geocode geocode) {
//
//                if (geocode == null)
//                    signal.countDown();
//                else {
//                    // strange
//                }
//
//            }
//        });
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//
//    }
//
//    public void testGet_shouldReturnNullWhenLngIsOutOfEarth() throws Exception {
//
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        GeocodeManager.getInstance().get(22, 190, null, new GeocodeManager.OnGetListener() {
//            @Override
//            public void returnGeocode(GeocodeManager.Geocode geocode) {
//
//                if (geocode == null)
//                    signal.countDown();
//                else {
//                    // strange
//                }
//
//            }
//        });
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//
//    }
//
//    public void testSearchByCountry_shouldReturnHkResultInEnWhenCountryHkLocaleNull() throws Exception {
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        final String query = "長沙灣政府合署";
//        GeocodeManager.getInstance().searchByCountry(query, "HK", null, new GeocodeManager.OnSearchListener() {
//            @Override
//            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
//
//                if (!query.equals(keyword))
//                    return;
//
//                if (poiPoints.isEmpty())
//                    return;
//
//                if (!"Cheung Sha Wan Government Offices, 303 Cheung Sha Wan Rd, Sham Shui Po, Hong Kong".equals(
//                        poiPoints.get(0).getFormattedAddress()))
//                    return;
//
//                signal.countDown();
//
//            }
//        });
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//    }
//

// ===

//    public void testSearchByCountry_shouldReturnHkResultInEnWhenCountryHkLocaleEnUs() throws Exception {
//        final CountDownLatch signal = new CountDownLatch(1);
//        final ArrayList<Boolean> succeeds = new ArrayList<>();
//        succeeds.add(false);
//
//        final ArrayList<String> errMsgs = new ArrayList<>();
//        errMsgs.add("");
//
//        final String query = "長沙灣政府合署";
//        GeocodeManager.getInstance().searchByCountry(query, "HK", Locale.US, new GeocodeManager.OnSearchListener() {
//            @Override
//            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword, String origJsonStr) {
//
//                if (!query.equals(keyword)) {
//                    errMsgs.set(0, "Result from other keyword, other keyword: " + keyword + ", orig query: " + query);
//                    signal.countDown();
//                    return;
//                }
//
//                if (poiPoints.isEmpty()) {
//                    errMsgs.set(0, "poiPoints is empty: origJsonStr: " + origJsonStr);
//                    signal.countDown();
//                    return;
//                }
//
//                if (!"Cheung Sha Wan Government Offices, 303 Cheung Sha Wan Rd, Sham Shui Po, Hong Kong".equals(
//                        poiPoints.get(0).getFormattedAddress())) {
//                    errMsgs.set(0, "Strange formatted address: " + poiPoints.get(0).getFormattedAddress());
//                    signal.countDown();
//                    return;
//                }
//
//                succeeds.set(0, true);
//
//                signal.countDown();
//
//            }
//        });
//
//        assertTrue("Timeout occurs", signal.await(Foundation.DEFAULT_CONNECT_READ_TIMEOUT_IN_MS, TimeUnit.MILLISECONDS));
//
//        assertTrue("Unexpected result: " + errMsgs.get(0), succeeds.get(0));
//
//    }

    // ===

//
//    public void testSearchByCountry_shouldReturnHkResultInTcWhenCountryHkLocaleZhHk() throws Exception {
//
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        final String query = "長沙灣政府合署";
//        GeocodeManager.getInstance().searchByCountry(query, "HK", new Locale("zh", "HK"), new GeocodeManager.OnSearchListener() {
//            @Override
//            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
//
//                if (!query.equals(keyword))
//                    return;
//
//                if (poiPoints.isEmpty())
//                    return;
//
//                if (!"香港深水埗長沙灣道303號長沙灣政府合署".equals(
//                        poiPoints.get(0).getFormattedAddress()))
//                    return;
//
//                signal.countDown();
//
//            }
//        });
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//
//    }
//
//    public void testSearchByCountry_shouldReturnHkResultInScWhenCountryHkLocaleZhCn() throws Exception {
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        final String query = "長沙灣政府合署";
//        GeocodeManager.getInstance().searchByCountry(query, "HK", new Locale("zh", "CN"), new GeocodeManager.OnSearchListener() {
//            @Override
//            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
//
//                if (!query.equals(keyword)) {
//                    Log.d("rtemp", "unit_test_failed_t: query not match");
//                    return;
//                }
//
//                if (poiPoints.isEmpty()) {
//                    Log.d("rtemp", "unit_test_failed_t: empty result");
//                    return;
//                }
//
//                if (!"香港深水埗長沙灣道303號长沙湾政府合署".equals(
//                        poiPoints.get(0).getFormattedAddress())) {
//                    Log.d("rtemp", "unit_test_failed_t: result address not match");
//                    return;
//                }
//
//                Log.d("rtemp", "unit_test_failed_t: SUCCEED");
//
//                signal.countDown();
//
//
//            }
//        });
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//    }
//
//    public void testSearchByCountry_shouldReturnCnResultInEnWhenCountryCnLocaleEnUs() throws Exception {
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        final String query = "天安門";
//        GeocodeManager.getInstance().searchByCountry(query, "CN", new Locale("en", "US"), new GeocodeManager.OnSearchListener() {
//            @Override
//            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
//
//                if (!query.equals(keyword))
//                    return;
//
//                if (poiPoints.isEmpty())
//                    return;
//
//                if (!"Tiananmen, Dongcheng, Beijing, China".equals(
//                        poiPoints.get(0).getFormattedAddress()))
//                    return;
//
//                signal.countDown();
//
//            }
//        });
//
//        assertTrue(signal.await(Foundation.DEFAULT_CONNECT_READ_TIMEOUT_IN_MS, TimeUnit.MILLISECONDS));
//    }
//
//    public void testSearchByCountry_shouldReturnCnResultInTcWhenCountryCnLocaleZhHk() throws Exception {
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        final String query = "天安門";
//        GeocodeManager.getInstance().searchByCountry(query, "CN", new Locale("zh", "HK"), new GeocodeManager.OnSearchListener() {
//            @Override
//            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
//
//                if (!query.equals(keyword)) {
//                    Log.d("rtemp", "unit_test_failed_t2: query not match");
//                    return;
//                }
//
//                if (poiPoints.isEmpty()) {
//                    Log.d("rtemp", "unit_test_failed_t2: empty result");
//                    return;
//                }
//
//                if (!"中國北京市東城區天安門".equals(
//                        poiPoints.get(0).getFormattedAddress())) {
//                    Log.d("rtemp", "unit_test_failed_t2: result address not match");
//                    return;
//                }
//
//                Log.d("rtemp", "unit_test_failed_t2: DONE");
//
//                signal.countDown();
//
//            }
//        });
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//
//    }
//
//    public void testSearchByCountry_shouldReturnCnResultInScWhenCountryCnLocaleZhCn() throws Exception {
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        final String query = "天安門";
//        GeocodeManager.getInstance().searchByCountry(query, "CN", new Locale("zh", "CN"), new GeocodeManager.OnSearchListener() {
//            @Override
//            public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
//
//                if (!query.equals(keyword))
//                    return;
//
//                if (poiPoints.isEmpty())
//                    return;
//
//                if (!"中国北京市东城区天安门".equals(
//                        poiPoints.get(0).getFormattedAddress()))
//                    return;
//
//                signal.countDown();
//
//            }
//        });
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//    }
//
//    public void testSearchByBounds_shouldReturnResultInTcWhenBoundsValidLocaleTc() throws Exception {
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        final String query = "長沙灣政府合署";
//
//        GeocodeManager.getInstance().searchByBounds(query,
//                22.1533884, 113.835078, 22.561968, 114.4069561, new Locale("zh", "HK"), new GeocodeManager.OnSearchListener() {
//                    @Override
//                    public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
//                        if (!query.equals(keyword))
//                            return;
//
//                        if (poiPoints.isEmpty())
//                            return;
//
//                        if (!"香港深水埗長沙灣道303號長沙灣政府合署".equals(
//                                poiPoints.get(0).getFormattedAddress()))
//                            return;
//
//                        signal.countDown();
//                    }
//                }
//        );
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//    }
//
//    public void testSearchByBounds_shouldReturnResultInScWhenBoundsValidLocaleZhCn() throws Exception {
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        final String query = "長沙灣政府合署";
//
//        GeocodeManager.getInstance().searchByBounds(query,
//                22.1533884, 113.835078, 22.561968, 114.4069561, new Locale("zh", "CN"), new GeocodeManager.OnSearchListener() {
//                    @Override
//                    public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
//                        if (!query.equals(keyword))
//                            return;
//
//                        if (poiPoints.isEmpty())
//                            return;
//
//                        if (!"香港深水埗長沙灣道303號长沙湾政府合署".equals(
//                                poiPoints.get(0).getFormattedAddress()))
//                            return;
//
//                        signal.countDown();
//                    }
//                }
//        );
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//    }
//
//    public void testSearchByBounds_shouldReturnResultInEnWhenBoundsValidLocaleEnUs() throws Exception {
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        final String query = "長沙灣政府合署";
//
//        GeocodeManager.getInstance().searchByBounds(query,
//                22.1533884, 113.835078, 22.561968, 114.4069561, Locale.US, new GeocodeManager.OnSearchListener() {
//                    @Override
//                    public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
//                        if (!query.equals(keyword))
//                            return;
//
//                        if (poiPoints.isEmpty())
//                            return;
//
//                        if (!"Cheung Sha Wan Government Offices, 303 Cheung Sha Wan Rd, Sham Shui Po, Hong Kong".equals(
//                                poiPoints.get(0).getFormattedAddress()))
//                            return;
//
//                        signal.countDown();
//                    }
//                }
//        );
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//    }
//
//    public void testSearchByBounds_shouldReturnResultInEnWhenBoundsValidLocaleNull() throws Exception {
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        final String query = "長沙灣政府合署";
//
//        GeocodeManager.getInstance().searchByBounds(query,
//                22.1533884, 113.835078, 22.561968, 114.4069561, null, new GeocodeManager.OnSearchListener() {
//                    @Override
//                    public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
//                        if (!query.equals(keyword))
//                            return;
//
//                        if (poiPoints.isEmpty())
//                            return;
//
//                        if (!"Cheung Sha Wan Government Offices, 303 Cheung Sha Wan Rd, Sham Shui Po, Hong Kong".equals(
//                                poiPoints.get(0).getFormattedAddress()))
//                            return;
//
//                        signal.countDown();
//                    }
//                }
//        );
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//    }
//
//    public void testSearchByBounds_shouldReturnEmptyResultWhenBoundsInValidLocaleEnUs() throws Exception {
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        final String query = "長沙灣政府合署";
//
//        GeocodeManager.getInstance().searchByBounds(query,
//                99, 113.835078, Double.NaN, 114.4069561, Locale.US, new GeocodeManager.OnSearchListener() {
//                    @Override
//                    public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
//                        if (!query.equals(keyword))
//                            return;
//
//                        if (!poiPoints.isEmpty())
//                            return;
//
//                        signal.countDown();
//                    }
//                }
//        );
//
//        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));
//    }

   




}