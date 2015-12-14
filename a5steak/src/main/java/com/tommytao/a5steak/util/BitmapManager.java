package com.tommytao.a5steak.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PictureDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Responsible for Bitmap reading and manipulation
 *
 * @author tommytao
 */

public class BitmapManager extends Foundation {

    private static BitmapManager instance;

    public static BitmapManager getInstance() {

        if (instance == null)
            instance = new BitmapManager();

        return instance;
    }

    private BitmapManager() {

        super();

        log("bitmap_manager: " + "create");

    }

    // --

    public static interface OnObtainSizeListener {

        public void onComplete(Point size);


    }

    public final static String VERSION_NO = "001";

    @Override
    public boolean init(Context context) {

        if (!super.init(context)) {

            log("bitmap_manager: " + "init REJECTED: already initialized");

            return false;

        }

        log("bitmap_manager: " + "init");

        return true;

    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }

    public Point obtainSizeFromBitmap(Bitmap bitmap) {

        if (bitmap == null)
            return null;

        Point result = new Point();
        result.x = bitmap.getWidth();
        result.y = bitmap.getHeight();

        return result;

    }

    public Point obtainSizeFromByteArray(byte[] data) {

        if (data == null || data.length == 0) {
            return null;
        }

        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, option);

        Point result = new Point();
        result.x = option.outWidth;
        result.y = option.outHeight;

        return result;

    }

    public Point obtainSizeFromResId(int resId) {
        if (resId == 0) { // wrong input, return null
            return null;
        }

        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(appContext.getResources(),
                resId, option);

        Point result = new Point();
        result.x = option.outWidth;
        result.y = option.outHeight;

        return result;

    }

    public Point obtainSizeFromFileLink(String fileLink) {
        if (fileLink == null || fileLink.length() == 0) { //  wrong input, return
            // null
            return null;
        }

        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileLink, option);

        Point result = new Point();
        result.x = option.outWidth;
        result.y = option.outHeight;

        return result;

    }


    public void obtainSizeFromLink(String link, final OnObtainSizeListener listener) {

        if (listener == null) {

            return;
        }

        httpGetByteArray(link, 1, new OnHttpGetByteArrayListener() {

            public void onDownloaded(byte[] data) {

                if (data == null || data.length == 0) {

                    listener.onComplete(new Point(0, 0));

                    return;
                }

                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(data, 0, data.length, option);

                Point result = new Point();
                result.x = option.outWidth;
                result.y = option.outHeight;

                listener.onComplete(result);


            }

            public void onDownloading(int percentage) {
                // do nothing

            }

        });

    }

    private String getBitmapCacheParent() {
        return appContext.getFilesDir()
                + File.separator + "bitmapQuickLoadCacheDir" + File.separator;
    }

    private String getBitmapCacheHolder() {
        return getBitmapCacheParent() + VERSION_NO + File.separator;
    }


    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    private Bitmap rotate(Bitmap bitmap, float degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();

            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2) {
                    bitmap.recycle();
                    bitmap = b2;
                }
            } catch (OutOfMemoryError ex) {
                throw ex;
            }
        }
        return bitmap;
    }

    private float checkRotationForImage(Context context, Uri uri) {
        if (uri.getScheme().equals("content")) {
            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
            Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        } else if (uri.getScheme().equals("file")) {
            try {
                ExifInterface exif = new ExifInterface(uri.getPath());
                int rotation = (int) exifOrientationToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
                return rotation;
            } catch (IOException e) {
            }
        }
        return 0f;
    }

    private float exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    // ====

    /**
     * Get sample size to load part of picture, save memory!
     *
     * @param bitmapWidth  Width of bitmap
     * @param bitmapHeight Height of bitmap
     * @param targetWidth  Target width of bitmap
     * @param targetHeight Target height of bitmap
     * @return Sample size
     */
    private int calculateSampleSize(int bitmapWidth, int bitmapHeight,
                                    int targetWidth, int targetHeight) {
        int result = 1;

        // Now we have image width and height. We should find the correct scale
        // value. (power of 2)

        // Suppose both are in the same orientation
        while (true) {
            if (bitmapWidth / 2 < targetWidth
                    || bitmapHeight / 2 < targetHeight)
                break;
            bitmapWidth /= 2;
            bitmapHeight /= 2;
            result++;
        }

        return result;

    }



    /**
     * Convert to mutable bitmap (if clear old bitmap function is enabled,
     * the old bitmap should not be referenced by variable outside this function,
     * including the caller function)
     *
     * @param bitmap       Bitmap being chopped
     * @param targetWidth  Target width of bitmap
     * @param targetHeight Target height of bitmap
     * @param topHorizontalCenterMode True: chop and remain top and horizontal center; False: chop out non-center region
     * @return Chopped bitmap
     */
    private Bitmap chop(Bitmap bitmap, int targetWidth, int targetHeight, boolean topHorizontalCenterMode) {

        if (bitmap == null || bitmap.isRecycled() || targetWidth <= 0 || targetHeight <= 0)
            return null;


        Matrix m = new Matrix();

        // Scale
        double widthScale = 1;
        double heightScale = 1;
        double realScale = 1;

        widthScale = (double) targetWidth / bitmap.getWidth();
        heightScale = (double) targetHeight / bitmap.getHeight();

        realScale = Math.max(widthScale, heightScale);

        int scaledBMPWidth = (int) (bitmap.getWidth() * realScale);
        int scaledBMPHeight = (int) (bitmap.getHeight() * realScale);

        m.postScale((float) realScale, (float) realScale);

        // chop location (left, top), width and height
        int b4choppedBMPWidth = (int) (targetWidth / realScale);
        int b4choppedBMPHeight = (int) (targetHeight / realScale);
        int b4choppedBMPLeft = 0;
        int b4choppedBMPTop = 0;

        if (widthScale > heightScale) {
            // chop vertical
            b4choppedBMPLeft = 0;
            b4choppedBMPTop = topHorizontalCenterMode ? 0 : ((int) (((scaledBMPHeight - (float) targetHeight
                    / targetWidth * scaledBMPWidth)) / 2 / realScale));
        } else {
            // chop horizontal
            b4choppedBMPLeft = (int) (((scaledBMPWidth - (float) targetWidth
                    / targetHeight * scaledBMPHeight)) / 2 / realScale);

            b4choppedBMPTop = 0;

        }

        Bitmap result = Bitmap
                .createBitmap(bitmap, b4choppedBMPLeft, b4choppedBMPTop,
                        b4choppedBMPWidth, b4choppedBMPHeight, m, true);


        if (result.getWidth() != targetWidth
                || result.getHeight() != targetHeight) {
            //  do nothing
        }

        return result;

    }

    public Bitmap loadPicture(Picture picture) {

        PictureDrawable pd = new PictureDrawable(picture);

        Bitmap bitmap = Bitmap.createBitmap(pd.getIntrinsicWidth(),
                pd.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawPicture(pd.getPicture());


        if (bitmap != null)
            bitmap.setHasAlpha(true);
        return bitmap;

    }

    public boolean loadFileLinkToExistingBitmap(String fileLink, Bitmap bitmap){

        return loadByteArrayToExistingBitmap(fileLink2ByteArray(fileLink), bitmap);


    }

    public Bitmap loadFileLink(String fileLink, int targetWidth, int targetHeight, boolean topAndHorizontalCenterMode) {

        return loadByteArray(fileLink2ByteArray(fileLink), targetWidth, targetHeight, topAndHorizontalCenterMode);

    }

    public void save(Bitmap bitmap, String folderName, String fileName) {

        if (bitmap == null)
            return;

        FileOutputStream fos = null;
        File folder = new File(folderName);

        if (!folder.exists())
            folder.mkdirs();


        try {
            fos = new FileOutputStream(new File(folderName + File.separator + fileName));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void saveSamplePic(String foldername, String fileName){

        Bitmap bitmap = loadResId(R.drawable.maneuver_ferry, -1, -1, false, false);

        save(bitmap, foldername, fileName);

    }

    public boolean loadByteArrayToExistingBitmap(byte[] data, Bitmap bitmap){
        if (data == null || data.length == 0)
            return false;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inBitmap = bitmap;

        boolean hasException = false;
        try {
            BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e){
            e.printStackTrace();
            hasException = true;
        }

        return !hasException;


    }

    public Bitmap loadByteArray(byte[] data, int targetWidth, int targetHeight, boolean topAndHorizontalCenterMode) {

        if (data == null || data.length == 0)
            return null;

        Bitmap bitmap = null;

        int dataLength = data.length;
        byte[] newData = new byte[dataLength];
        System.arraycopy(data, 0, newData, 0, dataLength);

        BitmapFactory.Options options = new BitmapFactory.Options();
        // inPurgeable = true because newData will NOT change;
        // inInputShareable = false because newData is a local variable, it will
        // NOT be used by other bitmaps
        options.inPurgeable = true;
        options.inInputShareable = false;

        if (targetWidth < 0 && targetHeight < 0) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bitmap != null)
                bitmap.setHasAlpha(true);
            return bitmap;
        }

        //  Only decode image size. Not whole image
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        options.inJustDecodeBounds = false;

        //  Adjust specificWidth and specificHeight to eliminate any <0
        // values (<0: NOT specified length)
        if (targetWidth < 0)
            targetWidth = targetHeight * outWidth / outHeight;
        else {
            if (targetHeight < 0)
                targetHeight = targetWidth * outHeight / outWidth;
        }

        // Decode again with inSampleSize
        options.inSampleSize = this.calculateSampleSize(outWidth, outHeight,
                targetWidth, targetHeight);

        bitmap = chop(
                BitmapFactory.decodeByteArray(data, 0, data.length, options),
                targetWidth, targetHeight, topAndHorizontalCenterMode);

        bitmap.setHasAlpha(true);

        return bitmap;

    }

    public Bitmap loadImageView(ImageView imgView) {

        if (imgView == null)
            return null;

        BitmapDrawable bitmapDrawable = null;

        try {
            bitmapDrawable = (BitmapDrawable) imgView.getDrawable();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bitmapDrawable == null)
            return null;

        Bitmap bitmap = bitmapDrawable.getBitmap();

        if (bitmap != null)
            bitmap.setHasAlpha(true);

        return bitmap;

    }

    private void clearCacheParent() {
        File cacheParent = new File(getBitmapCacheParent());

        deleteFolder(cacheParent);

    }

    private void createCacheDirIfNeeded() {
        File file = new File(getBitmapCacheHolder());
        if (!file.exists()) {
            this.clearCacheParent(); //  clear old theme-outdated
            // BitmapQuickLoadManager
            // cache
            file.mkdirs();
        }

    }


    public Bitmap loadResId(int resId, int specificWidth, int specificHeight, boolean topHorizontalCenterMode,
                            boolean cacheEnabled) {

        Bitmap bitmap = null;

        if (resId == 0) {
            // XXX wrong input, return null
            return null;

        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        // Because both resId picture and cache resId picture will NOT
        // change and they must be single instance, so both purgeable and
        // inputshareable are enabled
        options.inPurgeable = true;
        options.inInputShareable = true;

        if (specificWidth < 0 && specificHeight < 0) {

            bitmap = BitmapFactory.decodeResource(
                    appContext.getResources(), resId, options);


            if (bitmap != null)
                bitmap.setHasAlpha(true);
            return bitmap;
        }

        Configuration config = appContext.getResources()
                .getConfiguration();

        String lang = config.locale.getLanguage();

        String country = config.locale.getCountry();

        String idName = resIdToName(resId, false);

        if (idName.equals("")) {
            // XXX idName seems invalid
            // return null immediately

            return null;
        }

        String cacheFile = idName + "-" + String.valueOf(specificWidth) + "-"
                + String.valueOf(specificHeight) + "-" + lang + "-" + country
                + ".PNG";

        File file = new File(getBitmapCacheHolder() + cacheFile);

        if (cacheEnabled || !file.exists())
            synchronized (this) {
                if (cacheEnabled || !file.exists()) {
                    //  WRITE
                    //  Process the image and then save it to cache and then
                    // return
                    this.createCacheDirIfNeeded();

                    // Only decode image size. Not whole image
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeResource(
                            appContext.getResources(), resId,
                            options);

                    int outWidth = options.outWidth;
                    int outHeight = options.outHeight;

                    options.inJustDecodeBounds = false;

                    //  Adjust specificWidth and specificHeight to eliminate
                    // any <0 values (<0: NOT specified length)
                    if (specificWidth < 0)
                        specificWidth = specificHeight * outWidth / outHeight;
                    else {
                        if (specificHeight < 0)
                            specificHeight = specificWidth * outHeight
                                    / outWidth;
                    }

                    // Decode again with inSampleSize
                    options.inSampleSize = this.calculateSampleSize(outWidth,
                            outHeight, specificWidth, specificHeight);

                    InputStream is = null;
                    try {
                        is = appContext.getResources()
                                .openRawResource(resId);
                        bitmap = chop(
                                BitmapFactory.decodeStream(is, null, options),
                                specificWidth, specificHeight, topHorizontalCenterMode);
                    } catch (Resources.NotFoundException e) {

                    } catch (OutOfMemoryError oomE) {

                    } finally {
                        if (is != null) {
                            try {
                                is.close();
                                is = null;
                            } catch (IOException e) {

                            }
                        }
                    }

                    FileOutputStream fos = null;

                    try {
                        fos = new FileOutputStream(new File(
                                getBitmapCacheHolder() + cacheFile));
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

                    } catch (FileNotFoundException e) {

                    } finally {

                        try {
                            if (fos != null)
                                fos.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        bitmap.setHasAlpha(true);
                        return bitmap;
                    }
                }
            }

        //  READ
        //  load cache file and return

        bitmap = BitmapFactory.decodeFile(getBitmapCacheHolder()
                + cacheFile, options);

        bitmap.setHasAlpha(true);
        return bitmap;

    }

    public boolean loadResIdToExistingBitmap(int resId, Bitmap bitmap) {

        if (resId == 0) {
            return false;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inBitmap = bitmap;

        boolean hasException = false;
        try {
            BitmapFactory.decodeResource(
                    appContext.getResources(), resId, options);
        } catch (Exception e){
            e.printStackTrace();
            hasException = true;
        }

        return !hasException;

    }

    public Bitmap convertBitmapConfig(Bitmap bitmap, Bitmap.Config config) {
        return super.convertBitmapConfig(bitmap, config);
    }






}