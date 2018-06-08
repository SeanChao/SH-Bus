package xyz.seanchao.shbus;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Bus[] buses = new Bus[32];
    private SingleBusStop[] singleBusStops = new SingleBusStop[32];
    private SingleBus[] singleBuses = new SingleBus[32];
    private SingleBusAdapter singleAdapter;
    private BusAdapter adapter;
    private List<Bus> busList = new ArrayList<>();
    private List<SingleBus> singleBusList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private String busId = "022";
    private String busStopName;
    private DrawerLayout mDrawerLayout;
    private NavigationView navView;
    private BusStop[] stops;
    private String file = "nav_items.json";
    private String favourite_file = "favourite.json";
    private SwipeMenuRecyclerView recyclerView;
    private int flag = 0; //显示模式的标识符0->fav 1->normal
    private String url = "";
    int mappedId = 0;
    private AppCompatCheckBox cb_id;
    private AppCompatCheckBox cb_name;
    private boolean cb_id_checked = true;
    private boolean cb_name_chceked = false;
    public static boolean isConnectToNetwork = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recycler_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        checkInternet();

        initFab();

        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkInternet();
                if (!isConnectToNetwork) {
                    toastNoNetwork();
                    hideRefresh();
                    return;
                }
                refreshBuses();
            }
        });

        navView = findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.favourite);

        addMenuItem();

        //menu.setGroupVisible(R.id.group_bus_stop, true);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.favourite:
                        toolbar.setTitle(R.string.activity_label);
                        mDrawerLayout.closeDrawers();
                        flag = 0;
                        initFavFile();
                        return true;
                    case R.id.bus_stop_primary:
                        mDrawerLayout.closeDrawers();
                        mappedId = id = 0;
                        flag = 1;
                        busId = stops[id].getId();
                        toolbar.setTitle(stops[id].getName());
                        mDrawerLayout.closeDrawers();
                        getOnlineBusInfo(busId);
                        //showResponse();
                        //refreshBuses();
                        return true;
                    default:
                        flag = 1;
                        mappedId = id;
                        busId = stops[id].getId();
                        toolbar.setTitle(stops[id].getName());
                        mDrawerLayout.closeDrawers();
                        getOnlineBusInfo(busId);
                        //refreshBuses();
                        return true;
                }
                //return true;
            }
        });

        //从BusInfoProcess中获取信息
        //swipeRefresh.setRefreshing(true);
        initFavFile();
        //getOnlineBusInfo(busId);
        //swipeRefresh.setRefreshing(false);

        setupRecyclerView();
    }

    private void initFab() {
        String districtId = "";
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("添加");
                dialog.setCancelable(true);
                LayoutInflater inflater = getLayoutInflater();
                View dialogD = inflater.inflate(R.layout.edit_text_layout, (ViewGroup) findViewById(R.id.text_input_layout));
                final Spinner spinner = dialogD.findViewById(R.id.spinner);
                final EditText editText = (EditText) dialogD.findViewById(R.id.edit_text);
                dialog.setView(dialogD);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String inputText = editText.getText().toString();
                        String busId = "";
                        Log.d("debug", "" + spinner.getSelectedItemPosition());
                        switch (spinner.getSelectedItemPosition()) {
                            case 0:
                                busId += "hp";
                                break;
                            case 1:
                                busId += "xh";
                                break;
                            case 2:
                                busId += "ja";
                                break;
                            case 3:
                                busId += "zb";
                                break;
                            case 4:
                                busId += "pd";
                                break;
                            case 5:
                                busId += "mh";
                                break;
                            case 6:
                                busId += "bsq";
                                break;
                            case 7:
                                busId += "sj";
                                break;
                            case 8:
                                busId += "jd";
                                break;
                            case 9:
                                busId += "yp";
                                break;
                            case 10:
                                busId += "pt";
                                break;
                            case 11:
                                busId += "fx";
                                break;
                            case 12:
                                busId += "qp";
                                break;
                            case 13:
                                busId += "cn";
                                break;
                            case 14:
                                busId += "cm";
                                break;
                            case 15:
                                busId += "hk";
                                break;
                            case 16:
                                busId += "js";
                                break;

                        }
                        busId += inputText;
                        Intent intent = null;
                        if (cb_id_checked) {
                            intent = new Intent(MainActivity.this, BusStopActivity.class);
                            intent.putExtra("busId", busId);
                            // intent.putExtra("flag", 1);
                        } else if (cb_name_chceked) {
                            intent = new Intent(MainActivity.this, ChooseStopActivity.class);
                            intent.putExtra("busName", busId);
                            //intent.putExtra("flag", 2);
                        }
                        startActivity(intent);
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO
                    }
                });
                //setupCheckBox
                cb_id = dialogD.findViewById(R.id.cb_id);
                cb_name = dialogD.findViewById(R.id.cb_name);
                cb_id.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        cb_name.setChecked(!isChecked);
                        cb_id_checked = isChecked;
                        if (isChecked) {
                            editText.setHint(R.string.edit_text_hint_id);
                        }
                    }
                });
                cb_name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        cb_id.setChecked(!isChecked);
                        cb_name_chceked = isChecked;
                        if (isChecked) {
                            editText.setHint(R.string.edit_text_hint_name);
                        }
                    }
                });


                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

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
                        if (flag == 0) {
                            initFavFile();
                            singleAdapter.notifyDataSetChanged();
                        } else if (flag == 1) {
                            getOnlineBusInfo(busId);
                            adapter.notifyDataSetChanged();
                        }
                        System.out.println();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    //default:通过busId->爬虫处理成bus[]->busList->adapter->呈现结果
    //default:通过busId->爬虫处理成bus[]->busList->adapter
    private void getOnlineBusInfo(final String busId) {
        checkInternet();
        if (!isConnectToNetwork) {
            toastNoNetwork();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                //showRefresh();
                String urlBase = "http://webapp.shbustong.com:56008/MobileWeb/ForecastChange.aspx?stopid=bsq";
                url = urlBase + busId;
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
                Log.d("Debug", "开始执行showResponse()");
                // 在这里进行UI操作，将结果显示到界面上
                if (flag == 1) {
                    initBuses();
                    //GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 1);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    adapter = new BusAdapter(busList);
                    recyclerView.setAdapter(adapter);

                } else if (flag == 0) {
                    initBuses();
                    //GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 1);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    singleAdapter = new SingleBusAdapter(singleBusList);
                    recyclerView.setAdapter(singleAdapter);
                    hideRefresh();
                }
            }
        });
    }

    private void initBuses() {
        if (flag == 0) {
            singleBusList.clear();
            for (int i = 0; i < 32; i++) {
                if (singleBuses[i].getName().equals("")) {
                    boolean b = (buses[i] == null);
                    System.out.println("TEST:" + b);
                    break;
                }
                singleBusList.add(singleBuses[i]);
            }
        } else if (flag == 1) {
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
            case R.id.action_about:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.action_delete:
                if (flag == 0) {
                    Snackbar.make(recyclerView, "正在删阝…诶…删不了", Snackbar.LENGTH_LONG).show();
                    break;
                }
                stops[mappedId] = new BusStop("", "");
                updateBusStop();
                Snackbar.make(recyclerView, "删除成功，重新打开生效", Snackbar.LENGTH_SHORT).show();
                addMenuItem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUpdate() {
        new Thread(new Runnable() {
            String responseData = "";

            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://app.seanchao.xyz/app/version").build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    Log.d("Debug", "server Version:" + responseData);
                    String pattern = "[^\\d]";
                    responseData = responseData.replaceAll(pattern, "");
                    int version = Integer.parseInt(responseData);
                    int currentVersion = MainActivity.this.getPackageManager().getPackageInfo(MainActivity.this.getPackageName(), 0).versionCode;
                    Log.d("Debug", "本地Version:" + currentVersion);
                    if (version > currentVersion) {
                        Snackbar.make(recyclerView, "有更新！", Snackbar.LENGTH_LONG)
                                .setAction("我要更新", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("https://github.com/SeanChao/SH-Bus/releases"));
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    } else {
                        Snackbar.make(recyclerView, "暂无更新……", Snackbar.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void showRefresh() {
        //swipeRefresh.setProgressViewOffset(false, 0, 100);
        swipeRefresh.setRefreshing(true);
    }

    private void hideRefresh() {
        swipeRefresh.setRefreshing(false);
    }

    public void save(String inputText, String file) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            //PRIVATE覆盖 APPEDN增加
            out = openFileOutput(file, Context.MODE_PRIVATE);
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

    public void addMenuItem() {
        Log.d("debug", "addMenuItem()执行");
        stops = JsonProcess.fromJson(load(file));
        Menu menu = navView.getMenu();
        for (int i = 0; i < stops.length; i++) {
            if (!stops[i].getName().equals("")) {
                if (i == 0) {
                    menu.findItem(R.id.bus_stop_primary).setTitle(stops[i].getName());
                } else {
                    if (menu.findItem(i) != null) {
                        menu.removeItem(i);
                    }
                    menu.add(Menu.NONE, i, Menu.NONE, stops[i].getName()).setIcon(R.drawable.ic_directions_bus_24dp).setCheckable(true);
                    menu.setGroupCheckable(R.id.group_bus_stop, true, true);
                }
            }
        }
    }

    @Override
    protected void onRestart() {
        Log.d("debug", "onRestart()执行");
        super.onRestart();
        addMenuItem();
        refreshBuses();
    }

    public void setupRecyclerView() {
        if (flag == 0) {
            recyclerView.setLongPressDragEnabled(true); // 拖拽排序
        } else {
            recyclerView.setLongPressDragEnabled(false);
        }
        //recyclerView.setItemViewSwipeEnabled(true); // 侧滑删除

        OnItemMoveListener mItemMoveListener = new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                int fromPosition = srcHolder.getAdapterPosition();
                int toPosition = targetHolder.getAdapterPosition();
                // Item被拖拽时，交换数据，并更新adapter。
                if (flag == 0) {
                    Collections.swap(singleBusList, fromPosition, toPosition);
                    SingleBusStop tempSingleBus = singleBusStops[toPosition];
                    singleBusStops[toPosition] = singleBusStops[fromPosition];
                    singleBusStops[fromPosition] = tempSingleBus;
                    save(JsonProcess.toJsonSingle(singleBusStops), favourite_file);
                    singleAdapter.notifyItemMoved(fromPosition, toPosition);
                } else if (flag == 1) {
                    Collections.swap(busList, fromPosition, toPosition);
                    adapter.notifyItemMoved(fromPosition, toPosition);
                    Snackbar.make(recyclerView, "此页面不支持持久化拖动…", Snackbar.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
                final int position = srcHolder.getAdapterPosition();
                // Item被侧滑删除时，删除数据，并更新adapter。
                Snackbar.make(findViewById(R.id.recycler_view), "已删除" + busList.get(position).getName(), Snackbar.LENGTH_LONG)
                        .show();
                busList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        };

        recyclerView.setOnItemMoveListener(mItemMoveListener);// 监听拖拽，更新UI。

        //设置菜单
        SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int viewType) {
                SwipeMenuItem starItem = new SwipeMenuItem(MainActivity.this)
                        .setImage(R.drawable.ic_star_yellow_24dp)
                        .setHeight(height).setWidth(200);
                SwipeMenuItem deleteItem = new SwipeMenuItem(MainActivity.this)
                        .setImage(R.drawable.ic_delete_24dp)
                        .setHeight(height).setWidth(120);
                rightMenu.addMenuItem(starItem); // 在Item右侧添加一个菜单。
                //rightMenu.addMenuItem(deleteItem); // 在Item侧添加一个菜单。
            }
        };

        // 设置监听器。
        recyclerView.setSwipeMenuCreator(mSwipeMenuCreator);

        SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
                menuBridge.closeMenu();

                int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
                int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
                int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
                System.out.println("click" + adapterPosition);
                switch (menuPosition) {
                    case 0: //收藏
                        if (flag == 0) { //执行取消收藏
                            Snackbar.make(recyclerView, "已取消收藏" + singleBusStops[adapterPosition].getBus(), Snackbar.LENGTH_SHORT).show();
                            singleBusStops[adapterPosition] = new SingleBusStop("", "", "");
                            updateSingleBuses();
                            initFavFile();
                            //Toast.makeText(MainActivity.this,"已取消收藏"+singleBusStops[adapterPosition].getBus(),Toast.LENGTH_SHORT).show();
                        } else if (flag == 1) {
                            Log.d("Debug", "开始循环检查");
                            SingleBusStop curSingleBus = new SingleBusStop(buses[adapterPosition].getName(), busStopName, busId);
                            for (int i = 0; i < singleBusStops.length; i++) {
                                Log.d("Debug", "循环变量为" + i);
                                String name = singleBusStops[i].getName();
                                if (singleBusStops[i].equal(curSingleBus)) {
                                    Snackbar.make(recyclerView, "已经收藏过了哦", Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                                if (name.equals("")) {
                                    Log.d("Debug", "getName:线路名" + buses[adapterPosition].getName());
                                    singleBusStops[i] = curSingleBus;
                                    Snackbar.make(recyclerView, "把" + buses[adapterPosition].getName() + "收藏起来啦 (=´ω｀=)", Snackbar.LENGTH_LONG).show();
                                    break;
                                }
                            }
                            updateSingleBuses();
                        }
                }
            }
        };

        recyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);
    }

    public void initFavFile() {
        checkInternet();
        if (!isConnectToNetwork) {
            toastNoNetwork();
            return;
        }
        showRefresh();
        flag = 0;
        singleBusStops = JsonProcess.fromJsonSingle(load(favourite_file));//要加载的有哪些车，不包含所需要呈现的信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlBase = "http://webapp.shbustong.com:56008/MobileWeb/ForecastChange.aspx?stopid=bsq";
                for (int i = 0; i < 32; i++) {
                    if (!singleBusStops[i].getId().equals("")) {
                        url = urlBase + singleBusStops[i].getId();
                        singleBuses[i] = BusInfoProcess.getSingleBusByUrl(url, singleBusStops[i].getBus());
                    } else {
                        singleBuses[i] = new SingleBus("", "", "", "", "");
                    }
                    System.out.println("id:" + url + "singlebus[" + i + "]" + singleBuses[i].toString());
                    Log.d("Debug", "生成对象结束");
                }
                showResponse();
            }
        }).start();

    }

    public void updateSingleBuses() {
        Log.d("Debug", "updateSingleBuses()被执行了");
        int spaceIndex;
        for (int i = 0; i < 32; i++) {
            if (singleBusStops[i].getName().equals("")) {
                spaceIndex = i;
                for (int j = spaceIndex; j < 32 - 1; j++) {
                    singleBusStops[j] = singleBusStops[j + 1];
                }
            }
        }
        save(JsonProcess.toJsonSingle(singleBusStops), favourite_file);
    }

    public void updateBusStop() {
        Log.d("Debug", "updateBusStop()被执行了");
        int spaceIndex;
        for (int i = 0; i < 32; i++) {
            if (stops[i].getName().equals("")) {
                spaceIndex = i;
                for (int j = spaceIndex; j < 32 - 1; j++) {
                    stops[j] = stops[j + 1];
                }
            }
        }
        save(JsonProcess.toJson(stops), file);
    }

    //检测当前的网络状态

    //API版本23以下时调用此方法进行检测
    //因为API23后getNetworkInfo(int networkType)方法被弃用
    public void checkState_21() {
        //步骤1：通过Context.getSystemService(Context.CONNECTIVITY_SERVICE)获得ConnectivityManager对象
        ConnectivityManager connMgr = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        //步骤2：获取ConnectivityManager对象对应的NetworkInfo对象
        //NetworkInfo对象包含网络连接的所有信息
        //步骤3：根据需要取出网络连接信息
        //获取WIFI连接的信息
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();

        //获取移动数据连接的信息
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();
//        Toast.makeText(MainActivity.this, "Mobile:" + isMobileConn, Toast.LENGTH_SHORT).show();
//        Toast.makeText(MainActivity.this, "WiFi:" + isWifiConn, Toast.LENGTH_SHORT).show();
        Log.d("info", "api<21 网络状况:" + "Mobile:" + isMobileConn + "WiFi:" + isWifiConn);
        if (isMobileConn || isWifiConn) {
            isConnectToNetwork = true;
        }
    }

    //API版本23及以上时调用此方法进行网络的检测
    //步骤非常类似
    @TargetApi(21)
    public void checkState_21orNew() {
        //获得ConnectivityManager对象
        ConnectivityManager connMgr = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        //获取所有网络连接的信息
        Network[] networks = connMgr.getAllNetworks();
        //用于存放网络连接信息
        StringBuilder sb = new StringBuilder();
        //通过循环将网络信息逐个取出来
        for (int i = 0; i < networks.length; i++) {
            //获取ConnectivityManager对象对应的NetworkInfo对象
            NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
            sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected() + " ");
            if (networkInfo.getTypeName().equals("WIFI") || networkInfo.getTypeName().equals("MOBILE")) {
                if (networkInfo.isConnected()) {
                    isConnectToNetwork = true;
                }
            }
        }
//        Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
        Log.d("info", "api>=21 网络状况:" + sb.toString());
    }

    public void checkInternet() {
        isConnectToNetwork = false;
        int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 21) {
            checkState_21orNew();
        } else {
            checkState_21();
        }
        Log.d("info", "有网络连接:" + isConnectToNetwork);
    }

    public void toastNoNetwork() {
//        Toast.makeText(MainActivity.this, "连不上网了……", Toast.LENGTH_LONG).show();
        Snackbar.make(recyclerView, "连不上网了……", Snackbar.LENGTH_LONG)
                .setAction("重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkInternet();
                        if (isConnectToNetwork) {
                            Toast.makeText(MainActivity.this, "连上网啦~", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }
}
