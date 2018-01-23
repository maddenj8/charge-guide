package com.dcuproject.jmadden.chargeguide;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapMain extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void showLocation() {

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng ireland = new LatLng(53.433333, -7.95);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ireland, 6.0f));
    }

    public void showLocation(View view) {
        EditText edit = (EditText)findViewById(R.id.search_query);
        String location = edit.getText().toString();
        Geocoder coder = new Geocoder(this);
        try {
            ArrayList<Address> locations = (ArrayList<Address>) coder.getFromLocationName(location, 50);
            for (Address add : locations) {
                Log.i("COUNTRYNAME", add.getCountryName());
                if (add.getCountryName() == "Ireland") {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude())));
                    break;
                }
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

}
