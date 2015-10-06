package com.tommytao.a5steak.util;

public class Encyclopedia {

	private static Encyclopedia instance;

	public static Encyclopedia getInstance() { 

		if (instance == null)
			instance = new Encyclopedia();

		return instance; 
	}


	
	private Encyclopedia() { 

	}
	
	// --
	

	public static final String BOUNDS_OF_HK = "22.1533884,113.835078|22.561968,114.4069561";
	public static final String BOUNDS_OF_SG = "1.1663552,103.6065099|1.4707717,104.0855574";

	
	public static final String GUANGZHOU_AMAP_CITY_CODE = "020";
	public static final String SHENZHEN_AMAP_CITY_CODE = "0755";

    public static final double BEIJING_LAT = 39.903175;
    public static final double BEIJING_LNG = 116.391415;
	
	public static final double GUANGZHOU_LAT = 23.198807;
	public static final double GUANGZHOU_LNG = 113.320554;
	public static final int GUANGZHOU_RADIUS_IN_METER = 50000; 

	public static final double SHENZHEN_LAT = 22.615017;
	public static final double SHENZHEN_LNG = 114.055951;
	public static final int SHENZHEN_RADIUS_IN_METER = 30000;

	public static final double HKSIL_LAT = 22.394190;
	public static final double HKSIL_LNG = 114.202048; 
	
	public static final double HK_SPACE_MUSEUM_LAT = 22.294288;
	public static final double HK_SPACE_MUSEUM_LNG = 114.171910;

	public static final String GUANGZHOU_IN_SC = "广州";
	public static final String SHENZHEN_IN_SC = "深圳";


	
	
	
	


}
