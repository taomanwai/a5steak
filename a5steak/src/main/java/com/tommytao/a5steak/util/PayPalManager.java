package com.tommytao.a5steak.util;

public class PayPalManager extends Foundation {

    private static PayPalManager instance;

    public static PayPalManager getInstance() {

        if (instance == null)
            instance = new PayPalManager();

        return instance;
    }

    private PayPalManager() {

    }


    // --

}


