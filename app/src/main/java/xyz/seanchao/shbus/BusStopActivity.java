package xyz.seanchao.shbus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class BusStopActivity extends AppCompatActivity {

    private Bus[] buses = new Bus[32];
    private BusAdapter adapter;
    private List<Bus> busList = new ArrayList<>();
    private String busId = "022";
    private String busStopName = "";
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefresh;

    String file = "nav_items";
    private BusStop newBusStop = new BusStop("", "");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        busId = intent.getStringExtra("busId");
        setContentView(R.layout.activity_bus_stop);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        swipeRefresh = findViewById(R.id.bus_stop_swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshBuses();
            }
        });

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
                //生成newBusStop
                newBusStop = new BusStop(busStopName, busId);
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
                if (busStopName.equals("")) {
                    Toast.makeText(BusStopActivity.this, "未获取到信息", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
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

    private void refreshBuses() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showRefresh();
                        getOnlineBusInfo(busId);
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void showRefresh() {
        swipeRefresh.setProgressViewOffset(false, 0, 100);
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bus_stop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_done:
                String cuttentData = load(file);
                String newData = JsonProcess.toJson(newBusStop, cuttentData);
                savePri(newData);
                Snackbar.make(this.findViewById(R.id.action_done), "保存成功", Snackbar.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void savePri(String inputText) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            if (!new File("nav_items").exists()) {
                save("[{\"name\":\"\",\"id\":\"\"}]");
            }
            //PRIVATE覆盖 APPEDN增加
            out = openFileOutput("nav_items", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save(String inputText) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            //PRIVATE覆盖 APPEDN增加
            out = openFileOutput("nav_items", Context.MODE_APPEND);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String load(String fileName) {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            File file = new File("nav_items");
            if (!file.exists()) {
                save("[{\"name\":\"\",\"id\":\"\"}]");
            }
            in = openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }
}
