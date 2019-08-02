package com.example.authenteqfacedetection.view.cropFace;

import androidx.lifecycle.MutableLiveData;
import android.graphics.Bitmap;

import com.example.authenteqfacedetection.view.base.BaseViewModel;

public class CropFaceViewModel extends BaseViewModel<CropFaceNavigator> {

    public CropFaceViewModel() {
        super();
    }

    // Create a LiveData with a String
    private MutableLiveData<Bitmap> mCurrentFaceImage;

    public MutableLiveData<Bitmap> getCurrentFaceImage() {
        if (mCurrentFaceImage == null) {
            mCurrentFaceImage = new MutableLiveData<Bitmap>();
        }
        return mCurrentFaceImage;
    }


}
