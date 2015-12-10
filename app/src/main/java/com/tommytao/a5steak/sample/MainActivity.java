package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tommytao.a5steak.util.BitmapManager;
import com.tommytao.a5steak.util.NotificationBarManager;

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
                NotificationBarManager.getInstance().init(MainActivity.this);


                BitmapManager.getInstance().init(MainActivity.this);
                Bitmap bm = null;
                bm = BitmapManager.getInstance().loadResId(R.drawable.androidifysteve, -1, -1, false , false);
                NotificationBarManager.getInstance().notificate(R.drawable.maneuver_turn_left, "title", "msg", bm, "", false, false, true, 2048, null);


            }
        });









    }






}
