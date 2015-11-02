package com.tommytao.a5steak.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.tommytao.a5steak.ext.net.ntp.NTPUDPClient;

import java.net.InetAddress;
import java.util.GregorianCalendar;

/**
 * 
 * Responsible for time operations, e.g. time sync using NTP (Network Time
 * Protocol)
 *
 * Warning: Apache NTPUDPClient is needed
 * 
 * @author tommytao
 * 
 */
public class TimeManager extends Foundation {

	private static TimeManager instance;

	public static TimeManager getInstance() {

		if (instance == null)
			instance = new TimeManager();

		return instance;
	}

	/**
	 * Constructor, create TimeManager and it will pre-cache objects, increase
	 * performance and avoid multi thread read&write issue
	 * 
	 */
	private TimeManager() {

		super();

		log( "time_manager: " + "create");

		client = new NTPUDPClient();

	}

	// --

	public static final String[] TIME_SERVER_LIST = {"time-a.nist.gov", "time-c.nist.gov", "time.nist.gov", "time.asia.apple.com" };

	public class TimeSetReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals("android.intent.action.TIME_SET"))
				refreshLastKnownNtpTimeDiff();

		}

	}

	public static interface OnGetNtpTimeListener {

		public void onComplete(long time);

		public void onError();

	}

	public static interface OnRefreshNtpTimeDiffListener {

		public void onComplete(long diff);

		public void onError();

	}

	private GregorianCalendar calendar;

	@Override
	public boolean init(Context appContext) {

		if (!super.init(appContext)) {
			log( "time_manager: " + "init REJECTED: already initialized"); 
			return false;
		}
		
		log( "time_manager: " + "init"); 

		appContext.registerReceiver(new TimeSetReceiver(), new IntentFilter("android.intent.action.TIME_SET"));

		refreshLastKnownNtpTimeDiff();

		return true;

	}

	private GregorianCalendar getCalendar() {

		if (calendar==null){

			calendar = new GregorianCalendar();

		}

		calendar.setTimeInMillis(System.currentTimeMillis());

		return calendar;
	}

	private long lastKnownNtpTimeDiff;

	/**
	 * Return time difference between current time and NTP time
	 * 
	 * Note: Returned value is for reference ONLY. The returned value may be
	 * outdated and not accurate. Use getNtpTimeDiff(OnRefreshNTPTimeDiffListener) to get up-to-date value
	 * instead.
	 * 
	 * @return Time difference between current time and NTP time. Positive means
	 *         current time is running faster than NTP time; Negative means
	 *         current time is running slower than NTP time
	 */
	public long getLastKnownNtpTimeDiff() {

		return lastKnownNtpTimeDiff;
	}

	private void refreshLastKnownNtpTimeDiff() {
        // TODO MVP Now call refreshLastKnownNtpTimeDiff() when app startup and time change, should call it every particular time interval (e.g. 1 hr) in the future
		getNtpTimeDiff(null);
	}

	/**
	 * Get time difference between current time and NTP time, then return result
	 * through listener
	 * 
	 * Note: This function will refresh value of lastKnownNtpTimeDiff
	 * 
	 * @param listener
	 */
	public void getNtpTimeDiff(final OnRefreshNtpTimeDiffListener listener) {

		new Thread() {

			@Override
			public void run() {

				final Long timeInLong = getNtpTimeSync();

				handler.post(new Runnable() {

					@Override
					public void run() {

						if (timeInLong == null) {

							if (listener != null)
								listener.onError();

							return;
						}

						lastKnownNtpTimeDiff = System.currentTimeMillis() - timeInLong;

						if (listener != null)
							listener.onComplete(lastKnownNtpTimeDiff);

					}

				});

			}

		}.start();

	}

	private NTPUDPClient client;

	/**
	 * Get NTP time in sync style
	 * 
	 * @return Unix timestamp of NTP (Network time protocol) time. Null if
	 *         network time cannot be got
	 */
	private Long getNtpTimeSync() {

		long time = 0;
		boolean isSucceed = false;

		int i = 0;
		while (!isSucceed) {
			try {

				if (i >= TIME_SERVER_LIST.length)
					break;

				time = client.getTime(InetAddress.getByName(TIME_SERVER_LIST[i])).getMessage().getTransmitTimeStamp().getTime();
				isSucceed = true;

			} catch (Exception e) {

			}
			i++;
		}

		if (!isSucceed) {

			return null;

		}

		return new Long(time);

	}

	public void getNtpTime(final OnGetNtpTimeListener listener) {

		if (listener == null)
			return;

		new Thread() {

			@Override
			public void run() {

				final Long timeInLong = getNtpTimeSync();

				handler.post(new Runnable() {

					@Override
					public void run() {

						if (timeInLong == null) {

							listener.onError();

							return;
						}

						listener.onComplete(timeInLong);

					}

				});

			}

		}.start();

	}

    /**
     * Return NTP time
     *
     * Note: Returned value is for reference ONLY. The returned value may be
     * outdated and not accurate. Use getNtpTime(OnGetNTPTimeListener) to get up-to-date value
     * instead.
     *
     * @return Last known NTP time
     */
    public long getNtpTimeBasedOnLastKnownNtpTimeDiff(){
        return System.currentTimeMillis() - getLastKnownNtpTimeDiff();
    }

	public int getCurrentYear(){
		return getCalendar().get(GregorianCalendar.YEAR);
	}

	public int getCurrentMonth(){
		return getCalendar().get(GregorianCalendar.MONTH) + 1;
	}

	public int getCurrentDay(){
		return getCalendar().get(GregorianCalendar.DAY_OF_MONTH);
	}

	public int getCurrentWeekday(){
		return getCalendar().get(GregorianCalendar.DAY_OF_WEEK) - 1;
	}

	public int[] getCurrentYearMonthDayWeekDay(){

		int[] result = new int[4];
		GregorianCalendar currentCalendar = getCalendar();

		int year = currentCalendar.get(GregorianCalendar.YEAR);
		int month = currentCalendar.get(GregorianCalendar.MONTH) + 1;
		int day = currentCalendar.get(GregorianCalendar.DAY_OF_MONTH);
		int weekDay = currentCalendar.get(GregorianCalendar.DAY_OF_WEEK) - 1;

		result[0] = year;
		result[1] = month;
		result[2] = day;
		result[3] = weekDay;

		return result;

	}

	public static long trimMillisToDateOnly(long timeInMillis){

		timeInMillis = timeInMillis / 1000 / 60 / 60 / 24;
		timeInMillis = timeInMillis * 24 * 60 * 60 * 1000;

		return timeInMillis;

	}






}
