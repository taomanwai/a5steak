package com.tommytao.a5steak.misc;

import android.content.Context;
import android.util.Base64;
import android.util.DisplayMetrics;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.common.Foundation;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class Converter extends Foundation {

    private static Converter instance;

    public static Converter getInstance() {

        if (instance == null)
            instance = new Converter();

        return instance;
    }

    private Converter() {

    }


    // --

    private DateFormat ddMMyyyyDateFormat = null;

    @Override
    public boolean init(Context context) {
        return super.init(context);
    }


    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }

    public String timeInSecond2FriendlyStr(int timeInSecond, String minUnit, String secondUnit) {

        if (timeInSecond == -1)
            return "NA" + secondUnit;

        int minute = timeInSecond / 60;
        int second = timeInSecond % 60;

        return (minute == 0) ? (second + secondUnit) : (minute + minUnit + second + secondUnit);


    }

    public String urlTagTextToHtml(String text) {

        return "" + text.replaceAll("\\[url\\](\\S+)\\[\\/url\\]", "<a href=\"$1\">$1</a>") + "";

    }


    private DateFormat getDdMMyyyyDateFormat() {


        if (ddMMyyyyDateFormat == null)
            ddMMyyyyDateFormat = new SimpleDateFormat("dd/MM/yyyy");


        return ddMMyyyyDateFormat;

    }

    public String intToFormattedNumberStr(int value) {
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }


    public String time2DdMMyyyy(long time) {

        String result = "";

        try {
            result = getDdMMyyyyDateFormat().format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    public int res2ResId(Context ctx, String resType, String resName) {
        try {
            return ctx.getResources().getIdentifier(resName, resType, ctx.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    public String lengthInMeter2FriendlyStr(int lengthInMeter, String kmUnit, String meterUnit) {

        if (lengthInMeter == -1)
            return "NA" + meterUnit;

        int km = lengthInMeter / 1000;
        int meter = lengthInMeter % 1000;

        return (km == 0) ? (meter + meterUnit) : (String.format("%.2f", km + (double) meter / 1000) + kmUnit);

    }

    private int getScreenDpi() {
        DisplayMetrics metrics = appContext.getResources().getDisplayMetrics();
        return metrics.densityDpi;

    }

    /**
     * Convert dp to px
     *
     * @param dp Value of dp
     * @return Value of pixel (px)
     */
    public int dpToPx(int dp) {

        return (int) (dp * getScreenDpi() / 160);

    }

    public int pxToDp(int px) {

        return (int) (px * 160 / getScreenDpi());

    }

    public ArrayList<String> strArray2StrList(String[] strArray) {

        ArrayList<String> result = new ArrayList<String>();

        for (String str : strArray)
            result.add(str);

        return result;


    }

    public String strList2CSV(ArrayList<String> strList) {
        String csv = "";

        if (strList != null) {
            int i = 0;
            for (String str : strList) {
                csv += str
                        + ((i == (strList.size() - 1)) ? "" : ",");
                i++;
            }
        }

        return csv;
    }


    @Override
    public String resIdToName(int resId, boolean fullName) {
        return super.resIdToName(resId, fullName);
    }

    public String numOfStarsToStarsStr(int numOfStars) {

        StringBuffer sbStarsStr = new StringBuffer();
        for (int i = 0; i < numOfStars; i++) {
            sbStarsStr.append("★");
        }

        return sbStarsStr.toString();

    }

    public String htmlToText(String html) {

        return super.htmlToText(html);


    }





    public byte[] hexRepresentationToByteArray(String input) {

        return super.hexRepresentationToByteArray(input);


    }

    public String byteArrayToHexRepresentation(byte[] bytes) {

        return super.byteArrayToHexRepresentation(bytes);

    }

    public String colorToHexRepresentation(int color) {

        return Integer.toHexString(color).toUpperCase().substring(2);

    }

    @Override
    public String strToUtfRepresentation(String text) {
        return super.strToUtfRepresentation(text);
    }

    @Override
    public String tcToSc(String text) {
        return super.tcToSc(text);
    }

    @Override
    public String scToTc(String text) {
        return super.scToTc(text);
    }


    public String strToBase64(String input){

        byte[] encode = Base64.encode(input.getBytes(), Base64.DEFAULT);

        return new String(encode);

    }

    public String base64ToStr(String input){

        byte[] result = Base64.decode(input, Base64.DEFAULT);

        return new String(result);

    }

}
