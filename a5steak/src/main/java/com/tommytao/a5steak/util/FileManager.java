package com.tommytao.a5steak.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.common.util.Foundation;

import java.io.File;
import java.io.InputStream;

public class FileManager extends Foundation {

    private static FileManager instance;

    public static FileManager getInstance() {

        if (instance == null)
            instance = new FileManager();

        return instance;
    }

    private FileManager() {

    }


    // --

    @Override
    public boolean init(Context context) {
        return super.init(context);
    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }

    @Override
    public boolean deleteFolder(File folder) {
        return super.deleteFolder(folder);
    }

    @Override
    public boolean deleteFolderExcept(File folder, File exceptFileOrFolder) {
        return super.deleteFolderExcept(folder, exceptFileOrFolder);
    }

    public boolean deleteFile(File file){
        return file.delete();
    }

    // TODO MVP should not use in_s.available()
    public String openRawText(int resId){

//        String result = "";
//        try {
//            InputStream in_s = appContext.getResources().openRawResource(resId);
//            byte[] b = new byte[in_s.available()];
//            in_s.read(b);
//            result = new String(b);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return result;

        return new String(openRawByteArray(resId));

    }

    public byte[] openRawByteArray(int resId){
        byte[] result = new byte[0];
        try {
            InputStream in_s = appContext.getResources().openRawResource(resId);
            result = new byte[in_s.available()];
            in_s.read(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }


}
