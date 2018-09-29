package com.packagesharer.blash.packagesharer;

import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Tab1Fragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener{
    private static final String TAG = "Tab1Fragment";

    private static boolean IsFirstOpen = true;

    private GoogleMap mMap;
    PlaceAutocompleteFragment placeAutoComplete;

    private LatLngBounds AUSTRALIA = Utility.AUSTRALIA;

    private Location Current = new Location("");


    private static ProgressBar loadingBar;

    private static Handler handler = new Handler();
    private MarkerOptions currentLocationMarker;
    private ArrayList<Marker> markers = new ArrayList<Marker>();


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /// Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.DeliverMap);
        mapFragment.getMapAsync(this);


        placeAutoComplete = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(
                R.id.place_autocomplete);


        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_STREET_ADDRESS|Place.TYPE_ESTABLISHMENT)
                .setCountry("AU")
                .build();

        placeAutoComplete.setFilter(autocompleteFilter);
        placeAutoComplete.setHint("Where To?");
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                mMap.clear();
//                Toast.makeText(getContext(), "Place selected: " + place.getName(), Toast.LENGTH_SHORT).show();
                LatLng averagePosition = new LatLng( (place.getLatLng().latitude + Current.getLatitude()) / 2, (place.getLatLng().longitude + Current.getLongitude()) / 2);
//                Toast.makeText(getContext(), String.valueOf(Utility.findZoomPos(Current, Utility.toLocation(averagePosition))), Toast.LENGTH_SHORT).show();

                Toast.makeText(getContext(), String.valueOf(Current.distanceTo(Utility.toLocation(averagePosition))*0.001), Toast.LENGTH_SHORT).show();

//                Toast.makeText(getContext(), String.valueOf(Utility.findZoomPos(Current, Utility.toLocation(averagePosition))), Toast.LENGTH_SHORT).show();

//                Toast.makeText(getContext(), String.valueOf(" Lng: " + (place.getLatLng().longitude + Current.getLongitude()) / 2 + " Lat: " + (place.getLatLng().latitude + Current.getLatitude()) / 2), Toast.LENGTH_LONG).show();

                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                mMap.addMarker(new MarkerOptions().position(Utility.toLatLng(Current)).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(Utility.toLatLng(Current));
                builder.include(place.getLatLng());
                LatLngBounds bounds = builder.build();

                if(Utility.Orientation == Configuration.ORIENTATION_LANDSCAPE){
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
                            Math.round((float) getActivity().findViewById(R.id.DeliverMap).getHeight() * 3f/10f)));
                }else if(Utility.Orientation == Configuration.ORIENTATION_PORTRAIT) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
                            Math.round((float) getActivity().findViewById(R.id.DeliverMap).getWidth() * 3f / 10f)));
                }
            }
                @Override
                    public void onError(Status status) {
                        Toast.makeText(getContext(), "An error occurred: " + status, Toast.LENGTH_SHORT).show();
                    }
            });

        if (IsFirstOpen) {
            loadingBar = getActivity().findViewById(R.id.LoadingBar);

            new Thread(new Runnable() {
                private int progressStatus = 0;

                @Override
                public void run() {
                    while (progressStatus < 100) {
                        // Update the progress status
                        progressStatus += 1;

                        // Try to sleep the thread for 20 milliseconds
                        try {
                            Thread.sleep(42);
                        } catch (InterruptedException ignored) {

                        }

                        // Update the progress bar
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadingBar.setProgress((int) (Math.round(Utility.progressFunction((double) (progressStatus) / (double) (100)) * 100)));
                            }
                        });
                    }
                }
            }).start();

            new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {e.printStackTrace();}

                    handler.post(new Runnable() {
                        @Override public void run() {
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                    .target(new LatLng(Current.getLatitude(), Current.getLongitude()))
                                    .zoom(12)
                                    .bearing(0)
                                    .tilt(0)
                                    .build()), 2000, new GoogleMap.CancelableCallback() {
                                @Override public void onFinish() {}
                                @Override public void onCancel() {}
                            });
                        }
                    });
                }
            }).start();

            new Thread(new Runnable() {
                public void run() {
                    // Get Current Location:
                    Current = Utility.getLocation(getContext());
                }
            }).start();
            Utility.UpdateNetwork.run();
        }

        IsFirstOpen= false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment, container, false);

        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Utility.Orientation = Configuration.ORIENTATION_LANDSCAPE;
            updateMap();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            updateMap();
            Utility.Orientation = Configuration.ORIENTATION_PORTRAIT;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(mMap == null) {
            mMap = googleMap;
            mMap = Utility.configureMap(mMap);
            mMap.setOnCameraIdleListener(this);
        }
        mMap.clear();
        updateMap();
    }

    public void updateMap() {
        if(currentLocationMarker == null)
            currentLocationMarker = new MarkerOptions().position(new LatLng(Current.getLatitude(),
                    Current.getLongitude())).title("Current Location").icon(BitmapDescriptorFactory.fromAsset("current_location.png")).flat(true).alpha(1.0f);

        if(Utility.DriversInRadius != null) {
            try {
                for(Marker marker : markers){
                    marker.remove();
                }
                mMap.clear();
                for (int i = 0; i < Utility.DriversInRadius.length(); i++) {
                    JSONObject jsonobject = Utility.DriversInRadius.getJSONObject(i);
                    markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(
                            Float.valueOf(jsonobject.get("Lat").toString()),
                            Float.valueOf(jsonobject.get("Lng").toString())))
                            .title("Driver " + jsonobject.get("ID").toString() + " " + jsonobject.get("distance").toString()).flat(true)));
                }
            } catch (JSONException ignored) {}
        }
        mMap.addMarker(currentLocationMarker);


        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(Utility.Camera));
    }


    @Override
    public void onCameraIdle() {
        Utility.Camera = mMap.getCameraPosition();
    }
}
