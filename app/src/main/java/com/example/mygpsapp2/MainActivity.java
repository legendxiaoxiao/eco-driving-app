package com.example.mygpsapp2;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.mygpsapp2.CitySelect.CityPickerActivity;
import com.example.mygpsapp2.ui.DBGps;
import com.example.mygpsapp2.ui.dashboard.DashboardFragment;
import com.example.mygpsapp2.ui.home.HomeFragment;
import com.example.mygpsapp2.ui.notifications.NotificationsFragment;
import com.example.mygpsapp2.ui.suggestion.SuggestionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;


import com.example.mygpsapp2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,BottomNavigationView.OnNavigationItemSelectedListener{

    private ActivityMainBinding binding;
    private String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    String city;
    int user_id;
    int flag=-1;
    private BottomNavigationView navigationView;
    private ViewPager viewPager;
    private HomeFragment homeFragment=new HomeFragment();
    private NotificationsFragment notificationsFragment=new NotificationsFragment();
    private DashboardFragment dashboardFragment=new DashboardFragment();
    private SuggestionFragment suggestionFragment=new SuggestionFragment();

    public boolean isGettingData=false;
    public DBGps dbgps0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dbgps0 = new DBGps(MainActivity.this);
        dbgps0.openDB();
        dbgps0.deleteData();

        viewPager = findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(this);//监听viewPager页面变化
        navigationView = findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(this);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return homeFragment;
                    case 1:
                        return suggestionFragment;
                    case 2:
                        return dashboardFragment;
                    case 3:
                        return notificationsFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 4;
            }
        });

        user_id=getIntent().getIntExtra("user_id",0);
        flag=getIntent().getIntExtra("flag",-1);

        if (flag==4){
            user_id=getIntent().getIntExtra("usrID",0);
            city=getIntent().getStringExtra("city");
            Log.d("城市", city);
        }else {
            city="";
        }
    }
    public String getCity(){
        return city;
    }
    public int getFlag(){
        if (flag==4){
            flag=-1;
            return 4;
        }else
            return -1;
    }
    public int getUser_id(){
        return user_id;
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        navigationView.getMenu().getItem(position).setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //menu/navigation.xml里加的android:orderInCategory属性就是下面item.getOrder()取的值
        viewPager.setCurrentItem(menuItem.getOrder());
        return true;
    }

}
