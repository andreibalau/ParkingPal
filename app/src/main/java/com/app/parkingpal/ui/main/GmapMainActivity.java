package com.app.parkingpal.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.app.parkingpal.R;
import com.app.parkingpal.model.ParkingSpot;
import com.app.parkingpal.service.SSEListener;
import com.app.parkingpal.util.DirectionsRequest;
import com.app.parkingpal.api.DirectionsHttpRequestTask;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
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
    private Intent serviceIntent;
    private HashMap<Double,Marker> hashMapMarker = new HashMap<>();
    private Marker marker;

    private static GoogleMap gmap;
    private static List<Polyline> polylineHistory;

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 101;
    private static final int SSE_JOB_ID = 1000;

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
        serviceIntent = new Intent(this, SSEListener.class);
        startService(serviceIntent);

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        gmapMainViewModel.findAll().observe(this, this::markersController);
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
        gmapsUiSettings();
        gmap.setOnMarkerClickListener(this);//onMarkerClick()
        userLocationMarker();
        gmap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                GmapMainActivity.this.setTheme(R.style.AppTheme_NoActionAndTitleBar);
            }
        });
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

    private void gmapsUiSettings(){
        gmap.getUiSettings().setMyLocationButtonEnabled(true);
        gmap.setMyLocationEnabled(false);
        gmap.getUiSettings().setCompassEnabled(false);
        gmap.getUiSettings().setRotateGesturesEnabled(true);
        gmap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.style_json));
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void userLocationMarker(){
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
                            GmapMainActivity.this.userPositionMarker = gmap.addMarker(new MarkerOptions()
                                    .position(GmapMainActivity.this.userLocation)
                                    .title("My Position")
                                    .icon(bitmapDescriptorFromVector(GmapMainActivity.this,R.drawable.ic_directions_car_28_dp)));
                            goToMyLocation();
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void markersController(List<ParkingSpot> parkingSpots){
        parkingSpots.forEach(parkingSpot -> {
            if (parkingSpot.getAvailability() && hashMapMarker.get(parkingSpot.getLatitude())==null) {
                LatLng emptyParkingSpot = new LatLng(parkingSpot.getLatitude(), parkingSpot.getLongitude());
                marker = gmap.addMarker(new MarkerOptions()
                        .position(emptyParkingSpot).title("Empty Spot")
                        .icon(bitmapDescriptorFromVector(this,R.drawable.ic_local_parking_36dp)));
                hashMapMarker.put(parkingSpot.getLatitude(),marker);
            }
            else if(!parkingSpot.getAvailability() && hashMapMarker.get(parkingSpot.getLatitude())!=null){
                marker = hashMapMarker.get(parkingSpot.getLatitude());
                marker.remove();
                hashMapMarker.remove(parkingSpot.getLatitude());
            }
        });
    }

}
