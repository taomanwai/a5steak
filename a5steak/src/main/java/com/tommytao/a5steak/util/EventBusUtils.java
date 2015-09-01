package com.tommytao.a5steak.util;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Response for EventBus stuff (e.g. enforce EventBus onEvent() following main thread handler handling sequence)
 * <p/>
 * Warning: EventBus lib must be installed
 */
public class EventBusUtils {

    public static class BundledEvent {

        public String event;

        private HashMap<String, Object> hashMap = new HashMap<>();

        public BundledEvent(String event) {

            if (event == null)
                event = "";

            this.event = event;

        }

        public BundledEvent put(String key, Object value) {
            hashMap.put(key, value);

            return this;
        }

        public Object get(String key) {
            return hashMap.get(key);
        }

        public boolean isMatching(String event) {

            if (event == null)
                return false;

            return this.event.equals(event);

        }

    }

    public static void post(final java.lang.Object event) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(event);
            }
        });

    }

    public static void postSticky(final java.lang.Object event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().postSticky(event);
            }
        });
    }

    public static void removeSticky(final java.lang.Object event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().removeStickyEvent(event);
            }
        });
    }

    public static void register(final java.lang.Object subscriber) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().register(subscriber);
            }
        });
    }

    public static void registerSticky(final java.lang.Object subscriber) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().registerSticky(subscriber);
            }
        });
    }

    public static void unregister(final java.lang.Object subscriber) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().unregister(subscriber);
            }
        });
    }

}
