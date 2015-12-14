package com.tommytao.a5steak.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.SparseArray;

import com.android.volley.RequestQueue;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;

public class PayPalManager extends Foundation {

    private static PayPalManager instance;

    public static PayPalManager getInstance() {

        if (instance == null)
            instance = new PayPalManager();

        return instance;
    }

    private PayPalManager() {

    }


    // --

    public static final String ENVIRONMENT_NO_NETWORK = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
    public static final String ENVIRONMENT_SANDBOX = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    public static final String ENVIRONMENT_PRODUCTION = PayPalConfiguration.ENVIRONMENT_PRODUCTION;


    public static interface OnPayListener {
        public void onPay(PaymentConfirmation confirmation);
        public void onCancel();
    }

    public static class PayPalPayActivity extends Activity {

        public static int REQ_PAYPAL_PAY = 1432;

        private OnPayListener listener;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Intent intent = getIntent();
            double price = intent.getDoubleExtra("price", 0);
            String currency = intent.getStringExtra("currency");
            if (TextUtils.isEmpty(currency))
                currency = "USD";
            String itemTitle = intent.getStringExtra("itemTitle");
            if (TextUtils.isEmpty(itemTitle))
                itemTitle = "";

            int id = intent.getIntExtra("idOfOnPayListener", -1);
            listener = (id == -1) ? null : PayPalManager.getInstance().onPayListeners.get(id);
            PayPalManager.getInstance().onPayListeners.remove(id);

            Intent payIntent = new Intent(this, PaymentActivity.class);
            payIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, PayPalManager.getInstance().config);
            payIntent.putExtra(PaymentActivity.EXTRA_PAYMENT, new PayPalPayment(new BigDecimal("" + price), currency, itemTitle, PayPalPayment.PAYMENT_INTENT_SALE));
            startActivityForResult(payIntent, REQ_PAYPAL_PAY);

        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode != REQ_PAYPAL_PAY) {
                return;
            }

            if (resultCode != RESULT_OK) {
                finish();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onCancel();
                        }
                    }
                });
                return;
            }

            finish();

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                        listener.onPay(confirm);
                    }
                }
            });

        }
    }

    private SparseArray<OnPayListener> onPayListeners = new SparseArray<>();

    private String environment = "";
    private String clientId = "";
    private boolean acceptCreditCard;


    private PayPalConfiguration config;

    @Deprecated
    public boolean init(Context context) {
        return super.init(context);
    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }


    public boolean init(Context context, String environment, String clientId, boolean acceptCreditCard) {
        if (!super.init(context)) {
            return false;
        }

        this.environment = environment;
        this.clientId = clientId;
        this.acceptCreditCard = acceptCreditCard;

        config = new PayPalConfiguration()
                .environment(this.environment)
                .clientId(clientId)
                .acceptCreditCards(acceptCreditCard);

        return true;

    }

    public void start() {

        Intent intent = new Intent(appContext, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        appContext.startService(intent);

    }

    public void stop() {

        appContext.stopService(new Intent(appContext, PayPalService.class));

    }

    public void pay(Activity activity, double price, String currency, String itemTitle, OnPayListener listener) {

        int id = genUniqueId();
        onPayListeners.put(id, listener);

        activity.startActivity(new Intent(activity, PayPalPayActivity.class)
                .putExtra("price", price)
                .putExtra("currency", currency)
                .putExtra("itemTitle", itemTitle)
                .putExtra("idOfOnPayListener", id));

    }

}


