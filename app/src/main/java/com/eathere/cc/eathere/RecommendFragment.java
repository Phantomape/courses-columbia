package com.eathere.cc.eathere;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RecommendFragment extends Fragment{

    private Timer refreshTimer;
    private Handler refreshHandler;

    public RecommendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshTimer = new Timer();
        refreshHandler = new Handler();
    }

    public void refreshTimerTask() {
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Backend refresh", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }, 1000, 10000);// 定时任务
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recommend, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.frag_recommend_list_view);
        listView.setEmptyView(rootView.findViewById(R.id.frag_recommend_empty_list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), InfoActivity.class);
                Map<String, Object> listEntry = (Map<String, Object>) parent.getAdapter().getItem(position);
                intent.putExtra("rname", (String) listEntry.get("rname"));
                intent.putExtra("overall_rating", (double) listEntry.get("overall_rating"));
                intent.putExtra("address", (String) listEntry.get("address"));
                intent.putExtra("rid", (String) listEntry.get("rid")); // TODO: FIX
                startActivity(intent);
            }
        });
        refreshTimerTask();
        // Inflate the layout for this fragment
        return rootView;
    }

    private List<Map<String, Object>> getData() { // TODO: Remove
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < 10; i++) {
            map.put("img", android.R.drawable.ic_menu_search);
            map.put("title", "Organic Avenue");
            map.put("rating", "3.5/5.0");
            map.put("address", "111 W 40th St, Theater District");
            map.put("category", "Salad, JuiceBars & Smoothies, Vegetarian");
            list.add(map);
        }
        return list;
    }
}