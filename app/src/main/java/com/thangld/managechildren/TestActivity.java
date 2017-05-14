package com.thangld.managechildren;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.thangld.managechildren.cloud.HttpConnection;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static com.thangld.managechildren.cloud.HttpConnection.exePutFileConnection;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void upload(View view) throws MalformedURLException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL("http://192.168.1.15:8080/api/v1/save_file");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("data", "thangld");
                String respond = exePutFileConnection(url, Environment.getExternalStorageDirectory() + "/abc.JPG", hashMap);
                Log.d("mc_log", respond);
            }
        }).start();
    }

    public void download(View view) throws MalformedURLException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = null;
                url = new String("http://192.168.1.15:8080/api/v1/send_file");
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("data", "thangld");
                HttpConnection.downloadFile(url, new JSONObject(),
                        Environment.getExternalStorageDirectory().getAbsolutePath(), "abc_file_name.png");

            }
        }).start();

    }

}
