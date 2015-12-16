package com.tommytao.a5steak.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.common.Foundation;


/**
 * 
 * Responsible for monitor and receive SMS
 * 
 * 
 * Warning: Must use permission android.permission.RECEIVE_SMS
 * 
 * @author tommytao
 *
 */
public class SmsMonitor extends Foundation {

	private static SmsMonitor instance;

	public static SmsMonitor getInstance() { 

		if (instance == null)
			instance = new SmsMonitor();

		return instance;
	}

	private SmsMonitor() {
		
		super();
		
		log( "sms_monitor: create");

	}
	
	
	// --
	
	@Override
	public boolean init(Context context) {
		
		if (!super.init(context))
			return false;
		
		log("sms_monitor: init");

		return true;
		
	}

	@Deprecated
	public boolean init(Context context, RequestQueue requestQueue) {
		return super.init(context, requestQueue);
	}
	
	public static final int PRIORITY = Integer.MAX_VALUE;
	
	public static interface Listener { 

		public void onMessageReceived(String originatingAddress, String body);

	}
	
	private Listener listener;
	
	private SmsReceiver receiver;
	
	 

	public class SmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			log( "sms_monitor: receiver start");

			Bundle bundle = intent.getExtras();
			
			abortBroadcast(); 

			if (bundle == null){
				
				log( "sms_monitor: receiver REJECTED coz of null bundle");
				
				return;
			}

			Object[] pdusObj = (Object[]) bundle.get("pdus");
			SmsMessage[] messages = new SmsMessage[pdusObj.length];

			for (int i = 0; i < pdusObj.length; i++)
				messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

			if (messages.length==0){
				
				log( "sms_monitor: receiver REJECTED coz of messages being 0 size");
				
				return;
			}
			
			log( "sms_monitor: receiver address: " + messages[0].getDisplayOriginatingAddress());
			
			log( "sms_monitor: receiver body: " + messages[0].getDisplayMessageBody());
		
			
			if (listener!=null)
				listener.onMessageReceived(messages[0].getDisplayOriginatingAddress(), messages[0].getDisplayMessageBody());
				

		}

	}
	
	private IntentFilter intentFilter;
	
	private IntentFilter getIntentFilter() {

		if (intentFilter == null) {
			intentFilter = new IntentFilter();
			intentFilter.setPriority(PRIORITY);
			intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED"); 

		}

		return intentFilter;

	}
	
	private void startListening() {

		receiver = new SmsReceiver();
		appContext.registerReceiver(receiver, getIntentFilter());


	}
	

	private void stopListening() {
		// for safety, use try ... catch
		try {
			appContext.unregisterReceiver(receiver);
		} catch (Exception e) {
		}
		receiver = null;


	}
	
	public void setListener(Listener listener) {

		this.listener = listener;

		if (listener != null)
			startListening();
		else
			stopListening();

	}
	
	
	
	
	
	
	
	
 
	
	

	
	
	
	
}
