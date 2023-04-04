package com.example.mygpsapp2.ui.notifications;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mygpsapp2.CitySelect.CityPickerActivity;
import com.example.mygpsapp2.DBOpenHelper;
import com.example.mygpsapp2.GpsData;
import com.example.mygpsapp2.HttpUtils.NetworkSettings;
import com.example.mygpsapp2.LoginActivity;
import com.example.mygpsapp2.MainActivity;
import com.example.mygpsapp2.RegisteredActivity;
import com.example.mygpsapp2.User;
import com.example.mygpsapp2.databinding.FragmentNotificationsBinding;
import com.example.mygpsapp2.ui.DBGps;
import com.example.mygpsapp2.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private int param1;  //user_id
    private ArrayAdapter<String> adapter;
    private static final String[] gasType={"其他","汽油","混合动力","插电混合","增程式","纯电","柴油"};
    boolean isSpinnerFirst = true ;
    boolean isSubmit;
    EditText goalText;
    EditText carAge;
    EditText car_brand;
    EditText car_type;
    String items[]={ "上下班", "接送小孩", "商务开会/出差" ,"市内休闲娱乐","跨市休闲娱乐","物流配送","接送乘客"};
    TextView usr;
    TextView email;
    TextView phone;
    TextView cityText;
    Button btnSelectCity;
    Button goalBtn;
    TextView gasText;
    Spinner gas;
    Button submitButton;
    Button logOutButton;
    OkHttpClient client = new OkHttpClient();
    User tempUsr;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
    OkHttpClient client1 = new OkHttpClient();

    @SuppressLint("HandlerLeak")
    Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    tempUsr= (User) msg.obj;

                    usr.setText(tempUsr.getName());
                    email.setText(tempUsr.getEmail());
                    phone.setText(tempUsr.getPhonenum());
                    if (tempUsr.isHaveInfo()){
                        cityText.setText(tempUsr.getMostFreCity());
                        carAge.setText(tempUsr.getCarEge());
                        car_brand.setText(tempUsr.getCarBrand());
                        car_type.setText(tempUsr.getCarType());
                        gasText.setText(tempUsr.getGasType());
                        goalText.setText(tempUsr.getPurpose());
                    }
                    break;
            }
        }
    };


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        usr=root.findViewById(R.id.usr);
        email=root.findViewById(R.id.email);
        phone=root.findViewById(R.id.phone);
        btnSelectCity=root.findViewById(R.id.btnSelectCity);
        cityText=root.findViewById(R.id.cityText);
        gasText=root.findViewById(R.id.gasText);
        gas=root.findViewById(R.id.gas);
        goalBtn=root.findViewById(R.id.goalBtn);
        goalText=root.findViewById(R.id.goalText);
        submitButton=root.findViewById(R.id.submitButton);
        logOutButton=root.findViewById(R.id.logOutButton);
        carAge=root.findViewById(R.id.age);
        car_brand=root.findViewById(R.id.car_brand);
        car_type=root.findViewById(R.id.car_type);

        //获得用户
        param1=((MainActivity)getActivity()).getUser_id();
        Log.d("ID", Integer.toString(param1));

        Request request = new Request.Builder().url(NetworkSettings.GET_USER+"?_id="+param1).build();
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
                        JSONObject user = (JSONObject) JSONObject.parse(bodyString);
                        Message message=new Message();
                        message.what=1;
                        message.obj=JSON.toJavaObject(user,User.class);
                        uiHandler.sendMessage(message);
                    }else {
                        Log.d("出错", "该用户不存在");
                    }
                }
            }
        });

        //选择城市
        root.findViewById(R.id.btnSelectCity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(getActivity(), CityPickerActivity.class);
                intent.putExtra("userId",param1);
                startActivity(intent);
            }
        });
        if (((MainActivity)getActivity()).getFlag()==4)
            cityText.setText(((MainActivity)getActivity()).getCity());

        //选择汽油类型
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,gasType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gas.setAdapter(adapter);
        gas.setOnItemSelectedListener(new SpinnerSelectedListener());
        gas.setVisibility(View.VISIBLE);
        //出行目的
        goalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean[] isCheck=new boolean[items.length];
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("请选择您开车出行最常用目的（多选）");
                builder.setNegativeButton("取消", null);
                builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        isCheck[which]=isChecked;
                    }
                }).setPositiveButton("确定",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder=new StringBuilder();
                        for (int ii=0;ii< isCheck.length;ii++){
                            if (isCheck[ii]){
                                stringBuilder.append(items[ii]).append(" ");
                            }
                        }
                        goalText.setText(stringBuilder.toString());
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //提交
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name1=usr.getText().toString().trim();
                String cityText1 = cityText.getText().toString().trim();
                String age1 = carAge.getText().toString().trim();
                String car_brand1 = car_brand.getText().toString().trim();
                String car_type1 = car_type.getText().toString().trim();
                String gasText1 = gasText.getText().toString().trim();
                String goalText1 = goalText.getText().toString().trim();
                if (cityText1.isEmpty()||age1.isEmpty()||car_brand1.isEmpty()||car_type1.isEmpty()||gasText1.isEmpty()||goalText1.isEmpty()){
                    Toast.makeText(getActivity(),"请填写全部信息",Toast.LENGTH_SHORT).show();
                }else {
                    isSubmit=true;
                    try {
                        User usr=new User(name1,cityText1,age1,car_brand1,car_type1,gasText1,goalText1);
                        System.out.println("传入usr："+usr);
                        Request request1 = new Request.Builder().url(NetworkSettings.UPDATE).put(
                                RequestBody.create(mapper.writeValueAsString(usr), mediaType)
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
                                    boolean isOk=JSON.parseObject(bodyString,Boolean.class);

                                    Looper.prepare();
                                    if (isOk){
                                        Toast.makeText(getActivity(), "用户信息修改成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "出错啦", Toast.LENGTH_SHORT).show();
                                    }
                                    Looper.loop();
                                }else {
                                    Log.d("出错", response.body().string());
                                }
                            }
                        });
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //登出
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (isSpinnerFirst) {
                isSpinnerFirst = false ;
            }else {
                gasText.setText(gasType[arg2]);
            }
        }
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
}