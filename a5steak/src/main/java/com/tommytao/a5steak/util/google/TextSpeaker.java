package com.tommytao.a5steak.util.google;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.tommytao.a5steak.util.Foundation;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


/**
 * Responsible for speak text
 * <p/>
 * Note: Cantonese speaking may not work coz Google blocks robot access sometimes. Fixed by &client=t
 * <p/>
 * Ref: http://stackoverflow.com/questions/9893175/google-text-to-speech-api
 */
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

    public static final String SERVER_CANTONESE_SPEAKER_PREFIX = "http://translate.google.com/translate_tts?&tl=zh-yue&ie=UTF-8&client=t&q=";

    public static final Locale DEFAULT_LOCALE = new Locale("en", "US");

    private TextToSpeech tts;

    private ArrayList<OnConnectListener> onConnectListeners = new ArrayList<>();

    private boolean connected;

    private Locale locale = DEFAULT_LOCALE;

    public void setLocale(Locale locale) {
        this.locale = locale;

        if (isConnected()) {
            int setLangResult = tts.setLanguage(this.locale);
            if (setLangResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.setLanguage(DEFAULT_LOCALE);
            }
        }
    }

    public Locale getLocale() {
        return locale;
    }

    @Override
    public boolean init(Context context) {
        return super.init(context);
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

            onConnectListeners.add(onConnectListener);

            return;
        }

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

        onConnectListeners.add(onConnectListener);

        tts = new TextToSpeech(appContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {

                    connected = true;
                    setLocale(locale);

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

        for (OnConnectListener pendingOnConnectListener : pendingOnConnectListeners) {
            if (pendingOnConnectListener != null)
                pendingOnConnectListener.onConnected(succeed);
        }

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

            String link = SERVER_CANTONESE_SPEAKER_PREFIX + urlEncodedText;

            log("text_speaker: speak cantonese: link: " + link);

//            playLink(link, new OnPlayListener() {
//                @Override
//                public void onStart() {
//                    if (listener != null)
//                        listener.onStart();
//                }
//
//                @Override
//                public void onComplete(boolean succeed) {
//
//                    if (listener != null)
//                        listener.onComplete(succeed);
//                }
//            });

            try {

                Uri uri = Uri.parse(link);

                HashMap<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "stagefright/1.2 (Linux;Android 5.0)");
                headers.put("Referer", "http://translate.google.com/");

                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                mediaPlayer.setDataSource(link);
                mediaPlayer.setDataSource(appContext, uri, headers);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {

                        boolean succeed = true;
                        try {
                            mediaPlayer.start();

                        } catch (Exception e) {
                            e.printStackTrace();
                            succeed = false;
                            if (listener != null)
                                listener.onComplete(false);
                        }

                        if (succeed && listener != null) {
                            listener.onStart();
                        }

                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        try {
                            mediaPlayer.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (listener != null)
                            listener.onComplete(true);
                    }
                });
                mediaPlayer.prepareAsync();

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null)
                            listener.onComplete(false);

                    }
                });
            }


            return;
        }

        // UtteranceProgressListener may be running in bg thread
        // Ref: http://developer.android.com/intl/es/reference/android/speech/tts/UtteranceProgressListener.html
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

            @Override
            public void onStart(String s) {


                if (listener != null) {

                    if (isRunningOnUiThread())
                        listener.onStart();
                    else
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onStart();
                            }
                        });

                }
            }

            @Override
            public void onDone(String s) {
                if (listener != null) {
                    if (isRunningOnUiThread())
                        listener.onStart();
                    else
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onComplete(true);
                            }
                        });

                }


            }

            @Override
            public void onError(String s) {
                if (listener != null) {
                    if (isRunningOnUiThread())
                        listener.onStart();
                    else
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onComplete(false);
                            }
                        });

                }

            }
        });

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "" + genUniqueId());
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, hashMap);

    }

    private int genUniqueId() {

        return (int) (Math.random() * Integer.MAX_VALUE);

    }

    private boolean isRunningOnUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


}
