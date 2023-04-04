package com.example.mygpsapp2.CitySelect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.mygpsapp2.CitySelect.adapter.CityListAdapter;
import com.example.mygpsapp2.CitySelect.widget.SideLetterBar;
import com.example.mygpsapp2.CitySelect.adapter.CityListAdapter;
import com.example.mygpsapp2.CitySelect.bean.AreasBean;
import com.example.mygpsapp2.CitySelect.bean.City;
import com.example.mygpsapp2.CitySelect.bean.CityPickerBean;
import com.example.mygpsapp2.CitySelect.bean.LocateState;
import com.example.mygpsapp2.CitySelect.util.GsonUtil;
import com.example.mygpsapp2.CitySelect.util.PinyinUtils;
import com.example.mygpsapp2.CitySelect.util.ReadAssetsFileUtil;
import com.example.mygpsapp2.CitySelect.widget.SideLetterBar;
import com.example.mygpsapp2.MainActivity;
import com.example.mygpsapp2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class CityPickerActivity extends FragmentActivity {
    private ListView mListView;
    private SideLetterBar mLetterBar;
    private CityListAdapter mCityAdapter;
    private int usrID;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cp_activity_city_list);
        usrID=getIntent().getIntExtra("userId",0);
        initView();
        initData();
    }

    protected void initView() {
        mListView = findViewById(R.id.listview_all_city);
        TextView overlay = findViewById(R.id.tv_letter_overlay);
        mLetterBar = findViewById(R.id.side_letter_bar);
        mLetterBar.setOverlay(overlay);
        mLetterBar.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                int position = mCityAdapter.getLetterPosition(letter);
                mListView.setSelection(position);
            }
        });
        mCityAdapter = new CityListAdapter(this);
        mListView.setAdapter(mCityAdapter);
    }

    public void getCityData() {
        String json = ReadAssetsFileUtil.getJson(this, "city.json");
        CityPickerBean bean = GsonUtil.getBean(json, CityPickerBean.class);
        HashSet<City> citys = new HashSet<>();
        for (AreasBean areasBean : bean.data.areas) {
            String name = areasBean.name.replace("　", "");
            citys.add(new City(areasBean.id, name, PinyinUtils.getPinYin(name), areasBean.is_hot == 1));
            for (AreasBean.ChildrenBeanX childrenBeanX : areasBean.children) {
                citys.add(new City(childrenBeanX.id, childrenBeanX.name, PinyinUtils.getPinYin(childrenBeanX.name), childrenBeanX.is_hot == 1));
            }
        }
        //set转换list
        ArrayList<City> cities = new ArrayList<>(citys);
        //按照字母排序
        Collections.sort(cities, new Comparator<City>() {
            @Override
            public int compare(City city, City t1) {
                return city.getPinyin().compareTo(t1.getPinyin());
            }
        });
        mCityAdapter.setData(cities);
    }

    protected void initData() {
        getCityData();
        mCityAdapter.setOnCityClickListener(new CityListAdapter.OnCityClickListener() {
            @Override
            public void onCityClick(String name) {//选择城市
                Toast.makeText(CityPickerActivity.this, name, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(CityPickerActivity.this, MainActivity.class);
                intent.putExtra("usrID",usrID);
                intent.putExtra("city",name);
                intent.putExtra("flag",4);
                startActivity(intent);
                CityPickerActivity.this.finish();
            }

            @Override
            public void onLocateClick() {//点击定位按钮
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            //销毁定位客户端之后，若要重新开启定位请重新New一个AMapLocationClient对象。
            mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
        }
    }

}
