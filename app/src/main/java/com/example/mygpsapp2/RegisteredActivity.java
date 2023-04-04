package com.example.mygpsapp2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mygpsapp2.HttpUtils.NetworkSettings;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RegisteredActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_rgsName, et_rgsEmail, et_rgsPhoneNum, et_rgsPsw1, et_rgsPsw2;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);//禁止横屏
        setContentView(R.layout.activity_registered);
        setTitle("用户注册");//顶部标题改成用户注册
        initView();//初始化界面
    }

    private void initView() {
        et_rgsName = findViewById(R.id.et_rgsName);
        et_rgsEmail = findViewById(R.id.et_rgsEmail);
        et_rgsPhoneNum = findViewById(R.id.et_rgsPhoneNum);
        et_rgsPsw1 = findViewById(R.id.et_rgsPsw1);
        et_rgsPsw2 = findViewById(R.id.et_rgsPsw2);

        Button btn_register = findViewById(R.id.btn_rgs);
        ImageView iv_back = findViewById(R.id.iv_back);
        /**
         * 注册页面能点击的就三个地方
         * top处返回箭头、刷新验证码图片、注册按钮
         */
        iv_back.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back://返回登录界面
                Intent intent = new Intent(RegisteredActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_rgs://注册按钮
                //获取用户输入的用户名、密码、验证码
                String username = et_rgsName.getText().toString().trim();
                String password1 = et_rgsPsw1.getText().toString().trim();
                String password2 = et_rgsPsw2.getText().toString().trim();
                String email = et_rgsEmail.getText().toString().trim();
                String phonenum = et_rgsPhoneNum.getText().toString().trim();

                //注册验证
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password1) && !TextUtils.isEmpty(password2)) {
                    //判断两次密码是否一致
                    if (password1.equals(password2)) {
//                        String pswd=DigestUtils.md5Hex(password1);
                        try {
                            Request request = new Request.Builder().url(NetworkSettings.SIGN_UP).post(
                                    //请求体类型为application/json;charset=utf-8，利用了Jackson序列化为JSON
                                    RequestBody.create(mapper.writeValueAsString(new User(username, password1,email,phonenum)), mediaType)
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
                                        if (body!=null){
                                            String str=body.string();
                                            if (str.contains("true")){
                                                Intent intent1 = new Intent(RegisteredActivity.this, LoginActivity.class);
                                                startActivity(intent1);
                                                finish();
                                                Looper.prepare();
                                                Toast.makeText(RegisteredActivity.this, "验证通过，注册成功", Toast.LENGTH_SHORT).show();
                                                Looper.loop();
                                            }else {
                                                Looper.prepare();
                                                Toast.makeText(RegisteredActivity.this, "该用户名已使用，请更换用户名", Toast.LENGTH_SHORT).show();
                                                Looper.loop();
                                            }
                                        }
                                    }
                                }
                            });
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "两次密码不一致,注册失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "注册信息不完善,注册失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
