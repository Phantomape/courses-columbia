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
import android.widget.RatingBar;
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
    private static final String TAG = "InfoActivity";
    private static final float EMPTY_RATING = -1;
    private static final float EMPTY_COORDINATE = -999;

    String rname;
    String address;
    double latitude;
    double longitude;
    float overallRating;

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
        rname = intent.getStringExtra("rname");
        overallRating = (float) intent.getDoubleExtra("overall_rating", EMPTY_RATING);
        address = intent.getStringExtra("address");
        latitude = intent.getDoubleExtra("latitude", EMPTY_COORDINATE);
        longitude = intent.getDoubleExtra("longitude", EMPTY_COORDINATE);
        TextView textViewRName = (TextView) findViewById(R.id.activity_info_text_view_rname);
        TextView textViewAddress = (TextView) findViewById(R.id.activity_info_text_view_address);
        textViewRName.setText(rname);
        textViewAddress.setText(address);

        RatingBar ratingBar = (RatingBar) findViewById(R.id.activity_info_rating_bar_overall_rating);
        ratingBar.setRating(overallRating);

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
        LatLng latLng = null;
        if ((latitude != EMPTY_COORDINATE) && (longitude != EMPTY_COORDINATE)) {
            latLng = new LatLng(latitude, longitude);
        } else {
            Geocoder geodecoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geodecoder.getFromLocationName(address, 3);
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
                latLng = new LatLng(lat, lng);
            }
        }
        if (latLng != null) {
            map.addMarker(new MarkerOptions().position(latLng));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        } else {
            Log.e("Google Map", "No marker is placed");
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
