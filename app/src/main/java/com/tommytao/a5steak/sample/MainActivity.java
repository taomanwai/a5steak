package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.vision.barcode.Barcode;
import com.tommytao.a5steak.customview.google.BarcodeCamView;
import com.tommytao.a5steak.util.DeviceInfoManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    @Bind(R.id.barcodeCamView)
    BarcodeCamView barcodeCamView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        BosonNlpManager.getInstance().init(this, "Se9DNydp.3665.8FudbgZo3UGG");

        barcodeCamView.setListener(new BarcodeCamView.Listener() {
            @Override
            public void onCreate(int id, Barcode barcode) {
                Log.d("", "barcode_t: create id: " + id + " barcode " + barcode.rawValue + " " + barcodeCamView.getIdBarcodesOnScreen().size());
            }

            @Override
            public void onUpdate(int id, Barcode barcode) {
                Log.d("", "barcode_t: update id: " + id + " barcode " + barcode.rawValue + " " + barcodeCamView.getIdBarcodesOnScreen().size());

            }

            @Override
            public void onDelete(int id, Barcode barcode) {
                Log.d("", "barcode_t: delete id: " + id + " barcode " + barcode.rawValue + " " + barcodeCamView.getIdBarcodesOnScreen().size());

            }
        });

        barcodeCamView.start();

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
