package com.tommytao.a5steak.ai;

import com.tommytao.a5steak.common.Foundation;

/**
 * Responsible for Standford NLP (Under construction)
 */
public class StandfordNlpManager extends Foundation {

    private static StandfordNlpManager instance;

    public static StandfordNlpManager getInstance() {

        if (instance == null)
            instance = new StandfordNlpManager();

        return instance;
    }

    private StandfordNlpManager() {

    }


    // --






}
