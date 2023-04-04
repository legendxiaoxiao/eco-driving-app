package com.example.mygpsapp2.ui.dashboard;

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

public class DashboardFragment extends Fragment {

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
                        showData.add("第"+i+"次行程");
                    }
                    ArrayAdapter<String> adapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,showData);
                    listView.setAdapter(adapter);
                    break;
                case 2:
                    String dataStr=(String) msg.obj;
                    List<GpsData> oneGpsData=new ArrayList<>();
                    oneGpsData=JSONObject.parseArray(dataStr,GpsData.class);

                    ArrayList<String> oneGpsDataStr=new ArrayList<>();
                    for(GpsData gd:oneGpsData){
                        oneGpsDataStr.add(gd.toString());
                    }
                    ArrayAdapter<String> adapter1=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,oneGpsDataStr);
                    listViewData.setAdapter(adapter1);
                    break;
            }
        }
    };

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