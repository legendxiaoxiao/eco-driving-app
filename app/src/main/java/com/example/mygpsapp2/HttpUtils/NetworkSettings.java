package com.example.mygpsapp2.HttpUtils;

public class NetworkSettings {
    private static final String HOST = "47.104.89.179";
    private static final String PORT = "8800";
    private static final String PATH1 = "/user";
    private static final String PATH2 = "/gps";
    public static final String SIGN_IN = "http://" + HOST + ":" + PORT + PATH1 + "/signIn";
    public static final String SIGN_UP = "http://" + HOST + ":" + PORT + PATH1 + "/signUp";
    public static final String GET_USER = "http://" + HOST + ":" + PORT + PATH1 + "/getUserById";
    public static final String UPDATE = "http://" + HOST + ":" + PORT + PATH1 + "/update";
    public static final String DELETE = "http://" + HOST + ":" + PORT + PATH1 + "/delete";

    public static final String GET_TRIP_NUM = "http://" + HOST + ":" + PORT + PATH2 + "/maxTripId";
    public static final String GET_GPS = "http://" + HOST + ":" + PORT + PATH2 + "/getOneUserOneTripGps";
    public static final String UP_LOAD = "http://" + HOST + ":" + PORT + PATH2 + "/upLoad";
}
