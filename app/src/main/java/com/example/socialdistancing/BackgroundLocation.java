package com.example.socialdistancing;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BackgroundLocation extends Service
{
    private int score= 0;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private CountDownTimer timer;
    private SharedPreferences preferences;
    private LocationRequest locationRequest;
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        fusedLocationProviderClient = new FusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        preferences=getApplicationContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        score = preferences.getInt("score",0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(600000);//location request every 10 minutes.
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                super.onLocationResult(locationResult);
                System.out.println(locationResult.getLastLocation().getLatitude()+ " , "+locationResult.getLastLocation().getLongitude());
                double curr_lat = Double.parseDouble(preferences.getString("lat","0.0"));
                double current_long = Double.parseDouble(preferences.getString("long","0.0"));
                double dist = distance(curr_lat,current_long,locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());
                if(Math.abs(dist)<=0.1)// if distance between home and current location is less than 100 meters, increment score
                {
                    score+=10;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("score",score);
                    editor.commit();
                }
                else
                {
                    score-=10;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("score",score);
                    editor.commit();
                }
                Intent intent1 = new Intent("data");
                intent1.putExtra("score",score);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
            }
        },getMainLooper());
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
    public static double distance(double lat1, double lon1, double lat2, double lon2)// used to calculate distance between 2 geo points without using any API(since distance is just 100 meters, error scope is very less
    {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }
}
