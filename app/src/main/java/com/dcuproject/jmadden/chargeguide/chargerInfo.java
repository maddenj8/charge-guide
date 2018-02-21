package com.dcuproject.jmadden.chargeguide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class chargerInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charger_info);



        Bundle bundle = null;
        bundle = this.getIntent().getExtras();




        double lat = bundle.getDouble("lat");
        double lon = bundle.getDouble("lon");
        String chargerTitle = bundle.getString("chargerTitle");
        String  chargerSnipit = bundle.getString("chargerSnipit");
        Log.d("lat",lat + "");


        //just a test to get items from the main class to the about charger class 
    }
}
