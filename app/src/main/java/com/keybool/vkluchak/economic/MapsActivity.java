package com.keybool.vkluchak.economic;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity {

    SupportMapFragment mapFragment;
    GoogleMap map;
    final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapactivity);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        map = mapFragment.getMap();
        if (map == null) {
            finish();
            return;
        }
        init();
    }

    private void init() {
    }


    public void onClickTest(View view) {
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }
}
