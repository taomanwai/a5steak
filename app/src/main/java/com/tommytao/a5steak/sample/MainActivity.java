package com.tommytao.a5steak.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.tommytao.a5steak.util.Encyclopedia;
import com.tommytao.a5steak.util.google.GeocodeManager;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.tvMsg)
    TextView tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);



        GeocodeManager.getInstance().searchByBounds("長沙灣政府合署",
                22.1533884, 113.835078, 22.561968, 114.4069561, new Locale("en", "US"), new GeocodeManager.OnSearchListener() {
                    @Override
                    public void returnPOIPoints(ArrayList<GeocodeManager.POIPoint> poiPoints, String keyword) {
                        Log.d("","");
                    }
                }
        );


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }


}
