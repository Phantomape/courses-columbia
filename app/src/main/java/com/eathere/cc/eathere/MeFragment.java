package com.eathere.cc.eathere;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class MeFragment extends Fragment{
    private static final String TAG = "MeFragment";

    private TextView username;
    private Button loginLogout;

    private SharedPreferences sharedPreferences;

    public MeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_me, container, false);
        username = (TextView) rootView.findViewById(R.id.frag_me_user_name);
        Button about = (Button) rootView.findViewById(R.id.frag_me_button_about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });
        loginLogout = (Button) rootView.findViewById(R.id.frag_me_button_login_logout);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String uidStr = sharedPreferences.getString("uid", "empty_uid");
        String usernameStr = sharedPreferences.getString("username", "empty_username");
        if (uidStr.equals("empty_uid")) {
            setSignInLayout();
        } else {
            setSignOutLayout(usernameStr);
        }
    }

    private void setSignInLayout() {
        username.setText(R.string.frag_me_username);
        loginLogout.setText(R.string.frag_me_sign_in);
        loginLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setSignOutLayout(String usernameStr) {
        username.setText(usernameStr);
        loginLogout.setText(R.string.frag_me_sign_out);
        loginLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear().commit();
                setSignInLayout();
            }
        });
    }

}