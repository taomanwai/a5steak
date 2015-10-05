package com.tommytao.a5steak.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.tommytao.a5steak.util.Encyclopedia;
import com.tommytao.a5steak.util.google.GeocodeManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GeocodeManager.getInstance().get(Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {
                Log.d("","");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }


}
