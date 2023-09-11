package com.eygsl.cbs.referencemsal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eygsl.cbs.referencemsal.adapters.SharepointListAdapter;
import com.eygsl.cbs.referencemsal.helpers.HttpsTrustManager;
import com.eygsl.cbs.referencemsal.models.SharepointListItemModel;
import com.eygsl.cbs.referencemsal.utils.Constants;
import com.microsoft.identity.client.AuthenticationResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SharepointList extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AuthenticationResult mAuthResult;

    private ListView listView;
    ArrayList<SharepointListItemModel> tennisModelArrayList;
    private SharepointListAdapter tennisAdapter;
    private final int jsoncode = 1;
    private int counterPending = 0;
    private int counterApproved = 0;

    String current = "";
    private boolean AppType;
    private String ModerationStatus;

    SharedPreferences sharedPreferences;
    String token = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharepoint_list);
        setTitle("Sharepoint");
        sharedPreferences = getSharedPreferences("STORAGE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("GRAPHACCESSTOKEN","");

        listView = findViewById(R.id.lv);
        listView.setVisibility(View.VISIBLE);
        LayoutInflater inflater = getLayoutInflater();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(), "Selected project - "+tennisModelArrayList.get(position).getName(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SharepointList.this, SharePointItemDetail.class);
                intent.putExtra("appName", tennisModelArrayList.get(position).getName());
                intent.putExtra("appDevFramework", tennisModelArrayList.get(position).getCity());
                intent.putExtra("mobilePlatforms", tennisModelArrayList.get(position).getMobilePlatforms());
                intent.putExtra("serviceLine", tennisModelArrayList.get(position).getserviceLine());
                intent.putExtra("homePageURL", tennisModelArrayList.get(position).gethomePageURL());
                intent.putExtra("Environments", tennisModelArrayList.get(position).getenvironments());
                intent.putExtra("appDescription", tennisModelArrayList.get(position).getAppDescription());

                Map<String, String> properties = new HashMap<>();
                properties.put("Title ", tennisModelArrayList.get(position).getName());

                startActivity(intent);
            }
        });

        callSharepointListAPI();
    }

    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case jsoncode:
                if (isSuccess(response)) {
                    tennisModelArrayList = getInfo(response);
                    tennisAdapter = new SharepointListAdapter(this,tennisModelArrayList);
                    listView.setAdapter(tennisAdapter);
                    findViewById(R.id.lv).setVisibility(View.VISIBLE);
                } else {
                    Log.d("after Success TRUE", "ERROR from onTaskConmplted");
                }
        }
    }

    public ArrayList<SharepointListItemModel> getInfo(String response) {
        ArrayList<SharepointListItemModel> tennisModelArrayList = new ArrayList<>();
        ArrayList<SharepointListItemModel> tennisModelArrayList1 = new ArrayList<>();
        ArrayList<SharepointListItemModel> tennisModelArrayList2 = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dataArray = jsonObject.getJSONArray("items");
            for (int i = 0; i < dataArray.length(); i++) {
                SharepointListItemModel playersModel = new SharepointListItemModel();
                SharepointListItemModel playersModel1 = new SharepointListItemModel();
                JSONObject dataobj = dataArray.getJSONObject(i);
                if(Integer.parseInt(dataobj.getJSONObject("fields").getString("_ModerationStatus")) == 2) {
                    counterPending = counterPending + 1;
                    playersModel1.setName(dataobj.getJSONObject("fields").getString("Title"));
                    playersModel1.setMobilePlatfroms(dataobj.getJSONObject("fields").getString("MobilePlatforms"));
                    playersModel1.setserviceLine(dataobj.getJSONObject("fields").getString("ServiceLine"));
                    playersModel1.setAppDescription(dataobj.getJSONObject("fields").getString("AppName"));
                    if(dataobj.getJSONObject("fields").has("AppType")) {
                        playersModel1.setCity(dataobj.getJSONObject("fields").getString("AppType"));
                    } else {
                        playersModel1.setCity("No application type specified");
                    }

                    if(dataobj.getJSONObject("fields").has("HomePageURL")) {
                        playersModel1.sethomePageURL(dataobj.getJSONObject("fields").getString("HomePageURL"));
                    } else {
                        playersModel1.sethomePageURL("No home page URL specified");
                    }

                    if(dataobj.getJSONObject("fields").has("Environments")) {
                        playersModel1.setenvironments(dataobj.getJSONObject("fields").getString("Environments"));
                    } else {
                        playersModel1.setenvironments("No environment specified");
                    }
                    tennisModelArrayList1.add(playersModel1);
                    ((TextView) findViewById(R.id.tv)).setText("Request Pending (" +counterPending+ ")");

                } else {

                    counterApproved = counterApproved + 1;
                    if(dataobj.getJSONObject("fields").has("AppType")) {
                        playersModel.setCity(dataobj.getJSONObject("fields").getString("AppType"));
                    } else {
                        playersModel.setCity("No application type specified");
                    }
                    if(dataobj.getJSONObject("fields").has("HomePageURL")) {
                        playersModel.sethomePageURL(dataobj.getJSONObject("fields").getString("HomePageURL"));
                    } else {
                        playersModel.sethomePageURL("No home page URL specified");
                    }
                    if(dataobj.getJSONObject("fields").has("Environments")) {
                        playersModel.setenvironments(dataobj.getJSONObject("fields").getString("Environments"));
                    } else {
                        playersModel.setenvironments("No environment specified");
                    }
                    playersModel.setName(dataobj.getJSONObject("fields").getString("Title"));
                    playersModel.setMobilePlatfroms(dataobj.getJSONObject("fields").getString("MobilePlatforms"));
                    playersModel.setserviceLine(dataobj.getJSONObject("fields").getString("ServiceLine"));
                    playersModel.setAppDescription(dataobj.getJSONObject("fields").getString("AppName"));

                    tennisModelArrayList2.add(playersModel);
                    ((TextView) findViewById(R.id.tv2)).setText("Request Approved (" +counterApproved+ ")");
                }
            }
            tennisModelArrayList.addAll(tennisModelArrayList1);
            tennisModelArrayList.addAll(tennisModelArrayList2);

        } catch (JSONException ex) {
            Log.d(TAG, "Sharepointlist ERRRORRRRR" +ex.toString());
            ex.printStackTrace();
        }
        return tennisModelArrayList;
    }

    private void callSharepointListAPI() {

        HttpsTrustManager.allowAllSSL();
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject parameters = new JSONObject();

        try {
            parameters.put("key", "value");
        } catch (Exception e) {
            Log.d(TAG, "Sharepointlist Failed to put parameters: " + e.toString());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constants.SHAREPOINT_LIST_URL,
                parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onTaskCompleted(response.toString(), 1);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Sharepointlist Error: " + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public boolean isSuccess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.length() > 0) {
                return true;
            } else {
                return false;
            }

        } catch (JSONException e) {
            Log.d("from Success catch", e.toString());
            e.printStackTrace();
        }
        return false;
    }
}
