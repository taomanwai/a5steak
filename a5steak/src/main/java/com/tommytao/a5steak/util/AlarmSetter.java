package com.tommytao.a5steak.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.common.util.Foundation;

public class AlarmSetter extends Foundation {

    private static AlarmSetter instance;

    public static AlarmSetter getInstance() {

        if (instance == null)
            instance = new AlarmSetter();

        return instance;
    }

    private AlarmSetter() {

    }


    // --

    @Override
    public boolean init(Context context) {
        return super.init(context);
    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }

    protected AlarmManager am;

    protected AlarmManager getAlarmManager() {

        if (am == null)
            this.am = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);

        return am;
    }

    public void cancel(PendingIntent operation) {

        this.getAlarmManager().cancel(operation);


    }

    /**
     * Generate unique request code, to avoid a bug (i.e. to make PendingIntent unique even though their action, class, data, type and category are different)
     * <p/>
     * Ref: http://stackoverflow.com/questions/3730258/mulitple-instances-of-pending-intent
     *
     * @return Unique request code
     */
    private int genUniqueRequestCode() {

        return genUniqueId();

    }

    private PendingIntent set(long timeInMillis, String broadcastAction, Class<?> cls, boolean activityClearTop, Bundle bundle, int requestCode) {

        if (broadcastAction == null)
            broadcastAction = "";

        boolean broadcastMode = (cls == null);

        if (broadcastMode && broadcastAction.isEmpty())
            return null;

        if (requestCode < 0)
            requestCode = genUniqueRequestCode();

        Intent intent = broadcastMode ? new Intent(broadcastAction) : new Intent(appContext, cls);

        if (bundle != null)
            intent.putExtras(bundle);

        PendingIntent operation = null;

        if (broadcastMode)
            operation = PendingIntent.getBroadcast(appContext, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (Service.class.isAssignableFrom(cls))
            operation = PendingIntent.getService(appContext, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        else if (Activity.class.isAssignableFrom(cls)) {
            if (activityClearTop)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            operation = PendingIntent.getActivity(appContext, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        if (operation == null)
            return null;

        this.getAlarmManager().set(AlarmManager.RTC_WAKEUP, timeInMillis, operation);

        return operation;

    }

    public PendingIntent setActivity(long timeInMillis, Class<?> cls, boolean clearTop, Bundle bundle, int requestCode) {

        log("alarm: " + "set: " + timeInMillis + " for activity");

        return set(timeInMillis, "", cls, clearTop, bundle, requestCode);
    }

    public PendingIntent setBroadcast(long timeInMillis, String broadcastAction, Bundle bundle, int requestCode) {

        log("alarm: " + "set: " + timeInMillis + " for action " + broadcastAction);

        return set(timeInMillis, broadcastAction, null, false, bundle, requestCode);
    }

    public PendingIntent setService(long timeInMillis, Class<?> cls, Bundle bundle, int requestCode) {

        log("alarm: " + "set: " + timeInMillis + " for service");

        return set(timeInMillis, "", cls, false, bundle, requestCode);

    }


}
