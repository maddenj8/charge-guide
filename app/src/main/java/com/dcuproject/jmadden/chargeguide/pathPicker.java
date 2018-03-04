package com.dcuproject.jmadden.chargeguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static java.lang.Math.round;

public class pathPicker extends AppCompatActivity {
    int option0 = -1;
    int option1 = -1;
    int option2 = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_picker);
        int [] colors = new int[] {Color.CYAN, Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA};
        final SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);

        Boolean boolPath0 = false;
        Boolean boolPath1 = false;
        Boolean boolPath2 = false;

       final Float  destLat  = sharedPref.getFloat("destLat"  ,0);
       final Float destLon  = sharedPref.getFloat("destLon"  ,0);



        float dist0 = sharedPref.getFloat("0" + "distance", 0);
        final float lat0 = sharedPref.getFloat("0" + "chargerLat", 0);
        final float lon0 = sharedPref.getFloat("0" + "chargerLon", 0);


        float dist1 = sharedPref.getFloat("1" + "distance", 0);
        final float lat1 = sharedPref.getFloat("1" + "chargerLat", 0);
        final float lon1 = sharedPref.getFloat("1" + "chargerLon", 0);


        float dist2 = sharedPref.getFloat("2" + "distance", 0);
        final float lat2 = sharedPref.getFloat("2" + "chargerLat", 0);
        final float lon2 = sharedPref.getFloat("2" + "chargerLon", 0);


        dist0 = round(dist0);
        dist1 = round(dist1);
        dist2 = round(dist2);

        TextView path0 = (TextView) (findViewById(R.id.path0));
        TextView path1 = (TextView) (findViewById(R.id.path1));
        TextView path2 = (TextView) (findViewById(R.id.path2));

        TextView distance0 = (TextView) (findViewById(R.id.distance0));
        TextView distance1 = (TextView) (findViewById(R.id.distance1));
        TextView distance2 = (TextView) (findViewById(R.id.distance2));

        Button sel0 = (Button) (findViewById(R.id.sel0));
        Button sel1 = (Button) (findViewById(R.id.sel1));
        Button sel2 = (Button) (findViewById(R.id.sel2));

        final Button des0 = (Button) (findViewById(R.id.des0));
        final Button des1 = (Button) (findViewById(R.id.des1));
        Button des2 = (Button) (findViewById(R.id.des2));


        Boolean distance0Set = false;
        Boolean distance1Set = false;


        int pathCount = 0;
        if (dist0 != 0) {
            pathCount++;
            boolPath0 = true;
        }
            if (dist1 != 0) {
                pathCount++;
                boolPath1 = true;

            }

            if (dist2 != 0) {
                pathCount++;
                boolPath2 = true;
            }
            Log.d("thing" , pathCount + "");
            if( pathCount < 3){

                sel2.setVisibility(View.GONE);
                des2.setVisibility(View.GONE);
                distance2.setVisibility(View.GONE);
                path2.setVisibility(View.GONE);
            }

            if( pathCount < 2){
                sel1.setVisibility(View.GONE);
                distance1.setVisibility(View.GONE);
                des1.setVisibility(View.GONE);
                path1.setVisibility(View.GONE);
            }


            if( boolPath0){
                distance0.setText(Float.toString(dist0) + " Km");

                path0.setText("Option 1");
                option0 = 0;
                distance0Set = true;
            }

            if(boolPath1){
                if(!distance0Set){
                    distance0.setText(Float.toString(dist1) + " Km");

                    path0.setText("Option 1");
                    option0 = 1;
                    distance0Set = true;

                }
                else{
                    distance1.setText(Float.toString(dist1) + " Km");
                    path1.setText("Option 2");
                    option1 = 1;
                    distance1Set = true;
                }
            }

            if(boolPath2){
                if(!distance0Set){
                    distance0.setText(Float.toString(dist2) + " Km");
                    path0.setText("Option 1");
                    option0 = 2;

                }
                else if(!distance1Set){
                    distance1.setText(Float.toString(dist2) + " Km");
                    path1.setText("Option 2");

                    option1 = 2;
                }

                else {
                    distance2.setText(Float.toString(dist2) + " km");

                    path2.setText("Option 3");
                    option2 = 2;
                }


            }


        sel0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


            if( option0 != -1) {

                toCharger(lat0 , lon0);

            }
            else if ( option1 != -1) {
                toCharger(lat1, lon1);
            }

            else if (option2 != -1){
                toCharger(lat2 , lon2);
                }
            }
        });

        sel1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                if( option0 != -1) {

                    toCharger(lat0 , lon0);

                }
                else if ( option1 != -1) {
                    toCharger(lat1, lon1);
                }

                else if (option2 != -1){
                    toCharger(lat2 , lon2);
                }
            }
        });

        sel2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                if( option0 != -1) {

                    toCharger(lat0 , lon0);

                }
                else if ( option1 != -1) {
                    toCharger(lat1, lon1);
                }

                else if (option2 != -1){
                    toCharger(lat2 , lon2);
                }
            }
        });


        des0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                    toDestination(destLat , destLon);
            }

        });

        des1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                toDestination(destLat , destLon);
                }

        });

        des2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                toDestination(destLat, destLon);
            }
        });

        }
        private void toCharger(float lat , float lon){
            String strLat = lat + "";
            String strLon = lon + "";
            String mapUrl = "google.navigation:q=";
            mapUrl = mapUrl + strLat + "," + strLon;


            Uri gmmIntentUri = Uri.parse(mapUrl);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            Log.d("url" ,mapUrl);
            startActivity(mapIntent);
        }

    private void toDestination(float destLat , float destLon){
        String mapUrl = "google.navigation:q=";
        mapUrl = mapUrl + destLat + "," + destLon;


        Uri gmmIntentUri = Uri.parse(mapUrl);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        Log.d("url" ,mapUrl);
        startActivity(mapIntent);
    }
    }