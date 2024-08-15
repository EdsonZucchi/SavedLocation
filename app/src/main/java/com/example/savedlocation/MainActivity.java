package com.example.savedlocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.savedlocation.entity.Location;
import com.example.savedlocation.infra.LocationDAO;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LocationDAO locationdb;
    private FusedLocationProviderClient fused;
    private RecyclerView recyclerView;
    private LocationAdapter adapter;
    private List<Location> locationList;
    private TextView countView;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationList = new ArrayList<>();
        locationdb = new LocationDAO(this);
        fused = LocationServices.getFusedLocationProviderClient(this);

        countView = (TextView) findViewById(R.id.textCount);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                return;
            }
        };

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        startLocationUpdates();
        reloadLocations();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    public void reloadButton(View view){
        reloadLocations();
    }

    public void saveLocation(View view){
        requestSingleUpdate();
    }

    public void clearLocations(View view){
        locationdb.deleteAllLocations();
        reloadLocations();
    }

    private void reloadLocations(){
        locationList.clear();
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

    private void requestSingleUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fused.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        locationdb.createLocation(location.getLatitude(), location.getLongitude());
                        reloadLocations();
                    }
                });
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fused.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fused.removeLocationUpdates(locationCallback);
    }
}