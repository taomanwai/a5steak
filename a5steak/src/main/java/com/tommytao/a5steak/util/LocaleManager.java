package com.tommytao.a5steak.util;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.common.util.Foundation;

import java.util.Currency;
import java.util.Locale;

/**
 * Responsible for Locale or related info saving and loading
 * 
 * 
 * List of standard Locale (ISO 3166)
 * 
 * af-ZA am-ET ar-AE ar-BH ar-DZ ar-EG ar-IQ ar-JO ar-KW ar-LB ar-LY ar-MA
 * arn-CL ar-OM ar-QA ar-SA ar-SY ar-TN ar-YE as-IN az-Cyrl-AZ az-Latn-AZ ba-RU
 * be-BY bg-BG bn-BD bn-IN bo-CN br-FR bs-Cyrl-BA bs-Latn-BA ca-ES co-FR cs-CZ
 * cy-GB da-DK de-AT de-CH de-DE de-LI de-LU dsb-DE dv-MV el-GR en-029 en-AU
 * en-BZ en-CA en-GB en-IE en-IN en-JM en-MY en-NZ en-PH en-SG en-TT en-US en-ZA
 * en-ZW es-AR es-BO es-CL es-CO es-CR es-DO es-EC es-ES es-GT es-HN es-MX es-NI
 * es-PA es-PE es-PR es-PY es-SV es-US es-UY es-VE et-EE eu-ES fa-IR fi-FI
 * fil-PH fo-FO fr-BE fr-CA fr-CH fr-FR fr-LU fr-MC fy-NL ga-IE gd-GB gl-ES
 * gsw-FR gu-IN ha-Latn-NG he-IL hi-IN hr-BA hr-HR hsb-DE hu-HU hy-AM id-ID
 * ig-NG ii-CN is-IS it-CH it-IT iu-Cans-CA iu-Latn-CA ja-JP ka-GE kk-KZ kl-GL
 * km-KH kn-IN kok-IN ko-KR ky-KG lb-LU lo-LA lt-LT lv-LV mi-NZ mk-MK ml-IN
 * mn-MN mn-Mong-CN moh-CA mr-IN ms-BN ms-MY mt-MT nb-NO ne-NP nl-BE nl-NL nn-NO
 * nso-ZA oc-FR or-IN pa-IN pl-PL prs-AF ps-AF pt-BR pt-PT qut-GT quz-BO quz-EC
 * quz-PE rm-CH ro-RO ru-RU rw-RW sah-RU sa-IN se-FI se-NO se-SE si-LK sk-SK
 * sl-SI sma-NO sma-SE smj-NO smj-SE smn-FI sms-FI sq-AL sr-Cyrl-BA sr-Cyrl-CS
 * sr-Cyrl-ME sr-Cyrl-RS sr-Latn-BA sr-Latn-CS sr-Latn-ME sr-Latn-RS sv-FI sv-SE
 * sw-KE syr-SY ta-IN te-IN tg-Cyrl-TJ th-TH tk-TM tn-ZA tr-TR tt-RU tzm-Latn-DZ
 * ug-CN uk-UA ur-PK uz-Cyrl-UZ uz-Latn-UZ vi-VN wo-SN xh-ZA yo-NG zh-CN zh-HK
 * zh-MO zh-SG zh-TW zu-ZA
 * 
 * @author tommytao
 * 
 */
public class LocaleManager extends Foundation {

	private static LocaleManager instance;

	public static LocaleManager getInstance() {

		if (instance == null)
			instance = new LocaleManager();

		return instance;
	}

	private LocaleManager() {
		
		super();

		log( "locale: " + "create");
		

	}

	// --

	public static String PREF_LOCALE_LANG = "LocaleManager.PREF_LOCALE.lang";
	public static String PREF_LOCALE_COUNTRY = "LocaleManager.PREF_LOCALE.country";
	public static String PREF_LOCALE_VARIANT = "LocaleManager.PREF_LOCALE.variant";

	private String lang = "";
	private String country = "";
	private String variant = "";

	private Currency currency;

	
	/**
	 * Load proper locale for app
	 * 
	 * PS: Should be initialized at the very beginning of Application.onCreate()
	 * 
	 */
	@Override
	public boolean init(Context context) {

		if (!super.init(context)){
			
			log( "locale: " + "init REJECTED: already initialized"); 
			
			return false;
		}
		
		
		log( "locale: " + "init"); 

		lang = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LOCALE_LANG, "");

		country = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LOCALE_COUNTRY, "");
		variant = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LOCALE_VARIANT, "");
		

		if (!isAppLocaleFollowingSystem())
			setAppLocaleConfiguration(new Locale(lang, country, variant));
		
		return true;

	}

	@Deprecated
	public boolean init(Context context, RequestQueue requestQueue) {
		return super.init(context, requestQueue);
	}

	public boolean isAppLocaleFollowingSystem() {
		return (lang.isEmpty() || country.isEmpty());
	}

	public Locale getSystemLocale() {

		return appContext.getResources().getConfiguration().locale;

	}

	public void syncAppToSystemLocale() {

		Locale systemLocale = getSystemLocale();
		Locale emptyLocale = new Locale("", "");

		setAppLocaleConfiguration(systemLocale);
		setAppLocaleLocalVar(emptyLocale);
		setAppLocalePrefRecord(emptyLocale);

	}

	private Currency getAppCurrency() {

		if (currency == null)
			this.assignAppCurrency(getAppLocale());

		return currency;

	}

	public Locale getAppLocale() {

		return isAppLocaleFollowingSystem() ? getSystemLocale() : new Locale(lang, country, variant);

	}

	public void setAppLocale(Locale locale) {

		if (locale == null)
			return;
		
	
		setAppLocaleConfiguration(locale);
		setAppLocaleLocalVar(locale);
		setAppLocalePrefRecord(locale); 
		assignAppCurrency(locale);

	}

	public Resources getResourcesOfSpecificLocale(Locale locale){

		Resources standardResources = appContext.getResources();
		AssetManager assets = standardResources.getAssets();
		DisplayMetrics metrics = standardResources.getDisplayMetrics();
		Configuration config = new Configuration(standardResources.getConfiguration());
		config.locale = locale;
		Resources res = new Resources(assets, metrics, config);
		return res;

	}


	private void assignAppCurrency(Locale locale) {

		if (locale == null)
			return;

		try {
			currency = Currency.getInstance(locale);
		} catch (Exception e) {
			currency = Currency.getInstance(Locale.US);
		}

	}

	public void refreshOnConfigurationChanged(Configuration newConfig) {

		Locale appLocale = getAppLocale();

		if (appLocale.equals(newConfig.locale) && appLocale.equals(Locale.getDefault()))
			return;

		// Create whole new config to avoid screen flashing. Ref:
		// http://stackoverflow.com/questions/13005010/android-app-screen-flashing-at-launch
		Configuration waitingToBeUpdatedConfig = new Configuration(newConfig);
		Locale.setDefault(appLocale);
		waitingToBeUpdatedConfig.locale = appLocale;
		appContext.getResources().updateConfiguration(waitingToBeUpdatedConfig, appContext.getResources().getDisplayMetrics());

	}

	public String getAppDollarCode() {

		return getAppCurrency().getCurrencyCode();

	}

	public String getAppDollarSymbol() {

		return getAppCurrency().getSymbol();

	}



	public int getAppDollarFractionDigits() {

		return getAppCurrency().getDefaultFractionDigits();

	}

	private void setAppLocaleConfiguration(Locale locale) {

		if (locale == null)
			return;

		if (Locale.getDefault().equals(locale))
			return;

		Locale.setDefault(locale);
		Configuration config = appContext.getResources().getConfiguration();
		config.locale = locale;
		appContext.getResources().updateConfiguration(config, appContext.getResources().getDisplayMetrics());

	}

	private void setAppLocaleLocalVar(Locale locale) {
		if (locale == null)
			return;

		lang = locale.getLanguage();
		country = locale.getCountry();
		variant = locale.getVariant();

	}

	private void setAppLocalePrefRecord(Locale locale) {

		if (locale == null)
			return;

		Editor editor = PreferenceManager.getDefaultSharedPreferences(appContext).edit();
		
		
		
		editor.putString(PREF_LOCALE_LANG, locale.getLanguage());
		editor.putString(PREF_LOCALE_COUNTRY, locale.getCountry());
		editor.putString(PREF_LOCALE_VARIANT, locale.getVariant());
		editor.commit();

	}





}
