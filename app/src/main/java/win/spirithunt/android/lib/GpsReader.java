package win.spirithunt.android.lib;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import win.spirithunt.android.provider.ContextProvider;
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

/**
 * @author Sven Boekelder
 * @author Remco Schipper
 */

public class GpsReader implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = GpsReader.class.getSimpleName();
    public static final int permissionRequestCode = 1111;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Activity activity;
    private boolean isUpdating = false;

    /**
     * Initialize the Google API client for later use
     * @param activity The activity used for the permission requests / alerts
     */
    public GpsReader(Activity activity) {
        this.activity = activity;

        if (this.mGoogleApiClient == null) {
            this.mGoogleApiClient = new GoogleApiClient.Builder(ContextProvider.getInstance().getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(7500);
        locationRequest.setFastestInterval(7500/2);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        this.mLocationRequest = locationRequest;
        this.mGoogleApiClient.connect();
    }

    /**
     * Ask permission to access the GPS sensor
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
        this.isUpdating = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Start the location updates
     */
    public void start() {
        if(!this.isUpdating) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, this.mLocationRequest, this);
                this.isUpdating = true;
            } else {
                this.askPermissions(this.activity);
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
        if(this.mLastLocation == null || (this.mLastLocation.getLongitude() != location.getLongitude() || this.mLastLocation.getLatitude() != location.getLatitude())) {
            GpsUpdater.getInstance().setCoordinates(location.getLatitude(), location.getLongitude());
            this.mLastLocation = location;
        }
    }
}
