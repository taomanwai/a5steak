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
import java.util.Set;


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

    private MediaPlayer cantoneseMediaPlayer;

    private ArrayList<OnConnectListener> onConnectListeners = new ArrayList<>();

    private HashMap<String, OnSpeakListener> ttsOnSpeakListeners = new HashMap<>();
    private OnSpeakListener cantoneseOnSpeakListener;

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

        // UtteranceProgressListener may be running in bg thread
        // Ref: http://developer.android.com/intl/es/reference/android/speech/tts/UtteranceProgressListener.html
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

            @Override
            public void onStart(String utteranceId) {

                final OnSpeakListener listener = ttsOnSpeakListeners.get(utteranceId);
//                onSpeakListenersOfTts.remove(utteranceId);

                if (listener != null) {

                    if (isRunningOnUiThread()) {
                        listener.onStart();
                    } else
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onStart();
                            }
                        });

                }
            }

            @Override
            public void onDone(String utteranceId) {

                final OnSpeakListener listener = ttsOnSpeakListeners.get(utteranceId);
                ttsOnSpeakListeners.remove(utteranceId);

                if (listener != null) {
                    if (isRunningOnUiThread())
                        listener.onComplete(true);
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
            public void onError(String utteranceId) {

                final OnSpeakListener listener = ttsOnSpeakListeners.get(utteranceId);
                ttsOnSpeakListeners.remove(utteranceId);

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


    }

    public void disconnect() {

        if (!(isConnecting() || isConnected()))
            return;

        tts.shutdown();
        tts = null;
        clearAndOnUiThreadTriggerTtsOnSpeakCompleteListeners(true);

        try {
            cantoneseMediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        clearAndOnUiThreadTriggerCantoneseOnSpeakCompleteListener(true);

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

    private void clearAndOnUiThreadTriggerTtsOnSpeakCompleteListeners(final boolean succeed) {

        final HashMap<String, OnSpeakListener> pendingTtsOnSpeakListeners = new HashMap<>(ttsOnSpeakListeners);

        ttsOnSpeakListeners.clear();

        handler.post(new Runnable() {
            @Override
            public void run() {

                Set<String> keys = pendingTtsOnSpeakListeners.keySet();

                for (String key : keys) {
                    if (pendingTtsOnSpeakListeners.get(key) != null)
                        pendingTtsOnSpeakListeners.get(key).onComplete(succeed);
                }

            }
        });

    }

    private void clearAndOnUiThreadTriggerCantoneseOnSpeakCompleteListener(final boolean succeed) {

        final OnSpeakListener pendingCantoneseOnSpeakListener = cantoneseOnSpeakListener;

        cantoneseOnSpeakListener = null;

        handler.post(new Runnable() {
            @Override
            public void run() {

                if (pendingCantoneseOnSpeakListener != null)
                    pendingCantoneseOnSpeakListener.onComplete(succeed);

            }
        });

    }

    private void clearAndTriggerCantoneseOnSpeakCompleteListener(boolean succeed) {

        final OnSpeakListener pendingCantoneseOnSpeakListener = cantoneseOnSpeakListener;

        cantoneseOnSpeakListener = null;

        if (pendingCantoneseOnSpeakListener != null)
            pendingCantoneseOnSpeakListener.onComplete(succeed);

    }

    private void triggerCantoneseOnSpeakStartListener() {

        if (cantoneseOnSpeakListener != null)
            cantoneseOnSpeakListener.onStart();

    }

    public void stop() {

        tts.stop();

        try {
            cantoneseMediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        clearAndOnUiThreadTriggerCantoneseOnSpeakCompleteListener(true);

    }

    private void speakInCantonese(String text, final OnSpeakListener listener) {

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

        try {
            cantoneseMediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        clearAndOnUiThreadTriggerCantoneseOnSpeakCompleteListener(true);

//            if (onSpeakListenerOfCantonese != null) {
//                final OnSpeakListener pendingOnSpeakListenerOfCantonese = onSpeakListenerOfCantonese;
//                onSpeakListenerOfCantonese = null;
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (pendingOnSpeakListenerOfCantonese != null)
//                            pendingOnSpeakListenerOfCantonese.onComplete(true);
//                    }
//                });
//            }

        try {

            cantoneseOnSpeakListener = listener;

            String urlEncodedText = text;
            try {
                urlEncodedText = URLEncoder.encode(text, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            String link = SERVER_CANTONESE_SPEAKER_PREFIX + urlEncodedText;

            log("text_speaker: speak cantonese: link: " + link);

            Uri uri = Uri.parse(link);

            HashMap<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "stagefright/1.2 (Linux;Android 5.0)");
            headers.put("Referer", "http://translate.google.com/");

            cantoneseMediaPlayer = new MediaPlayer();
            cantoneseMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            cantoneseMediaPlayer.setDataSource(appContext, uri, headers);

            cantoneseMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                    boolean succeed = true;
                    try {
                        mediaPlayer.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                        succeed = false;

//                        cantoneseOnSpeakListener = null;
//                        if (listener != null) {
//                            listener.onComplete(false);
//                        }

                        clearAndTriggerCantoneseOnSpeakCompleteListener(false);

                    }

                    if (succeed) {
//                        listener.onStart();
                        triggerCantoneseOnSpeakStartListener();

                    }

                }
            });

            cantoneseMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    try {
                        mediaPlayer.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    onSpeakListenerOfCantonese = null;
//                    if (listener != null) {
//                        listener.onComplete(true);
//                    }

                    clearAndTriggerCantoneseOnSpeakCompleteListener(true);
                }
            });


            cantoneseMediaPlayer.prepareAsync();

        } catch (Exception e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {

//                    onSpeakListenerOfCantonese = null;
//                    if (listener != null) {
//                        listener.onComplete(false);
//                    }

                    clearAndTriggerCantoneseOnSpeakCompleteListener(false);

                }
            });
        }


    }

    private void speakInTts(String text, final OnSpeakListener listener) {

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

        HashMap<String, String> hashMap = new HashMap<>();
        String utteranceId = "" + genUniqueId();
        hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
        ttsOnSpeakListeners.put(utteranceId, listener);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, hashMap);


    }


    public void speak(String text, final OnSpeakListener listener) {

        if (shouldUseCantonese())
            speakInCantonese(text, listener);
        else
            speakInTts(text, listener);

    }


    private boolean isRunningOnUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


}
