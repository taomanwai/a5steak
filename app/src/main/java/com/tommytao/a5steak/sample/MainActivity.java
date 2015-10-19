package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.tommytao.a5steak.util.UxUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    @Bind(R.id.tvMsg1)
    TextView tvMsg1;

    @Bind(R.id.tvMsg2)
    TextView tvMsg2;

    @Bind(R.id.tvMsg3)
    TextView tvMsg3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        BosonNlpManager.getInstance().init(this, "Se9DNydp.3665.8FudbgZo3UGG");


    }


    @OnClick(R.id.btnGo)
    public void go() {

        UxUtils.marqueeTextView(1.0f, 0.3f, UxUtils.DEFAULT_ANIM_DURATION_IN_MS, new LinearInterpolator(), tvMsg1, tvMsg2, tvMsg3);

    }


}
