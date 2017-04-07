package com.tommytao.a5steak.customview;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.tommytao.a5steak.common.Foundation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

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

            if (pageCount < 0){
                return 0;
            }

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


            if (convertView == null) {
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

                    if (taggedPosition != i) {
                        return;
                    }

                    if (bitmap == null){
                        ((ImageView) convertViewFinal).getLayoutParams().height = getMeasuredHeight();
                        ((ImageView) convertViewFinal).setImageBitmap(null);
                        return;
                    }

                    int width = getMeasuredWidth();
                    int height = bitmap.getHeight() * width / bitmap.getWidth();

                    ((ImageView) convertViewFinal).getLayoutParams().height = height;

                    int bitmapSize = bitmap.getWidth() * bitmap.getHeight();
                    int displaySize = width * height;

                    if (bitmapSize > displaySize){
                        bitmap = foundation.chop(bitmap, width, height, false);
                    }

                    ((ImageView) convertViewFinal).setImageBitmap(bitmap);

                }
            });


            return convertView;
        }
    }

    //    private File pdfFile;
    private String pdfLink = "";
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

    private Foundation getFoundation() {

        if (foundation == null) {
            foundation = new Foundation();
            foundation.init(getContext());
        }

        return foundation;

    }

//    public File getPdfFile() {
//
//        return pdfFile;
//
//    }

    private File getPdfFile(){
        ParcelFileDescriptor fd = null;

        ContentResolver contentResolver = getContext().getContentResolver();

        File f = null;
        Uri uri = null;

        f = new File(getContext().getCacheDir()+"/m1.map");
        if (!f.exists()) try {

            InputStream is = getContext().getAssets().open("deep.pdf");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();


            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) { throw new RuntimeException(e); }

        return f;
    }



    public void setPdfLink(final String link) {

        if (!pdfLink.equals(link)) {

            pdfLink = link;

            final String cacheFilename = String.valueOf(getFoundation().genUniqueId()) + ".pdf";

            getFoundation().httpGetFile(link, 1, getContext().getCacheDir().getAbsolutePath(), cacheFilename, new Foundation.OnHttpGetFileListener() {
                @Override
                public void onDownloaded(final File file) {

                    if (pdfLink != link){
                        return;
                    }


                    getFoundation().loadPdfPageCount(file, new Foundation.OnLoadPdfPageCountListener() {
                        @Override
                        public void onComplete(int pageCount) {

                            if (pdfLink != link){
                                return;
                            }

                            setAdapter(new PdfAdapter(getContext(), getFoundation(), file, pageCount));

                        }
                    });


                }

                @Override
                public void onDownloading(int percentage) {

                }
            });


        }


    }

//    public void setPdfFile(final File file) {
//
//        if (pdfFile != file){
//
//            pdfFile = file;
//
//            getFoundation().loadPdfPageCount(file, new Foundation.OnLoadPdfPageCountListener() {
//                @Override
//                public void onComplete(int pageCount) {
//
//                    if (pdfFile != file){
//                        return;
//                    }
//
//                    setAdapter(new PdfAdapter(getContext(), getFoundation(), file, pageCount));
//
//                }
//            });
//
//        }
//
//    }
}
