package com.tommytao.a5steak.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.text.TextUtils;
import android.util.Patterns;

public class Thinker {

	private static Thinker instance;

	public static Thinker getInstance() {

		if (instance == null)
			instance = new Thinker();

		return instance;
	}

	private Thinker() { 

	}

	// --


	/**
	 * Check if email valid
	 *
	 * Ref: http://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address
	 * 
	 * @param email
	 * @return TRUE = valid, FALSE = invalid
	 * 
	 */
	public boolean isEmailValid(String email) {

		String trimmedEmail = email.trim();

		if (trimmedEmail.isEmpty())
			return false;

		return Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches();
	}
	
	public boolean isQQValid(String qq) {

		String trimmedQQ = qq.trim();

		if (trimmedQQ.isEmpty())
			return false;

		return TextUtils.isDigitsOnly(trimmedQQ);
	}
	
	public boolean isPhoneNoFormatValidForChina(String phoneNo) {

		if (phoneNo == null)
			return false;

		return phoneNo.length() == 7 || phoneNo.length() == 8 || phoneNo.length() == 11 ;


	}

    public boolean isAppInstalled(Context ctx, String pkgName) {

        boolean appFound = false;
        try {
            ctx.getPackageManager().getApplicationInfo(pkgName, 0);
            appFound = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appFound;

    }

    public boolean isInteger(double value ) {

        return value== Math.round(value);

    }
	
//	public static boolean isNumeric(String str) {
//	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
//	}

	public boolean isInCity(double lat, double lng, double cityLat,
			double cityLng, int cityRadiusInMeter) {

		if (Double.isNaN(lat) || Double.isNaN(lng)){
			
			return false;

		}
		
		if (Double.isNaN(cityLat) || Double.isNaN(cityLng)){
			
			return false;
			
		}
		
		if (cityRadiusInMeter == 0){
			
			return false;
		}
		
		float[] distance = new float[3];
		Location.distanceBetween(lat, lng, cityLat, cityLng, distance);
		

		return (distance[0] <= cityRadiusInMeter);

	}
	
	

}
