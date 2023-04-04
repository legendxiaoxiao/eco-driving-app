package com.example.mygpsapp2;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mygpsapp2.ui.ShareDashHis;
import com.example.mygpsapp2.ui.ShareViewModel;
import com.example.mygpsapp2.ui.dashboard.DashboardFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    private ShareDashHis shareDashHis;
    private ImageView imageView;
    private ListView listView;

    public HistoryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_history, container, false);

        listView=view.findViewById(R.id.list_view_1);
        imageView=view.findViewById(R.id.iv_back_1);
        //获得Dashboard数据
        shareDashHis=new ViewModelProvider(requireActivity(),new ViewModelProvider.NewInstanceFactory()).get(ShareDashHis.class);
        ArrayList<GpsData> gpsData=shareDashHis.getGpsData().getValue();
        ArrayAdapter<GpsData> adapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,gpsData);
        listView.setAdapter(adapter);
        //跳转回Dashboard
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                callbackHistoryFragment.skipToDashboard();
                NavHostFragment.findNavController(HistoryFragment.this).navigate(R.id.action_navigation_history_to_navigation_dashboard2);
            }
        });

        return view;
    }
}