package com.eygsl.cbs.referencemsal;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.eygsl.cbs.referencemsal.adapters.DocumentsAdapter;
import com.eygsl.cbs.referencemsal.models.DocumentModel;
import com.eygsl.cbs.referencemsal.networkrequest.NetworkHandler;
import com.eygsl.cbs.referencemsal.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class DocumentsScreenActivity extends AppCompatActivity implements
        DocumentsAdapter.customButtonListener {

    BottomSheetDialog mBottomDialogNotificationAction;
    View sheetView;
    LinearLayout openDocument;
    LinearLayout saveOrShareDocument;
    LinearLayout closeActionSheet;
    TextView textViewSaveOrShare, errorMessage;
    DownloadManager downloadManager;
    public static String documentPath;

    ListView listView;
    ArrayList<DocumentModel> dataModels;
    private static DocumentsAdapter adapter;

    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_screen);
        setTitle("Documents");

        errorMessage = (TextView)findViewById(R.id.txt_view_message);
        listView = (ListView)findViewById(R.id.documentslist);
        dataModels = new ArrayList<>();
        adapter = new DocumentsAdapter(dataModels, getApplicationContext());
        adapter.setCustomButtonListner((DocumentsAdapter.customButtonListener) DocumentsScreenActivity.this);
        listView.setAdapter(adapter);
        this.fetchFiles();
    }

    @Override
    public void onButtonClickListner(int position, String value) throws IOException {
        DocumentModel dataModel = dataModels.get(position);

        if (value == "documentname") {
//            Toast.makeText(DocumentsscreenActivity.this, dataModel.getFileDownloadURL(), Toast.LENGTH_SHORT).show();
        } else if (value == "options") {
            this.showActionSheet(position);
        }
    }

    public void downloadFile(int position) throws IOException {
        DocumentModel dataModel = dataModels.get(position);
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(dataModel.getFileDownloadURL());
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //request.setDestinationInExternalPublicDir(getDocumentFileFromStorage(dataModel.getDocumentName()) , this.getFileExtension(dataModel.getDocumentName()));
        dataModel.setLocalFilePath(getDocumentFileFromStorage(dataModel.getDocumentName()));
        textViewSaveOrShare.setText("Share");
        Long reference = downloadManager.enqueue(request);
    }

    public String getDocumentFileFromStorage(String filename) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "RefApp" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File document = File.createTempFile(pictureFile,  "." + this.getFileExtension(filename), storageDir);
        documentPath = document.getAbsolutePath();
//        Log.d("DOWNLOADPATH", documentPath);
        return documentPath;
    }

    public String getFileExtension(String filename) {
        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i >= 0) {
            extension = filename.substring(i+1);
        }
        return extension;
    }

    private void fetchFiles() {
        try {
            // String receivedData = new NetworkHandler().execute("").get();
            String receivedData = new NetworkHandler(getApplicationContext()).execute("").get();
            JSONObject jsonObject = new JSONObject(receivedData);
            JSONArray jsonArray = new JSONArray(jsonObject.getString("value"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                if (jsonObject1.has("@microsoft.graph.downloadUrl")) {
                    dataModels.add(new DocumentModel(
                                    jsonObject1.getString("id"),
                                    jsonObject1.getString("name"),
                                    jsonObject1.getString("size"),
                                    jsonObject1.getString("webUrl"),
                                    jsonObject1.getString("@microsoft.graph.downloadUrl"),
                                    ""
                            )
                    );
                }
            }
        }
        catch (ExecutionException | InterruptedException | JSONException ei) {
            Log.d("ERROR::::", ei.toString());
            errorMessage.setVisibility(View.VISIBLE);
            ei.printStackTrace();
        }
    }

    private void showActionSheet(final int position) {
        final DocumentModel dataModel = dataModels.get(position);
        try {
            sheetView = this.getLayoutInflater().inflate(R.layout.action_sheet, null);
            mBottomDialogNotificationAction = new BottomSheetDialog(this);
            mBottomDialogNotificationAction.setContentView(sheetView);
            mBottomDialogNotificationAction.show();

            openDocument = (LinearLayout) sheetView.findViewById(R.id.layout_open_document);
            saveOrShareDocument = (LinearLayout) sheetView.findViewById(R.id.layout_save_document);
            closeActionSheet = (LinearLayout) sheetView.findViewById(R.id.layout_close_actionsheet);
            textViewSaveOrShare = (TextView) sheetView.findViewById(R.id.textViewSaveOrShare);

            if (dataModel.getLocalFilePath() == "") {
                textViewSaveOrShare.setText("Save to application");
            } else if (dataModel.getLocalFilePath() != "") {
                textViewSaveOrShare.setText("Share");
            }

            openDocument.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    openDocument(position);
                    mBottomDialogNotificationAction.dismiss();
                }
            });


            saveOrShareDocument.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (textViewSaveOrShare.getText() == "Save to application") {
                        try {
                            saveIntoApplication(position);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (textViewSaveOrShare.getText() == "Share") {
                        shareWithOneOutlook(dataModel.getLocalFilePath());
                    }
                    mBottomDialogNotificationAction.dismiss();
                }
            });

            closeActionSheet.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mBottomDialogNotificationAction.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareWithOneOutlook(String attachmentLink) {
        // 'com.microsoft.office.outlook' : 'ms-outlook://compose',
        boolean isOutlookInstalled = isApplicationIstalled(Constants.MS_OUTLOOK_APP_PKG_ID);
        if (!isOutlookInstalled) {
            showMessage("Please install Microsoft Outlook from Company Portal.");
        } else {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(Constants.MS_OUTLOOK_APP_PKG_ID);
            launchIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ "abd@gmail.com"});
            launchIntent.putExtra(Intent.EXTRA_SUBJECT, "Email subject");
            launchIntent.putExtra(Intent.EXTRA_TEXT, "My message");
            startActivity(launchIntent);
        }
    }

    private void openDocument(int position) {
        DocumentModel dataModel = dataModels.get(position);
        Toast.makeText(DocumentsScreenActivity.this, "Open Document", Toast.LENGTH_LONG).show();
        boolean isEdgeBrowserInstalled = isApplicationIstalled(Constants.MS_EDGE_APP_PKG_ID);
        if(isEdgeBrowserInstalled) {
            openInEdgeBrowser(dataModel.getFileDownloadURL());
        } else {
            showMessage("Please install Edge browser from the Intune Company Portal.");
        }
    }

    private void saveIntoApplication(int position) throws IOException {
        this.downloadFile(position);
        Toast.makeText(DocumentsScreenActivity.this, "Save Document", Toast.LENGTH_LONG).show();
    }

    public void openInEdgeBrowser(String url) {
        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(Constants.MS_EDGE_APP_PKG_ID);
        LaunchIntent.setData(Uri.parse(url));
        startActivity(LaunchIntent);
    }

    public void showMessage(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Message");
        dialog.setMessage(message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Enter your Code for exit Application
            }
        });
        dialog.show();
    }

    private boolean isApplicationIstalled(String uri) {
        PackageManager packageManager = getPackageManager();
        try {
            packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


}
