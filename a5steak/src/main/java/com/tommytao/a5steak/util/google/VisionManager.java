package com.tommytao.a5steak.util.google;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.FaceDetector;

import com.tommytao.a5steak.util.Foundation;

import java.util.ArrayList;

/**
 * Responsible to vision operations, e.g. recognize face, smile, barcode, etc.
 */
public class VisionManager extends Foundation {

    private static VisionManager instance;

    public static VisionManager getInstance() {

        if (instance == null)
            instance = new VisionManager();

        return instance;
    }

    private VisionManager() {
        // do nothing
    }

    // --


    @Deprecated
    public boolean init(Context context) {
        return super.init(context);
    }

    public ArrayList<FaceDetector.Face> findFacesFromBitmap(Bitmap bitmap, int maxNumOfFaces){

        ArrayList<FaceDetector.Face> result = new ArrayList<>();

        if (bitmap==null || bitmap.isRecycled() || maxNumOfFaces<=0)
            return result;

        if (bitmap.getConfig() != Bitmap.Config.RGB_565){
            // FaceDetector only accepts RGB_565 image, Ref: http://stackoverflow.com/questions/19782215/android-facedetector-not-finding-any-faces-findface-returns-0-every-time
            bitmap = convertBitmapConfig(bitmap, Bitmap.Config.RGB_565);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        FaceDetector detector = new FaceDetector(width,height, maxNumOfFaces);
        FaceDetector.Face[] faces = new FaceDetector.Face[maxNumOfFaces];


        int numOfFacesFound = detector.findFaces(bitmap, faces);


        for (int i=0; i<numOfFacesFound; i++){
            result.add(faces[i]);
        }

        return result;






    }

}
