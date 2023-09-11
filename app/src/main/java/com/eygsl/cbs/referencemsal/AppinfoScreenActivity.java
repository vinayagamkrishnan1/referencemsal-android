package com.eygsl.cbs.referencemsal;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class AppinfoScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appinfo_screen);
        setTitle("Application Info");
        TextView textViewAppVersion = (TextView) findViewById(R.id.textview_applicationversion);
        TextView textViewADALVersion = (TextView) findViewById(R.id.textview_adalversion);
        TextView textViewIntuneVersion = (TextView) findViewById(R.id.textview_intunesdkversion);
        TextView textViewAppcenterVersion = (TextView) findViewById(R.id.textview_appcenterversion);

        textViewAppVersion.setText("Application Version: " + BuildConfig.VERSION_NAME );
        textViewADALVersion.setText("MSAL Version: " + com.microsoft.identity.msal.BuildConfig.VERSION_NAME);
        // textViewIntuneVersion.setText("Intune SDK Version: " + com.microsoft.intune.mam.BuildConfig.VERSION_NAME);
        // textViewAppcenterVersion.setText("AppCenter Version: " + com.microsoft.appcenter.BuildConfig.VERSION_NAME);

        Map<String, String> properties = new HashMap<>();
        properties.put("App version", BuildConfig.VERSION_NAME);
        properties.put("ADAL Version", com.microsoft.identity.msal.BuildConfig.VERSION_NAME);
        // properties.put("Intune SDK Version", com.microsoft.intune.mam.BuildConfig.VERSION_NAME);
        //properties.put("AppCenter Version", com.microsoft.appcenter.BuildConfig.VERSION_NAME);

        //Analytics.trackEvent("App Info screen", properties);

    }

}
