package win.spirithunt.android.lib;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import win.spirithunt.android.provider.ContextProvider;

/**
 * @author Sven Boekelder
 * @author Remco Schipper
 */

public class GpsReader implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final int permissionRequestCode = 1111;
    private static final String TAG = GpsReader.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Location mLastLocation;
    private Activity activity;
    private boolean isUpdating = false;

    /**
     * Initialize the Google API client for later use
     *
     * @param activity The activity used for the permission requests / alerts
     */
    public GpsReader(Activity activity) {
        this.activity = activity;

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(ContextProvider.getInstance().getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();
        }

        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(7500);
        locationRequest.setFastestInterval(7500 / 2);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationRequest = locationRequest;
    }

    /**
     * Ask permission to access the GPS sensor
     *
     * @param activity The activity used for the permission requests
     */
    private void askPermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, permissionRequestCode);
        }
    }

    /**
     * Stop the location updates
     */
    public void stop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        isUpdating = false;
    }

    /**
     * Start the location updates
     */
    private void start() {
        if (!isUpdating) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                isUpdating = true;
            } else {
                askPermissions(activity);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(GpsReader.TAG, "Google API services connected");
        start();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(GpsReader.TAG, "Google API services connection suspended");
        System.out.println("onConnectionSuspended");
        stop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(GpsReader.TAG, "Google API services connection failed");
        stop();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mLastLocation == null || (mLastLocation.getLongitude() != location.getLongitude() || mLastLocation.getLatitude() != location.getLatitude())) {
            GpsUpdater.getInstance().setCoordinates(location.getLatitude(), location.getLongitude());
            mLastLocation = location;
        }
    }
}
