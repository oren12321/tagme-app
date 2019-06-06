package com.apps.orenc.detectandrecognize;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Opening activity of the application: performs initial tests before running the application.
 */
public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";

    private static final int STARTUP_WAITING_TIME_MS = 2000;

    private String mErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        Thread startupWaitingThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    // Artificial loading time...
//                    Thread.sleep(STARTUP_WAITING_TIME_MS);

                    boolean checkPassed = isApplicationSupportedByDevice();

                    // If the check passed, start the main activity (the application UI).
                    if(checkPassed) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), mErrorMessage, Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                }
//                catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                finally {
                    // No matter what, this activity need to be terminated.
                    // Possible cases: check is fail, check is passed or exception.
                    finish();
                }

            }
        });

        // Start loading application...
        startupWaitingThread.start();

    }

    private boolean isApplicationSupportedByDevice() {

        boolean checkResult = true;

        // 1. Check if the device contains camera.
        if(!getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            mErrorMessage = "This device does not contains camera";
            checkResult = false;
        }

        // 2. Check if the device camera supports face detection.
        //    (for now we will take camera instance just for that check).
        if(!EnvironmentVariables.iAmEmulator) {

            Camera camera = null;
            try {

                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                camera.startPreview();

                if (camera.getParameters().getMaxNumDetectedFaces() <= 0) {
                    mErrorMessage = "The device camera does not support face detection";
                    checkResult = false;
                }
            } catch (Exception e) {
                mErrorMessage = "The device having an error with its camera. " +
                        e.getMessage();
                checkResult = false;
            } finally {
                if (camera != null) {
                    camera.stopFaceDetection();
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }

        }

        return checkResult;
    }
}
