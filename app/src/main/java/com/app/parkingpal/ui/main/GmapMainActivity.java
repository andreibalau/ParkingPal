package com.app.parkingpal.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.parkingpal.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class GmapMainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private DrawerLayout mainMenuDrawer;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private SupportMapFragment mapFragment;
    private double longitude;
    private double latitude;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_nav_menu);
        mainMenuDrawer = findViewById(R.id.menu_drawer_layout);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(GmapMainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            }
        }
        else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
            ActivityCompat.requestPermissions(GmapMainActivity.this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                    ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(GmapMainActivity.this, "Permission denied to access location in the background", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 102:{
                if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(GmapMainActivity.this, "Permission denied to access location", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            LatLng position = new LatLng(latitude, longitude);
                            GmapMainActivity.this.googleMap.addMarker(new MarkerOptions().position(position).title("My Position"));
                            GmapMainActivity.this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                            GmapMainActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,17.0f));
                        }
                    }
                });
    }

    public void onClickMenuIcon(View view){
        if(!mainMenuDrawer.isDrawerOpen(GravityCompat.START)) {
            mainMenuDrawer.openDrawer(GravityCompat.START);
        } else {
            mainMenuDrawer.closeDrawer(GravityCompat.START);
        }
    }

}
