package com.tommytao.a5steak.util.google.gapiclient;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.tommytao.a5steak.util.google.GFoundation;

import java.util.ArrayList;

/**
 * Responsible for Facebook operations
 * <p/>
 * Ref:
 * https://developers.facebook.com/docs/sharing/android
 */
public class GPlusManager extends GFoundation implements GoogleApiClient.ConnectionCallbacks,
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

        public void onCompleted(boolean succeed);

    }

    private static interface OnUserRecoverableAuthListener {

        public void onCompleted(String token);

    }

    public static interface OnGetLastKnownTokenListener {

        public void onCompleted(String token);

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
        protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            finish();

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onCompleted(resultCode == RESULT_OK);
                }
            });


        }

    }

    public static class GPlusUserRecoverableAuthActivity extends Activity {

        public static int REQ_USER_RECOVERABLE_AUTH = 9383;

        private OnUserRecoverableAuthListener listener;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Intent intent = getIntent();

            Intent userRecoverableAuthIntent = intent.getParcelableExtra("userRecoverableAuthIntent");

            int idOfUserRecoverableAuthListener = intent.getIntExtra("idOfUserRecoverableAuthListener", -1);
            listener = GPlusManager.getInstance().onUserRecoverableAuthListeners.get(idOfUserRecoverableAuthListener);
            GPlusManager.getInstance().onUserRecoverableAuthListeners.remove(idOfUserRecoverableAuthListener);

            startActivityForResult(userRecoverableAuthIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), REQ_USER_RECOVERABLE_AUTH);

        }

        @Override
        protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode != REQ_USER_RECOVERABLE_AUTH) {
                return;
            }

            if (resultCode != RESULT_OK) {
                finish();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onCompleted("");
                        }
                    }
                });
                return;
            }

            finish();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {

                        String token = "";

                        try {
                            token = data.getExtras().getString("authtoken");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        listener.onCompleted(token);
                    }
                }
            });

        }

    }


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

        public String getProfileImageLinkForSize(int size) {
            String result = profileImageLink;

            try {

                result = profileImageLink.substring(0, profileImageLink.length() - 2) + size;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
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

    private SparseArray<OnStartResolutionListener> onStartResolutionListeners = new SparseArray<>();
    private SparseArray<OnUserRecoverableAuthListener> onUserRecoverableAuthListeners = new SparseArray<>();

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

    private void clearAndOnUiThreadTriggerOnConnectListeners(final boolean succeed, final int errorCode) {

        final ArrayList<OnConnectListener> pendingOnConnectListeners = new ArrayList<>(onConnectListeners);
        onConnectListeners.clear();
        if (pendingOnConnectListeners.isEmpty())
            return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (OnConnectListener pendingOnConnectListener : pendingOnConnectListeners) {
                    if (pendingOnConnectListener != null) {
                        if (succeed)
                            pendingOnConnectListener.onConnected();
                        else
                            pendingOnConnectListener.onFailed(errorCode);
                    }
                }
            }
        });

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

        Plus.AccountApi.clearDefaultAccount(getClient());
        getClient().disconnect();
        connected = false;
        clearAndOnUiThreadTriggerOnConnectListeners(false, -1);

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

        onConnectListeners.add(onConnectListener);

        if (!isConnecting())
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

        final int errorCode = connectionResult.getErrorCode();


        if (errorCode == 4) { // 4 = SIGN_IN_REQUIRED
            startResolution(connectionResult, new OnStartResolutionListener() {
                @Override
                public void onCompleted(boolean succeed) {

                    if (getClient().isConnected()) {
                        clearAndTriggerOnConnectListeners(true, -1);
                        return;
                    }


                    if (getClient().isConnecting()) {
                        // do nothing, waiting connection done
                        return;
                    }

                    if (!getClient().isConnected()) {
                        if (succeed)
                            client.connect();
                        else {
                            clearAndTriggerOnConnectListeners(false, errorCode);
                        }

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

            if (id == null)
                id = "";
            if (name == null)
                name = "";
            if (email == null)
                email = "";
            if (profileImageLink == null)
                profileImageLink = "";

            person = new Person(id, name, gender, email, profileImageLink);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return person;


    }

    /**
     * Note: This function has BUG, but will not be run (coz + "https://www.googleapis.com/auth/plus.profile.emails.read" is removed from getLastKnownToken())
     *
     * @param userRecoverableAuthIntent
     * @param listener
     */
    private void userRecoverAuth(Intent userRecoverableAuthIntent, OnUserRecoverableAuthListener listener) {
        int id = genUniqueId();
        onUserRecoverableAuthListeners.put(id, listener);

        appContext.startActivity(new Intent(appContext, GPlusUserRecoverableAuthActivity.class).putExtra("userRecoverableAuthIntent", userRecoverableAuthIntent).putExtra("idOfUserRecoverableAuthIntent", id).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }


    public void getLastKnownToken(final OnGetLastKnownTokenListener listener) {

        if (listener == null)
            return;


        if (!isConnected()) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onCompleted("");
                }
            });

            return;
        }


        new AsyncTask<GoogleApiClient, Void, Pair<String, Intent>>() {

            @Override
            protected Pair<String, Intent> doInBackground(GoogleApiClient... clients) {

                String token = "";
                Intent intent = null;

                if (clients.length != 1)
                    return new Pair<>(token, intent);

                try {
                    token = GoogleAuthUtil.getToken(
                            appContext,
                            Plus.AccountApi.getAccountName(clients[0]), "oauth2:"
                                    + Scopes.PLUS_LOGIN + " "
                                    + Scopes.PLUS_ME); // + " https://www.googleapis.com/auth/plus.profile.emails.read"
                    if (token == null)
                        token = "";
                } catch (UserRecoverableAuthException e) {
                    e.printStackTrace();
                    intent = e.getIntent();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return new Pair<>(token, intent);
            }

            @Override
            protected void onPostExecute(Pair<String, Intent> result) {

                String token = result.first;
                Intent intent = result.second;

                if (TextUtils.isEmpty(token) && intent != null) {

                    userRecoverAuth(intent, new OnUserRecoverableAuthListener() {
                        @Override
                        public void onCompleted(String token) {

                            listener.onCompleted(token);

                        }
                    });

                    return;
                }

                listener.onCompleted(token);

            }

        }.execute(getClient());


    }


}
