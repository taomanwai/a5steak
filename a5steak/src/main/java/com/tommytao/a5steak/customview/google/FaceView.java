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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.ArrayList;

/**
 * View which displays a bitmap containing a face along with overlay graphics that identify the
 * locations of detected facial landmarks.
 */
public class FaceView extends ImageView {

    private ArrayList<Face> faces = new ArrayList<>();

    private boolean drawLandmark = true;
    private boolean drawBoundary = true;

    public FaceView(Context context) {
        super(context);
    }

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isDrawLandmark() {
        return drawLandmark;
    }

    public void setDrawLandmark(boolean drawLandmark) {
        this.drawLandmark = drawLandmark;
    }

    public boolean isDrawBoundary() {
        return drawBoundary;
    }

    public void setDrawBoundary(boolean drawBoundary) {
        this.drawBoundary = drawBoundary;
    }

    /**
     * Sets the bitmap background and the associated face detections.
     */
    public void setImageBitmap(Bitmap bitmap, ArrayList<Face> faces) {
        super.setImageBitmap(bitmap);
        this.faces = faces;
        invalidate();
    }

    /**
     * Draws the bitmap background and the associated face landmarks.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawFaceAnnotations(canvas, faces);
    }


    /**
     * Draws a small circle for each detected landmark, centered at the detected landmark position.
     * <p/>
     * <p/>
     * Note that eye landmarks are defined to be the midpoint between the detected eye corner
     * positions, which tends to place the eye landmarks at the lower eyelid rather than at the
     * pupil position.
     */
    private void drawFaceAnnotations(Canvas canvas, ArrayList<Face> faces) {


        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        Point landmarkPoint = new Point();
        Point cornerPoint = new Point();
        int left = -1;
        int top = -1;
        int right = -1;
        int bottom = -1;
        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.get(i);

            if (drawBoundary) {
                left = (int) face.getPosition().x;
                top = (int) face.getPosition().y;
                right = (int) (left + face.getWidth());
                bottom = (int) (top + face.getHeight());
                cornerPoint = bitmapCoordToImageViewCoord(left, top);
                left = cornerPoint.x + getPaddingLeft();
                top = cornerPoint.y + getPaddingTop();
                cornerPoint = bitmapCoordToImageViewCoord(right, bottom);
                right = cornerPoint.x + getPaddingLeft();
                bottom = cornerPoint.y + getPaddingTop();
                canvas.drawRect(left, top, right, bottom, paint);
            }

            if (drawLandmark) {
                for (Landmark landmark : face.getLandmarks()) {
                    int cx = (int) (landmark.getPosition().x);
                    int cy = (int) (landmark.getPosition().y);
                    landmarkPoint = bitmapCoordToImageViewCoord(cx, cy);
                    canvas.drawCircle(landmarkPoint.x + getPaddingLeft(), landmarkPoint.y + getPaddingTop(), 10, paint);
                }
            }


        }
    }

    private Point bitmapCoordToImageViewCoord(int x, int y) {

        Point result = new Point();

        float[] pt = new float[]{x, y};

        Matrix m = getImageMatrix();

        m.mapPoints(pt);

        result.x = (int) pt[0];
        result.y = (int) pt[1];

        return result;

    }


    /**
     * Note: Not in use, but keep it for reference!
     *
     * @return
     */
    private Point getBitmapOffset() {
        Point offset = new Point();
        float[] values = new float[9];

        Matrix m = getImageMatrix();
        m.getValues(values);

        offset.x = (int) values[2];
        offset.y = (int) values[5];


        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) getLayoutParams();
        int paddingTop = (int) (getPaddingTop());
        int paddingLeft = (int) (getPaddingLeft());
        offset.x += paddingLeft; // + lp.leftMargin
        offset.y += paddingTop; // + lp.topMargin


        return offset;
    }

}
