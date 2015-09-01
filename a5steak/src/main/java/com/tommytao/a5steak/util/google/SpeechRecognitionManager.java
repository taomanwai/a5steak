package com.tommytao.a5steak.util.google;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import com.tommytao.a5steak.util.Foundation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpeechRecognitionManager extends Foundation {

    private static SpeechRecognitionManager instance;

    public static SpeechRecognitionManager getInstance() {

        if (instance == null)
            instance = new SpeechRecognitionManager();

        return instance;
    }

    private SpeechRecognitionManager() {

    }

    // --

    public static final String MARKET_PREFIX = "market://details?id=";
    public static final String HTTP_PREFIX = "http://play.google.com/store/apps/details?id=";

    public static final String VOICE_SEARCH_PACKAGE_NAME = "com.google.android.voicesearch";

    public static interface Listener {

        public void onComplete(String result);

    }

    private SpeechRecognizer speechRecognizer; // static

    private SpeechRecognizer getSpeechRecognizer() { // synchronized

        if (speechRecognizer == null)
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(appContext);

        return speechRecognizer;
    }


    public boolean isAvailable() {

        PackageManager pm = appContext.getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        return (activities.size() != 0);

    }

    public void installGoogleVoiceSearch(Activity activity) {

//        int connectionResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(appContext);
//        boolean gPlayExist = (connectionResult == ConnectionResult.SUCCESS || connectionResult == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED);
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((isGPlayExist() ? MARKET_PREFIX : HTTP_PREFIX) + VOICE_SEARCH_PACKAGE_NAME)));

    }

    @Override
    public boolean init(Context appContext) {
        return super.init(appContext);
    }

    private Intent genIntent(boolean isWebSearchOnly, Locale locale) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, isWebSearchOnly ? RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
                : RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        String localeStr = locale.getLanguage() + "-" + locale.getCountry();

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeStr);

        intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{localeStr});

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        return intent;
    }


    public void stopAndAnalysisListening(){
        getSpeechRecognizer().stopListening();
    }

    public void cancelListening(){
        getSpeechRecognizer().cancel();
    }

    /**
     *
     *
     * Note:
     * <a href="http://stackoverflow.com/questions/18476088/how-do-i-create-the-semi-transparent-grey-tutorial-overlay-in-android">Note1</a>
     * <a href="http://stackoverflow.com/questions/5849063/does-recognitionlistener-onerror-automatically-speechrecognizer-cancel">Note2</a>
     * <a href="http://stackoverflow.com/questions/6316937/how-can-i-use-speech-recognition-without-the-annoying-dialog-in-android-phones">Note3</a>
     *
     * @param isWebSearchOnly
     * @param locale
     * @param listener
     */
    public void listen(boolean isWebSearchOnly, Locale locale, final Listener listener) {

        if (listener == null)
            return;

        getSpeechRecognizer().setRecognitionListener(new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle params) {

                // do nothing


            }

            @Override
            public void onBeginningOfSpeech() {

                // do nothing

            }

            @Override
            public void onRmsChanged(float rmsdB) {

                // do nothing

            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // do nothing

            }

            @Override
            public void onEndOfSpeech() {
                // do nothing

            }

            @Override
            public void onError(int error) {

                // TODO it may be called twice
                listener.onComplete("");


            }

            @Override
            public void onResults(Bundle results) {

                ArrayList<String> recognitionResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                listener.onComplete(recognitionResults.isEmpty() ? "" : recognitionResults.get(0));


            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // do nothing

            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // do nothing
            }

        });
        getSpeechRecognizer().startListening(genIntent(isWebSearchOnly, locale));

    }



}
