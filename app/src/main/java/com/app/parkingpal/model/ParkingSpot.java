package com.app.parkingpal.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingSpot {
    private Double latitude;
    private Double longitude;
    private Boolean availability;
}
