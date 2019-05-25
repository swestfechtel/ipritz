package com.fhaachen.ip_ritz.prototyp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.fhaachen.ip_ritz.prototyp.Constants.ERROR_DIALOG_REQUEST;
import static com.fhaachen.ip_ritz.prototyp.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.fhaachen.ip_ritz.prototyp.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;



public class NewOrderAcitivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private static final String TAG = "NewOrderactivity";
    private double destinationLatitude;
    private double destinationLongitude;
    private double myLocationLatitude;
    private double myLocationLongitude;
    private boolean mLocationPermissionGranted = false;
    private boolean aRouteIsShown = false;
    private String searchText;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchText = getIntent().getExtras().getString("destination");
        setContentView(R.layout.activity_new_order);
        getLastKnownLocation();
        //find location of destination
        geoLocate(searchText);
        //show route on map
        if(aRouteIsShown){
            //Clear the map,if a route is already show
            mMap.clear();
        }
        aRouteIsShown = true;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


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
                    myLocationLatitude = geoPoint.getLatitude();
                    myLocationLongitude = geoPoint.getLongitude();

                    Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude());
                    Log.d(TAG, "onComplete: longitude: " + geoPoint.getLongitude());
                }
            }
        });


    }
    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

            getLastKnownLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(NewOrderAcitivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(NewOrderAcitivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){

                    getLastKnownLocation();
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }


    //search a location
    public void geoLocate(String searchText){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = searchText;

        if(searchString == null){

            Toast.makeText(this, "Enter the destination of your Delivery", Toast.LENGTH_SHORT).show();

        }

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
            this.destinationLatitude = address.getLatitude();
            this.destinationLongitude= address.getLongitude();
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.delivery_search_button:{
                Log.i("DeliveryActivity", "Show route on map");
                getLastKnownLocation();
                //find location of destination
                geoLocate(this.searchText);
                //show route on map
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }

            case R.id.delivery_back_button:{
                Log.i("DeliveryActivity", "Go Back to MainActivity");
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng myLocation = new LatLng(this.myLocationLatitude, this.myLocationLongitude);
        LatLng destination = new LatLng(this.destinationLatitude, this.destinationLongitude);

        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add( myLocation,  destination));
        polyline1.setStartCap(new RoundCap());
        polyline1.setEndCap(new RoundCap());
        polyline1.setColor(R.color.colorPrimary);

        mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
       // mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
    }
}
