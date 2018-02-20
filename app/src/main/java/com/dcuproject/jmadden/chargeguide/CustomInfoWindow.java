package com.dcuproject.jmadden.chargeguide;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
            else {
                statusIcon.setBackgroundResource(R.drawable.red_charger);
            }
            return view;
        } catch(Exception e) {
            Log.d("inflator not working", "NO IT DIDN'T WORK");
            e.printStackTrace();
        }
        return null;
    }
}
