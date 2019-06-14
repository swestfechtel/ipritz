package com.fhaachen.ip_ritz.prototyp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewFlightActivity extends AppCompatActivity implements OnMapReadyCallback {

    public double latitudeFrom;
    public double longitudeFrom;
    public double latitudeTo;
    public double longitudeTo;

    private TextView price;
    private ImageButton flightBackButton;
    private ImageButton getRoute;
    private Button bookButton;
    private EditText flightTextTo;
    private AutoCompleteTextView flightTextFrom;

    private boolean aRouteIsShown = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;


    private static final String[] LOCATIONS = new String[]{
            "My location"
    };
    private static final String TAG = "NewFlightActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_flight);

        String[] locations = getResources().getStringArray(R.array.locations);
        flightTextFrom = findViewById ( R.id.flight_text_from );
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, locations);
        flightTextFrom.setAdapter(adapter);
        bookButton = findViewById(R.id.flight_button);
        flightTextTo = findViewById(R.id.flight_text_to);
        flightBackButton = findViewById(R.id.flightBackButton);
        bookButton = findViewById(R.id.flight_button);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        price = findViewById ( R.id.price );
        getRoute = findViewById(R.id.search_route_flight);
        if(getIntent().hasExtra("text") == true) {
            String text = getIntent().getExtras().getString("text");
            flightTextTo.setText(text);
        }
        flightBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i ( "NewFlightActivity" , "Go to MainActivity" );
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), WaitingActivity.class);
                startActivity(i);
            }
        });
        getRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i ( "NewFlightActivity" , "Show route startlocation to destination" );
                //Keyboard weg
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                if(flightTextFrom.getText().toString().equals("My location")){
                    //get current location
                    getLastKnownLocation();
                    geoLocateTo(flightTextTo);

                }else{

                    //find the location of the start address
                    geoLocateFrom(flightTextFrom);
                    //find the location of the destination address
                    geoLocateTo(flightTextTo);
                }

                if(aRouteIsShown){
                    //Clear the map, if a route is already shown
                    mMap.clear();
                }
                aRouteIsShown = true;
                price.setText ( setDynamicPrice () );
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(NewFlightActivity.this);

            }
        });
        flightTextTo.setOnEditorActionListener ( new TextView.OnEditorActionListener () {
            @Override
            public boolean onEditorAction ( TextView v , int actionId , KeyEvent event ) {
                if ( actionId == EditorInfo.IME_ACTION_SEARCH ) {

                    Log.i ( "NewFlightActivity" , "Show route startlocation to destination" );
                    //Keyboard weg
                    InputMethodManager inputManager = ( InputMethodManager )
                            getSystemService ( Context.INPUT_METHOD_SERVICE );

                    inputManager.hideSoftInputFromWindow ( getCurrentFocus ().getWindowToken () ,
                            InputMethodManager.HIDE_NOT_ALWAYS );
                    if ( flightTextFrom.getText ().toString ().equals ( "My location" ) ) {
                        //get current location
                        getLastKnownLocation ();
                        geoLocateTo ( flightTextTo );

                    } else {

                        //find the location of the start address
                        geoLocateFrom ( flightTextFrom );
                        //find the location of the destination address
                        geoLocateTo ( flightTextTo );
                    }

                    if ( aRouteIsShown ) {
                        //Clear the map, if a route is already shown
                        mMap.clear ();
                    }
                    aRouteIsShown = true;
                    price.setText ( setDynamicPrice () );
                    SupportMapFragment mapFragment = ( SupportMapFragment ) getSupportFragmentManager ()
                            .findFragmentById ( R.id.map );
                    mapFragment.getMapAsync ( NewFlightActivity.this );


                    return true;
                }
                return false;
            }
        } );

    }


    //search a location
    public void geoLocateFrom(EditText searchText){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = searchText.getText().toString();

        if(searchString != null) {

            Geocoder geocoder = new Geocoder(NewFlightActivity.this);
            List<Address> list = new ArrayList<>();
            try{
                list = geocoder.getFromLocationName(searchString, 1);
            }catch (IOException e){
                Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
            }

            if(list.size() > 0){

                Address address = list.get(0);

                Log.d(TAG, "geoLocate: found a location: " + address.toString());
                this.latitudeFrom= address.getLatitude();
                this.longitudeFrom= address.getLongitude();
                //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            }
        }else{

            Toast.makeText(this, "Enter the start address for your order", Toast.LENGTH_SHORT).show();

        }
    }


    public void geoLocateTo(EditText searchText){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = searchText.getText().toString();

        if(searchString != null) {

            Geocoder geocoder = new Geocoder(NewFlightActivity.this);
            List<Address> list = new ArrayList<>();
            try{
                list = geocoder.getFromLocationName(searchString, 1);
            }catch (IOException e){
                Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
            }

            if(list.size() > 0){

                Address address = list.get(0);

                Log.d(TAG, "geoLocate: found a location: " + address.toString());
                this.latitudeTo= address.getLatitude();
                this.longitudeTo= address.getLongitude();
                //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            }
        }else{

            Toast.makeText(this, "Enter the destination address for your order", Toast.LENGTH_SHORT).show();

        }
    }

    //Get actual Location
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    latitudeFrom = geoPoint.getLatitude(); //myLocationLatitude
                    longitudeFrom = geoPoint.getLongitude(); //myLocatonLongitude

                    Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude());
                    Log.d(TAG, "onComplete: longitude: " + geoPoint.getLongitude());
                }
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng from = new LatLng(this.latitudeFrom, this.longitudeFrom);
        LatLng destination = new LatLng(this.latitudeTo, this.longitudeTo);

        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add( from,  destination));
        polyline1.setStartCap(new RoundCap());
        polyline1.setEndCap(new RoundCap());
        polyline1.setColor(R.color.colorPrimary);

        mMap.addMarker(new MarkerOptions().position(from).title("Start point"));
        mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(from);
        builder.include(destination);
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        mMap.animateCamera(cu);

    }

    private String setDynamicPrice () {
        String price;


        Location loc1 = new Location ( "from" );
        loc1.setLatitude ( latitudeFrom );
        loc1.setLongitude ( longitudeFrom );

        Location loc2 = new Location ( "to" );
        loc2.setLatitude ( latitudeTo );
        loc2.setLongitude ( longitudeTo );

        double distanceInKiloMeters = ( loc1.distanceTo ( loc2 ) / 1000 );
        Bundle extras = getIntent ().getExtras ();
        String time = extras.getString ( "time" );
        double p;
        if ( time == "Normal" ) {
            p = distanceInKiloMeters * 5.2;

        } else {
            p = distanceInKiloMeters * 6.2;

        }
        p = Math.round ( p * 100.0 ) / 100.0;
        price = p + " €";

        return price;
    }
}
