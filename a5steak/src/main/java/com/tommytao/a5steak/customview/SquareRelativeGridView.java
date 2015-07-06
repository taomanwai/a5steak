package com.tommytao.a5steak.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SquareRelativeGridView extends RelativeLayout {
	
	public SquareRelativeGridView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		
	}

	public SquareRelativeGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public SquareRelativeGridView(Context context) {
		super(context);
		
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
}
