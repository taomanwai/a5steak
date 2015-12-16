package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tommytao.a5steak.ai.ApiAiManager;

import java.util.Locale;


public class MainActivity extends Activity {


    private Button btnGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGo = (Button) findViewById(R.id.btnGo);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ApiAiManager.getInstance().init(MainActivity.this, "", "", new Locale("zh", "HK"));


            }
        });





    }






}
