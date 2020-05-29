package com.app.parkingpal.ui.main;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

    @Inject
    ParkingSpotRepository parkingSpotRepository;

    public GmapMainViewModel(){
        ParkingPalApplication.getAppComponent().inject(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void fetchFromApi() throws ExecutionException, InterruptedException {
        String result;
        result = new ParkingPalGetRequest().execute("http://192.168.1.3:8080/available-spots").get();
        Gson gson = new Gson();
        List<ParkingSpot> parkingSpots = gson.fromJson(result, new TypeToken<List<ParkingSpot>>(){}.getType());
        parkingSpots.forEach(spot -> parkingSpotRepository.save(spot));
    }

    public LiveData<List<ParkingSpot>> findAll(){
        return parkingSpotRepository.findAll();
    }

}
