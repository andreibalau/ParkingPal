package com.app.parkingpal.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.parkingpal.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DrawerLayout mainMenuDrawer;
    private GoogleMap gooleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_nav_menu);

        mainMenuDrawer = findViewById(R.id.menu_drawer_layout);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        onClickMenuIcon();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gooleMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        gooleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        gooleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void onClickMenuIcon(){
        ImageButton imageButton = findViewById(R.id.menu_icon);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mainMenuDrawer.isDrawerOpen(GravityCompat.START)) {
                    mainMenuDrawer.openDrawer(GravityCompat.START);
                } else {
                    mainMenuDrawer.closeDrawer(GravityCompat.START);
                }
            }
        });
    }
}
