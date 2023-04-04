package com.example.mygpsapp2.ui.suggestion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SuggestionViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SuggestionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is suggestion fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}