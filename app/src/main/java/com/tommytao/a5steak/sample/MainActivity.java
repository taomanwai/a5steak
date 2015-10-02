package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;

import com.tommytao.a5steak.util.DeviceInfoManager;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        BosonNlpManager.getInstance().init(this, "Se9DNydp.3665.8FudbgZo3UGG");


        

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

    @OnClick(R.id.btnChoose)
    public void choose() {


    }


    public Point calculateHidingPlace(View view){

        Point result = new Point();
        int resultX = -1;
        int resultY = -1;

        int viewHeight = view.getMeasuredHeight();

        Point screenSize = DeviceInfoManager.getInstance().getScreenSize();
        int screenWidth = screenSize.x;
        int screenHeight = screenSize.y;

        resultX = screenWidth / 2 ;
        resultY = screenHeight + viewHeight;

        result.set(resultX, resultY);

        return result;

    }



}
