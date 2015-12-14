package com.tommytao.a5steak.util.ai;

import com.tommytao.a5steak.util.Foundation;

/**
 * Responsible for Standford NLP (Under construction)
 *
 * Ref: https://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/apis/#!/visual-recognition/recognizeLabelsService
 *
 */
public class IbmWatsonNlpManager extends Foundation {

    private static IbmWatsonNlpManager instance;

    public static IbmWatsonNlpManager getInstance() {

        if (instance == null)
            instance = new IbmWatsonNlpManager();

        return instance;
    }

    private IbmWatsonNlpManager() {

    }


    // --






}
