package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.tommytao.a5steak.customview.FaceView;
import com.tommytao.a5steak.util.BitmapManager;
import com.tommytao.a5steak.util.FbManager;
import com.tommytao.a5steak.util.google.BarcodeSensor;
import com.tommytao.a5steak.util.google.FaceSensor;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {



    @Bind(R.id.fvMain)
    FaceView fvMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        BitmapManager.getInstance().init(this);

        FbManager.getInstance().init(this);

        FaceSensor.getInstance().init(this);

        BarcodeSensor.getInstance().init(this);

        fvMain.setDrawBoundary(false);
        fvMain.setDrawLandmark(true);



    }


    @OnClick(R.id.btnGo)
    public void go() {

        FbManager.getInstance().getYearClass(new FbManager.OnGetYearClassListener() {
            @Override
            public void onComplete(int yearClass) {
                Toast.makeText(MainActivity.this, "year class: " + yearClass, Toast.LENGTH_LONG).show();
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
