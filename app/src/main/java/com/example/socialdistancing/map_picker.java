package com.example.socialdistancing;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class map_picker extends AppCompatActivity
{
    private GoogleMap map;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_layout);
        initMap();
    }
    private void initMap()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                map=googleMap;
                currentLocation();
            }
        });
    }

    private void currentLocation()
    {
        if(map!=null)
        {

            mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(map_picker.this);
            mfusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>()
            {
                @Override
                public void onComplete(@NonNull Task<Location> task)
                {
                    if(task.isSuccessful())// adding the home location in the SharedPreferences for future use
                    {
                        Location location = task.getResult();
                        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("lat",Double.toString(location.getLatitude()));
                        editor.putString("long",Double.toString(location.getLongitude()));
                        editor.commit();
                        map.setMyLocationEnabled(true);
                        LatLng obj = new LatLng(location.getLatitude(),location.getLongitude());
                        moveCamera(obj,20f);
                    }
                    else
                    {
                        Toast.makeText(map_picker.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    void moveCamera(LatLng current,float zoom)
    {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoom));
    }
}
