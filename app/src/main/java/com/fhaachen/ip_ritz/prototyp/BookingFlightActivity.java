package com.fhaachen.ip_ritz.prototyp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookingFlightActivity extends AppCompatActivity implements OnMapReadyCallback {

    public double latitudeFrom;
    public double longitudeFrom;
    public double latitudeTo;
    public double longitudeTo;

    private ImageButton flightBackButton;
    private Button bookButton;
    private EditText flightTextTo;
    private AutoCompleteTextView flightTextFrom;

    private boolean mLocationPermissionGranted = false;
    private boolean aRouteIsShown = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;


    private static final String[] LOCATIONS = new String[]{
            "My location"
    };
    private static final String TAG = "BookingFlightActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_flight);

        String[] locations = getResources().getStringArray(R.array.locations);
        flightTextFrom = findViewById ( R.id.flight_text_from );
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, locations);
        flightTextFrom.setAdapter(adapter);

        flightTextTo = findViewById(R.id.flight_text_to);
        flightBackButton = findViewById(R.id.flightBackButton);
        bookButton = findViewById(R.id.book_button);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        flightBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish ();
            }
        });

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("BookingOrderActivity", "Show route startlocation to destination");

                if(flightTextFrom.getText().toString().equals("My location")){
                    //get current location
                    getLastKnownLocation();

                }else{

                    //find the location of the start address
                    geoLocateFrom(flightTextFrom);
                    //find the lcation of the destination address
                    geoLocateTo(flightTextTo);
                }

                if(aRouteIsShown){
                    //Clear the map, if a route is already shown
                    mMap.clear();
                }
                aRouteIsShown = true;

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(BookingFlightActivity.this);

            }
        });

    }


    //search a location
    public void geoLocateFrom(EditText searchText){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = searchText.getText().toString();

        if(searchString != null) {

            Geocoder geocoder = new Geocoder(BookingFlightActivity.this);
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

            Geocoder geocoder = new Geocoder(BookingFlightActivity.this);
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

        mMap.moveCamera(CameraUpdateFactory.newLatLng(from));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }
}
