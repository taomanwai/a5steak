package com.tommytao.a5steak.customview;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import java.util.List;

public class CameraView extends RelativeLayout {

    private class FittedSurfaceViewInfo {

        private int viewWidth = -1;
        private int viewHeight = -1;

        private int resolutionWidth = -1;
        private int resolutionHeight = -1;

        private int surfaceWidth = -1;
        private int surfaceHeight = -1;

        private int leftMargin = -1;
        private int topMargin = -1;
        private int rightMargin = -1;
        private int bottomMargin = -1;

        public FittedSurfaceViewInfo(int viewWidth, int viewHeight, int resolutionWidth, int resolutionHeight, int surfaceWidth, int surfaceHeight, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
            this.viewWidth = viewWidth;
            this.viewHeight = viewHeight;
            this.resolutionWidth = resolutionWidth;
            this.resolutionHeight = resolutionHeight;
            this.surfaceWidth = surfaceWidth;
            this.surfaceHeight = surfaceHeight;
            this.leftMargin = leftMargin;
            this.topMargin = topMargin;
            this.rightMargin = rightMargin;
            this.bottomMargin = bottomMargin;
        }

        public int getViewWidth() {
            return viewWidth;
        }

        public int getViewHeight() {
            return viewHeight;
        }

        public int getResolutionWidth() {
            return resolutionWidth;
        }

        public int getResolutionHeight() {
            return resolutionHeight;
        }

        public int getSurfaceWidth() {
            return surfaceWidth;
        }

        public int getSurfaceHeight() {
            return surfaceHeight;
        }

        public int getLeftMargin() {
            return leftMargin;
        }

        public int getTopMargin() {
            return topMargin;
        }

        public int getRightMargin() {
            return rightMargin;
        }

        public int getBottomMargin() {
            return bottomMargin;
        }

        public int getBlankAreaInPxSquare(){
            return viewWidth * (topMargin + bottomMargin) + viewHeight * (leftMargin + rightMargin);
        }

        public int getValidVisibleAreaInPxSquare(){
            int surfaceArea = surfaceWidth * surfaceHeight;
            int resolutionArea = resolutionWidth * resolutionHeight;

            return (resolutionArea < surfaceArea) ? resolutionArea : surfaceArea;

        }

        public int getInvalidVisibleAreaInPxSquare(){

            int surfaceArea = surfaceWidth * surfaceHeight;
            int resolutionArea = resolutionWidth * resolutionHeight;

            return (resolutionArea < surfaceArea) ? (surfaceArea - resolutionArea) : 0;

        }

        public int getUselessAreaInPxSquare(){
            return getBlankAreaInPxSquare() + getInvalidVisibleAreaInPxSquare();
        }

        @Override
        public String toString() {
            return "FittedSurfaceViewInfo{" +
                    "viewWidth=" + viewWidth +
                    ", viewHeight=" + viewHeight +
                    ", resolutionWidth=" + resolutionWidth +
                    ", resolutionHeight=" + resolutionHeight +
                    ", surfaceWidth=" + surfaceWidth +
                    ", surfaceHeight=" + surfaceHeight +
                    ", leftMargin=" + leftMargin +
                    ", topMargin=" + topMargin +
                    ", rightMargin=" + rightMargin +
                    ", bottomMargin=" + bottomMargin +
                    '}';
        }
    }

    public interface OnSnapshotListener {
        public void onCompleted(byte[] data);
    }

    private Context context;
    private Camera camera;
    private SurfaceView surfaceView;


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
        this.setBackgroundColor(Color.BLACK);
        surfaceView = new SurfaceView(context);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
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
        });
        addView(surfaceView);

    }

    public void snapshot(final OnSnapshotListener listener) {

        if (listener == null)
            return;

        if (!isConnected()) {

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

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        fitSurfaceViewToCameraView(width, height);
    }

    private boolean isCameraResolutionSupported(int resolutionWidth, int resolutionHeight) {
        List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();

        boolean result = false;
        for (Camera.Size size : sizes) {
            if (size.width== resolutionHeight && size.height == resolutionWidth) {
                result = true;
                break;
            }
        }
        return result;
    }

    private FittedSurfaceViewInfo getFittedSurfaceViewInfo(int viewWidth, int viewHeight){

        FittedSurfaceViewInfo result = null;

        if (!isConnected()){
            return result;
        }

        FittedSurfaceViewInfo fittedSurfaceViewInfo = null;

        int resolutionWidth = -1;
        int resolutionHeight = -1;

        int surfaceWidth = -1;
        int surfaceHeight = -1;

        int leftMargin = -1;
        int topMargin = -1;
        int rightMargin = -1;
        int bottomMargin = -1;

        double widthScale = Double.NaN;
        double heightScale = Double.NaN;
        for (Camera.Size size : camera.getParameters().getSupportedPreviewSizes()){

            resolutionWidth = size.height;
            resolutionHeight = size.width;

            widthScale = (double) viewWidth / resolutionWidth;
            heightScale = (double) viewHeight / resolutionHeight;

            if (widthScale > heightScale){
                // set horizontal margin
                surfaceWidth = (int) (resolutionWidth * heightScale);
                surfaceHeight = (int) (resolutionHeight * heightScale);
                topMargin = 0;
                bottomMargin = 0;
                leftMargin = (viewWidth - surfaceWidth) / 2;
                rightMargin = leftMargin;
            } else {
                // set vertical margin
                surfaceWidth = (int) (resolutionWidth * widthScale);
                surfaceHeight = (int) (resolutionHeight * widthScale);
                leftMargin = 0;
                rightMargin = 0;
                topMargin = (viewHeight - (int) (resolutionHeight * widthScale)) / 2;
                bottomMargin = topMargin;
            }

            fittedSurfaceViewInfo = new FittedSurfaceViewInfo(viewWidth, viewHeight, resolutionWidth, resolutionHeight, surfaceWidth, surfaceHeight, leftMargin, topMargin, rightMargin, bottomMargin);

            if (result==null){
                result = fittedSurfaceViewInfo;
            } else {

                if (fittedSurfaceViewInfo.getUselessAreaInPxSquare() < result.getUselessAreaInPxSquare()){
                    result = fittedSurfaceViewInfo;
                }

            }


        }

        Log.d("rtemp", "info_t: " + result);

        return result; // 144, 176 or 480, 640
    }

    private void fitSurfaceViewToCameraView(int viewWidth, int viewHeight){

        final FittedSurfaceViewInfo fittedSurfaceViewInfo = getFittedSurfaceViewInfo(viewWidth, viewHeight);

        if (fittedSurfaceViewInfo==null)
            return;


        assignCameraResolution(fittedSurfaceViewInfo.getResolutionWidth(), fittedSurfaceViewInfo.getResolutionHeight());
        assignSurfaceViewMargin(fittedSurfaceViewInfo.getLeftMargin(), fittedSurfaceViewInfo.getTopMargin(), fittedSurfaceViewInfo.getRightMargin(), fittedSurfaceViewInfo.getBottomMargin());
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                requestLayout();

            }
        });

    }

    private void assignSurfaceViewMargin(int left, int top, int right, int bottom) {
        RelativeLayout.LayoutParams lp = (LayoutParams) surfaceView.getLayoutParams();
        lp.setMargins(left, top, right, bottom);
        surfaceView.setLayoutParams(lp);
    }

    private void assignCameraResolution(int resolutionWidth, int resolutionHeight) {

        if (!isConnected())
            return;

        if (!isCameraResolutionSupported(resolutionWidth, resolutionHeight)){
            return;
        }

        camera.stopPreview();

        Camera.Parameters params = camera.getParameters();
        int origWidth = params.getPreviewSize().height;
        int origHeight = params.getPreviewSize().width;
        params.setPreviewSize(resolutionHeight, resolutionWidth);
        try {
            camera.setParameters(params);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            params.setPreviewSize(origHeight, origWidth);
            try {
                camera.setParameters(params);
                camera.startPreview();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
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
            FittedSurfaceViewInfo fittedSurfaceViewInfo = getFittedSurfaceViewInfo(getWidth(), getHeight());
            Camera.Parameters params = camera.getParameters();
            params.setPreviewSize(fittedSurfaceViewInfo.getResolutionHeight(), fittedSurfaceViewInfo.getResolutionWidth());
            camera.setParameters(params);
            camera.setPreviewDisplay(surfaceView.getHolder());
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
