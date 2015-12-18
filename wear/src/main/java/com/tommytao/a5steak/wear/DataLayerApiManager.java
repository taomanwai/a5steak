package com.tommytao.a5steak.wear;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.tommytao.a5steak.common.Foundation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by tommytao on 16/12/2015.
 */
public class DataLayerApiManager extends Foundation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener {
    private static DataLayerApiManager instance = new DataLayerApiManager();

    public static DataLayerApiManager getInstance() {
        return instance;
    }

    private DataLayerApiManager() {
    }


    // --

    public static interface OnPutListener {

        public void onComplete(boolean succeed);

    }

    public static interface OnDataListener {

        public void onChanged(String path, HashMap<String, String> data);

        public void onDeleted(String path, HashMap<String, String> data);

    }


    public static interface OnConnectListener {

        public void onConnected(boolean succeed);

    }

    // == OnDataListener ==

    private ArrayList<OnDataListener> onDataListeners = new ArrayList<>();



    /**
     * Trigger current listeners on UI thread
     *
     * @param path             Path of data item
     * @param data             Data of data item in HashMap format
     * @param changedOrDeleted TRUE=changed; FALSE=deleted
     */
    private void triggerOnDataListeners(final String path, final HashMap<String, String> data, final boolean changedOrDeleted) {

        final ArrayList<OnDataListener> pendingListeners = new ArrayList<>(onDataListeners);
//        onDataListeners.clear();

        if (pendingListeners.isEmpty())
            return;

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (OnDataListener pendingListener : pendingListeners) {
                    if (pendingListener != null) {
                        if (changedOrDeleted)
                            pendingListener.onChanged(path, data);
                        else
                            pendingListener.onDeleted(path, data);
                    }
                }
            }
        });

    }

    public void addOnReceiveListener(OnDataListener listener) {
        onDataListeners.add(listener);
    }

    public boolean removeOnReceiveListener(OnDataListener listener) {
        return onDataListeners.remove(listener);
    }


    // == End of OnDataChangedListener ==


    // == onConnectListener ==

    private ArrayList<OnConnectListener> onConnectListeners = new ArrayList<>();

    private void clearAndTriggerOnConnectListeners(boolean succeed) {

        ArrayList<OnConnectListener> pendingListeners = new ArrayList<>(onConnectListeners);
        onConnectListeners.clear();
        for (OnConnectListener pendingListener : pendingListeners) {
            if (pendingListener != null) {
                pendingListener.onConnected(succeed);
            }
        }

    }

    private void clearAndOnUiThreadTriggerOnConnectListeners(final boolean succeed) {

        final ArrayList<OnConnectListener> pendingListeners = new ArrayList<>(onConnectListeners);
        onConnectListeners.clear();
        if (pendingListeners.isEmpty())
            return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (OnConnectListener pendingListener : pendingListeners) {
                    if (pendingListener != null) {
                        pendingListener.onConnected(succeed);
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
                        onConnectListener.onConnected(true);
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

        Wearable.DataApi.removeListener(getClient(), this);

        getClient().disconnect();

        connected = false;
        clearAndOnUiThreadTriggerOnConnectListeners(false);

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
        Wearable.DataApi.addListener(getClient(), this);
        clearAndTriggerOnConnectListeners(true);


    }

    @Override
    public void onConnectionSuspended(int cause) {
        getClient().connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d("rtemp", "conn_e_t: " + connectionResult.getErrorMessage());

        clearAndTriggerOnConnectListeners(false);
    }

    public DataMap hashMapToDataMap(HashMap<String, String> hashMap) {

        DataMap result = new DataMap();

        if (hashMap == null)
            return result;


        Set<String> keys = hashMap.keySet();

        for (String key : keys) {
            result.putString(key, hashMap.get(key));
        }

        return result;

    }

    public HashMap<String, String> dataMapToHashMap(DataMap dataMap) {

        HashMap<String, String> result = new HashMap<>();

        if (dataMap == null)
            return result;

        Set<String> keys = dataMap.keySet();

        for (String key : keys) {
            result.put(key, dataMap.getString(key, ""));
        }

        return result;

    }


    public void put(String path, HashMap<String, String> payload, final OnPutListener listener) {

        if (!isConnected()){

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onComplete(false);
                }
            });

            return;
        }

        final PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path).setUrgent();

        putDataMapRequest.getDataMap().putAll(hashMapToDataMap(payload));

        Wearable.DataApi.putDataItem(getClient(), putDataMapRequest.asPutDataRequest()).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult result) {

                if (listener != null)
                    listener.onComplete(result.getStatus().isSuccess());

            }
        });


    }

    public void remove(String path){
        Wearable.DataApi.deleteDataItems(getClient(), Uri.parse(path));
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

//        ArrayList<DataEvent> events = new ArrayList<>();
//
//        for (DataEvent event : dataEventBuffer) {
//            events.add(event);
//        }
//
//        clearAndTriggerOnDataChangedListeners(events);

        DataItem dataItem = null;
        DataMap dataMap = null;
        for (DataEvent event : dataEventBuffer) {

            dataItem = event.getDataItem();

            dataMap = null;
            try {
                dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (dataItem == null)
                continue;

            switch (event.getType()) {

                case DataEvent.TYPE_CHANGED:
                    triggerOnDataListeners(event.getDataItem().getUri().getPath(), dataMapToHashMap(dataMap), true);
                    break;


                case DataEvent.TYPE_DELETED:
                    triggerOnDataListeners(event.getDataItem().getUri().getPath(), dataMapToHashMap(dataMap), false);
                    break;

                default:
                    // do nothing
                    break;

            }

        }


    }


}
