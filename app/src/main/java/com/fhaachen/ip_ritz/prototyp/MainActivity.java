package com.fhaachen.ip_ritz.prototyp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.fhaachen.ip_ritz.prototyp.ui.login.LoginActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private ImageButton searchButton;
    private EditText searchText;

    protected String chosenType;
    protected String chosenTime;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private LocationManager locationManager;
    public double latitudeNow;
    public double longitudeNow;

    private TextView navHeaderName;
    private TextView navHeaderMail;

    private Toolbar toolbar;
    private DrawerLayout drawer;



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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        checkLocation(); //check whether location service is enable or not in your  phone
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
        }

        searchText = findViewById(R.id.input_search);
        searchButton = findViewById(R.id.delivery_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                new AlertDialog.Builder(view.getContext())
                        .setTitle("New Booking")
                        .setMessage("What do you want to receive?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("Flight", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                chosenType = "Flight";
                                new AlertDialog.Builder(view.getContext())
                                        .setTitle("New Booking")
                                        .setMessage("How fast?")

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.

                                        .setPositiveButton("Fast", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                chosenTime = "Fast";
                                                Log.i("NewFlightActivity", "Flight is pressed");
                                                Intent i = new Intent(getApplicationContext(), NewFlightActivity.class);
                                                i.putExtra("type", chosenTime);
                                                i.putExtra("text", searchText.getText().toString());
                                                startActivity(i);
                                            }
                                        })
                                        .setNeutralButton("Normal", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                chosenTime = "Normal";
                                                Log.i("NewFlightActivity", "Flight is pressed");
                                                Intent i = new Intent(getApplicationContext(), NewFlightActivity.class);
                                                i.putExtra("type", chosenTime);
                                                i.putExtra("text", searchText.getText().toString());
                                                startActivity(i);
                                            }
                                        })
                                        .show();
                            }
                        })

                        .setNeutralButton("Order", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                chosenType= "Order";
                                new AlertDialog.Builder(view.getContext())
                                        .setTitle("New Order")
                                        .setMessage("How fast?")

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton("Normal", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                chosenTime = "Normal";
                                                Log.i("NewOrderActivity", "Order is pressed");
                                                Intent i = new Intent(getApplicationContext(), NewOrderAcitivity.class);
                                                i.putExtra("type", chosenTime);
                                                i.putExtra("text", searchText.getText().toString());
                                                startActivity(i);
                                            }
                                        })
                                        .setNeutralButton("Fast", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                chosenTime = "Fast";
                                                Log.i("NewOrderActivity", "Order is pressed");
                                                Intent i = new Intent(getApplicationContext(), NewOrderAcitivity.class);
                                                i.putExtra("type", chosenTime);
                                                i.putExtra("text", searchText.getText().toString());
                                                startActivity(i);
                                            }
                                        })
                                        .show();
                            }
                        })
                        .show();
                //Keyboard weg
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Construct a GeoDataClient.
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
        } else if(id == R.id.nav_delivery){
            Log.i("MainActivity", "Navigation item selected: Delivery");
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
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


    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {

            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("MainActivity", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("MainActivity", "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onLocationChanged(Location location) {

        mMap.clear();

        MarkerOptions mp = new MarkerOptions();

        mp.position(new LatLng(location.getLatitude(), location.getLongitude()));

        mp.title("my position");

        mMap.addMarker(mp);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 16));
    }

    private boolean checkLocation() {

        return isLocationEnabled();
    }



    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
