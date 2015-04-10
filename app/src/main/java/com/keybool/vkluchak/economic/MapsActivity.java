package com.keybool.vkluchak.economic;

import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity {

    SupportMapFragment mapFragment;
    GoogleMap map;
    private Marker marker;
    final String LOG_TAG = "myLogs";

    DB db;

    private LocationManager locationManager;
    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapactivity);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        map = mapFragment.getMap();
        if (map == null) {
            finish();
            return;
        }
        // откриваем подлючение к ДБ
        db = new DB(this);
        db.open();


    init();
        //getAllLoc();
    }

    public void getAllLoc(){

        Cursor cursor = db.getAllData();


        if (cursor.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int nameColIndex = cursor.getColumnIndex(DB.COLUMN_NAME);
            int courseColIndex = cursor.getColumnIndex(DB.COLUMN_COURSE);
            int amountColIndex = cursor.getColumnIndex(DB.COLUMN_AMOUNT);
            int phoneColIndex = cursor.getColumnIndex(DB.COLUMN_PHONE);
            int corColIndex = cursor.getColumnIndex(DB.COLUMN_CLICKS);

            do {
                        String name = cursor.getString(nameColIndex);
                        String snippet = cursor.getString(courseColIndex) + ", amount = "
                                + cursor.getString(amountColIndex) + ", phone = "
                                + cursor.getString(phoneColIndex);
                        String location = cursor.getString(corColIndex);
                String latitude = location.split(",")[0]; //
                Log.d(LOG_TAG, "latitude " + latitude+" longitude "+ location);
                String longitude = location.split(",")[1];
                createMarker(latitude,longitude,name, snippet);
            } while (cursor.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
    }
    private void createMarker(String latitude,String longitude, String name, String snippet){

        Log.d(LOG_TAG, "latitude " + latitude+" longitude "+ longitude);
        //create initial marker
        marker = map.addMarker( new MarkerOptions().position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_marker))
                        .title(name)
                        .snippet(snippet)
        );

        //marker.showInfoWindow();
    }
    private void init() {
        // долгое нажатие
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d(LOG_TAG, "onMapLongClick: " + latLng.latitude + "," + latLng.longitude);

                if(checkEnabled())
                    createMarker(latLng);
            }
        });
    }
    private void createMarker(LatLng latLng){
        //create initial marker
        marker = map.addMarker( new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude))
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_marker))
                        .title("Exchange")
                        .snippet("Drag to location of exchange")
                        .draggable(true)
        );

        marker.showInfoWindow();
    }

    public void onClickTest(View view) {
        //map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        switch(view.getId()){
/*            case R.id.btnZoom1:
                CameraUpdate cameraUpdate = CameraUpdateFactory.zoomIn();
                map.animateCamera(cameraUpdate);
                break;
            case R.id.btnZoom2:
                CameraUpdate cameraUpdate2 = CameraUpdateFactory.zoomOut();
                map.animateCamera(cameraUpdate2);
                break;
*/            case R.id.btnTest:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.btnSelect:
                Intent output = new Intent();
                LatLng latLng = marker.getPosition();
                String s = GeoCoderUtil.getAddress(latLng, this);
                output.putExtra("address", s + " -" + Double.toString(latLng.latitude)
                        + "," + Double.toString(latLng.longitude) + "-");
                setResult(RESULT_OK, output);
                finish();
                break;
            case R.id.btnShow:
                getAllLoc();
                break;
        }
    }


    private void showLocation(Location location) {
        if (location == null)
            return;
        if (checkEnabled()){
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(15)
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.animateCamera(cameraUpdate);

            map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).icon(
                    BitmapDescriptorFactory.fromResource(R.mipmap.marker_my_location)));
        }
    }

    public String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }



    private boolean checkEnabled() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                return true;
            }else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                return true;
            } else return false;
    }



    @Override
    protected void onResume() {
        super.onResume();
        // параметри(тип провайдера, минимум ждать для получения данних, метров для смени значения)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);
        checkEnabled();
    }
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }
// --------------------------Lisener ---------------------------------------------
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //map.clear();
            showLocation(location);// свой метод
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }
    };
}
