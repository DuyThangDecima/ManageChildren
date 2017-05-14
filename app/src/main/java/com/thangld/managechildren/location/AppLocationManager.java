package com.thangld.managechildren.location;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.thangld.managechildren.cloud.resource.LocationResource;

import org.json.JSONException;

/**
 * Created by thangld on 05/05/2017.
 */

public class AppLocationManager extends Service implements LocationListener {


    private Context mContext;
    private String mDeviceId;
    private LocationManager locationManager;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        mDeviceId = intent.getAction();
        getLocationCurrent(mContext);
        return START_STICKY;
    }

    public Location getLocationCurrent(Context context) {
        // The minimum distance to change Updates in meters
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

        // The minimum time between updates in milliseconds
        long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

        locationManager = (LocationManager) context
                .getSystemService(LOCATION_SERVICE);
        if (locationManager == null) {
            Log.d("mc_log", "locationManager = null");
        }
        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        Log.d("mc_log", "isGPSEnabled " + isGPSEnabled);
        Log.d("mc_log", "isNetworkEnabled " + isNetworkEnabled);
        Location location = null;
        // First get location from Network Provider
        if (isNetworkEnabled) {
            Log.d("mc_log", "isNetworkEnabled enter");
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Log.d("mc_log", "Network");
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0, this);


            if (locationManager != null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    return location;
                }
            }
        }
        // if GPS Enabled get lat/long using GPS Services
        if (isGPSEnabled) {
            if (location == null) {


                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0, this);

                Log.d("mc_log", "GPS Enabled");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    return location;
                }
            }
        }

        return null;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(final Location location) {
        Log.d("mc_log", location.toString());
        if (location != null) {


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new LocationResource(mContext).sendLocation(location, mDeviceId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            locationManager.removeUpdates(this);
            this.stopSelf();
        }
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
}