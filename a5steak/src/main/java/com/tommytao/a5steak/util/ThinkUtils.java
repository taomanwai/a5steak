package com.tommytao.a5steak.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.text.TextUtils;
import android.util.Patterns;

public class ThinkUtils {


	/**
	 * Check if email valid
	 *
	 * Ref: http://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address
	 * 
	 * @param email
	 * @return TRUE = valid, FALSE = invalid
	 * 
	 */
	public static boolean isEmailValid(String email) {

		String trimmedEmail = email.trim();

		if (trimmedEmail.isEmpty())
			return false;

		return Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches();
	}
	
	public static boolean isQQValid(String qq) {

		String trimmedQQ = qq.trim();

		if (trimmedQQ.isEmpty())
			return false;

		return TextUtils.isDigitsOnly(trimmedQQ);
	}
	




    public static boolean isInteger(double value ) {

        return value== Math.round(value);

    }


	public static boolean isInCity(double lat, double lng, double cityLat,
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

	public static boolean hasCamera(Context ctx){
		return  ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}


	
	

}
