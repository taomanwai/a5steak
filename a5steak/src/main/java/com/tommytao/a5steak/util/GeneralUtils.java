package com.tommytao.a5steak.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class GeneralUtils {

    public static String replaceStrokeNByEnter(String input, String replacement) {
        if (input == null)
            return "";

        return input.replace("\\n", replacement);
    }

    public static String genGMapLink(double latitude, double longitude) {
        return "https://maps.google.com.hk/maps?q=" + String.format("%.6f", latitude) + "," + String.format("%.6f", longitude);
    }

    public static Spanned genHyperLink(String label, String link) {
        return Html.fromHtml("<a href=\"" + link + "\">" + label + "</a>");
    }

//    private static String appendLocaleToFieldNameInGlobalStyle(String fieldName, String lang, String country, boolean forceCountryToBeLowerCase) {
//        return fieldName + (fieldName.isEmpty() ? "" : "_") + lang + "_" + (forceCountryToBeLowerCase ? country.toLowerCase(Locale.US) : country);
//    }


    public static long timeToNextNextOneSixthHourTime(long time) {

        final int INTERVAL_IN_MIN = 10;

        Time timeObj = new Time();

        timeObj.set(time);

        int normalizedMin = ((int) (Math.floor((double) timeObj.minute / INTERVAL_IN_MIN) * INTERVAL_IN_MIN));

        timeObj.minute = normalizedMin;
        timeObj.second = 0;


        long result = timeObj.toMillis(true) + 2 * (INTERVAL_IN_MIN * 60 * 1000); // true
        // means
        // ignore
        // day-saving-time
        // ,
        // China
        // has
        // no
        // day-saving-time


        return result;


    }

//    private static String appendLocaleToFieldNameInHKStyle(String fieldName, String lang) {
//        return fieldName + (fieldName.isEmpty() ? "" : "_") + lang;
//    }

    public static String appendLocaleToFieldName(String fieldName) {

//        return "HK".equalsIgnoreCase(country) ? GeneralUtils.appendLocaleToFieldNameInHKStyle(hasFieldNameInHKStyle ? fieldName : "", lang) : GeneralUtils
//                .appendLocaleToFieldNameInGlobalStyle(fieldName, lang, country, forceCountryToBeLowerCase);

        return fieldName.isEmpty() ? "zh_cn" : (fieldName + "_zh_cn");

    }

    public static String translateNotifierLocation(String notifierlocation) {
        String result = notifierlocation;

        result = result.replace("\",\"", " > ");
        result = result.replace("[", "");
        result = result.replace("]", "");
        result = result.replace("\"", "");
        return result;
    }


    public static String shortenStr(String s, int len, String appendStr) {

        if (s == null || s.isEmpty())
            return "";

        if (s.length() < len)
            return s;

        return s.substring(0, len) + appendStr;

    }

    public static String shortenStrBasedOnScreenWidthInDp(String s, int screenWidthInDp, String appendStr) {

        int len = 6;

        len *= (double) screenWidthInDp / 411;

        // TODO MVP
        len *= 1.9;


        return shortenStr(s, len, appendStr);

    }

    public static String genTimeDescStr(long timestamp, String morning, String afternoon, String night, String midnight) {


        // get hour
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);


        if (7 <= hour && hour < 12)
            return morning;

        if (12 <= hour && hour < 21)
            return afternoon;

        if (21 <= hour && hour < 24)
            return night;

        if (0 <= hour && hour < 7)
            return midnight;


        return "";



//        LocalTime ref = new LocalTime(timestamp);
//        LocalTime lt1 = new LocalTime(7, 00);
//        LocalTime lt2 = new LocalTime(12, 00);
//        LocalTime lt3 = new LocalTime(21, 00);
//
//        if (ref.isBefore(lt1)) { // 0-7
//            return ctx.getString(R.string.order__time__midnight);
//        } else if (ref.isBefore(lt2) && ref.isAfter(lt1)) { // 7-12
//            return ctx.getString(R.string.order__time__morning);
//        } else if (ref.isBefore(lt3) && ref.isAfter(lt2)) { //12-21
//            return ctx.getString(R.string.order__time__afternoon);
//        } else if (ref.isAfter(lt3)) {//21-24
//            return ctx.getString(R.string.order__time__night);
//        } else {
//            return "";
//        }

    }

    public static long calculateNoOfDaysFromToday(long timestamp) {

        Calendar timestampCalendar = Calendar.getInstance();
        timestampCalendar.setTimeInMillis(timestamp);
        timestampCalendar.set(Calendar.HOUR_OF_DAY, 0);
        timestampCalendar.set(Calendar.MINUTE, 0);
        timestampCalendar.set(Calendar.SECOND, 0);
        timestampCalendar.set(Calendar.MILLISECOND, 0);

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(System.currentTimeMillis());
        currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
        currentCalendar.set(Calendar.MINUTE, 0);
        currentCalendar.set(Calendar.SECOND, 0);
        currentCalendar.set(Calendar.MILLISECOND, 0);


        return (timestampCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis()) / (24 * 60 * 60 * 1000);


    }

    public static String genDateDescStr(long timestamp, String yesterday, String today, String tomorrow, String timeFormat) {


        long noOfDaysFromToday = calculateNoOfDaysFromToday(timestamp);

        if (noOfDaysFromToday == -1)
            return yesterday;

        if (noOfDaysFromToday == 0)
            return today;

        if (noOfDaysFromToday == 1)
            return tomorrow;

        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        return sdf.format(new Date(timestamp));


    }


    public static String uriToFileLink(Context ctx, Uri uri) {

        if (uri == null)
            return "";

        String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.ORIENTATION};

        Cursor cursor = ctx.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA /*
                                                                                             * filePathColumn
																							 * [
																							 * 0
																							 * ]
																							 */);

        return cursor.getString(dataColumnIndex);


    }

}
