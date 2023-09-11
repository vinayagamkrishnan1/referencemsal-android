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

package com.eygsl.cbs.referencemsal.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.eygsl.cbs.referencemsal.R;
import com.microsoft.identity.msal.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class SingleAccountModeFragment extends Fragment {

    SharedPreferences sharedPreferences;
    TextView welcomeText;
    TextView appVersionText;
    TextView bestPracticeLinkText;
    String userDataString;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        sharedPreferences = getActivity().getSharedPreferences("STORAGE", Context.MODE_PRIVATE);
        userDataString = sharedPreferences.getString("USERDATA","{}");
        Log.d("USER_DATA_STRING", userDataString);

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_single_account_mode, container, false);
        initializeUI(view);

        return view;
    }

    /**
     * Initializes UI variables and callbacks.
     */
    private void initializeUI(@NonNull final View view) {

        welcomeText = view.findViewById(R.id.welcome);
        appVersionText = view.findViewById(R.id.homeAppVersion);

        try {
            JSONObject userData = new JSONObject(userDataString);
            String name = String.valueOf(userData.get("displayName"));
            welcomeText.setText("Welcome " + name + "to Mobile Application Services team.");
            appVersionText.setText("Version : " + BuildConfig.VERSION_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
