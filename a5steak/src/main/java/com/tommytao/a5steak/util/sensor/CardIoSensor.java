package com.tommytao.a5steak.util.sensor;

import android.content.Context;

import com.tommytao.a5steak.util.Foundation;


/**
 * Responsible for getting gravity field reading
 *
 */
public class CardIoSensor extends Foundation {

    private static CardIoSensor instance;

    public static CardIoSensor getInstance() {

        if (instance == null)
            instance = new CardIoSensor();

        return instance;
    }

    private CardIoSensor() {

    }

    // --

    @Override
    public boolean init(Context context) {
        return super.init(context);
    }

}
