package com.dcuproject.jmadden.chargeguide;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.Object;

import com.dcuproject.jmadden.chargeguide.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;


/**
 * Created by jmadden on 20/02/18.
 */

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {
    private View view;
    private Activity context;


    public CustomInfoWindow(Activity context) {
        this.context = context;

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }



    @Override
    public View getInfoContents(Marker marker) {
        try {
            view = context.getLayoutInflater().inflate(R.layout.info_window_layout, null);

            Button button = (Button) view.findViewById(R.id.moreInfo);
            TextView title = view.findViewById(R.id.tv_title);
            ImageView statusIcon = view.findViewById(R.id.status_icon);

            title.setText(marker.getTitle());
            TextView subText = view.findViewById(R.id.tv_subtitle);
            subText.setText(marker.getSnippet());


            if (subText.getText().toString().contains("Available")) {
                statusIcon.setBackgroundResource(R.drawable.green_charger);
            }
            else if (subText.getText().toString().contains("Occupied")) {
                statusIcon.setBackgroundResource(R.drawable.blue_charger);
            }
            else if (subText.getText().toString().contains("Out of Contact")){
                statusIcon.setBackgroundResource(R.drawable.gray_charger);
            }
            else if (subText.getText().toString().contains("Out of Service")){
                statusIcon.setBackgroundResource(R.drawable.red_charger);
            }

            else if (subText.getText().toString().contains("Destination")) {
                statusIcon.setBackgroundResource(R.drawable.flag);
                button.setText("Choose Path");

                return view;
            }
            else{
                statusIcon.setBackgroundResource(R.drawable.home);
            }

            if (!marker.getTitle().equals("Home")) {
                button.setVisibility(View.VISIBLE);
            }
            else {
                button.setVisibility(View.GONE);
            }

            return view;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;


    }


}


