package com.tommytao.a5steak.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

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




    }

    @OnClick(R.id.btnSpeak)
    public void speak(){

        TextSpeaker.getInstance().speak(ediInput.getText().toString(), new TextSpeaker.OnSpeakListener() {
            @Override
            public void onStart() {
                Toast.makeText(MainActivity.this, "start", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onComplete(boolean succeed) {
                Toast.makeText(MainActivity.this, "end", Toast.LENGTH_SHORT).show();

            }
        });

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
