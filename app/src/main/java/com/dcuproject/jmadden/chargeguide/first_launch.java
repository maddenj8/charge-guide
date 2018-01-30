package com.dcuproject.jmadden.chargeguide;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;


public class first_launch extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Spinner makes, model;
    private Button currentLocAsHome;
    private Button fin;
    private Boolean home;
    boolean mLocationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);

        makes = (Spinner) findViewById(R.id.make_dropdown);
        model = (Spinner) findViewById(R.id.model_dropdown);
        currentLocAsHome = (Button) findViewById(R.id.currentLocAsHome);
        fin = (Button) findViewById(R.id.enterApp);

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

        currentLocAsHome.setOnClickListener(new View.OnClickListener(){
            public void onClick (View view){
                //SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);
                //SharedPreferences.Editor editor = sharedPref.edit();
                home = currentLocAsHome.isActivated(); //check if the checkbox is ticked
            }
        });

        fin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (home) { // if the user wants the current location as home
                    //get the users current location
                }
                startActivity(new Intent(first_launch.this, MapMain.class));
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Setup_complete", "true");
                editor.apply();
            }


        });
        //getLocationPermission();
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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
}









