package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.tommytao.a5steak.util.FbManager;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        FbManager.getInstance().init(this);


    }


    @OnClick(R.id.btnGo)
    public void go() {


        FbManager.getInstance().login(this, false, new ArrayList<String>(Arrays.asList("publish_actions")), new FbManager.OnLoginListener() {
            @Override
            public void onComplete(String token) {
                if (TextUtils.isEmpty(token)) {
                    Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this, "succeed " + token, Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @OnClick(R.id.btnGet)
    public void get() {

        FbManager.getInstance().getFriends(0, 10, new FbManager.OnGetFriendsListener() {
            @Override
            public void onComplete(ArrayList<FbManager.User> users, int totalCount) {


                if (totalCount == -1) {
                    Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(MainActivity.this, "totalCount " + totalCount, Toast.LENGTH_LONG).show();


            }
        });
    }

    @OnClick(R.id.btnShare)
    public void share() {



        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1234);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode==RESULT_OK){

            if (requestCode== 1234){

                Uri uri = data.getData();

                FbManager.getInstance().shareVideo(MainActivity.this, uri, new FbManager.OnShareListener() {
                    @Override
                    public void onComplete(String postId) {

                        Toast.makeText(MainActivity.this, "postId: " + postId, Toast.LENGTH_SHORT).show();

                    }
                });



            }

        }
    }
}
