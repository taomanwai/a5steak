package com.tommytao.a5steak.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for app stuff (e.g. get version code / name, force/voluntary
 * update, etc.)
 *
 * @author tommytao
 */
public class AppManager extends Foundation {

    private static AppManager instance;

    public static AppManager getInstance() {

        if (instance == null)
            instance = new AppManager();

        return instance;
    }

    private AppManager() {

        super();

        log("app_manager: " + "create");

    }

    // --

    public static interface Listener {

        /**
         * Triggered when APK is downloaded
         *
         * @param file Downloaded file
         * @return Return value defined by programmer; TRUE: install APK after
         * download completed; FALSE: do nothing
         */
        public void onDownloaded(File file);

        /**
         * Triggered when APK is downloading
         *
         * @param percentage Percentage of progress
         */
        public void onDownloading(int percentage);

    }

    private boolean downloadingLatestAppApk;

    public final static String PREFS_LATEST_RECORDED_VERSION_CODE = "PlacedOrderCounter.PREFS_LATEST_RECORDED_VERSION_CODE";

    public static final String MARKET_PREFIX = "market://details?id=";
    public static final String HTTP_PREFIX = "http://play.google.com/store/apps/details?id=";

    public static final String LATEST_APP_APK = "latest.apk";

    private boolean newInstall;

    private boolean justUpdated;

    @Override
    public boolean init(Context appContext) {

        if (!super.init(appContext)) {
            log("app_manager: " + "init REJECTED: already initialized");
            return false;
        }

        log("app_manager: " + "init");

        int versionCodeInPref = PreferenceManager.getDefaultSharedPreferences(appContext).getInt(PREFS_LATEST_RECORDED_VERSION_CODE, -1);

        if (versionCodeInPref == -1)
            newInstall = true;
        else if (this.getVersionCode() > versionCodeInPref)
            justUpdated = true;

        PreferenceManager.getDefaultSharedPreferences(appContext).edit().putInt(PREFS_LATEST_RECORDED_VERSION_CODE, this.getVersionCode()).apply();

        return true;

    }

    public void setManifestReceiverEnabled(Class<?> cls, boolean enabled) {

        if (cls == null)
            return;

        if (!BroadcastReceiver.class.isAssignableFrom(cls))
            return;

        ComponentName componentName = new ComponentName(appContext, cls);

        PackageManager packageManager = appContext.getPackageManager();

        packageManager.setComponentEnabledSetting(componentName, enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

    }

    public void reportJustUpgradeFromAppWithoutAppManager() {
        newInstall = false;
        justUpdated = true;
    }

    public boolean isNewInstall() {
        return newInstall;
    }

    public boolean isJustUpdated() {
        return justUpdated;
    }

    private PackageInfo packageInfo;

    private PackageInfo getPackageInfo() {

        if (packageInfo == null) {

            try {
                packageInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
            } catch (NameNotFoundException e) {

            }
        }

        return packageInfo;
    }

    public String getPackageName() {

        return appContext.getPackageName();

    }

    public String getAppName() {
        return "" + getPackageInfo().applicationInfo.loadLabel(appContext.getPackageManager());
    }

    public int getPid() {

        return android.os.Process.myPid();

    }

    public int getVersionCode() {

        if (getPackageInfo() == null)
            return -1;

        return getPackageInfo().versionCode;
    }

    public String getVersionName() {

        if (getPackageInfo() == null)
            return "";

        return getPackageInfo().versionName;

    }

    private Bundle getMetaDataBundleFromManifest() {

        Bundle bundle = null;

        try {
            ApplicationInfo applicationInfo = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(), PackageManager.GET_META_DATA);
            bundle = applicationInfo.metaData;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bundle;
    }

    public String getMetaDataStringFromManifest(String name) {

        String result = "";

        try {
            result = getMetaDataBundleFromManifest().getString(name);
            result = (result == null || result.equals("null")) ? "" : result;
        } catch (Exception e) {
            e.printStackTrace();
            result = "";
        }

        return result;
    }

    public boolean getMetaDataBooleanFromManifest(String name) {

        try {
            return getMetaDataBundleFromManifest().getBoolean(name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    public boolean isAppInstalled(String pkgName) {

        boolean appFound = false;
        try {
            appContext.getPackageManager().getApplicationInfo(pkgName, 0);
            appFound = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appFound;

    }


    public int pkgName2Pid(String pkgName) {
        int result = -1;

        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);

        if (am != null) {
            for (ActivityManager.RunningAppProcessInfo pi : am.getRunningAppProcesses()) {

                if (pi.processName.equalsIgnoreCase(pkgName))
                    result = pi.pid;

                if (result != -1) break;

            }
        } else {
            result = -1;
        }

        return result;
    }

    public void showUpdateDialog(final Activity activity, final String title, final String msg, boolean forcely,
                                 final DialogInterface.OnClickListener positiveButtonListener, final DialogInterface.OnClickListener negativeButtonListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title).setMessage(msg).setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (positiveButtonListener != null)
                    positiveButtonListener.onClick(dialog, which);

            }

        });

        if (!forcely)
            builder.setNegativeButton(android.R.string.cancel, negativeButtonListener);

        builder.show();

    }

    public void uninstall() {
        Uri uri = Uri.parse("package:" + getPackageName());
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appContext.startActivity(intent);
    }

    public void installApk(String pathOfApk) {

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(pathOfApk)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        appContext.startActivity(intent);

    }

    public boolean installDownloadedLatestAppApk() {

        String pathOfApk = appContext.getExternalFilesDir(null).getAbsolutePath() + File.separator + LATEST_APP_APK;

        File file = new File(pathOfApk);

        if (!file.exists())
            return false;

        installApk(pathOfApk);

        return true;

    }

    private void triggerListener(final Listener listener, final File file) {


        if (listener == null)
            return;

        handler.post(new Runnable() {

            @Override
            public void run() {
                listener.onDownloaded(file);

            }

        });
    }

    public boolean isDownloadingLatestAppApk() {
        return downloadingLatestAppApk;
    }

    /**
     * @param link Link of APK
     */
    public void downloadLatestAppApk(String link, final Listener listener) {

        if (isDownloadingLatestAppApk())
            triggerListener(listener, null);

        downloadingLatestAppApk = true;

        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {


            downloadingLatestAppApk = false;
            triggerListener(listener, null);

            return;
        }

        httpGetFile(link, 1, appContext.getExternalFilesDir(null).getAbsolutePath(), LATEST_APP_APK, new Foundation.OnHttpGetFileListener() {

            @Override
            public void onDownloading(int percentage) {

                if (listener != null)
                    listener.onDownloading(percentage);

            }

            @Override
            public void onDownloaded(File file) {

                downloadingLatestAppApk = false;
                listener.onDownloaded(file);

            }

        });

    }

    public void killProcess(String pkgName) {
        int pid = pkgName2Pid(pkgName);
        android.os.Process.killProcess(pid);
    }

    public void forceClose() {
        System.exit(0);
    }

    /**
     * Ref: http://stackoverflow.com/questions/6609414/howto-programatically-
     * restart-android-app
     *
     * @return TRUE: Succeed (Actually, if succeed, app should be force closed
     * and this funct. will never return true! :)); FALSE: Failed
     */
    public boolean forceRestart() {

        PackageManager pm = appContext.getPackageManager();

        if (pm == null)
            return false;

        Intent intent = pm.getLaunchIntentForPackage(appContext.getPackageName());

        if (intent == null)
            return false;

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // create a pending intent so the application is
        // restarted after System.exit(0) was called.
        // We use an AlarmManager to call this intent in 100ms
        int mPendingIntentId = 223344;
        PendingIntent mPendingIntent = PendingIntent.getActivity(appContext, mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        // kill the application
        System.exit(0);

        return true;

    }

    public ArrayList<String> getInstalledApps() {
        ArrayList<String> result = new ArrayList<>();

        List<PackageInfo> packs = appContext.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (p.versionName == null)
                continue;

            result.add(p.packageName);

        }
        return result;
    }

}
