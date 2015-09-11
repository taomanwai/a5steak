package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.tommytao.a5steak.customview.CameraView;
import com.tommytao.a5steak.util.google.SpeechRecognitionManager;

import java.util.Locale;

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

        SpeechRecognitionManager.getInstance().init(this);




    }



    @OnClick(R.id.btnGo)
    public void go() {

        SpeechRecognitionManager.getInstance().listenUsingGoogleUI(this, false, new Locale("zh", "HK"), new SpeechRecognitionManager.Listener() {
            @Override
            public void onComplete(String result) {

                Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();

            }
        });


    }

    @OnClick(R.id.btnGet)
    public void get() {


    }

    @OnClick(R.id.btnShare)
    public void share() {




    }


}
