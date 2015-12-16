package com.tommytao.a5steak.system;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.common.Foundation;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class DeviceInfoManager extends Foundation {

    private static DeviceInfoManager instance;

    public static DeviceInfoManager getInstance() {

        if (instance == null)
            instance = new DeviceInfoManager();

        return instance;
    }

    private DeviceInfoManager() {

        super();

        log("device_info_manager: " + "create");

    }

    // --


    private String deviceId = "";

    public String getDeviceId() {

        if (deviceId.isEmpty()) {
            TelephonyManager tm = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId() + "-" + android.os.Build.SERIAL;
        }

        return deviceId;

    }

    // --
    public String getGmailUsername() {
        AccountManager manager = AccountManager.get(appContext);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0];
        }

        return "";
    }

    @Override
    public boolean init(Context context) {

        if (!super.init(context)) {

            log("device_info_manager: " + "init REJECTED: already initialized");

            return false;

        }

        log("device_info_manager: " + "init");

        return true;

    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }


    private int screenDpi;

    private Point screenSize;

    public int getScreenDpi() {

        if (screenDpi == 0) {
            DisplayMetrics metrics = appContext.getResources().getDisplayMetrics();
            screenDpi = metrics.densityDpi;
        }

        return screenDpi;

    }

    public Point getScreenSize() {


        if (screenSize == null) {
            Display display = ((WindowManager) (appContext
                    .getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay();
            screenSize = new Point();

            display.getSize(screenSize);

        }


        return new Point(screenSize);

    }

    public Point getScreenSizeInDp() {

        Point result = getScreenSize();

        double ratio = (double) this.getScreenDpi() / 160;

        result.x /= ratio;
        result.y /= ratio;

        return result;

    }

    public int getAndroidApiLevel() {
        return Build.VERSION.SDK_INT;
    }

    public int getStatusBarHeight(Activity activity) {
        int statusBarHeight = 0;

        if (!isSystemBarVisible(activity)) {
            int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
            }
        }

        return statusBarHeight;
    }

    public boolean isSystemBarVisible(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        int rawDisplayHeight = 0;
        try {
            Method getRawHeight = Display.class.getMethod("getRawHeight");
            rawDisplayHeight = (Integer) getRawHeight.invoke(display);
        } catch (Exception ex) {
        }

        int UIRequestedHeight = display.getHeight();

        return rawDisplayHeight - UIRequestedHeight > 0;
    }


}
