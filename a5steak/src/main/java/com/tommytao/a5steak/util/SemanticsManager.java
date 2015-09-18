package com.tommytao.a5steak.util;

import android.content.Context;
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

    public static interface OnAnalyzeGrammarListener {

        public void onComplete(ArrayList<GarmmarWord> words);

    }

    public static String GET_KEYWORD_LINK = "http://api.bosonnlp.com/keywords/analysis";
    public static String ANALYZE_GRAMMAR_LINK = "http://api.bosonnlp.com/depparser/analysis";

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

    /**
     *
     *
     * Ref: http://docs.bosonnlp.com/depparser.html
     *
     */
    public class GarmmarWord {

        public final int ROLE_NONE = -1; // None
        public final int ROLE_ROOT = 0; // Main verb
        public final int ROLE_SBJ = 1; // Subject
        public final int ROLE_OBJ = 2; // Object
        public final int ROLE_PU = 3; // Punctuation
        public final int ROLE_TMP = 4; // Time
        public final int ROLE_LOC = 5; // Location
        public final int ROLE_MNR = 6; // Adverb for verb
        public final int ROLE_POBJ = 7; // Object (middle)
        public final int ROLE_PMOD = 8; // Adverb for verb
        public final int ROLE_NMOD = 9; // Adjective
        public final int ROLE_VMOD = 10; // Adverb for verb
        public final int ROLE_VRD = 11; // Verb (showing result)
        public final int ROLE_DEG = 12; // Subject for "of"
        public final int ROLE_DEV = 13; // Adverb for "ly"
        public final int ROLE_LC = 14; // Location (structure)
        public final int ROLE_M = 15; // number
        public final int ROLE_AMOD = 16; // Adverb (for adjective)
        public final int ROLE_PRN = 17; // word inside "()"
        public final int ROLE_VC = 18; // Adverb for is
        public final int ROLE_COOR = 19; // Relationship similar to "And"
        public final int ROLE_CS = 20; // word after if
        public final int ROLE_DEC = 21; // of

        private String text = "";
        private float head = Float.NaN;
        private int role = ROLE_NONE;

        public GarmmarWord(String text, float head, int role) {
            this.text = text;
            this.head = head;
            this.role = role;
        }

        public String getText() {
            return text;
        }

        public int getRole() {
            return role;
        }

        public float getHead() {
            return head;
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

    private String getBranketSentenceInUtfRepresentation(String sentence){

        String prefix = "[\"";
        String suffix = "\"]";

        if (TextUtils.isEmpty(sentence)){
            return prefix + suffix;
        }

        return prefix + strToUtfRepresentation(sentence) + suffix;

    }

    public void getKeyword(String sentence, final OnGetKeywordListener listener){

        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-Token", token);

        String dataStr = getBranketSentenceInUtfRepresentation(sentence);

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

    public void analyzeGrammar(String sentence, final OnAnalyzeGrammarListener listener){

        String sentenceInUtfRepresentation = getBranketSentenceInUtfRepresentation(sentence);

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

//                listener.onComplete(hasException ? new ArrayList<Keyword>() : result);

            }
        });


    }







}
