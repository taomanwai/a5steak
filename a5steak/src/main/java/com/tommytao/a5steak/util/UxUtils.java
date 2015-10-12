package com.tommytao.a5steak.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tommytao.a5steak.R;

import java.lang.reflect.Field;

/**
 * Responsible for UX functions
 *
 * @author tommytao
 */
public class UxUtils {

    public static interface Listener {

        public void onComplete();

    }

    public static int DEFAULT_ANIM_DURATION_IN_MS = 300;
    public static float DEFAULT_ALPHA_SEMI_TRANSPARENT = 0.7f;

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public static void setNumberPickerDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    public static void showKeyboard(Context context, View view) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(2, 0);
        if (view != null)
            view.requestFocus();

    }

    public static void hideKeyboard(Context context, View view) {

        if (view == null && context instanceof Activity)
            view = ((Activity) context).getWindow().getDecorView();

        // Still cannot get valid view, return
        if (view == null)
            return;

        InputMethodManager inputmethodmanager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputmethodmanager != null && view != null)
            inputmethodmanager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public static void outOfFocusOnEditText(EditText editText) {

        if (null == editText)
            return;

        editText.setFocusableInTouchMode(false);

    }

    public static void focusOnEditText(Activity activity, EditText editText, boolean isShowKeyboard) {

        if (null == editText)
            return;

        editText.setFocusableInTouchMode(true);

        editText.requestFocus();

        if (isShowKeyboard)
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editText, 0);

    }

    public static void fadeChangeTextViewText(final TextView textView, final String text, final int durationInMs, final Listener listener) {

        if (null == textView)
            return;

        final int halfDurationInMs = durationInMs / 2;

        UxUtils.fadeOutView(textView, halfDurationInMs, new LinearInterpolator(), new Listener() {
            @Override
            public void onComplete() {

                textView.setText(text);

                UxUtils.fadeInView(textView, halfDurationInMs, new LinearInterpolator(), new Listener() {
                    @Override
                    public void onComplete() {

                        if (listener != null)
                            listener.onComplete();

                    }
                });


            }
        });

    }

    public static TextView getToolBarTextView(Toolbar mToolBar) {

        TextView titleTextView = null;

        try {
            Field f = mToolBar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (TextView) f.get(mToolBar);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return titleTextView;


    }

    public static void slideDownHideView(final View view, int delayInMs, int durationInMs, Interpolator interpolator, final Listener listener) {
        slideView(view, 0, 0, 0, view.getHeight(), 1.0f, 0.0f, delayInMs, durationInMs, interpolator, listener);
    }

    public static void slideUpShowView(final View view, int delayInMs, int durationInMs, Interpolator interpolator, final Listener listener) {
        slideView(view, 0, 0, view.getHeight(), 0, 1.0f, 1.0f, delayInMs, durationInMs, interpolator,  listener);
    }


    // ===

    public static void slideDownShowView(final View view, int delayInMs, int durationInMs, Interpolator interpolator, final Listener listener) {
        slideView(view, 0, 0, -view.getHeight(), 0, 1.0f, 1.0f, delayInMs, durationInMs, interpolator, listener);


    }

    public static void slideUpHideView(final View view, int delayInMs, int durationInMs, Interpolator interpolator, final Listener listener) {
        slideView(view, 0, 0, 0, -view.getHeight(), 1.0f, 0.0f, delayInMs, durationInMs, interpolator, listener);
    }


    // ===


    public static void slideLeftShowView(final View view, int delayInMs, int durationInMs, Interpolator interpolator, final Listener listener) {
        slideView(view, view.getWidth(), 0, 0, 0, 1.0f, 1.0f, delayInMs, durationInMs, interpolator, listener);
    }

    public static void slideRightHideView(final View view, int delayInMs, int durationInMs, Interpolator interpolator, final Listener listener) {
        slideView(view, 0, view.getWidth(), 0, 0, 1.0f, 0.0f, delayInMs, durationInMs, interpolator, listener);
    }


    // ===


    public static void slideView(final View view, int fromXDelta, int toXDelta, int fromYDelta, int toYDelta, float fromAlpha, final float toAlpha, final long delayInMs, final long durationInMs, Interpolator interpolator, final Listener listener) {

        if (view == null)
            return;

        AnimationSet animSet = new AnimationSet(true);
        Animation slideAnim = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);

        slideAnim.setStartOffset(delayInMs);
        slideAnim.setDuration(durationInMs);

        animSet.addAnimation(slideAnim);

        Animation fadeOutAnim = new AlphaAnimation(fromAlpha, toAlpha);
        fadeOutAnim.setDuration(durationInMs);
        animSet.addAnimation(fadeOutAnim);
        animSet.setInterpolator(interpolator);

        animSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (toAlpha == 0)
                    view.setVisibility(View.INVISIBLE);

                if (listener != null)
                    listener.onComplete();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(animSet);

    }

    public static void slideViewAbsolutely(final View view, final int fromX, final int fromY, final int toX, final int toY, final float fromAlpha, final float toAlpha, final long delayInMs, final long durationInMs, final Interpolator interpolator, final Listener listener){

        view.post(new Runnable() {
            @Override
            public void run() {

                int viewCenterX = view.getLeft() + view.getMeasuredWidth() / 2;
                int viewCenterY = view.getTop() + view.getMeasuredHeight() / 2;

                int fromXDelta = fromX - viewCenterX;
                int fromYDelta = fromY - viewCenterY;

                int toXDelta = toX - viewCenterX;
                int toYDelta = toY - viewCenterY;

                slideView(view, fromXDelta, toXDelta, fromYDelta, toYDelta, fromAlpha, toAlpha, delayInMs, durationInMs, interpolator, listener);

            }
        });

    }

    public static void slideViewTo(final View view, final int toX, final int toY, final float fromAlpha, final float toAlpha, final long delayInMs, final long durationInMs, final Interpolator interpolator, final Listener listener){

        view.post(new Runnable() {
            @Override
            public void run() {
                int viewCenterX = view.getLeft() + view.getMeasuredWidth() / 2;
                int viewCenterY = view.getTop() + view.getMeasuredHeight() / 2;

                slideViewAbsolutely(view, viewCenterX, viewCenterY, toX, toY, fromAlpha, toAlpha, delayInMs, durationInMs, interpolator, listener);

            }
        });

    }


    public static void fadeInView(final View view, final long durationInMs, Interpolator interpolator, final Listener listener) {

        if (null == view)
            return;

        Animation anim = new AlphaAnimation(0, 1.0f);
        anim.setDuration(durationInMs);
        anim.setInterpolator(interpolator);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (listener != null)
                    listener.onComplete();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        view.startAnimation(anim);
    }

    private static void playXmlAnim(Context ctx, final View view, final int resId, final Listener listener) {

        if (null == view)
            return;

        Animation anim = AnimationUtils.loadAnimation(ctx, resId);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (listener != null)
                    listener.onComplete();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(anim);

    }


    public static void blastViewForTwoAndHalfSecond(Context ctx, final View view, final Listener listener) {

        playXmlAnim(ctx, view, R.anim.blast, listener);

    }


    public static void doubleBlinkViewForHalfSecond(Context ctx, final View view, final Listener listener) {

        playXmlAnim(ctx, view, R.anim.double_blink, listener);

    }

    public static void shakeViewForOneSecond(Context ctx, final View view, final Listener listener) {

        playXmlAnim(ctx, view, R.anim.shake, listener);

    }

    public static void spinViewInfinitely(Context ctx, final View view, final Listener listener) {

        playXmlAnim(ctx, view, R.anim.spin, listener);

    }


    // TODO MVP seems having performance issue
    public static void fadeOutView(final View view, final long durationInMs, Interpolator interpolator, final Listener listener) {

        if (null == view)
            return;

        Animation anim = new AlphaAnimation(1.0f, 0);
        anim.setDuration(durationInMs);
        anim.setInterpolator(interpolator);


        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
                if (listener != null)
                    listener.onComplete();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

        });

        view.startAnimation(anim);
    }

    public static void clearAnimationTo(final View view, boolean visible) {

        if (null == view)
            return;

        float alphaValue = visible ? 1.0f : 0;

        Animation anim = new AlphaAnimation(alphaValue, alphaValue);
        anim.setDuration(0);

        view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        view.startAnimation(anim);

    }

    public static void assignLayoutWidth(ViewGroup layout, int width) {

        LayoutParams p = layout.getLayoutParams();
        p.width = width;

        layout.setLayoutParams(p);

    }

    public static void assignLayoutHeight(ViewGroup layout, int height) {

        LayoutParams p = layout.getLayoutParams();
        p.height = height;

        layout.setLayoutParams(p);

    }

    /**
     * Ref:
     * http://www.java2s.com/Code/Android/UI/setListViewHeightBasedOnChildren
     * .htm
     *
     * @param listView Listview which will be set
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null)
            return;

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if (listItem == null)
                continue;

            // This next line is needed before you call measure or else you
            // won't get measured height at all. The listitem needs to be
            // drawn first to know the height.
            listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    private static ViewGroup getParent(View view) {
        return (ViewGroup) view.getParent();
    }

    public static void removeView(View view) {
        ViewGroup parent = getParent(view);
        if (parent != null) {
            parent.removeView(view);
        }
    }

    public static void replaceView(View currentView, View newView) {
        ViewGroup parent = getParent(currentView);
        if (parent == null) {
            return;
        }
        final int index = parent.indexOfChild(currentView);
        removeView(currentView);
        removeView(newView);
        parent.addView(newView, index);
    }


}
