package com.tommytao.a5steak.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.tommytao.a5steak.util.google.DirectionsApiManager;
import com.tommytao.a5steak.util.google.GeocodeManager;
import com.tommytao.a5steak.util.google.PlacesApiManager;
import com.tommytao.a5steak.util.google.TextSpeaker;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.ediInput)
    EditText ediInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);


        TextSpeaker.getInstance().init(this, new Locale("zh", "HK"));
        GeocodeManager.getInstance().init(this, "gme-easyvanhongkonglimited", "RglSWAR2KO9R2OghAMwyj4WqIXg=");
//        GeocodeManager.getInstance().init(this, "", "");

        DirectionsApiManager.getInstance().init(this, "gme-easyvanhongkonglimited", "RglSWAR2KO9R2OghAMwyj4WqIXg=");

        PlacesApiManager.getInstance().init(this, "AIzaSyDho8iArjPHWI7GiY1xGhefeB6LplFucdI");







    }

    @OnClick(R.id.btnSpeak)
    public void speak(){

//        GeocodeManager.getInstance().get(22.425218, 114.238208, new Locale("zh", "HK"), new GeocodeManager.OnGetListener() {
//            @Override
//            public void returnGeocode(GeocodeManager.Geocode geocode) {
//                Log.d("", "");
//            }
//        });

//        DirectionsApiManager.getInstance().route(22.425218, 114.238208, 22.425218, 114.238218, new Locale("zh", "HK"), new DirectionsApiManager.OnRouteListener() {
//            @Override
//            public void returnStepList(ArrayList<DirectionsApiManager.Step> stepList) {
//                Log.d("", "");
//            }
//        });

        PlacesApiManager.getInstance().getPlaceFromPlaceId("ChIJ_zlGSrMIBDQRvDT41ncaC2c", new Locale("zh", "HK"), new PlacesApiManager.OnGetPlaceListener() {
            @Override
            public void returnPlace(PlacesApiManager.Place place) {
                Log.d("", "");
            }
        });

//        22.425218, 114.238208

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
