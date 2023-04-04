package com.example.mygpsapp2.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.example.mygpsapp2.GpsData;
import com.example.mygpsapp2.User;

import java.sql.Date;
import java.util.ArrayList;

public class DBGps {
    private static final String dbname = "gpsdata.db";
    private final Context ct;
    private SQLiteDatabase db;
    private SQLiteDB sdb;

    private static class SQLiteDB extends SQLiteOpenHelper {
        public SQLiteDB(Context context) {
            super(context, dbname, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sdb) {
            sdb.execSQL("create table if not exists tab_gps (usrID integer,tripID integer," +
                    "latitude double," +
                    "longitude double,direct double,speed double,gpstime date," +
                    "address text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sdb, int oldVersion, int newVersion) {
            sdb.execSQL("drop table if exists tab_gps");
            onCreate(sdb);
        }
    }


    public DBGps(Context context) {
        ct = context;
        sdb = new SQLiteDB(ct);
    }

    public void openDB() {
        db = sdb.getWritableDatabase();
    }
    public void deleteData(){
        String str="delete from tab_gps where 1=1";
        db.execSQL(str);
    }

    public void closeDB() {
        sdb.close();
    }

    public boolean addGpsData(GpsData cdata) {
        boolean result = true;
        try {
            @SuppressLint("DefaultLocale") String StrSql = String.format("insert into tab_gps (usrID,tripID,latitude,longitude,direct,speed,gpstime,address) values (%d,%d,%.6f,%.6f,%.2f,%.2f,'%s','%s')",
                    cdata.usrID,cdata.tripID,cdata.latitude, cdata.longitude, cdata.direct, cdata.speed, cdata.gpstime,cdata.address);
            db.execSQL(StrSql);
            result = true;
        } catch (Exception e) {
            result = false;
            Toast.makeText(ct, "GPS:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public ArrayList<GpsData> getAllData(){
        ArrayList<GpsData> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.query("tab_gps",null,null,null,null,null,"gpstime DESC");
        while(cursor.moveToNext()){
            @SuppressLint("Range") int usrID=cursor.getInt(cursor.getColumnIndex("usrID"));
            @SuppressLint("Range") int tripID=cursor.getInt(cursor.getColumnIndex("tripID"));
            @SuppressLint("Range") double latitude=cursor.getDouble(cursor.getColumnIndex("latitude"));
            @SuppressLint("Range") double longitude=cursor.getDouble(cursor.getColumnIndex("longitude"));
            @SuppressLint("Range") double direct=cursor.getDouble(cursor.getColumnIndex("direct"));
            @SuppressLint("Range") double speed=cursor.getDouble(cursor.getColumnIndex("speed"));
            @SuppressLint("Range") String gpstime=cursor.getString(cursor.getColumnIndex("gpstime"));
            @SuppressLint("Range") String address=cursor.getString(cursor.getColumnIndex("address"));
            list.add(new GpsData(usrID,tripID,latitude,longitude,direct,speed,gpstime,address));
        }
        return list;
    }

    @SuppressLint("Range")
    public int maxTripID(){
        ArrayList<Integer> list=new ArrayList<>();
        Cursor cursor=db.query("tab_gps",null,null,null,null,null,"tripID DESC");
        if (cursor.getCount()==0)
            return 0;
        cursor.moveToNext();
        return cursor.getInt(cursor.getColumnIndex("tripID"));
    }
    public boolean isContain(int usrId, int tripId, String gpsTime){
        Cursor cursor=db.query("tab_gps",null,null, null,null,null,null);
        int num=0;
        while(cursor.moveToNext()){
            @SuppressLint("Range") int usrID=cursor.getInt(cursor.getColumnIndex("usrID"));
            @SuppressLint("Range") int tripID=cursor.getInt(cursor.getColumnIndex("tripID"));
            @SuppressLint("Range") String gpstime=cursor.getString(cursor.getColumnIndex("gpstime"));
            if (usrId==usrID&&tripId==tripID&&gpstime.equals(gpsTime)){
                num+=1;
            }
        }
        if (num>0){
            return true;
        }else {
            return false;
        }
    }

    public int getCount(){
        Cursor cursor=db.query("tab_gps",null,null,null,null,null,null);
        return cursor.getCount();
    }

}

