package com.apps.orenc.detectandrecognize;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by orenc on 6/5/15.
 *
 * Codec for bitmap.
 */
public class BitmapCodec {

    private static final String TAG = "BitmapCodec";

    public static final int COMPRESSION_QUALITY = 100;

    public static final int REQUIRED_BITMAP_SIZE = 100;

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // Decode bitmap from bytes array.
    public static Bitmap decode(byte[] buffer) {

        Bitmap res = null;

        try {

            ByteArrayInputStream bis = new ByteArrayInputStream(buffer);

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            res = BitmapFactory.decodeStream(bis, null, options);

            bis.close();

        }
        catch(IOException e) {
            e.printStackTrace();
        }

        return res;
//        return Bitmap.createScaledBitmap(res, REQUIRED_BITMAP_SIZE, REQUIRED_BITMAP_SIZE, true);
    }


    // Encode JPEG bitmap to bytes array.
    public static byte[] encode(Bitmap bitmap) {
        byte[] res = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, os);
            res = os.toByteArray();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    // Encode JPEG drawable resource to bytes array.
    public static byte[] encode(Resources resources, int id) {
        return encode(BitmapFactory.decodeResource(resources, id));
    }

}
