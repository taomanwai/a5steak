package com.tommytao.a5steak.gvision;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;

import com.android.volley.RequestQueue;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.tommytao.a5steak.common.Foundation;

import java.util.ArrayList;

/**
 * Responsible to face detection
 * <p/>
 * Ref:
 * http://arjunu.com/2015/08/face-detection-with-android-vision-api/
 */
public class BarcodeScanner extends Foundation {

    private static BarcodeScanner instance;

    public static BarcodeScanner getInstance() {

        if (instance == null)
            instance = new BarcodeScanner();

        return instance;
    }

    private BarcodeScanner() {
        // do nothing
    }

    // --


    @Override
    public boolean init(Context context) {
        return super.init(context);
    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }

    private BarcodeDetector detector;

    private BarcodeDetector getDetector() {

        if (detector == null) {
            detector = new BarcodeDetector.Builder(appContext)
                    .setBarcodeFormats(Barcode.UPC_A | Barcode.UPC_E | Barcode.EAN_8 | Barcode.EAN_13 | Barcode.QR_CODE)
                    .build();
        }

        return detector;

    }


    public boolean isOperational() {
        boolean result = getDetector().isOperational();

        getDetector().release();
        detector = null;

        return result;
    }

    public ArrayList<Barcode> findBarcodesFromBitmap(Bitmap bitmap) {

        ArrayList<Barcode> result = new ArrayList<>();

        if (!isOperational())
            return result;

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Barcode> barcodes = getDetector().detect(frame);

        getDetector().release();
        detector = null;

        for (int i = 0; i < barcodes.size(); i++) {
            result.add(barcodes.valueAt(i));
        }

        return result;

    }


}
