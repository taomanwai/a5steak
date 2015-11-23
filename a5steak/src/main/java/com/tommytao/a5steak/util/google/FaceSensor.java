package com.tommytao.a5steak.util.google;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;

import com.android.volley.RequestQueue;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.tommytao.a5steak.util.Foundation;

import java.util.ArrayList;

/**
 * Responsible to face detection
 *
 * Ref:
 * http://arjunu.com/2015/08/face-detection-with-android-vision-api/
 *
 */
public class FaceSensor extends Foundation {

    private static FaceSensor instance;

    public static FaceSensor getInstance() {

        if (instance == null)
            instance = new FaceSensor();

        return instance;
    }

    private FaceSensor() {
        // do nothing
    }

    // --


    @Override
    public boolean init(Context context) {
        return super.init(context);
    }

    public ArrayList<android.media.FaceDetector.Face> findFacesFromBitmapInOldStyle(Bitmap bitmap, int maxNumOfFaces){

        ArrayList<android.media.FaceDetector.Face> result = new ArrayList<>();

        if (bitmap==null || bitmap.isRecycled() || maxNumOfFaces<=0)
            return result;

        if (bitmap.getConfig() != Bitmap.Config.RGB_565){
            // FaceDetector only accepts RGB_565 image, Ref: http://stackoverflow.com/questions/19782215/android-facedetector-not-finding-any-faces-findface-returns-0-every-time
            bitmap = convertBitmapConfig(bitmap, Bitmap.Config.RGB_565);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        android.media.FaceDetector detector = new android.media.FaceDetector(width,height, maxNumOfFaces);
        android.media.FaceDetector.Face[] faces = new android.media.FaceDetector.Face[maxNumOfFaces];


        int numOfFacesFound = detector.findFaces(bitmap, faces);


        for (int i=0; i<numOfFacesFound; i++){
            result.add(faces[i]);
        }

        return result;


    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }

    public boolean isOperational(){
        FaceDetector detector = new FaceDetector.Builder(appContext)
                .setTrackingEnabled(false)
                .build();

        boolean result = detector.isOperational();

        detector.release();

        return result;

    }

    public ArrayList<Face> findFacesFromBitmap(Bitmap bitmap, boolean detectLandmarks, boolean classify){

        ArrayList<Face> result = new ArrayList<>();

        FaceDetector detector = new FaceDetector.Builder(appContext)
                .setTrackingEnabled(false)
                .setLandmarkType(detectLandmarks ? FaceDetector.ALL_LANDMARKS : FaceDetector.NO_LANDMARKS)
                .setClassificationType(classify ? FaceDetector.ALL_CLASSIFICATIONS : FaceDetector.NO_CLASSIFICATIONS)
                .build();

        if (!detector.isOperational())
            return result;

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);

        detector.release();

        for (int i=0; i<faces.size();i++){
            result.add(faces.valueAt(i));
        }

        return result;

    }

}
