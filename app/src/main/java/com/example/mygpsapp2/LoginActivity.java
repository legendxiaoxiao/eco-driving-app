package com.example.mygpsapp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationManager;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Looper;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.mygpsapp2.HttpUtils.NetworkSettings;
import com.example.mygpsapp2.ui.notifications.NotificationsFragment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 此类 implements View.OnClickListener 之后，
 * 就可以把onClick事件写到onCreate()方法之外
 * 这样，onCreate()方法中的代码就不会显得很冗余
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_User, et_Psw;
    private CheckBox cb_rmbPsw;
    private String userName;
    private SharedPreferences.Editor editor;

    private final ObjectMapper mapper = new ObjectMapper();
    private static final MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();//隐藏标题栏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);//禁止横屏
        setContentView(R.layout.activity_login);

        initView();//初始化界面

        SharedPreferences sp = getSharedPreferences("user_mes", MODE_PRIVATE);
        editor = sp.edit();
        if(sp.getBoolean("flag",false)){
            String user_read = sp.getString("user","");
            String psw_read = sp.getString("psw","");
            et_User.setText(user_read);
            et_Psw.setText(psw_read);
        }
    }

    private void initView() {
        //初始化控件
        et_User = findViewById(R.id.et_User);
        et_Psw = findViewById(R.id.et_Psw);
        cb_rmbPsw = findViewById(R.id.cb_rmbPsw);
        Button btn_Login = findViewById(R.id.btn_Login);
        TextView tv_register = findViewById(R.id.tv_Register);
        //设置点击事件监听器
        btn_Login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_Register: //注册
                Intent intent = new Intent(LoginActivity.this, RegisteredActivity.class);//跳转到注册界面
                startActivity(intent);
                finish();
                break;

            case R.id.btn_Login:

                String name = et_User.getText().toString().trim();
                String password = et_Psw.getText().toString().trim();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)) {
                    try {
//                        String pswd= DigestUtils.md5Hex(password);
                        Request request = new Request.Builder().url(NetworkSettings.SIGN_IN).post(
                                //请求体类型为application/json;charset=utf-8，利用了Jackson序列化为JSON
                                RequestBody.create(mapper.writeValueAsString(new User(name, password)), mediaType)
                        ).build();

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

                                        if(cb_rmbPsw.isChecked()){
                                            editor.putBoolean("flag",true);
                                            editor.putString("user",user.getString("name"));
                                            editor.putString("psw",user.getString("password"));
                                            editor.apply();
                                            cb_rmbPsw.setChecked(true);
                                        }else {
                                            editor.putString("user",user.getString("name"));
                                            editor.putString("psw","");
                                            editor.apply();
                                        }

                                        Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);//设置自己跳转到成功的界面

                                        intent1.putExtra("user_id",user.getIntValue("_id"));
                                        startActivity(intent1);

                                        Looper.prepare();
                                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                        Looper.loop();

                                    } else {
                                        Looper.prepare();
                                        Toast.makeText(LoginActivity.this, "用户名或密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                }
                            }
                        });
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "请输入你的用户名或密码", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
