package com.eygsl.cbs.referencemsal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eygsl.cbs.referencemsal.R;
import com.eygsl.cbs.referencemsal.models.SharepointListItemModel;

import java.util.ArrayList;

public class SharepointListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SharepointListItemModel> sharepointItemModelArrayList;

    public SharepointListAdapter(Context context, ArrayList<SharepointListItemModel> tennisModelArrayList) {
        this.context = context;
        this.sharepointItemModelArrayList = tennisModelArrayList;
    }

    @Override
    public int getCount() {
        return sharepointItemModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return sharepointItemModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Initialize view holder
        ViewHolder viewHolder;

        // Inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.sharepoint_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get current item to be displayed
        SharepointListItemModel currentItem = (SharepointListItemModel) getItem(position);

        viewHolder.name.setText(currentItem.getName());
        viewHolder.description.setText(currentItem.getDescription());

        return convertView;
    }
}

class ViewHolder {
    TextView name;
    TextView description;

    public ViewHolder(View view) {
        name = (TextView) view.findViewById(R.id.name);
        description = (TextView) view.findViewById(R.id.description);
    }
}
