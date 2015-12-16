package com.tommytao.a5steak.alarm.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 *
 * Base class for Services that handle requests (inside onLoop()) in looping style
 *
 * Warning: Permission android.permission.WAKE_LOCK is required
 *
 * Note: In Android 5.1 or above, AlarmManager cannot be set lower than 1 min.
 *
 * Ref: https://commonsware.com/blog/2015/03/23/alarmmanager-regression-android-5p1.html
 *
 * Created by tommytao on 10/4/15.
 */
public abstract class LoopingService extends Service {

    public static class Loader {

        public static void load(Context ctx, Class<?> cls) {
            ctx.getApplicationContext().startService(new Intent(ctx.getApplicationContext(), cls));
        }

        public static void unload(Context ctx, Class<?> cls) {
            ((AlarmManager) ctx.getApplicationContext().getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getService(ctx.getApplicationContext(), LoopingService.REQUEST_CODE, new Intent(ctx.getApplicationContext(), cls), PendingIntent.FLAG_CANCEL_CURRENT));
        }

    }

    public static final int REQUEST_CODE = 987876; // picked by random

    public abstract void onLoop(int startId);

    public abstract int getTimeIntervalInMs();

    public abstract int getMaxRandomDerivationOfTimeIntervalInMs();



    protected void stopServiceForStartId(int startId) {
        stopSelf(startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ((AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + getTimeIntervalInMs() + (int) (Math.random() * getMaxRandomDerivationOfTimeIntervalInMs()), PendingIntent.getService(this.getApplicationContext(), REQUEST_CODE, new Intent(this.getApplicationContext(), this.getClass()), PendingIntent.FLAG_CANCEL_CURRENT));

        onLoop(startId);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
