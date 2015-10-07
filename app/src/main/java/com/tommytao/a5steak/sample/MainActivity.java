package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.tommytao.a5steak.util.sensor.CardIoSensor;

import java.math.BigDecimal;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.card.payment.CreditCard;

public class MainActivity extends Activity {

    private PayPalConfiguration config;
    private PayPalPayment item;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        switch (requestCode){
//
//            case 1234:
//                if (resultCode== Activity.RESULT_OK){
//
//                    PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
//
//                    if (confirm!=null)
//                        Toast.makeText(MainActivity.this, "paid", Toast.LENGTH_LONG).show();
//
//                }
//
//            default:
//                // do nothing
//        }

        super.onActivityResult(requestCode, resultCode, data);



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // setup config
        config = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
                .clientId("");

        item = new PayPalPayment(new BigDecimal("1.5"), "HKD", "testingItem", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);



    }

    @OnClick(R.id.btnGo)
    public void go(){
//        Intent intent = new Intent(this, PaymentActivity.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, item);
//        startActivityForResult(intent, 1234);

        CardIoSensor.getInstance().scan(this, true, true, true, new CardIoSensor.OnScanListener() {
            @Override
            public void onComplete(CreditCard creditCard) {
                Log.d("", "");
            }
        });

    }



}
