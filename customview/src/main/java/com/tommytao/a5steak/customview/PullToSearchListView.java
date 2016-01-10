package com.tommytao.a5steak.customview;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by tommytao on 10/1/2016.
 */
public class PullToSearchListView extends ListView {

    public static class PullToSearchHeader extends RelativeLayout {

        private EditText editText;

        public PullToSearchHeader(Context context) {
            super(context);
            init(context);
            initEditText(context);
        }

        public PullToSearchHeader(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
            initEditText(context);
        }

        private void init(Context context){

            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, 0);
            this.setLayoutParams(lp);

        }

        private void initEditText(Context context){

            editText = new EditText(context);

            RelativeLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

            lp.addRule(RelativeLayout.CENTER_VERTICAL);

            editText.setLayoutParams(lp);

            this.addView(editText);

        }

        public EditText getEditText() {
            return editText;
        }
    }


    private float latestY = -1;
    private float latestDeltaY= -1;

    private PullToSearchHeader header;


    public PullToSearchListView(Context context) {
        super(context);
        init(context);
    }

    public PullToSearchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullToSearchListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){

        header = new PullToSearchHeader(context);
        addHeaderView(header);

    }

    public PullToSearchHeader getHeader(){
        return header;
    }

    public EditText getHeaderEditText(){

        if (getHeader()==null)
            return null;

        return getHeader().getEditText();

    }

    public void addHeaderEditTextTextChangedListener(TextWatcher textWatcher) {

        getHeaderEditText().addTextChangedListener(textWatcher);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

//        if (latestY == -1){
//            ev.getRawY();
//        }
//
//        switch(ev.getAction()){
//            case MotionEvent.ACTION_DOWN:
//
//                latestY = ev.getRawY();
//
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//
//                break;
//
//            case MotionEvent.ACTION_UP:
//
//                break;
//
//
//            default:
//
//                break;
//        }



        return super.onTouchEvent(ev);
    }

}
