package com.tommytao.a5steak.gspeech;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.common.Foundation;

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

//    public static final String SERVER_G_TRANSLATE_SPEAKER_PREFIX = "http://translate.google.com/translate_tts?&tl=zh-yue&ie=UTF-8&client=t&q=";

    public static final String SERVER_G_TRANSLATE_SPEAKER_PREFIX = "http://translate.google.com/translate_tts?&ie=UTF-8&client=t";

    public static final Locale DEFAULT_LOCALE = new Locale("en", "US");

    private TextToSpeech tts;

    private MediaPlayer gTranslateMediaPlayer;

    private ArrayList<OnConnectListener> onConnectListeners = new ArrayList<>();

    private HashMap<String, OnSpeakListener> ttsOnSpeakListeners = new HashMap<>();
    private OnSpeakListener gTranslateOnSpeakListener;

    private boolean connected;

    @Override
    public boolean init(Context context) {
        return super.init(context);
    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }

    private boolean isCantonese(Locale locale) {

        if (!"zh".equals(locale.getLanguage()))
            return false;

        if (!"HK".equals(locale.getCountry()) && !"MO".equals(locale.getCountry()))
            return false;

        return true;

    }

    private boolean isEnglish(Locale locale) {

        return "en".equals(locale.getLanguage());

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
            gTranslateMediaPlayer.release();
        } catch (Exception e) {
        }
        clearAndOnUiThreadTriggerGTranslateOnSpeakCompleteListener(true);

        connected = false;
    }

    public boolean isConnecting() {

        if (isConnected())
            return false;

        return !onConnectListeners.isEmpty();

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

    private void clearAndOnUiThreadTriggerGTranslateOnSpeakCompleteListener(final boolean succeed) {

        final OnSpeakListener pendingGTranslateOnSpeakListener = gTranslateOnSpeakListener;

        gTranslateOnSpeakListener = null;

        handler.post(new Runnable() {
            @Override
            public void run() {

                if (pendingGTranslateOnSpeakListener != null)
                    pendingGTranslateOnSpeakListener.onComplete(succeed);

            }
        });

    }

    private void clearAndTriggerGTranslateOnSpeakCompleteListener(boolean succeed) {

        final OnSpeakListener pendingGTranslateOnSpeakListener = gTranslateOnSpeakListener;

        gTranslateOnSpeakListener = null;

        if (pendingGTranslateOnSpeakListener != null)
            pendingGTranslateOnSpeakListener.onComplete(succeed);

    }

    private void triggerGTranslateOnSpeakStartListener() {

        if (gTranslateOnSpeakListener != null)
            gTranslateOnSpeakListener.onStart();

    }

    public void stop() {

        tts.stop();

        try {
            gTranslateMediaPlayer.release();
        } catch (Exception e) {
//            e.printStackTrace();
        }

        clearAndOnUiThreadTriggerGTranslateOnSpeakCompleteListener(true);

    }

    private String genGTranslateLocaleStr(Locale locale) {

        if (isCantonese(locale))
            return "zh-yue";

        if (isEnglish(locale))
            return "en-us";

        return locale.getLanguage() + "-" + locale.getCountry().toLowerCase(Locale.US);

    }

    private void speakInGTranslate(String text, Locale locale, final OnSpeakListener listener) {

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
            gTranslateMediaPlayer.release();
        } catch (Exception e) {
//            e.printStackTrace();
        }
        clearAndOnUiThreadTriggerGTranslateOnSpeakCompleteListener(true);


        try {

            gTranslateOnSpeakListener = listener;

            String urlEncodedText = text;
            try {
                urlEncodedText = URLEncoder.encode(text, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            String link = SERVER_G_TRANSLATE_SPEAKER_PREFIX + "&tl=" + genGTranslateLocaleStr(locale) + "&q=" + urlEncodedText;

            log("text_speaker: speak by g translate: link: " + link);

            Uri uri = Uri.parse(link);

            HashMap<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "stagefright/1.2 (Linux;Android 5.0)");
            headers.put("Referer", "http://translate.google.com/");

            gTranslateMediaPlayer = new MediaPlayer();
            gTranslateMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            gTranslateMediaPlayer.setDataSource(appContext, uri, headers);

            gTranslateMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                    boolean succeed = true;
                    try {
                        mediaPlayer.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                        succeed = false;

                        clearAndTriggerGTranslateOnSpeakCompleteListener(false);

                    }

                    if (succeed) {
                        triggerGTranslateOnSpeakStartListener();
                    }

                }
            });

            gTranslateMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    try {
                        mediaPlayer.release();
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }

                    clearAndTriggerGTranslateOnSpeakCompleteListener(true);
                }
            });


            gTranslateMediaPlayer.prepareAsync();

        } catch (Exception e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {

                    clearAndTriggerGTranslateOnSpeakCompleteListener(false);

                }
            });
        }


    }

    /**
     * Turns invalid or strange locale to standard locale
     *
     * @param locale
     * @return
     */
    private Locale normalizeLocale(Locale locale) {

        if (locale == null)
            return DEFAULT_LOCALE;

        if ("en".equals(locale.getLanguage()))
            return new Locale("en", "US");

        return new Locale(locale.getLanguage(), locale.getCountry(), locale.getVariant());

    }

    private void speakInTts(String text, Locale locale, final OnSpeakListener listener) {

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

//        if (locale == null)
//            locale = DEFAULT_LOCALE;

        locale = normalizeLocale(locale);

        int setLangResult = tts.setLanguage(locale);
        if (setLangResult == TextToSpeech.LANG_NOT_SUPPORTED)
            tts.setLanguage(DEFAULT_LOCALE);

        HashMap<String, String> hashMap = new HashMap<>();
        String utteranceId = "" + genUniqueId();
        hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
        ttsOnSpeakListeners.put(utteranceId, listener);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, hashMap);


    }

    public boolean isLanguageAvailable(Locale locale) {

        if (!isConnected())
            return false;

        if (locale == null)
            return false;

        locale = normalizeLocale(locale);

        return tts.isLanguageAvailable(locale) == TextToSpeech.LANG_COUNTRY_AVAILABLE;

    }


    public void speak(String text, Locale locale, final OnSpeakListener listener) {

        if (!isLanguageAvailable(locale)) // isCantonese(locale)
            speakInGTranslate(text, locale, listener);
        else
            speakInTts(text, locale, listener);

    }


    private boolean isRunningOnUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


}
