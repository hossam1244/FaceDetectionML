// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.faceDetection.utils.faceDetectionUtil;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.example.faceDetection.utils.faceDetectionUtil.common.CameraImageGraphic;
import com.example.faceDetection.utils.faceDetectionUtil.common.FrameMetadata;
import com.example.faceDetection.utils.faceDetectionUtil.common.GraphicOverlay;

import java.io.IOException;
import java.util.List;

/**
 * Face Detector Demo.
 */

public class FaceDetectionProcessor extends VisionProcessorBase<List<FirebaseVisionFace>> {

    private static final String TAG = "FaceDetectionProcessor";

    private final FirebaseVisionFaceDetector detector;

    FaceDetectionResultListener faceDetectionResultListener;



    public FaceDetectionProcessor(FirebaseVisionFaceDetector detector) {
        this.detector=detector;
    }



    public void setFaceDetectionResultListener(FaceDetectionResultListener faceDetectionResultListener) {
        this.faceDetectionResultListener = faceDetectionResultListener;
    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: " + e);
        }
    }

    @Override
    protected Task<List<FirebaseVisionFace>> detectInImage(FirebaseVisionImage image) {
        return detector.detectInImage(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<FirebaseVisionFace> faces,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.add(imageGraphic);
        }

        graphicOverlay.postInvalidate();

        if(faceDetectionResultListener!=null)
            faceDetectionResultListener.onSuccess(originalCameraImage,faces,frameMetadata,graphicOverlay);
    }

    @Override
    protected void onFailure(@NonNull Exception e) {

        if(faceDetectionResultListener!=null)
            faceDetectionResultListener.onFailure(e);

        Log.e(TAG, "Face detection failed " + e);
    }
}
