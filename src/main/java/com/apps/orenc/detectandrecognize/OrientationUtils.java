package com.apps.orenc.detectandrecognize;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.view.OrientationEventListener;
import android.view.Surface;

/**
 * Created by orenc on 6/11/15.
 *
 * This class contains utility functions for orientation and transformation.
 */
public class OrientationUtils {

    // Orientation hysteresis amount in rounding, in degrees.
    private static final int ORIENTATION_HYSTERESIS = 5;

    private static final int ROTATION_0_VALUE = 0;
    private static final int ROTATION_90_VALUE = 90;
    private static final int ROTATION_180_VALUE = 180;
    private static final int ROTATION_270_VALUE = 270;

    // In case that the change between the previous orientation to the
    // current one is above this value - update orientation.
    private static final int MIN_ORIENTATION_CHANGE = 45;

    // Get the device display rotation.
    public static int getDisplayRotation(Activity activity) {
        try {
            // Get the rotation enumeration.
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            // Get the real rotation value from it.
            switch (rotation) {
                case Surface.ROTATION_0:
                    return ROTATION_0_VALUE;
                case Surface.ROTATION_90:
                    return ROTATION_90_VALUE;
                case Surface.ROTATION_180:
                    return ROTATION_180_VALUE;
                case Surface.ROTATION_270:
                    return ROTATION_270_VALUE;
            }
            return 0;
        }
        catch(Exception e) {
            // For now we ignore from this exception. It occurs sometimes when the user makes fast and bit
            // rotations to the device.
            return 0;
        }
    }

    public static int getDisplayOrientation(int degrees, int cameraId) {
        // See android.hardware.Camera.setDisplayOrientation for documentation.
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int result;
        if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror.
        }
        else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    // Prepare transformation matrix from camera driver aspect ratio to desired aspect ratio.
    public static void prepareMatrix(Matrix matrix, boolean mirror, int displayOrientation, int viewWidth, int viewHeight) {
        // Need mirror for front camera.
        matrix.setScale(mirror ? -1 : 1, 1);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(displayOrientation);
        // Camera driver coordinates range from (-1000,-1000) to (1000,1000).
        // UI coordinates range from (0,0) to (width,height).
        matrix.postScale(viewWidth / 2000f, viewHeight / 2000f);
        matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
    }

    // Prepare transformation matrix to desired aspect ratio.
    public static void prepareMatrix(Matrix matrix, int srcMaxW, int srcMaxH, int dstW, int dstH) {
        matrix.postScale(dstW / (float) srcMaxW, dstH / (float) srcMaxH);
    }

    public static int roundOrientation(int orientation, int orientationHistory) {
        boolean changeOrientation = false;
        // If there is no orientation history, we need to take the newest.
        if(orientationHistory == OrientationEventListener.ORIENTATION_UNKNOWN) {
            changeOrientation = true;
        }
        // There is orientation history.
        else {
            // Take orientation change.
            int distance = Math.abs(orientation - orientationHistory);
            // Take the shortest way to perform the orientation.
            distance = Math.min(distance, 360 - distance);

            changeOrientation = (distance >= MIN_ORIENTATION_CHANGE + ORIENTATION_HYSTERESIS);
        }

        if(changeOrientation) {
            // Return the current orientation in radians and drop 360 degrees factors.
            return ((orientation + MIN_ORIENTATION_CHANGE) / 90 * 90) % 360;
        }

        return orientationHistory;
    }

    // Transform the given source rectangle according to the given transformation matrix
    // and put the result in destination rectangle.
    public static void transformRect(Rect src, Rect dst, Matrix mat) {
        RectF srcF = new RectF();
        srcF.set(src);
        RectF dstF = new RectF();
        mat.mapRect(dstF, srcF);
        dst.set((int)dstF.left, (int)dstF.top, (int)dstF.right, (int)dstF.bottom);
    }
}
