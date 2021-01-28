package com.example.together.my;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.together.R;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<String> item;

    public MyAdapter(Context context, ArrayList<String> data) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        item = data;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int position) {
        return item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.my_list_item, null);

        TextView textView = view.findViewById(R.id.tvItem);

        textView.setText(item.get(position));

        return view;
    }
}
