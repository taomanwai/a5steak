package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;

import com.tommytao.a5steak.customview.google.FaceCamView;
import com.tommytao.a5steak.util.SemanticsManager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    @Bind(R.id.cam)
    FaceCamView cam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        SemanticsManager.getInstance().init(this, "Se9DNydp.3665.8FudbgZo3UGG");


        SemanticsManager.getInstance().getKeyword("他是个傻逼", new SemanticsManager.OnGetKeywordListener() {
            @Override
            public void onComplete(ArrayList<SemanticsManager.Keyword> keywords) {

            }
        });

    }


    @OnClick(R.id.btnGo)
    public void go() {





    }

    @OnClick(R.id.btnGet)
    public void get() {




    }

    @OnClick(R.id.btnShare)
    public void share() {

    }

    @OnClick(R.id.btnChoose)
    public void choose() {


    }


}
