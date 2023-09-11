package com.eygsl.cbs.referencemsal.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.eygsl.cbs.referencemsal.R;
import com.eygsl.cbs.referencemsal.models.SharepointListItemModel;

import java.util.ArrayList;

public class SharepointListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SharepointListItemModel> tennisModelArrayList;

    public SharepointListAdapter(Context context, ArrayList<SharepointListItemModel> tennisModelArrayList) {
      this.context = context;
      this.tennisModelArrayList = tennisModelArrayList;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return tennisModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return tennisModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

      if (convertView == null) {
        holder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.lv_player, null, true);

        holder.tvname = (TextView) convertView.findViewById(R.id.name);
        holder.tvcity = (TextView) convertView.findViewById(R.id.city);

        convertView.setTag(holder);
      }else {
          holder = (ViewHolder)convertView.getTag();
      }

        holder.tvname.setText(tennisModelArrayList.get(position).getName());
        holder.tvcity.setText(tennisModelArrayList.get(position).getCity());

        return convertView;
    }

    private class ViewHolder {

//      protected TextView tvname, tvcountry;
//      protected TextView tvname, tvcountry, tvcity;
        protected TextView tvname, tvcity, tvMobilePlatform, tvServiceLine, tvAppDesc;
        protected ImageView iv;
    }

}
