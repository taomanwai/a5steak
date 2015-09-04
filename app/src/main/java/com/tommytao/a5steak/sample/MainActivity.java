package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.face.Face;
import com.tommytao.a5steak.util.BitmapManager;
import com.tommytao.a5steak.util.FbManager;
import com.tommytao.a5steak.util.google.BarcodeManager;
import com.tommytao.a5steak.util.google.FaceManager;

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

        FbManager.getInstance().init(this);

        FaceManager.getInstance().init(this);

        BarcodeManager.getInstance().init(this);


    }


    @OnClick(R.id.btnGo)
    public void go() {

        if (!FaceManager.getInstance().isOperational()){
            Toast.makeText(this, "face detection not ready", Toast.LENGTH_LONG).show();
            return;
        }

        Bitmap bm = BitmapManager.getInstance().loadResId(R.drawable.face3, -1, -1, false);
//        bm = BitmapManager.getInstance().convertBitmapConfig(bm, Bitmap.Config.RGB_565);

        ArrayList<Face> faces = FaceManager.getInstance().findFacesFromBitmap(bm, true, true);

        Toast.makeText(this, "face num: " + faces.size(), Toast.LENGTH_LONG).show();




    }

    @OnClick(R.id.btnGet)
    public void get() {

        if (!BarcodeManager.getInstance().isOperational()){
            Toast.makeText(this, "barcode detection not ready", Toast.LENGTH_LONG).show();
            return;
        }

        Bitmap bm = BitmapManager.getInstance().loadResId(R.drawable.barcode2, -1, -1, false);
//        bm = BitmapManager.getInstance().convertBitmapConfig(bm, Bitmap.Config.RGB_565);

        ArrayList<Barcode> barcodes = BarcodeManager.getInstance().findBarcodesFromBitmap(bm);

        Toast.makeText(this, "barcodes num: " + barcodes.size(), Toast.LENGTH_LONG).show();




    }

    @OnClick(R.id.btnShare)
    public void share() {




    }


}
