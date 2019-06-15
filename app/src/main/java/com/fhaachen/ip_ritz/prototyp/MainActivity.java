package com.fhaachen.ip_ritz.prototyp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fhaachen.ip_ritz.prototyp.data.TokenUpdateTarget;
import com.fhaachen.ip_ritz.prototyp.ui.login.LoginActivity;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import static android.support.constraint.Constraints.TAG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageButton searchButton;
    private EditText searchText;

    protected String chosenType;
    protected String chosenTime;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    public double latitudeNow;
    public double longitudeNow;

    private TextView navHeaderName;
    private TextView navHeaderMail;

    private Toolbar toolbar;
    private DrawerLayout drawer;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder ().permitAll ().build ();
        StrictMode.setThreadPolicy ( policy );

        if ( LoginActivity.loginViewModel == null ) {
            Intent i = new Intent ( getApplicationContext () , LoginActivity.class );
            startActivity ( i );
        }

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView ( 0 );
        ImageView headerImage = header.findViewById ( R.id.navHeaderImage );
        navHeaderName = header.findViewById ( R.id.navHeaderName );
        navHeaderMail = header.findViewById ( R.id.navHeaderMail );

        headerImage.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick ( View v ) {

                Intent i = new Intent ( v.getContext () , LoginActivity.class );
                startActivity ( i );
            }
        } );

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        searchText = findViewById(R.id.input_search);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FirebaseInstanceId.getInstance ().getInstanceId ()
                .addOnCompleteListener ( new OnCompleteListener < InstanceIdResult > () {
                    @Override
                    public void onComplete ( @NonNull Task < InstanceIdResult > task ) {
                        if ( !task.isSuccessful () ) {
                            Log.w ( TAG , "getInstanceId failed" , task.getException () );
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult ().getToken ();

                        // Log and toast
                        String msg = token;//getString(R.string.msg_token_fmt, token);
                        Log.d ( TAG , "Token" + msg );
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } );

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient ( this );

        locationRequest = LocationRequest.create ();
        locationRequest.setInterval ( UPDATE_INTERVAL );
        locationRequest.setFastestInterval ( FASTEST_INTERVAL );
        locationRequest.setPriority ( LocationRequest.PRIORITY_HIGH_ACCURACY );
    }

    @Override
    public void onResume () {
        super.onResume ();

        if ( LoginActivity.loginViewModel != null && LoginActivity.loginViewModel.getLoggedInUser () == null ) {
            Intent i = new Intent ( getApplicationContext () , LoginActivity.class );
            startActivity ( i );
        }

        if ( LoginActivity.loginViewModel != null && LoginActivity.loginViewModel.getLoggedInUser () != null ) {
            navHeaderName.setText ( LoginActivity.loginViewModel.getLoggedInUser ().getFirstName () + " " + LoginActivity.loginViewModel.getLoggedInUser ().getLastName () );
            navHeaderMail.setText ( LoginActivity.loginViewModel.getLoggedInUser ().getEmail () );

            String token = FirebaseInstanceId.getInstance ().getToken ();
            Log.i ( "MainActivity" , " " + token );
            TokenUpdateTarget tokenUpdateTarget = new TokenUpdateTarget ();
            String userId = LoginActivity.loginViewModel.getLoggedInUser ().get_id ().get$oid ();
            String[] params = new String[] { userId , token };
            tokenUpdateTarget.doInBackground ( params );
        }

        searchText = findViewById(R.id.input_search);
        searchButton = findViewById(R.id.delivery_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //Alert Dialogs
                popUp ( view );

                //Keyboard weg
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        searchText.setOnEditorActionListener ( new TextView.OnEditorActionListener () {
            @Override
            public boolean onEditorAction ( TextView v , int actionId , KeyEvent event ) {
                if ( actionId == EditorInfo.IME_ACTION_SEARCH ) {
                    //Alert Dialogs
                    popUp ( searchButton );
                    return true;
                }
                return false;
            }
        } );


        if ( ContextCompat.checkSelfPermission ( this , Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission ( this , Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            String[] permissions = new String[] { Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission.ACCESS_FINE_LOCATION };
            ActivityCompat.requestPermissions ( this , permissions , 0 );
        }


        fusedLocationProviderClient.requestLocationUpdates ( locationRequest , new LocationCallback () {
            Location lastLocation;

            @Override
            public void onLocationResult ( LocationResult locationResult ) {
                if ( locationResult == null ) {
                    return;
                } else if ( locationResult.getLastLocation ().equals ( lastLocation ) ) {
                    Log.i ( "MainActivity" , "No new location." );
                } else {
                    Location location = locationResult.getLastLocation ();
                    lastLocation = location;

                    try {
                        /*User loggedInUser = LoginActivity.loginViewModel.getLoggedInUser ();
                        com.fhaachen.ip_ritz.prototyp.data.model.Location userLocation = new com.fhaachen.ip_ritz.prototyp.data.model.Location ( (float)location.getLatitude (), (float)location.getLongitude () );
                        ArrayList<com.fhaachen.ip_ritz.prototyp.data.model.Location> currentLocation = loggedInUser.getCurrentLocation ();
                        currentLocation.add(userLocation);
                        loggedInUser.setCurrentLocation ( currentLocation );*/
                    } catch ( Exception e ) {
                        e.printStackTrace ();
                    }

                    Log.i ( "MainActivity" , "Location updated." );

                    mMap.clear ();
                    MarkerOptions mp = new MarkerOptions ();
                    mp.position ( new LatLng ( location.getLatitude () , location.getLongitude () ) );
                    mp.title ( "my position" );
                    mMap.addMarker ( mp );
                    mMap.animateCamera ( CameraUpdateFactory.newLatLngZoom (
                            new LatLng ( location.getLatitude () , location.getLongitude () ) , 16 ) );
                }
            }
        } , null );
            /*fusedLocationProviderClient.getLastLocation ().addOnSuccessListener ( this , new OnSuccessListener < Location > () {
                @Override
                public void onSuccess ( Location location ) {
                    if (location != null) {

                    }
                }
            } )*/


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng loc = new LatLng(latitudeNow, longitudeNow);
        mMap.addMarker(new MarkerOptions().position(loc).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Log.i("MainActivity", "Navigation item selected: Home");
        } else if (id == R.id.nav_bookings) {
            Log.i("ShowBookingActivity", "Navigation item selected: Bookings");
            Intent i = new Intent(getApplicationContext(), ShowBookingActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_payments) {
            Log.i("MainActivity", "Navigation item selected: Payments");
        } else if (id == R.id.nav_payments) {
            Log.i("MainActivity", "Navigation item selected: Payments");
        } else if (id == R.id.nav_contact) {
            Log.i("MainActivity", "Navigation item selected: Contact");
        } else if (id == R.id.nav_friends) {
            Log.i("MainActivity", "Navigation item selected: Friends");
            Intent in = new Intent(getApplicationContext(), FriendsActivity.class);
            startActivity(in);
        } else if (id == R.id.nav_about) {
            Log.i("MainActivity", "Navigation item selected: About");
        } else if (id == R.id.nav_privacy) {
            Log.i("MainActivity", "Navigation item selected: Privacy");
        } else if (id == R.id.nav_logout) {
            Log.i("MainActivity", "Navigation item selected: Logout");
            LoginActivity.loginViewModel.logout ();
            Intent i = new Intent ( getApplicationContext () , LoginActivity.class );
            startActivity ( i );
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void popUp ( final View view ) {
        new AlertDialog.Builder ( view.getContext () )
                .setTitle ( "New Booking" )
                .setMessage ( "What do you want to receive?" )

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton ( "Flight" , new DialogInterface.OnClickListener () {
                    public void onClick ( DialogInterface dialog , int which ) {
                        chosenType = "Flight";
                        new AlertDialog.Builder ( view.getContext () )
                                .setTitle ( "New Booking" )
                                .setMessage ( "How fast?" )

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.

                                .setPositiveButton ( "Fast" , new DialogInterface.OnClickListener () {
                                    public void onClick ( DialogInterface dialog , int which ) {
                                        chosenTime = "Fast";
                                        Log.i ( "NewFlightActivity" , "Flight is pressed" );
                                        Intent i = new Intent ( getApplicationContext () , NewFlightActivity.class );
                                        i.putExtra ( "type" , chosenTime );
                                        i.putExtra ( "text" , searchText.getText ().toString () );
                                        startActivity ( i );
                                    }
                                } )
                                .setNeutralButton ( "Normal" , new DialogInterface.OnClickListener () {
                                    public void onClick ( DialogInterface dialog , int which ) {
                                        chosenTime = "Normal";
                                        Log.i ( "NewFlightActivity" , "Flight is pressed" );
                                        Intent i = new Intent ( getApplicationContext () , NewFlightActivity.class );
                                        i.putExtra ( "type" , chosenTime );
                                        i.putExtra ( "text" , searchText.getText ().toString () );
                                        startActivity ( i );
                                    }
                                } )
                                .show ();
                    }
                } )

                .setNeutralButton ( "Order" , new DialogInterface.OnClickListener () {
                    public void onClick ( DialogInterface dialog , int which ) {
                        chosenType = "Order";
                        new AlertDialog.Builder ( view.getContext () )
                                .setTitle ( "New Order" )
                                .setMessage ( "How fast?" )

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton ( "Normal" , new DialogInterface.OnClickListener () {
                                    public void onClick ( DialogInterface dialog , int which ) {
                                        chosenTime = "Normal";
                                        Log.i ( "NewOrderActivity" , "Order is pressed" );
                                        Intent i = new Intent ( getApplicationContext () , NewOrderAcitivity.class );
                                        i.putExtra ( "type" , chosenTime );
                                        i.putExtra ( "text" , searchText.getText ().toString () );
                                        startActivity ( i );
                                    }
                                } )
                                .setNeutralButton ( "Fast" , new DialogInterface.OnClickListener () {
                                    public void onClick ( DialogInterface dialog , int which ) {
                                        chosenTime = "Fast";
                                        Log.i ( "NewOrderActivity" , "Order is pressed" );
                                        Intent i = new Intent ( getApplicationContext () , NewOrderAcitivity.class );
                                        i.putExtra ( "type" , chosenTime );
                                        i.putExtra ( "text" , searchText.getText ().toString () );
                                        startActivity ( i );
                                    }
                                } )
                                .show ();
                    }
                } )
                .show ();
    }
}
