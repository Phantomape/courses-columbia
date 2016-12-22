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
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.eathere.cc.eathere.model.AsyncNetUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //Toast toast = Toast.makeText(getContext(), "Searching: " + s, Toast.LENGTH_SHORT);
                //toast.show();
                AsyncNetUtils.post("http://54.210.133.203:8080/api/restaurant/search", "uid=123&keyword=pizza", new AsyncNetUtils.Callback() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("status") == true) {
                                Toast toast2 = Toast.makeText(getContext(), "response: " + response, Toast.LENGTH_LONG);
                                toast2.show();
                            } else {

                            }
                        } catch (JSONException e) {

                        }
                    }
                });
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

        ListView listView = (ListView) rootView.findViewById(R.id.frag_restaurant_list_view);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), getData(),
                R.layout.fragment_restaurant_list_view_item, new String[] { "img", "title", "rating", "address", "category"},
                new int[] { R.id.img, R.id.title, R.id.rating, R.id.address, R.id.category });
        listView.setAdapter(adapter);
        listView.setEmptyView(rootView.findViewById(R.id.frag_restaurant_empty_list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), InfoActivity.class);
                Map<String, Object> listEntry = (Map<String, Object>) parent.getAdapter().getItem(position);
                intent.putExtra("title", (String) listEntry.get("title"));
                intent.putExtra("rating", (String) listEntry.get("rating"));
                intent.putExtra("address", (String) listEntry.get("address"));
                intent.putExtra("category", (String) listEntry.get("category"));
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }


    private List<Map<String, Object>> getData() {
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