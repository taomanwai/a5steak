package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {

    public static final String CLIENT_ACCESS_TOKEN = "6e68a86687cb4913be51644042d55830";

    public static final String SUBSCRIPTION_KEY = "7e4b7c8d-a455-4125-8c95-18e3db6cfbc9";

    private Button btnGo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ApiAiManager.getInstance().init(this, CLIENT_ACCESS_TOKEN, SUBSCRIPTION_KEY, new Locale("zh", "HK"));
//
//        btnGo = (Button) findViewById(R.id.btnGo);
//
//        btnGo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                ApiAiManager.getInstance().analyze("大圍去沙田", new ApiAiManager.Listener() {
//                    @Override
//                    public void returnApiAiResult(ApiAiManager.ApiAiResult apiResult) {
//                        String action = apiResult.getAction();
//                        HashMap<String, String> parameters = apiResult.getParameters();
//
//                        Log.d("", "");
//
//                    }
//                });
//
//
//            }
//        });









    }






}
