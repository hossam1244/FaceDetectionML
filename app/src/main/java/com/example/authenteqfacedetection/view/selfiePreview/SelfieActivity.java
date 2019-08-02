package com.example.authenteqfacedetection.view.selfiePreview;

import android.annotation.SuppressLint;
import android.app.Dialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import com.example.authenteqfacedetection.BR;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.example.authenteqfacedetection.R;
import com.example.authenteqfacedetection.databinding.ActivitySelfieBinding;
import com.example.authenteqfacedetection.utils.FaceCenterCircleView.FaceCenterCrop;
import com.example.authenteqfacedetection.utils.FaceCenterCircleView.FaceCenterCrop.FaceCenterCropListener;
import com.example.authenteqfacedetection.utils.faceDetectionUtil.FaceDetectionProcessor;
import com.example.authenteqfacedetection.utils.faceDetectionUtil.FaceDetectionResultListener;
import com.example.authenteqfacedetection.utils.faceDetectionUtil.common.CameraSource;
import com.example.authenteqfacedetection.utils.faceDetectionUtil.common.FrameMetadata;
import com.example.authenteqfacedetection.utils.faceDetectionUtil.common.GraphicOverlay;
import com.example.authenteqfacedetection.utils.Imageutils;
import com.example.authenteqfacedetection.view.base.BaseActivity;
import com.example.authenteqfacedetection.view.cropFace.CropFaceActivity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.example.authenteqfacedetection.utils.FaceDetectionScanner.Constants.KEY_CAMERA_PERMISSION_GRANTED;
import static com.example.authenteqfacedetection.utils.FaceDetectionScanner.Constants.PERMISSION_REQUEST_CAMERA;

public class SelfieActivity extends BaseActivity<ActivitySelfieBinding,SelfieActivityViewModel> implements SelfieActivityNavigator {

    String TAG = "SelfieActivity";

    private ActivitySelfieBinding activitySelfieBinding;
    SelfieActivityViewModel selfieActivityViewModel;

    private CameraSource mCameraSource = null;

    FaceDetectionProcessor faceDetectionProcessor;
    FaceDetectionResultListener faceDetectionResultListener = null;

    Bitmap bmpCapturedImage;
    List<FirebaseVisionFace> capturedFaces;

    FaceCenterCrop faceCenterCrop;
    FaceCenterCropListener faceCenterCropListener;


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_selfie;
    }

    @Override
    public SelfieActivityViewModel getViewModel() {
        selfieActivityViewModel = ViewModelProviders.of(this).get(SelfieActivityViewModel.class);
        return selfieActivityViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       windowSettings();
        super.onCreate(savedInstanceState);
        setup();

    }


    private void setup() {
        activitySelfieBinding = getViewDataBinding();
        selfieActivityViewModel.setNavigator(this);
        faceCenterCrop = new FaceCenterCrop(this, 100, 100, 1);
        if (activitySelfieBinding.preview != null)
            if (activitySelfieBinding.preview.isPermissionGranted(true, mMessageSender))
                new Thread(mMessageSender).start();
    }

    private void createCameraSource() {
        // To initialise the detector
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .enableTracking()
                        .build();

        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
        // To connect the camera resource with the detector
        mCameraSource = new CameraSource(this, activitySelfieBinding.barcodeOverlay);
        mCameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);

        faceDetectionProcessor = new FaceDetectionProcessor(detector);
        faceDetectionProcessor.setFaceDetectionResultListener(getFaceDetectionListener());
         // To set machine learning frame processor
        mCameraSource.setMachineLearningFrameProcessor(faceDetectionProcessor);

        startCameraSource();
    }

    private FaceDetectionResultListener getFaceDetectionListener() {
        if (faceDetectionResultListener == null)
            faceDetectionResultListener = new FaceDetectionResultListener() {
                @Override
                public void onSuccess(@Nullable Bitmap originalCameraImage, @NonNull List<FirebaseVisionFace> faces, @NonNull FrameMetadata frameMetadata, @NonNull GraphicOverlay graphicOverlay) {
                    boolean isEnable;
                    isEnable = faces.size() > 0;

                    for (FirebaseVisionFace face : faces) {

                        // To get the results

                        Log.d(TAG, "Face bounds : " + face.getBoundingBox());

                        // To get this, we have to set the ClassificationMode attribute as ALL_CLASSIFICATIONS

                        Log.d(TAG, "Left eye open probability : " + face.getLeftEyeOpenProbability());
                        Log.d(TAG, "Right eye open probability : " + face.getRightEyeOpenProbability());
                        Log.d(TAG, "Smiling probability : " + face.getSmilingProbability());

                        // To get this, we have to enableTracking

                        Log.d(TAG, "Face ID : " + face.getTrackingId());

                    }

                    runOnUiThread(() -> {
                        Log.d(TAG, "button enable true ");
                        bmpCapturedImage = originalCameraImage;
                        capturedFaces = faces;
                        activitySelfieBinding.btnCapture.setEnabled(isEnable);
                    });
                }

                @Override
                public void onFailure(@NonNull Exception e) {

                }
            };

        return faceDetectionResultListener;
    }

    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());

        Log.d(TAG, "startCameraSource: " + code);

        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, PERMISSION_REQUEST_CAMERA);
            dlg.show();
        }

        if (mCameraSource != null && activitySelfieBinding.preview != null && activitySelfieBinding.barcodeOverlay != null) {
            try {
                Log.d(TAG, "startCameraSource: ");
                activitySelfieBinding.preview.start(mCameraSource, activitySelfieBinding.barcodeOverlay);
            } catch (IOException e) {
                Log.d(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        } else
            Log.d(TAG, "startCameraSource: not started");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);
        activitySelfieBinding.preview.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (activitySelfieBinding.preview != null)
            activitySelfieBinding.preview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (activitySelfieBinding.preview != null)
                createCameraSource();

        }
    };

    private final Runnable mMessageSender = () -> {
        Log.d(TAG, "mMessageSender: ");
        Message msg = mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_CAMERA_PERMISSION_GRANTED, false);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    };


    private FaceCenterCropListener getFaceCropResult() {
        if (faceCenterCropListener == null)
            faceCenterCropListener = new FaceCenterCropListener() {
                @Override
                public void onTransform(Bitmap updatedBitmap) {


                    Log.d(TAG, "onTransform: ");

                    try {
                        File capturedFile = new File(getFilesDir(), "newImage.jpg");

                        Imageutils imageutils = new Imageutils(SelfieActivity.this);
                        imageutils.store_image(capturedFile, updatedBitmap);

                        Intent currentIntent = CropFaceActivity.newIntent(SelfieActivity.this);
                        currentIntent.putExtra("image", capturedFile.getAbsolutePath());
                        startActivity(currentIntent);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure() {
                    showToast("No face found",SelfieActivity.this);
                }
            };

        return faceCenterCropListener;
    }

    private void windowSettings() {
        if (getWindow() != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            Log.e(TAG, "Barcode scanner could not go into fullscreen mode!");
        }
    }

    @Override
    public void onBtnCaptureClicked() {
        if (faceCenterCrop != null)
            faceCenterCrop.transform(bmpCapturedImage, faceCenterCrop.getCenterPoint(capturedFaces), getFaceCropResult());

    }
}
