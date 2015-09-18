package com.tommytao.a5steak.util;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

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

        public void onComplete(ArrayList<GrammarWord> words);

    }

    public static interface OnGetTimeInMillisListener {

        public void onComplete(long timeInMillis);

        public void onError();

    }

    public static String GET_KEYWORD_LINK = "http://api.bosonnlp.com/keywords/analysis";
    public static String ANALYZE_GRAMMAR_LINK = "http://api.bosonnlp.com/depparser/analysis";
    public static String GET_TIME_IN_MILLIS_LINK_PREFIX = "http://api.bosonnlp.com/time/analysis";

    public static String INPUT_PREFIX = "[\"";
    public static String INPUT_SUFFIX = "\"]";

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
     * Ref: http://docs.bosonnlp.com/depparser.html
     */
    public class GrammarWord {

        public static final int ROLE_NONE = -1; // None
        public static final int ROLE_ROOT = 0; // Main verb
        public static final int ROLE_SBJ = 1; // Subject
        public static final int ROLE_OBJ = 2; // Object
        public static final int ROLE_PU = 3; // Punctuation
        public static final int ROLE_TMP = 4; // Time
        public static final int ROLE_LOC = 5; // Location
        public static final int ROLE_MNR = 6; // Adverb for verb
        public static final int ROLE_POBJ = 7; // Object (middle)
        public static final int ROLE_PMOD = 8; // Adverb for verb
        public static final int ROLE_NMOD = 9; // Adjective
        public static final int ROLE_VMOD = 10; // Adverb for verb
        public static final int ROLE_VRD = 11; // Verb (showing result)
        public static final int ROLE_DEG = 12; // Subject for "of"
        public static final int ROLE_DEV = 13; // Adverb for "ly"
        public static final int ROLE_LC = 14; // Location (structure)
        public static final int ROLE_M = 15; // number
        public static final int ROLE_AMOD = 16; // Adverb (for adjective)
        public static final int ROLE_PRN = 17; // word inside "()"
        public static final int ROLE_VC = 18; // Adverb for is
        public static final int ROLE_COOR = 19; // Relationship similar to "And"
        public static final int ROLE_CS = 20; // word after if
        public static final int ROLE_DEC = 21; // of

        private String text = "";
        private double head = Double.NaN;
        private int role = ROLE_NONE;

        public GrammarWord(String text, double head, int role) {
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

        public double getHead() {
            return head;
        }
    }

    private String token = "";

    @Deprecated
    public boolean init(Context context) {
        return super.init(context);
    }

    public boolean init(Context context, String token) {

        if (!super.init(context)) {
            return false;
        }

        this.token = token;

        return true;

    }

    private String getBranketSentenceInUtfRepresentation(String sentence) {

        if (TextUtils.isEmpty(sentence)) {
            return INPUT_PREFIX + INPUT_SUFFIX;
        }

        return INPUT_PREFIX + strToUtfRepresentation(sentence) + INPUT_SUFFIX;

    }

    private HashMap<String, String> getTokenedHeaders() {

        HashMap<String, String> result = new HashMap<>();
        result.put("X-Token", token);
        return result;

    }

    public void getKeyword(String sentence, final OnGetKeywordListener listener) {

        if (listener==null)
            return;

        httpPostString(GET_KEYWORD_LINK, getBranketSentenceInUtfRepresentation(sentence), getTokenedHeaders(), new OnHttpPostStringListener() {
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
                    for (int i = 0; i < jArray.length(); i++) {
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

    public void analyzeGrammar(String sentence, final OnAnalyzeGrammarListener listener) {

        if (listener==null)
            return;

        httpPostString(ANALYZE_GRAMMAR_LINK, getBranketSentenceInUtfRepresentation(sentence), getTokenedHeaders(), new OnHttpPostStringListener() {
            @Override
            public void onComplete(String responseStr) {
                ArrayList<GrammarWord> result = new ArrayList<>();
                boolean hasException = false;
                try {
                    JSONArray jArray = new JSONArray(responseStr);
                    JSONObject jObj = jArray.getJSONObject(0);
                    JSONArray textJArray = jObj.getJSONArray("word");
                    JSONArray headJArray = jObj.getJSONArray("head");
                    JSONArray roleJArray = jObj.getJSONArray("role");

                    String text = "";
                    double head = Double.NaN;
                    int role = GrammarWord.ROLE_NONE;
                    GrammarWord grammarWord = new GrammarWord(text, head, role);
                    for (int i = 0; i < textJArray.length(); i++) {
                        text = textJArray.getString(i);
                        head = headJArray.getDouble(i);
                        switch (roleJArray.getString(i)) {
                            case "ROOT":
                                role = GrammarWord.ROLE_ROOT;
                                break;
                            case "SBJ":
                                role = GrammarWord.ROLE_SBJ;
                                break;
                            case "OBJ":
                                role = GrammarWord.ROLE_OBJ;
                                break;
                            case "PU":
                                role = GrammarWord.ROLE_PU;
                                break;
                            case "TMP":
                                role = GrammarWord.ROLE_TMP;
                                break;
                            case "LOC":
                                role = GrammarWord.ROLE_LOC;
                                break;
                            case "MNR":
                                role = GrammarWord.ROLE_MNR;
                                break;
                            case "POBJ":
                                role = GrammarWord.ROLE_POBJ;
                                break;
                            case "PMOD":
                                role = GrammarWord.ROLE_PMOD;
                                break;
                            case "NMOD":
                                role = GrammarWord.ROLE_NMOD;
                                break;
                            case "VMOD":
                                role = GrammarWord.ROLE_VMOD;
                                break;
                            case "VRD":
                                role = GrammarWord.ROLE_VRD;
                                break;
                            case "DEG":
                                role = GrammarWord.ROLE_DEG;
                                break;
                            case "DEV":
                                role = GrammarWord.ROLE_DEV;
                                break;
                            case "LC":
                                role = GrammarWord.ROLE_LC;
                                break;
                            case "M":
                                role = GrammarWord.ROLE_M;
                                break;
                            case "AMOD":
                                role = GrammarWord.ROLE_AMOD;
                                break;
                            case "PRN":
                                role = GrammarWord.ROLE_PRN;
                                break;
                            case "VC":
                                role = GrammarWord.ROLE_VC;
                                break;
                            case "COOR":
                                role = GrammarWord.ROLE_COOR;
                                break;
                            case "CS":
                                role = GrammarWord.ROLE_CS;
                                break;
                            case "DEC":
                                role = GrammarWord.ROLE_DEC;
                                break;
                            default:
                                role = GrammarWord.ROLE_NONE;
                                break;
                        }

                        result.add(new GrammarWord(text, head, role));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    hasException = true;
                }

                listener.onComplete(hasException ? new ArrayList<GrammarWord>() : result);

            }
        });


    }


    public void getTimeInMillis(String sentence, final OnGetTimeInMillisListener listener) {

        if (listener == null) {
            return;
        }

        httpPostString(GET_TIME_IN_MILLIS_LINK_PREFIX + "?pattern=" +
                sentence
                        .replace("晏晝", "下午")
                        .replace("廿", "二十")
                        .replace("兩", "二"), "", getTokenedHeaders(), new OnHttpPostStringListener() {
            @Override
            public void onComplete(String responseStr) {

//                {
//                    "timestamp": "2013-02-28 00:00:00",
//                        "type": "timestamp"
//                }

                long timeInMillis = -1;
                boolean hasException = false;

                try {
                    JSONObject responseJObj = new JSONObject(responseStr);
                    String timeInStr = responseJObj.getString("timestamp");

                    // convert time in str to time in millis
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                    timeInMillis = sdf.parse(timeInStr).getTime();

                } catch (Exception e) {
                    e.printStackTrace();
                    hasException = true;
                }

                if (!hasException)
                    listener.onComplete(timeInMillis);
                else
                    listener.onError();


            }
        });


    }


}
