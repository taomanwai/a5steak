package com.tommytao.a5steak.util.ai;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.tommytao.a5steak.util.Foundation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.Executors;


/**
 * Responsible for BosonNLP
 */
public class BosonNlpManager extends Foundation {

    private static BosonNlpManager instance;

    public static BosonNlpManager getInstance() {

        if (instance == null)
            instance = new BosonNlpManager();

        return instance;
    }

    private BosonNlpManager() {

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

    public static interface OnGetAssociationListener {

        public void onComplete(ArrayList<Association> associations);

    }

    public static interface OnPartOfSpeechListener {

        public void onComplete(ArrayList<PartOfSpeechWord> partOfSpeechWords);

    }



    public static String GET_KEYWORD_LINK = "http://api.bosonnlp.com/keywords/analysis";
    public static String ANALYZE_GRAMMAR_LINK = "http://api.bosonnlp.com/depparser/analysis";
    public static String GET_ASSOCIATION_LINK = "http://api.bosonnlp.com/suggest/analysis";
    public static String GET_TIME_IN_MILLIS_LINK_PREFIX = "http://api.bosonnlp.com/time/analysis";
    public static String ANALYZE_PART_OF_SPEECH_LINK_PREFIX = "http://api.bosonnlp.com/tag/analysis";


    public static String INPUT_BRANKET_PREFIX = "[\"";
    public static String INPUT_BRANKET_SUFFIX = "\"]";

    public static String INPUT_QUOTATION_PREFIX = "\"";
    public static String INPUT_QUOTATION_SUFFIX = "\"";

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

    public class Association {

        private String text = "";
        private double weight = -1;

        public Association(String text, double weight) {
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

    /**
     * Ref: http://docs.bosonnlp.com/tag_rule.html
     */
    public class PartOfSpeechWord {

        public static final int TAG_NONE = -1; // None

        public static final int TAG_NOUN = 0; // noun
        public static final int TAG_NOUN_PEOPLE = 1; // people name
        public static final int TAG_NOUN_PEOPLE_FIRST_NAME = 2; // first name
        public static final int TAG_NOUN_PEOPLE_LAST_NAME = 3; // last name
        public static final int TAG_NOUN_LOCATION = 4; // location
        public static final int TAG_NOUN_ORGANIZATION = 5; // organization
        public static final int TAG_NOUN_PROPER = 6; // proper noun (專有名詞)
        public static final int TAG_NOUN_PHRASE = 7; // phrase (慣用語)

        public static final int TAG_TIME = 8; // time

        public static final int TAG_LOCATION = 9; // location

        public static final int TAG_DIRECTION = 10; // direction

        public static final int TAG_VERB = 11; // verb
        public static final int TAG_VERB_AUXILIARY = 12; // auxiliary (副動詞)
        public static final int TAG_VERB_IS = 13; // is
        public static final int TAG_VERB_HAVE = 14; // have
        public static final int TAG_VERB_VI = 15; // vi
        public static final int TAG_VERB_VT = 16; // vt

        public static final int TAG_ADJ = 17; // adjective
        public static final int TAG_ADJ_ADV = 18; // adverb style adj
        public static final int TAG_ADJ_NOUN = 19; // noun style adj
        public static final int TAG_ADJ_PHRASE = 20; // phrase (慣用語) adj

        public static final int TAG_DISTINGUISH = 21; // distinguish (區別詞)
        public static final int TAG_DISTINGUISH_PHRASE = 22; // distinguish phrase (慣用語)

        public static final int TAG_STATUS = 23; // status (狀態詞)

        public static final int TAG_PRONOUN = 24; // pronoun

        public static final int TAG_NUMBER = 25; // number

        public static final int TAG_MEASURE = 26; // measure (量詞 - e.g. 一*隻*)

        public static final int TAG_ADVERB = 27; // adverb
        public static final int TAG_ADVERB_PHRASE = 28; // adverb phrase (慣用語)
        public static final int TAG_PREPOSITION = 29; // preposition
        public static final int TAG_PREPOSITION_TO = 30; // to (把)
        public static final int TAG_PREPOSITION_BY = 31; // by (被)
        public static final int TAG_CONJUNCTION = 32; // conjunction (連接詞)

        public static final int TAG_PARTICLE = 33; // 助詞
        public static final int TAG_PARTICLE_ZHE = 34; // 著
        public static final int TAG_PARTICLE_LE = 35; // 了
        public static final int TAG_PARTICLE_GUO = 36; // 過
        public static final int TAG_PARTICLE_DE = 37; // 的, 地, 得
        public static final int TAG_PARTICLE_SUO = 38; // 所
        public static final int TAG_PARTICLE_DENG = 39; // 等
        public static final int TAG_PARTICLE_YY = 40; // 一樣, 似
        public static final int TAG_PARTICLE_DH = 41; // 的話
        public static final int TAG_PARTICLE_ZHI = 42; // 之
        public static final int TAG_PARTICLE_LIAN = 43; // 連

        public static final int TAG_PARTICLE_MODAL = 44; // 語氣詞

        public static final int TAG_ONOMATOPOEIA = 45; // 擬聲詞

        public static final int TAG_PREFIX = 46; // prefix (e.g. 非, 正常)
        public static final int TAG_SUFFIX = 47; // suffix (e.g. 孩子*們*, 隐藏*式*)

        public static final int TAG_STRING = 48; // string

        public static final int TAG_PUNCTUATION = 49; // punctuation
        public static final int TAG_PUNCTUATION_LEFT_BRACKET = 50; // left/open bracket
        public static final int TAG_PUNCTUATION_RIGHT_BRACKET = 51; // right/close bracket
        public static final int TAG_PUNCTUATION_OPEN_QUOTATION = 52; // open quotation
        public static final int TAG_PUNCTUATION_CLOSE_QUOTATION = 53; // close quotation
        public static final int TAG_PUNCTUATION_FULL_STOP = 54; // full stop
        public static final int TAG_PUNCTUATION_QUESTION_MARK = 55; // question mark
        public static final int TAG_PUNCTUATION_EXCLAMATION_MARK = 56; // exclamation
        public static final int TAG_PUNCTUATION_COMMA = 57; // comma
        public static final int TAG_PUNCTUATION_SEMICOLON = 58; // semicolon
        public static final int TAG_PUNCTUATION_IDEOGRAPHIC_COMMA = 59; // i.e. 、
        public static final int TAG_PUNCTUATION_COLON = 60; // colon
        public static final int TAG_PUNCTUATION_ELLIPSIS = 61; // ...
        public static final int TAG_PUNCTUATION_EM_DASH = 62; // ---
        public static final int TAG_PUNCTUATION_PERCENTAGE = 63; // %
        public static final int TAG_PUNCTUATION_UNIT = 64; // unit


        public static final int TAG_OTHER_EMAIL = 65; // email
        public static final int TAG_OTHER_TEL = 66; // tel
        public static final int TAG_OTHER_ID = 67; // id of id card of citizen
        public static final int TAG_OTHER_IP = 68; // ip address
        public static final int TAG_OTHER_URL = 69; // url

        private String text = "";
        private int tag = TAG_NONE;

        public PartOfSpeechWord(String text, int tag) {
            this.text = text;
            this.tag = tag;
        }

        public String getText() {
            return text;
        }

        public int getTag() {
            return tag;
        }

    }

    private String token = "";

    @Deprecated
    public boolean init(Context context) {
        return super.init(context);
    }

    public boolean init(Context context, String token) {

        if (!super.init(context))
            return false;


        this.token = token;

        return true;

    }

    private String getQuotatedSentenceInUtfRepresentation(String sentence) {

        if (TextUtils.isEmpty(sentence)) {
            return INPUT_QUOTATION_PREFIX + INPUT_QUOTATION_SUFFIX;
        }

        return INPUT_QUOTATION_PREFIX + strToUtfRepresentation(sentence) + INPUT_QUOTATION_SUFFIX;

    }

    private String getBranketSentenceInUtfRepresentation(String sentence) {

        if (TextUtils.isEmpty(sentence)) {
            return INPUT_BRANKET_PREFIX + INPUT_BRANKET_SUFFIX;
        }

        return INPUT_BRANKET_PREFIX + strToUtfRepresentation(sentence) + INPUT_BRANKET_SUFFIX;

    }

    private HashMap<String, String> getTokenedHeaders() {

        HashMap<String, String> result = new HashMap<>();
        result.put("X-Token", token);
        return result;

    }

    public void getKeyword(String sentence, final OnGetKeywordListener listener) {

        if (listener == null)
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

    public void analyzePartOfSpeech(String sentence, int oovLevel, final OnPartOfSpeechListener listener) {

        if (listener == null)
            return;

        String link = ANALYZE_PART_OF_SPEECH_LINK_PREFIX + "?oov_level=" + oovLevel;

        httpPostString(link, getQuotatedSentenceInUtfRepresentation(sentence), getTokenedHeaders(), new OnHttpPostStringListener() {
            @Override
            public void onComplete(String responseStr) {
                ArrayList<PartOfSpeechWord> result = new ArrayList<>();
                boolean hasException = false;
                try {
                    JSONArray jArray = new JSONArray(responseStr);
                    JSONObject jObj = jArray.getJSONObject(0);
                    JSONArray textJArray = jObj.getJSONArray("word");
                    JSONArray tagJArray = jObj.getJSONArray("tag");

                    String text = "";
                    int tag = PartOfSpeechWord.TAG_NONE;
                    PartOfSpeechWord partOfSpeechWord = new PartOfSpeechWord(text, tag);
                    for (int i = 0; i < textJArray.length(); i++) {
                        text = textJArray.getString(i);
                        switch (tagJArray.getString(i)) {
                            case "n":
                                tag = PartOfSpeechWord.TAG_NOUN;
                                break;
                            case "nr":
                                tag = PartOfSpeechWord.TAG_NOUN_PEOPLE;
                                break;
                            case "nr1":
                                tag = PartOfSpeechWord.TAG_NOUN_PEOPLE_LAST_NAME;
                                break;
                            case "nrf":
                                tag = PartOfSpeechWord.TAG_NOUN_PEOPLE_FIRST_NAME;
                                break;
                            case "ns":
                                tag = PartOfSpeechWord.TAG_NOUN_LOCATION;
                                break;
                            case "nt":
                                tag = PartOfSpeechWord.TAG_NOUN_ORGANIZATION;
                                break;
                            case "nz":
                                tag = PartOfSpeechWord.TAG_NOUN_PROPER;
                                break;
                            case "nl":
                                tag = PartOfSpeechWord.TAG_NOUN_PHRASE;
                                break;
                            case "t":
                                tag = PartOfSpeechWord.TAG_TIME;
                                break;
                            case "s":
                                tag = PartOfSpeechWord.TAG_LOCATION;
                                break;
                            case "f":
                                tag = PartOfSpeechWord.TAG_DIRECTION;
                                break;
                            case "v":
                                tag = PartOfSpeechWord.TAG_VERB;
                                break;
                            case "vd":
                                tag = PartOfSpeechWord.TAG_VERB_AUXILIARY;
                                break;
                            case "vshi":
                                tag = PartOfSpeechWord.TAG_VERB_IS;
                                break;
                            case "vyou":
                                tag = PartOfSpeechWord.TAG_VERB_HAVE;
                                break;
                            case "vi":
                                tag = PartOfSpeechWord.TAG_VERB_VI;
                                break;
                            case "vl":
                                tag = PartOfSpeechWord.TAG_VERB_VT;
                                break;
                            case "a":
                                tag = PartOfSpeechWord.TAG_ADJ;
                                break;
                            case "ad":
                                tag = PartOfSpeechWord.TAG_ADJ_ADV;
                                break;
                            case "an":
                                tag = PartOfSpeechWord.TAG_ADJ_NOUN;
                                break;
                            case "al":
                                tag = PartOfSpeechWord.TAG_ADJ_PHRASE;
                                break;
                            case "b":
                                tag = PartOfSpeechWord.TAG_DISTINGUISH;
                                break;
                            case "bl":
                                tag = PartOfSpeechWord.TAG_DISTINGUISH_PHRASE;
                                break;
                            case "z":
                                tag = PartOfSpeechWord.TAG_STATUS;
                                break;
                            case "r":
                                tag = PartOfSpeechWord.TAG_PRONOUN;
                                break;
                            case "m":
                                tag = PartOfSpeechWord.TAG_NUMBER;
                                break;
                            case "q":
                                tag = PartOfSpeechWord.TAG_MEASURE;
                                break;
                            case "d":
                                tag = PartOfSpeechWord.TAG_ADVERB;
                                break;
                            case "dl":
                                tag = PartOfSpeechWord.TAG_ADVERB_PHRASE;
                                break;
                            case "p":
                                tag = PartOfSpeechWord.TAG_PREPOSITION;
                                break;
                            case "pba":
                                tag = PartOfSpeechWord.TAG_PREPOSITION_TO;
                                break;
                            case "pbei":
                                tag = PartOfSpeechWord.TAG_PREPOSITION_BY;
                                break;
                            case "c":
                                tag = PartOfSpeechWord.TAG_CONJUNCTION;
                                break;
                            case "u":
                                tag = PartOfSpeechWord.TAG_PARTICLE;
                                break;
                            case "uzhe":
                                tag = PartOfSpeechWord.TAG_PARTICLE_ZHE;
                                break;
                            case "ule":
                                tag = PartOfSpeechWord.TAG_PARTICLE_LE;
                                break;
                            case "uguo":
                                tag = PartOfSpeechWord.TAG_PARTICLE_GUO;
                                break;
                            case "ude":
                                tag = PartOfSpeechWord.TAG_PARTICLE_DE;
                                break;
                            case "usuo":
                                tag = PartOfSpeechWord.TAG_PARTICLE_SUO;
                                break;
                            case "udeng":
                                tag = PartOfSpeechWord.TAG_PARTICLE_DENG;
                                break;
                            case "uyy":
                                tag = PartOfSpeechWord.TAG_PARTICLE_YY;
                                break;
                            case "udh":
                                tag = PartOfSpeechWord.TAG_PARTICLE_DH;
                                break;
                            case "uzhi":
                                tag = PartOfSpeechWord.TAG_PARTICLE_ZHI;
                                break;
                            case "ulian":
                                tag = PartOfSpeechWord.TAG_PARTICLE_LIAN;
                                break;
                            case "y":
                                tag = PartOfSpeechWord.TAG_PARTICLE_MODAL;
                                break;
                            case "o":
                                tag = PartOfSpeechWord.TAG_ONOMATOPOEIA;
                                break;
                            case "h":
                                tag = PartOfSpeechWord.TAG_PREFIX;
                                break;
                            case "k":
                                tag = PartOfSpeechWord.TAG_SUFFIX;
                                break;
                            case "nx":
                                tag = PartOfSpeechWord.TAG_STRING;
                                break;
                            case "w":
                                tag = PartOfSpeechWord.TAG_PUNCTUATION;
                                break;
                            case "wkz":
                                tag = PartOfSpeechWord.TAG_PUNCTUATION_LEFT_BRACKET;
                                break;
                            case "wky":
                                tag = PartOfSpeechWord.TAG_PUNCTUATION_RIGHT_BRACKET;
                                break;
                            case "wyz":
                                tag = PartOfSpeechWord.TAG_PUNCTUATION_OPEN_QUOTATION;
                                break;
                            case "wyy":
                                tag = PartOfSpeechWord.TAG_PUNCTUATION_CLOSE_QUOTATION;
                                break;
                            case "wj":
                                tag = PartOfSpeechWord.TAG_PUNCTUATION_FULL_STOP;
                                break;
                            case "ww":
                                tag = PartOfSpeechWord.TAG_PUNCTUATION_QUESTION_MARK;
                                break;
                            case "wt":
                                tag = PartOfSpeechWord.TAG_PUNCTUATION_EXCLAMATION_MARK;
                                break;
                            case "wd":
                                tag = PartOfSpeechWord.TAG_PUNCTUATION_COMMA;
                                break;
                            case "wf":
                                tag = PartOfSpeechWord.TAG_PUNCTUATION_SEMICOLON;
                                break;
                            case "wn":
                                tag = PartOfSpeechWord.TAG_PUNCTUATION_IDEOGRAPHIC_COMMA;
                                break;
                            case "wm":
                                tag = PartOfSpeechWord.TAG_PUNCTUATION_COLON;
                                break;
                            case "ws":
                                tag = PartOfSpeechWord.TAG_PUNCTUATION_ELLIPSIS;
                                break;
                            case "wp":
                                tag = PartOfSpeechWord.TAG_PUNCTUATION_EM_DASH;
                                break;
                            case "wb":
                                tag = PartOfSpeechWord.TAG_PUNCTUATION_PERCENTAGE;
                                break;
                            case "wh":
                                tag = PartOfSpeechWord.TAG_PUNCTUATION_UNIT;
                                break;
                            case "email":
                                tag = PartOfSpeechWord.TAG_OTHER_EMAIL;
                                break;
                            case "tel":
                                tag = PartOfSpeechWord.TAG_OTHER_TEL;
                                break;
                            case "id":
                                tag = PartOfSpeechWord.TAG_OTHER_ID;
                                break;
                            case "ip":
                                tag = PartOfSpeechWord.TAG_OTHER_IP;
                                break;
                            case "url":
                                tag = PartOfSpeechWord.TAG_OTHER_URL;
                                break;

                            default:
                                tag = PartOfSpeechWord.TAG_NONE;
                                break;
                        }

                        result.add(new PartOfSpeechWord(text, tag));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    hasException = true;
                }

                listener.onComplete(hasException ? new ArrayList<PartOfSpeechWord>() : result);
            }
        });

    }


    public void analyzeGrammar(String sentence, final OnAnalyzeGrammarListener listener) {

        if (listener == null)
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

        if (listener == null)
            return;

        // TODO can optimize replace algorithm
        sentence = sentence
                .replace("晏晝", "下午")
                .replace("廿", "二十")
                .replace("兩", "二")
                .replace("點搭一", "點05分")
                .replace("點搭二", "點10分")
                .replace("點搭三", "點15分")
                .replace("點搭四", "點20分")
                .replace("點搭五", "點25分")
                .replace("點搭六", "點30分")
                .replace("點搭七", "點35分")
                .replace("點搭八", "點40分")
                .replace("點搭九", "點45分")
                .replace("點搭十", "點50分")
                .replace("點搭十一", "點55分");

        sentence = tcToSc(sentence);

        httpPostString(GET_TIME_IN_MILLIS_LINK_PREFIX + "?pattern=" + sentence
                , "", getTokenedHeaders(), new OnHttpPostStringListener() {
            @Override
            public void onComplete(String responseStr) {


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

    public void getAssociation(String sentence, final OnGetAssociationListener listener) {

        if (listener == null)
            return;


        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {

                if (params.length != 1)
                    return "";

                return tcToSc(params[0]);

            }

            @Override
            protected void onPostExecute(String result) {

                httpPostString(GET_ASSOCIATION_LINK, getQuotatedSentenceInUtfRepresentation(result), getTokenedHeaders(), new OnHttpPostStringListener() {
                    @Override
                    public void onComplete(String responseStr) {

                        new AsyncTask<String, Void, ArrayList<Association>>() {

                            @Override
                            protected ArrayList<Association> doInBackground(String... params) {

                                ArrayList<Association> result = new ArrayList<>();

                                if (params.length != 1)
                                    return result;

                                boolean hasException = false;
                                try {
                                    JSONArray jArray = new JSONArray(params[0]);

                                    String text = "";
                                    double weight = -1;
                                    Association association = new Association(text, weight);
                                    for (int i = 0; i < jArray.length(); i++) {
                                        weight = jArray.getJSONArray(i).getDouble(0);
                                        text = jArray.getJSONArray(i).getString(1);
                                        text = text.replace("/n", "");
                                        text = scToTc(text);
                                        association = new Association(text, weight);
                                        result.add(association);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    hasException = true;
                                }

                                return (hasException ? new ArrayList<Association>() : result);

                            }

                            @Override
                            protected void onPostExecute(ArrayList<Association> result) {

                                listener.onComplete(result);

                            }

                        }.executeOnExecutor(Executors.newCachedThreadPool(), responseStr);

                    }
                });

            }

        }.executeOnExecutor(Executors.newCachedThreadPool(), sentence);


    }

    public ArrayList<String> getHkStopsInDistrict(String text) {

        text = text.replace("湧", "涌").replace("砲", "炮").replace("舂磡角", "舂坎角").replace("洛馬洲", "落馬洲")
                .replace("樂馬洲", "落馬洲").replace("大塘", "大棠").replace("蠍涌", "蛹涌").replace("濠", "蠔")
                .replace("杏花村", "杏花邨").replace("馬遊塘", "馬游塘").replace("馬油塘", "馬游塘");

        String[] resultInStrArray = text.split("去");

        if (resultInStrArray == null) {
            return new ArrayList<>();
        }

        ArrayList<String> result = new ArrayList<>();
        for (String resultInStr : resultInStrArray){
            result.add(resultInStr);
        }

        return result;


    }


}
