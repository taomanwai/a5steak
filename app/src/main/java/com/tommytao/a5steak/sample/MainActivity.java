package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import com.facebook.network.connectionclass.ConnectionQuality;
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

        final long time = SystemClock.elapsedRealtime();
        FbManager.getInstance().getConnectionClass(new FbManager.OnGetConnectionClassListener() {
            @Override
            public void onComplete(ConnectionQuality quality) {

                switch (quality){

                    case UNKNOWN:
                        Toast.makeText(MainActivity.this, "" + time + " unknown", Toast.LENGTH_LONG).show();
                        break;

                    case POOR:
                        Toast.makeText(MainActivity.this, "" + time + " POOR", Toast.LENGTH_LONG).show();
                        break;

                    case MODERATE:
                        Toast.makeText(MainActivity.this, "" + time + " MODERATE", Toast.LENGTH_LONG).show();
                        break;

                    case GOOD:
                        Toast.makeText(MainActivity.this, "" + time + " GOOD", Toast.LENGTH_LONG).show();
                        break;

                    case EXCELLENT:
                        Toast.makeText(MainActivity.this, "" + time + " EXCELLENT", Toast.LENGTH_LONG).show();
                        break;

                }


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
