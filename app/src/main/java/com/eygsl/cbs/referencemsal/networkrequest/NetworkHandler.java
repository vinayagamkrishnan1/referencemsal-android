package com.eygsl.cbs.referencemsal.networkrequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.eygsl.cbs.referencemsal.helpers.HttpsTrustManager;
import com.eygsl.cbs.referencemsal.utils.Constants;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkHandler extends AsyncTask<String, String, String> {

    HttpURLConnection urlConnection;
    private String bufferData = "";

//    private String token = HomeFragment.accessToken;

    private String token = "";
    private Context mContext;
    public NetworkHandler(Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // display a progress dialog for good user experiance
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("STORAGE", mContext.MODE_PRIVATE);
        token = sharedPreferences.getString("GRAPHACCESSTOKEN","");
        Log.d("ASYNCTASK_TOKEN", token);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            HttpsTrustManager.allowAllSSL();
            Log.d("TRY CATCH::::::", "Executing try catch");
            URL url = new URL(Constants.SHAREPOINT_DOCUMENTS_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer "+ token);
            InputStream in = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while (line != null) {
                line = reader.readLine();
                bufferData = bufferData + line;
            }
            Log.d("BUFFERED_DATA:", bufferData.toString());
        } catch( Exception e) {
            Log.d("ERROR WHILE GET DATA::", e.toString());
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return bufferData;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

}
