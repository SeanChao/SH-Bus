package xyz.seanchao.shbus;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by SeanC on 2018/2/16.
 */

public class InstallApk extends Application {

    String file = "nav_items.json";
    String favFile = "favourite.json";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Debug", "Application onCreate() Run!!");
        String fromFile = load(file);
        String str = "[{\"name\":\"\",\"id\":\"\"}]";
        boolean b = fromFile.equals("");
        if (b) {
            System.out.println("Application on create--fileext" + b);
            save(str, file);
        }

        String fromFile2 = load(favFile);
        boolean b2 = fromFile2.equals("");

        String str2 = "[{\"bus\":\"\",\"name\":\"\",\"id\":\"\"}]";
        if (b2) {
            System.out.println("Application on create--fileext" + b2);
            save(str2, favFile);
        }

    }

    public void save(String inputText, String file) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            //PRIVATE覆盖 APPEDN增加
            out = openFileOutput(file, Context.MODE_APPEND);
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
}
