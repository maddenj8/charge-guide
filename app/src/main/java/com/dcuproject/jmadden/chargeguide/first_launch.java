package com.dcuproject.jmadden.chargeguide;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
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

public class first_launch extends AppCompatActivity {
    private Spinner makes, model;
    private Button currentLocAsHome;
    private Button fin;
    private String lat;
    private String lon;
    public boolean home = false;
    private LocationListener listener;
    double dlat;
    double dlon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);
        final LocationManager locationManager;
        // if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        makes = (Spinner) findViewById(R.id.make_dropdown);
        model = (Spinner) findViewById(R.id.model_dropdown);
        currentLocAsHome = (Button) findViewById(R.id.currentLocAsHome);
        fin = (Button) findViewById(R.id.enterApp);


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


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                dlat = location.getLatitude();
                dlon = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);

            }


        };


            currentLocAsHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    Boolean home = currentLocAsHome.isActivated();
                 //   locationManager.requestLocationUpdates("gps", 500,0,listener);

                    String lat = String.valueOf(dlat);
                    String lon = String.valueOf(dlon);
                    editor.putString("lat", lat);
                    editor.putString("lon", lon);
                    editor.apply();
                    Log.d(lon, "lon");
                    Log.d(lat, "lat");
                }


            });

            //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            fin.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    startActivity(new Intent(first_launch.this, MapMain.class));
                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("Setup_complete", "true");
                    editor.apply();
                }


            });

        }
    }
