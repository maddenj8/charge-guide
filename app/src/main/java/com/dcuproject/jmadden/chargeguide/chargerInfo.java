package com.dcuproject.jmadden.chargeguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Calendar;
import java.util.Locale;

import static java.lang.Math.log;
import static java.lang.Math.round;

public class chargerInfo extends AppCompatActivity {

    double lat;
    double lon;
    //String make;
    String model;
    Float kwh;
    Float range;
    Double dist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charger_info);

        TextView title = (TextView) findViewById(R.id.heading_charger);
        TextView subHeading = (TextView) findViewById(R.id.subheader_charger);
        TextView status = (TextView) findViewById(R.id.status_text);
        ImageView icon = (ImageView) findViewById(R.id.status_image);
        final TextView distance = (TextView) findViewById(R.id.distance);
        final TextView arrivalTime = (TextView) findViewById(R.id.arrivalTime);




        //setting average speed as 90 kph

        try {
            Intent receive = getIntent();
            Bundle bundle = receive.getExtras();
            title.setText(bundle.getString("chargerTitle"));
            String subText = bundle.getString("chargerSnippet");
            dist = bundle.getDouble("distance");
            lat = bundle.getDouble("lat");
            lon = bundle.getDouble("lon");
            String distString = String.valueOf(dist);
            distString = distString.substring(0, distString.lastIndexOf(".") + 3); // two decimal points

            String[] splitText = subText.split("\n");
            String stats = splitText[splitText.length - 1];
            status.setText(stats);


            double hourdouble= dist / 90 ; // time in hours
            double totalSecs = hourdouble *60 * 60;
            double hours = totalSecs / 3600;
            double minutes = (totalSecs % 3600) / 60;
            Log.d("hour" , totalSecs + "");
            arrivalTime.setText(round(hours) + " hours" + " "+ round(minutes) +" minutes away");





            subText = "";
            for (int i = 0; i < splitText.length - 2; i++) {
                subText += splitText[i] + "\n";
            }
            subText += splitText[splitText.length - 2];
            subHeading.setText(subText);
            distance.setText(distString + " km away from Home");

            if (stats.equals("Available")) {
                Toast.makeText(this, "Available", Toast.LENGTH_SHORT).show();
                icon.setBackgroundResource(R.drawable.large_green_charger);
            } else if (stats.equals("Occupied")) {
                Toast.makeText(this, "Occupied", Toast.LENGTH_SHORT).show();
                icon.setBackgroundResource(R.drawable.large_blue_charger);
            } else if (stats.equals("Out of Contact")) {
                Toast.makeText(this, "Out of Contact", Toast.LENGTH_SHORT).show();
                icon.setBackgroundResource(R.drawable.large_gray_charger);
            } else if (stats.equals("Out of Service")) {
                Toast.makeText(this, "Out of Service", Toast.LENGTH_SHORT).show();
                icon.setBackgroundResource(R.drawable.large_red_charger);
            }
            //just a test to get items from the main class to the about charger class
        } catch (Exception e) {
            e.printStackTrace();
        }

        FloatingActionButton openGoogle = findViewById(R.id.openMaps);
        openGoogle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String strLat = lat + "";
                String strLon = lon + "";
                String mapUrl = "google.navigation:q=";
                mapUrl = mapUrl + strLat + "," + strLon;


                Uri gmmIntentUri = Uri.parse(mapUrl);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });


        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("usbattery_interPref", MODE_PRIVATE);
        sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);
        //make  = sharedPref.getString("selectedMake" , "");
        model = sharedPref.getString("selectedModel", "");

        final String battery = model.substring(model.length() - 6, model.length() - 4);
        final int battery_int = Integer.parseInt(battery);
        //Log.d("bat" , battery_int +"");

        final TextView chargeTime =  findViewById(R.id.timeTo80);
        final Button applybut = findViewById(R.id.apply);
        final EditText soc = findViewById(R.id.socInt);
        final TextView arrivalSoc = findViewById(R.id.arrivalsocInt);
        applybut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String socStr = soc.getText().toString();

                Float socInt = Float.parseFloat(socStr);

                if (socInt >= 0 && socInt <= 100) {
                    socInt = socInt / 100;
                    kwh = battery_int * socInt;

                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    range = kwh * 6;
                    editor.putFloat("range", range);
                    Log.v("range", range + "");

                    float KwhToFull =  battery_int- kwh;
                    // apoxomity 120 seconds per kwh to charge
                    int minToCharge  = round(KwhToFull) *2;
                    Log.d("mintocharge", minToCharge + "");

                    chargeTime.setText(battery_int + " minutes");

                    float tmp = round(range  - dist)/100;
                    Log.d("tmp" , tmp + "");
                    tmp = battery_int * tmp / 6;
                    arrivalSoc.setText(  tmp + "%");


                    if ( range  - dist < 20   &&  range  - dist >0){
                        Toast.makeText(getApplicationContext(), "You may not reach your destination"  , Toast.LENGTH_LONG).show();
                    }

                    if ( range  - dist < 0  ){
                        Toast.makeText(getApplicationContext(), "You are unlikely to reach your destination"  , Toast.LENGTH_LONG).show();
                    }



                    editor.commit();

                }

                else {

                    Toast.makeText(getApplicationContext(), "Enter a number between 0 and 100", Toast.LENGTH_LONG).show();
                }



            }
        });


    }
}