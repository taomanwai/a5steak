package com.tommytao.a5steak.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

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
    public boolean init(Context appContext) {

        if (!super.init(appContext)) {

            log("device_info_manager: " + "init REJECTED: already initialized");

            return false;

        }

        log("device_info_manager: " + "init");

        return true;

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
//			Display display = ctx.getWindowManager().getDefaultDisplay();
            Display display = ((WindowManager) (appContext
                    .getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay();
            screenSize = new Point();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
                display.getSize(screenSize);
            else {
                screenSize.x = display.getWidth();
                screenSize.y = display.getHeight();
            }

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


}
