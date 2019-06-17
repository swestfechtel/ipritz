package com.fhaachen.ip_ritz.prototyp;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.Tile;

import java.io.File;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource;

public class WaitingActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static double longitudeTo;
    public static double latitufeTo;
    public static double myLocationlatitude;
    public static double myLocationlongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*LatLng freundin = new LatLng(50.785474, 6.052972);
        LatLng drone = new LatLng(50.790208, 6.062671);*/

        LatLng freundin = new LatLng(myLocationlatitude, myLocationlongitude);
        LatLng drone = new LatLng(latitufeTo, longitudeTo);

        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        drone,
                        freundin));
        polyline1.setStartCap(new RoundCap());
        polyline1.setEndCap(new RoundCap());
        polyline1.setColor(R.color.colorPrimary);
        drawMarker(drone, "Flugtaxi");
        mMap.addMarker(new MarkerOptions().position(freundin).title("Freundin"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(freundin));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
    }

    public void drawMarker(LatLng position, String title) {
        Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_airplanemode_active_black_24dp);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);

        mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(title)
                .icon(markerIcon)
        );
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
