package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.vision.face.Face;
import com.tommytao.a5steak.customview.FaceView;
import com.tommytao.a5steak.util.BitmapManager;
import com.tommytao.a5steak.util.FbManager;
import com.tommytao.a5steak.util.google.BarcodeSensor;
import com.tommytao.a5steak.util.google.FaceSensor;

import java.util.ArrayList;

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

        if (!FaceSensor.getInstance().isOperational()){
            Toast.makeText(this, "face detection not ready", Toast.LENGTH_LONG).show();
            return;
        }

        Bitmap bm = BitmapManager.getInstance().loadResId(R.drawable.face3, -1, -1, false);

        ArrayList<Face> faces = FaceSensor.getInstance().findFacesFromBitmap(bm, true, true);

        Toast.makeText(this, "face num: " + faces.size(), Toast.LENGTH_LONG).show();

        fvMain.setImageBitmap(bm, faces);




    }

    @OnClick(R.id.btnGet)
    public void get() {




    }

    @OnClick(R.id.btnShare)
    public void share() {




    }


}
