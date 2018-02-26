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

public class chargerInfo extends AppCompatActivity {

    double lat;
    double lon;
    //String make;
    String model;
    Float battery_soc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charger_info);

        TextView title = (TextView) findViewById(R.id.heading_charger);
        TextView subHeading = (TextView) findViewById(R.id.subheader_charger);
        TextView status = (TextView) findViewById(R.id.status_text);
        ImageView icon = (ImageView) findViewById(R.id.status_image);
        TextView distance = (TextView) findViewById(R.id.distance);


        try {
            Intent receive = getIntent();
            Bundle bundle = receive.getExtras();
            title.setText(bundle.getString("chargerTitle"));
            String subText = bundle.getString("chargerSnippet");
            Double dist = bundle.getDouble("distance");
            lat = bundle.getDouble("lat");
            lon = bundle.getDouble("lon");
            String distString = String.valueOf(dist);
            distString = distString.substring(0, distString.lastIndexOf(".") + 3); // two decimal points

            String[] splitText = subText.split("\n");
            String stats = splitText[splitText.length - 1];
            status.setText(stats);

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

        String battery = model.substring(model.length() - 6, model.length() - 4);
        final int battery_int = Integer.parseInt(battery);
        //Log.d("bat" , battery_int +"");

        final EditText soc = findViewById(R.id.socInt);
        soc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String socStr =soc.getText().toString();

                Float socInt = Float.parseFloat(socStr);

                if (socInt >=0  && socInt <=100 ){
                    socInt = socInt / 100;
                    battery_soc = battery_int * socInt;
                    Log.v("soc", battery_soc  +"");
                }

                else {

                    Toast.makeText(getApplicationContext() , "Enter a number between 0 and 100", Toast.LENGTH_LONG).show();

                }




            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }




}
