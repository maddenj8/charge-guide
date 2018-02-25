package com.dcuproject.jmadden.chargeguide;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.*;

import com.google.android.gms.maps.model.Polyline;
import com.jcraft.jsch.*;




public class MapMain extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private ImageButton hamburger;
    private PlaceAutocompleteFragment autocompleteFragment; // the searchbar at the top
    private SharedPreferences user_info; // user info that was obtained by the setup process
    private MarkerOptions markerOptions;
    private Marker destination; // the marker that has to be updated
    private Boolean destinationUpdated = false; // check if app has to draw a new marker or update an existing one
    private AutocompleteFilter autocompleteFilter;
    private CustomInfoWindow customInfoWindow;
    private Float user_lat;
    private Float user_long;
    private ViewGroup infoWindow;
    private Button infoButton;
    private String line = null;
    private String make;
    private String model;
    private String pathStr;
    private Thread downloadThread;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private List<Polyline> polylines;


    // Key for Google directions API
    private String directionsKey = "AIzaSyD0tlhhO3qg6QqbXESkGbiSO_j9ciDG0JU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_main);

        //checks if the setup process if complete
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);
        String setupComplete = sharedPref.getString("Setup_complete", "false");

        //show the applicable chargers in Ireland
        sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);
        make  = sharedPref.getString("selectedMake" , "");
        model  = sharedPref.getString("selectedModel" , "");

        //download the charger info on a separate thread to the main thread
        try {
            downloadThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    downloadChargerInfo();
                }
            });
            downloadThread.start();
        } catch (Exception e) {e.printStackTrace();}

        if (!setupComplete.equals("true")) {
            startActivity(new Intent(getApplicationContext(), first_launch.class));
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //handlers for the navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        //inflating the layout that is used for the custom info window
        infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.info_window_layout, null);
        infoButton = (Button) infoWindow.findViewById(R.id.moreInfo);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //adds functionality to the hamburger button
        hamburger = (ImageButton) (findViewById(R.id.hamburger));
        hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) (findViewById(R.id.drawer_layout));

                drawer.openDrawer(Gravity.START);
            }
        });

        //adds a filter to the search bar and handles a place selected
        autocompleteFilter = new AutocompleteFilter.Builder().setCountry("UK").setCountry("Irl").build(); // make a filter for the searchbar (uk and ireland only)
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setFilter(autocompleteFilter); // apply the filter
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
               // Log.i("PLACE TEST", place.getName().toString());
                if (destinationUpdated) {
                    destination.setPosition(place.getLatLng()); // if there is a marker just update the position
                }
                else { // else make the new marker
                    destinationUpdated = true; // tells the program that a marker is made so just update the position in future
                    markerOptions = new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)).anchor(0.5f, 1);
                    destination = mMap.addMarker(markerOptions); // add the newly made marker to the map
                }
                try {
                    for (Polyline polyline : polylines) {
                        polyline.remove();
                    }
                } catch(Exception e) {e.printStackTrace();}
                Object dataTransfer[];
                dataTransfer = new Object[3];
                String url = getDirectionsURL(new LatLng(user_lat, user_long), place.getLatLng());
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                getDirectionsData.execute(dataTransfer);
                polylines = getDirectionsData.polylines;
                for (Marker marker:markers) {
                    double distance = getDistance(marker.getPosition().latitude, marker.getPosition().longitude);
                    if (!(distance > 100 && distance < 120)) {
                         marker.remove();
                    }
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
        user_lat = user_info.getFloat("latitude", 9999); // 9999 is to make sure the value returned when there is no value set is not
        user_long = user_info.getFloat("longitude", 9999); // mistaken for a coordinate (example if -1 was used for error it's also a coordinate)
        // Add a marker in Sydney and move the camera
        LatLng ireland = new LatLng(53.433333, -7.95); // position for the camera
        LatLng userLocation = new LatLng(user_lat, user_long); // LatLng of the users positions
       // Log.i("USER LOCATION", user_lat.toString() + " " + user_long.toString());

        //Adapter to handle the infoWindow
        customInfoWindow = new CustomInfoWindow(this);
        mMap.setInfoWindowAdapter(customInfoWindow);

        //add marker and adjust camera
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Home").anchor(0.5f, 1)); // set a marker for user location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ireland, 6.5f)); //animate camera towards Ireland

        Toast.makeText(this, "Make is " + make + " and Model is " + model, Toast.LENGTH_SHORT).show();
        //add the charger pins of the chargers that are applicable to the car the user drives
        pinDrop();

    }

    public String getJSONResponse(String strUrl) {
        return "";
    }

    public String getDirectionsURL(LatLng userPosition, LatLng destPosition) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=";
        url += userPosition.latitude + "," + userPosition.longitude;
        url += "&destination=" + destPosition.latitude + "," + destPosition.longitude;
        url += "&key=" + directionsKey;
        return url;
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

        //noinspection SimplifiableIfStatement//compile 'com.github.ar-android:DrawRouteMaps:1.0.0'
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

    public void downloadChargerInfo() {


        //get the directory to store the downloaded charger info
        File path = getApplicationContext().getFilesDir();
        pathStr = path.toString() + "/plug.txt";
        Log.d("path ", pathStr);

        try {
            JSch ssh = new JSch();
            Session session = ssh.getSession("nugenc12", "student.computing.dcu.ie", 22);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword("5yv68ain");
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();

            ChannelSftp sftp = (ChannelSftp) channel;

            //sftp.cd("/users/case3/nugenc12/kml_parse/");

            if( make.equals("Nissan")){
                sftp.get("/users/case3/nugenc12/kml_parse/chademo_output.txt" , pathStr);
            }
            else if ( make.equals("Renault")){
                sftp.get("/users/case3/nugenc12/kml_parse/ac_output.txt" , pathStr);
            }

            else{
                sftp.get("/users/case3/nugenc12/kml_parse/ccs_output.txt" , pathStr);
            }

            channel.disconnect();
            session.disconnect();
        } catch ( Exception e) {
            System.out.println(e.getMessage().toString());
            e.printStackTrace();
        }
    }

    public double getDistance(double charger_lat, double charger_long) {
            Location chargerLoc = new Location("charger_Location");
            Location userLocation = new Location("user_Location");
            chargerLoc.setLatitude(charger_lat);
            chargerLoc.setLongitude(charger_long);
            userLocation.setLatitude(user_lat);
            userLocation.setLongitude(user_long);
            return userLocation.distanceTo(chargerLoc) / 1000 * 1.17; // convert to km and add a fudge factor
    }

    public void pinDrop () {
        // wait for the download to complete before trying to add the markers
        try {
            downloadThread.join();
        } catch(Exception e) {e.printStackTrace();}

        BufferedReader reader;
        try{

            //open an input stream for the downloaded charger info
            FileInputStream fileInputStream = new FileInputStream (new File(pathStr));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            reader = new BufferedReader(inputStreamReader);
            line = reader.readLine();

            //while there are still chargers to show
            while(line != null){

                //parse the charger details into a more readable state
                final String [] charger_Info = line.split("\\|"); //
                String charger_Name = charger_Info[0];
                String latlon = charger_Info[1];
                String state = charger_Info[2];
                String stateEnds = state.charAt(state.length() -4) + "";
                String placeOutput = "";
                final String [] chargeSplit = charger_Name.split(", ");
                String title = chargeSplit[0];

                title = title.replace("amp;" , ""); // because the python script brakes when this is done
                for (int i = 1; i < chargeSplit.length - 2; i++) {
                    placeOutput = placeOutput + chargeSplit[i] + "\n";
                }

                placeOutput += chargeSplit[chargeSplit.length - 2];

                //parsing the latlng of each of the chargers
                String [] split_Lat_Lon = latlon.split(",");
                final double charger_lat = Double.parseDouble(split_Lat_Lon[0]);
                final double charger_lon = Double.parseDouble(split_Lat_Lon[1]);
                LatLng chargerLocation = new LatLng(charger_lon, charger_lat); // LatLng of the chargers positions

                if(state.contains("Available")){
                    state = "Available";
                    markers.add(mMap.addMarker(new MarkerOptions().position(chargerLocation).title(title).snippet(placeOutput + "\n" + state).icon(BitmapDescriptorFactory.fromResource(R.drawable.green_charger)).anchor(0.3f, 1)));
                }
                else if (state.contains("Occupied")){
                    state = "Occupied";
                    markers.add(mMap.addMarker(new MarkerOptions().position(chargerLocation).title(title).snippet(placeOutput + "\n" + state).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_charger)).anchor(0.5f, 1)));
                }

                else if (state.contains("Out-of-Service")){
                    state = "Out of Service";
                    markers.add(mMap.addMarker(new MarkerOptions().position(chargerLocation).title(title).snippet(placeOutput + "\n" + state).icon(BitmapDescriptorFactory.fromResource(R.drawable.red_charger)).anchor(0.5f, 1)));
                }

                else {
                    state = "Out of Contact";
                    markers.add(mMap.addMarker(new MarkerOptions().position(chargerLocation).title(title).snippet(placeOutput + "\n" + state).icon(BitmapDescriptorFactory.fromResource(R.drawable.gray_charger)).anchor(0.5f, 1)));
                }

                line = reader.readLine();

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (!marker.getTitle().equals("Home")) {
                            Double distance = getDistance(marker.getPosition().latitude, marker.getPosition().longitude);

                            Intent intent = new Intent(MapMain.this, chargerInfo.class);
                            Bundle bundle = new Bundle();
                            //final Double dist = distance;
                            bundle.putString("chargerTitle", marker.getTitle().toString());
                            bundle.putString("chargerSnippet", marker.getSnippet().toString());
                            bundle.putDouble("lat", marker.getPosition().latitude);
                            bundle.putDouble("lon", marker.getPosition().longitude);
                            bundle.putDouble("distance", distance);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            //another comment
                        }
                    }
                });
            }
        } catch(IOException ioe){
            ioe.printStackTrace();
            Toast.makeText(getApplicationContext(),"Charger not Found" , Toast.LENGTH_LONG).show();
        }
    }
}
