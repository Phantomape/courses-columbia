package com.eathere.cc.eathere;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;


public class GlideSimpleAdapter extends SimpleAdapter {
    private Context context;
    private String[] picUrls;
    public GlideSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        String[] picUrls = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            picUrls[i] = (String) (((Map<String, Object>) data.get(i)).get("pic_url"));
        }
        this.picUrls = picUrls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ImageView imageView = (ImageView) view.findViewById(R.id.pic);
        Glide.with(context).load(picUrls[position]).placeholder(R.drawable.ic_restaurant).into(imageView);
        return view;
    }
}
