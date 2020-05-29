package com.app.parkingpal.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "parking_spots",primaryKeys = {"latitude","longitude"})
public class ParkingSpot {
    @NonNull
    @ColumnInfo(name = "latitude")
    private Double latitude;
    @NonNull
    @ColumnInfo(name = "longitude")
    private Double longitude;
    @ColumnInfo(name = "availability")
    private Boolean availability;
}
