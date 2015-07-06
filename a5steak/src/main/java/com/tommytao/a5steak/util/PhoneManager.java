package com.tommytao.a5steak.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;

/**
 * Responsible for phone stuff
 * <p/>
 * Warning: Need <uses-permission android:name="android.permission.CALL_PHONE" />
 */
public class PhoneManager extends Foundation {

    private static PhoneManager instance;

    public static PhoneManager getInstance() {

        if (instance == null)
            instance = new PhoneManager();

        return instance;
    }

    private PhoneManager() {

    }


    // --

    /**
     * Get SIM card phone number (may not work)
     * <p/>
     * Ref:
     * http://stackoverflow.com/questions/10363947/how-to-get-international-
     * dialing-code-based-on-sim-provider
     *
     * @return Phone number recorded in SIM card
     */
    public String getSimPhoneNo() {
        String result = "";

        try {
            TelephonyManager tm = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
            result = tm.getLine1Number();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    /**
     * Get Whatsapp phone number (may not work)
     * <p/>
     * Ref:
     * http://stackoverflow.com/questions/22639640/hhow-can-i-get-my-whatsapp
     * -number
     *
     * @return Phone number recorded in Whatsapp
     */
    public String getWhatsappPhoneNo() {

        String result = "";

        try {
            AccountManager am = AccountManager.get(appContext);
            Account[] accounts = am.getAccounts();

            for (Account ac : accounts) {

                String actype = ac.type;
                // Take your time to look at all available accounts
                if (actype.equals("com.whatsapp"))
                    result = ac.name;

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();

        }

        return result;

    }

    public boolean isSimAvailable() {
        TelephonyManager tm = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        return (tm.getSimState() != TelephonyManager.SIM_STATE_ABSENT);

    }

    private boolean dialOrCall(String phoneNo, boolean dial) {


        // TODO MVP try catch
        boolean succeed = true;
        try {
            if (!isSimAvailable())
                return false;

            if (phoneNo == null)
                return false;

            phoneNo = phoneNo.trim();

            if (phoneNo.isEmpty())
                return false;

            String action = dial ? Intent.ACTION_DIAL : Intent.ACTION_CALL;

            Intent intent = new Intent(action, Uri.parse("tel:" + phoneNo));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            appContext.startActivity(intent);
        } catch (Exception e){
            e.printStackTrace();
            succeed = false;
        }

        if (!succeed)
            return false;

        return true;

    }

    /**
     *
     * Dial phone no. (i.e. show phone pad and let user click call button)
     *
     * @param phoneNo
     * @return
     */
    public boolean dial(String phoneNo) {
        return dialOrCall(phoneNo, true);
    }


    /**
     * Call phone no. directly and immediately
     *
     * @param phoneNo
     * @return
     */
    public boolean call(String phoneNo) {
        return dialOrCall(phoneNo, false);
    }


}
