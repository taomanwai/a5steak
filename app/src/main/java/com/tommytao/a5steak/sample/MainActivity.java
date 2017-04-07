package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.ImageView;

import com.tommytao.a5steak.common.Foundation;
import com.tommytao.a5steak.misc.BitmapManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class MainActivity extends Activity {



    private ImageView iv_Test;

    private File getPdfFile(){
        ParcelFileDescriptor fd = null;

        ContentResolver contentResolver = getContentResolver();

        File f = null;
        Uri uri = null;

        f = new File(getCacheDir()+"/m1.map");
        if (!f.exists()) try {

            InputStream is = getAssets().open("deep.pdf");
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_Test = (ImageView) findViewById(R.id.iv_Test);

        BitmapManager.getInstance().init(this);

        if (true){

            BitmapManager.getInstance().loadPdf(getPdfFile(), 0, new Foundation.OnLoadPdfListener() {
                @Override
                public void onComplete(Bitmap bitmap) {
                    iv_Test.setImageBitmap(bitmap);
                }
            });

            return;
        }

        BitmapManager.getInstance().loadPdfPageCount(getPdfFile(), new Foundation.OnLoadPdfPageCountListener() {
            @Override
            public void onComplete(int pageCount) {

                Log.d("fdsa", "pageCountT " + pageCount);

            }
        });





    }


}
