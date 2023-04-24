
package com.eygsl.cbs.referencemsal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class WebViewBrowser extends AppCompatActivity {

    WebView mywebView;
    SharedPreferences sharedPreferences;
    private String token = "";
    private String internalWebLinkToken = "";
    private boolean isInternalLink = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_browser);

        sharedPreferences = getSharedPreferences("STORAGE", Context.MODE_PRIVATE);
        internalWebLinkToken = sharedPreferences.getString("INTERNALWEBLINKTOKEN","{}");
        token = sharedPreferences.getString("GRAPHACCESSTOKEN","{}");

        Intent intent = getIntent();
        String url = intent.getExtras().getString("link");
        isInternalLink = intent.getExtras().getBoolean("isInternalLink");


        Log.d("URL", url);

        mywebView = (WebView) findViewById(R.id.webview);
        mywebView.getSettings().setJavaScriptEnabled(true);

        if (isInternalLink) {
            //put all headers in this header map
            Log.d("Internal link", url);
            Log.d("INTERNAL_TOKEn", internalWebLinkToken);
            Log.d("True>>>>>>>>", String.valueOf(isInternalLink));
            HashMap<String, String> headerMap = new HashMap<>();
            headerMap.put("Authorization", "Bearer " + internalWebLinkToken);
            mywebView.loadUrl(url, headerMap);
        } else {
            Log.d("External link", url);
            HashMap<String, String> headerMap = new HashMap<>();
            headerMap.put("Authorization", "Bearer " + token);
            mywebView.loadUrl(url, headerMap);
        }

        mywebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch (keyCode){
                case KeyEvent.KEYCODE_BACK:
                    if(mywebView.canGoBack()){
                        mywebView.goBack();
                    }
                    else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}