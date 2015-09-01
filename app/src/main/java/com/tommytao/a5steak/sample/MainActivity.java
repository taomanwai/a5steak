package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.tommytao.a5steak.util.FbManager;
import com.tommytao.a5steak.util.google.GFoundation;
import com.tommytao.a5steak.util.google.GPlusManager;

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

    }

    @OnClick(R.id.btnGet)
    public void get() {

        GPlusManager.getInstance().getLastKnownToken(new GPlusManager.OnGetLastKnownTokenListener() {
            @Override
            public void onCompleted(String token) {
                Toast.makeText(MainActivity.this, "token: " + token, Toast.LENGTH_LONG).show();
            }
        });

    }

    @OnClick(R.id.btnShare)
    public void share() {

        GPlusManager.getInstance().init(this);
        GPlusManager.getInstance().connect(new GFoundation.OnConnectListener() {
            @Override
            public void onConnected(boolean succeed) {

                if (succeed){
                    Toast.makeText(MainActivity.this, "onConnected:  ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "onFailed: " , Toast.LENGTH_LONG).show();

                }


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
