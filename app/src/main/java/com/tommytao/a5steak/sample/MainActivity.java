package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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




    }




    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.btnGo)
    public void go() {

        String s = "" + ediInput.getText();
        tvMsg.setText(s);

    }

    @OnClick(R.id.btnGet)
    public void get() {



    }


}
