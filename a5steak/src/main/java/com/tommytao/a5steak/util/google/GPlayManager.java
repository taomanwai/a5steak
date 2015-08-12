package com.tommytao.a5steak.util.google;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.tommytao.a5steak.util.Foundation;


public class GPlayManager extends Foundation {

	private static GPlayManager instance;

	public static GPlayManager getInstance() {

		if (instance == null)
			instance = new GPlayManager();

		return instance;
	}

	private GPlayManager() {

	}

	// --
	
	
	public static interface Listener {
		
		public void onDismiss();
		
	}

	public static final String MARKET_PREFIX = "market://details?id=";
	public static final String HTTP_PREFIX = "http://play.google.com/store/apps/details?id=";
	
	public final static int PLAY_SERVICES_UPDATE_REQUEST = 9000;

	@Override
	public boolean init(Context context) {
		return super.init(context);
	}

	public boolean isGPlayExistAndUpToDate() {

		return (GooglePlayServicesUtil.isGooglePlayServicesAvailable(appContext) == ConnectionResult.SUCCESS);

	}

	public boolean isGPlayExist() {
		return (isGPlayExistAndUpToDate() || GooglePlayServicesUtil.isGooglePlayServicesAvailable(appContext) == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED);
	}

	public boolean isGPlayUpdateRequired(){
		return isGPlayExist() && !isGPlayExistAndUpToDate();
	}
	
	public void showGPlayUpdateDialog(Activity activity, final Listener listener){
		
		Dialog dialog = GooglePlayServicesUtil.getErrorDialog(ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED, activity,
                PLAY_SERVICES_UPDATE_REQUEST); 
		
		dialog.setOnDismissListener(new OnDismissListener(){

			@Override
			public void onDismiss(DialogInterface dialog) {
				
				if (listener!=null)
					listener.onDismiss();
				
			}
			
		});
		
		dialog.show();
		
	}
	
	public void goToAppGPlayPage(Activity activity) {

		activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((isGPlayExist() ? MARKET_PREFIX : HTTP_PREFIX) + activity.getPackageName())));

	}

	public void rate(Activity activity) {

		goToAppGPlayPage(activity);

	}
	
	

}
