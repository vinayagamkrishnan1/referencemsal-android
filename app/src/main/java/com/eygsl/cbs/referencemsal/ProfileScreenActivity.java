
package com.eygsl.cbs.referencemsal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eygsl.cbs.referencemsal.authentication.MSGraphRequestWrapper;
import com.eygsl.cbs.referencemsal.utils.Constants;

public class ProfileScreenActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    final String defaultGraphResourceUrl = Constants.MS_GRAPH_ROOT + Constants.MS_GRAPH_ENDPOINT;
    JSONObject userData;
    public String token = null;

    TextView profile_displayName, profile_mobileNumber, profile_jobTitle, profile_mail, profile_employeeID, profile_givenName,
            profile_officeLocation, profile_businessPhones, profile_preferredLanguage, profile_context, profile_userPrincipalName, profile_surname;

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
        profile_preferredLanguage = (TextView) findViewById(R.id.profile_preferredLanguage);
        profile_context = (TextView) findViewById(R.id.profile_context);
        profile_userPrincipalName = (TextView) findViewById(R.id.profile_userPrincipalName);
        profile_surname = (TextView) findViewById(R.id.profile_surname);

        if (token != null) {
            Log.d("TOKEN_NOT_AVAILABLE", token);
            callGraphAPI(token);
        } else {
            super.onBackPressed();
            Log.d("TOKEN_NOT_AVAILABLE", "EMPTY TOKEN.");
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
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERR_LOAD_ACCOUNT_MSG", error.getMessage());
                        Log.d("ERR_LOAD_ACCOUNT_ERROR", error.toString());
                        // displayError(error);
                    }
                });
    }

    private void initializeViews(JSONObject profileData) {
        try {
            profile_displayName.setText( getString(R.string.profile_displayName) + profileData.get("displayName"));
            profile_mobileNumber.setText( getString(R.string.profile_mobileNumber) +  profileData.get("mobilePhone"));
            profile_jobTitle.setText( getString(R.string.profile_jobTitle) +  profileData.get("jobTitle"));
            profile_mail.setText( getString(R.string.profile_mail) +  profileData.get("mail"));
            profile_employeeID.setText( getString(R.string.profile_employeeID) +  profileData.get("id"));
            profile_givenName.setText( getString(R.string.profile_givenName) +  profileData.get("givenName"));
            profile_officeLocation.setText( getString(R.string.profile_officeLocation) +  profileData.get("officeLocation"));
            profile_businessPhones.setText( getString(R.string.profile_businessPhones) +  profileData.get("businessPhones"));
            profile_preferredLanguage.setText( getString(R.string.profile_preferredLanguage) +  profileData.get("preferredLanguage"));
            profile_context.setText( getString(R.string.profile_context) );
            profile_userPrincipalName.setText( getString(R.string.profile_userPrincipalName) +profileData.get("userPrincipalName"));
            profile_surname.setText( getString(R.string.profile_surname) +  profileData.get("surname"));

            if(profileData.get("mobilePhone").equals(null)) {
                profile_mobileNumber.setText( getString(R.string.profile_mobileNumber) +  " ");
            }

            if(profileData.get("preferredLanguage").equals("EN")) {
                profile_preferredLanguage.setText( getString(R.string.profile_preferredLanguage) +  "ENGLISH");
            }

        } catch (JSONException e) {
            Log.d("ERROR_WHILE_INIT_VIEWS", "Exception from profileScreen - "+e);
            e.printStackTrace();

        }
    }
}
