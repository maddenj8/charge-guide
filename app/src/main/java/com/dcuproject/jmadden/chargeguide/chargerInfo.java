package com.dcuproject.jmadden.chargeguide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class chargerInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charger_info);


        Intent receive = getIntent();
        Bundle bundle = receive.getBundleExtra("lat");
        //just a test to get items from the main class to the about charger class 
    }
}
