package com.example.wulinpeng.tcptest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wulinpeng on 16/5/1.
 */
public class MyAdapter extends BaseAdapter {

    private List<ChatBean> data;

    private LayoutInflater inflater;

    public MyAdapter(Context context, List<ChatBean> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void setData(List<ChatBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            if (getItemViewType(position) == 0) {
                holder = new ViewHolder();
                convertView = inflater.inflate(
                        R.layout.chat_in, null);
                holder.icon = (ImageView) convertView.findViewById(
                        R.id.icon_in);
                holder.text = (TextView) convertView.findViewById(
                        R.id.text_in);
            } else if (getItemViewType(position) == 1) {
                holder = new ViewHolder();
                convertView = inflater.inflate(
                        R.layout.chat_out, null);
                holder.icon = (ImageView) convertView.findViewById(
                        R.id.icon_out);
                holder.text = (TextView) convertView.findViewById(
                        R.id.text_out);
            } else {
                holder = new ViewHolder();
                convertView = inflater.inflate(
                        R.layout.message_item, null);
                holder.text = (TextView) convertView.findViewById(
                        R.id.msg);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (getItemViewType(position) == 2) {
            holder.text.setText(data.get(position).getText());
            return  convertView;
        } else {
            holder.icon.setImageBitmap(data.get(position).getIcon());
            holder.text.setText(data.get(position).getText());
            return convertView;
        }
    }

    public final class ViewHolder {
        public ImageView icon;
        public TextView text;
    }
}
