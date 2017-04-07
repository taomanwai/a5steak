package com.tommytao.a5steak.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.tommytao.a5steak.common.Foundation;

import java.io.File;

/**
 * Created by tommytao on 7/4/2017.
 */

public class PdfView extends ListView {

    public class PdfAdapter extends BaseAdapter {

        private Context ctx;

        private File pdfFile;
        private int pageCount = -1;


        public PdfAdapter(Context ctx, File pdfFile, int pageCount) {
            this.ctx = ctx;
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
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

                    setAdapter(new PdfAdapter(getContext(), file, pageCount));

                }
            });

        }

    }
}
