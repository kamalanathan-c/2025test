package com.jstyle.test2025.activity.ui.ui.ECG;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EcgViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EcgViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}