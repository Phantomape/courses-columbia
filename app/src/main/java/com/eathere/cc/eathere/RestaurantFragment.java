package com.eathere.cc.eathere;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
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


public class RestaurantFragment extends Fragment{
    private static final String TAG = "RestaurantFragment";

    private SharedPreferences sharedPreferences;

    public RestaurantFragment() {
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
        final View rootView = inflater.inflate(R.layout.fragment_restaurant, container, false);
        final SearchView searchView = (SearchView) rootView.findViewById(R.id.frag_restaurant_search_view);
        final ListView listView = (ListView) rootView.findViewById(R.id.frag_restaurant_list_view);

        listView.setEmptyView(rootView.findViewById(R.id.frag_restaurant_empty_list));
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
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (NetworkStatusUtils.isNetworkConnected(getActivity())) {
                    String keyword = s;
                    String uid = sharedPreferences.getString("uid", "empty_uid");
                    AsyncNetUtils.post("http://cclb-635335002.us-east-1.elb.amazonaws.com:8080/api/restaurant/search", "uid="+uid+"&keyword="+keyword, new AsyncNetUtils.Callback() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null) { // if http response is 200
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
                                        Toast.makeText(getContext(), "No results found", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(), "No results found", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "No Internet", Toast.LENGTH_LONG).show();
                }
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                }
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

}