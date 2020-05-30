package com.app.parkingpal.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.app.parkingpal.ParkingPalApplication;
import com.app.parkingpal.model.ParkingSpot;
import com.app.parkingpal.repository.ParkingSpotRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.kaazing.net.sse.SseEventReader;
import org.kaazing.net.sse.SseEventSource;
import org.kaazing.net.sse.SseEventSourceFactory;
import org.kaazing.net.sse.SseEventType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;


public class SSEListener extends IntentService {

    @Inject
    ParkingSpotRepository parkingSpotRepository;

    public SSEListener() {
        super("SSEListener");
        ParkingPalApplication.getAppComponent().inject(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            final SseEventSourceFactory sseEventSourceFactory = SseEventSourceFactory.createEventSourceFactory();
            final SseEventSource sseEventSource = sseEventSourceFactory.createEventSource(new URI("http://192.168.1.3:8080/sse-news"));//TODO: Change ip with the server mechine ip
            sseEventSource.connect();

            final SseEventReader sseEventReader = sseEventSource.getEventReader();

            SseEventType type = sseEventReader.next();
            Gson gson = new Gson();
            while (type != SseEventType.EOS) {
                Log.d("EVENTSOURCE", "new event");
                if (type != null && type.equals(SseEventType.DATA)) {
                    CharSequence data = sseEventReader.getData();
                    Log.d("DATA",data.toString());
                    List<ParkingSpot> parkingSpots = gson.fromJson(data.toString(), new TypeToken<List<ParkingSpot>>(){}.getType());
                    if(parkingSpots!=null){
                        parkingSpots.forEach(spot -> parkingSpotRepository.save(spot));
                    }
                } else {
                    Log.d("TAG","type null or not data: " + type);
                }
                type = sseEventReader.next();
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}
