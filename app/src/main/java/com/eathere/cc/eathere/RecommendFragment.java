package com.eathere.cc.eathere;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends Fragment{

    public RecommendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recommend, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.frag_recommend_list_view);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), getData(),
                R.layout.fragment_recommend_list_view_item, new String[] { "img", "title", "rating", "address", "category"},
                new int[] { R.id.img, R.id.title, R.id.rating, R.id.address, R.id.category });
        listView.setAdapter(adapter);
        // Inflate the layout for this fragment
        return rootView;
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("img", android.R.drawable.ic_menu_search);
        map.put("title", "Organic Avenue");
        map.put("rating", "3.5/5.0");
        map.put("address", "111 W 40th St, Theater District");
        map.put("category", "Salad, JuiceBars & Smoothies, Vegetarian");
        list.add(map);

        map.put("img", android.R.drawable.ic_menu_search);
        map.put("title", "Organic Avenue");
        map.put("rating", "3.5/5.0");
        map.put("address", "111 W 40th St, Theater District");
        map.put("category", "Salad, JuiceBars & Smoothies, Vegetarian");
        list.add(map);

        map.put("img", android.R.drawable.ic_menu_search);
        map.put("title", "Organic Avenue");
        map.put("rating", "3.5/5.0");
        map.put("address", "111 W 40th St, Theater District");
        map.put("category", "Salad, JuiceBars & Smoothies, Vegetarian");

        list.add(map);
        map.put("img", android.R.drawable.ic_menu_search);
        map.put("title", "Organic Avenue");
        map.put("rating", "3.5/5.0");
        map.put("address", "111 W 40th St, Theater District");
        map.put("category", "Salad, JuiceBars & Smoothies, Vegetarian");
        list.add(map);

        map.put("img", android.R.drawable.ic_menu_search);
        map.put("title", "Organic Avenue");
        map.put("rating", "3.5/5.0");
        map.put("address", "111 W 40th St, Theater District");
        map.put("category", "Salad, JuiceBars & Smoothies, Vegetarian");
        list.add(map);

        map.put("img", android.R.drawable.ic_menu_search);
        map.put("title", "Organic Avenue");
        map.put("rating", "3.5/5.0");
        map.put("address", "111 W 40th St, Theater District");
        map.put("category", "Salad, JuiceBars & Smoothies, Vegetarian");
        list.add(map);

        map.put("img", android.R.drawable.ic_menu_search);
        map.put("title", "Organic Avenue");
        map.put("rating", "3.5/5.0");
        map.put("address", "111 W 40th St, Theater District");
        map.put("category", "Salad, JuiceBars & Smoothies, Vegetarian");
        list.add(map);

        map.put("img", android.R.drawable.ic_menu_search);
        map.put("title", "Organic Avenue");
        map.put("rating", "3.5/5.0");
        map.put("address", "111 W 40th St, Theater District");
        map.put("category", "Salad, JuiceBars & Smoothies, Vegetarian");
        list.add(map);

        map.put("img", android.R.drawable.ic_menu_search);
        map.put("title", "Organic Avenue");
        map.put("rating", "3.5/5.0");
        map.put("address", "111 W 40th St, Theater District");
        map.put("category", "Salad, JuiceBars & Smoothies, Vegetarian");
        list.add(map);



        return list;
    }
}