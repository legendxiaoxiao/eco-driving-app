package com.example.mygpsapp2;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GpsData {
    public int usrID;
    public int tripID;
    public double latitude;
    public double longitude;
    public double direct;
    public double speed;
    public String gpstime;
    public String address;

    public GpsData() {
    }

    public GpsData(int usrID, int tripID, double latitude, double longitude, double direct, double speed, String gpsTime, String address) {
        this.usrID = usrID;
        this.tripID = tripID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.direct = direct;
        this.speed = speed;
        this.gpstime = gpsTime;
        this.address = address;
    }


    @Override
    public String toString() {
        return "usrID=" + usrID +
                "tripID=" + tripID +
                "\n纬度=" + latitude +
                "\n经度=" + longitude +
                "\n方向=" + direct +
                " 速度=" + speed +
                "\n时间=" + gpstime +
                "\n地址=" + address ;
    }
}
