package com.app.parkingpal.mock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class IOTMock {
    public  List<HashMap<String,Double>> emptyParkingSpotsListMock;

    public IOTMock(){
        HashMap<String,Double> emptyParkingSpotHashMapMock = new HashMap<>();
        emptyParkingSpotHashMapMock.put("latitude",45.608807);
        emptyParkingSpotHashMapMock.put("longitude",25.301236);

        HashMap<String,Double> emptyParkingSpotHashMapMock2 = new HashMap<>();
        emptyParkingSpotHashMapMock2.put("latitude",45.605002);
        emptyParkingSpotHashMapMock2.put("longitude",25.305924);

        HashMap<String,Double> emptyParkingSpotHashMapMock3 = new HashMap<>();
        emptyParkingSpotHashMapMock3.put("latitude",45.604717);
        emptyParkingSpotHashMapMock3.put("longitude",25.307844);

        emptyParkingSpotsListMock = Arrays.asList(emptyParkingSpotHashMapMock,emptyParkingSpotHashMapMock2,emptyParkingSpotHashMapMock3);
    }
}
