package com.dcuproject.jmadden.chargeguide;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;



public class first_launch extends AppCompatActivity {
    private Spinner makes, model;
    private Button currentLocAsHome;
    private Button fin;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);



        makes = (Spinner) findViewById(R.id.make_dropdown);
        model = (Spinner) findViewById(R.id.model_dropdown);
        currentLocAsHome = (Button) findViewById(R.id.currentLocAsHome);
        fin = (Button) findViewById(R.id.enterApp);


        makes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected_make = makes.getSelectedItem().toString();

                if (selected_make.equals("BMW")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.BMW, android.R.layout.simple_spinner_dropdown_item);
                    model.setAdapter(adapter);

                } else if (selected_make.equals("Nissan")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.Nissan, android.R.layout.simple_spinner_dropdown_item);
                    model.setAdapter(adapter);


                } else if (selected_make.equals("VW")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.VW, android.R.layout.simple_spinner_dropdown_item);
                    model.setAdapter(adapter);

                } else if (selected_make.equals("Renault")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.Renault, android.R.layout.simple_spinner_dropdown_item);
                    model.setAdapter(adapter);
                } else if (selected_make.equals("Hyundai")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.Hyundai, android.R.layout.simple_spinner_dropdown_item);
                    model.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        model.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override

            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                String selected_make = model.getSelectedItem().toString();
                editor.putString("selectedMake", selected_make);
                editor.apply();


            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }


        });




/*

          currentLocAsHome.setOnClickListener(new View.OnClickListener(){
            public void onClick (View view){
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            Boolean home = currentLocAsHome.isActivated();




        }
        });

 */





        fin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                startActivity(new Intent(first_launch.this, MapMain.class));
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Setup_complete", "true");
                editor.apply();
            }


        });

    }








            }









