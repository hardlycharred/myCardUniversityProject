package com.example.charliehard.mycard.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.charliehard.mycard.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in the Octagon and move the camera
        LatLng octagon = new LatLng(-45.874152, 170.503567);
        mMap.addMarker(new MarkerOptions().position(octagon).title("The Octagon, Dunedin"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(octagon, 15f));
    }
}