package com.fhaachen.ip_ritz.prototyp;

import android.location.Address;
import android.location.Geocoder;
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
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Button mFetchButton;
    private Button mNormalButton;

    private TextView fetchProfileName;

    private RelativeLayout mFetchFetchCancelButtonContainer;
    private LinearLayout mFetchOptionButtonContainer;

    private String friendId;

    private double friendLat = 0, friendLong = 0, ownLat = 0, ownLong = 0;

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
                try {
                    ArrayList < String > startLocation = new ArrayList < String > ();
                    startLocation.add ( String.valueOf ( friendLat ) );
                    startLocation.add ( String.valueOf ( friendLong ) );

                    ArrayList < String > destinationLocation = new ArrayList < String > ();
                    destinationLocation.add ( String.valueOf ( ownLat ) );
                    destinationLocation.add ( String.valueOf ( ownLong ) );

                    ArrayList < String > passengers = new ArrayList < String > ();
                    passengers.add ( friendId );

                    Geocoder geocoder = new Geocoder ( getApplicationContext () , Locale.getDefault () );
                    List < Address > addresses = geocoder.getFromLocation ( friendLat , friendLong , 1 );
                    String startAddress = addresses.get ( 0 ).getAddressLine ( 0 );
                    addresses = geocoder.getFromLocation ( ownLat , ownLong , 1 );
                    String destinationAddress = addresses.get ( 0 ).getAddressLine ( 0 );

                    Order order = new Order ( LoginActivity.loginViewModel.getLoggedInUser ().get_id ().get$oid () , startLocation , destinationLocation );
                    order.setPassengers ( passengers );
                    order.setStartAddress ( startAddress );
                    order.setDestinationAddress ( destinationAddress );

                    OrderDataCreationTarget dataTarget = new OrderDataCreationTarget ();
                    String orderId = dataTarget.doInBackground ( order );
                    Log.i ( "MapsActivity" , orderId );

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
                } catch ( Exception e ) {
                    e.printStackTrace ();
                }
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

        UserDataSource dataSource = new UserDataSource ();
        User friend = dataSource.doInBackground ( friendId );
        friendLat = friend.getCurrentLocation ().get ( friend.getCurrentLocation ().size () - 1 ).getLatitude ();
        friendLong = friend.getCurrentLocation ().get ( friend.getCurrentLocation ().size () - 1 ).getLongitude ();
        String friendName = friend.getFirstName ();
        ownLat = LoginActivity.loginViewModel.getLoggedInUser ().getCurrentLocation ().get ( LoginActivity.loginViewModel.getLoggedInUser ().getCurrentLocation ().size () - 1 ).getLatitude ();
        ownLong = LoginActivity.loginViewModel.getLoggedInUser ().getCurrentLocation ().get ( LoginActivity.loginViewModel.getLoggedInUser ().getCurrentLocation ().size () - 1 ).getLongitude ();

        LatLng ownLocation = new LatLng ( ownLat , ownLong );
        LatLng friendLocation = new LatLng ( friendLat , friendLong );
        Log.i ( "MapsActivity" , "Setting own location to " + ownLocation.toString () );
        Log.i ( "MapsActivity" , "Setting friend location to " + friendLocation.toString () );

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
