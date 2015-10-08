package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.tommytao.a5steak.util.PayPalManager;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

//    private PayPalConfiguration config;
//    private PayPalPayment item;

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
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
//
//        super.onActivityResult(requestCode, resultCode, data);
//
//
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        // setup config
//        config = new PayPalConfiguration()
//                .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
//                .clientId("")
//                .acceptCreditCards(false);
//
//        item = new PayPalPayment(new BigDecimal("1.5"), "HKD", "testingItem", PayPalPayment.PAYMENT_INTENT_SALE);
//
//        Intent intent = new Intent(this, PayPalService.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//        startService(intent);

        PayPalManager.getInstance().init(this, PayPalManager.ENVIRONMENT_NO_NETWORK, "", true);


        PayPalManager.getInstance().start();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PayPalManager.getInstance().stop();
    }

    @OnClick(R.id.btnGo)
    public void go(){
//        Intent intent = new Intent(this, PaymentActivity.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, item);
//        startActivityForResult(intent, 1234);

//        CardIoSensor.getInstance().scan(this, true, true, false, new CardIoSensor.OnScanListener() {
//            @Override
//            public void onComplete(CreditCard creditCard) {
//                Log.d("", "");
//            }
//        });

        PayPalManager.getInstance().pay(this, 1.75, "HKD", "item_1", new PayPalManager.OnPayListener() {
            @Override
            public void onPay(PaymentConfirmation confirmation) {
                Log.d("","");
            }

            @Override
            public void onCancel() {
                Log.d("","");
            }
        });

    }



}
