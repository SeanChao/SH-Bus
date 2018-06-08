package xyz.seanchao.shbus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

}
