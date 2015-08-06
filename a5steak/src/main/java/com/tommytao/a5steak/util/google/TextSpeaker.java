package com.tommytao.a5steak.util.google;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.tommytao.a5steak.util.Foundation;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class TextSpeaker extends Foundation {

    private static TextSpeaker instance;

    public static TextSpeaker getInstance() {

        if (instance == null)
            instance = new TextSpeaker();

        return instance;
    }

    private TextSpeaker() {

    }

    // --

    public static interface OnSpeakListener {
        public void onStart();

        public void onComplete(boolean succeed);
    }

    public static interface OnConnectListener {

        public void onConnected(boolean succeed);


    }

    public static final String SERVER_CANTONESE_SPEAKER_PREFIX = "http://translate.google.com/translate_tts?&tl=zh-yue&ie=UTF-8&q=";

    private TextToSpeech tts;

    private ArrayList<OnConnectListener> onConnectListeners = new ArrayList<>();

    private boolean connected;

    private Locale locale = new Locale("en", "US");

    public void setLocale(Locale locale) {
        this.locale = locale;

        if (isConnected())
            tts.setLanguage(this.locale);
    }

    public Locale getLocale() {
        return locale;
    }


    private boolean shouldUseCantonese() {

        if (!"zh".equals(locale.getLanguage()))
            return false;


        if (!"HK".equals(locale.getCountry()) && !"MO".equals(locale.getCountry()))
            return false;

        return true;

    }

    public void connect(final OnConnectListener onConnectListener) {

        if (isConnecting()) {

            if (onConnectListener != null)
                onConnectListeners.add(onConnectListener);

            return;
        }

        if (isConnected()){
            disconnect();
        }

        if (onConnectListener != null)
            onConnectListeners.add(onConnectListener);

        tts = new TextToSpeech(appContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {

                    tts.setLanguage(locale);
                    connected = true;

                    clearAndTriggerOnConnectListeners(true);
                } else {

                    clearAndTriggerOnConnectListeners(false);

                }
            }
        });

    }

    public void disconnect() {

        if (!(isConnecting() || isConnected()))
            return;

        tts.shutdown();
        tts = null;

        connected = false;
    }

    public boolean isConnecting() {
        return tts != null && !connected;
    }

    public boolean isConnected() {
        return tts != null && connected;
    }

    private void clearAndTriggerOnConnectListeners(boolean succeed) {

        ArrayList<OnConnectListener> pendingOnConnectListeners = new ArrayList<>(onConnectListeners);

        onConnectListeners.clear();

        for (OnConnectListener pendingOnConnectListener : pendingOnConnectListeners)
            pendingOnConnectListener.onConnected(succeed);

    }


    public void speak(String text, final OnSpeakListener listener) {

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

        String urlEncodedText = text;

        try {
            urlEncodedText = URLEncoder.encode(text, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (shouldUseCantonese()) {
            playUrl(SERVER_CANTONESE_SPEAKER_PREFIX + urlEncodedText, new OnPlayListener() {
                @Override
                public void onStart() {
                    if (listener != null)
                        listener.onStart();
                }

                @Override
                public void onComplete(boolean succeed) {

                    if (listener != null)
                        listener.onComplete(succeed);
                }
            });
            return;
        }


        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

            @Override
            public void onStart(String s) {
                if (listener != null)
                    listener.onStart();
            }

            @Override
            public void onDone(String s) {
                if (listener != null)
                    listener.onComplete(true);


            }

            @Override
            public void onError(String s) {
                if (listener != null)
                    listener.onComplete(false);

            }
        });

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }


}
