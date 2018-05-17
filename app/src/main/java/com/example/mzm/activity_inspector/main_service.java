package com.example.mzm.activity_inspector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.mzm.activity_inspector.Models.activity_model;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class main_service extends Service implements SensorEventListener, LocationListener {


    private String _stance = "";
    private Location previouslocation = null;
    private ResultReceiver receiver;
    private double acceleration;
    @SuppressLint("SimpleDateFormat")
    private DateFormat df = new SimpleDateFormat("h:mm a");
    private DecimalFormat precision = new DecimalFormat("#0.00");
    private String prevous_time = " 0.00 PM ";
    private db_helper db;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Sensor mySensor;
        SensorManager SM;
        db = new db_helper(getApplicationContext());


        // Create our Sensor Manager
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Accelerometer Sensor
        if (SM != null) {
            mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            // Register sensor Listener
            SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.d("##-->acc_serv :", "SM is null");
        }

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            Log.d("##-->acc_serv :", "Location manager is not null");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        receiver = intent.getParcelableExtra("reciever");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float magnitude = 0.0f;
        magnitude = (float) Math.sqrt(sensorEvent.values[0] * sensorEvent.values[0] + sensorEvent.values[1] * sensorEvent.values[1] + sensorEvent.values[2] * sensorEvent.values[2]);
        magnitude = (float) (Math.abs(magnitude - 9.7));
        acceleration = magnitude;

        if (magnitude <= 0.1) {
            detectStance("standing");
        } else if (magnitude > 0.1 && magnitude < 2) {
            detectStance("walking");
        } else {
            detectStance("runing");
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void detectStance(String stance) {

        if (!stance.equals(_stance)) {
            Bundle bundle = new Bundle();
            bundle.putString("stance", "started " + stance);
            bundle.putString("speed", null);
            if (receiver != null)
                receiver.send(999, bundle);
            _stance = stance;
        }

    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onLocationChanged(Location location) {


        //calcul manually speed
        double speed = 0, distance = 0;
        if (this.previouslocation != null) {

            distance = location.distanceTo(previouslocation);
            speed = distance / ((location.getTime() - this.previouslocation.getTime()));
        } else {
            previouslocation = location;
            return;
        }

        Log.d("##-->acc_serv :", "" + df.format(Calendar.getInstance().getTime()) + " " + precision.format(speed) + " " + precision.format(acceleration) + " " + precision.format(distance));

        Bundle bundle = new Bundle();
        bundle.putString("speed", String.format("speed : %.2f m/s", speed));
        bundle.putString("stance", _stance);


        if (receiver != null)
            receiver.send(999, bundle);
        activity_model item = new activity_model();
        item.stance = _stance;
        item.acc = String.format("%.2f", acceleration);
        item.speed = String.format("%.2f", speed);
        item.start_time = df.format(Calendar.getInstance().getTime());
        item.end_time = prevous_time;
        item.dist = String.format("%.2f", distance);
        ;

        db.insert_data(item);

        Toast.makeText(getApplicationContext(), "speed " + speed, Toast.LENGTH_SHORT).show();
        this.previouslocation = location;
        this.prevous_time = df.format(Calendar.getInstance().getTime());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    @Override
    public void onDestroy() {
        sendBroadcast(new Intent("com.example.mzm.activity_inspector.restart_service"));
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        sendBroadcast(new Intent("com.example.mzm.activity_inspector.restart_service"));

        super.onTaskRemoved(rootIntent);

    }
}

