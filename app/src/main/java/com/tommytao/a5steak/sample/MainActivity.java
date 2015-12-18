package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tommytao.a5steak.wear.DataLayerApiManager;

import java.util.HashMap;


public class MainActivity extends Activity {


    private Button btnSend, btnRemove;
    private TextView tvMsg;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSend = (Button) findViewById(R.id.btnSend);
        btnRemove = (Button) findViewById(R.id.btnRemove);


        tvMsg = (TextView) findViewById(R.id.tvMsg);

        DataLayerApiManager.getInstance().connect(new DataLayerApiManager.OnConnectListener() {
            @Override
            public void onConnected(boolean succeed, String errMsg) {
                tvMsg.setText("connect_t: " + succeed);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, String> h = new HashMap<String, String>();
                h.put("content", "hehe " + System.currentTimeMillis());
                DataLayerApiManager.getInstance().put("/data", h, new DataLayerApiManager.OnPutListener() {
                    @Override
                    public void onComplete(boolean succeed, Uri uri) {
                        if (succeed) {
                            tvMsg.setText("send: " + succeed);

                            MainActivity.this.uri = uri;
                        }
                    }
                });

            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataLayerApiManager.getInstance().delete(uri);
            }
        });




    }






}
