package com.dcuproject.jmadden.chargeguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MapMain extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private ImageButton hamburger;
    private PlaceAutocompleteFragment autocompleteFragment; // the searchbar at the top
    private SharedPreferences user_info; // user info that was obtained by the setup process
    private MarkerOptions markerOptions;
    private Marker destination; // the marker that has to be updated
    private Boolean destinationUpdated = false; // check if app has to draw a new marker or update an existing one
    private AutocompleteFilter autocompleteFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_main);

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);
        String config = sharedPref.getString("Setup_complete", "false");

        if (!config.equals("true")) {
            startActivity(new Intent(getApplicationContext(), first_launch.class));
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        hamburger = (ImageButton) (findViewById(R.id.hamburger));
        hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) (findViewById(R.id.drawer_layout));

                drawer.openDrawer(Gravity.START);
            }
        });

        autocompleteFilter = new AutocompleteFilter.Builder().setCountry("UK").setCountry("Irl").build(); // make a filter for the searchbar (uk and ireland only)
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setFilter(autocompleteFilter); // apply the filter
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("PLACE TEST", place.getName().toString());
                if (destinationUpdated) {
                    destination.setPosition(place.getLatLng()); // if there is a marker just update the position
                }
                else { // else make the new marker
                    destinationUpdated = true; // tells the program that a marker is made so just update the position in future
                    markerOptions = new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)).anchor(0.5f, 1);
                    destination = mMap.addMarker(markerOptions); // add the newly made marker to the map
                }
            }

            @Override
            public void onError(Status status) {
                Log.i("MAP PROBLEM", "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        user_info = getApplicationContext().getSharedPreferences("user_location", Context.MODE_PRIVATE);
        Float user_lat = user_info.getFloat("latitude", 9999); // 9999 is to make sure the value returned when there is no value set is not
        Float user_long = user_info.getFloat("longitude", 9999); // mistaken for a coordinate (example if -1 was used for error it's also a coordinate)
        // Add a marker in Sydney and move the camera
        LatLng ireland = new LatLng(53.433333, -7.95); // position for the camera
        LatLng userLocation = new LatLng(user_lat, user_long); // LatLng of the users positions
        Log.i("USER LOCATION", user_lat.toString() + " " + user_long.toString());
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Home")); // set a marker for user location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ireland, 6.5f)); //animate camera towards Ireland
        pinDrop("Chademo");


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


  @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        item.setChecked(true);
        int id = item.getItemId();

        if (id == R.id.nav_manage) {

            Intent intent = new Intent(getApplicationContext() , first_launch.class);
            startActivity(intent);



        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(getApplicationContext() , help.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
        }

//Toast.makeText(getApplicationContext(),"Somthing bad happended" , Toast.LENGTH_LONG).show();

//Toast.makeText(getApplicationContext(),"Somthing bad happended" , Toast.LENGTH_LONG).show();

    // /app/src/chademo_output.txt");
    // mMap.addMarker(new MarkerOptions().position(userLocation).title("Home"))

    public void pinDrop (String plug) {


        if (plug.equals("Chademo")){
            BufferedReader reader;

            try{
                final InputStream file = getAssets().open("chademo_output.txt");
                reader = new BufferedReader(new InputStreamReader(file));
                String line = reader.readLine();
                while(line != null){

                    String [] charger_Info = line.split("\\|"); //
                    String charger_Name = charger_Info[0];
                    String latlon = charger_Info[1];
                    String state = charger_Info[2];
                    String stateEnds = state.charAt(state.length() -2) + "";
                    Log.d( state.charAt(state.length() -2) + "", "pinDrop: ");

                    String [] split_Lat_Lon = latlon.split(",");
                    double charger_lat = Double.parseDouble(split_Lat_Lon[0]);
                    double charger_lon = Double.parseDouble(split_Lat_Lon[1]);
                    LatLng chargerLocation = new LatLng(charger_lon, charger_lat); // LatLng of the chargers positions

                    if("e".equals(stateEnds)){
                        state = "Available";
                        mMap.addMarker(new MarkerOptions().position(chargerLocation).title(charger_Name).icon(BitmapDescriptorFactory.fromResource(R.drawable.green_charger)).anchor(0.3f, 1));


                    }
                    else if ("t".equals(stateEnds)){
                        state = "Occupied";
                        mMap.addMarker(new MarkerOptions().position(chargerLocation).title(charger_Name).icon(BitmapDescriptorFactory.fromResource(R.drawable.red_charger)).anchor(0.5f, 1));


                    }
                    else{
                        state = "Out of Contact";
                        mMap.addMarker(new MarkerOptions().position(chargerLocation).title(charger_Name).icon(BitmapDescriptorFactory.fromResource(R.drawable.gray_charger)).anchor(0.5f, 1));

                    }


                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)).anchor(0.5f, 1);


                    line = reader.readLine();
                }
            } catch(IOException ioe){
                ioe.printStackTrace();
                Toast.makeText(getApplicationContext(),"Somthing bad happended" , Toast.LENGTH_LONG).show();
            }


            }
        }


    }





