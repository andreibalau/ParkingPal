package com.app.parkingpal.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.parkingpal.R;
import com.app.parkingpal.mock.IOTMock;
import com.app.parkingpal.model.DirectionsRequest;
import com.app.parkingpal.util.DirectionsHttpRequestTask;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class GmapMainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private DrawerLayout mainMenuDrawer;
    private static GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private double longitude;
    private double latitude;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 101;
    private IOTMock iotMock;
    private LatLng userLocation;
    private Marker userPositionMarker;
    private static List<Polyline> polylineHistory;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_nav_menu);
        mainMenuDrawer = findViewById(R.id.menu_drawer_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(GmapMainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET},
                        ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            }
        }
        buildGmap();
        polylineHistory = new ArrayList<>();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
                    buildGmap();
                } else {
                    TextView meesage = findViewById(R.id.permissionsDeniedText);
                    meesage.setVisibility(View.VISIBLE);
                    Toast toast = Toast.makeText(this, "Please, next time accept permissions.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                return;
            }
        }
    }

    private void buildGmap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        GmapMainActivity.googleMap = googleMap;
        gmapsUiSettings(GmapMainActivity.googleMap);
        showEmptyParkingSpots(GmapMainActivity.googleMap);
    }

    private void gmapsUiSettings(GoogleMap googleMap){
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.style_json));
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            GmapMainActivity.this.userLocation = new LatLng(latitude, longitude);
                            GmapMainActivity.this.userPositionMarker = googleMap.addMarker(new MarkerOptions().position(GmapMainActivity.this.userLocation).title("My Position").icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon)));
                            GmapMainActivity.googleMap.moveCamera(CameraUpdateFactory.newLatLng(GmapMainActivity.this.userLocation));
                            GmapMainActivity.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(GmapMainActivity.this.userLocation,17.0f));
                        }
                    }
                });
        GmapMainActivity.googleMap.setOnMarkerClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showEmptyParkingSpots(GoogleMap googleMap){
        //mocked empty parking spot location
        iotMock = new IOTMock();
        iotMock.emptyParkingSpotsListMock.forEach(mock -> {
            LatLng emptyParkingSpot = new LatLng(mock.get("latitude"),mock.get("longitude"));
            googleMap.addMarker(new MarkerOptions().position(emptyParkingSpot).title("Empty Spot").icon(BitmapDescriptorFactory.fromResource(R.drawable.empty_parking_spot_icon)));
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if(!marker.equals(null)) {
            Toast.makeText(this, String.format("latitude: %s,longitude: %s",marker.getPosition().latitude,marker.getPosition().longitude), Toast.LENGTH_SHORT).show();
            DirectionsRequest directionsRequest = DirectionsRequest.builder()
                    .origin(userPositionMarker.getPosition())
                    .destination(marker.getPosition())
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
            new DirectionsHttpRequestTask().execute(directionsRequest.getUrl());
        }
        if(polylineHistory.size()==1){
            polylineHistory.get(0).remove();
            polylineHistory.remove(0);
        }
//        Log.d("=====>", ""+ polylineHistory.size());
        return false;
    }

    public void onClickMenuIcon(View view){
        if(!mainMenuDrawer.isDrawerOpen(GravityCompat.START)) {
            mainMenuDrawer.openDrawer(GravityCompat.START);
        } else {
            mainMenuDrawer.closeDrawer(GravityCompat.START);
        }
    }

    public static GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void goToMyLocation(View view) {
        GmapMainActivity.googleMap.moveCamera(CameraUpdateFactory.newLatLng(GmapMainActivity.this.userLocation));
    }

    public static void addToPolylineHistory(Polyline polyline) {
        GmapMainActivity.polylineHistory.add(polyline);
    }
}
