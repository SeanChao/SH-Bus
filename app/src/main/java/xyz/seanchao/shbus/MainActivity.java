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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Bus[] buses = new Bus[32];
    private BusAdapter adapter;
    private List<Bus> busList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private String busId = "022";
    private DrawerLayout mDrawerLayout;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }


        initFab();

        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshBuses();
            }
        });

        navView = findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_1);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_1:
                        mDrawerLayout.closeDrawers();
                        return true;
                    default:
                        return true;
                }
            }
        });

        //从BusInfoProcess中获取信息
        swipeRefresh.setRefreshing(true);
        getOnlineBusInfo(busId);
        swipeRefresh.setRefreshing(false);
    }

    private void initFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("添加");
                dialog.setCancelable(true);
                LayoutInflater inflater = getLayoutInflater();
                View dialogD = inflater.inflate(R.layout.edit_text_layout, (ViewGroup) findViewById(R.id.text_input_layout));
                final EditText editText = (EditText) dialogD.findViewById(R.id.edit_text);
                dialog.setView(dialogD);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO
                        busId = editText.getText().toString();
                        Intent intent = new Intent(MainActivity.this, BusStopActivity.class);
                        intent.putExtra("busId", busId);
                        startActivity(intent);
                        //refreshBuses();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO
                    }
                });
                dialog.create().show();
            }
        });
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

    private void getOnlineBusInfo(final String busId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlBase = "http://webapp.shbustong.com:56008/MobileWeb/ForecastChange.aspx?stopid=bsq";
                String url = urlBase + busId;
                buses = BusInfoProcess.getBusByUrl(url);
                showResponse();
            }
        }).start();
    }

    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里进行UI操作，将结果显示到界面上
                initBuses();
                RecyclerView recyclerView = findViewById(R.id.recycler_view);
                GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 1);
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRefresh() {
        swipeRefresh.setProgressViewOffset(false, 0, 100);
        swipeRefresh.setRefreshing(true);
    }
}
