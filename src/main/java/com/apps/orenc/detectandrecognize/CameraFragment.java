package com.apps.orenc.detectandrecognize;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 *
 * This is the fragment that holds everything related to the camera.
 */
public class CameraFragment extends Fragment implements AddPersonAlertDialog.OnAddPersonAlertDialogClickListener {

    private static final String TAG = "CameraFragment";

    public static final String TITLE = "Camera";

    // We need the phone orientation to correctly draw the overlay.
    private int mOrientation;
    private int mOrientationCompensation;
    private OrientationEventListener mOrientationEventListener;

    // The surface vuew for the camera data
    private CameraPreview mCameraPreview;

    // Draw rectangles and other stuff on the detected faces.
    private FaceOverlayView mFaceOverlayView;


    private AddPersonAlertDialog mAddPersonAlertDialog;

    private List<Person> mPersons;
    private PeopleListViewAdapter mAdapter;

    private TakeFacePictureCallback mTakeFacePictureCallback;


    public CameraFragment() {

    }

    public void setArguments(List<Person> persons, PeopleListViewAdapter adapter) {
        // Required empty public constructor
        mPersons = persons;
        mAdapter = adapter;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setRetainInstance(true);

        mAddPersonAlertDialog = new AddPersonAlertDialog();
        mAddPersonAlertDialog.setOnAddPersonAlertDialogClickListener(this);

        View root = inflater.inflate(R.layout.fragment_camera, container, false);

        // Get the FrameLayout.
        FrameLayout cameraPreviewLayout = (FrameLayout) root.findViewById(R.id.camera_preview_layout);

        // Initialize the camera preivew and its faces overlay and add them to the preview layout.
        mTakeFacePictureCallback = new TakeFacePictureCallback();
        mFaceOverlayView = new FaceOverlayView(getActivity(), mTakeFacePictureCallback);
        mCameraPreview = new CameraPreview(getActivity(), mFaceOverlayView);
        cameraPreviewLayout.addView(mCameraPreview, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        cameraPreviewLayout.addView(mFaceOverlayView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        // Create and register the OrientationListener
        mOrientationEventListener = new SimpleOrientationEventListener(getActivity());
        mOrientationEventListener.enable();



        return root;
    }

    @Override
    public void onPause() {
        mOrientationEventListener.disable();
        super.onPause();
    }

    @Override
    public void onResume() {
        mOrientationEventListener.enable();
        super.onResume();
    }

    // We need to react on OrientationEvents to rotate the screen and
    // update the views.
    private class SimpleOrientationEventListener extends OrientationEventListener {

        public SimpleOrientationEventListener(Context context) {
            super(context, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            // We keep the last known orientation.
            // So it the user first orient te camera then point the camera to floor or
            // sky, we still have the correct orientation.
            if(orientation == ORIENTATION_UNKNOWN) {
                return;
            }
            mOrientation = OrientationUtils.roundOrientation(orientation, mOrientation);

            // When the screen is unlocked, display rotation may change. Always
            // calculate the up-to-date orientationCompensation.
            int rotation = OrientationUtils.getDisplayRotation(getActivity());
            int orientationCompensation = mOrientation + rotation;
            if(mOrientationCompensation != orientationCompensation) {
                mOrientationCompensation = orientationCompensation;
                mFaceOverlayView.setOrientation(mOrientationCompensation);
            }


        }
    }

    @Override
    public void onAddPersonAlertDialogPositiveClick(Person newPerson) {
        mPersons.add(newPerson);
        // Update the database.
        SingletonPeopleSQLiteHelper helper = SingletonPeopleSQLiteHelper.getInstance(getActivity().getApplicationContext());
        helper.insertPerson(newPerson, true);

        mAdapter.notifyDataSetChanged();
    }


    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    class TakeFacePictureCallback implements Camera.PictureCallback {

        private Rect mFaceBounds = null;

        private Matrix mFaceTransformMatrix;

        public void setFaceBounds(RectF faceBounds) {
            mFaceBounds = new Rect(
                    (int)faceBounds.left, (int)faceBounds.top, (int)faceBounds.right, (int)faceBounds.bottom);
        }



        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            if (data == null) { // No data received from shutter.

                Toast.makeText(getActivity(), "Application failed to take picture", Toast.LENGTH_SHORT).show();

            }

            else if(EnvironmentVariables.iAmEmulator) { // Emulator : take full picture.

                mAddPersonAlertDialog.setBitmap(rotate(BitmapCodec.decode(data), 90));
                mAddPersonAlertDialog.show(CameraFragment.this.getChildFragmentManager(), TAG);

            }

            else { // Device : Take the detected face region.

                if (mFaceBounds != null) {
                    // Cut the face region from the picture and give it to the "Add Person" dialog.

                    // 1. get the bytes array as bitmap.
                    int currentDeviceRotation = OrientationUtils.getDisplayRotation(getActivity());
                    int imageRotation;
                    if(currentDeviceRotation == 90) {
                        imageRotation = 0;
                    }
                    else if(currentDeviceRotation == 0) {
                        imageRotation = 90;
                    }
                    else if(currentDeviceRotation == 270) {
                        imageRotation = 180;
                    }
                    else {
                        imageRotation = 180;
                    }

                    Bitmap fullPicture = rotate(BitmapCodec.decode(data),imageRotation);
                    // 2. create portion of the bitmap decoded above.

                    mFaceTransformMatrix = new Matrix();
                    OrientationUtils.prepareMatrix(mFaceTransformMatrix, mFaceOverlayView.getWidth(), mFaceOverlayView.getHeight(), fullPicture.getWidth(), fullPicture.getHeight());
                    OrientationUtils.transformRect(mFaceBounds, mFaceBounds, mFaceTransformMatrix);

                    Bitmap facePicture =
                            Bitmap.createBitmap(fullPicture,
                                    mFaceBounds.left, mFaceBounds.top,
                                    mFaceBounds.right - mFaceBounds.left, mFaceBounds.bottom - mFaceBounds.top);
                    // 3. Set the "Add Person" dialog picture with the partial bitmap and show the dialog.
                    mAddPersonAlertDialog.setBitmap(facePicture);
                    mAddPersonAlertDialog.show(CameraFragment.this.getChildFragmentManager(), TAG);
                }

                mFaceBounds = null;
            }
        }
    }
}
