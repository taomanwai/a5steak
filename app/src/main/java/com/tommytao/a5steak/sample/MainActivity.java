package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.tommytao.a5steak.customview.PullToSearchListView;
import com.tommytao.a5steak.system.DeviceInfoManager;


public class MainActivity extends Activity {

    public class MainAdapter extends BaseAdapter {

        private Context context;

        public MainAdapter(Context context) {

            this.context = context;
        }

        @Override
        public int getCount() {
            return 30;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView==null){
                convertView = LayoutInflater.from(context).inflate(R.layout.listitem_main, null);
            }

            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    private PullToSearchListView plvQuery;

    private ImageView iv_Test;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_Test = (ImageView) findViewById(R.id.iv_Test);

        DeviceInfoManager.getInstance().init(this);




        plvQuery = (PullToSearchListView) findViewById(R.id.plvQuery);

        plvQuery.setAdapter(new MainAdapter(this));

        plvQuery.addHeaderEditTextTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(MainActivity.this, plvQuery.getHeaderEditText().getText().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });







    }


}
