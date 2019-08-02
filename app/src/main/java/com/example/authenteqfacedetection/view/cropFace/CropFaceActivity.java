package com.example.authenteqfacedetection.view.cropFace;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.example.authenteqfacedetection.BR;
import com.example.authenteqfacedetection.R;
import com.example.authenteqfacedetection.databinding.ActivityCropFaceBinding;
import com.example.authenteqfacedetection.utils.Imageutils;
import com.example.authenteqfacedetection.view.base.BaseActivity;


public class CropFaceActivity extends BaseActivity<ActivityCropFaceBinding, CropFaceViewModel> implements CropFaceNavigator {

    Imageutils imageutils;
    Imageutils.ImageAttachmentListener imageAttachmentListener;


    private ActivityCropFaceBinding activityCropFaceBinding;
    CropFaceViewModel cropFaceViewModel;



    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_crop_face;
    }

    @Override
    public CropFaceViewModel getViewModel() {
        cropFaceViewModel = ViewModelProviders.of(this).get(CropFaceViewModel.class);
        return cropFaceViewModel;
    }


    public static Intent newIntent(Context context) {
        return new Intent(context, CropFaceActivity.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent() != null)
            imageutils.onّّIntentResult(getIntent());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup();
    }


    private void setup() {
        activityCropFaceBinding = getViewDataBinding();
        cropFaceViewModel.setNavigator(this);

        imageutils = new Imageutils(this);
        imageutils.setImageAttachment_callBack(getImageAttachmentCallback());

        // to observe the image file
        loadingImage();
    }


    private void loadingImage() {
        // Create the observer which updates the UI.
        final Observer<Bitmap> imageObserver = new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable final Bitmap file) {
                // Update the UI, in this case, a TextView.
                //nameTextView.setText(newName);
                activityCropFaceBinding.ivImage.setImageBitmap(file);
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        cropFaceViewModel.getCurrentFaceImage().observe(this, imageObserver);
    }


    private Imageutils.ImageAttachmentListener getImageAttachmentCallback() {
        if (imageAttachmentListener == null)
            imageAttachmentListener = (from, filename, file, uri) -> {
                cropFaceViewModel.getCurrentFaceImage().setValue(file);

            };

        return imageAttachmentListener;
    }


}
