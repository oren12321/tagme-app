package com.apps.orenc.detectandrecognize;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

/**
 * Created by orenc on 6/11/15.
 *
 * This class holds and manage the camera preview.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraPreview";

    private Context mContext;
    private Camera mCamera;

    // Draw rectangles and other stuff on the detected faces.
    private FaceOverlayView mFaceOverlayView;

    public CameraPreview(Context context, FaceOverlayView faceOverlayView) {

        super(context);

        mContext = context;
        mFaceOverlayView = faceOverlayView;

        // Add the holder callback.
        getHolder().addCallback(this);


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        // Open the back facing camera, set the holder to the camera display and give reference of
        // the camera to the overlay view.
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            mCamera.setPreviewDisplay(holder);

            mFaceOverlayView.setCamera(mCamera);

            // Set the face detection listener.
            mCamera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
                @Override
                public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                    // Sets the faces for the overlay view, so it can be updated and
                    // the face overlays will be drawn again.
                    mFaceOverlayView.setFaces(faces);
                }
            });

            mCamera.startFaceDetection();

        } catch (Exception e) {
            Log.e(TAG, "Could not preview the image.", e);
        }

    }



    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        // We have no surface, return immediately.
        if(holder.getSurface() == null) {
            return;
        }

        // Try to stop the current preview:
        try {
            mCamera.stopPreview();
        }
        catch(Exception e) {
            // Ignore.
        }

        // Get reference to the camera current parameters.
        Camera.Parameters parameters = mCamera.getParameters();

        // Get the supported preview sizes and set them.
        List<Camera.Size> preivewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = preivewSizes.get(0);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        // Add auto focus to the camera (if supported).
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        // Set the camera parameters.
        mCamera.setParameters(parameters);

        // Now set the display orientation for the camera.
        int displayRotation = OrientationUtils.getDisplayRotation((Activity) mContext);
        int displayOrientation = OrientationUtils.getDisplayOrientation(displayRotation, 0);
        mCamera.setDisplayOrientation(displayOrientation);

        // Set the display orientation also for the overlay.
        if(mFaceOverlayView != null) {
            mFaceOverlayView.setDisplayOrientation(displayOrientation);
        }

        // Restart face detection.
        mCamera.startFaceDetection();

        // Finally start the camera preview again.
        mCamera.startPreview();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Stop the preview, unregister its callbacks and release the camera.
        if(mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.setFaceDetectionListener(null);
            mCamera.setErrorCallback(null);
            mCamera.release();
            mCamera = null;
//            mFaceOverlayView.setCamera(null);
        }
    }

//    public Camera getCamera() {
//        return mCamera;
//    }

}
