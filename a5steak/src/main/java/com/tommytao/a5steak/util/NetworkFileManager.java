package com.tommytao.a5steak.util;

import android.content.Context;

import java.io.File;
import java.util.Map;

/**
 * Responsible for network file operation (e.g. download / upload files)
 *
 * @author tommytao
 */
public class NetworkFileManager extends Foundation {

    private static NetworkFileManager instance;

    public static NetworkFileManager getInstance() {

        if (instance == null)
            instance = new NetworkFileManager();

        return instance;
    }

    private NetworkFileManager() {

        super();

        log("network_file_manager: " + "create");

    }

    // --

//    @Override
//    public boolean init(Context appContext) {
//
//        if (!super.init(appContext)) {
//
//            log("network_file_manager: " + "init REJECTED: already initialized");
//
//            return false;
//
//        }
//
//        log("network_file_manager: " + "init");
//
//        return true;
//
//    }

    @Deprecated
    public boolean init(Context appContext) {
        return super.init(appContext);

    }

    public static interface DownloadFileListener {

        public void onDownloaded(File file);

        public void onDownloading(int percentage);

    }

    public static interface UploadFileListener {

        public void onUploaded(boolean succeed);


    }

    public void download(final String link, final String directory, final String fileName, final DownloadFileListener listener) {

        this.httpGetFile(link, directory, fileName, new Foundation.OnHttpGetFileListener() {

            @Override
            public void onDownloading(int percentage) {

                if (listener != null)
                    listener.onDownloading(percentage);

            }

            @Override
            public void onDownloaded(File file) {
                if (listener != null)
                    listener.onDownloaded(file);

            }
        });

    }


    public void upload(final String link, final Map<String, Object> params, final byte[] imgData, final Map<String, Object> imgDataParams, final UploadFileListener listener) {

        httpPostByteArray(link, params, imgData, imgDataParams, new Foundation.OnHttpPostByteArrayListener(){

            @Override
            public void onComplete(boolean succeed) {

                if (listener!=null)
                    listener.onUploaded(succeed);

            }

        });


    }


}


