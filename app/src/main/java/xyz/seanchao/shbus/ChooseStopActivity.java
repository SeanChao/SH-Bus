package xyz.seanchao.shbus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChooseStopActivity extends AppCompatActivity {

    private String stopName;
    BusStop[] busStops = new BusStop[2000];
    BusStop[] matchedBusStops = new BusStop[16];
    ProgressDialog progressDialog;
    List<BusStop> busStopsList = new ArrayList<>();
	private BusStopAdapter adapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_stop);
        toolbar = (Toolbar) findViewById(R.id.cb_toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        stopName = getIntent().getStringExtra("busName");
        Log.d("Debug", toolbar.getTitle().toString());
        Log.d("Debug","stopName:"+stopName);
        progressDialog = new ProgressDialog(ChooseStopActivity.this);
        progressDialog.setMessage("正在努力加载");
        progressDialog.setCancelable(false);
        progressDialog.show();
        byName();

    }

    public void onClick() {
        Intent intent = new Intent(ChooseStopActivity.this, ChooseStopActivity.class);
    }

    private void byName() {
        new Thread(new Runnable() {
            String responseData = "";
            @Override
            public void run() {
                try {
                    toolbar.setTitle(stopName);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://app.seanchao.xyz/app/all_stop.json").build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                busStops = JsonProcess.fromJsoninName(responseData);
                matchedBusStops = new BusStop[16];
                int index = 0;
                for (int i = 0; i < busStops.length; i++) {
                    if (busStops[i].getName().equals(stopName)) {
                        matchedBusStops[index] = busStops[i];
                        Log.d("Debug","matched stop["+index+"]:"+matchedBusStops[index].getName());
                        index ++;
                    }
                }
                for(int i = 0 ; i < matchedBusStops.length ; i++){
                    if (i > index - 1) {
                        matchedBusStops[i] = new BusStop("", "", "");
                    }
                }
                showResponse();
                progressDialog.dismiss();
            }
        }).start();

    }

    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里进行UI操作，将结果显示到界面上
                toolbar.setTitle(stopName);
                initBuses();
                RecyclerView recyclerView = findViewById(R.id.cb_recycler_view);
                GridLayoutManager layoutManager = new GridLayoutManager(ChooseStopActivity.this, 1);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new BusStopAdapter(busStopsList);
                recyclerView.setAdapter(adapter);
                if (stopName.equals("")) {
                    Toast.makeText(ChooseStopActivity.this, "未获取到信息", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });
    }
	
	private void initBuses() {
        busStopsList.clear();
        for (int i = 0; i < matchedBusStops.length; i++) {
            if (matchedBusStops[i].getName().equals("")) {
                boolean b = (matchedBusStops[i] == null);
                System.out.println("TEST:" + b);
                break;
            }
            busStopsList.add(matchedBusStops[i]);
        }
    }

}
