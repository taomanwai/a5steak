package com.tommytao.a5steak.util.google;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tommytao.a5steak.util.Foundation;

import java.util.ArrayList;

/**
 * Responsible for GCM.
 * 
 * 
 * Ref:
 * https://blog.pushbullet.com/2014/02/12/keeping-google-cloud-messaging-for
 * -android-working-reliably-techincal-post/
 * 
 * http://developer.android.com/training/monitoring-device-state/manifest-
 * receivers.html
 * 
 * http://www.grokkingandroid.com/enabling-and-disabling-broadcastreceivers/
 * 
 * 
 * @author tommytao
 * 
 */
public class GcmPusher extends Foundation {

	private static GcmPusher instance;

	public static GcmPusher getInstance() {

		if (instance == null)
			instance = new GcmPusher();

		return instance;
	}

	private GcmPusher() {

	}

	// --


	private GcmBroadcastReceiver receiver;

	private IntentFilter intentFilter;

	private ArrayList<OnReceiveIntentListener> onReceiveIntentListeners = new ArrayList<OnReceiveIntentListener>();

	private String senderId = "";

	private String prefsGCMRegId = "";

	public static final int TIME_OF_CYCLE_OF_WAITING_FOR_INTENT_REGISTRATION_IN_MS = 1000;
	public static final int MAX_NO_OF_CYCLE_OF_WAITING_FOR_INTENT_REGISTRATION = 15;



	private String lastKnownRegId = "";

	private GoogleCloudMessaging gcm;

	@Override
	public boolean init(Context appContext) {
		return super.init(appContext);
	}

	public boolean isOnReceiveIntentListenerAssigned() {
		return (this.onReceiveIntentListeners.size() != 0);
	}

	private void setSenderId(String senderId) {

		int appVersionCode = 0;

		try {
			appVersionCode = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0).versionCode;
		} catch (Exception e) {
		}

		this.senderId = senderId;
		this.prefsGCMRegId = "GCMManager." + appVersionCode + "_" + senderId + "_PREFS_GCM_REG_ID";

		lastKnownRegId = PreferenceManager.getDefaultSharedPreferences(appContext).getString(prefsGCMRegId, "");

	}

	public void init(Context appContext, String senderId) {
		super.init(appContext);

		setSenderId(senderId);

	}

	/**
	 * 
	 * Re-init GCMManager with new sender ID
	 * 
	 * @param senderId
	 *            Sender ID
	 */
	public void reInit(String senderId) {

		unregister();

		setSenderId(senderId);

	}

	public static interface OnRegisterListener {

		public void onComplete(boolean succeed);

	}

	public static interface OnReceiveIntentListener {

		public void onReceiveIntent(Intent intent);

	}

	private class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

		@Override
		public void onReceive(Context context, final Intent intent) {

			if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
				if (!isRegistering())
					return;

				setLastKnownRegId(intent.getStringExtra("registration_id"));

				setResultCode(Activity.RESULT_OK);
				return;
			}

			triggerOnReceiveIntentListeners(intent);

			setResultCode(Activity.RESULT_OK);

		}

	}

	private void triggerOnReceiveIntentListeners(Intent intent) {

		for (OnReceiveIntentListener onReceiveIntentListener : onReceiveIntentListeners)
			onReceiveIntentListener.onReceiveIntent(intent);

	}

	private ArrayList<OnRegisterListener> onRegisterListeners = new ArrayList<OnRegisterListener>();

	private void clearAndTriggerOnRegisterListeners(boolean succeed) {

		ArrayList<OnRegisterListener> pendingOnRegisterListeners = new ArrayList<OnRegisterListener>(onRegisterListeners);

		onRegisterListeners.clear();


		for (OnRegisterListener pendingOnRegisterListener : pendingOnRegisterListeners)
			pendingOnRegisterListener.onComplete(succeed);

	}

	private GoogleCloudMessaging getGCM() {

		if (gcm == null)
			gcm = GoogleCloudMessaging.getInstance(appContext);

		return gcm;
	}

	public String getLastKnownRegId() {
		return lastKnownRegId;
	}

	private void setLastKnownRegId(String lastKnownRegId) {

		// Sometimes (e.g. no network, intent.getStringExtra("registration_id")
		// being null), we may assign setLastKnownRegId as null. In this case,
		// we assign lastKnownRegId as empty string (to avoid null pointer
		// exception, and it is reasonable to serve null string as empty string)
		this.lastKnownRegId = (lastKnownRegId == null) ? "" : lastKnownRegId;

		PreferenceManager.getDefaultSharedPreferences(appContext).edit().putString(prefsGCMRegId, this.lastKnownRegId).commit();

	}


	private IntentFilter getIntentFilter() {

		if (intentFilter == null) {
			intentFilter = new IntentFilter();
			intentFilter.addAction("com.google.android.c2dm.intent.RECEIVE");
			intentFilter.addAction("com.google.android.c2dm.intent.REGISTRATION");
			intentFilter.addCategory(appContext.getPackageName());

		}

		return intentFilter;

	}

	public void addOnReceiveIntentListener(OnReceiveIntentListener onReceiveIntentListener) {

		this.onReceiveIntentListeners.add(onReceiveIntentListener);

	}

	public boolean removeOnReceiveIntentListener(OnReceiveIntentListener onReceiveIntentListener) {

		return onReceiveIntentListeners.remove(onReceiveIntentListener);

	}

    /**
     *
     * Register GCM
     *
     * @param maxNoOfRetries Max no. of retries (include the first time of start())
     * @param onRegisterListener Listener which will be triggered after GCM service is registered
     */
	public void register(final int maxNoOfRetries, final OnRegisterListener onRegisterListener) {

		// A hack to pass a final primitive which can be *MODIFIED*
		final ArrayList<Integer> retryCounterFinal = new ArrayList<Integer>(1);
		retryCounterFinal.add(0);

		registerInSingleShoot(new OnRegisterListener() {

			@Override
			public void onComplete(boolean succeed) {

				retryCounterFinal.set(0, retryCounterFinal.get(0) + 1);

				if (succeed) {

					if (onRegisterListener != null)
						onRegisterListener.onComplete(true);

					return;
				}

				if (retryCounterFinal.get(0) >= maxNoOfRetries) {

					if (onRegisterListener != null)
						onRegisterListener.onComplete(false);

					return;

				}

				registerInSingleShoot(this);

			}

		});

	}

	private void registerInSingleShoot(final OnRegisterListener onRegisterListener) {

		if (isRegistering()) {
			if (onRegisterListener != null)
				onRegisterListeners.add(onRegisterListener);

			return;
		}

		if (isRegistered())
			unregister();

		if (onRegisterListener != null)
			onRegisterListeners.add(onRegisterListener);

		setLastKnownRegId("");

		receiver = new GcmBroadcastReceiver();
		appContext.registerReceiver(receiver, getIntentFilter(), "com.google.android.c2dm.permission.SEND", null);

		new AsyncTask<GoogleCloudMessaging, Void, String>() {

			@Override
			protected String doInBackground(GoogleCloudMessaging... gcms) {

				if (gcms.length != 1)
					return "";

				String result = "";

				try {
					result = gcms[0].register(senderId);
					// For safety only
					if (result == null)
						result = "";
				} catch (Exception e) {
                    e.printStackTrace();
				}

				return result;
			}

			@Override
			protected void onPostExecute(String result) {

				boolean isSucceed = !result.isEmpty();

				if (isSucceed) {

					setLastKnownRegId(result);
					clearAndTriggerOnRegisterListeners(isSucceed);

					return;
				}

				// Wait for recv regId from
				// com.google.android.c2dm.intent.REGISTRATION (for
				// TIME_OF_WAITING_FOR_INTENT_REGISTRATION_IN_MS) and then
				// check regId ready or not, if not, fire register not
				// succeed event

				// A hack to pass a final primitive which can be *MODIFIED*
				final ArrayList<Integer> counterFinal = new ArrayList<Integer>(1);
				counterFinal.add(0);

				handler.post(new Runnable() {

					@Override
					public void run() {

						boolean isSucceed = !lastKnownRegId.isEmpty();

						counterFinal.set(0, counterFinal.get(0) + 1);

						if (isSucceed) {

							clearAndTriggerOnRegisterListeners(true);

							return;

						}

						if (counterFinal.get(0) >= MAX_NO_OF_CYCLE_OF_WAITING_FOR_INTENT_REGISTRATION) {
							try {
								appContext.unregisterReceiver(receiver);
							} catch (Exception e) {

							}
							receiver = null;

							clearAndTriggerOnRegisterListeners(false);

							return;

						}

						handler.postDelayed(this, TIME_OF_CYCLE_OF_WAITING_FOR_INTENT_REGISTRATION_IN_MS);

					}

				});

			}

		}.execute(getGCM());

	}

	public void unregister() {

		if (!(isRegistering() || isRegistered()))
			return;

		setLastKnownRegId("");

		// for safety, use try .. catch
		try {
			appContext.unregisterReceiver(receiver);
		} catch (Exception e) {
		}

		receiver = null;

		clearAndOnUiThreadTriggerOnRegisterListeners(false);

	}

	private void clearAndOnUiThreadTriggerOnRegisterListeners(final boolean isSucceed) {

		final ArrayList<OnRegisterListener> pendingOnRegisterListeners = new ArrayList<OnRegisterListener>(onRegisterListeners);

		onRegisterListeners.clear();

		if (pendingOnRegisterListeners.isEmpty())
			return;

		handler.post(new Runnable() {

			@Override
			public void run() {

				for (OnRegisterListener pendingOnRegisterListener : pendingOnRegisterListeners)
					pendingOnRegisterListener.onComplete(isSucceed);

			}

		});

	}

	public boolean isRegistered() {
		return !lastKnownRegId.isEmpty() && onRegisterListeners.isEmpty();
	}

	public boolean isRegistering() {
		return (receiver != null && !isRegistered());
	}

}
