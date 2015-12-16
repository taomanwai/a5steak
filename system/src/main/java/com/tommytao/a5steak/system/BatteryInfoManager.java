package com.tommytao.a5steak.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.common.Foundation;

/**
 * Responsible for battery stuff (e.g. reading battery level, status)
 * 
 * Ref: http://developer.android.com/training/monitoring-device-state/battery-
 * monitoring.html
 * 
 * @author tommytao
 * 
 */
public class BatteryInfoManager extends Foundation {

	private static BatteryInfoManager instance;

	public static BatteryInfoManager getInstance() {

		if (instance == null)
			instance = new BatteryInfoManager();

		return instance;
	}

	private BatteryInfoManager() {

		super();

		log( "battery_info_manager: " + "create");

	}

	// --

	@Override
	public boolean init(Context context) {

		if (!super.init(context)) {

			log( "battery_info_manager: " + "init REJECTED: already initialized");

			return false;

		}

		log( "battery_info_manager: " + "init");

		return true;

	}

	@Deprecated
	public boolean init(Context context, RequestQueue requestQueue) {
		return super.init(context, requestQueue);
	}

	public class PowerConnectionReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (listener != null)
				listener.onPowerConnectionChanged(isCharging());

		}

	}

	public class BatteryLevelReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (listener != null) {

				String action = intent.getAction();

				if (action.equals(Intent.ACTION_BATTERY_LOW))
					listener.onBatteryLow(getLevelInPercentage());

				else if (action.equals(Intent.ACTION_BATTERY_OKAY))
					listener.onBatteryOk(getLevelInPercentage());

			}

		}

	}

	public static interface Listener {

		public void onPowerConnectionChanged(boolean connected);

		public void onBatteryLow(int levelInPercentage);

		public void onBatteryOk(int levelInPercentage);

	}

	PowerConnectionReceiver powerConnectionReceiver;

	BatteryLevelReceiver batteryLevelReceiver;

	private Listener listener;

	private IntentFilter powerConnectionIntentFilter;

	private IntentFilter getPowerConnectionIntentFilter() {

		if (powerConnectionIntentFilter == null) {
			powerConnectionIntentFilter = new IntentFilter();
			powerConnectionIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
			powerConnectionIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);

		}

		return powerConnectionIntentFilter;

	}

	private IntentFilter batteryLevelIntentFilter;

	private IntentFilter getBatteryLevelIntentFilter() {

		if (batteryLevelIntentFilter == null) {
			batteryLevelIntentFilter = new IntentFilter();
			batteryLevelIntentFilter.addAction(Intent.ACTION_BATTERY_LOW);
			batteryLevelIntentFilter.addAction(Intent.ACTION_BATTERY_OKAY);

		}

		return batteryLevelIntentFilter;

	}

	public void setListener(Listener listener) {

		this.listener = listener;

		if (listener != null)
			startListening();
		else
			stopListening();

	}

	private void startListening() {

		powerConnectionReceiver = new PowerConnectionReceiver();
		appContext.registerReceiver(powerConnectionReceiver, getPowerConnectionIntentFilter());

		batteryLevelReceiver = new BatteryLevelReceiver();
		appContext.registerReceiver(batteryLevelReceiver, getBatteryLevelIntentFilter());

	}

	private void stopListening() {
		// for safety, use try ... catch
		try {
			appContext.unregisterReceiver(powerConnectionReceiver);
		} catch (Exception e) {
		}
		powerConnectionReceiver = null;

		// for safety, use try ... catch
		try {
			appContext.unregisterReceiver(batteryLevelReceiver);
		} catch (Exception e) {
		}
		batteryLevelReceiver = null;

	}

	private IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

	private Intent getIntent() {
		return appContext.registerReceiver(null, ifilter);
	}

	private int getStatus() {
		return getIntent().getIntExtra(BatteryManager.EXTRA_STATUS, -1);

	}

	public int getLevelInPercentage() {
		int scaledLevel = getScaledLevel();
		int scale = getScale();

		if (scaledLevel < 0 || scale <= 0)
			return -1;

		return scaledLevel * 100 / scale;
	}

	private int getScaledLevel() {
		return getIntent().getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	}

	private int getScale() {
		return getIntent().getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	}

	public boolean isCharging() {
		int status = getStatus();

		return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

	}

	private int getChargePlug() {
		return getIntent().getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
	}

	public boolean isChargedByUSB() {

		if (!isCharging())
			return false;

		return getChargePlug() == BatteryManager.BATTERY_PLUGGED_USB;

	}

	public boolean isChargedByAC() {
		if (!isCharging())
			return false;

		return getChargePlug() == BatteryManager.BATTERY_PLUGGED_AC;

	}

}
