

package com.example.authenteqfacedetection.view.base;

import androidx.lifecycle.ViewModel;


public abstract class BaseViewModel<N> extends ViewModel {



    private N mNavigator;

    public BaseViewModel() {

    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }




    public N getNavigator() {
        return mNavigator;
    }

    public void setNavigator(N navigator) {
        this.mNavigator = navigator;
    }



}
