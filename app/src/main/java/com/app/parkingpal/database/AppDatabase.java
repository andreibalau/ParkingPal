package com.app.parkingpal.database;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.app.parkingpal.model.ParkingSpot;
import com.app.parkingpal.model.dao.ParkingSpotDao;

@Database(entities = {ParkingSpot.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase appDatabase;

    public static synchronized AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room
                    .databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "parkingpaldb")
                    .fallbackToDestructiveMigration()
                    .addCallback(initialDbData)
                    .build();
        }
        return appDatabase;
    }

    private static RoomDatabase.Callback initialDbData = new RoomDatabase.Callback() {
        @Override
        public void onCreate (@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d("ROOM_CALLBACK", "onCreate: database");
            ContentValues parkingSpotsValues = new ContentValues();
            parkingSpotsValues.put("latitude",45.608807);
            parkingSpotsValues.put("longitude",25.301236);
            parkingSpotsValues.put("availability",true);
            db.insert("parking_spots", OnConflictStrategy.IGNORE,parkingSpotsValues);

            parkingSpotsValues = new ContentValues();
            parkingSpotsValues.put("latitude",45.605002);
            parkingSpotsValues.put("longitude",25.305924);
            parkingSpotsValues.put("availability",true);
            db.insert("parking_spots", OnConflictStrategy.IGNORE,parkingSpotsValues);

            parkingSpotsValues = new ContentValues();
            parkingSpotsValues.put("latitude",45.604717);
            parkingSpotsValues.put("longitude",25.307844);
            parkingSpotsValues.put("availability",true);
            db.insert("parking_spots", OnConflictStrategy.IGNORE,parkingSpotsValues);
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };
    public abstract ParkingSpotDao getParkingSpotDao();
}
