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
public class PhoneUtils {

    /**
     * Get SIM card phone number (may not work)
     * <p/>
     * Ref:
     * http://stackoverflow.com/questions/10363947/how-to-get-international-
     * dialing-code-based-on-sim-provider
     *
     * @return Phone number recorded in SIM card
     */
    public static String getSimPhoneNo(Context ctx) {
        String result = "";

        try {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
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
    public static String getWhatsappPhoneNo(Context ctx) {

        String result = "";

        try {
            AccountManager am = AccountManager.get(ctx);
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

    public static boolean isSimAvailable(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        return (tm.getSimState() != TelephonyManager.SIM_STATE_ABSENT);

    }

    private static boolean dialOrCall(Context ctx, String phoneNo, boolean dial) {


        // TODO MVP try catch
        boolean succeed = true;
        try {
            if (!isSimAvailable(ctx))
                return false;

            if (phoneNo == null)
                return false;

            phoneNo = phoneNo.trim();

            if (phoneNo.isEmpty())
                return false;

            String action = dial ? Intent.ACTION_DIAL : Intent.ACTION_CALL;

            Intent intent = new Intent(action, Uri.parse("tel:" + phoneNo));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            ctx.startActivity(intent);
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
    public static boolean dial(Context ctx, String phoneNo) {
        return dialOrCall(ctx, phoneNo, true);
    }


    /**
     * Call phone no. directly and immediately
     *
     * @param phoneNo
     * @return
     */
    public static boolean call(Context ctx, String phoneNo) {
        return dialOrCall(ctx, phoneNo, false);
    }

    public static boolean isPhoneNumFormatValidForChina(String phoneNum) {

        if (phoneNum == null)
            return false;

        return phoneNum.length() == 7 || phoneNum.length() == 8 || phoneNum.length() == 11 ;


    }

}
