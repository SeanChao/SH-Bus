package xyz.seanchao.shbus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AboutActivity extends AppCompatActivity {

    private TextView versionText;
    private LinearLayout linearLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("关于");
        linearLayout = findViewById(R.id.about_linearlayout);
        versionText = findViewById(R.id.textview_version);
        versionText.setText(getCurrentVersionName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_money:
                setContentView(R.layout.activity_about_money);
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkUpdate(View v) {
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
                    int currentVersion = AboutActivity.this.getPackageManager().getPackageInfo(AboutActivity.this.getPackageName(), 0).versionCode;
                    Log.d("Debug", "本地Version:" + currentVersion);
                    if (version > currentVersion) {
                        Snackbar.make(linearLayout, "有更新！", Snackbar.LENGTH_LONG)
                                .setAction("我要更新", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("https://www.coolapk.com/apk/177615"));
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    } else {
                        Snackbar.make(linearLayout, "暂无更新……", Snackbar.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public String getCurrentVersionName() {
        String versionName = "";
        try {
            versionName = AboutActivity.this.getPackageManager().getPackageInfo(AboutActivity.this.getPackageName(), 0).versionName;
        } catch (Exception e) {

        }
        return versionName;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.img_alipay) {
            saveBitmap(v, "/storage/emulated/0/Pictures/Bus/alipay.jpg");
        } else if (v.getId() == R.id.img_qq) {
            saveBitmap(v, "/storage/emulated/0/Pictures/Bus/wx.png");
        } else if (v.getId() == R.id.img_qq) {
            saveBitmap(v, "/storage/emulated/0/Pictures/Bus/qq.png");
        }
    }

    public static void saveBitmap(View view, String filePath) {

        // 创建对应大小的bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        //存储
        FileOutputStream outStream = null;
        File file = new File(filePath);
        if (file.isDirectory()) {//如果是目录不允许保存
            return;
        }
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bitmap.recycle();
                if (outStream != null) {
                    outStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}

