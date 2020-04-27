package com.example.socialdistancing;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends AppCompatActivity
{
    private boolean mpermission = false;// initializing the location permission needed as false.
    private Button locate;
    private TextView sc;
    SharedPreferences preferences;// used so that whenever the app is opened fresh, the count of the points is not lost
    BroadcastReceiver receiver = new BroadcastReceiver()// used to receive the intent sent from the service BackgroundLocation.java
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            sc.setText(Integer.toString(intent.getIntExtra("score",preferences.getInt("score",0))));
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getApplicationContext().getSharedPreferences("my_prefs",Context.MODE_PRIVATE);
        sc = findViewById(R.id.points);
        System.out.println(getApplicationContext()+"    "+this);
        locate = findViewById(R.id.locate_me);
        checkPermission();
        if(mpermission && !preferences.getString("lat","0.0").equals("0.0"))// if the home location is already available, user cannot select location again(button invisible and unclickable.
        {
            Intent intent = new Intent(MainActivity.this,BackgroundLocation.class);
            startService(intent);
            locate.setClickable(false);
            locate.setVisibility(View.INVISIBLE);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver,new IntentFilter("data"));
        }
        locate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mpermission)
                {
                    LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver,new IntentFilter("data"));
                    if(preferences.getString("lat","0.0").equals("0.0"))
                    {
                        Intent intent = new Intent(MainActivity.this,map_picker.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(MainActivity.this,BackgroundLocation.class);
                        startService(intent);
                    }
                    //startActivity(intent);
                }
                else
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1234);
                    //Toast.makeText(MainActivity.this, "Please location permission", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void checkPermission()
    {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),permission[0])== PackageManager.PERMISSION_GRANTED)
        {
            mpermission=true;
        }
        else
        {
            ActivityCompat.requestPermissions(this, permission,1234);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        mpermission = false;
        if(requestCode==1234)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                mpermission=true;
            }
            else
            {
                Toast.makeText(this, "Need this permission to run app!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
