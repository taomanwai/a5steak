package com.tommytao.a5steak.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Responsible for zip operations (e.g. zip, unzip, etc.)
 * <p/>
 * <p/>
 * Ref:
 *
 * @author tommytao
 */
public class ZipUtils {


    public static byte[] zipByteArray(byte[] data) {

        // Compressor with highest level of compression
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);

        // Give the compressor the data to compress
        compressor.setInput(data);
        compressor.finish();

        // Create an expandable byte array to hold the compressed data.
        // It is not necessary that the compressed data will be smaller than
        // the uncompressed data.
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // Compress the data
        byte[] buf = new byte[1024];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the compressed data
        return bos.toByteArray();

    }

    public static byte[] zipString(String dataStr) {
        return zipByteArray(dataStr.getBytes());
    }

    public static byte[] unzipByteArray(byte[] data) {

        // Create the decompressor and give it the data to compress
        Inflater decompressor = new Inflater();
        decompressor.setInput(data);

        // Create an expandable byte array to hold the decompressed data
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // Decompress the data
        byte[] buf = new byte[1024];
        while (!decompressor.finished()) {
            try {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            } catch (DataFormatException e) {
                e.printStackTrace();
                break;
            }
        }
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the decompressed data
        return bos.toByteArray();

    }


}
