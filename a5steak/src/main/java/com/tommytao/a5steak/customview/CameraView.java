package com.tommytao.a5steak.customview;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {


    public interface OnSnapshotListener {
        public void onCompleted(byte[] data);
    }

    private Context context;
    private Camera camera;


    public CameraView(Context context) {
        super(context);
        init(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        this.context = context;
        getHolder().addCallback(this);
    }


    public void snapshot(final OnSnapshotListener listener){

        if (listener==null)
            return;

        if (!isConnected()){

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listener.onCompleted(new byte[0]);
                }
            });

            return;
        }


        camera.takePicture(null, null, new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                camera.startPreview();
                listener.onCompleted(data);
            }
        });
    }

    private Point getOptimizedSizeForCenterInside(Camera camera, int width, int height){

        double targetRatio = (double) width / height;

        List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();

        double ratio = Double.NaN;
        double lowestRatioDiff = Double.POSITIVE_INFINITY;
        int indexOfLowestRatioDiff = -1;
        int i=0;
        for (Camera.Size size : sizes){
            ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) < lowestRatioDiff){
                lowestRatioDiff = ratio;
                indexOfLowestRatioDiff = i;
            }
            i++;
        }

        if (indexOfLowestRatioDiff==-1){
            return null;
        }

        Camera.Size sizeNearTargetRatio = sizes.get(indexOfLowestRatioDiff);
        return new Point(sizeNearTargetRatio.height, sizeNearTargetRatio.width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        connect();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        int optimizedWidth = width; // 480 or  144
        int optimizedHeight = height; // 640 or 176

        changeResolution(optimizedWidth, optimizedHeight);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        disconnect();
    }

    // Change resolution
    private void changeResolution(int width, int height){

        if (!isConnected())
            return;

        camera.stopPreview();

        Camera.Parameters params = camera.getParameters();
        int origWidth = params.getPreviewSize().height;
        int origHeight = params.getPreviewSize().width;
        params.setPreviewSize(height, width);
        try {
            camera.setParameters(params);
            camera.startPreview();
        } catch (Exception e){
            e.printStackTrace();
            params.setPreviewSize(origHeight, origWidth);
            camera.setParameters(params);
            camera.startPreview();
        }


    }


    // Connect
    private boolean isConnected() {
        return camera != null;
    }

    private void connect() {

        if (isConnected())
            return;

        try {
            camera = Camera.open();
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(getHolder());
            camera.startPreview();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                camera.release();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            camera = null;
        }

    }

    private void disconnect() {

        if (!isConnected())
            return;

        camera.release();
        camera = null;

    }




}
