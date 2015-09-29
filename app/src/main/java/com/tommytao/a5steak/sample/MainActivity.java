package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.tommytao.a5steak.util.google.TextSpeaker;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        BosonNlpManager.getInstance().init(this, "Se9DNydp.3665.8FudbgZo3UGG");
        TextSpeaker.getInstance().init(this);
        TextSpeaker.getInstance().connect(new TextSpeaker.OnConnectListener() {
            @Override
            public void onConnected(boolean succeed) {
            }
        });

    }


    public int add(int a, int b){
        return a + b;
    }


    @OnClick(R.id.btnGo)
    public void go() {

        TextSpeaker.getInstance().speak("一號歡迎使用導航", new Locale("zh", "HK"), new TextSpeaker.OnSpeakListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(boolean succeed) {
                Log.d("", "");
            }
        });

    }

    @OnClick(R.id.btnGet)
    public void get() {

        TextSpeaker.getInstance().speak("二號歡迎使用導航", new Locale("zh", "HK"), new TextSpeaker.OnSpeakListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(boolean succeed) {
                Log.d("", "");
            }
        });



    }

    @OnClick(R.id.btnShare)
    public void share() {

        TextSpeaker.getInstance().disconnect();

    }

    @OnClick(R.id.btnChoose)
    public void choose() {


    }


}
