
package com.eygsl.cbs.referencemsal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.eygsl.cbs.referencemsal.authentication.MSGraphRequestWrapper;
import com.eygsl.cbs.referencemsal.utils.Constants;

public class ProfileScreenActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    final String defaultGraphResourceUrl = Constants.MS_GRAPH_ROOT + Constants.MS_GRAPH_ENDPOINT;
    JSONObject userData;
    public String token = null;


    TextView profile_displayName, profile_mobileNumber, profile_jobTitle, profile_mail, profile_employeeID, error_message, profile_givenName,
            profile_officeLocation, profile_businessPhones, profile_userPrincipalName;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        setTitle("Profile Information ");

        sharedPreferences = getSharedPreferences("STORAGE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("GRAPHACCESSTOKEN",null);

        profile_displayName = (TextView) findViewById(R.id.profile_displayName);
        profile_mobileNumber = (TextView) findViewById(R.id.profile_mobileNumber);
        profile_jobTitle = (TextView) findViewById(R.id.profile_jobTitle);
        profile_mail = (TextView) findViewById(R.id.profile_mail);
        profile_employeeID = (TextView) findViewById(R.id.profile_employeeID);
        profile_givenName = (TextView) findViewById(R.id.profile_givenName);
        profile_officeLocation = (TextView) findViewById(R.id.profile_officeLocation);
        profile_businessPhones = (TextView) findViewById(R.id.profile_businessPhones);
        profile_userPrincipalName = (TextView) findViewById(R.id.profile_userPrincipalName);
        error_message = (TextView) findViewById(R.id.txt_view_message);

        if (token != null) {
            Log.d("TOKEN_NOT_AVAILABLE", token);
            callGraphAPI(token);
        } else {
            super.onBackPressed();
            Log.d("TOKEN_NOT_AVAILABLE", "EMPTY TOKEN.");
            error_message.setVisibility(View.VISIBLE);
            error_message.setText("Token not available.");
        }

    }

    private void callGraphAPI(String accessToken) {
        MSGraphRequestWrapper.callGraphAPIUsingVolley(
                getApplicationContext(),
                defaultGraphResourceUrl,
                accessToken,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("GRAPH_API_RESPONSE", response.toString());
                        try {
                            userData = new JSONObject(response.toString());
                            initializeViews(userData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            error_message.setVisibility(View.VISIBLE);
                            error_message.setText("Error While calling Graph API.");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.d("ERR_LOAD_ACCOUNT_MSG", error.getMessage());
//                        Log.d("ERR_LOAD_ACCOUNT_ERROR", error.toString());
                        error_message.setVisibility(View.VISIBLE);
                        try {
                            if (error instanceof TimeoutError) {
                                error_message.setText(error.getMessage());
                            } else if(error instanceof NoConnectionError){
                                error_message.setText(error.getMessage());
                            } else if (error instanceof AuthFailureError) {
                                error_message.setText(error.getMessage());
                            } else if (error instanceof ServerError) {
                                error_message.setText(error.getMessage());
                            } else if (error instanceof NetworkError) {
                                error_message.setText(error.getMessage());
                            } else if (error instanceof ParseError) {
                                error_message.setText(error.getMessage());
                            }else {
                                error_message.setText("Something went wrong.");
                            }
                        } catch (Exception e) {
                            error_message.setText(e.getMessage());
                        }
                    }
                });
    }

    private void initializeViews(JSONObject profileData) {
        try {
            if(profileData.get("displayName").equals(null)) {
                profile_displayName.setText("Null");
            } else {
                profile_displayName.setText(profileData.get("displayName").toString());
            }

            if(profileData.get("mobilePhone").equals(null)) {
                profile_mobileNumber.setText("Null");
            } else {
                profile_mobileNumber.setText(profileData.get("mobilePhone").toString());
            }

            if(profileData.get("jobTitle").equals(null)) {
                profile_jobTitle.setText("Null");
            } else {
                profile_jobTitle.setText(profileData.get("jobTitle").toString());
            }

            if(profileData.get("mail").equals(null)) {
                profile_mail.setText("Null");
            } else {
                profile_mail.setText(profileData.get("mail").toString());
            }

            if(profileData.get("id").equals(null)) {
                profile_employeeID.setText("Null");
            } else {
                profile_employeeID.setText(profileData.get("id").toString());
            }

            if(profileData.get("givenName").equals(null)) {
                profile_givenName.setText("Null");
            } else {
                profile_givenName.setText(profileData.get("givenName").toString());
            }

            if(profileData.get("officeLocation").equals(null)) {
                profile_officeLocation.setText("Null");
            } else {
                profile_officeLocation.setText(profileData.get("officeLocation").toString());
            }

            if(profileData.get("businessPhones").equals(null)) {
                profile_businessPhones.setText("Null");
            } else {
                profile_businessPhones.setText(profileData.get("businessPhones").toString());
            }

            if(profileData.get("userPrincipalName").equals(null)) {
                profile_userPrincipalName.setText("Null");
            } else {
                profile_userPrincipalName.setText(profileData.get("userPrincipalName").toString());
            }
        } catch (JSONException e) {
            Log.d("ERROR_WHILE_INIT_VIEWS", "Exception from profileScreen - "+e);
            e.printStackTrace();
            error_message.setVisibility(View.VISIBLE);
            error_message.setText(e.getMessage());
        }
    }
}
