package com.tommytao.a5steak.util.google;

import android.app.IntentService;
import android.content.Intent;

import com.tommytao.a5steak.util.Foundation;

/**
 *
 * Responsible to recognize activity of users
 *
 * Note: Permission com.google.android.gms.permission.ACTIVITY_RECOGNITION is needed
 *
 */
public class ActivityRecognizer extends Foundation {

    private static ActivityRecognizer instance;

    public static ActivityRecognizer getInstance() {

        if (instance == null)
            instance = new ActivityRecognizer();

        return instance;
    }

    private ActivityRecognizer() {

    }

    // --

    public static class RecognizeService extends IntentService {

        public RecognizeService(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(Intent intent) {

        }

    }

    public static interface OnConnectListener {

        public void onConnected(boolean succeed);


    }

//    GoogleApiClient apiClient = new GoogleApiClient.Builder(appContext)
//            .addApi(ActivityRecognition.API)
//            .build();




    public void connect(OnConnectListener onConnectListener){

    }

    public void disconnect(){

    }




}
