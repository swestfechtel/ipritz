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



public class NewOrderAcitivity extends AppCompatActivity implements  OnMapReadyCallback {

    public double latitudeFrom;
    public double longitudeFrom;
    public double latitudeTo;
    public double longitudeTo;
    public double latitudeStopover;
    public double longitudeStopover;

    private ImageButton orderBackButton;
    private Button orderButton;
    private EditText orderTextFrom;
    private AutoCompleteTextView orderTextTo;
    private ImageButton getRoute;
    private ImageButton orderAddStopover;
    private ImageButton orderRemoveStopover;

    private EditText orderTextStopover;
    private TextView orderStopover;

    private TextView price;
    private boolean mLocationPermissionGranted = false;
    private boolean stopoverIsDemanded = false;
    private boolean aRouteIsShown = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;


    private static final String[] LOCATIONS = new String[]{
            "My location"
    };
    private static final String TAG = "NewOrderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        String[] locations = getResources().getStringArray(R.array.locations);
        orderTextTo = findViewById ( R.id.order_text_to );
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, locations);
        orderTextTo.setAdapter(adapter);
        orderTextFrom = findViewById(R.id.order_text_from);
        orderBackButton = findViewById(R.id.orderBackButton);
        orderButton = findViewById(R.id.order_button);

        //Insert Stopover
        orderTextStopover = (EditText) findViewById(R.id.order_text_stopover);
        orderStopover = (TextView) findViewById(R.id.order_stopover);
        orderAddStopover = (ImageButton) findViewById(R.id.order_add_stopover);
        orderRemoveStopover = (ImageButton) findViewById(R.id.order_remove_stopover);
        orderRemoveStopover.setVisibility(View.GONE);
        orderStopover.setVisibility(View.GONE);
        orderTextStopover.setVisibility(View.GONE);
        //Insert Stop over end

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getRoute = findViewById(R.id.search_route_order);
        price = findViewById ( R.id.price_output );
        orderBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("NewOrderActivity", "Go to MainActivity");
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });


        if(getIntent().hasExtra("text") == true) {
            String text = getIntent().getExtras().getString("text");
            orderTextFrom.setText(text);
        }

        //Insert Stopover
        orderAddStopover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                orderAddStopover.setVisibility(View.GONE);
                orderRemoveStopover.setVisibility(View.VISIBLE);
                orderStopover.setVisibility(View.VISIBLE);
                orderTextStopover.setVisibility(View.VISIBLE);

            }
        });

        orderRemoveStopover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopoverIsDemanded = false;
                orderRemoveStopover.setVisibility(View.GONE);
                orderAddStopover.setVisibility(View.VISIBLE);
                orderStopover.setVisibility(View.GONE);
                orderTextStopover.setVisibility(View.GONE);
            }
        });
        //Insert Stopver end


        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), WaitingActivity.class);
                startActivity(i);
            }
        });
        getRoute.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v) {
                Log.i("BookingOrderActivity", "Show route startlocation to destination");
//Keyboard weg
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow ( getCurrentFocus ().getWindowToken () ,
                        InputMethodManager.HIDE_NOT_ALWAYS );
                if ( orderTextTo.getText ().toString ().equals ( "My location" ) ) {
                    //get current location
                    getLastKnownLocation ();
                    geoLocateFrom ( orderTextFrom );

                    //Check if a stopover is demanded and if the user entered an address
                    if(orderTextStopover.getVisibility() == View.VISIBLE && !orderTextStopover.toString().isEmpty()){

                        stopoverIsDemanded = true;
                        //get the location of th stopover
                        geoLocateTo(orderTextStopover);

                    }

                } else {

                    //find the location of the start address
                    geoLocateFrom ( orderTextFrom );
                    //find the location of the destination address
                    geoLocateTo ( orderTextTo );

                    //Check if a stopover is demanded and if the user entered an address
                    if(orderTextStopover.getVisibility() == View.VISIBLE && !orderTextStopover.toString().isEmpty()){

                        stopoverIsDemanded = true;
                        //get the location of th stopover
                        geoLocateTo(orderTextStopover);

                    }
                }

                if ( aRouteIsShown ) {
                    //Clear the map, if a route is already shown
                    mMap.clear ();
                }
                aRouteIsShown = true;
                price.setText ( setDynamicPrice () );
                SupportMapFragment mapFragment = ( SupportMapFragment ) getSupportFragmentManager ()
                        .findFragmentById ( R.id.map );
                mapFragment.getMapAsync ( NewOrderAcitivity.this );

            }
        } );


        orderTextTo.setOnEditorActionListener ( new TextView.OnEditorActionListener () {
            @Override
            public boolean onEditorAction ( TextView v , int actionId , KeyEvent event ) {
                if ( actionId == EditorInfo.IME_ACTION_SEARCH ) {

                    Log.i ( "BookingOrderActivity" , "Show route startlocation to destination" );
                    //Keyboard weg
                    InputMethodManager inputManager = ( InputMethodManager )
                            getSystemService ( Context.INPUT_METHOD_SERVICE );

                    inputManager.hideSoftInputFromWindow ( getCurrentFocus ().getWindowToken () ,
                            InputMethodManager.HIDE_NOT_ALWAYS );
                    if ( orderTextTo.getText ().toString ().equals ( "My location" ) ) {
                        //get current location
                        getLastKnownLocation ();
                        geoLocateFrom ( orderTextFrom );

                        //Check if a stopover is demanded and if the user entered an address
                        if(orderTextStopover.getVisibility() == View.VISIBLE && !orderTextStopover.toString().isEmpty()){

                            stopoverIsDemanded = true;
                            //get the location of th stopover
                            geoLocateTo(orderTextStopover);

                        }

                    } else {

                        //find the location of the start address
                        geoLocateFrom ( orderTextFrom );
                        //find the lcation of the destination address
                        geoLocateTo ( orderTextTo );

                        //Check if a stopover is demanded and if the user entered an address
                        if(orderTextStopover.getVisibility() == View.VISIBLE && !orderTextStopover.toString().isEmpty()){

                            stopoverIsDemanded = true;
                            //get the location of th stopover
                            geoLocateTo(orderTextStopover);

                        }
                    }

                    if ( aRouteIsShown ) {
                        //Clear the map, if a route is already shown
                        mMap.clear ();
                    }
                    aRouteIsShown = true;
                    price.setText ( setDynamicPrice () );
                    SupportMapFragment mapFragment = ( SupportMapFragment ) getSupportFragmentManager ()
                            .findFragmentById ( R.id.map );
                    mapFragment.getMapAsync ( NewOrderAcitivity.this );
                    return true;
                }
                return false;
            }
        });
    }


    //search a location
    public void geoLocateFrom(EditText searchText){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = searchText.getText().toString();

        if(!searchString.isEmpty()) {

            Geocoder geocoder = new Geocoder(NewOrderAcitivity.this);
            List<Address> list = new ArrayList<>();
            try{
                list = geocoder.getFromLocationName(searchString, 1);
            }catch (IOException e){
                Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
            }

            if(list.size() > 0){

                Address address = list.get(0);

                Log.d(TAG, "geoLocate: found a location: " + address.toString());

                //search the location for the given stepover
                if(stopoverIsDemanded){

                    this.latitudeStopover = address.getLatitude();
                    this.longitudeStopover = address.getLongitude();

                }else{

                    this.latitudeTo = address.getLatitude();
                    this.longitudeTo = address.getLongitude();
                }
                //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            }
        }else{

            Toast.makeText(this, "Enter the start address for your order", Toast.LENGTH_SHORT).show();

        }
    }


    public void geoLocateTo(EditText searchText){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = searchText.getText().toString();

        if(!searchString.isEmpty()) {

            Geocoder geocoder = new Geocoder(NewOrderAcitivity.this);
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
                    latitudeTo = geoPoint.getLatitude(); //myLocationLatitude
                    longitudeTo = geoPoint.getLongitude(); //myLocatonLongitude

                    Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude());
                    Log.d(TAG, "onComplete: longitude: " + geoPoint.getLongitude());
                }
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        // Add a marker in Sydney and move the camera
        LatLng from = new LatLng(this.latitudeFrom, this.longitudeFrom);
        LatLng destination = new LatLng(this.latitudeTo, this.longitudeTo);

        //first check if a stopover is demanded
        if(this.stopoverIsDemanded){

            LatLng stopover = new LatLng(this.latitudeStopover, this.longitudeStopover);

            //show route from start point to stopover
            Polyline polylineStartToStopover = mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .add( from,  stopover));
            polylineStartToStopover.setStartCap(new RoundCap());
            polylineStartToStopover.setEndCap(new RoundCap());
            polylineStartToStopover.setColor(R.color.colorPrimary);

            Polyline polylineStopoverToDestination = mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .add( stopover,  destination));
            polylineStopoverToDestination.setStartCap(new RoundCap());
            polylineStopoverToDestination.setEndCap(new RoundCap());
            polylineStopoverToDestination.setColor(R.color.colorPrimary);

            mMap.addMarker(new MarkerOptions().position(from).title("Start point"));
            mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));
            mMap.addMarker(new MarkerOptions().position(destination).title("Stopover"));

            builder.include(from);
            builder.include(destination);
            builder.include(stopover);
        }else{

            //show route from start point to destination
            Polyline polylineStartToDestination = mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .add( from,  destination));
            polylineStartToDestination.setStartCap(new RoundCap());
            polylineStartToDestination.setEndCap(new RoundCap());
            polylineStartToDestination.setColor(R.color.colorPrimary);

            mMap.addMarker(new MarkerOptions().position(from).title("Start point"));
            mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));

            builder.include(from);
            builder.include(destination);

        }

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
