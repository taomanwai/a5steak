package com.tommytao.a5steak.wear;

import android.content.Context;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.tommytao.a5steak.common.Foundation;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by tommytao on 16/12/2015.
 */
public class MessageApiManager extends Foundation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private static MessageApiManager instance = new MessageApiManager();

    public static MessageApiManager getInstance() {
        return instance;
    }

    private MessageApiManager() {
    }

    // --

    private static final String CAPABILITY_NAME = "message_api_manager_transcription";

    public static interface OnConnectListener {

        public void onConnected(boolean succeed, String errMsg);

    }

    private String transcriptionNodeId = "";

    public String getTranscriptionNodeId() {
        return transcriptionNodeId;
    }

    public void refreshTranscriptionNodeId() {

        transcriptionNodeId = "";

        if (!isConnected())
            return;

        Wearable.CapabilityApi.addCapabilityListener(
                getClient(),
                new CapabilityApi.CapabilityListener() {
                    @Override
                    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
                        updateTranscriptionCapability(capabilityInfo);
                    }
                },
                CAPABILITY_NAME);
    }

    private void updateTranscriptionCapability(CapabilityInfo capabilityInfo) {
        Set<Node> connectedNodes = capabilityInfo.getNodes();

        transcriptionNodeId = pickBestNodeId(connectedNodes);
    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }


    // == onConnectListener ==

    private ArrayList<OnConnectListener> onConnectListeners = new ArrayList<>();

    private void clearAndTriggerOnConnectListeners(boolean succeed, final String errMsg) {

        ArrayList<OnConnectListener> pendingListeners = new ArrayList<>(onConnectListeners);
        onConnectListeners.clear();
        for (OnConnectListener pendingListener : pendingListeners) {
            if (pendingListener != null) {
                pendingListener.onConnected(succeed, errMsg);
            }
        }

    }

    private void clearAndOnUiThreadTriggerOnConnectListeners(final boolean succeed, final String errMsg) {

        final ArrayList<OnConnectListener> pendingListeners = new ArrayList<>(onConnectListeners);
        onConnectListeners.clear();
        if (pendingListeners.isEmpty())
            return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (OnConnectListener pendingListener : pendingListeners) {
                    if (pendingListener != null) {
                        pendingListener.onConnected(succeed, errMsg);
                    }
                }
            }
        });

    }

    // == END of onConnectListener ==


    @Override
    public boolean init(Context context) {
        return super.init(context);
    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }

    @Override
    public boolean isInitialized() {
        return super.isInitialized();
    }

    private GoogleApiClient client;

    protected GoogleApiClient getClient() {

        if (client == null)
            this.client = new GoogleApiClient.Builder(appContext).addApi(Wearable.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

        return client;

    }

    private boolean connected;

    @Deprecated
    protected void connect() {
        super.connect();
    }

    public void connect(final OnConnectListener onConnectListener) {

        if (isConnected()) {
            if (onConnectListener != null)
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onConnectListener.onConnected(true, "");
                    }
                });

            return;
        }

        if (!isConnecting())
            this.getClient().connect();

        onConnectListeners.add(onConnectListener);

    }

    @Override
    public void disconnect() {

        if (!isConnected() && !isConnecting())
            return;


        getClient().disconnect();

        connected = false;
        clearAndOnUiThreadTriggerOnConnectListeners(false, "Forced disconnect outside " + MessageApiManager.class.getSimpleName());

    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isConnecting() {

        if (isConnected())
            return false;

        return !onConnectListeners.isEmpty();

    }

    @Override
    public void onConnected(Bundle bundle) {
        connected = true;

        clearAndTriggerOnConnectListeners(true, "");

    }

    @Override
    public void onConnectionSuspended(int cause) {
        getClient().connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        clearAndTriggerOnConnectListeners(false, connectionResult.getErrorMessage());

    }

    // --












}
