package com.eathere.cc.eathere;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class InfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static String TAG = "InfoActivity";
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

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.activity_info_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // check permission for current location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Got ACCESS_FINE_LOCATION permission");
            map.setMyLocationEnabled(true);
        }
        Geocoder geodecoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geodecoder.getFromLocationName("111 W 40th St, New York, NY 10018", 100);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            String errorMessage = getString(R.string.google_map_service_not_available);
            Log.e("Google Map", errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid arguments
            String errorMessage = getString(R.string.google_map_invalid_arguments_used);
            Log.e("Google Map", errorMessage, illegalArgumentException);
        }
        if ((addresses != null) && (!addresses.isEmpty())) {
            Address target = addresses.get(0);
            double lat = target.getLatitude();
            double lng = target.getLongitude();
            LatLng latLng = new LatLng(lat, lng);
            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Marker"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        } else {
            Log.e("Google Map", "No addresses found");
        }
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
