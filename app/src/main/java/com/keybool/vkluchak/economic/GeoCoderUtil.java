package com.keybool.vkluchak.economic;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by vkluc_000 on 09.04.2015.
 */
public class GeoCoderUtil {
    public static String getAddress(final LatLng latLng, final Context context) {
        final Geocoder geocoder = new Geocoder(context);
        final double latitude = latLng.latitude;
        final double longitude = latLng.longitude;

        String address = "";

        try {
            Log.i("Address Info", "Address based opn geocoder");
            final List<Address> addresses = geocoder.getFromLocation(latitude,
                    longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                final Address returnedAddress = addresses.get(0);
                final StringBuilder strReturnedAddress = new StringBuilder();
                final int addressLineIndex = returnedAddress.getMaxAddressLineIndex();

                int addressLinesToShow = 2;
//              To get address in limited lines
                if (addressLineIndex < 2) {
                    addressLinesToShow = addressLineIndex;
                }
                for (int p = 0; p < addressLinesToShow; p++) {
                    strReturnedAddress
                            .append(returnedAddress.getAddressLine(p)).append(
                            "\n");
                }
                address = strReturnedAddress.toString();
            } else {
                address = "Address not available";

            }
        } catch (final IOException e) {
            e.printStackTrace();
            address = "Address not available";
            Log.e("Address not found","Unable to get Address in info window");
        }
        return address;
    }

    public static String getDistanceByUnit(final double startLatitude, final double startLongitude, final double endLatitude, final double endLongitude) {
        final float[] distance = new float[1];
        Log.i("Distance","Distance from source to end");
        Location.distanceBetween(startLatitude, startLongitude, endLatitude,
                endLongitude, distance);
        String distanceByUnit = "Not Available";

        final DecimalFormat d = new DecimalFormat("0.00");
        if (distance[0] > 999.99) {
            distance[0] = distance[0] / 1000;
            distanceByUnit = String.valueOf(d.format(distance[0])) + " Km";
        } else {
            distanceByUnit = String.valueOf(d.format(distance[0])) + " m";
        }
        return distanceByUnit;
    }

    public static Location getLastKnownLocation(Activity activity) {
        LocationManager mLocationManager = (LocationManager) activity.getApplicationContext().getSystemService(Activity.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            //Log.d("last known location, provider: %s, location: %s", provider, l);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                //Log.d("found best last known location: %s", l);
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }
}