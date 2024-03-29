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
import com.google.android.gms.location.places.AutocompleteFilter;
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


public class first_launch extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Spinner makes, models;
    private Button currentLocAsHome;
    private Button fin;
    private Boolean home;
    private Boolean oneSelected;
    private Place placeSelected;
    private PlaceAutocompleteFragment searchBar;
    boolean mLocationPermissionGranted;
    private AutocompleteFilter autocompleteFilter;
    private LocationListener mLocationListener;
    TextView address;
    AppCompatEditText placeEditText;
    private LocationManager locationManager;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);

        // CAR DETAILS
        makes = (Spinner) findViewById(R.id.make_dropdown);
        models = (Spinner) findViewById(R.id.model_dropdown);

        //HOME DETAILS
        currentLocAsHome = (Button) findViewById(R.id.currentLocAsHome);
        fin = (Button) findViewById(R.id.enterApp);

        sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);

        //PLACE SELECTED CHECKS
        oneSelected = false;
        home = false;
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("LOCATION", location.getLatitude() + " " + location.getLongitude());
                saveCoordinates((float) location.getLatitude(), (float) location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) { //check to see what this does

            }

            @Override
            public void onProviderDisabled(String s) { //check to see what this does

            }
        };

        //PLACEAUTOCOMPLETEFRAGMENT
        searchBar = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment); //PlaceAutocomplete fragment
        searchBar.setHint("Enter Home Address");
        placeEditText = searchBar.getView().findViewById(R.id.place_autocomplete_search_input); //Edit the text of the PlaceAutocomplete
        placeEditText.setTextSize(18.0f);

        autocompleteFilter = new AutocompleteFilter.Builder().setCountry("UK").setCountry("Irl").build(); // make a filter for the searchbar (uk and ireland only)
        searchBar = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        searchBar.setFilter(autocompleteFilter); // apply the filter

        address = (TextView) (findViewById(R.id.address_section));

        searchBar.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) { //when a place is selected
                Log.d("PLACE SELECTED", place.toString());
                locationManager.removeUpdates(mLocationListener);
                placeSelected = place; //remember the place that the user selected
                oneSelected = true;
                saveCoordinates((float) place.getLatLng().latitude, (float) place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                Log.i("ERROR_PLACE_SELECT", "Error found: " + status.toString());
            }
        });

        searchBar.getView().findViewById(R.id.place_autocomplete_clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneSelected = false;
            }
        });
        searchBar.getView().findViewById(R.id.place_autocomplete_clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneSelected = false;
                EditText searchText = (EditText) searchBar.getView().findViewById(R.id.place_autocomplete_search_input);
                searchText.setText("");
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
                    models.setAdapter(adapter);

                } else if (selected_make.equals("Nissan")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.Nissan, android.R.layout.simple_spinner_dropdown_item);
                    models.setAdapter(adapter);

                } else if (selected_make.equals("Volkswagen")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.Volkswagen, android.R.layout.simple_spinner_dropdown_item);
                    models.setAdapter(adapter);

                } else if (selected_make.equals("Renault")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.Renault, android.R.layout.simple_spinner_dropdown_item);
                    models.setAdapter(adapter);
                } else if (selected_make.equals("Hyundai")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.Hyundai, android.R.layout.simple_spinner_dropdown_item);
                    models.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        models.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected_model = models.getSelectedItem().toString();
                SharedPreferences.Editor editor = sharedPref.edit();
                String selected_make = makes.getSelectedItem().toString();
                editor.putString("selectedMake", selected_make);
                editor.putString("selectedModel", selected_model);

                if(selected_model.equals("Leaf 24 KWh")) {
                    editor.putFloat("range" , 126);
                    editor.putFloat("kwh", 21);

                    editor.commit();
                }

                else {
                    String battery = selected_model.substring(selected_model.length() - 6, selected_model.length() - 4);

                    final float kwh = Integer.parseInt(battery);
                    float range = kwh * 6;
                    editor.putFloat("kwh", kwh);
                    editor.putFloat("range", range);
                    Log.d("soc", range + "");
                    editor.commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        fin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (!home) { // If the user didn't select home to be current location
                    if (!oneSelected) {
                        // none of the selections are made so the user should be notified
                        Toast.makeText(getApplicationContext(), "Nothing was selected for home, please enter a home address or use current location", Toast.LENGTH_SHORT).show();
                    } else {
                        // home is set by what is searched in the search bar
                        if (placeSelected != null) {
                            try {
                                //get the latitude and longitude of the place that was searched for
                                float lat = (float) placeSelected.getLatLng().latitude;
                                float lng = (float) placeSelected.getLatLng().longitude;
                                Toast.makeText(getApplicationContext(), "You selected " + placeSelected.getName(), Toast.LENGTH_SHORT).show();
                                saveCoordinates(lat, lng);
                                finishSetup();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    // else home is already set as current location be this point so just go to the map activity
                    locationManager.removeUpdates(mLocationListener);
                    finishSetup();
                }
            }
        });
        String make = sharedPref.getString("rememberedMake", "");
        makes.setSelection(getIndex(makes, make));
        //String model = sharedPref.getString("rememberedModel", "");
        //models.setSelection(getIndex(models, model));
    }

    public void finishSetup() {
        startActivity(new Intent(first_launch.this, MapMain.class));
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Setup_complete", "true");
        editor.apply();
    }

    public void onCheckboxClicked(View v) throws IOException {
        home = ((CheckBox) v).isChecked();
        Log.i("SOMETHING", home.toString());
        if (home) {
            try {
                searchBar.getView().setVisibility(View.GONE);
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    //commented out as we don't need updates we just need the initial location
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener); //check for changes

                    String bestProvider = locationManager.NETWORK_PROVIDER; //set the provider of the location (network is faster)

                    Location location = locationManager.getLastKnownLocation(bestProvider); //get the last known location

                    if (location != null) { // make sure that location is not null before operating on it
                        //SAVE COORDINATES
                        saveCoordinates((float) location.getLatitude(), (float) location.getLongitude());

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
                    } else {
                        Toast.makeText(this, "Issue finding your location please check your internet connection and try again.", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            locationManager.removeUpdates(mLocationListener);
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
        } else {
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
    }

    public void saveCoordinates(float lat, float lng) {
        SharedPreferences location_info = getApplicationContext().getSharedPreferences("user_location", Context.MODE_PRIVATE); //save the lat/lng of the user
        SharedPreferences.Editor e = location_info.edit();
        e.putFloat("latitude", lat);
        e.putFloat("longitude", lng);
        e.commit();
    }


    private int getIndex(Spinner spinner, String value) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                index = i;
            }
        }
        return index;
    }
}