package com.tommytao.a5steak.wear;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.tommytao.a5steak.common.Foundation;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * Created by tommytao on 16/12/2015.
 */
public class MessageApiManager extends Foundation
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener, CapabilityApi.CapabilityListener {
    private static MessageApiManager instance = new MessageApiManager();

    public static MessageApiManager getInstance() {
        return instance;
    }

    private MessageApiManager() {
    }

    // --

    private static final String CAPABILITY_NAME = "message_api_manager_capability_name";


    public static interface OnConnectListener {

        public void onConnected(boolean succeed, String errMsg);

    }

    public static interface OnSendListener {

        public void onComplete(boolean succeed);

    }

    public static interface OnSearchNodeIdListener {

        public void onComplete(boolean succeed);

    }

    public static interface OnMessageListener {

        public void onReceive(String message);


    }

    private String lastKnownNodeId = "";

    public String getLastKnownNodeId() {
        return lastKnownNodeId;
    }

    public void searchNodeId(final OnSearchNodeIdListener listener) {


        if (!isConnected()) {

            handler.post(new Runnable() {
                @Override
                public void run() {

                    if (listener != null)
                        listener.onComplete(false);

                }
            });

            return;
        }


        new AsyncTask<Void, Void, CapabilityInfo>() {

            @Override
            protected CapabilityInfo doInBackground(Void... params) {
                CapabilityApi.GetCapabilityResult result =
                        Wearable.CapabilityApi.getCapability(
                                getClient(), CAPABILITY_NAME,
                                CapabilityApi.FILTER_REACHABLE).await();
                return result.getCapability();
            }

            @Override
            protected void onPostExecute(CapabilityInfo result) {
                updateCapabilityInfo(result);

                if (listener != null)
                    listener.onComplete(!TextUtils.isEmpty(getLastKnownNodeId()));
            }

        }.executeOnExecutor(Executors.newCachedThreadPool());


    }

    private void updateCapabilityInfo(CapabilityInfo capabilityInfo) {
        Set<Node> connectedNodes = capabilityInfo.getNodes();

        lastKnownNodeId = pickBestNodeId(connectedNodes);
    }

    private String pickBestNodeId(Set<Node> nodes) {
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby())
                return node.getId();
//            bestNodeId = node.getId();
        }
        return "";
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
        Wearable.CapabilityApi.removeCapabilityListener(getClient(), this, CAPABILITY_NAME);
        lastKnownNodeId = "";

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

        Wearable.MessageApi.addListener(getClient(), this);
        Wearable.CapabilityApi.addCapabilityListener(
                getClient(),
                this,
                CAPABILITY_NAME);

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

    // == OnMessageListener ==

    private ArrayList<OnMessageListener> onMessageListeners = new ArrayList<>();

    private void triggerOnMessageListeners(final String message) {

        final ArrayList<OnMessageListener> pendingListeners = new ArrayList<>(onMessageListeners);

        if (pendingListeners.isEmpty())
            return;

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (OnMessageListener pendingListener : pendingListeners) {
                    if (pendingListener != null) {
                        pendingListener.onReceive(message);
                    }
                }
            }
        });

    }

    public void addOnMessageListener(OnMessageListener listener) {
        onMessageListeners.add(listener);
    }

    public boolean removeOnMessageListener(OnMessageListener listener) {
        return onMessageListeners.remove(listener);
    }


    // == End of OnMessageChangedListener ==

    // --

    public void send(final String message, final OnSendListener listener) {

        if (TextUtils.isEmpty(getLastKnownNodeId())) {

            handler.post(new Runnable() {
                @Override
                public void run() {

                    if (listener != null)
                        listener.onComplete(false);

                }
            });

            return;
        }

        Wearable.MessageApi.sendMessage(getClient(), getLastKnownNodeId(),
                "/" + CAPABILITY_NAME, message.getBytes()).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {

                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                        if (listener != null)
                            listener.onComplete(sendMessageResult.getStatus().isSuccess());

                    }
                }
        );

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (!("/" + CAPABILITY_NAME).equals(messageEvent.getPath())) {
            return;
        }

        String message = new String(messageEvent.getData());

        triggerOnMessageListeners(message);


    }

    @Override
    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {

        updateCapabilityInfo(capabilityInfo);

    }


}
