package com.tommytao.a5steak.util;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Response for EventBus stuff (e.g. enforce EventBus onEvent() following main thread handler handling sequence)
 *
 * Warning: EventBus lib must be installed
 *
 */
public class EventBusManager {


    private static EventBusManager instance;

    public static EventBusManager getInstance() {

        if (instance == null)
            instance = new EventBusManager();

        return instance;
    }

    private EventBusManager() {

    }


    // --

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

    private Handler handler = new Handler(Looper.getMainLooper());

    public void post(final java.lang.Object event) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                EventBus.getDefault().post(event);

            }
        });

    }

    public void postSticky(final java.lang.Object event) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                EventBus.getDefault().postSticky(event);

            }
        });

    }

    public void removeSticky(final java.lang.Object event) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                EventBus.getDefault().removeStickyEvent(event);

            }
        });

    }

    public void register(final java.lang.Object subscriber) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                EventBus.getDefault().register(subscriber);

            }
        });

    }

    public void registerSticky(final java.lang.Object subscriber) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                EventBus.getDefault().registerSticky(subscriber);

            }
        });


    }

    public void unregister(final java.lang.Object subscriber) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                EventBus.getDefault().unregister(subscriber);

            }
        });

    }


}
