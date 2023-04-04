package com.example.mygpsapp2.ui.home;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.text.format.DateFormat;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;


import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.example.mygpsapp2.GpsData;
import com.example.mygpsapp2.HttpUtils.NetworkSettings;
import com.example.mygpsapp2.MainActivity;
import com.example.mygpsapp2.R;
import com.example.mygpsapp2.User;
import com.example.mygpsapp2.databinding.FragmentHomeBinding;
import com.example.mygpsapp2.ui.DBGps;
import com.example.mygpsapp2.ui.ShareViewModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private FragmentHomeBinding binding;

    private TextView tvDirection;
    private TextView tvSpeed;
    private TextView tvGpsTime;
    private TextView tvAddress;

//    private TextView tvLevel;

    private Button btnmanual;
    private Button btnexit;
    public DBGps dbgps;
    private String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private int tempTripID;
    private int usrId;
    private MapView mapView;
    private BaiduMap baiduMap;
    public LocationClient mLocationClient=null;
    private MyLocationListener myLocationListener = new MyLocationListener();
    private boolean isGetPermissions=true;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private static final MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
    OkHttpClient client1 = new OkHttpClient();

    public double lic=0;

    public DrivingRouteLine.DrivingStep drivingStep=new DrivingRouteLine.DrivingStep();

    @SuppressLint("HandlerLeak")
    Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    tempTripID=msg.arg1 + 1;
                    break;
                case 2:
                    tempTripID=1;
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbgps=((MainActivity) getActivity()).dbgps0;
        tvDirection = root.findViewById(R.id.tvdirection);
        tvSpeed = root.findViewById(R.id.tvspeed);
        tvGpsTime = root.findViewById(R.id.tvgpstime);
        tvAddress = root.findViewById(R.id.tvaddress);
        btnmanual = root.findViewById(R.id.btnmanual);
        btnmanual.setOnClickListener(this);
        btnexit = root.findViewById(R.id.btnexit);
        btnexit.setOnClickListener(this);
        mapView=root.findViewById(R.id.mapView);

        if (((MainActivity) getActivity()).isGettingData==true){
            btnmanual.setEnabled(false);
            btnexit.setEnabled(true);
        }else {
            btnmanual.setEnabled(true);
            btnexit.setEnabled(false);
        }
        getPermissions();

        try {
            baiduMap=mapView.getMap();
            //开启交通图
            baiduMap.setTrafficEnabled(true);
            baiduMap.setMyLocationEnabled(true);
            mLocationClient = new LocationClient(getActivity());
            //通过LocationClientOption设置LocationClient相关参数
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            option.setOpenGps(true); // 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(1000);
            option.setNeedNewVersionRgc(true);
            option.setIsNeedAddress(true);
            option.setIsNeedLocationDescribe(true);
            option.setNeedDeviceDirect(true);
            //设置locationClientOption
            mLocationClient.setLocOption(option);
            /**
             * 设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生效
             * customMarker用户自定义定位图标
             * enableDirection是否允许显示方向信息
             * locationMode定位图层显示方式
             */
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
            baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.NORMAL, true, bitmapDescriptor));

            mLocationClient.registerLocationListener(myLocationListener);
            //开启地图定位图层
            mLocationClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mapView == null){
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .speed(location.getSpeed())
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            String addr=Integer.toString(drivingStep.getRoadLevel())+":"+location.getAddrStr()+":"+location.getLocationDescribe();
//            String addr=Integer.toString(drivingStep)+":"+location.getAddrStr()+":"+location.getLocationDescribe();
            if (locData.direction==-1){
                isGetPermissions=false;
            }else {
                isGetPermissions=true;
            }
            if (!isGetPermissions){  //未获得权限
                btnmanual.setEnabled(false);
                btnexit.setEnabled(false);
                tvDirection.setText("请打开地理位置权限");
                tvSpeed.setText("");
                tvGpsTime.setText("");
                tvAddress.setText("");
            }else {
                //write data into database
                if (((MainActivity) getActivity()).isGettingData){  //已获得权限，且已打开gps
                    btnmanual.setEnabled(false);
                    btnexit.setEnabled(true);
                    String date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    usrId=((MainActivity) getActivity()).getUser_id();

                    GpsData gpsData=new GpsData(usrId, tempTripID,locData.latitude,locData.longitude,
                            locData.direction, locData.speed,date,addr);
                    tvDirection.setText(String.format("方向:%.1f°", gpsData.direct));
                    tvSpeed.setText(String.format("速度:%.1fkm/h", gpsData.speed));
                    tvGpsTime.setText(String.format("记录时间:%s", gpsData.gpstime));
                    tvAddress.setText(String.format("地址:%s",gpsData.address));
//                  tvLevel.setText(string.format("等级:%s",gpsData.level));
                    if (!dbgps.isContain(usrId,tempTripID,date)){
                        dbgps.addGpsData(gpsData);
                        Log.d("正在获取", gpsData.toString());


//                    lic+=locData.speed/3.6;
//                    if (lic>=1500){
//                            lic=0;
//                            //后端交互
//                        OkHttpClient client = new OkHttpClient();
//                        MediaType mediaType = MediaType.parse("application/json");
//                        String jsonRequestBody = "{\"time\":\"value\"}";
//                        RequestBody requestBody = RequestBody.create(mediaType, jsonRequestBody);
//
//                        Request request = new Request.Builder()
//                                .url(NetworkSettings.UP_LOAD)
//                                .post(requestBody)
//                                .build();
//                        Call call = client.newCall(request);
//                        call.enqueue(new Callback() {
//                            @Override
//                            public void onFailure(Call call, IOException e) {
//                                // 请求失败
//                            }
//                            @Override
//                            public void onResponse(Call call, Response response) throws IOException {
//                                // 请求成功，处理响应数据
//                                String responseString = response.body().string();
//                                // ...
//                            }
//                        });


//                        首先，将locData.speed的值转换为米每秒，并且除以3.6，得到车辆的速度，然后将这个速度加到lic变量中。
//
//                        然后，判断lic变量的值是否大于等于1500，如果是，就执行if语句块中的代码。
//
//                        在if语句块中，先创建一个OkHttpClient对象，用于发送请求。
//
//                        然后，定义一个MediaType对象，用于指定请求的Content-Type。
//
//                        接着，创建一个RequestBody对象，将需要发送的数据转换为JSON格式，并设置Content-Type为application/json。
//
//                        然后，创建一个Request对象，设置请求的URL和请求的方法为POST，将RequestBody对象作为请求体，并且设置请求的Content-Type为application/json。
//
//                        接着，使用OkHttpClient对象的newCall方法创建一个Call对象，并且将Request对象作为参数。
//
//                        然后，调用Call对象的enqueue方法，将请求加入到请求队列中，并且设置一个回调函数，当请求成功或失败时，回调函数将会被调用。
//
//                        在回调函数中，如果请求失败，就在onFailure方法中处理失败的情况；如果请求成功，就在onResponse方法中处理响应数据。
//
//                        最后，请求完成后，将lic变量重置为0。


                        //request
//                        }
                    }
                }else {  //已获得权限，且未打开gps
                    btnmanual.setEnabled(true);
                    btnexit.setEnabled(false);
                    tvDirection.setText("");
                }
            }

            // 设置定位数据, 只有先允许定位图层后设置数据才会生效
            baiduMap.setMyLocationData(locData);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng).zoom(20.0f);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

   public void getPermissions(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(),"没有权限,请手动开启定位权限",Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 10);
        }
    }

    //Android6.0申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case 10:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理
                } else {
                    // 没有获取到权限，做特殊处理
                    Toast.makeText(getActivity(), "获取位置权限失败，请手动开启", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnmanual:
                Toast.makeText(getActivity(), "开始收集GPS信息", Toast.LENGTH_LONG).show();

                //获取tempTripID
                Request request = new Request.Builder().url(NetworkSettings.GET_TRIP_NUM+"?usrID="+
                        ((MainActivity)getActivity()).getUser_id()).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("onFailure: ", e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()){
                            ResponseBody body=response.body();
                            String bodyString= body.string();
                            if (body!=null&&!bodyString.isEmpty()){
                                Message message=new Message();
                                message.what=1;
                                message.arg1=Integer.parseInt(bodyString);
                                uiHandler.sendMessage(message);
                            }else {
                                Message message=new Message();
                                message.what=2;
                                uiHandler.sendMessage(message);
                            }
                        }
                    }
                });
                btnmanual.setEnabled(false);
                btnexit.setEnabled(true);
                ((MainActivity) getActivity()).isGettingData=true;
                break;
            case R.id.btnexit:
                Toast.makeText(getActivity(), "退出", Toast.LENGTH_LONG).show();
                ((MainActivity) getActivity()).isGettingData=false;
                btnmanual.setEnabled(true);
                btnexit.setEnabled(false);
                tvDirection.setText("");
                tvSpeed.setText("");
                tvGpsTime.setText("");
                tvAddress.setText("");
//              tvLevel.setText("");

                List<GpsData> all= dbgps.getAllData();
                List<GpsData> upLoad =new ArrayList<>();
                for (GpsData d:all){
                    if (d.usrID==usrId&&d.tripID==tempTripID){
                        upLoad.add(d);
                    }
                }
                System.out.println("-------------------------要上传的数据--------");
                for (GpsData g:upLoad) {
                    System.out.println(g.toString());
                }
                System.out.println("-------------------------要上传的数据--------");


                //上传GPS
                Request request1 = null;
                try {
                    request1 = new Request.Builder().url(NetworkSettings.UP_LOAD).post(
                            RequestBody.create(mapper.writeValueAsString(upLoad), mediaType)
                    ).build();
                    client1.newCall(request1).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("onFailure: ", e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()){
                                ResponseBody body=response.body();
                                String bodyString= body.string();
                                boolean isOk= JSON.parseObject(bodyString,Boolean.class);
                                if (!isOk){
                                    Log.d("出错", "false");
                                }
                            }else {
                                Log.d("出错le", response.body().string());
                                Log.d("出错le", response.message());
                                Log.d("出错le", response.headers().toString());



                            }
                        }
                    });
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}