package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.tommytao.a5steak.common.Foundation;
import com.tommytao.a5steak.misc.BitmapManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class MainActivity extends Activity {



    private WebView webView;

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

        webView = (WebView) findViewById(R.id.webView);

        String url = "https://www.egltours.com/nfit/ism/productPdf/HKOPRENT-A-CAR-B-021mix.pdf";


//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int progress) {
//            }
//
//
//        });

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//	            return false;

                if ( url.endsWith(".pdf") || url.contains("fetchPdf.action")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "application/pdf");
                    try{
                        view.getContext().startActivity(intent);
                    } catch (Exception e) {
                        //user does not have a pdf viewer installed
                    }
                } else {
                    webView.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {


            }

        });

        webView.loadUrl(url);







    }


}
