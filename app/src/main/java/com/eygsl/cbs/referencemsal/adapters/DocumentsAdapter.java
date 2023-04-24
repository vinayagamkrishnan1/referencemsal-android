package com.eygsl.cbs.referencemsal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eygsl.cbs.referencemsal.R;
import com.eygsl.cbs.referencemsal.models.DocumentModel;

import java.io.IOException;
import java.util.ArrayList;


public class DocumentsAdapter extends ArrayAdapter<DocumentModel> {
    customButtonListener customListner;

    public interface customButtonListener {
        void onButtonClickListner(int position, String value) throws IOException;
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private Context context;
    private ArrayList<DocumentModel> dataSet;

    public DocumentsAdapter(ArrayList<DocumentModel> data, Context context) {
        super(context, R.layout.list_row_document, data);
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        DocumentModel dataModel = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_row_document, null);
            viewHolder = new ViewHolder();
            viewHolder.documentName = (TextView) convertView.findViewById(R.id.documentname);
            viewHolder.documentSize = (TextView) convertView.findViewById(R.id.documentsize);
            viewHolder.documentTypeImage = (ImageView) convertView.findViewById(R.id.documenttypeicon);
            viewHolder.infoImage = (ImageView) convertView.findViewById(R.id.infoicon);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final DocumentModel temp = getItem(position);

        String size = dataModel.getDocumentsize() + " KB";

        viewHolder.documentName.setText(dataModel.getDocumentName());
        viewHolder.documentSize.setText(size);
        String extension = this.getFileExtension(dataModel.getDocumentName());

        if (extension.equals("pdf")) {
            viewHolder.documentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.pdf_icon));
        }
        if (extension.equals("ipa")) {
            viewHolder.documentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ipa_icon));
        }
        if (extension.equals("docx")) {
            viewHolder.documentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.doc_icon));
        }
        if (extension.equals("excel")) {
            viewHolder.documentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.excel_icon));
        }
        if (extension.equals("ppt")) {
            viewHolder.documentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.powerpoint_icon));
        }
        if (!extension.equals("pdf") && !extension.equals("ipa") && !extension.equals("docx") && !extension.equals("excel") && !extension.equals("ppt")) {
            viewHolder.documentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.file_icon));
        }
        viewHolder.infoImage.setImageDrawable(context.getResources().getDrawable(R.drawable.more_icon));

        viewHolder.documentName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    try {
                        customListner.onButtonClickListner(position, "documentname");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        viewHolder.infoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    try {
                        customListner.onButtonClickListner(position, "options");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        return convertView;
    }

    public class ViewHolder {
        TextView documentName;
        TextView documentSize;
        ImageView documentTypeImage;
        ImageView infoImage;

    }

    public String getFileExtension(String filename) {
        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i >= 0) {
            extension = filename.substring(i+1);
        }
        return extension;
    }
}
