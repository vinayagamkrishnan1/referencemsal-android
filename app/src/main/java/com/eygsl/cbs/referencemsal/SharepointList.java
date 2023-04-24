package com.eygsl.cbs.referencemsal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SharepointList extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String token = "";
    ArrayList<SharepointListItemModel> sharepointItemModelArrayList;
    SharepointListAdapter adapter;
    ListView sharepointListView;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharepoint_list);
        setTitle("Sharepoint");

        sharepointListView  = (ListView) findViewById(R.id.sharepoint_list);
        message  = (TextView) findViewById(R.id.txt_view_message);
        sharedPreferences = getSharedPreferences("STORAGE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("GRAPHACCESSTOKEN","");
        Log.d("TOKEN_TO_GET_SP_LIST", token);

        sharepointListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(), "Selected project - "+ sharepointItemModelArrayList.get(position).getName(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SharepointList.this, SharePointItemDetail.class);
                intent.putExtra("Title", sharepointItemModelArrayList.get(position).getName());
                intent.putExtra("Description", sharepointItemModelArrayList.get(position).getDescription());
                startActivity(intent);
//        intent.putExtra("mobilePlatforms", tennisModelArrayList.get(position).getMobilePlatforms());
//        intent.putExtra("serviceLine", tennisModelArrayList.get(position).getserviceLine());
//        intent.putExtra("homePageURL", tennisModelArrayList.get(position).gethomePageURL());
//        intent.putExtra("Environments", tennisModelArrayList.get(position).getenvironments());
//        intent.putExtra("appDescription", tennisModelArrayList.get(position).getAppDescription());
//        Map<String, String> properties = new HashMap<>();
//        properties.put("Title ", tennisModelArrayList.get(position).getName());
            }
        });
        callSharepointAPI();
    }

    public void onTaskCompleted(String response) {
        // Setup the data source
        ArrayList<SharepointListItemModel> sharepointItemModelArrayList = constructGraphAPIResponse(response);
        if (sharepointItemModelArrayList.size() <= 0) {
            message.setVisibility(View.VISIBLE);
        } else {
            message.setVisibility(View.GONE);
            // Instantiate the custom list adapter
            adapter = new SharepointListAdapter(this, sharepointItemModelArrayList);
            // Get the ListView and attach the adapter
            sharepointListView.setAdapter(adapter);
        }
    }

    public ArrayList<SharepointListItemModel> constructGraphAPIResponse(String response) {
        sharepointItemModelArrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    SharepointListItemModel sharepointItemModel = new SharepointListItemModel();
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    sharepointItemModel.setName(jsonObject1.getJSONObject("fields").getString("Title"));
                    sharepointItemModel.setDescription(jsonObject1.getJSONObject("fields").getString("AppDescription"));
                    sharepointItemModelArrayList.add(sharepointItemModel);
                }
            } else { }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JSON OBJ ERROR", e.toString());
        }
        return sharepointItemModelArrayList;
    }

    private void callSharepointAPI() {
        RequestQueue queue = Volley.newRequestQueue(this);
        final JSONObject parameters = new JSONObject();
        HttpsTrustManager.allowAllSSL();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constants.SHAREPOINT_LIST_URL,
                parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("SHARE_POINT_LIST_RES", response.toString());
                onTaskCompleted(response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                message.setVisibility(View.VISIBLE);
                Log.d("SHARE_POINT_LIST_ERROR", error.toString());
                // Log.d("SHARE_POINT_LIST_ERROR", error.getMessage());
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
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

}