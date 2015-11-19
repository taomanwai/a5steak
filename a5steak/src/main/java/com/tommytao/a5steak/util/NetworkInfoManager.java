package com.tommytao.a5steak.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Responsible for getting network info (e.g. connectivity, etc.)
 * 
 * @author tommytao
 * 
 */
public class NetworkInfoManager extends Foundation {

	private static NetworkInfoManager instance;

	private ConnectivityManager connectivityManager;

	private NetworkInfo networkInfo;

	private NetworkInfoManager() {

		super();

		log("network_info_manager: " + "create");

	}

	public static NetworkInfoManager getInstance() {
		if (instance == null)
			instance = new NetworkInfoManager();

		return instance;

	}

	// --

	@Override
	public boolean init(Context appContext) {

		if (!super.init(appContext)) {

			log("network_info_manager: " + "init REJECTED: already initialized");

			return false;

		}

		log("network_info_manager: " + "init");

		return true;

	}

	public static final String GOOGLE_LINK = "http://www.google.com";

	public static final String YAHOO_LINK = "http://www.yahoo.com";

	public static interface Listener {

		public void onComplete(boolean accessible);

	}


	private ConnectivityManager getConnectivityManager() {
		if (connectivityManager == null)
			connectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		return connectivityManager;
	}

	private void refreshNetworkInfo() {

		networkInfo = getConnectivityManager().getActiveNetworkInfo();

	}

	public String getTypeName() {

		refreshNetworkInfo();

		if (this.networkInfo == null)
			return "";

		return networkInfo.getTypeName();

	}

	public String getDetailedState() {

		refreshNetworkInfo();

		if (this.networkInfo == null)
			return NetworkInfo.DetailedState.DISCONNECTED.name();

		return networkInfo.getDetailedState().name();

	}

	public boolean isAvailable() {

		refreshNetworkInfo();

		if (this.networkInfo == null)
			return false;

		return networkInfo.isAvailable();
	}

	private boolean isConnectedAsTypeOf(String type) {
		refreshNetworkInfo();

		if (this.networkInfo == null)
			return false;

		if (!networkInfo.isConnected())
			return false;

		return (networkInfo.getTypeName().equals(type));

	}

	public boolean isConnectedWifi() {

		return isConnectedAsTypeOf("WIFI");

	}

	public boolean isConnectedMobile() {

		return isConnectedAsTypeOf("MOBILE");

	}

	public boolean isConnected() {

		refreshNetworkInfo();

		if (this.networkInfo == null)
			return false;

		return networkInfo.isConnected();
	}

	/**
	 * Check if Google accessible
	 * 
	 * @param listener
	 *            Listener which is used to return result
	 */
	public void isGoogleAccessible(final Listener listener) {

		if (listener==null)
			return;
		
		if (!this.isConnected()) {

			handler.post(new Runnable() {
				@Override
				public void run() {
					listener.onComplete(false);
				}
			}); 

		}

		httpGetByteArray(GOOGLE_LINK, new OnHttpGetByteArrayListener() {
			@Override
			public void onDownloaded(byte[] ba) {
				listener.onComplete(ba.length > 0);
			}

			@Override
			public void onDownloading(int percentage) {

			}
		});

	}

	/**
	 * Check if Yahoo accessible
	 *
	 * @param listener
	 *            Listener which is used to return result
	 */
	public void isYahooAccessible(final Listener listener) {

		if (listener==null)
			return;

		if (!this.isConnected()) {

			handler.post(new Runnable() {
				@Override
				public void run() {
					listener.onComplete(false);
				}
			});

		}

		httpGetByteArray(YAHOO_LINK, new OnHttpGetByteArrayListener() {
			@Override
			public void onDownloaded(byte[] ba) {
				listener.onComplete(ba.length > 0);
			}

			@Override
			public void onDownloading(int percentage) {

			}
		});

	}

	public boolean isConnectedOrConnecting() {

		refreshNetworkInfo();

		if (this.networkInfo == null)
			return false;

		return networkInfo.isConnectedOrConnecting();
	}

	public boolean isConnecting() {

		refreshNetworkInfo();

		if (this.networkInfo == null)
			return false;

		return (networkInfo.isConnectedOrConnecting() && !networkInfo.isConnected());
	}

	public boolean isFailover() {

		refreshNetworkInfo();

		if (this.networkInfo == null)
			return false;

		return networkInfo.isFailover();
	}

	public boolean isRoaming() {

		refreshNetworkInfo();

		if (this.networkInfo == null)
			return false;

		return networkInfo.isRoaming();
	}

}
