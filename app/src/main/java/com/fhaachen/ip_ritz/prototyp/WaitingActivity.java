package com.fhaachen.ip_ritz.prototyp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fhaachen.ip_ritz.prototyp.data.DroneConfirmationTarget;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.net.HttpURLConnection;
import java.net.URL;

public class WaitingActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double la;
    private double lo;
    private Button cancelBtn;
    private Button landingBtn;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver () {
        @Override
        public void onReceive ( Context context , Intent intent ) {
            showDronePopup ();
        }
    };


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
        if ( getIntent ().hasExtra ( "startLat" ) ) {
            la = getIntent().getExtras().getDouble("startLat");

        }
        if ( getIntent ().hasExtra ( "startLong" ) ) {
            lo = getIntent().getExtras().getDouble("startLong");

        }
        LatLng start = new LatLng(la, lo);
        LatLng drone = new LatLng(la+0.0023, lo+0.0003);

        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        drone,
                        start));
        polyline1.setStartCap(new RoundCap());
        polyline1.setEndCap(new RoundCap());
        polyline1.setColor(R.color.colorPrimary);
        drawMarker(drone, "Flugtaxi");
        mMap.addMarker(new MarkerOptions().position(start).title("start"));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(start);
        builder.include(drone);
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        mMap.animateCamera(cu);
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

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_waiting );
        cancelBtn = findViewById(R.id.cancel_button);
        landingBtn = findViewById(R.id.landing_button);
        landingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //landing Befehl
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL server = new URL("149.201.48.86/landing.php");
                            HttpURLConnection connection = (HttpURLConnection) server.openConnection();
                            connection.setDoOutput(false);
                            connection.connect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel Befehl
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = ( SupportMapFragment ) getSupportFragmentManager ()
                .findFragmentById ( R.id.map );
        mapFragment.getMapAsync ( this );

        LocalBroadcastManager.getInstance ( this ).registerReceiver ( mMessageReceiver ,
                new IntentFilter ( "message-test" ) );


    }

    private void showDronePopup () {
        try {
            new AlertDialog.Builder ( this )
                    .setTitle ( "Your drone has arrived!" )
                    .setMessage ( "Your drone has arrived at your location. Please confirm its arrival to resume the ride." )

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton ( "Confirm" , new DialogInterface.OnClickListener () {
                        public void onClick ( DialogInterface dialog , int which ) {
                            Toast.makeText ( getApplicationContext () , "Confirmed" , Toast.LENGTH_SHORT );
                            DroneConfirmationTarget droneConfirmationTarget = new DroneConfirmationTarget ();
                            droneConfirmationTarget.doInBackground ();
                        }
                    } )

                    .setNeutralButton ( "Deny" , new DialogInterface.OnClickListener () {
                        public void onClick ( DialogInterface dialog , int which ) {
                            Toast.makeText ( getApplicationContext () , "Denied" , Toast.LENGTH_SHORT );
                        }
                    } )
                    .show ();
        } catch ( Exception e ) {
            e.printStackTrace ();
        }
    }
}
