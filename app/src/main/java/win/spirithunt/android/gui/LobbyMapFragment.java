package win.spirithunt.android.gui;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import io.socket.client.Ack;
import io.socket.client.Socket;
import win.spirithunt.android.R;
import win.spirithunt.android.provider.SocketProvider;

/**
 * @author Remco Schipper
 */

public class LobbyMapFragment extends Fragment implements OnMapReadyCallback {
    private SupportMapFragment fragment;
    private GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lobby_view_map, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment)fm.findFragmentById(R.id.map_container);

        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, fragment).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fragment.getMapAsync(this);
    }

    /**
     * Called when the Google Maps map is loaded and ready to be used.
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            System.out.println(e.getMessage());
        }

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        this.map = googleMap;

        final LobbyMapFragment self = this;
        Socket socket = SocketProvider.getInstance().getConnection();
        socket.emit("lobby:map", null, new Ack() {
            @Override
            public void call(final Object... args) {
                self.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (args[0] == null) {
                            double centerLat = Double.parseDouble(args[1].toString());
                            double centerLong = Double.parseDouble(args[2].toString());
                            double borderLat = Double.parseDouble(args[3].toString());
                            double borderLong = Double.parseDouble(args[4].toString());

                            self.markArea(centerLat, centerLong, borderLat, borderLong);
                        }
                    }
                });
            }
        });
    }

    private void markArea(double centerLat, double centerLong, double borderLat, double borderLong) {
        LatLng center = new LatLng(centerLat, centerLong);
        LatLng border = new LatLng(borderLat, borderLong);

        MarkerOptions markerOptions = new MarkerOptions()
            .position(center)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        map.addMarker(markerOptions);

        this.createCircle(center, border);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(center);
        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(center, 10);

        map.moveCamera(cameraUpdate);
        map.animateCamera(zoom);
    }

    private void createCircle(LatLng center, LatLng border) {
        int strokeColor = 0xffff0000;
        int shadeColor = 0x44ff0000;

        float[] dist = new float[1];
        Location.distanceBetween(center.latitude,
            center.longitude,
            border.latitude,
            border.longitude,
            dist);

        CircleOptions circleOptions = new CircleOptions()
            .center(center)
            .radius(dist[0])
            .fillColor(shadeColor)
            .strokeColor(strokeColor)
            .strokeWidth(8);

        map.addCircle(circleOptions);
    }
}
