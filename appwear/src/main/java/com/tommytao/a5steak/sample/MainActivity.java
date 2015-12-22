package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tommytao.a5steak.wear.MessageApiManager;

public class MainActivity extends Activity implements MessageApiManager.OnMessageListener{
    Button btnSend;

    private TextView tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MessageApiManager.getInstance().init(this);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                tvMsg = (TextView) stub.findViewById(R.id.tvMsg);
                btnSend = (Button) stub.findViewById(R.id.btnSend);

                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MessageApiManager.getInstance().send("hello", new MessageApiManager.OnSendListener() {
                            @Override
                            public void onComplete(boolean succeed) {
                                tvMsg.setText("send: " + succeed);
                            }
                        });
                    }
                });


            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        MessageApiManager.getInstance().connect(new MessageApiManager.OnConnectListener() {
            @Override
            public void onConnected(boolean succeed, String errMsg) {

                tvMsg.setText("connect: " + succeed);

                if (!succeed)
                    return;

                MessageApiManager.getInstance().searchNodeId(new MessageApiManager.OnSearchNodeIdListener() {
                    @Override
                    public void onComplete(boolean succeed) {

                        tvMsg.setText("search id: " + succeed);
                        MessageApiManager.getInstance().addOnMessageListener(MainActivity.this);
                    }
                });




            }
        });
    }

    @Override
    protected void onPause() {

        MessageApiManager.getInstance().removeOnMessageListener(MainActivity.this);
        MessageApiManager.getInstance().disconnect();

        super.onPause();
    }



    @Override
    public void onReceive(String message) {

        tvMsg.setText("recv: " + message + " ");

    }
}
