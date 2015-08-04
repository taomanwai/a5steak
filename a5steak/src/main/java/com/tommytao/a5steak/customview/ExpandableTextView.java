package com.tommytao.a5steak.customview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.TextView;

/**
 * Responsible for showing text which can be collapsed when text is too long
 * <p/>
 * Limitation:
 * height (must be wrap_content)
 * maxLines / singleLine (cannot be set)
 * ellipsize (cannot be set)
 * During expanding / collapsing, expand() and collapse() cannot be called (otherwise, unexpected effect may occurs)
 * <p/>
 * Created by tommytao on 6/7/15.
 */
public class ExpandableTextView extends TextView {

    public static interface OnExpandStateChangedListener {
        public void onChanged(boolean expanded);
    }

    public static final int DEFAULT_ANIM_DURATION_IN_MS = 300;

    protected int collapsedLineCount = 1;

    protected boolean expanded = true;

    protected Handler handler = new Handler(Looper.getMainLooper());

    protected OnExpandStateChangedListener onExpandStateChangedListener;

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ExpandableTextView(Context context) {
        super(context);
        init();
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void init() {
        this.setEllipsize(TextUtils.TruncateAt.END);
    }


    public void setOnExpandStateChangedListener(OnExpandStateChangedListener onExpandStateChangedListener) {
        this.onExpandStateChangedListener = onExpandStateChangedListener;
    }


    public int getCollapsedLineCount() {
        return collapsedLineCount;
    }

    /**
     * Initialize ExpandableTextView
     * <p/>
     * Note: Should be call in onCreate() / onCreateView / onViewCreated for one time only
     *
     * @param collapsedLineCount Initial collapsed line count
     * @param expandState        Initial expanded state
     */
    public void initExpandedTextView(int collapsedLineCount, boolean expandState) {

        if (collapsedLineCount < 1)
            collapsedLineCount = 1;

        this.collapsedLineCount = collapsedLineCount;

        if (!expandState) {
            this.expanded = false;
            this.setMaxLines(collapsedLineCount);
        }

    }

    public boolean isExpanded() {
        return expanded;
    }

    public void expand() {


        if (isExpanded()) {
            return;
        }


        this.setMaxLines(Integer.MAX_VALUE);

        final int initHeight = this.getMeasuredHeight();

        this.getLayoutParams().height = initHeight;
        this.requestLayout();

        this.setVisibility(View.VISIBLE);

        Animation anim = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                if (interpolatedTime == 1) {
                    getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                } else {
                    int targetDerivation = getLineCount() * getLineHeight() - initHeight;
                    if (targetDerivation<0)
                        targetDerivation = 0;
                    getLayoutParams().height = (int) (initHeight + targetDerivation * interpolatedTime); // (int) ((initLineCount + (getLineCount() - initLineCount) * interpolatedTime) * getLineHeight())
                }
                requestLayout();

            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }


        };

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                expanded = true;

                if (onExpandStateChangedListener != null)
                    onExpandStateChangedListener.onChanged(expanded);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        anim.setDuration(DEFAULT_ANIM_DURATION_IN_MS);
        anim.setInterpolator(new DecelerateInterpolator());
        startAnimation(anim);


    }

    public void collapse() {

        if (!isExpanded()) {
            return;
        }

        final int initHeight = getMeasuredHeight();

        if (initHeight <= (collapsedLineCount * getLineHeight())){

//            triggerExpandStateChangedListenerOnUiThread(false, DEFAULT_ANIM_DURATION_IN_MS);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    expanded = false;

                    if (onExpandStateChangedListener != null)
                        onExpandStateChangedListener.onChanged(expanded);
                }
            }, DEFAULT_ANIM_DURATION_IN_MS);

            return;
        }

        // ===
        Animation anim = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                if (interpolatedTime == 1) {
                    setMaxLines(collapsedLineCount);
                    getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                } else {
                    getLayoutParams().height = (int) (initHeight - (initHeight - collapsedLineCount * getLineHeight()) * interpolatedTime); // (int) ((collapsedLineCount + (getLineCount() - collapsedLineCount) * (1 - interpolatedTime)) * getLineHeight());
                }
                requestLayout();

            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }


        };

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                expanded = false;

                if (onExpandStateChangedListener != null)
                    onExpandStateChangedListener.onChanged(expanded);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        anim.setDuration(DEFAULT_ANIM_DURATION_IN_MS);
        anim.setInterpolator(new DecelerateInterpolator());
        startAnimation(anim);


    }


}
