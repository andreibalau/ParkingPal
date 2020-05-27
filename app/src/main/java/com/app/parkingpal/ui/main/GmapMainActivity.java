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
import androidx.lifecycle.ViewModelProvider;

import com.app.parkingpal.ParkingPalApplication;
import com.app.parkingpal.R;
import com.app.parkingpal.model.ParkingSpot;
import com.app.parkingpal.util.DirectionsRequest;
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
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GmapMainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private DrawerLayout mainMenuDrawer;
    private FusedLocationProviderClient fusedLocationClient;
    private double longitude;
    private double latitude;
    private LatLng userLocation;
    private Marker userPositionMarker;
    private GmapMainViewModel gmapMainViewModel;

    private static GoogleMap gmap;
    private static List<Polyline> polylineHistory;

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 101;

    public static GoogleMap getGmap() {
        return gmap;
    }

    public static void addToPolylineHistory(Polyline polyline) {
        GmapMainActivity.polylineHistory.add(polyline);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gmapMainViewModel = new ViewModelProvider(this).get(GmapMainViewModel.class);
        setContentView(R.layout.main_nav_menu);
        mainMenuDrawer = findViewById(R.id.menu_drawer_layout);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(GmapMainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET},
                        ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            }
        }
        buildGmap();
        polylineHistory = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            gmapMainViewModel.fetchFromApi();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        gmapMainViewModel.findAll().observe(this, this::displayEmptyParkingSpots);
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
                    TextView message = findViewById(R.id.permissionsDeniedText);
                    message.setVisibility(View.VISIBLE);
                    Toast toast = Toast.makeText(this, "Please, next time accept permissions.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                return;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gmap = googleMap;
        gmapsUiSettings(gmap);
        gmap.setOnMarkerClickListener(this);//onMarkerClick()
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if(polylineHistory.size()==1){
            polylineHistory.get(0).remove();
            polylineHistory.remove(0);
        }
        if(!marker.equals(null)) {
            Toast.makeText(this, String.format("latitude: %s,longitude: %s",marker.getPosition().latitude,marker.getPosition().longitude), Toast.LENGTH_SHORT).show();
            DirectionsRequest directionsRequest = DirectionsRequest.builder()
                    .origin(userPositionMarker.getPosition())
                    .destination(marker.getPosition())
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
            new DirectionsHttpRequestTask().execute(directionsRequest.getUrl());
        }
        return false;
    }

    public void onClickMenuIcon(View view){
        if(!mainMenuDrawer.isDrawerOpen(GravityCompat.START)) {
            mainMenuDrawer.openDrawer(GravityCompat.START);
        } else {
            mainMenuDrawer.closeDrawer(GravityCompat.START);
        }
    }

    public void onClickGoToMyLocation(View view) {
        goToMyLocation();
    }

    public void goToMyLocation(){
        gmap.moveCamera(CameraUpdateFactory.newLatLng(GmapMainActivity.this.userLocation));
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(GmapMainActivity.this.userLocation,17.0f));
    }

    private void buildGmap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void gmapsUiSettings(GoogleMap googleMap){
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
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
                            goToMyLocation();
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayEmptyParkingSpots(List<ParkingSpot> parkingSpots){
        //mocked empty parking spot location from Room
        Log.d("======>", "displayEmptyParkingSpots: "+parkingSpots.size());
        parkingSpots.forEach(parkingSpot -> {
            LatLng emptyParkingSpot = new LatLng(parkingSpot.getLatitude(),parkingSpot.getLongitude());
            gmap.addMarker(new MarkerOptions().position(emptyParkingSpot).title("Empty Spot").icon(BitmapDescriptorFactory.fromResource(R.drawable.empty_parking_spot_icon)));
        });
    }
}
