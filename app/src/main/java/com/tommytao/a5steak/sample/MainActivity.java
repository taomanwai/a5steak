package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tommytao.a5steak.customview.CameraView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {


    @Bind(R.id.cameraView)
    CameraView cameraView;

    @Bind(R.id.tvMsg)
    TextView tvMsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

    }


    @OnClick(R.id.btnGo)
    public void go() {

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
        lp.width = 480;
        lp.height = 640;
        cameraView.setLayoutParams(lp);

    }

    @OnClick(R.id.btnGet)
    public void get() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
        lp.width = 144;
        lp.height = 176;
        cameraView.setLayoutParams(lp);
    }

    @OnClick(R.id.btnShare)
    public void share() {


    }


}
