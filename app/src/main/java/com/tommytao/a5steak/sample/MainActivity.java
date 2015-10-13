package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tommytao.a5steak.util.DeviceInfoManager;
import com.tommytao.a5steak.util.UxUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    @Bind(R.id.tvMsg)
    TextView tvMsg;

    @Bind(R.id.ivSample)
    ImageView ivSample;

    @Bind(R.id.ediInput)
    EditText ediInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        BosonNlpManager.getInstance().init(this, "Se9DNydp.3665.8FudbgZo3UGG");

        DeviceInfoManager.getInstance().init(this);


    }

    public void pop() {

        final int fromY = DeviceInfoManager.getInstance().getScreenSize().y * 13 / 10;

        ivSample.setVisibility(View.VISIBLE);
        UxUtils.slideViewAbsolutely(ivSample, 0, fromY, 0, 0, 1.0f, 1.0f, 0, 5000, new LinearInterpolator(), new UxUtils.Listener() {
            @Override
            public void onComplete() {

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        pop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.btnGo)
    public void go() {

    }

    @OnClick(R.id.btnGet)
    public void get() {

        final int fromY = DeviceInfoManager.getInstance().getScreenSize().y * 13 / 10;

        UxUtils.slideViewAbsolutely(ivSample, 0, 0, 0, fromY, 1.0f, 1.0f, 0, 5000, new LinearInterpolator(), new UxUtils.Listener() {
            @Override
            public void onComplete() {
                ivSample.setVisibility(View.INVISIBLE);
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });



    }


}
