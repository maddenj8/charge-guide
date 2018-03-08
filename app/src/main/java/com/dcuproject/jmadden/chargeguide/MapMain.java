package com.dcuproject.jmadden.chargeguide;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import  java.util.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.google.android.gms.maps.model.Polyline;
import com.jcraft.jsch.*;


public class MapMain extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private ImageButton hamburger;
    private PlaceAutocompleteFragment autocompleteFragment; // the searchbar at the top
    private SharedPreferences user_info; // user info that was obtained by the setup process
    private MarkerOptions markerOptions;
    public Marker destination; // the marker that has to be updated
    private Boolean destinationUpdated = false; // check if app has to draw a new marker or update an existing one
    private AutocompleteFilter autocompleteFilter;
    private CustomInfoWindow customInfoWindow;
    private Float user_lat;
    private Float user_long;
    private Marker userMarker;
    private ViewGroup infoWindow;
    private Button infoButton;
    private String line = null;
    private String make;
    private String model;
    private String pathStr;
    private Thread downloadThread;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private List<Polyline> polylines;
    private int[] colors;
    private Float range;
    private int colorSelected;
    private SharedPreferences sharedPref;
    // Key for Google directions API
    private String directionsKey = "AIzaSyD0tlhhO3qg6QqbXESkGbiSO_j9ciDG0JU";
    private EditText socMainEdittext;
    private Float socMain;
    private Button apply;
    private Float kwh;
    private TreeMap tm;
    private ArrayList<Marker> firstChargers;
    private List<Marker> route;
    public List<List<Marker>> routes;
    private Button route0;
    private Button route1;
    private Button route2;
    private Button route3;
    private double distance1;
    private double distance2;
    private double distance3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_main);//
        colors = new int[]{Color.RED, Color.BLUE, Color.GREEN};

        route0 = (Button) findViewById(R.id.route0);
        route1 = (Button) findViewById(R.id.route1);
        route2 = (Button) findViewById(R.id.route2);
        route3 = (Button) findViewById(R.id.route3);


        route0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "it is being pressed", Toast.LENGTH_SHORT).show();

                String url = "https://www.google.com/maps/dir/?api=1";
                url += "&origin=" + String.valueOf(user_lat) + "," + String.valueOf(user_long);
                url += "&destination=" + String.valueOf(destination.getPosition().latitude) + "," + String.valueOf(destination.getPosition().longitude);
                url += "&travelmode=driving";
                url += "&waypoints=";

                Uri gmmIntentUri = Uri.parse(url);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mapIntent.setPackage("com.google.android.apps.maps");

                startActivity(mapIntent);
            }
        });


        route1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "it is being pressed", Toast.LENGTH_SHORT).show();
                try {
                    String url = buildMapUrl(routes.get(0));
                    Log.i("url", url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                } catch(Exception e) {e.printStackTrace();}
            }
        });

        route2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String url = buildMapUrl(routes.get(1));
                    Log.i("url", url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                } catch(Exception e) {e.printStackTrace();}
            }
        });

        route3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String url = buildMapUrl(routes.get(2));
                    Log.i("url", url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                } catch(Exception e) {e.printStackTrace();}
            }
        });
        //checks if the setup process if complete

        final SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);
        String setupComplete = sharedPref.getString("Setup_complete", "false");

        //get the make and model of the user
        make = sharedPref.getString("selectedMake", "");
        model = sharedPref.getString("selectedModel", "");
        kwh = sharedPref.getFloat("kwh", 9999);
        //get the soc from the user
        socMainEdittext = (EditText) (findViewById(R.id.socIntMain));
        try {
            range = sharedPref.getFloat("range", 100);
        } catch(Exception e) {e.printStackTrace();}
        final Float range = sharedPref.getFloat("range", 0);

        Log.d("range", range + "");

        //download the charger info on a separate thread to the main thread
        try {
            downloadThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    downloadChargerInfo();
                }
            });
            downloadThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("setup ", setupComplete);
        if (!setupComplete.equals("true")) {
            startActivity(new Intent(getApplicationContext(), first_launch.class));
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //handlers for the navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        //inflating the layout that is used for the custom info window
        infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.info_window_layout, null);
        infoButton = (Button) infoWindow.findViewById(R.id.moreInfo);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        apply = (Button) (findViewById(R.id.applySoc));
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socMain = Float.valueOf(socMainEdittext.getText().toString());
                if (socMain >= 0 && socMain <= 100) {
                    socMain *= (kwh / 100) * 6;
                    SharedPreferences.Editor e = sharedPref.edit();
                    Toast.makeText(getApplicationContext(), "You have set range to " + String.valueOf(socMain) + " Km", Toast.LENGTH_SHORT).show();
                    e.putFloat("range", socMain);
                    e.commit();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a number between 0 and 100", Toast.LENGTH_SHORT).show();
                }
            }
        });
        apply.requestFocus();

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
        autocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Thread clearTags = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (List<Marker> route : routes) {
                                for (Marker marker : route) {
                                    marker.setTag(null);
                                }
                            }
                        }
                    });
                    clearTags.run();
                    mMap.clear();
                    addMarker(userMarker);
                    markers.remove(markers.size() - 1);
                    autocompleteFragment.setText("");
                    view.setVisibility(View.GONE);
                    route0.setVisibility(View.GONE);
                    route1.setVisibility(View.GONE);
                    route2.setVisibility(View.GONE);
                    route3.setVisibility(View.GONE);
                    SharedPreferences path = getApplicationContext().getSharedPreferences("path", MODE_PRIVATE);
                    SharedPreferences.Editor pathEdit = path.edit();

                    pathEdit.clear();
                    pathEdit.commit();

                    for (Marker marker : markers) {
                        addMarker(marker);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                tm = new TreeMap();
                // Log.i("PLACE TEST", place.getName().toString());
                if (destinationUpdated) {
                    destination.setPosition(place.getLatLng()); // if there is a marker just update the position
                    destination.setTitle(place.getName().toString());
                } else { // else make the new marker
                    destinationUpdated = true; // tells the program that a marker is made so just update the position in future
                    markerOptions = new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()).snippet("Destination").icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)).anchor(0.5f, 1);
                    destination = mMap.addMarker(markerOptions);

                    markers.add(mMap.addMarker(markerOptions)); // add the newly made marker to the map
                }

                //if the user can reach the destination without charging
                if (getDistance(place.getLatLng().latitude, place.getLatLng().longitude) < range) {
                    mMap.clear();
                    addMarker(destination);
                    addMarker(userMarker);
                    Toast.makeText(getApplicationContext(), "You should reach your destination without charging", Toast.LENGTH_SHORT).show();
                    startDirectionsSteps(new LatLng(user_lat, user_long), place.getLatLng(), colors[0]);
                    route0.setVisibility(View.VISIBLE);

                    //else find a charger to stop at
                }

                else { //else find a charger to stop at
                    if (!(user_lat == 9999)) {
                        int count = 0;
                        mMap.clear();
                        for (Marker marker : markers) {
                            double distance = getDistance(marker.getPosition().latitude, marker.getPosition().longitude);
                        }
                    }
                    try {
                        findOptimalChargers();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    route0.setVisibility(View.GONE);
                    if (routes.size() >= 1) {
                        route1.setVisibility(View.VISIBLE);
                    }
                    if (routes.size() >= 2) {
                        route2.setVisibility(View.VISIBLE);
                    }
                    if (routes.size() >= 3) {
                        route3.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onError(Status status) {

            }
        });
    }

    public String buildMapUrl(List<Marker> route) {
        String url = "https://www.google.com/maps/dir/?api=1";
        url += "&origin=" + String.valueOf(user_lat) + "," + String.valueOf(user_long);
        url += "&destination=" + String.valueOf(destination.getPosition().latitude) + "," + String.valueOf(destination.getPosition().longitude);
        url += "&travelmode=driving";
        url += "&waypoints=";
        for (Marker charger : route) {
            url += String.valueOf(charger.getPosition().latitude) + "," + String.valueOf(charger.getPosition().longitude) + "|";
        }
        url = url.substring(0, url.length() - 1);
        Log.i("URL", url);
        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
        return url;
    }

    public void downloadChargerInfo() {
        //get the directory to store the downloaded charger info
        File path = getApplicationContext().getFilesDir();
        pathStr = path.toString() + "/plug.txt";
        //Log.d("path ", pathStr);

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
                Log.i("GET HERE", make);
                sftp.get("/users/case3/nugenc12/kml_parse/ccs_output.txt" , pathStr);
            }

            channel.disconnect();
            session.disconnect();
        } catch ( Exception e) {
            System.out.println(e.getMessage().toString());
            e.printStackTrace();
        }
    }


    public Marker getNextCharger(Marker currentCharger, double rangeAtEighty, int index, int tag) {
        double distance = getDistanceToDestination(currentCharger.getPosition().latitude, currentCharger.getPosition().longitude, destination.getPosition().latitude, destination.getPosition().longitude);
        if (distance < rangeAtEighty || currentCharger == null) {
            startDirectionsSteps(currentCharger.getPosition(), destination.getPosition(), colors[index]);
            return null;
        }
        for (Iterator i = tm.keySet().iterator(); i.hasNext(); ) {
            Marker nextCharger = (Marker) tm.get(i.next());
            double distToCharger = getDistanceToDestination(currentCharger.getPosition().longitude, currentCharger.getPosition().latitude, nextCharger.getPosition().longitude, nextCharger.getPosition().latitude);
            if (distToCharger < rangeAtEighty && currentCharger != nextCharger) {
                nextCharger.setTag(tag);
                addMarker(nextCharger);
                startDirectionsSteps(currentCharger.getPosition(), nextCharger.getPosition(), colors[index]);
                return nextCharger;
            }
        }
        return null;
    }

    public void buildMap(LatLng startPos, double range, int index) {
        for (Marker marker : markers) {
            double distance = getDistanceToDestination(startPos.latitude, startPos.longitude, marker.getPosition().latitude, marker.getPosition().longitude);

            if (marker.getSnippet() != null) {

                //if the charger is reachable regardless of direction
                if (range > distance && (marker.getSnippet().contains("Available") || marker.getSnippet().contains("Occupied"))) { //if you can get to the charger
                    colorSelected = colors[index % 3];
                    addToMap(marker);
                }
            }
        }
    }



    public void addToMap(Marker marker) { //only pick the ones that are in the direction of the destination
        try {

            Location destLocation = new Location("destLocation");
            destLocation.setLatitude(destination.getPosition().latitude);
            destLocation.setLongitude(destination.getPosition().longitude);

            Location markerLocation = new Location("markerLocation");
            markerLocation.setLatitude(marker.getPosition().latitude);
            markerLocation.setLongitude(marker.getPosition().longitude);

            double distToLocation = destLocation.distanceTo(markerLocation) / 1000 * 1.17;
            //sort by the distance the charger is from the destination to take direction
            //into account
            tm.put(distToLocation, marker);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startDirectionsSteps(LatLng start, LatLng place, int colorSelected) {
        Object dataTransfer[];
        dataTransfer = new Object[4];
        String url = getDirectionsURL(start, place);
        GetDirectionsData getDirectionsData = new GetDirectionsData();
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        dataTransfer[2] = new LatLng(place.latitude, place.longitude);
        dataTransfer[3] = colorSelected;
        getDirectionsData.execute(dataTransfer);
        polylines = getDirectionsData.polylines;
    }

    //if you have a marker made already call this function to redraw it
    public void addMarker(Marker marker) {
        if (marker.getTitle().contains("Home")) {
            mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()).anchor(0.3f, 1));
        } else if (marker.getSnippet().contains("Destination")) {
            mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()).snippet(marker.getSnippet()).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)).anchor(0.3f, 1));
        } else if (marker.getSnippet().contains("Available")) {
            mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()).snippet(marker.getSnippet()).icon(BitmapDescriptorFactory.fromResource(R.drawable.green_charger)).anchor(0.3f, 1));
        } else if (marker.getSnippet().contains("Occupied")) {
            mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()).snippet(marker.getSnippet()).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_charger)).anchor(0.5f, 1));
        } else if (marker.getSnippet().contains("Out of Service")) {
            mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()).snippet(marker.getSnippet()).icon(BitmapDescriptorFactory.fromResource(R.drawable.red_charger)).anchor(0.5f, 1));
        } else {
            mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()).snippet(marker.getSnippet()).icon(BitmapDescriptorFactory.fromResource(R.drawable.gray_charger)).anchor(0.5f, 1));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        user_info = getApplicationContext().getSharedPreferences("user_location", Context.MODE_PRIVATE);
        user_lat = user_info.getFloat("latitude", 9999); // 9999 is to make sure the value returned when there is no value set is not
        user_long = user_info.getFloat("longitude", 9999); // mistaken for a coordinate (example if -1 was used for error it's also a
        Log.i("start_location", String.valueOf(user_lat) + " " + String.valueOf(user_long));

        try {
            Log.i("distance", String.valueOf(getDistance(53.62549, -7.466440000000034)));
        } catch(Exception e) {e.printStackTrace();}

        // Add a marker in Sydney and move the camera
        LatLng ireland = new LatLng(53.433333, -7.95); // position for the camera
        LatLng userLocation = new LatLng(user_lat, user_long); // LatLng of the users positions
        // Log.i("USER LOCATION", user_lat.toString() + " " + user_long.toString());

        //Adapter to handle the infoWindow
        customInfoWindow = new CustomInfoWindow(this);
        mMap.setInfoWindowAdapter(customInfoWindow);

        //add marker and adjust camera
        userMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("Home").anchor(0.5f, 1)); // set a marker for user location
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
        url += "&sensor=false";
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

    public void findOptimalChargers() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        firstChargers = new <Marker>ArrayList();
        int index = 1;
        Toast.makeText(getApplicationContext(), String.valueOf(range), Toast.LENGTH_SHORT).show();
        double rangeAtEighty = 80 * (kwh / 100) * 6; //if the user stops at a charger use this range instead
        int count = 0;
        mMap.clear();

        buildMap(new LatLng(user_lat, user_long), range, index);

        //Bundle pathBundel = new Bundle();
        try {
            // trys++; // limits the number of hops to 3;
            int limit = 0;
            Set keys = tm.keySet();
            addMarker(destination);
            addMarker(userMarker);

            for (Iterator i = keys.iterator(); i.hasNext(); ) {
                //only pick the top three

                if (limit > 2) {
                    break;
                }
                double key = (double) i.next();
                Marker marker = (Marker) tm.get(key);
                marker.setTag(limit); //show what route it is apart of
                firstChargers.add(marker);
                LatLng mkPosition = marker.getPosition();
                double mklat = mkPosition.latitude;
                double mklon = mkPosition.longitude;


                LatLng desPos = destination.getPosition();
                double deslat = desPos.latitude;
                double deslon = desPos.longitude;

                double distToDestnaiton = getDistanceToDestination(mklat, mklon, deslat, deslon);
                double homeToCharger = getDistance(mklat, mklon);

                //if (distToDestnaiton < range) {
                Log.d("key", distToDestnaiton + "");


                mMap.setInfoWindowAdapter(customInfoWindow);

                addMarker(marker); // a full charge form the charger will get you to your destnation
                startDirectionsSteps(new LatLng(user_lat, user_long), marker.getPosition(), colors[limit]);

                String stringIndex = Integer.toString(index);
                double totalDistance = distToDestnaiton + homeToCharger;

                ArrayList<Double> journyInfo = new ArrayList<>();
                journyInfo.add(totalDistance);

                editor.putFloat(stringIndex + "chargerLat", ((float) mklat));
                editor.putFloat(stringIndex + "chargerLon", ((float) mklon));
                editor.putFloat("destLat", ((float) deslat));
                editor.putFloat("destLon", ((float) deslon));
                editor.putFloat(stringIndex + "distance", ((float) totalDistance));

                editor.commit();

                count++;
                limit++;
            }
            limit = 0;
            routes = new ArrayList<>();
            //Marker currentCharger = firstChargers.get(2);
            for (Marker currentCharger : firstChargers) {
                route = new ArrayList<>();
                route.add(currentCharger);
                tm = new TreeMap();
                buildMap(currentCharger.getPosition(), rangeAtEighty, index);
                double currDistToDest = getDistanceToDestination(currentCharger.getPosition().longitude, currentCharger.getPosition().latitude, destination.getPosition().longitude, destination.getPosition().latitude);
                if (currDistToDest < rangeAtEighty) {
                    startDirectionsSteps(currentCharger.getPosition(), destination.getPosition(), colors[limit]);
                    routes.add(route);
                    limit++;
                } else {
                    try {
                        Marker prevM = userMarker;
                        Marker m = getNextCharger(route.get(route.size() - 1), rangeAtEighty, limit, (int) currentCharger.getTag());
                        double totalDistance = getDistanceToDestination(userMarker.getPosition().latitude, userMarker.getPosition().longitude, m.getPosition().latitude, m.getPosition().longitude);
                        if (m != null) {
                            totalDistance += getDistanceToDestination(m.getPosition().latitude, m.getPosition().longitude, prevM.getPosition().latitude, prevM.getPosition().longitude);
                            prevM = m;
                            route.add(m);
                        }

                        buildMap(route.get(route.size() - 1).getPosition(), rangeAtEighty, limit);
                        m = getNextCharger(route.get(route.size() - 1), rangeAtEighty, limit, (int) currentCharger.getTag());
                        if (m != null) {
                            totalDistance += getDistanceToDestination(m.getPosition().latitude, m.getPosition().longitude, prevM.getPosition().latitude, prevM.getPosition().longitude);
                            prevM = m;
                            route.add(m);
                        }

                        buildMap(route.get(route.size() - 1).getPosition(), rangeAtEighty, limit);
                        m = getNextCharger(route.get(route.size() - 1), rangeAtEighty, limit, (int) currentCharger.getTag());
                        if (m != null) {
                            totalDistance += getDistanceToDestination(m.getPosition().latitude, m.getPosition().longitude, prevM.getPosition().latitude, prevM.getPosition().longitude);
                            prevM = m;
                            route.add(m);
                        }

                        buildMap(route.get(route.size() - 1).getPosition(), rangeAtEighty, limit);
                        m = getNextCharger(route.get(route.size() - 1), rangeAtEighty, limit, (int) currentCharger.getTag());
                        if (m != null) {
                            totalDistance += getDistanceToDestination(m.getPosition().latitude, m.getPosition().longitude, prevM.getPosition().latitude, prevM.getPosition().longitude);
                            prevM = m;
                            route.add(m);
                        }

                        buildMap(route.get(route.size() - 1).getPosition(), rangeAtEighty, limit);
                        m = getNextCharger(route.get(route.size() - 1), rangeAtEighty, limit, (int) currentCharger.getTag());
                        if (m != null) {
                            totalDistance += getDistanceToDestination(m.getPosition().latitude, m.getPosition().longitude, prevM.getPosition().latitude, prevM.getPosition().longitude);
                            prevM = m;
                            route.add(m);
                        }
                        Log.i("totalDistance", String.valueOf(totalDistance));
                    } catch (Exception e) {e.printStackTrace();}
                }
                routes.add(route);
                limit++;
                index++;
            }
            addSelectedChargers();
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        item.setChecked(true);
        int id = item.getItemId();

        if (id == R.id.nav_manage) {

            Intent intent = new Intent(getApplicationContext(), first_launch.class);
            startActivity(intent);

        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(getApplicationContext(), help.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addSelectedChargers() {
        for (List<Marker> route : routes) {
        }
    }

    public double getDistance(double charger_lat, double charger_long) {
        Location chargerLoc = new Location("charger_Location");
        Location userLocation = new Location("user_Location");
        chargerLoc.setLatitude(charger_lat);
        chargerLoc.setLongitude(charger_long);
        userLocation.setLatitude(user_lat);
        userLocation.setLongitude(this.user_long);
        return userLocation.distanceTo(chargerLoc) / 1000 * 1.17; // convert to km and add a fudge factor
    }

    public static double getDistanceToDestination(double charger_lat, double charger_long, double destination_lat, double destination_lon) {
        Location chargerLoc = new Location("charger_Location");
        Location destination_location = new Location("destination_location");
        chargerLoc.setLatitude(charger_lat);
        chargerLoc.setLongitude(charger_long);
        destination_location.setLatitude(destination_lat);
        destination_location.setLongitude(destination_lon);
        return destination_location.distanceTo(chargerLoc) / 1000 * 1.17; // convert to km and add a fudge factor
    }


    public void pinDrop() {
        // wait for the download to complete before trying to add the markers
        try {
            downloadThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedReader reader;
        try {

            //open an input stream for the downloaded charger info
            FileInputStream fileInputStream = new FileInputStream(new File(pathStr));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            reader = new BufferedReader(inputStreamReader);
            line = reader.readLine();

            //while there are still chargers to show
            while (line != null && line.length() > 12) {

                //parse the charger details into a more readable state
                final String[] charger_Info = line.split("\\|"); //

                String charger_Name = charger_Info[0];
                String latlon = charger_Info[1];
                String state = charger_Info[2];
                String stateEnds = state.charAt(state.length() - 4) + "";
                String placeOutput = "";
                final String[] chargeSplit = charger_Name.split(", ");
                String title = chargeSplit[0];

                title = title.replace("amp;", ""); // because the python script brakes when this is done
                for (int i = 1; i < chargeSplit.length - 2; i++) {
                    placeOutput = placeOutput + chargeSplit[i] + "\n";
                }

                placeOutput += chargeSplit[chargeSplit.length - 2];

                //parsing the latlng of each of the chargers
                String[] split_Lat_Lon = latlon.split(",");
                final double charger_lat = Double.parseDouble(split_Lat_Lon[0]);
                final double charger_lon = Double.parseDouble(split_Lat_Lon[1]);
                LatLng chargerLocation = new LatLng(charger_lon, charger_lat); // LatLng of the chargers positions

                if (state.contains("Available")) {
                    state = "Available";
                    markers.add(mMap.addMarker(new MarkerOptions().position(chargerLocation).title(title).snippet(placeOutput + "\n" + state).icon(BitmapDescriptorFactory.fromResource(R.drawable.green_charger)).anchor(0.3f, 1)));
                } else if (state.contains("Occupied")) {
                    state = "Occupied";
                    markers.add(mMap.addMarker(new MarkerOptions().position(chargerLocation).title(title).snippet(placeOutput + "\n" + state).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_charger)).anchor(0.5f, 1)));
                } else if (state.contains("Out-of-Service")) {
                    state = "Out of Service";
                    markers.add(mMap.addMarker(new MarkerOptions().position(chargerLocation).title(title).snippet(placeOutput + "\n" + state).icon(BitmapDescriptorFactory.fromResource(R.drawable.red_charger)).anchor(0.5f, 1)));
                } else {
                    state = "Out of Contact";
                    markers.add(mMap.addMarker(new MarkerOptions().position(chargerLocation).title(title).snippet(placeOutput + "\n" + state).icon(BitmapDescriptorFactory.fromResource(R.drawable.gray_charger)).anchor(0.5f, 1)));
                }

                line = reader.readLine();

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        //done to avoid null pointer issues
                        if (!marker.getTitle().equals("Home") && !marker.getSnippet().contains("Destination")) {
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
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Toast.makeText(getApplicationContext(), "Charger not Found", Toast.LENGTH_LONG).show();
        }
    }
}

