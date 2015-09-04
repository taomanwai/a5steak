package com.tommytao.a5steak.util.google;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.tommytao.a5steak.util.Foundation;

import java.util.ArrayList;

/**
 * Responsible to face detection
 * <p/>
 * Ref:
 * http://arjunu.com/2015/08/face-detection-with-android-vision-api/
 */
public class BarcodeManager extends Foundation {

    private static BarcodeManager instance;

    public static BarcodeManager getInstance() {

        if (instance == null)
            instance = new BarcodeManager();

        return instance;
    }

    private BarcodeManager() {
        // do nothing
    }

    // --


    @Override
    public boolean init(Context context) {
        return super.init(context);
    }

    private BarcodeDetector barcodeDetector;

    private BarcodeDetector getBarcodeDetector() {

        if (barcodeDetector == null) {
            barcodeDetector = new BarcodeDetector.Builder(appContext)
                    .setBarcodeFormats(Barcode.UPC_A | Barcode.UPC_E | Barcode.EAN_8 | Barcode.EAN_13 | Barcode.QR_CODE)
                    .build();
        }

        return barcodeDetector;

    }


    public boolean isOperational() {
        return getBarcodeDetector().isOperational();
    }

    public ArrayList<Barcode> findBarcodesFromBitmap(Bitmap bitmap){

        ArrayList<Barcode> result = new ArrayList<>();


        if (!getBarcodeDetector().isOperational())
            return result;

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Barcode> barcodes = getBarcodeDetector().detect(frame);

        for (int i=0; i<barcodes.size();i++){
            result.add(barcodes.valueAt(i));
        }

        return result;

    }


}
