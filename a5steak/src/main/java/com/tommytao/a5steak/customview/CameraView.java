package com.tommytao.a5steak.customview;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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

    public boolean isConnected() {
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


        camera.takePicture(null, null, new Camera.PictureCallback(){

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                camera.startPreview();
                listener.onCompleted(data);

            }
        });
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        connect();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        disconnect();
    }




}
