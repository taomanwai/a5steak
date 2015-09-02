package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.FaceDetector;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.tommytao.a5steak.util.BitmapManager;
import com.tommytao.a5steak.util.google.VisionManager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {



    @Bind(R.id.ivMain)
    ImageView ivMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        BitmapManager.getInstance().init(this);


    }


    @OnClick(R.id.btnGo)
    public void go() {

        Bitmap bm = BitmapManager.getInstance().loadResId(R.drawable.face3, -1, -1, false);


        ArrayList<FaceDetector.Face> faces = VisionManager.getInstance().findFacesFromBitmap(bm, 10);

        Log.d("","");



    }

    @OnClick(R.id.btnGet)
    public void get() {






    }

    @OnClick(R.id.btnShare)
    public void share() {




    }


}
