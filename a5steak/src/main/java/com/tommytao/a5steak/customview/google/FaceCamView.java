/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tommytao.a5steak.customview.google;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class FaceCamView extends RelativeLayout {

    private Context context;
    private CameraSource cameraSource;
    private SurfaceView surfaceView;

    private boolean surfaceCreated;
    private boolean started;

    public FaceCamView(Context context) {
        super(context);
        init(context);
    }

    public FaceCamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FaceCamView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        surfaceCreated = false;
        this.setBackgroundColor(Color.BLACK);
        surfaceView = new SurfaceView(context);
        surfaceView.setBackgroundColor(Color.BLACK);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                surfaceCreated = true;
                if (started)
                    startCore();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                surfaceCreated = false;
            }
        });
        addView(surfaceView);
    }

    public boolean isStarted() {
        return started;
    }

    private void startCore() {
        try {

            if (cameraSource != null) {
                cameraSource.release();
                cameraSource = null;
            }

            FaceDetector detector = new FaceDetector.Builder(context)
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .build();
            detector.setProcessor(
                    new MultiProcessor.Builder<>(
                            new MultiProcessor.Factory<Face>() {

                                @Override
                                public Tracker<Face> create(Face face) {
                                    return new Tracker<Face>() {

                                        @Override
                                        public void onDone() {
                                            super.onDone();
                                        }

                                        @Override
                                        public void onNewItem(int id, Face item) {
                                            super.onNewItem(id, item);
                                        }

                                        @Override
                                        public void onUpdate(Detector.Detections<Face> detections, Face item) {
                                            super.onUpdate(detections, item);
                                        }

                                        @Override
                                        public void onMissing(Detector.Detections<Face> detections) {
                                            super.onMissing(detections);
                                        }
                                    };
                                }
                            }
                    ).build());
            cameraSource = new CameraSource.Builder(context, detector)
                    .setRequestedPreviewSize(640, 480)
                    .setFacing(CameraSource.CAMERA_FACING_FRONT)
                    .setRequestedFps(30.0f)
                    .build();
            cameraSource.start(surfaceView.getHolder());
            surfaceView.setBackgroundColor(Color.TRANSPARENT);
            started = true;

            fitSurfaceViewToFaceCamView(getWidth(), getHeight());

        } catch (Exception e) {
            e.printStackTrace();

            started = false;
            surfaceView.setBackgroundColor(Color.BLACK);
            try {
                cameraSource.release();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            cameraSource = null;

        }




    }

    public void start() {

        if (started)
            return;

        if (!surfaceCreated) {
            started = true;
            return;
        }

        startCore();

    }


    public void stop() {

        if (!started)
            return;

        stopCore();

    }

    private void stopCore() {

        started = false;
        surfaceView.setBackgroundColor(Color.BLACK);
        if (cameraSource != null) {
            cameraSource.release();
            cameraSource = null;
        }


    }

    private void fitSurfaceViewToFaceCamView(int viewWidth, int viewHeight) {
        // get resolution size
        if (cameraSource == null) {
            return;
        }

        Size size = cameraSource.getPreviewSize();

        if (size == null) {
            return;
        }

        int resolutionWidth = size.getHeight();
        int resolutionHeight = size.getWidth();


        // fitting by calculating margin & size
        double widthScale = (double) viewWidth / resolutionWidth;
        double heightScale = (double) viewHeight / resolutionHeight;

        int surfaceWidth = -1;
        int surfaceHeight = -1;

        int leftMargin = -1;
        int topMargin = -1;
        int rightMargin = -1;
        int bottomMargin = -1;

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

        assignViewMarginUnderRelativeLayout(surfaceView, leftMargin, topMargin, rightMargin, bottomMargin);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        fitSurfaceViewToFaceCamView(width, height);
    }

    private void assignViewMarginUnderRelativeLayout(View view, int left, int top, int right, int bottom) {
        RelativeLayout.LayoutParams lp = (LayoutParams) view.getLayoutParams();
        lp.setMargins(left, top, right, bottom);
        view.setLayoutParams(lp);
    }

}
