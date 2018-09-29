package com.packagesharer.blash.packagesharer;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class Tab2Fragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "Tab2Fragment";

    private GoogleMap mMap;

    private LatLngBounds AUSTRALIA = Utility.AUSTRALIA;

    private static Handler handler = new Handler();

    private Location Current = new Location("");

    @Override
    public void onStart() {
        super.onStart();

        // Get Current Location:
        Current = Utility.getLocation(getContext());

        /// Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.PickupMap);
        mapFragment.getMapAsync(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Current.getLatitude(),
                                Current.getLongitude())).title("Current Location"));
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(Current.getLatitude(), Current.getLongitude()))
                                .zoom(12)
                                .bearing(0)
                                .tilt(0)
                                .build()));
                    }
                });
            }
        }).start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment, container, false);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = Utility.configureMap(googleMap);
    }
}
