package com.example.authenteqfacedetection.view.selfiePreview;



import com.example.authenteqfacedetection.view.base.BaseViewModel;

public class SelfieActivityViewModel extends BaseViewModel<SelfieActivityNavigator> {


    public SelfieActivityViewModel() {
        super();
    }


    public void onBtnCaptureClicked() {
        getNavigator().onBtnCaptureClicked();
    }


}
