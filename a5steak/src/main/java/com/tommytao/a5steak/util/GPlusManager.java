package com.tommytao.a5steak.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Responsible for Facebook operations
 * <p/>
 * Ref:
 * https://developers.facebook.com/docs/sharing/android
 */
public class GPlusManager extends Foundation implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static GPlusManager instance;

    public static GPlusManager getInstance() {

        if (instance == null)
            instance = new GPlusManager();

        return instance;
    }

    private GPlusManager() {

    }


    // --

    public static interface OnConnectListener {

        public void onConnected();

        public void onFailed(int errorCode);

    }

    private static interface OnStartResolutionListener {

        public void onCompleted(Intent data);

    }

    public static class GPlusStartResolutionActivity extends Activity {

        private OnStartResolutionListener listener;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Intent intent = getIntent();

            ConnectionResult connectionResult = intent.getParcelableExtra("connectionResult");
            int id = intent.getIntExtra("idOfStartResolutionListener", -1);
            listener = GPlusManager.getInstance().onStartResolutionListeners.get(id);
            GPlusManager.getInstance().onStartResolutionListeners.remove(id);

            try {
                connectionResult.startResolutionForResult(this, 0);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            finish();

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onCompleted(data);
                }
            });


        }

    }

//    try {
//        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
//            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
//            String personName = currentPerson.getDisplayName();
//            String personEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
//            int gender = currentPerson.getGender();
//            String plusId = currentPerson.getId();
//            String personPhotoUrl = currentPerson.getImage().getUrl();
//
//            String content = "PersonName:"+personName+"\n";
//            content += "PersonEmail:"+personEmail+"\n";
//            content += "Gender:"+gender+"\n";
//            content += "PlusId:"+plusId;
//
//            Toast.makeText(MainActivity.this, "onConnected: " + content, Toast.LENGTH_LONG).show();
//
//        }
//    } catch (Exception e) {
//        e.printStackTrace();
//    }

    public static class Person {

        public static final int GENDER_MALE = 0;
        public static final int GENDER_FEMALE = 1;
        public static final int GENDER_UNCLASSIFIED = 2;

        private String id = "";
        private String name = "";
        private int gender = GENDER_UNCLASSIFIED;
        private String email = "";
        private String profileImageLink = "";

        public Person(String id, String name, int gender, String email, String profileImageLink) {
            this.id = id;
            this.name = name;
            this.gender = gender;
            this.email = email;
            this.profileImageLink = profileImageLink;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getGender() {
            return gender;
        }

        public String getEmail() {
            return email;
        }

        public String getProfileImageLink() {
            return profileImageLink;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", gender=" + gender +
                    ", email='" + email + '\'' +
                    ", profileImageLink='" + profileImageLink + '\'' +
                    '}';
        }
    }

    private HashMap<Integer, OnStartResolutionListener> onStartResolutionListeners = new HashMap<>();

    private boolean connected;

    private ArrayList<OnConnectListener> onConnectListeners = new ArrayList<>();

    private GoogleApiClient client;

    public GoogleApiClient getClient() {

        if (client == null) {
            client = new GoogleApiClient.Builder(appContext).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();
        }

        return client;
    }

    private void clearAndTriggerOnConnectListeners(boolean succeed, int errorCode) {

        ArrayList<OnConnectListener> pendingOnConnectListeners = new ArrayList<>(onConnectListeners);

        onConnectListeners.clear();

        for (OnConnectListener pendingOnConnectListener : pendingOnConnectListeners) {
            if (pendingOnConnectListener != null) {
                if (succeed) {
                    pendingOnConnectListener.onConnected();
                } else {
                    pendingOnConnectListener.onFailed(errorCode);
                }
            }
        }
    }

    @Override
    public boolean init(Context context) {
        if (!super.init(context))
            return false;

        return true;

    }

    public boolean isConnecting() {

        if (isConnected())
            return false;

        return !onConnectListeners.isEmpty();
    }

    public boolean isConnected() {
        return connected;
    }


    public void disconnect() {

        getClient().disconnect();
        connected = false;
        clearAndTriggerOnConnectListeners(false, -1);

    }

    private void startResolution(ConnectionResult connectionResult, OnStartResolutionListener listener) {

        int id = genUniqueId();
        onStartResolutionListeners.put(id, listener);

        appContext.startActivity(new Intent(appContext, GPlusStartResolutionActivity.class).putExtra("connectionResult", connectionResult).putExtra("idOfStartResolutionListener", id).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }

    public void connect(final OnConnectListener onConnectListener) {

        if (isConnected()) {

            if (onConnectListener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onConnectListener.onConnected();
                    }
                });
            }

            return;
        }

        if (isConnecting()) {
            onConnectListeners.add(onConnectListener);
            return;
        }

        onConnectListeners.add(onConnectListener);

        getClient().connect();

    }

    @Override
    public void onConnected(Bundle bundle) {

        // coz onConnected will be run in async style. Ref: https://developers.google.com/android/reference/com/google/android/gms/common/api/GoogleApiClient.ConnectionCallbacks

        handler.post(new Runnable() {
            @Override
            public void run() {
                connected = true;
                clearAndTriggerOnConnectListeners(true, -1);
            }
        });
    }

    @Override
    public void onConnectionSuspended(int cause) {

        getClient().connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        int errorCode = connectionResult.getErrorCode();


        if (errorCode == 4) { // 4 = SIGN_IN_REQUIRED
            startResolution(connectionResult, new OnStartResolutionListener() {
                @Override
                public void onCompleted(Intent data) {

                    if (getClient().isConnected()) {
                        clearAndTriggerOnConnectListeners(true, -1);
                        return;
                    }


                    if (getClient().isConnecting()) {
                        // do nothing, waiting connection done
                        return;
                    }

                    if (!getClient().isConnected()) {
                        client.connect();
                        return;
                    }


                }
            });
            return;
        }

        clearAndTriggerOnConnectListeners(false, connectionResult.getErrorCode());

    }

    public Person getCurrentPerson() {

        if (!isConnected()) {
            return null;
        }

        Person person = null;
        String id = "";
        String name = "";
        int gender = Person.GENDER_UNCLASSIFIED;
        String email = "";
        String profileImageLink = "";

        try {

            com.google.android.gms.plus.model.people.Person currentPerson = Plus.PeopleApi.getCurrentPerson(getClient());
            id = currentPerson.getId();
            name = currentPerson.getDisplayName();
            gender = currentPerson.getGender();
            email = Plus.AccountApi.getAccountName(getClient());
            profileImageLink = currentPerson.getImage().getUrl();

            if (id==null)
                id = "";
            if (name==null)
                name = "";
            if (email==null)
                email = "";
            if (profileImageLink==null)
                profileImageLink = "";

            person = new Person(id, name, gender, email, profileImageLink);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return person;


    }


}
