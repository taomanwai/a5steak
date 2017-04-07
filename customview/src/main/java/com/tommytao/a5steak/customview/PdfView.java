package com.tommytao.a5steak.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.tommytao.a5steak.common.Foundation;

import java.io.File;

/**
 * Created by tommytao on 7/4/2017.
 */

public class PdfView extends ListView {

    private class PdfAdapter extends BaseAdapter {

        private Context ctx;

        private Foundation foundation;
        private File pdfFile;
        private int pageCount = -1;


        public PdfAdapter(Context ctx, Foundation foundation, File pdfFile, int pageCount) {
            this.ctx = ctx;
            this.foundation = foundation;
            this.pdfFile = pdfFile;
            this.pageCount = pageCount;
        }

        @Override
        public int getCount() {
            return pageCount;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {

            // Log.d("fdsa", "getViewIndex " + i + " " + getMeasuredWidth() + " " + getMeasuredHeight());

            if (convertView == null){
                convertView = new ImageView(this.ctx);
                ((ImageView) convertView).setScaleType(ImageView.ScaleType.FIT_XY);
                ((ImageView) convertView).setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, getMeasuredHeight()));
            }

            convertView.setTag(i);

            final View convertViewFinal = convertView;

            foundation.loadPdf(pdfFile, i, new Foundation.OnLoadPdfListener() {
                @Override
                public void onComplete(Bitmap bitmap) {

                    int taggedPosition = (Integer) convertViewFinal.getTag();

                    if (taggedPosition != i){
                        return;
                    }

                    int width = getMeasuredWidth();
                    int height = bitmap.getHeight() * width / bitmap.getWidth();

                    ((ImageView) convertViewFinal).getLayoutParams().height = height;


                    ((ImageView) convertViewFinal).setImageBitmap(bitmap);

                }
            });


            return convertView;
        }
    }

    private File pdfFile;
    private Foundation foundation;

    public PdfView(Context context) {
        super(context);
    }

    public PdfView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PdfView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Foundation getFoundation(){

        if (foundation == null){
            foundation = new Foundation();
            foundation.init(getContext());
        }

        return foundation;

    }

    public File getPdfFile() {

        return pdfFile;

    }

    public void setPdfFile(final File file) {

        if (pdfFile != file){

            pdfFile = file;

            getFoundation().loadPdfPageCount(file, new Foundation.OnLoadPdfPageCountListener() {
                @Override
                public void onComplete(int pageCount) {

                    if (pdfFile != file){
                        return;
                    }

                    setAdapter(new PdfAdapter(getContext(), getFoundation(), file, pageCount));

                }
            });

        }

    }
}
