package com.packagesharer.blash.packagesharer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONObject;

class Utility {
    static Location Current;

    static final LatLngBounds AUSTRALIA = new LatLngBounds(
            new LatLng(-44, 113), new LatLng(-10, 154));

    static CameraPosition Camera = CameraPosition.fromLatLngZoom(AUSTRALIA.getCenter(), 1);
    static int Orientation = Configuration.ORIENTATION_PORTRAIT;

    static AssetManager assetManager;
    static GoogleSignInAccount account;
    static Runnable UpdateNetwork;
    static JSONArray DriversInRadius;

    static Location getLocation(Context c) {
        LocationManager locationManager =
                (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (networkEnabled) {
            if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Current = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }else{
            Current = null;
        }
        return Current;
    }

    static GoogleMap configureMap(GoogleMap map) {
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.setBuildingsEnabled(false);
        map.setLatLngBoundsForCameraTarget(AUSTRALIA);
        map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(new LatLng(AUSTRALIA.getCenter().latitude, AUSTRALIA.getCenter().longitude))
                .zoom(4)
                .bearing(0)
                .tilt(0)
                .build()));

        map.setMinZoomPreference(3.5f);
        map.setMaxZoomPreference(18.0f);
        return map;
    }

    static double progressFunction(double x){
        return 1/(1+Math.pow(Math.E, (-22.5*(x-Math.E/(2*Math.E)))));
    }

    static LatLng toLatLng(Location l){
        double lat = l.getLatitude();
        double lng = l.getLongitude();
        return new LatLng(lat, lng);
    }

    static Location toLocation(LatLng averagePosition) {
        Location temp = new Location(LocationManager.GPS_PROVIDER);
        temp.setLatitude(averagePosition.latitude);
        temp.setLongitude(averagePosition.longitude);
        return temp;
    }
}
