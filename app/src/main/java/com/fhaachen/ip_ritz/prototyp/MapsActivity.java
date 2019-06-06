package com.fhaachen.ip_ritz.prototyp;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fhaachen.ip_ritz.prototyp.data.OrderDataCreationTarget;
import com.fhaachen.ip_ritz.prototyp.data.UserDataSource;
import com.fhaachen.ip_ritz.prototyp.data.UserDataUpdateTarget;
import com.fhaachen.ip_ritz.prototyp.data.model.Order;
import com.fhaachen.ip_ritz.prototyp.data.model.User;
import com.fhaachen.ip_ritz.prototyp.ui.login.LoginActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Button mFetchButton;
    private Button mNormalButton;

    private TextView fetchProfileName;

    private RelativeLayout mFetchFetchCancelButtonContainer;
    private LinearLayout mFetchOptionButtonContainer;

    private String friendId;

    private double friendLat = 0, friendLong = 0, ownLat = 50.771758, ownLong = 6.068255;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        friendId = getIntent ().getStringExtra ( "friendId" );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFetchFetchCancelButtonContainer = findViewById(R.id.fetchFetchCancelButtonContainer);
        mFetchOptionButtonContainer = findViewById(R.id.fetchOptionButtonContainer);

        fetchProfileName = findViewById ( R.id.fetchProfileName );

        mFetchButton = findViewById(R.id.fetchFetchButton);
        mFetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFetchFetchCancelButtonContainer.setVisibility(View.GONE);
                mFetchOptionButtonContainer.setVisibility(View.VISIBLE);
            }
        });

        mNormalButton = findViewById(R.id.fetchNormalButton);
    }

    @Override
    protected void onResume () {
        super.onResume ();
        mNormalButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick ( View v ) {
                ArrayList < String > startLocation = new ArrayList < String > ();
                startLocation.add ( String.valueOf ( friendLat ) );
                startLocation.add ( String.valueOf ( friendLong ) );
                ArrayList < String > destinationLocation = new ArrayList < String > ();
                destinationLocation.add ( String.valueOf ( ownLat ) );
                destinationLocation.add ( String.valueOf ( ownLong ) );
                ArrayList < String > passengers = new ArrayList < String > ();
                passengers.add ( friendId );
                Order order = new Order ( LoginActivity.loginViewModel.getLoggedInUser ().get_id ().get$oid () , startLocation , destinationLocation );
                order.setPassengers ( passengers );

                OrderDataCreationTarget dataTarget = new OrderDataCreationTarget ();
                String orderId = dataTarget.doInBackground ( order );

                UserDataSource dataSource = new UserDataSource ();
                User loggedInUser = LoginActivity.loginViewModel.getLoggedInUser ();
                User friend = dataSource.doInBackground ( friendId );

                ArrayList < String > journeys;
                if ( ( journeys = loggedInUser.getJourneys () ) == null ) {
                    journeys = new ArrayList <> ();
                }
                journeys.add ( orderId );
                loggedInUser.setJourneys ( journeys );

                if ( ( journeys = friend.getJourneys () ) == null ) {
                    journeys = new ArrayList <> ();
                }
                journeys.add ( orderId );
                friend.setJourneys ( journeys );

                UserDataUpdateTarget userDataUpdateTarget = new UserDataUpdateTarget ();
                userDataUpdateTarget.doInBackground ( loggedInUser , friend );

                finish ();
            }
        } );
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


        String friendName = "";

        UserDataSource dataSource = new UserDataSource ();
        User friend = dataSource.doInBackground ( friendId );
        friendLat = friend.getCurrentLocation ().get ( 0 ).getLatitude ();
        friendLong = friend.getCurrentLocation ().get ( 0 ).getLongitude ();

        LocationManager locManager = ( LocationManager ) getSystemService ( LOCATION_SERVICE );

        boolean network_enabled = locManager.isProviderEnabled ( LocationManager.NETWORK_PROVIDER );

        Location location;
        LatLng ownLocation = new LatLng ( 50.771758 , 6.068255 );
        LatLng friendLocation = new LatLng ( friendLat , friendLong );
        Log.i ( "MapsActivity" , "Setting own location to " + ownLocation.toString () );
        Log.i ( "MapsActivity" , "Setting friend location to " + friendLocation.toString () );

        // TODO: get own location
        /*if ( network_enabled ) {

            if ( ActivityCompat.checkSelfPermission ( this , Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission ( this , Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

                return;
            }
            location = locManager.getLastKnownLocation ( LocationManager.NETWORK_PROVIDER );

            if ( location != null ) {
                ownLocation = new LatLng ( location.getLatitude () , location.getLongitude () );
                Log.i("MapsActivity", "Setting own location to " + ownLocation.toString ());
                //mMap.addMarker(new MarkerOptions().position(westpark).title("Current Location"));
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(westpark, 15) );

            }
        }*/
        // Add a marker in Sydney and move the camera
        //LatLng westpark = new LatLng(50.771758, 6.068255);


        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        ownLocation ,
                        friendLocation ) );
        polyline1.setStartCap(new RoundCap());
        polyline1.setEndCap(new RoundCap());
        polyline1.setColor(R.color.colorPrimary);

        mMap.addMarker ( new MarkerOptions ().position ( ownLocation ).title ( "You" ) );
        mMap.addMarker ( new MarkerOptions ().position ( friendLocation ).title ( friendName ) );

        mMap.moveCamera ( CameraUpdateFactory.newLatLng ( ownLocation ) );
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
    }
}
