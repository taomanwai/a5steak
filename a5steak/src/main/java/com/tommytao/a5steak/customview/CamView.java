package com.tommytao.a5steak.customview;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import java.util.List;

public class CamView extends RelativeLayout {

    public static int CAMERA_NONE = -1;

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

        public int getBlankAreaInPxSquare() {
            return viewWidth * (topMargin + bottomMargin) + viewHeight * (leftMargin + rightMargin);
        }

        public int getValidVisibleAreaInPxSquare() {
            int surfaceArea = surfaceWidth * surfaceHeight;
            int resolutionArea = resolutionWidth * resolutionHeight;

            return (resolutionArea < surfaceArea) ? resolutionArea : surfaceArea;

        }

        public int getInvalidVisibleAreaInPxSquare() {

            int surfaceArea = surfaceWidth * surfaceHeight;
            int resolutionArea = resolutionWidth * resolutionHeight;

            return (resolutionArea < surfaceArea) ? (surfaceArea - resolutionArea) : 0;

        }

        public int getUselessAreaInPxSquare() {
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


    public CamView(Context context) {
        super(context);
        init(context);
    }

    public CamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CamView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        this.setBackgroundColor(Color.BLACK);
        surfaceView = new SurfaceView(context);
        surfaceView.setBackgroundColor(Color.BLACK);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                surfaceCreated = true;
                startCore(targetCamera);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                surfaceCreated = false;
                stopCore();
            }
        });
        addView(surfaceView);

    }

    public void snapshot(final OnSnapshotListener listener) {

        if (listener == null)
            return;

        if (camera == null) {

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
                // try catch for safety
                try {
                    camera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listener.onCompleted(data);
            }
        });

    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        fitSurfaceViewToCamView(width, height);
    }

    private boolean isCameraResolutionSupported(int resolutionWidth, int resolutionHeight) {

        if (camera == null)
            return false;

        List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();

        boolean result = false;
        for (Camera.Size size : sizes) {
            if (size.width == resolutionHeight && size.height == resolutionWidth) {
                result = true;
                break;
            }
        }
        return result;
    }

    private FittedSurfaceViewInfo getFittedSurfaceViewInfo(int viewWidth, int viewHeight) {

        FittedSurfaceViewInfo result = null;

        if (camera == null) {
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
        for (Camera.Size size : camera.getParameters().getSupportedPreviewSizes()) {

            resolutionWidth = size.height;
            resolutionHeight = size.width;

            widthScale = (double) viewWidth / resolutionWidth;
            heightScale = (double) viewHeight / resolutionHeight;

            if (widthScale > heightScale) {
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

            if (result == null) {
                result = fittedSurfaceViewInfo;
            } else {

                if (fittedSurfaceViewInfo.getUselessAreaInPxSquare() < result.getUselessAreaInPxSquare()) {
                    result = fittedSurfaceViewInfo;
                }

            }


        }

        return result; // 144, 176 or 480, 640

    }

    private void fitSurfaceViewToCamView(int viewWidth, int viewHeight) {

        final FittedSurfaceViewInfo fittedSurfaceViewInfo = getFittedSurfaceViewInfo(viewWidth, viewHeight);

        if (fittedSurfaceViewInfo == null)
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

        if (!isCameraResolutionSupported(resolutionWidth, resolutionHeight)) {
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

    private boolean surfaceCreated;

    private int targetCamera = CAMERA_NONE;
    private int coreCamera = CAMERA_NONE;


    // Start
    private void startCore(int targetCamera) {

        if (targetCamera == CAMERA_NONE) {
            stopCore();
            return;
        }

        if (targetCamera == coreCamera) {
            return;
        }

        stopCore();

        try {
            coreCamera = targetCamera;
            camera = Camera.open(targetCamera);
            camera.setDisplayOrientation(90);
            FittedSurfaceViewInfo fittedSurfaceViewInfo = getFittedSurfaceViewInfo(getWidth(), getHeight());
            Camera.Parameters params = camera.getParameters();
            params.setPreviewSize(fittedSurfaceViewInfo.getResolutionHeight(), fittedSurfaceViewInfo.getResolutionWidth());
            camera.setParameters(params);
            camera.setPreviewDisplay(surfaceView.getHolder());
            camera.startPreview();
            surfaceView.setBackgroundColor(Color.TRANSPARENT);


        } catch (Exception e) {
            e.printStackTrace();
            coreCamera = CAMERA_NONE;
            surfaceView.setBackgroundColor(Color.BLACK);
            try {
                camera.release();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            camera = null;
        }

    }

    private void stopCore() {

        if (coreCamera == CAMERA_NONE)
            return;

        coreCamera = CAMERA_NONE;
        surfaceView.setBackgroundColor(Color.BLACK);
        camera.release();
        camera = null;

    }

    public void start(int targetCamera) {

        if (targetCamera == CAMERA_NONE) {
            stop();
            return;
        }

        if (targetCamera == this.targetCamera) {
            return;
        }

        stop();

        this.targetCamera = targetCamera;
        if (surfaceCreated)
            startCore(targetCamera);


    }

    public void stop() {

        if (targetCamera == CAMERA_NONE)
            return;

        this.targetCamera = CAMERA_NONE;
        stopCore();
    }

    public boolean isStarted() {
        return coreCamera != CAMERA_NONE;
    }

    public static int getNumOfCameras() {
        return Camera.getNumberOfCameras();
    }

    private static int getCameraIdBasedOnType(int type){
        int cameraId = -1;
        int numberOfCameras = getNumOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == type) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public static int getFrontCameraId() {
        return getCameraIdBasedOnType(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    public static int getBackCameraId() {
        return getCameraIdBasedOnType(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

}
