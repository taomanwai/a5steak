package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.tommytao.a5steak.util.FbManager;
import com.tommytao.a5steak.util.google.GPlusManager;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        FbManager.getInstance().init(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();


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



//        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i, 1234);

//        mGoogleApiClient.connect();

        GPlusManager.getInstance().init(this);
        GPlusManager.getInstance().connect(new GPlusManager.OnConnectListener() {
            @Override
            public void onConnected() {

                GPlusManager.getInstance().getLastKnownToken(new GPlusManager.OnGetLastKnownTokenListener() {
                    @Override
                    public void onCompleted(String token) {
                        Toast.makeText(MainActivity.this, "onConnected: token: " + token, Toast.LENGTH_LONG).show();
                    }
                });



            }

            @Override
            public void onFailed(int errorCode) {

                Toast.makeText(MainActivity.this, "onFailed: " + errorCode , Toast.LENGTH_LONG).show();

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                break;
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
                int gender = currentPerson.getGender();
                String plusId = currentPerson.getId();
                String personPhotoUrl = currentPerson.getImage().getUrl();

                String content = "PersonName:"+personName+"\n";
                content += "PersonEmail:"+personEmail+"\n";
                content += "Gender:"+gender+"\n";
                content += "PlusId:"+plusId;

                Toast.makeText(MainActivity.this, "onConnected: " + content, Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MainActivity.this, "onConnectionSuspended:", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, "onConnectionFailed: " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();

        try {
            connectionResult.startResolutionForResult(this, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
}
