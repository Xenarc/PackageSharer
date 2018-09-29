package com.packagesharer.blash.packagesharer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends FragmentActivity{

    private static final String TAG = "MainActivity";

    private static final int PERMISSION_COARSE_LOCATION = 1;
    private static final int PERMISSION_FINE_LOCATION = 2;

    private static Handler handler = new Handler(Looper.getMainLooper());

    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPageAdapter mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the viewpager with the sections adapter
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);


        // Grant Permissions

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_COARSE_LOCATION);
            }
        }
        else {
            // Add else for a Permission has already been granted
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_FINE_LOCATION);
            }
        }
        else {
            // Add else for a Permission has already been granted
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            Toast.makeText(this, "ALL DENIED!!", Toast.LENGTH_SHORT).show();
        }else{
//            Toast.makeText(this, " ALL ALLOWED.", Toast.LENGTH_SHORT).show();
        }

        Utility.UpdateNetwork = new Runnable() {
            public void run() {
                // Update Network
                Utility.getLocation(MainActivity.this);
                updateNetwork(new String[][]{
                        new String[]{"ID", Utility.account.getId()},
                        new String[]{"Lng", String.valueOf(Utility.Current.getLongitude())},
                        new String[]{"Lat", String.valueOf(Utility.Current.getLatitude())}
                });
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(Utility.UpdateNetwork, 0, 20, TimeUnit.SECONDS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        Toast.makeText(this, "onRequestPermissionsResult", Toast.LENGTH_LONG).show();
        switch (requestCode) {
            case PERMISSION_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "COARSE ALLOWED.", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(this, "COARSE DENIED.", Toast.LENGTH_SHORT).show();
                }
            }
            case PERMISSION_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "FINE ALLOWED.", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(this, "FINE DENIED.", Toast.LENGTH_SHORT).show();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private static void updateNetwork(final String[][] req){
        new Thread(new Runnable() {
            String s = "";
            @Override
            public void run() {
                try {
                    if((Utility.DriversInRadius = Https.go(req)) != null)
                        s = Utility.DriversInRadius.toString(2);
                    else
                        s = Https.message;
                } catch (JSONException e) {
                    s = e.getMessage();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                        Log.d("NETWORK", s);
                    }
                });
            }
        }).start();
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "Deliver");
        adapter.addFragment(new Tab2Fragment(), "Pickup");
        mViewPager.setAdapter(adapter);
    }
}
