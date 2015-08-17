package com.tommytao.a5steak.util;


/**
 * Responsible for speak text
 * <p/>
 * Note: Cantonese speaking may not work coz Google blocks robot access sometimes. Still fixing ...
 */
public class TextInCantoneseSpeaker extends Foundation {

    private static TextInCantoneseSpeaker instance;

    public static TextInCantoneseSpeaker getInstance() {

        if (instance == null)
            instance = new TextInCantoneseSpeaker();

        return instance;
    }

    private TextInCantoneseSpeaker() {

    }

    // --




}
