package com.example.mygpsapp2.ui.suggestion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mygpsapp2.GpsData;
import com.example.mygpsapp2.HistoryFragment;
import com.example.mygpsapp2.HttpUtils.NetworkSettings;
import com.example.mygpsapp2.MainActivity;
import com.example.mygpsapp2.R;
import com.example.mygpsapp2.User;
import com.example.mygpsapp2.databinding.FragmentDashboardBinding;
import com.example.mygpsapp2.ui.DBGps;
import com.example.mygpsapp2.ui.ShareDashHis;
import com.example.mygpsapp2.ui.ShareViewModel;
import com.example.mygpsapp2.ui.notifications.NotificationsFragment;
import com.example.mygpsapp2.ui.notifications.NotificationsViewModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SuggestionFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DBGps dbGps;

    private ListView listView;
    private ListView listViewData;
    private int usrID;
    private final OkHttpClient client = new OkHttpClient();

    @SuppressLint("HandlerLeak")
    Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    ArrayList<String> showData=new ArrayList<>();
                    for (int i = 1; i < msg.arg1+1; i++) {
                        showData.add("第"+i+"段行程建议");
                    }
                    ArrayAdapter<String> adapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,showData);
                    listView.setAdapter(adapter);
                    break;
                case 2:
                    String dataStr=(String) msg.obj;
                    List<GpsData> oneGpsData=new ArrayList<>();
                    oneGpsData=JSONObject.parseArray(dataStr,GpsData.class);

                    ArrayList<String> oneGpsDataStr=new ArrayList<>();
                    //正则表达式
                    oneGpsDataStr.add(oneGpsData.get(oneGpsData.size()-1).address);


//                    for(GpsData gd:oneGpsData){
//                        String show=new String(gd.address);
//                        //
//                        oneGpsDataStr.add(show);
//
//                    }
                    ArrayAdapter<String> adapter1=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,oneGpsDataStr);
                    listViewData.setAdapter(adapter1);
                    break;
            }
        }
    };

//    首先声明了一些变量，包括FragmentDashboardBinding类型的变量binding，DBGps类型的变量dbGps，ListView类型的变量listView和listViewData，以及整型变量usrID和一个OkHttpClient类型的client。
//
//    接着声明了一个Handler类型的变量uiHandler，并重写了它的handleMessage方法。uiHandler主要用于在UI线程中更新UI。
//
//    handleMessage方法根据传入的Message对象的what属性的值，执行不同的逻辑。当what属性的值为1时，根据传入的arg1属性的值生成一个String类型的ArrayList，并使用ArrayAdapter将其绑定到listView上；当what属性的值为2时，解析传入的obj属性的值，生成一个GpsData类型的List，将其转换为String类型的ArrayList，再使用ArrayAdapter将其绑定到listViewData上。
//    总体而言，这段代码用于在Fragment中更新UI，具体实现方式是通过Handler在UI线程中更新UI，使用ListView和ArrayAdapter进行数据绑定。




    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView=root.findViewById(R.id.list_view);
        listViewData=root.findViewById(R.id.list_view_data);
        usrID=((MainActivity)getActivity()).getUser_id();

        dbGps = new DBGps(getActivity());
        dbGps.openDB();

        //show 行程
        Request request = new Request.Builder().url(NetworkSettings.GET_TRIP_NUM+"?usrID="+usrID).build();
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
                        Log.d("暂无数据", "该用户暂无出行记录");
                    }
                }
            }
        });

//        首先通过FragmentDashboardBinding将布局文件fragment_dashboard.xml解析为View对象，并设置为root。
//
//        接着通过findViewById方法获取布局文件中的listView和listViewData对象，并将其赋值给全局变量。
//
//        再获取MainActivity中的user_id，并赋值给全局变量usrID。
//
//        然后创建一个DBGps对象，并打开数据库。
//
//        接下来发送一个GET请求，获取该用户的行程数目，并在回调函数中处理返回结果。如果返回结果成功，将结果转换为整型数值，并使用uiHandler发送消息更新UI。如果返回结果失败，则记录日志。
//        总体而言，这段代码用于在Fragment中获取用户的行程数目，并在UI中展示出来。具体实现方式是通过OkHttpClient发送GET请求，使用Callback在异步线程中获取返回结果，并使用uiHandler在UI线程中更新UI。



        //show 详细记录
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int tID=i+1;
                String param="?usrID="+usrID+"&tripID="+tID;
                System.out.println(param);
                Request request1 = new Request.Builder().url(NetworkSettings.GET_GPS+param).build();
                client.newCall(request1).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("onFailure: ", e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()){
                            ResponseBody body=response.body();
                            String bodyString= body.string();
                            System.out.println(bodyString);
                            if (body!=null&&!bodyString.isEmpty()){
                                Message message=new Message();
                                message.what=2;
                                message.obj=bodyString;
                                uiHandler.sendMessage(message);
                            }else {
                                Log.d("暂无数据", "该用户暂无出行记录");
                            }
                        }
                    }
                });
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}



//首先通过listView.setOnItemClickListener方法设置ListView的点击监听器，当用户点击ListView中的某一项时会触发该监听器。
//
//        在监听器的回调函数中，根据点击的项的位置i和用户ID usrID，构造HTTP请求的参数param。
//
//        然后根据构造好的参数param，创建一个HTTP GET请求对象request1，并通过OkHttpClient的newCall方法异步发送请求。
//
//        在Callback的回调函数中，如果返回结果成功，将结果转换为字符串，并使用uiHandler发送消息更新UI。如果返回结果失败，则记录日志。
//
//        最后返回root，即通过FragmentDashboardBinding解析布局文件fragment_dashboard.xml生成的View对象。
//
//        在onDestroyView方法中，将binding对象置为null，释放内存。
//        总体而言，这段代码用于在Fragment中监听用户点击ListView的某一项，并通过OkHttpClient发送GET请求获取该用户对应行程的GPS数据，并在UI中展示出来。具体实现方式是通过设置ListView的点击监听器，在回调函数中构造HTTP请求的参数，通过OkHttpClient发送GET请求，使用Callback在异步线程中获取返回结果，并使用uiHandler在UI线程中更新UI。




//TextToSpeech tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//    @Override
//    public void onInit(int status) {
//        if (status == TextToSpeech.SUCCESS) {
//            int result = tts.setLanguage(Locale.US);
//            if (result == TextToSpeech.LANG_MISSING_DATA ||
//                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                Log.e("TTS", "This Language is not supported");
//            }
//        } else {
//            Log.e("TTS", "Initilization Failed!");
//        }
//    }
//});

//tts.speak("Hello World", TextToSpeech.QUEUE_FLUSH, null);

//@Override
//protected void onDestroy() {
//    super.onDestroy();
//    if (tts != null) {
//        tts.stop();
//        tts.shutdown();
//    }
//}