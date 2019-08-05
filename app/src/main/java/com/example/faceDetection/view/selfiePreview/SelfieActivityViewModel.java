package com.example.faceDetection.view.selfiePreview;



import com.example.faceDetection.view.base.BaseViewModel;

public class SelfieActivityViewModel extends BaseViewModel<SelfieActivityNavigator> {


    public SelfieActivityViewModel() {
        super();
    }


    public void onBtnCaptureClicked() {
        getNavigator().onBtnCaptureClicked();
    }


}
