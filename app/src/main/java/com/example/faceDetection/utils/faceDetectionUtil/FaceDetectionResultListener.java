package com.example.faceDetection.utils.faceDetectionUtil;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.example.faceDetection.utils.faceDetectionUtil.common.FrameMetadata;
import com.example.faceDetection.utils.faceDetectionUtil.common.GraphicOverlay;

import java.util.List;


public interface FaceDetectionResultListener {
    void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<FirebaseVisionFace> faces,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay);

    void onFailure(@NonNull Exception e);
}
