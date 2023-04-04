package com.example.mygpsapp2.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mygpsapp2.GpsData;

import java.util.ArrayList;

public class ShareViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<GpsData>> gpsData=new MutableLiveData<>();
    public void setGpsData(ArrayList<GpsData> data){
        gpsData.setValue(data);
    }
    public LiveData<ArrayList<GpsData>> getGpsData(){
        return gpsData;
    }
}
