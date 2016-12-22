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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eathere.cc.eathere.model.AsyncNetUtils;
import com.eathere.cc.eathere.model.NetworkStatusUtils;
import com.eathere.cc.eathere.model.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class RestaurantFragment extends Fragment{

    public RestaurantFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    AsyncNetUtils.post("http://54.210.133.203:8080/api/restaurant/search", "uid=123&keyword=pizza", new AsyncNetUtils.Callback() {
                        @Override
                        public void onResponse(String response) {
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
                                    RestaurantSimpleAdapter adapter = new RestaurantSimpleAdapter(getActivity(), restaurants,
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

    public class RestaurantSimpleAdapter extends SimpleAdapter {
        private Context context;
        private String[] picUrls;
        public RestaurantSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
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
            Glide.with(context).load(picUrls[position]).placeholder(android.R.drawable.ic_menu_search).into(imageView);
            return view;
        }
    }
}