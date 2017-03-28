package android.spirithunt.win.Controllers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

/**
 * @author sven
 */

public class GpsController extends AppCompatActivity implements
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 7500;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    TextView mLatitudeTextView, mLongitudeTextView, mLastUpdateTimeTextView;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation, mLastLocation;
    String mLastUpdateTime;
    int MY_PERMISSION_ACCESS_FINE_LOCATION;
    LocationRequest mLocationRequest;
    boolean mRequestingLocationUpdates;

    /**
     * Checks if Google Api Client exists and optionally creates one.
     * Also starts creating Location requests.
     *
     * @param savedInstanceState instance state for superclass
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();
        }

        createLocationRequest();
    }

    /**
     * Creates location request and sets interval
     */
    private void createLocationRequest() {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Handle Location permissions if necessary and starts Location updates
     *
     * @param connectionHint optional connection info
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSION_ACCESS_FINE_LOCATION);
        }

        if (MY_PERMISSION_ACCESS_FINE_LOCATION == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

            startLocationUpdates();
        }
    }

    /**
     * Starts location updates if permission is given
     */
    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSION_ACCESS_FINE_LOCATION);
        }

        if (MY_PERMISSION_ACCESS_FINE_LOCATION == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * @param i cause of suspension
     */
    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdates();
    }

    /**
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        stopLocationUpdates();
    }



    /**
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    /**
     * Updates the UI accordingly to the location
     */
    private void updateUI() {
//        mLatitudeTextView.setText(String.valueOf(mCurrentLocation.getLatitude()));
//        mLongitudeTextView.setText(String.valueOf(mCurrentLocation.getLongitude()));
//        mLastUpdateTimeTextView.setText(mLastUpdateTime);
    }

    /**
     * Connects Google Api Client on start
     */
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    /**
     * Stop location updates on pause
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * Remove location updates
     */
    protected void stopLocationUpdates() {
        mRequestingLocationUpdates = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(
            mGoogleApiClient, this);
    }

    /**
     * Start location updates on resume if able to
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }


    /**
     * Disconnect Google Api Client
     */
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
}
