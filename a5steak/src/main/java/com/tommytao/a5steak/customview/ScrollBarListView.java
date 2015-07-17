package com.tommytao.a5steak.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by tommytao on 17/7/15.
 */
public class ScrollBarListView extends ListView {

    public ScrollBarListView(Context context) {
        super(context);
    }

    public ScrollBarListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollBarListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

    @Override
    public int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }

    @Override
    public int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }
}
