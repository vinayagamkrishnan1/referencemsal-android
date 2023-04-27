// Copyright (c) Microsoft Corporation.
// All rights reserved.
//
// This code is licensed under the MIT License.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package com.eygsl.cbs.referencemsal;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eygsl.cbs.referencemsal.authentication.AppAccount;
import com.eygsl.cbs.referencemsal.authentication.AppSettings;
import com.eygsl.cbs.referencemsal.authentication.MSALUtil;
import com.eygsl.cbs.referencemsal.authentication.MSGraphRequestWrapper;
import com.eygsl.cbs.referencemsal.fragments.B2CModeFragment;
import com.eygsl.cbs.referencemsal.fragments.ErrorFragment;
import com.eygsl.cbs.referencemsal.fragments.MultipleAccountModeFragment;
import com.eygsl.cbs.referencemsal.fragments.OnFragmentInteractionListener;
import com.eygsl.cbs.referencemsal.fragments.SingleAccountModeFragment;
import com.eygsl.cbs.referencemsal.helpers.HttpsTrustManager;
import com.eygsl.cbs.referencemsal.utils.Constants;
import com.google.android.material.navigation.NavigationView;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalIntuneAppProtectionPolicyRequiredException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import com.microsoft.identity.client.exception.MsalUserCancelException;
import com.microsoft.intune.mam.client.app.MAMComponents;
import com.microsoft.intune.mam.policy.MAMEnrollmentManager;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnFragmentInteractionListener {

    enum AppFragment {
        SingleAccount,
        MultipleAccount,
        B2C,
        ErrorFragment
    }
    private AppFragment mCurrentFragment;
    private ConstraintLayout mContentMain;
    DrawerLayout drawer;
    private ISingleAccountPublicClientApplication mSingleAccountApp;
    final String defaultGraphResourceUrl = Constants.MS_GRAPH_ROOT + Constants.MS_GRAPH_ENDPOINT;

    // private AppAccount mUserAccount;
    private AppAccount mUserAccount;
    IAccount account;
    public MAMEnrollmentManager mEnrollmentManager;
    SharedPreferences sharedPreferences;

    private static final int REQUEST_CAPTURE_IMAGE = 1;
    public static String pictureFilePath;
    public static File pictureFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCenter.start(getApplication(), Constants.APPCENTER_APP_SECRET,
                Analytics.class, Crashes.class);

        mEnrollmentManager = MAMComponents.get(MAMEnrollmentManager.class);

        mUserAccount = AppSettings.getAccount(getApplicationContext());
        sharedPreferences = getApplicationContext().getSharedPreferences("STORAGE", Context.MODE_PRIVATE);

        mContentMain = findViewById(R.id.content_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //Set default fragment
        navigationView.setCheckedItem(R.id.nav_single_account);

        if (mUserAccount == null) {
            signWithMSAL();
        } else {
            displayHomePage();
        }
    }

    private void createAlertForTokenExpireAndSignout() {
        new AlertDialog.Builder(getApplicationContext())
                .setTitle("Message")
                .setCancelable(false)
                .setMessage("Your session got expired, Please login again to continue.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private boolean isTokenExpired() {
        long expiredate = sharedPreferences.getLong("GRAPHTOKENEXPIREON", 0);
        if (expiredate != 0) {
            Log.d("EXPIRE_DATE", String.valueOf(expiredate));
            return new Date().getTime() > expiredate;
        } else {
            Log.d("EXPIRE_DATE_IS", String.valueOf(0));
            return false;
        }
    }

    private void signWithMSAL() {
        // initiate the MSAL authentication on a background thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("MSAL_AUTH_STARTED", "MSAL AUTH STARTED...");
                try {
                    String loginHint = null;
                    if (mUserAccount != null) {
                        loginHint = mUserAccount.getUPN();
                    }
                    MSALUtil.acquireToken(MainActivity.this, Constants.MSAL_SCOPES, loginHint, new AuthCallback());
                } catch (MsalException | InterruptedException e) {
                    Log.d("ERROR_MSAL_AUTH", "ERROR WHILE MSAL AUTHENTICATION");
                    setSharedPreferencesValue("ERROR", e.getMessage(), "string");
                    displayErrorPage();
                }
            }
        });
        thread.start();
    }

    private class AuthCallback implements AuthenticationCallback {
        @Override
        public void onError(final MsalException exc) {
            if (exc instanceof MsalIntuneAppProtectionPolicyRequiredException) {
                MsalIntuneAppProtectionPolicyRequiredException appException = (MsalIntuneAppProtectionPolicyRequiredException) exc;

                final String upn = appException.getAccountUpn();
                final String aadid = appException.getAccountUserId();
                final String tenantId = appException.getTenantId();
                final String authorityURL = appException.getAuthorityUrl();

                String msg = "UPN:::"+upn +":::AADID"+aadid+":::TENANTID"+ tenantId +":::AUTHORITY_URL"+ authorityURL;
                Log.d("ERROR_WHILE_MSAL_AUTH", msg);

                // The user cannot be considered "signed in" at this point, so don't save it to the settings.
                mUserAccount = new AppAccount(upn, aadid, tenantId, authorityURL);
                setSharedPreferencesValue("MSAL_SIGNIN_CALLBACK_ERROR1", exc.getMessage(), "string");
                displayErrorPage();
            } else if (exc instanceof MsalUserCancelException) {
                setSharedPreferencesValue("MSAL_SIGNIN_CALLBACK_ERROR2", exc.getMessage(), "string");
                displayErrorPage();
            } else {
                setSharedPreferencesValue("MSAL_SIGNIN_CALLBACK_ERROR3", exc.getMessage(), "string");
                displayErrorPage();
            }
        }

        @Override
        public void onSuccess(final IAuthenticationResult authenticationResult) {
            account = authenticationResult.getAccount();
            setSharedPreferencesValue("GRAPHACCESSTOKEN", authenticationResult.getAccessToken(), "string");
            setSharedPreferencesValue("GRAPHTOKENEXPIREON", String.valueOf(authenticationResult.getExpiresOn().getTime()), "long");
            final String upn = account.getUsername();
            final String aadId = account.getId();
            final String tenantId = account.getTenantId();
            final String authorityURL = account.getAuthority();

            String msg = "UPN:::"+upn +":::AADID"+ aadId +":::TENANTID"+ tenantId +":::AUTHORITY_URL"+ authorityURL;
            Log.d("ACCESS_TOKEN", authenticationResult.getAccessToken());
            Log.d("MSAL_AUTH_ACCOUNT::::::", msg);

            // Save the user account in the settings, since the user is now "signed in".
            mUserAccount = new AppAccount(upn, aadId, tenantId, authorityURL);
            AppSettings.saveAccount(getApplicationContext(), mUserAccount);
            // Register the account for MAM.

            mEnrollmentManager.registerAccountForMAM(upn, aadId, tenantId, authorityURL);
            mSingleAccountApp = MSALUtil.mSingleAccountApp;
            // getTokenForQA();
            callGraphAPI(authenticationResult);
        }
        @Override
        public void onCancel() {
            displayErrorPage();
        }
    }

    private void callGraphAPI(final IAuthenticationResult authenticationResult) {
        HttpsTrustManager.allowAllSSL();
        MSGraphRequestWrapper.callGraphAPIUsingVolley(
                getApplicationContext(),
                defaultGraphResourceUrl,
                authenticationResult.getAccessToken(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("USERDATA", response.toString());
                        myEdit.commit();
                        displayHomePage();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR_CALL_GRAPH_API", error.toString());
                        Log.d("ERROR_CALL_GRAPH_API", error.getMessage());
                    }
                });
    }

    public void getTokenForQA() {
        Log.d("SCOPE>>>>>>>>>>", Constants.INTERNAL_WEB_LINK_SCOPE.toString());
        Log.d("mSINGLEACCOUTNT", mSingleAccountApp.toString());
        Log.d("AUTHORITY>>>>>>>>>>", mSingleAccountApp.getConfiguration().getDefaultAuthority().getAuthorityURL().toString());
        mSingleAccountApp.acquireTokenSilentAsync(
                Constants.INTERNAL_WEB_LINK_SCOPE,
                mSingleAccountApp.getConfiguration().getDefaultAuthority().getAuthorityURL().toString(),
                getAuthSilentCallback1());
//        MSALUtil.mSingleAccountApp.acquireTokenSilentAsync(
//                SCOPES,
//                MSALUtil.mSingleAccountApp.getConfiguration().getDefaultAuthority().getAuthorityURL().toString(),
//                getAuthSilentCallback1());
    }

    private SilentAuthenticationCallback getAuthSilentCallback1() {
        return new SilentAuthenticationCallback() {
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                Log.d(">>>>>>>>>>>>>>>>>RES", authenticationResult.getAccessToken());
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("INTERNALWEBLINKTOKEN", authenticationResult.getAccessToken());
                myEdit.commit();
            }
            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(">>>>>>>>>>>>>>>>>CODE", exception.getErrorCode());
                Log.d(">>>>>>>>>>>>>>>>>MSG", exception.getMessage());
                if (exception instanceof MsalClientException) {
                    Log.d("MsalClientException_COD", exception.getErrorCode());
                    Log.d("MsalClientException_MSG", exception.getMessage());
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    Log.d("MsalServiceException_C", exception.getErrorCode());
                    Log.d("MsalServiceException_M", exception.getMessage());
                    /* Exception when communicating with the STS, likely config issue */
                } else if (exception instanceof MsalUiRequiredException) {
                    Log.d("MsalUiRequiException_C", exception.getErrorCode());
                    Log.d("MsalUiRequiException_M", exception.getMessage());
                    /* Tokens expired or no session, retry with interactive */
                }
            }
        };
    }

    private void displayErrorPage() {
        setCurrentFragment(AppFragment.ErrorFragment);
    }

    private void displayHomePage() {
        setCurrentFragment(AppFragment.SingleAccount);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) { }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) { }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_single_account) {
                    setCurrentFragment(AppFragment.SingleAccount);
                }

//                if (id == R.id.nav_multiple_account) {
//                    setCurrentFragment(AppFragment.MultipleAccount);
//                }
//
//                if (id == R.id.nav_b2c) {
//                    setCurrentFragment(AppFragment.B2C);
//                }

                if (id == R.id.profile_screen) {
                    sharedPreferences = getSharedPreferences("STORAGE", Context.MODE_PRIVATE);
                    String errorMessage = sharedPreferences.getString("ERROR","Error, Try again later.");
                    Log.d("EERROR_MSG", errorMessage);
                    Intent intent = new Intent(getApplicationContext(), ProfileScreenActivity.class);
                    startActivity(intent);
                }

                if (id == R.id.intLink) {
                    // openLinkInEdgeBrowser("https://app-gn.eyqa.net/GN/EngagementApp/");
                    Intent intent = new Intent(MainActivity.this, WebViewBrowser.class);
                    intent.putExtra("link", Constants.INTERNAL_WEB_LINK);
                    intent.putExtra("isInternalLink", true);
                    startActivity(intent);
                }
                if (id == R.id.extLink) {
                    // openLinkInEdgeBrowser("https://app-gn.eyqa.net/GN/EngagementApp/");
                    Intent intent = new Intent(MainActivity.this, WebViewBrowser.class);
                    intent.putExtra("link", Constants.EXTERNAL_WEB_LINK);
                    intent.putExtra("isInternalLink", false);
                    startActivity(intent);
                }

                if (id == R.id.camera) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
                        } else {
                            openCameraAndTakePic();
                        }
                    }
                }

                if (id == R.id.documents) {
                    Intent intent = new Intent(MainActivity.this, DocumentsScreenActivity.class);
                    startActivity(intent);
                }

                if (id == R.id.scanQRCode) {
                    startActivity(new Intent(MainActivity.this, ScanQRcodeActivity.class));
                }

                if (id == R.id.sharePoint) {
                    Intent intent = new Intent(MainActivity.this, SharepointList.class);
                    startActivity(intent);
                }

                if (id == R.id.playVideo) {
                    Intent mIntent = getPackageManager().getLaunchIntentForPackage(Constants.MS_STREAM_APP_PKG_ID);
                    if (mIntent != null) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.VIDEO_LINK)));
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    else {
                        installMSStreamApp();
                    }
                }

                if (id == R.id.appSupport) {
                    // Analytics.trackEvent("App Support screen");
                    Intent intent = new Intent(MainActivity.this, SupportScreenActivity.class);
                    startActivity(intent);
                }

                if (id == R.id.appInfo) {
                    Intent intent = new Intent(MainActivity.this, AppinfoScreenActivity.class);
                    startActivity(intent);
                }

                if (id == R.id.faq) {
                    // Analytics.trackEvent("FAQ screen");
                    Intent intent = new Intent(MainActivity.this, FAQScreenActivity.class);
                    startActivity(intent);
                }

                if (id == R.id.logout) {
                    signOut();
                }

                drawer.removeDrawerListener(this);
            }

            @Override
            public void onDrawerStateChanged(int newState) { }
        });

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        SharedPreferences settings = getSharedPreferences("STORAGE", Context.MODE_PRIVATE);
        settings.edit().clear().commit();
        Log.d("ADDID>>>>>>>>>", mUserAccount.getAADID());
        Thread thread = new Thread(() -> {
            try {
                MSALUtil.signOutAccount(getApplicationContext(), mUserAccount.getAADID());
            } catch (MsalException | InterruptedException e) {
                Log.d("LOGOUT_STAUTS:::", "Failed to sign out user " + mUserAccount.getAADID(), e);
            }
            mEnrollmentManager.unregisterAccountForMAM(mUserAccount.getUPN());
            AppSettings.clearAccount(getApplicationContext());
            mUserAccount = null;
        });
        thread.start();
    }

    private void setCurrentFragment(final AppFragment newFragment){
        if (newFragment == mCurrentFragment) {
            return;
        }

        mCurrentFragment = newFragment;
        setHeaderString(mCurrentFragment);
        displayFragment(mCurrentFragment);
    }

    private void setHeaderString(final AppFragment fragment){
        switch (fragment) {
            case SingleAccount:
                getSupportActionBar().setTitle(R.string.app_name);
                return;

            case MultipleAccount:
                getSupportActionBar().setTitle("Multiple Account Mode");
                return;

            case B2C:
                getSupportActionBar().setTitle("Business Account Mode");
                return;

            case ErrorFragment:
                getSupportActionBar().setTitle("Test2ReferenceQA Error");
                return;

            default:
                getSupportActionBar().setTitle("Test2ReferenceQA");
                return;
        }
    }

    private void displayFragment(final AppFragment fragment){
        switch (fragment) {
            case SingleAccount:
                attachFragment(new SingleAccountModeFragment());
                return;

            case MultipleAccount:
                attachFragment(new MultipleAccountModeFragment());
                return;

            case B2C:
                attachFragment(new B2CModeFragment());
                return;

            case ErrorFragment:
                attachFragment(new ErrorFragment());
                return;
        }
    }

    private void attachFragment(final Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(mContentMain.getId(),fragment)
                .commit();
    }

    private void setSharedPreferencesValue(String key, String value, String type) {
        Log.d("SET_SP>>>>>>>>>>>", value);
        SharedPreferences.Editor spEdit = sharedPreferences.edit();
        if (type == "string") { spEdit.putString(key, value); }
        if (type == "long") { spEdit.putLong(key, Long.parseLong(value)); }
        spEdit.commit();
    }

    private void openCameraAndTakePic() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra( MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            try {
                pictureFile = getPictureFile();
            } catch (IOException ex) {
                // Analytics.trackEvent("ERROR: captured image failed to save in app memory");
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".provider", pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    public File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "RefApp" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        return image;
    }

    public void installMSStreamApp() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Stream App not found...!");
        dialog.setMessage("Microsoft Stream app is not installed on your device. Would you like to install now ?");
        dialog.setNegativeButton("Not Really", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.MS_STREAM_APP_PLAY_STORE_LINK)));
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        dialog.show();
    }

}
