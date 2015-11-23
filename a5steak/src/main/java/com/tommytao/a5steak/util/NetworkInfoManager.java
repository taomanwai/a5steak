package com.tommytao.a5steak.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.android.volley.RequestQueue;

import org.json.JSONObject;

/**
 * Responsible for getting network info (e.g. connectivity, etc.)
 *
 * @author tommytao
 */
public class NetworkInfoManager extends Foundation {

    private static NetworkInfoManager instance;

    private ConnectivityManager connectivityManager;

    private NetworkInfo networkInfo;

    private NetworkInfoManager() {

        super();

        log("network_info_manager: " + "create");

    }

    public static NetworkInfoManager getInstance() {
        if (instance == null)
            instance = new NetworkInfoManager();

        return instance;

    }

    // --

    public static final int DEFAULT_MAX_NUM_OF_RETRIES = 10;

    @Deprecated
    public boolean init(Context context) {
        return super.init(context);
    }

    @Override
    public boolean init(Context context, RequestQueue requestQueue) {
        if (!super.init(context, requestQueue)) {

            log("network_info_manager: " + "init REJECTED: already initialized");

            return false;

        }

        log("network_info_manager: " + "init");

        return true;
    }

    public static final String GOOGLE_LINK = "http://www.google.com";

    public static final String YAHOO_LINK = "http://www.yahoo.com";

    public static interface IsLinkAccessibleInStrListener {

        public void onComplete(boolean accessible, String str);

    }

    public static interface IsLinkAccessibleInJSONListener {

        public void onComplete(boolean accessible, JSONObject jObj);

    }

    public static interface IsLinkAccessibleInByteArrayListener {

        public void onComplete(boolean accessible, byte[] ba);

    }


    private ConnectivityManager getConnectivityManager() {
        if (connectivityManager == null)
            connectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager;
    }

    private void refreshNetworkInfo() {

        networkInfo = getConnectivityManager().getActiveNetworkInfo();

    }

    public String getTypeName() {

        refreshNetworkInfo();

        if (this.networkInfo == null)
            return "";

        return networkInfo.getTypeName();

    }

    public String getDetailedState() {

        refreshNetworkInfo();

        if (this.networkInfo == null)
            return NetworkInfo.DetailedState.DISCONNECTED.name();

        return networkInfo.getDetailedState().name();

    }

    public boolean isAvailable() {

        refreshNetworkInfo();

        if (this.networkInfo == null)
            return false;

        return networkInfo.isAvailable();
    }

    private boolean isConnectedAsTypeOf(String type) {
        refreshNetworkInfo();

        if (this.networkInfo == null)
            return false;

        if (!networkInfo.isConnected())
            return false;

        return (networkInfo.getTypeName().equals(type));

    }

    public boolean isConnectedWifi() {

        return isConnectedAsTypeOf("WIFI");

    }

    public boolean isConnectedMobile() {

        return isConnectedAsTypeOf("MOBILE");

    }

    public boolean isConnected() {

        refreshNetworkInfo();

        if (this.networkInfo == null)
            return false;

        return networkInfo.isConnected();
    }


    public void isLinkAccessibleInJSON(String link, final IsLinkAccessibleInJSONListener listener) {

        if (listener == null)
            return;

        if (!this.isConnected()) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onComplete(false, null);
                }
            });

        }

        httpGetJSON(link, DEFAULT_MAX_NUM_OF_RETRIES, new OnHttpGetJSONListener() {
            @Override
            public void onComplete(JSONObject response) {
                listener.onComplete(response != null, response);
            }
        });

    }

    public void isLinkAccessibleInByteArray(String link, final IsLinkAccessibleInByteArrayListener listener) {

        if (listener == null)
            return;

        if (!this.isConnected()) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onComplete(false, new byte[0]);
                }
            });

        }

        httpGetByteArray(link, DEFAULT_MAX_NUM_OF_RETRIES, new OnHttpGetByteArrayListener() {
            @Override
            public void onDownloaded(byte[] ba) {

                listener.onComplete(ba.length > 0, ba);

            }

            @Override
            public void onDownloading(int percentage) {

            }
        });


    }


    /**
     * Check if link accessible
     *
     * @param link     String of link
     * @param listener Listener which is used to return result
     */
    public void isLinkAccessibleInStr(String link, final IsLinkAccessibleInStrListener listener) {

        if (listener == null)
            return;

        if (!this.isConnected()) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onComplete(false, "");
                }
            });

        }

        httpGetString(link, DEFAULT_MAX_NUM_OF_RETRIES, new OnHttpGetStringListener() {
            @Override
            public void onComplete(String str) {

                listener.onComplete(!TextUtils.isEmpty(str), str);
            }
        });

    }

    public boolean isConnectedOrConnecting() {

        refreshNetworkInfo();

        if (this.networkInfo == null)
            return false;

        return networkInfo.isConnectedOrConnecting();
    }

    public boolean isConnecting() {

        refreshNetworkInfo();

        if (this.networkInfo == null)
            return false;

        return (networkInfo.isConnectedOrConnecting() && !networkInfo.isConnected());
    }

    public boolean isFailover() {

        refreshNetworkInfo();

        if (this.networkInfo == null)
            return false;

        return networkInfo.isFailover();
    }

    public boolean isRoaming() {

        refreshNetworkInfo();

        if (this.networkInfo == null)
            return false;

        return networkInfo.isRoaming();
    }

}
