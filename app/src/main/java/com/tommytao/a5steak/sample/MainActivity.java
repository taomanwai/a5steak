package com.tommytao.a5steak.sample;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.tommytao.a5steak.util.Encyclopedia;
import com.tommytao.a5steak.util.google.GeocodeManager;
import com.tommytao.a5steak.util.google.PlacesApiManager;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.tvMsg)
    TextView tvMsg;

    private PayPalConfiguration config;
    private PayPalPayment item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // setup config
        config = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
                .clientId("")
                .merchantName("merchant name")
                .merchantPrivacyPolicyUri(Uri.parse("http://google.com"))
                .merchantUserAgreementUri(Uri.parse("http://yahoo.com"));





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }


}
