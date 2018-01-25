package com.dcuproject.jmadden.chargeguide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

public class first_launch extends AppCompatActivity {
    private Spinner makes, model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);

        makes = (Spinner)findViewById(R.id.make_dropdown);

        makes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected_make = makes.getSelectedItem().toString();
                Log.d("sainity_check"  ,selected_make);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

    }
}


