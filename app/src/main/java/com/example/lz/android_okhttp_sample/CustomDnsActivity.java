package com.example.lz.android_okhttp_sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CustomDnsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_dns);
    }

    public void onGetClick(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = executeGet("https://www.baidu.com");
                    Log.e(SimpleDemoActivity.class.getSimpleName(), response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String executeGet(String url) throws IOException {
        String result = null;
        OkHttpClient client = new OkHttpClient().newBuilder().dns(new Dns() {
            @Override
            public List<InetAddress> lookup(String hostname) throws UnknownHostException {
                InetAddress[] inetAddresses = DNSResolover.getInstance().getAllByName();
                InetAddress[] addresses = INetAddressUtil.filter(inetAddresses);
                if (addresses != null && addresses.length > 0) {
                    return Arrays.asList(addresses);
                } else {
                    List<InetAddress> adds = Dns.SYSTEM.lookup(hostname);
                    return adds;
                }
            }
        }).build();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        result = response.body().string();
        return result;
    }
}
