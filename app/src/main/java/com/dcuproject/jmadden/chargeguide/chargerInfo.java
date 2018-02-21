package com.dcuproject.jmadden.chargeguide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class chargerInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charger_info);

        TextView title = (TextView) findViewById(R.id.heading_charger);
        TextView subHeading = (TextView) findViewById(R.id.subheading_charger);
        TextView status = (TextView) findViewById(R.id.status_charger);
        ImageView icon = (ImageView) findViewById(R.id.status_icon);

        Intent receive = getIntent();
        Bundle bundle = receive.getBundleExtra("lat");
        title.setText(bundle.getString("title"));
        //just a test to get items from the main class to the about charger class 
    }
}
