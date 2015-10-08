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
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.ArrayList;

public class BarcodeCamView extends RelativeLayout {

    public static interface OnBarcodeChangedListener {

        public void onCreate(int id, Barcode barcode);

        public void onUpdate(int id, Barcode barcode);

        public void onDelete(int id, Barcode barcode);

    }

    public static interface OnVideoSizeChangeRequestedListener {

        public void onVideoSizeChangeRequested(int width, int height);

    }

    private Context context;
    private CameraSource cameraSource;
    private SurfaceView surfaceView;

    private boolean surfaceCreated;
    private boolean started;

    private int DEFAULT_FACING = CameraSource.CAMERA_FACING_BACK;

    private int SUGGESTED_PREVIEW_WIDTH = 480;
    private int SUGGESTED_PREVIEW_HEIGHT = 640;

    private float DEFAULT_PREVIEW_FPS = 30.0f;


    private ArrayList<Pair<Integer, Barcode>> idBarcodesOnScreen = new ArrayList<>();

    private OnBarcodeChangedListener onBarcodeChangedListener;

    private OnVideoSizeChangeRequestedListener onVideoSizeChangeRequestedListener;

    public BarcodeCamView(Context context) {
        super(context);
        init(context);
    }

    public BarcodeCamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BarcodeCamView(Context context, AttributeSet attrs, int defStyleAttr) {
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

    public void setOnBarcodeChangedListener(OnBarcodeChangedListener listener) {
        this.onBarcodeChangedListener = listener;
    }

    public void setOnVideoSizeChangeRequestedListener(OnVideoSizeChangeRequestedListener listener) {
        this.onVideoSizeChangeRequestedListener = listener;
    }

    public ArrayList<Pair<Integer, Barcode>> getIdBarcodesOnScreen() {
        return idBarcodesOnScreen;
    }

    // size assign
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        fitSurfaceViewToBarcodeCamView(width, height);
    }

    private void fitSurfaceViewToBarcodeCamView(final int viewWidth, final int viewHeight) {
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

        final int leftMarginFinal = leftMargin;
        final int rightMarginFinal = rightMargin;
        final int topMarginFinal = rightMargin;
        final int bottomMarginFinal = bottomMargin;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                requestLayout();

                int width = viewWidth - leftMarginFinal - rightMarginFinal;
                int height = viewHeight - topMarginFinal - bottomMarginFinal;

                if (onVideoSizeChangeRequestedListener != null)
                    onVideoSizeChangeRequestedListener.onVideoSizeChangeRequested(width, height);

            }
        });
    }

    private void assignViewMarginUnderRelativeLayout(View view, int left, int top, int right, int bottom) {
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        lp.setMargins(left, top, right, bottom);
        view.setLayoutParams(lp);
    }


    // Start or stop
    private void startCore() {
        try {

            if (cameraSource != null) {
                cameraSource.release();
                cameraSource = null;
            }

            BarcodeDetector detector = new BarcodeDetector.Builder(context)
                    .setBarcodeFormats(Barcode.UPC_A | Barcode.UPC_E | Barcode.EAN_8 | Barcode.EAN_13 | Barcode.QR_CODE)
                    .build();

            detector.setProcessor(
                    new MultiProcessor.Builder<>(
                            new MultiProcessor.Factory<Barcode>() {

                                @Override
                                public Tracker<Barcode> create(Barcode barcode) {
                                    return new Tracker<Barcode>() {

                                        private int id = 0;
                                        private Barcode barcode;

                                        private void removeIdBarcodeOnScreen(int id) {

                                            for (int i = 0; i < idBarcodesOnScreen.size(); i++) {
                                                if (id == idBarcodesOnScreen.get(i).first) {

                                                    idBarcodesOnScreen.remove(i);

                                                    break;
                                                }
                                            }

                                        }

                                        @Override
                                        public void onNewItem(int id, Barcode barcode) {

                                            this.id = id;
                                            this.barcode = barcode;

                                            idBarcodesOnScreen.add(new Pair<>(this.id, this.barcode));

                                            if (onBarcodeChangedListener != null)
                                                onBarcodeChangedListener.onCreate(this.id, this.barcode);

                                        }

                                        @Override
                                        public void onUpdate(Detector.Detections<Barcode> detections, Barcode barcode) {

                                            this.barcode = barcode;

                                            int indexOfSameId = -1;
                                            Pair<Integer, Barcode> idBarcode = null;
                                            for (int i = 0; i < idBarcodesOnScreen.size(); i++) {
                                                idBarcode = idBarcodesOnScreen.get(i);
                                                if (this.id == idBarcode.first) {
                                                    indexOfSameId = i;
                                                    break;
                                                }
                                            }

                                            Pair<Integer, Barcode> updatedIdBarcode = new Pair<>(this.id, this.barcode);
                                            if (indexOfSameId == -1) {
                                                idBarcodesOnScreen.add(updatedIdBarcode);
                                                if (onBarcodeChangedListener != null)
                                                    onBarcodeChangedListener.onCreate(this.id, this.barcode);
                                            } else {
                                                idBarcodesOnScreen.set(indexOfSameId, updatedIdBarcode);
                                                if (onBarcodeChangedListener != null)
                                                    onBarcodeChangedListener.onUpdate(this.id, this.barcode);
                                            }


                                        }

                                        @Override
                                        public void onMissing(Detector.Detections<Barcode> detections) {

                                            removeIdBarcodeOnScreen(this.id);

                                            if (onBarcodeChangedListener != null)
                                                onBarcodeChangedListener.onDelete(this.id, this.barcode);
                                        }

                                        @Override
                                        public void onDone() {

                                            removeIdBarcodeOnScreen(this.id);

                                            if (onBarcodeChangedListener != null)
                                                onBarcodeChangedListener.onDelete(this.id, this.barcode);

                                        }


                                    };
                                }
                            }
                    ).build());

            int proposedWidth = getMeasuredWidth();
            int proposedHeight = getMeasuredHeight();

            if (proposedWidth <= 0 || proposedHeight <= 0) {
                proposedWidth = SUGGESTED_PREVIEW_WIDTH;
                proposedHeight = SUGGESTED_PREVIEW_HEIGHT;
            }

            if (proposedWidth < proposedHeight) {
                proposedHeight = SUGGESTED_PREVIEW_WIDTH * proposedHeight / proposedWidth;
                proposedWidth = SUGGESTED_PREVIEW_WIDTH;
            } else {
                proposedHeight = SUGGESTED_PREVIEW_HEIGHT * proposedHeight / proposedWidth;
                proposedWidth = SUGGESTED_PREVIEW_HEIGHT;
            }

            cameraSource = new CameraSource.Builder(context, detector)
                    .setRequestedPreviewSize(proposedHeight, proposedWidth)
                    .setFacing(DEFAULT_FACING)
                    .setRequestedFps(DEFAULT_PREVIEW_FPS)
                    .build();
            cameraSource.start(surfaceView.getHolder());
            surfaceView.setBackgroundColor(Color.TRANSPARENT);
            started = true;

            fitSurfaceViewToBarcodeCamView(getWidth(), getHeight());

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

    public boolean isStarted() {
        return started;
    }


    /**
     * Return margin of video inside BarcodeCamView, margin region is represented as black region.
     *
     * @return Array of margins (i.e. margin[0] is margin left, margin[1] is margin top, margin[2] is margin right, margin[3] is margin bottom)
     */
    public int[] getVideoMargin() {

        int[] result = new int[4];

        LayoutParams lp = ((LayoutParams) surfaceView.getLayoutParams());

        result[0] = lp.leftMargin;
        result[1] = lp.topMargin;
        result[2] = lp.rightMargin;
        result[3] = lp.bottomMargin;

        return result;
    }


}