package xyz.seanchao.shbus;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;


public class BusStopActivity extends AppCompatActivity {

    private Bus[] buses = new Bus[32];
    private BusAdapter adapter;
    private List<Bus> busList = new ArrayList<>();
    private String busId = "022";
    private String busStopName = "";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        busId = intent.getStringExtra("busId");
        setContentView(R.layout.activity_bus_stop);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //从BusInfoProcess中获取信息
        getOnlineBusInfo(busId);

    }


    private void getOnlineBusInfo(final String busId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlBase = "http://webapp.shbustong.com:56008/MobileWeb/ForecastChange.aspx?stopid=bsq";
                String url = urlBase + busId;
                buses = BusInfoProcess.getBusByUrl(url);
                busStopName = BusInfoProcess.getBusStopName(url);
                showResponse();
            }
        }).start();
    }

    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里进行UI操作，将结果显示到界面上
                toolbar.setTitle(busStopName);
                initBuses();
                RecyclerView recyclerView = findViewById(R.id.bus_stop_recycler_view);
                GridLayoutManager layoutManager = new GridLayoutManager(BusStopActivity.this, 1);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new BusAdapter(busList);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private void initBuses() {
        busList.clear();
        for (int i = 0; i < 32; i++) {
            if (buses[i].getName().equals("")) {
                boolean b = (buses[i] == null);
                System.out.println("TEST:" + b);
                break;
            }
            busList.add(buses[i]);
        }
    }

}
