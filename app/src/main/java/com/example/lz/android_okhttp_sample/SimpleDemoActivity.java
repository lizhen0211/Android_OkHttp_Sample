package com.example.lz.android_okhttp_sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SimpleDemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_demo);
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private String executePostJSON(String url, String json) throws IOException {
        String result = null;
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        result = response.body().string();
        return result;
    }

    //private static final MediaType FORM_URLENCODED = MediaType.parse("application/x-www-form-urlencoded");

    private String executePostForm(String url) throws IOException {
        String result = null;
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("", "")
                .add("", "")
                .add("", "")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        result = response.body().string();
        return result;
    }

    private String executeGet(String url) throws IOException {
        String result = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        result = response.body().string();
        return result;
    }

    String bowlingJson(String player1, String player2) {
        return "{'winCondition':'HIGH_SCORE',"
                + "'name':'Bowling',"
                + "'round':4,"
                + "'lastSaved':1367702411696,"
                + "'dateStarted':1367702378785,"
                + "'players':["
                + "{'name':'" + player1 + "','history':[10,8,6,7,8],'color':-13388315,'total':39},"
                + "{'name':'" + player2 + "','history':[6,10,5,10,10],'color':-48060,'total':41}"
                + "]}";
    }

    public void onSimpleGetClick(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = executeGet("https://raw.github.com/square/okhttp/master/README.md");
                    Log.e(SimpleDemoActivity.class.getSimpleName(), response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void onSimpleJSONPostClick(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json = bowlingJson("Jesse", "Jake");
                try {
                    String response = executePostJSON("http://www.roundsapp.com/post", json);
                    Log.e(SimpleDemoActivity.class.getSimpleName(), response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void onSimpleFormPostClick(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = executePostForm("");
                    Log.e(SimpleDemoActivity.class.getSimpleName(), response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
