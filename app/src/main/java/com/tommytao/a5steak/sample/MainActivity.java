package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.tommytao.a5steak.util.UxUtils;

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


    private class DataAdapter extends BaseAdapter {

        public class ViewHolder {
            public TextView tvMsg;
        }

        private Context ctx;

        public DataAdapter(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return 50;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Log.d("rtemp", "get_view_t : start");

            if (convertView==null){

                Log.d("rtemp", "get_view_t : null");

                convertView = LayoutInflater.from(ctx).inflate(R.layout.data_listitem, null);
                TextView tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
                ViewHolder holder = new ViewHolder();
                holder.tvMsg = tvMsg;
                convertView.setTag(holder);


            }

            ((ViewHolder) convertView.getTag()).tvMsg.setText("" + position);

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


    @Bind(R.id.listViewMain)
    ListView listViewMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        BosonNlpManager.getInstance().init(this, "Se9DNydp.3665.8FudbgZo3UGG");

        listViewMain.setAdapter(new DataAdapter(this));

        listViewMain.setRecyclerListener(new AbsListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {

                TextView tvMsg = ((DataAdapter.ViewHolder) view.getTag()).tvMsg;

                UxUtils.clearTextViewAnimTo(tvMsg, "" + tvMsg.getText(), 1.0f);
            }
        });





    }


    @OnClick(R.id.btnGo)
    public void go() {

//        UxUtils.fadeOutView(
//                ((DataAdapter.ViewHolder) listViewMain.getChildAt(0).getTag()).tvMsg, 10000, new LinearInterpolator(), null);
//
//        TextView tvMsg = ((DataAdapter.ViewHolder) listViewMain.getChildAt(0).getTag()).tvMsg;
//        String s = "" + tvMsg.getText();
//
//        Toast.makeText(this, s, Toast.LENGTH_LONG).show();

        UxUtils.fadeView(((DataAdapter.ViewHolder) listViewMain.getChildAt(0).getTag()).tvMsg,
                0.3f, 1.0f, 5000, new LinearInterpolator(), null);




    }

    @OnClick(R.id.btnGet)
    public void get() {





    }



}
