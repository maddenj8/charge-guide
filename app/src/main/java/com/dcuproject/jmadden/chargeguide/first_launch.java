package com.dcuproject.jmadden.chargeguide;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class first_launch extends AppCompatActivity implements LocationListener{
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Spinner makes, model;
    private Button currentLocAsHome;
    private Button fin;
    private Boolean home;
    private PlaceAutocompleteFragment searchBar;
    boolean mLocationPermissionGranted;
    TextView address;
    AppCompatEditText placeEditText;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);

        // CAR DETAILS
        makes = (Spinner) findViewById(R.id.make_dropdown);
        model = (Spinner) findViewById(R.id.model_dropdown);

        //HOME DETAILS
        currentLocAsHome = (Button) findViewById(R.id.currentLocAsHome);
        fin = (Button) findViewById(R.id.enterApp);

        //PLACEAUTOCOMPLETEFRAGMENT
        searchBar = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment); //PlaceAutocomplete fragment
        searchBar.setHint("Enter Home Address");
        placeEditText = searchBar.getView().findViewById(R.id.place_autocomplete_search_input); //Edit the text of the PlaceAutocomplete
        placeEditText.setTextSize(18.0f);

        address = (TextView) (findViewById(R.id.address_section));

        searchBar.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) { //when a place is selected
                Log.d("PLACE SELECTED", place.toString());
                //SharedPreferences location_info = getApplicationContext().getSharedPreferences("user_location", Context.MODE_PRIVATE); //save the lat/lng of the user
                //SharedPreferences.Editor e = location_info.edit();
                //LatLng location = place.getLatLng();
                //e.putFloat("latitude", (Float) location.latitude());
                //e.putFloat("longitude", location.longitude());
                //e.commit();
            }
            @Override
            public void onError(Status status) {
                Log.i("ERROR_PLACE_SELECT", "Error found: " + status.toString());
            }
        });
        getLocationPermission();

        GeoDataClient nGeoDataClient = Places.getGeoDataClient(this, null);
        PlaceDetectionClient mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        makes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected_make = makes.getSelectedItem().toString();

                if (selected_make.equals("BMW")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.BMW, android.R.layout.simple_spinner_dropdown_item);
                    model.setAdapter(adapter);

                } else if (selected_make.equals("Nissan")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.Nissan, android.R.layout.simple_spinner_dropdown_item);
                    model.setAdapter(adapter);


                } else if (selected_make.equals("VW")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.VW, android.R.layout.simple_spinner_dropdown_item);
                    model.setAdapter(adapter);

                } else if (selected_make.equals("Renault")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.Renault, android.R.layout.simple_spinner_dropdown_item);
                    model.setAdapter(adapter);
                } else if (selected_make.equals("Hyundai")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.Hyundai, android.R.layout.simple_spinner_dropdown_item);
                    model.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        model.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                String selected_make = model.getSelectedItem().toString();
                editor.putString("selectedMake", selected_make);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        fin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                //if (home) { // if the user wants the current location as home
                    //get the users current location
                //}
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Setup_complete", "true");
                editor.apply();
                startActivity(new Intent(first_launch.this, MapMain.class));

            }


        });
    }

    public void onCheckboxClicked(View v) throws IOException {
        home = ((CheckBox) v).isChecked();
        Log.i("SOMETHING", home.toString());
        if (home) {
            searchBar.getView().setVisibility(View.GONE);
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this); //check for changes

                String bestProvider = locationManager.NETWORK_PROVIDER; //set the provider of the location (network is faster)

                Location location = locationManager.getLastKnownLocation(bestProvider); //get the last known location

                //SAVE COORDINATES
                SharedPreferences location_info = getApplicationContext().getSharedPreferences("user_location", Context.MODE_PRIVATE); //save the lat/lng of the user
                SharedPreferences.Editor e = location_info.edit();
                e.putFloat("latitude", (float) location.getLatitude());
                e.putFloat("longitude", (float) location.getLongitude());
                e.commit();

                //GET GEOCODE LOCATION
                List<Address> addresses;
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                //FORMULATE ADDRESS
                String currentLocation = addresses.get(0).getAddressLine(0);
                currentLocation += "\n" + addresses.get(0).getLocality();
                currentLocation += "\n" + addresses.get(0).getCountryName();
                currentLocation += "\n\nIf this is incorrect untick the checkbox and search for location";

                //SHOW ADDRESS
                address.setText(currentLocation);
                address.setVisibility(View.VISIBLE);
            }
        }
        else {
            searchBar.getView().setVisibility(View.VISIBLE);
            address.setVisibility(View.GONE);
        }
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
    */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        //getUsersLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("LOCATION", location.getLatitude() + " " + location.getLongitude());
        SharedPreferences location_info = getApplicationContext().getSharedPreferences("user_location", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = location_info.edit();
        e.putFloat("latitude", (float) location.getLatitude());
        e.putFloat("longitude", (float) location.getLongitude());
        Log.i("COMMIT MESSAGE", String.valueOf(e.commit()));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}









