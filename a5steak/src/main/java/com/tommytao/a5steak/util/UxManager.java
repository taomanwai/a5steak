package com.tommytao.a5steak.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
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
public class UxManager extends Foundation {

    private static UxManager instance;

    public static UxManager getInstance() {

        if (instance == null) ;
        instance = new UxManager();

        return instance;
    }

    private UxManager() {

    }

    // --

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


    public void setNumberPickerDividerColor(NumberPicker picker, int color) {

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


    public void showKeyboard(Context context, View view) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(2, 0);
        if (view != null)
            view.requestFocus();

    }

    public void hideKeyboard(Context context, View view) {

        if (view == null && context instanceof Activity)
            view = ((Activity) context).getWindow().getDecorView();

        // Still cannot get valid view, return
        if (view == null)
            return;

        InputMethodManager inputmethodmanager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputmethodmanager != null && view != null)
            inputmethodmanager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public void outOfFocusOnEditText(EditText editText) {

        if (null == editText)
            return;

        editText.setFocusableInTouchMode(false);

    }

    public void focusOnEditText(Activity activity, EditText editText, boolean isShowKeyboard) {

        if (null == editText)
            return;

        editText.setFocusableInTouchMode(true);

        editText.requestFocus();

        if (isShowKeyboard)
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editText, 0);

    }

    public void fadeChangeTextViewText(final TextView textView, final String text, final int durationInMs, final Listener listener) {

        if (null == textView)
            return;

        final int halfDurationInMs = durationInMs / 2;

        UxManager.getInstance().fadeOutView(textView, halfDurationInMs, new Listener() {
            @Override
            public void onComplete() {

                textView.setText(text);

                UxManager.getInstance().fadeInView(textView, halfDurationInMs, new Listener() {
                    @Override
                    public void onComplete() {

                        if (listener != null)
                            listener.onComplete();

                    }
                });


            }
        });

    }

    public TextView getToolBarTextView(Toolbar mToolBar) {

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

    public void slideDownHideView(final View view, int durationInMs, final Listener listener) {

        if (null == view)
            return;

        if (view.getVisibility() != View.VISIBLE) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (listener!=null)
                        listener.onComplete();

                }
            }, durationInMs);

            return;
        }

        final Animation slideDown =
                new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0.0f, TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                        TranslateAnimation.RELATIVE_TO_SELF, 0.0f, TranslateAnimation.RELATIVE_TO_SELF, 1.0f);
        slideDown.setDuration(durationInMs);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
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

        view.startAnimation(slideDown);
    }

    public void slideUpShowView(final View view, int durationInMs, final Listener listener) {

        if (null == view)
            return;

        if (view.getVisibility() == View.VISIBLE) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (listener!=null)
                        listener.onComplete();

                }
            }, durationInMs);

            return;
        }

        final Animation slideUp =
                new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0.0f, TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                        TranslateAnimation.RELATIVE_TO_SELF, 1.0f, TranslateAnimation.RELATIVE_TO_SELF, 0.0f);
        slideUp.setDuration(durationInMs);
        slideUp.setAnimationListener(new Animation.AnimationListener() {
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

        view.startAnimation(slideUp);
    }


    // ===

    public void slideDownShowView(final View view, int durationInMs, final Listener listener) {

        if (null == view)
            return;

        if (view.getVisibility() == View.VISIBLE) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (listener!=null)
                        listener.onComplete();

                }
            }, durationInMs);

            return;
        }

        final Animation slideDown =
                new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0.0f, TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                        TranslateAnimation.RELATIVE_TO_SELF, -1.0f, TranslateAnimation.RELATIVE_TO_SELF, 0.0f);
        slideDown.setDuration(durationInMs);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
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

        view.startAnimation(slideDown);
    }

    public void slideUpHideView(final View view, int durationInMs, final Listener listener) {

        if (null == view)
            return;

        if (view.getVisibility() != View.VISIBLE) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (listener!=null)
                        listener.onComplete();

                }
            }, durationInMs);

            return;
        }

        final Animation slideUp =
                new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0.0f, TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                        TranslateAnimation.RELATIVE_TO_SELF, 0.0f, TranslateAnimation.RELATIVE_TO_SELF, -1.0f);
        slideUp.setDuration(durationInMs);
        slideUp.setAnimationListener(new Animation.AnimationListener() {
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

        view.startAnimation(slideUp);
    }


    // ===


    public void slideLeftShowView(final View view, int durationInMs, final Listener listener) {

        if (null == view)
            return;

        if (view.getVisibility() == View.VISIBLE) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (listener!=null)
                        listener.onComplete();

                }
            }, durationInMs);

            return;
        }

        final Animation slideDown =
                new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 1.0f, TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                        TranslateAnimation.RELATIVE_TO_SELF, 0.0f, TranslateAnimation.RELATIVE_TO_SELF, 0.0f);
        slideDown.setDuration(durationInMs);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
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

        view.startAnimation(slideDown);
    }

    public void slideRightHideView(final View view, int durationInMs, final Listener listener) {

        if (null == view)
            return;

        if (view.getVisibility() != View.VISIBLE) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (listener!=null)
                        listener.onComplete();

                }
            }, durationInMs);

            return;
        }

        final Animation slideUp =
                new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0.0f, TranslateAnimation.RELATIVE_TO_SELF, 1.0f,
                        TranslateAnimation.RELATIVE_TO_SELF, 0.0f, TranslateAnimation.RELATIVE_TO_SELF, 0.0f);
        slideUp.setDuration(durationInMs);
        slideUp.setAnimationListener(new Animation.AnimationListener() {
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

        view.startAnimation(slideUp);
    }


    // ===


//    public void circularReveal(View view, int centerX, int centerY, int fromRadius, int toRadius, int durationInMs, final Listener listener) {
//
//        if (null == view)
//            return;
//
//        view.setVisibility(View.VISIBLE);
//
//        SupportAnimator animator = io.codetail.animation.ViewAnimationUtils.createCircularReveal(
//                view, centerX, centerY, fromRadius, toRadius);
//        animator.setInterpolator(new AccelerateDecelerateInterpolator());
//        animator.setDuration(durationInMs);
//        animator.start();
//
//        animator.addListener(new SupportAnimator.AnimatorListener() {
//            @Override
//            public void onAnimationStart() {
//
//            }
//
//            @Override
//            public void onAnimationEnd() {
//
//
//                if (listener != null)
//                    listener.onComplete();
//
//            }
//
//            @Override
//            public void onAnimationCancel() {
//
//            }
//
//            @Override
//            public void onAnimationRepeat() {
//
//            }
//        });
//
//    }

    public void slideView(final View view, int fromX, int toX, int fromY, int toY, float fromAlpha, float toAlpha, final long durationInMs, final Listener listener) {

        AnimationSet animSet = new AnimationSet(true);
        Animation slideAnim = new TranslateAnimation(fromX, toX, fromY, toY);
        slideAnim.setDuration(durationInMs);

        animSet.addAnimation(slideAnim);

        Animation fadeOutAnim = new AlphaAnimation(fromAlpha, toAlpha);
        fadeOutAnim.setDuration(durationInMs);
        animSet.addAnimation(fadeOutAnim);


        view.setVisibility(View.VISIBLE);

        if (listener != null) {

            animSet.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    listener.onComplete();

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }

        view.startAnimation(animSet);

    }


    public void fadeInView(final View view, final long durationInMs, final Listener listener) {

        if (null == view)
            return;

        if (View.VISIBLE == view.getVisibility())
            return;

        Animation anim = new AlphaAnimation(0, 1.0f);
        anim.setDuration(durationInMs);
        view.setVisibility(View.VISIBLE);

        if (listener != null) {

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    listener.onComplete();

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
        view.startAnimation(anim);
    }

    public void shakeView(final View view) {

        if (null == view)
            return;

        view.startAnimation(AnimationUtils.loadAnimation(appContext, R.anim.anim_sideshake));

    }

    // TODO MVP seems having performance issue
    public void fadeOutView(final View view, final long durationInMs, final Listener listener) {

        if (null == view)
            return;

        Animation anim = new AlphaAnimation(1.0f, 0);
        anim.setDuration(durationInMs);
        view.setVisibility(View.VISIBLE);

        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                if (listener != null)
                    listener.onComplete();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

        });

        view.startAnimation(anim);
    }

    public void assignLayoutWidth(ViewGroup layout, int width) {

        LayoutParams p = layout.getLayoutParams();
        p.width = width;

        layout.setLayoutParams(p);

    }

    public void assignLayoutHeight(ViewGroup layout, int height) {

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
    public void setListViewHeightBasedOnChildren(ListView listView) {
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


    private ViewGroup getParent(View view) {
        return (ViewGroup) view.getParent();
    }

    public void removeView(View view) {
        ViewGroup parent = getParent(view);
        if (parent != null) {
            parent.removeView(view);
        }
    }

    public void replaceView(View currentView, View newView) {
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
