package com.tommytao.a5steak.util.google;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Responsible to vision operations, e.g. recognize face, smile, barcode, etc.
 */
public class VisionGApiAnalyzer extends GFoundation {

    private static VisionGApiAnalyzer instance;

    public static VisionGApiAnalyzer getInstance() {

        if (instance == null)
            instance = new VisionGApiAnalyzer();

        return instance;
    }

    private VisionGApiAnalyzer() {
        // do nothing
    }

    // --

    @Override
    public boolean init(Context context) {
        return super.init(appContext);
    }

    @Override
    protected GoogleApiClient getClient() {
        // TODO under construction
        return super.getClient();
    }

    @Override
    public void connect(final OnConnectListener onConnectListener) {
        super.connect(onConnectListener);
    }
    @Override
    public void disconnect() {
        super.disconnect();
    }
    @Override
    public boolean isConnecting() {
       return super.isConnecting();
    }
    @Override
    public boolean isConnected() {
        return super.isConnected();
    }




}
