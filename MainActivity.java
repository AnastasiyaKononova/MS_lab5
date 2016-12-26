package com.lab31.admin.lab5_run;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button btnStart;
    Button btnStop;
    BroadcastReceiver broadcastReceiver;
    TextView tvStarted;
    TextView tvLongitude;
    TextView tvLatitude;
    TextView tvAttitude;
    TextView tvElapsedTime;
    TextView tvCity;
    private boolean isStarted = false;
    private ScheduledExecutorService service;
    IntentFilter intentFilter;
    Date startTime;
    LocationManager locationManager;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.buttonStart);
        btnStop = (Button) findViewById(R.id.buttonStop);
        tvStarted = (TextView) findViewById(R.id.textViewStart);
        tvLongitude = (TextView) findViewById(R.id.textViewLong);
        tvLatitude = (TextView) findViewById(R.id.textViewLat);
        tvAttitude = (TextView) findViewById(R.id.textViewAt);
        tvElapsedTime = (TextView) findViewById(R.id.textViewElaps);
        tvCity = (TextView) findViewById(R.id.textViewCity);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "not GPS", Toast.LENGTH_SHORT).show();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("GPS_ACTION");
        LocationReceiver receiver = new LocationReceiver();
        registerReceiver(receiver, filter);
        Intent intent = new Intent("GPS_ACTION");
        pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

    }

    public void onClickStart(View v) {
        if (isStarted) {
            Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();
            return;
        }
        isStarted = true;
        final Date date = new Date();

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, pendingIntent);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.removeUpdates(pendingIntent);
                Toast.makeText(this, "not GPS", Toast.LENGTH_SHORT).show();
                return;
            }

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                tvStarted.setText(date.toString());
                service = Executors.newSingleThreadScheduledExecutor();
                /*service.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (isStarted) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvElapsedTime.setText((new Date().getTime() - date.getTime()) + " ms");
                                }
                            });
                        }
                    }
                }, 0L, 500L, TimeUnit.MILLISECONDS);*/
            }
            else {
                Toast.makeText(this, "not GPS", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    public void onClickStop(View v) {
        isStarted = false;
        if (service != null) {
            service.shutdown();
        }
        try {
            locationManager.removeUpdates(pendingIntent);
            tvLatitude.setText("");
            tvLongitude.setText("");
            tvAttitude.setText("");
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException ex) {

        }
    }

    public class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Bundle b = intent.getExtras();
            Location loc = (Location) b.get(android.location.LocationManager.KEY_LOCATION_CHANGED);
            if (loc != null) {
                tvStarted.setText(timeFormat.format(startTime));
                tvLongitude.setText(String.format("%.7f", loc.getLongitude()));
                tvLatitude.setText(String.format("%.7f", loc.getLatitude()));
                tvAttitude.setText(String.format("%.7f", loc.getAltitude()));
                tvElapsedTime.setText(String.valueOf((new Date().getTime() - startTime.getTime()) / 1000));
                Geocoder gcd = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                    if (addresses.size() > 0)
                        tvCity.setText(String.format("%s, %s", addresses.get(0).getLocality(),
                                addresses.get(0).getFeatureName()));
                } catch (IOException ignored) {}
            }
        }
    }
}
