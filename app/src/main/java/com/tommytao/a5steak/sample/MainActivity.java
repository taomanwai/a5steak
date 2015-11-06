package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    @Bind(R.id.btnGo)
    Button btnGo;

    @Bind(R.id.btnGet)
    Button btnGet;

    @Bind(R.id.tvMsg)
    TextView tvMsg;


    @Bind(R.id.listViewMain)
    ListView listViewMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);


    }


    @OnClick(R.id.btnGo)
    public void go() {


    }

    @OnClick(R.id.btnGet)
    public void get() {


    }


}
