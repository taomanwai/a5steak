package com.tommytao.a5steak.util.test;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.SystemClock;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;
import android.view.View;

import com.tommytao.a5steak.util.NetworkInfoManager;

import org.hamcrest.Matcher;

import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by tommytao on 20/11/2015.
 */
public class TestCaseUtils {

    public static final int GENERAL_DELAY_IN_MS = 3 * 1000;

    // ==

    public static class ClickRecyclerChildViewAction {

        public static ViewAction clickChildViewWithId(final int id) {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return null;
                }

                @Override
                public String getDescription() {
                    return "Click on a child view of recyclerview with specified id.";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    View v = view.findViewById(id);
                    if (v != null) {
                        v.performClick();
                    }
                }
            };
        }

    }

    public static ViewAction actionOpenDrawer() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(DrawerLayout.class);
            }

            @Override
            public String getDescription() {
                return "open drawer";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((DrawerLayout) view).openDrawer(GravityCompat.START);
                SystemClock.sleep(650);
            }
        };
    }

    public static ViewAction actionCloseDrawer() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(DrawerLayout.class);
            }

            @Override
            public String getDescription() {
                return "close drawer";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((DrawerLayout) view).closeDrawer(GravityCompat.START);
                SystemClock.sleep(650);
            }
        };
    }


    public static void loopUntilWifiReady(Context ctx) {

        if (!NetworkInfoManager.getInstance().isInitialized())
            NetworkInfoManager.getInstance().init(ctx);

        while (!NetworkInfoManager.getInstance().isConnected()) {
        }

    }

    public static void hideKeyboard(int editTextResId) {
        onView(withId(editTextResId)).perform(ViewActions.closeSoftKeyboard());
    }

    public static int getRecyclerViewChildCount(Matcher<View> matcher) {
        final int[] count = {0};
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(RecyclerView.class);
            }

            @Override
            public String getDescription() {
                return "getting child count";
            }

            @Override
            public void perform(UiController uiController, View view) {
                RecyclerView rv = (RecyclerView) view;
                count[0] = rv.getChildCount();
            }
        });
        return count[0];
    }

    public static Activity getCurrentActivity(ActivityInstrumentationTestCase2 testCase) {
        try {
            testCase.getInstrumentation().waitForIdleSync();
            final Activity[] activity = new Activity[1];

            testCase.runTestOnUiThread(new Runnable() {
                @Override
                public void run() {

                    java.util.Collection<Activity> activites = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                    activity[0] = Iterables.getOnlyElement(activites);

                }
            });
            return activity[0];
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void assertToast(Activity activity, String text) {
        onView(withText(text)).inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    public static void changeActivityLocale(ActivityInstrumentationTestCase2 testCase, final Activity activity, Locale locale){

        Resources res = activity.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
        activity.getResources().updateConfiguration(conf, dm);

        testCase.getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                activity.recreate();
            }
        });

    }

    public static String getRandomStr(int length) {

        if (length > 9)
            length = 9;

        return String.format("%0" + length + "d", (int) (Math.random() * (Math.pow(10, length) - 1))); // 9999999

    }

}
