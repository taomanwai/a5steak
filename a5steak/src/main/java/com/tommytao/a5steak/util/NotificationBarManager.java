package com.tommytao.a5steak.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

/**
 * Responsible for prompt & cancel notification (status bar)
 *
 * @author tommytao
 */
public class NotificationBarManager extends Foundation {

    private static NotificationBarManager instance;

    private NotificationBarManager() {
        super();

        log("notification_bar_manager: " + "create");

    }

    public static NotificationBarManager getInstance() {
        if (instance == null) {
            instance = new NotificationBarManager();

        }

        return instance;

    }

    // --

    public static final int MAX_ALLOWED_VALUE_OF_PROGRAMMER_DEFINED_NOTIFICATION_ID = 10000;
//	public static final int DEFAULT_REQUEST_CODE = 1001;

    @Override
    public boolean init(Context appContext) {

        if (!super.init(appContext)) {

            log("notification_bar_manager: " + "init REJECTED: already initialized");

            return false;

        }

        log("notification_bar_manager: " + "init");

        return true;

    }

    private NotificationManager notificationManager;

    private NotificationManager getNotificationManager() {

        if (notificationManager == null)
            notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);

        return notificationManager;
    }

    private int genUniqueNotificationID() {

        return genUniqueID(MAX_ALLOWED_VALUE_OF_PROGRAMMER_DEFINED_NOTIFICATION_ID);

    }

    /**
     * Generate unique pending intent request code (to avoid a bug)
     * <p/>
     * Ref: http://stackoverflow.com/questions/3168484/pendingintent-works-correctly-for-the-first-notification-but-incorrectly-for-the
     *
     * @return Unique pending intent request code
     */
    private int genUniquePendingIntentReqCode() {

        return genUniqueID(-1) % 65536;

    }

    private int genUniqueID(int lowestValueExclusive) {
        // TODO may cause err, coz long -> int
        return (int) SystemClock.elapsedRealtime() + lowestValueExclusive + 1;

    }

    public void cancelNotification(int notificationID) {

        this.getNotificationManager().cancel(notificationID);

    }

    /**
     * Show notification on notification bar. PS: If notification with the same
     * notification ID is already existing on notification bar, existing
     * notification bar will be overwritten by new one.
     *
     * @param iconResId      Resource ID of icon
     * @param title          Title of notification
     * @param msg            Message of notification
     * @param notificationId ID of notification. It should be smaller or equal to
     *                       MAX_ALLOWED_VALUE_OF_PROGRAMMER_DEFINED_NOTIFICATION_ID. If ID
     *                       is -1, a unique id will be auto generated and will be returned
     *                       after functions is finished.
     * @param intent         Intent (for notification response)
     * @return ID of notification. -1 means failing to show notification
     */
    public int notificate(int iconResId, String title, String msg, String soundFilenameInRaw, boolean hasLights, boolean hasVibrate, boolean isAutoCancel,
                          int notificationId, Intent intent) {

        if (notificationId > MAX_ALLOWED_VALUE_OF_PROGRAMMER_DEFINED_NOTIFICATION_ID)
            return -1;

        if (notificationId < 0)
            notificationId = genUniqueNotificationID();

        if (title == null)
            title = "";

        if (msg == null)
            msg = "";

        if (soundFilenameInRaw == null)
            soundFilenameInRaw = "";


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(appContext);
        notificationBuilder.setSmallIcon(iconResId).setContentTitle(title).setContentText(msg).setContentIntent(genPendingIntent(intent));

        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));

        Notification notification = notificationBuilder.build();

        if (!soundFilenameInRaw.isEmpty()) {
            Uri uri = Uri.parse("android.resource://" + appContext.getPackageName() + "/raw/" + soundFilenameInRaw);
            notification.sound = uri;
        }
        notification.flags = isAutoCancel ? Notification.FLAG_AUTO_CANCEL : ~Notification.FLAG_AUTO_CANCEL;

        if (hasLights)
            notification.defaults |= Notification.DEFAULT_LIGHTS;

        if (hasVibrate)
            notification.defaults |= Notification.DEFAULT_VIBRATE;

        getNotificationManager().notify(notificationId, notification);

        return notificationId;
    }

    /**
     * Traditional pending intent generation method may have bugs in Android
     * 4.x, use it to solve it.
     *
     * @param intent Intent of pending intent
     * @return Pending intent
     */
    private PendingIntent genPendingIntent(Intent intent) {

        if (intent == null)
            return null;

        final int DEFAULT_REQUEST_CODE = genUniquePendingIntentReqCode();

        Intent dummyIntent = new Intent(intent);

        PendingIntent dummyPendingIntent = PendingIntent.getActivity(appContext, DEFAULT_REQUEST_CODE, dummyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        dummyPendingIntent.cancel();

        return PendingIntent.getActivity(appContext, DEFAULT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }



}
