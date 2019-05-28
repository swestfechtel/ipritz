package com.fhaachen.ip_ritz.prototyp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import com.fhaachen.ip_ritz.prototyp.ui.login.LoginActivity;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;

    private TextView navHeaderName;
    private TextView navHeaderMail;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private FrameLayout flSearch;
    private ImageButton imageButton;
    private EditText searchText;
    private ImageButton closePopupButton;
    private Button buttonMyself;
    private Button buttonSomebody;

    DrawerLayout drawerLayout;
    PopupWindow popupWindow;


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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    @Override
    public void onResume () {
        super.onResume ();

        if ( LoginActivity.loginViewModel != null && LoginActivity.loginViewModel.getLoggedInUser () == null ) {
            Intent i = new Intent ( getApplicationContext () , LoginActivity.class );
            startActivity ( i );
        }

        if ( LoginActivity.loginViewModel != null && LoginActivity.loginViewModel.getLoggedInUser () != null ) {
            navHeaderName.setText ( LoginActivity.loginViewModel.getLoggedInUser ().getDisplayName () + " " + LoginActivity.loginViewModel.getLoggedInUser ().getSurname () );
            navHeaderMail.setText ( LoginActivity.loginViewModel.getLoggedInUser ().getMailAddress () );
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Construct a GeoDataClient.


        LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location;

        if (network_enabled) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(location!=null) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                Log.i ( "MainActivity" , "Setting own location to " + loc.toString () );
                mMap.addMarker(new MarkerOptions().position(loc).title("Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15) );

            }
        }

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

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i("Main Activity", s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPopup(){

       drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        //instantiate the popup.xml layout file
        LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.booking_popup,null);

        closePopupButton = (ImageButton) customView.findViewById(R.id.close_popup_button);

        //instantiate popup window
        popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //display the popup window
        popupWindow.showAtLocation(drawerLayout, Gravity.CENTER, 0, 0);

        //close the popup window on button click
        closePopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        buttonMyself = customView.findViewById(R.id.btn_myself);
        buttonSomebody = customView.findViewById(R.id.btn_somebody);

        buttonMyself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("BookingActivity", "Go to SearchLocationActivity");
                Intent i = new Intent(getApplicationContext(), MyBookingsActivity.class);
                startActivity(i);
            }
        });

        buttonSomebody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("BookingActivity", "Go to SearchLocationActivity");
                Intent i = new Intent(getApplicationContext(), SearchLocationActivity.class);
                startActivity(i);
            }
        });

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Log.i("MainActivity", "Navigation item selected: Home");
        } else if (id == R.id.nav_bookings) {
            Log.i("MainActivity", "Navigation item selected: Bookings");
            //Intent in = new Intent(getApplicationContext(), BookingActivity.class);
            //startActivity(in);
            showPopup();
        } else if (id == R.id.nav_payments) {
            Log.i("MainActivity", "Navigation item selected: Payments");
        } else if(id == R.id.nav_delivery){

            Log.i("MainActivity", "Navigation item selected: Delivery");
            Intent i = new Intent(getApplicationContext(), DeliveryActivity.class);
           startActivity(i);

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
}
