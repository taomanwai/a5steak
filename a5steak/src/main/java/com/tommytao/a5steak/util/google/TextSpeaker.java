package com.tommytao.a5steak.util.google;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.tommytao.a5steak.util.Foundation;

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

    public static final String SERVER_CANTONESE_SPEAKER_PREFIX = "http://translate.google.com/translate_tts?&tl=zh-yue&ie=UTF-8&q=";

    private TextToSpeech tts;

    private boolean ttsInitialized;

    private boolean cantonese;

    @Deprecated
    public boolean init(Context appContext) {
        return super.init(appContext);
    }

    public boolean init(Context context, Locale locale) {
        if (!super.init(context)) {
            return false;
        }

        if (isLocaleBeingHK(locale) || isLocaleBeingMacau(locale))
            initCantonese();
        else
            initTts(locale);

        return true;

    }

    private boolean isLocaleBeingHK(Locale locale){
        return "zh".equals(locale.getLanguage()) && "HK".equals(locale.getCountry());
    }

    private boolean isLocaleBeingMacau(Locale locale){
        return "zh".equals(locale.getLanguage()) && "MO".equals(locale.getCountry());
    }



    private void initTts(final Locale locale) {

        tts = new TextToSpeech(appContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(locale);
                    ttsInitialized = true;
                }
            }
        });


    }

    private void initCantonese() {

        cantonese = true;

    }

    public boolean isTtsInitialized() {
        return ttsInitialized;
    }

    public boolean isCantonese() {
        return cantonese;
    }

    public void speak(String text) {

        if (cantonese) {
            playUrl(SERVER_CANTONESE_SPEAKER_PREFIX + text);
            return;
        }

        if (!isTtsInitialized())
            return;

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }


}
