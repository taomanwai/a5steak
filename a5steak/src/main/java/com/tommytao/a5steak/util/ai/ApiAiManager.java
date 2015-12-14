package com.tommytao.a5steak.util.ai;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.util.Foundation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;


/**
 *
 * Responsible for accessing api.ai Siri-like web service
 *
 * Note: <uses-permission android:name="android.permission.INTERNET" /> required
 *
 * Created by tommytao on 11/12/2015.
 *
 */
public class ApiAiManager extends Foundation {
    private static ApiAiManager instance = new ApiAiManager();

    public static ApiAiManager getInstance() {
        return instance;
    }

    private ApiAiManager() {
    }

    // --

    public static class ApiAiResult {

        private String action = "";
        private HashMap<String, String> parameters = new HashMap<>();

        public ApiAiResult(String action, HashMap<String, String> parameters) {
            this.action = action;
            this.parameters = parameters;
        }

        public String getAction() {
            return action;
        }

        public HashMap<String, String> getParameters() {
            return parameters;
        }
    }

    public static interface Listener {

        public void returnApiAiResult(ApiAiResult apiResult);

    }

    private AIDataService aiDataService;

    private String clientAccessToken = "";
    private String subscriptionKey = "";
    private Locale locale;

    @Deprecated
    public boolean init(Context context) {
        return super.init(context);
    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }

    public boolean init(Context context, String subscriptionKey, String clientAccessToken, Locale locale) {

        if (!super.init(context)) {
            log("apiai: " + "init REJECTED: already initialized");
            return false;
        }

        this.clientAccessToken = clientAccessToken;
        this.subscriptionKey = subscriptionKey;

        this.locale = locale;

        // init aiDataService at the very beginning (to speed up analyze speed later on)
        getAiDataService();

        return true;

    }

    @Override
    public boolean isInitialized() {
        return super.isInitialized();
    }

    public AIDataService getAiDataService() {

        if (aiDataService == null) {

//                English("en"),
//                Russian("ru"),
//                German("de"),
//                Portuguese("pt"),
//                PortugueseBrazil("pt-BR"),
//                Spanish("es"),
//                French("fr"),
//                Italian("it"),
//                Japanese("ja"),
//                Korean("ko"),
//                ChineseChina("zh-CN"),
//                ChineseHongKong("zh-HK"),
//                ChineseTaiwan("zh-TW");

            AIConfiguration.SupportedLanguages supportedLanguage = AIConfiguration.SupportedLanguages.English;
            String lang = locale.getLanguage();
            String country = locale.getCountry();


            switch (lang) {
                case "en":
                    supportedLanguage = AIConfiguration.SupportedLanguages.English;
                    break;
                case "ru":
                    supportedLanguage = AIConfiguration.SupportedLanguages.Russian;
                    break;
                case "de":
                    supportedLanguage = AIConfiguration.SupportedLanguages.German;
                    break;
                case "pt":
                    if (!"BR".equals(country)) {
                        supportedLanguage = AIConfiguration.SupportedLanguages.Portuguese;
                    } else {
                        supportedLanguage = AIConfiguration.SupportedLanguages.PortugueseBrazil;
                    }
                    break;
                case "es":
                    supportedLanguage = AIConfiguration.SupportedLanguages.Spanish;
                    break;
                case "fr":
                    supportedLanguage = AIConfiguration.SupportedLanguages.French;
                    break;
                case "it":
                    supportedLanguage = AIConfiguration.SupportedLanguages.Italian;
                    break;
                case "ja":
                    supportedLanguage = AIConfiguration.SupportedLanguages.Japanese;
                    break;
                case "ko":
                    supportedLanguage = AIConfiguration.SupportedLanguages.Korean;
                    break;
                case "zh":
                    if ("TW".equals(country)) {
                        supportedLanguage = AIConfiguration.SupportedLanguages.ChineseTaiwan;
                    } else if ("HK".equals(country) || "MO".equals(country)) {
                        supportedLanguage = AIConfiguration.SupportedLanguages.ChineseHongKong;
                    } else if ("CN".equals(country) || "SG".equals(country)) {
                        supportedLanguage = AIConfiguration.SupportedLanguages.ChineseChina;
                    } else {
                        supportedLanguage = AIConfiguration.SupportedLanguages.ChineseChina;
                    }
                    break;

            }


            final AIConfiguration config = new AIConfiguration(clientAccessToken,
                    subscriptionKey,
                    supportedLanguage,
                    AIConfiguration.RecognitionEngine.System);

            aiDataService = new AIDataService(appContext, config);
        }

        return aiDataService;
    }

    private String aiResponseToAction(AIResponse aiResponse) {

        String action = "";

        if (aiResponse == null)
            return action;

        if (aiResponse.getResult() == null)
            return action;

        action = aiResponse.getResult().getAction();

        if (action == null)
            action = "";

        return action;


    }

    private HashMap<String, String> aiResponseToParameters(AIResponse aiResponse) {

        HashMap<String, String> parameters = new HashMap<>();

        if (aiResponse == null)
            return parameters;

        if (aiResponse.getResult() == null)
            return parameters;

        if (aiResponse.getResult().getParameters()==null)
            return parameters;

        Set<String> keys = aiResponse.getResult().getParameters().keySet();
        String value = "";

        for (String key : keys) {
            value = "";
            try {
                value = aiResponse.getResult().getParameters().get(key).isJsonNull() ? "" : aiResponse.getResult().getParameters().get(key).getAsString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            parameters.put(key, value);
        }

        return parameters;

    }

    public void analyze(String input, final Listener listener) {

        if (TextUtils.isEmpty(input)){

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.returnApiAiResult(new ApiAiResult("", new HashMap<String, String>()));
                }
            });

            return;
        }

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(input); // e.g. "大圍去沙田"

        new AsyncTask<AIRequest, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                final AIRequest request = requests[0];
                try {
                    final AIResponse response = getAiDataService().request(aiRequest);
                    return response;
                } catch (AIServiceException e) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(AIResponse aiResponse) {

                if (listener != null)
                    listener.returnApiAiResult(new ApiAiResult(aiResponseToAction(aiResponse), aiResponseToParameters(aiResponse)));

            }

        }.executeOnExecutor(Executors.newCachedThreadPool(), aiRequest);

    }


}
