package com.tommytao.a5steak.util.sensor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import com.tommytao.a5steak.util.Foundation;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;


/**
 * Responsible for read credit card info thru" card.io
 * <p/>
 * Note: Certain permissions & features are needed
 * <!-- Permission to vibrate - recommended, allows vibration feedback on scan -->
 * <uses-permission android:name="android.permission.VIBRATE" />
 * <!-- Permission to use camera - required -->
 * <uses-permission android:name="android.permission.CAMERA" />
 * <!-- Camera features - recommended -->
 * <uses-feature android:name="android.hardware.camera" android:required="false" />
 * <uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />
 * <uses-feature android:name="android.hardware.camera.flash" android:required="false" />
 * <p/>
 * Ref:
 * https://github.com/card-io/card.io-Android-SDK
 */
public class CardIoSensor extends Foundation {

    private static CardIoSensor instance;

    public static CardIoSensor getInstance() {

        if (instance == null)
            instance = new CardIoSensor();

        return instance;
    }

    private CardIoSensor() {

    }

    // --

    public static interface OnScanListener {
        public void onComplete(CreditCard creditCard);
    }

    public static class CardIoActivity extends Activity {

        public static int REQ_CARD_IO = 0;

        private OnScanListener listener;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Intent intent = getIntent();

            int id = intent.getIntExtra("idOfScanListener", -1);
            listener = (id == -1) ? null : CardIoSensor.getInstance().scanListeners.get(id);
            CardIoSensor.getInstance().scanListeners.remove(id);

            boolean requireExpiry = intent.getBooleanExtra("requireExpiry", false);
            boolean requireCvv = intent.getBooleanExtra("requireCvv", false);
            boolean requirePostalCode = intent.getBooleanExtra("requirePostalCode", false);

            Intent scanIntent = new Intent(this, CardIOActivity.class);

            // customize these values to suit your needs.
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, requireExpiry);
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, requireCvv);
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, requirePostalCode);

            startActivityForResult(scanIntent, REQ_CARD_IO);


        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode != REQ_CARD_IO)
                return;

            finish();

            if (listener == null) {
                return;
            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    CreditCard creditCard = null;

                    if (data == null) {
                        listener.onComplete(creditCard);
                        return;
                    }

                    creditCard = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                    listener.onComplete(creditCard);


                }
            });


        }
    }

    private SparseArray<OnScanListener> scanListeners = new SparseArray<>();

    @Deprecated
    public boolean init(Context context) {
        return super.init(context);
    }

    public void scan(Activity activity, boolean requireExpiry, boolean requireCvv, boolean requirePostalCode, OnScanListener listener) {

        int id = genUniqueId();
        scanListeners.put(id, listener);

        activity.startActivity(new Intent(activity, CardIoActivity.class)
                .putExtra("requireExpiry", requireExpiry)
                .putExtra("requireCvv", requireCvv)
                .putExtra("requirePostalCode", requirePostalCode)
                .putExtra("idOfScanListener", id));

    }


}
