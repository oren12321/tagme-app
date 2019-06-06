package com.apps.orenc.detectandrecognize;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by orenc on 6/11/15.
 *
 * This class handle the overlay off the detected faces and the touch events over detected
 * faces.
 */
public class FaceOverlayView extends View implements View.OnTouchListener {

    // Painter for the detected face rectangle.
    private Paint mPaint;

    // The display orientation and the device orientation.
    private int mDisplayOrientation;
    private int mOrientation;

    // The current detected faces.
    private Camera.Face[] mFaces;

    private boolean mIsBackCamera = true;

    // Rectangle and matrix used for drawing.
    RectF mRectF;
    Matrix mMatrix;
    PointF mLastTouchedPoint;

    private Context mContext;

    private CameraFragment.TakeFacePictureCallback mTakeFacePictureCallback;
    private Camera mCamera;

    private boolean mAutoFocusOn = false;

    private boolean mAutoFocusHasBeenSet = false;

    public FaceOverlayView(Context context, CameraFragment.TakeFacePictureCallback takeFacePictureCallback) {

        super(context);

        initializePaint();

        mContext = context;

        mRectF = new RectF();
        mMatrix = new Matrix();

        mLastTouchedPoint = new PointF(Float.MAX_VALUE, Float.MAX_VALUE);

        mTakeFacePictureCallback = takeFacePictureCallback;



        setOnTouchListener(this);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    private void initializePaint() {
        // We want to draw green box around the face.
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setAlpha(128);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
//        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void setFaces(Camera.Face[] faces) {
        // When setting new faces the other threads occupy with this
        // array needs to wait until the update will complete.
        synchronized (this) {
            mFaces = faces;
            invalidate();
        }
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    public void setDisplayOrientation(int displayOrientation) {
        mDisplayOrientation = displayOrientation;
        invalidate();
    }

    // Initialize auto focus event handler:
    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if(success) {
                mAutoFocusOn = true;
            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!mAutoFocusHasBeenSet) {
            mCamera.autoFocus(mAutoFocusCallback);
            mAutoFocusHasBeenSet = true;
        }

        // Dont let someone interrupting when drawing faces.
        synchronized (this) {
            // Check if any faces detected.
            if (mFaces != null && mFaces.length > 0) {

                // Build transformation matrix from the camera driver face rectangle to the
                // overlay face rectangle.
                mMatrix.reset();
                OrientationUtils.prepareMatrix(mMatrix, !mIsBackCamera, mDisplayOrientation, getWidth(), getHeight());
                canvas.save();
                mMatrix.postRotate(mOrientation);
                canvas.rotate(-mOrientation);

                // Indication if its the first time face detected.
                boolean discoveredOnce = false;

                // Loop over all the detected faces.
                for (Camera.Face face : mFaces) {

                    // Take the face rectangle of the driver and transform it
                    // to the overlay view coordinates system.
                    mRectF.set(face.rect);
                    mMatrix.mapRect(mRectF);

                    // Draw the overlay rectangle surrounding the detected face.
                    canvas.drawRect(mRectF, mPaint);

                    if(!discoveredOnce) {

                        // Check if the user selected one of the faces.
                        if (isPointInsideTheRectangle(mLastTouchedPoint.x, mLastTouchedPoint.y, mRectF)) {

                            // If the camera available with focus on, take picture.
                            if(mCamera != null) {
                                if(mAutoFocusOn) {
                                    mTakeFacePictureCallback.setFaceBounds(mRectF);
                                    mCamera.takePicture(null, null, mTakeFacePictureCallback /*JPEG*/);
                                }
                            }

                            discoveredOnce = true;
                        }
                    }
                }

                // Reset canvas and touched point to the next cycle of drawing.
                canvas.restore();
                mLastTouchedPoint.set(Float.MAX_VALUE, Float.MAX_VALUE);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;

            case MotionEvent.ACTION_UP:

                synchronized (this) {

                    // Just for emulator (kind of a tester application of the basic operations).
                    if(EnvironmentVariables.iAmEmulator) {
                        mCamera.takePicture(null, null, mTakeFacePictureCallback /*JPEG*/);
                    }
                    else {
                        // Save the last touched point for the drawing function.
                        mLastTouchedPoint.set(event.getX(), event.getY());
                    }

                }

                return true;
        }

        return false;
    }


    // Check if given point is inside given rectangle.
    public static boolean isPointInsideTheRectangle(float x, float y, RectF r) {
        return x >= r.left && x <= r.right && y >= r.top && y <= r.bottom;
    }
}
