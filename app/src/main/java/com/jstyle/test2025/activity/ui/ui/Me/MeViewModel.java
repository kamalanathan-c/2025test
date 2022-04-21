package com.jstyle.test2025.activity.ui.ui.Me;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MeViewModel extends ViewModel {

    private final MutableLiveData<String> mText1;

    public MeViewModel() {
        mText1 = new MutableLiveData<>();
        mText1.setValue("This is Me fragment");
    }

    public LiveData<String> getText() {
        return mText1;
    }
}