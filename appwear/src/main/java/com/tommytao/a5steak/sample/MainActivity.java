package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.Button;
import android.widget.TextView;

import com.tommytao.a5steak.wear.DataLayerApiManager;

import java.util.HashMap;

public class MainActivity extends Activity implements DataLayerApiManager.OnDataListener {
    Button btnSend;

    private TextView tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataLayerApiManager.getInstance().init(this);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                tvMsg = (TextView) stub.findViewById(R.id.tvMsg);
                btnSend = (Button) stub.findViewById(R.id.btnSend);


            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        DataLayerApiManager.getInstance().connect(new DataLayerApiManager.OnConnectListener() {
            @Override
            public void onConnected(boolean succeed, String errMsg) {
                tvMsg.setText("connect " + succeed );

                if (!succeed)
                    return;

                DataLayerApiManager.getInstance().addOnDataListener(MainActivity.this);

            }
        });
    }

    @Override
    protected void onPause() {

        DataLayerApiManager.getInstance().removeOnDataListener(MainActivity.this);
        DataLayerApiManager.getInstance().disconnect();

        super.onPause();
    }

    @Override
    public void onChanged(String path, HashMap<String, String> data) {

        String content = "";
        try{

            content = data.get("content");
        } catch (Exception e){
            e.printStackTrace();
        }

        tvMsg.setText("change " + path + " " + content);

    }

    @Override
    public void onDeleted(String path) {


        tvMsg.setText("del path: " + path);

    }
}
