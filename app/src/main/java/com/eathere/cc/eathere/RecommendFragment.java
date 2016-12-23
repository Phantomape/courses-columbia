package com.eathere.cc.eathere;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.eathere.cc.eathere.model.AsyncNetUtils;
import com.eathere.cc.eathere.model.NetworkStatusUtils;
import com.eathere.cc.eathere.model.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RecommendFragment extends Fragment{

    private static final String TAG = "RecommendFragment";

    private Timer refreshTimer;
    private Handler refreshHandler;
    private SharedPreferences sharedPreferences;

    private ListView listView;

    public RecommendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recommend, container, false);

        listView = (ListView) rootView.findViewById(R.id.frag_recommend_list_view);
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

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        startRefreshTimerTask();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRefreshTimerTask();
    }

    public void startRefreshTimerTask() {
        refreshTimer = new Timer();
        refreshHandler = new Handler();
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
            refreshHandler.post(new Runnable() {
                @Override
                public void run() {
                if (NetworkStatusUtils.isNetworkConnected(getActivity())) {
                    String uid = sharedPreferences.getString("uid", "empty_uid");
                    AsyncNetUtils.post("http://cclb-635335002.us-east-1.elb.amazonaws.com:8080/api/restaurant/search", "uid=123&keyword=pizza", new AsyncNetUtils.Callback() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "Background refresh for recommended restaurant list");
                            JSONObject jsonResponse = null;
                            try {
                                jsonResponse = new JSONObject(response);
                                if (jsonResponse.getBoolean("status") == true) {
                                    JSONArray restaurantsJson = jsonResponse.getJSONArray("restaurant_infos");
                                    List<Map<String, Object>> restaurants = new ArrayList<Map<String, Object>>();
                                    for (int i = 0; i < restaurantsJson.length(); i++) {
                                        Restaurant restaurant = Restaurant.fromJSONObject(restaurantsJson.getJSONObject(i));
                                        restaurants.add(restaurant.toMap());
                                    }
                                    GlideSimpleAdapter adapter = new GlideSimpleAdapter(getActivity(), restaurants,
                                            R.layout.fragment_restaurant_list_view_item, new String[]{"pic_url", "rname", "overall_rating", "address", "rid"},
                                            new int[]{R.id.pic, R.id.rname, R.id.overall_rating, R.id.address, R.id.category});
                                    listView.setAdapter(adapter);
                                } else {
                                    // No recommendation: do nothing
                                }
                            } catch (JSONException e) {
                                // No recommendation: do nothing
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "No Internet");
                }
                }
            });
            }
        }, 1000, 10000);
    }

    public void stopRefreshTimerTask() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
        refreshTimer = null;
        refreshHandler = null;
    }

}