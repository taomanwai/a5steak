package com.tommytao.a5steak.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class SemanticsManager extends Foundation {

    private static SemanticsManager instance;

    public static SemanticsManager getInstance() {

        if (instance == null)
            instance = new SemanticsManager();

        return instance;
    }

    private SemanticsManager() {

    }


    // --

    public static interface OnGetKeywordListener {

        public void onComplete(ArrayList<Keyword> keywords);


    }

    public static String GET_KEYWORD_LINK = "http://api.bosonnlp.com/keywords/analysis";

    public class Keyword {

        private String text = "";
        private double weight = -1;

        public Keyword(String text, double weight) {
            this.text = text;
            this.weight = weight;
        }

        public String getText() {
            return text;
        }

        public double getWeight() {
            return weight;
        }
    }

    private String token = "";

    @Deprecated
    public boolean init(Context context) {
        return super.init(context);
    }

    public boolean init(Context context, String token) {

        if (!super.init(context)){
            return false;
        }

        this.token = token;

        return true;

    }

    public void getKeyword(String sentence, final OnGetKeywordListener listener){

        if (TextUtils.isEmpty(sentence)){

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listener.onComplete(new ArrayList<Keyword>());
                }
            });

            return;
        }

        String sentenceInUtfRepresentation = textToUtfRepresentation(sentence);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-Token", token);

        String dataStr = "[\"" + sentenceInUtfRepresentation + "\"]";


        httpPostString(GET_KEYWORD_LINK, dataStr, headers, new OnHttpPostStringListener() {
            @Override
            public void onComplete(String responseStr) {
                ArrayList<Keyword> result = new ArrayList<>();
                boolean hasException = false;
                try {
                    JSONArray jArray = new JSONArray(responseStr);
                    jArray = jArray.getJSONArray(0);


                    String text = "";
                    double weight = -1;
                    Keyword keyword = new Keyword(text, weight);
                    for (int i=0; i<jArray.length(); i++){
                        weight = jArray.getJSONArray(i).getDouble(0);
                        text = jArray.getJSONArray(i).getString(1);
                        keyword = new Keyword(text, weight);
                        result.add(keyword);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    hasException = true;
                }

                listener.onComplete(hasException ? new ArrayList<Keyword>() : result);

            }
        });

    }




}
