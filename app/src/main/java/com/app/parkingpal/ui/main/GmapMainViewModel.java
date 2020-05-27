package com.app.parkingpal.ui.main;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Response;
import com.app.parkingpal.ParkingPalApplication;
import com.app.parkingpal.api.ParkingPalGetRequest;
import com.app.parkingpal.model.ParkingSpot;
import com.app.parkingpal.repository.ParkingSpotRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

public class GmapMainViewModel extends ViewModel {

    private MutableLiveData<List<ParkingSpot>> spots = new MutableLiveData<>();

    @Inject
    ParkingSpotRepository parkingSpotRepository;

    public GmapMainViewModel(){
        ParkingPalApplication.getAppComponent().inject(this);
    }

    public void fetchFromApi() throws ExecutionException, InterruptedException, JSONException {//TODO: ..to be continue
        String result;
        result = new ParkingPalGetRequest().execute("http://192.168.1.3:8080/available-spots").get();
        Gson gson = new Gson();
        List<ParkingSpot> parkingSpots = gson.fromJson(result, new TypeToken<List<ParkingSpot>>(){}.getType());
        Log.d("REQUEST_API response ->", ""+parkingSpots.get(0).getLongitude());
        parkingSpotRepository.saveAll(parkingSpots);
    }

    public LiveData<List<ParkingSpot>> findAll(){
        return parkingSpotRepository.findAll();
    }

}
