package com.example.savedlocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.savedlocation.entity.Location;
import com.example.savedlocation.infra.LocationDAO;
import com.example.savedlocation.infra.LocationDatabase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private LocationDAO locationdb;
    private FusedLocationProviderClient fused;
    private RecyclerView recyclerView;
    private LocationAdapter adapter;
    private List<Location> locationList;
    private TextView countView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationList = new ArrayList<>();
        locationdb = new LocationDAO(this);
        fused = LocationServices.getFusedLocationProviderClient(this);

        countView = (TextView) findViewById(R.id.textCount);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = locationdb.readAllLocations();
        while (cursor.moveToNext()){
            Location location = new Location();
            location.setLatitude(cursor.getDouble(1));
            location.setLongitude(cursor.getDouble(2));
            locationList.add(location);
        }
        cursor.close();

        adapter = new LocationAdapter(locationList);
        recyclerView.setAdapter(adapter);
        countView.setText(String.valueOf(adapter.getItemCount()));
    }

    private void saveLocation(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            Location location = null;
            try {
                location = getLastLocation().get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (location != null){
                locationdb.createLocation(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private CompletableFuture<Location> getLastLocation(){
        CompletableFuture<Location> future = new CompletableFuture<>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            future.complete(null);
            return future;
        }

        fused.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null){
                        Location location1 = new Location(location.getLatitude(), location.getLongitude());
                        future.complete(location1);
                    }else{
                        future.complete(null);
                    }
                });

        return future;
    }
}