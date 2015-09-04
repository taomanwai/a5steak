package com.tommytao.a5steak.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.SparseArray;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.device.yearclass.YearClass;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 *
 * Responsible for Facebook operations
 *
 * Ref:
 * https://developers.facebook.com/docs/sharing/android
 *
 */
public class FbManager extends Foundation {

    private static FbManager instance;

    public static FbManager getInstance() {

        if (instance == null)
            instance = new FbManager();

        return instance;
    }

    private FbManager() {

    }


    // --

    public static final String PERMISSION_READ_PUBLIC_PROFILE = "public_profile";
    public static final String PERMISSION_READ_USER_FRIENDS = "user_friends";

    public static final String PERMISSION_PUBLISH_PUBLISH_ACTIONS = "publish_actions";


    public static interface OnGetYearClassListener {
        public void onComplete(int yearClass);
    }

    public static interface OnLoginListener {
        public void onComplete(String token);
    }

    public static interface OnGetMeListener {
        public void onComplete(User user);
    }

    public static interface OnGetFriendsListener {

        /**
         * Note: to check if update succeed or not, check if totalCount being -1
         *
         * @param users
         * @param totalCount
         */
        public void onComplete(ArrayList<User> users, int totalCount);
    }

    public static interface OnShareListener {
        public void onComplete(String postId);
    }





    public static class Device {
        private String hardware = "";
        private String os = "";

        public Device(String hardware, String os) {
            this.hardware = hardware;
            this.os = os;
        }

        public String getHardware() {
            return hardware;
        }

        public String getOs() {
            return os;
        }
    }


    public static class User {

        public static final int GENDER_UNCLASSIFIED = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

        private String id = "";
        private String firstName = "";
        private String lastName = "";
        private int gender;
        private Date birthday;
        private String email = "";
        private String coverImageLink = "";
        private ArrayList<Device> devices = new ArrayList<>();
        private String currency = "";

        public User(String id, String firstName, String lastName, int gender, Date birthday, String email, String coverImageLink, ArrayList<Device> devices, String currency) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.gender = gender;
            this.birthday = birthday;
            this.email = email;
            this.coverImageLink = coverImageLink;
            this.devices = devices;
            this.currency = currency;
        }

        public String getName() {

            if (TextUtils.isEmpty(getFirstName()))
                return getLastName();

            if (TextUtils.isEmpty(getLastName()))
                return getFirstName();

            return getFirstName() + " " + getLastName();
        }

        public String getLink() {
            return "https://www.facebook.com/" + getId();
        }

        public String getProfileImageLink() {
            return String.format("https://graph.facebook.com/%s/picture?type=large", getId());
        }

        public static User fromJSONObject(JSONObject jObj) {

            if (jObj == null)
                return null;

            String id = jObj.optString("id", "");
            String firstName = jObj.optString("first_name", "");
            String lastName = jObj.optString("last_name", "");
            int gender = GENDER_UNCLASSIFIED;
            String genderStr = jObj.optString("gender", "");
            if ("male".equals(genderStr)) {
                gender = GENDER_MALE;
            } else if ("female".equals(genderStr)) {
                gender = GENDER_FEMALE;
            }
            String birthdayStr = jObj.optString("birthday", "");
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date birthday = null;
            try {
                birthday = sdf.parse(birthdayStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String email = jObj.optString("email", "");
            String coverImageLink = "";
            try {
                coverImageLink = jObj.optJSONObject("cover").optString("source", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            ArrayList<Device> devices = new ArrayList<>();
            try {
                JSONArray devicesJArray = jObj.optJSONArray("devices");
                JSONObject deviceJObj = null;
                String hardware = "";
                String os = "";
                for (int i = 0; i < devicesJArray.length(); i++) {
                    deviceJObj = devicesJArray.optJSONObject(i);
                    if (deviceJObj == null)
                        continue;
                    hardware = deviceJObj.optString("hardware", "");
                    os = deviceJObj.optString("os", "");
                    devices.add(new Device(hardware, os));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String currency = "";
            try {
                currency = jObj.optJSONObject("currency").optString("user_currency", "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return new User(id, firstName, lastName, gender, birthday, email, coverImageLink, devices, currency);

        }


        public String getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public int getGender() {
            return gender;
        }

        public Date getBirthday() {
            return birthday;
        }

        public String getEmail() {
            return email;
        }

        public String getCoverImageLink() {
            return coverImageLink;
        }

        public ArrayList<Device> getDevices() {
            return devices;
        }

        public String getCurrency() {
            return currency;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id='" + id + '\'' +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", gender=" + gender +
                    ", birthday=" + birthday +
                    ", email='" + email + '\'' +
                    ", coverImageLink='" + coverImageLink + '\'' +
                    ", devices=" + devices +
                    ", currency='" + currency + '\'' +
                    '}';
        }
    }


    public static class FbLoginActivity extends Activity {

        private CallbackManager callbackManager = CallbackManager.Factory.create();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Intent intent = getIntent();
            boolean read = intent.getBooleanExtra("read", true);
            ArrayList<String> permissions = intent.getStringArrayListExtra("permissions");
            int id = intent.getIntExtra("idOfLoginListener", -1);

            final OnLoginListener listener = (id == -1) ? null : FbManager.getInstance().loginListeners.get(id);
            FbManager.getInstance().loginListeners.remove(id);

            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                @Override
                public void onSuccess(LoginResult loginResult) {

                    String token = loginResult.getAccessToken().getToken();
                    if (token == null)
                        token = "";

                    final String tokenFinal = token;

                    finish();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null)
                                listener.onComplete(tokenFinal);
                        }
                    });

                }

                @Override
                public void onCancel() {

                    finish();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null)
                                listener.onComplete("");
                        }
                    });
                }

                @Override
                public void onError(FacebookException e) {

                    finish();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null)
                                listener.onComplete("");
                        }
                    });

                }
            });

            if (read)
                LoginManager.getInstance().logInWithReadPermissions(this, permissions);
            else
                LoginManager.getInstance().logInWithPublishPermissions(this, permissions);


        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            callbackManager.onActivityResult(requestCode, resultCode, data);


        }
    }

    public static class FbShareActivity extends Activity {

        private ShareDialog shareDialog;
        private CallbackManager callbackManager = CallbackManager.Factory.create();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            shareDialog = new ShareDialog(this);

            Intent intent = getIntent();

            int idOfShareListener = intent.getIntExtra("idOfShareListener", -1);
            int idOfShareBitmap = intent.getIntExtra("idOfShareBitmap", -1);
            String shareContentType = intent.getStringExtra("shareContentType");
            ShareContent content = null;
            switch (shareContentType) {

                case "link":
                    String link = intent.getStringExtra("link");
                    String imageLink = intent.getStringExtra("imageLink");
                    String title = intent.getStringExtra("title");
                    String description = intent.getStringExtra("description");
                    if (link == null)
                        link = "";
                    if (imageLink == null)
                        imageLink = "";
                    if (title == null)
                        title = "";
                    if (description == null)
                        description = "";

                    content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(link))
                            .setImageUrl(Uri.parse(imageLink))
                            .setContentTitle(title)
                            .setContentDescription(description)
                            .build();

                    break;

                case "photo":

                    final Bitmap bitmap = (idOfShareBitmap==-1) ? null : FbManager.getInstance().shareBitmaps.get(idOfShareBitmap);
                    FbManager.getInstance().shareBitmaps.remove(idOfShareBitmap);

                    SharePhoto sharePhoto = new SharePhoto.Builder()
                            .setBitmap(bitmap)
                            .build();

                    content = new SharePhotoContent.Builder()
                            .addPhoto(sharePhoto)
                            .build();
                    break;

                case "video":
                    Uri localUrl = (Uri) intent.getParcelableExtra("localUrl");

                    ShareVideo shareVideo = new ShareVideo.Builder()
                            .setLocalUrl(localUrl)
                            .build();

                    content = new ShareVideoContent.Builder()
                            .setVideo(shareVideo)
                            .build();

                    break;


            }


            final OnShareListener listener = (idOfShareListener == -1) ? null : FbManager.getInstance().shareListeners.get(idOfShareListener);
            FbManager.getInstance().shareListeners.remove(idOfShareListener);

            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {

                    final String postId = result.getPostId() == null ? "" : result.getPostId();

                    finish();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null)
                                listener.onComplete(postId);
                        }
                    });


                }

                @Override
                public void onCancel() {

                    finish();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null)
                                listener.onComplete("");
                        }
                    });


                }

                @Override
                public void onError(FacebookException e) {

                    finish();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null)
                                listener.onComplete("");
                        }
                    });

                }
            });


            if (content != null)
                shareDialog.show(content);



        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            callbackManager.onActivityResult(requestCode, resultCode, data);


        }
    }



    private SparseArray<OnLoginListener> loginListeners = new SparseArray<>();
    private SparseArray<OnShareListener> shareListeners = new SparseArray<>();
    private SparseArray<Bitmap> shareBitmaps = new SparseArray<>();

    @Override
    public boolean init(Context context) {
        if (!super.init(context)) {
            return false;
        }

        FacebookSdk.sdkInitialize(appContext);

        return true;
    }

    /**
     * Login to Facebook
     *
     * @param activity    Activity of page calling this function
     * @param readMode    TRUE=login with read granting, FALSE=login with publish granting
     * @param permissions Exact permissions of what can be read, what can be publish.
     * @param listener    Listener to receive result (i.e. token)
     */
    public void login(Activity activity, boolean readMode, ArrayList<String> permissions, OnLoginListener listener) {

        int id = genUniqueId();
        loginListeners.put(id, listener);

        activity.startActivity(new Intent(activity, FbLoginActivity.class).putExtra("read", readMode).putExtra("permissions", permissions).putExtra("idOfLoginListener", id));

    }


    public String getLastKnownToken() {

        String result = "";

        if (!isLoggedIn())
            return result;

        result = AccessToken.getCurrentAccessToken().getToken();
        if (result == null)
            result = "";

        return result;

    }

    public boolean isLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    public void getMe(final OnGetMeListener listener) {

        if (listener == null) {
            return;
        }

        if (!isLoggedIn()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onComplete(null);
                }
            });
            return;
        }

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject me, GraphResponse response) {

                listener.onComplete(User.fromJSONObject(me));

            }
        });
        Bundle params = new Bundle();
        params.putString("fields", "id, first_name, last_name, gender, birthday, email, cover, devices, currency");
        request.setParameters(params);
        request.executeAsync();

    }

    public void getFriends(int offset, int limit, final OnGetFriendsListener listener) {

        if (listener == null) {
            return;
        }

        if (!isLoggedIn()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onComplete(new ArrayList<User>(), -1);
                }
            });
            return;
        }

        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        JSONArray dataJArray = null;

                        try {
                            dataJArray = response.getJSONObject().optJSONArray("data");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (dataJArray == null) {
                            listener.onComplete(new ArrayList<User>(), -1);
                            return;
                        }

                        ArrayList<User> users = new ArrayList<>();
                        User user = null;
                        for (int i = 0; i < dataJArray.length(); i++) {

                            user = User.fromJSONObject(dataJArray.optJSONObject(i));

                            if (user != null)
                                users.add(user);

                        }

                        int totalCount = -1;
                        try {
                            totalCount = response.getJSONObject().optJSONObject("summary").optInt("total_count");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (totalCount == -1) {
                            listener.onComplete(new ArrayList<User>(), -1);
                            return;
                        }

                        listener.onComplete(users, totalCount);


                    }
                }
        );
        Bundle params = new Bundle();
        params.putString("fields", "id, first_name, last_name, gender, birthday, email, cover, devices, currency");
        params.putInt("offset", offset);
        params.putInt("limit", limit);
        request.setParameters(params);
        request.executeAsync();


    }



    public void shareLink(Activity activity, String link, String imageLink, String title, String description, final OnShareListener listener) {

        if (!isLoggedIn()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onComplete("");
                }
            });
            return;
        }

        int id = genUniqueId();
        shareListeners.put(id, listener);

        activity.startActivity(new Intent(activity, FbShareActivity.class)
                .putExtra("shareContentType", "link")
                .putExtra("link", link)
                .putExtra("imageLink", imageLink)
                .putExtra("title", title)
                .putExtra("description", description)
                .putExtra("idOfShareListener", id));

    }

    public void sharePhoto(Activity activity, Bitmap bitmap, final OnShareListener listener){

        if (!isLoggedIn()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onComplete("");
                }
            });
            return;
        }

        int id = genUniqueId();
        shareListeners.put(id, listener);
        shareBitmaps.put(id, bitmap);

        activity.startActivity(new Intent(activity, FbShareActivity.class)
                .putExtra("shareContentType", "photo")
                .putExtra("idOfShareListener", id)
                .putExtra("idOfShareBitmap", id));

    }



    /**
     *
     * Note: Max. size of local video url should be 12MB size in max.
     *
     * @param activity
     * @param localUrl
     * @param listener
     */
    public void shareVideo(Activity activity, Uri localUrl, final OnShareListener listener){

        if (!isLoggedIn()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onComplete("");
                }
            });
            return;
        }

        int id = genUniqueId();
        shareListeners.put(id, listener);

        activity.startActivity(new Intent(activity, FbShareActivity.class)
                .putExtra("shareContentType", "video")
                .putExtra("localUrl", localUrl)
                .putExtra("idOfShareListener", id));

    }

    public void getYearClass(final OnGetYearClassListener listener){

        if (listener==null)
            return;

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {

                return YearClass.get(appContext);

            }

            @Override
            protected void onPostExecute(Integer result) {

                listener.onComplete(result);

            }

        }.execute();

    }



}
