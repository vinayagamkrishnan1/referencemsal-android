package com.eygsl.cbs.referencemsal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SharePointItemDetail extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_point_item_detail);

        Intent intent = getIntent();
        String appName = intent.getStringExtra("appName");

        String appDevFramework = intent.getStringExtra("appDevFramework");
        String mobilePlatforms = intent.getStringExtra("mobilePlatforms");
        String serviceLine = intent.getStringExtra("serviceLine");
        String homePageURL = intent.getStringExtra("homePageURL");
        String environments = intent.getStringExtra("Environments");
        String appDescription = intent.getStringExtra("appDescription");


        ((TextView) findViewById(R.id.appName)).setText(appName);
        ((TextView) findViewById(R.id.appDevFramework)).setText(appDevFramework);
        ((TextView) findViewById(R.id.mobilePlatforms)).setText(mobilePlatforms);
        ((TextView) findViewById(R.id.serviceLine)).setText(serviceLine);
        ((TextView) findViewById(R.id.homePageURL)).setText(homePageURL);
        ((TextView) findViewById(R.id.environments)).setText(environments);
        ((TextView) findViewById(R.id.appDesc)).setText(appDescription);

//    Log.d(TAG, "appDevFramework - "+appDevFramework);
//    Log.d(TAG, "mobilePlatforms - "+mobilePlatforms);
//    Log.d(TAG, "serviceLine - "+serviceLine);
//    Log.d(TAG, "homePageURL - "+homePageURL);
//    Log.d(TAG, "environments - "+environments);
//    Log.d(TAG, "appDescription - "+appDescription);

        setTitle(appName);
    }
}
