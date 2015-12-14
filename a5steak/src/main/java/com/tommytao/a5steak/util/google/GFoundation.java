package com.tommytao.a5steak.util.google;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.tommytao.a5steak.util.Foundation;

/**
 * Created by tommytao on 1/9/15.
 */
public class GFoundation extends Foundation {

    // == GPlayManager ==
    protected boolean isGPlayExistAndUpToDate() {
        return (GooglePlayServicesUtil.isGooglePlayServicesAvailable(appContext) == ConnectionResult.SUCCESS);
    }

    protected boolean isGPlayExist() {
        return (isGPlayExistAndUpToDate() || GooglePlayServicesUtil.isGooglePlayServicesAvailable(appContext) == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED);
    }



}
