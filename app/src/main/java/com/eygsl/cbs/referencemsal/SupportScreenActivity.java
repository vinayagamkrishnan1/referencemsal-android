

package com.eygsl.cbs.referencemsal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.eygsl.cbs.referencemsal.utils.Constants;

import java.util.List;

public class SupportScreenActivity extends AppCompatActivity {

    Button supportButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_screen);

        setTitle("Support");

        supportButton = (Button) findViewById(R.id.supportbutton);

        supportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isAppInstalled(getApplicationContext(), Constants.MS_OUTLOOK_APP_PKG_ID)) {
                    openOutlookAndComposeMain(Constants.SUPPORT_EMAIL);
                    // Analytics.trackEvent("App Support screen - Clicked on Mail to button");
                } else  {
                    showMessage();
                    //Analytics.trackEvent("App Support screen - Displayed a message to install Outlook app");
                }
            }
        });
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void openOutlookAndComposeMain(String mail) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.setData(Uri.parse("mailto:" + mail));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Regarding queries");
        PackageManager pm = getPackageManager();
        List emailers = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY | PackageManager.GET_RESOLVED_FILTER);
        startActivity(intent);
    }

    public void showMessage() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Message");
        dialog.setMessage("Please install Outlook application.");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Enter your Code for exit Application
            }
        });
        dialog.show();
    }
}
