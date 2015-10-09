package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.tommytao.a5steak.util.PayPalManager;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.btnGo)
    public void go(){


    }



}
