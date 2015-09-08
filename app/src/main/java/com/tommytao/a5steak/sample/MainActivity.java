package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;

import com.tommytao.a5steak.customview.CameraView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {


    @Bind(R.id.cameraView)
    CameraView cameraView;


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
    public void go() {

    }

    @OnClick(R.id.btnGet)
    public void get() {

    }

    @OnClick(R.id.btnShare)
    public void share() {

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();


    }
}
