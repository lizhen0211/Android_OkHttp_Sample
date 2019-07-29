package com.example.lz.android_okhttp_sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSimpleDemoClick(View view) {
        Intent intent = new Intent(MainActivity.this, SimpleDemoActivity.class);
        startActivity(intent);
    }

    public void onRecipesClick(View view) {
        Intent intent = new Intent(MainActivity.this, RecipesActivity.class);
        startActivity(intent);
    }

    public void onInterceptorsClick(View view) {
        Intent intent = new Intent(MainActivity.this, InterceptorsActivity.class);
        startActivity(intent);
    }
}
