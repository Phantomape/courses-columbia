package com.eathere.cc.eathere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        // Get toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // display back arrow button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String rating = intent.getStringExtra("rating");
        String address = intent.getStringExtra("address");
        String category = intent.getStringExtra("category");
        TextView textViewTitle = (TextView) findViewById(R.id.activity_info_text_view_title);
        TextView textViewRating = (TextView) findViewById(R.id.activity_info_text_view_rating);
        TextView textViewAddress = (TextView) findViewById(R.id.activity_info_text_view_address);
        TextView textViewCategory = (TextView) findViewById(R.id.activity_info_text_view_category);
        textViewTitle.setText(title);
        textViewRating.setText(rating);
        textViewAddress.setText(address);
        textViewCategory.setText(category);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // Let back arrow button to go back to MainActivity
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
