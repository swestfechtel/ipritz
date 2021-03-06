package com.fhaachen.ip_ritz.prototyp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fhaachen.ip_ritz.prototyp.data.OrderDataCreationTarget;
import com.fhaachen.ip_ritz.prototyp.data.model.Order;
import com.fhaachen.ip_ritz.prototyp.data.model.User;
import com.fhaachen.ip_ritz.prototyp.ui.login.LoginActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NewFlightActivity extends AppCompatActivity implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener,  TimePickerFragment.TimePickerListener {

    public double latitudeFrom;
    public double longitudeFrom;
    public double latitudeTo;
    public double longitudeTo;
    private Address startAddress, destAddress;


    private TextView price;
    //private ImageButton flightBackButton;
    private ImageButton getRoute;
    private AutoCompleteTextView flightTextTo;
    private Button bookButton;
    private ImageButton switchButton;

    private AutoCompleteTextView flightTextFrom;

    private boolean aRouteIsShown = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;

    //Add Stopover option
    public double latitudeStopover;
    public double longitudeStopover;

    private EditText flightTextStopover;
    private TextView flightStopover;

    private ImageButton flightAddStopover;
    private ImageButton flightRemoveStopover;

    private boolean stopoverIsDemanded = false;
    //Add Stopover option end

    //Pick date
    private EditText flightTextDepartureDate;
    private ImageButton buttonDepartureDate;
    //Pick date end

    //Pick time
    private EditText flightTextDepatureTime;
    private ImageButton buttonDepatureTime;
    //pick time end

    //Adjust arrival time
    private double speed = 60; // MaxSpeed 60 km/h for Parrot Bebop 2
    private TextView flightTextArrivalTime;
    //Adjust arrival time end

    //Send notification
    private NotificationManagerCompat notificationManager;
    //Send Notification end

    //Show popup to warn user about the entered start and destiantion Addresses
    private AlertDialog.Builder builder;
    //Show popup to warn user about the entered start and destiantion Addresses -- end

    private static final String[] LOCATIONS = new String[]{
            "My location"
            //hier Locations in FH Aachen Eup Str hinzufügen
    };
    private static final String TAG = "NewFlightActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_flight);

        //set back button on action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //set back button on action bar end

        String[] locations = getResources().getStringArray(R.array.locations);
        flightTextFrom = findViewById ( R.id.flight_text_from );
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, locations);
        flightTextFrom.setAdapter(adapter);

        bookButton = findViewById(R.id.flight_button);
        flightTextTo = findViewById(R.id.flight_text_to);
        flightTextTo.setAdapter(adapter);
        //flightBackButton = findViewById(R.id.flightBackButton);
        switchButton = findViewById(R.id.btn_switch_start_end_flight);
        //Insert Stopover
        flightTextStopover = findViewById ( R.id.flight_text_stopover );
        flightStopover = findViewById ( R.id.flight_stopover );
        flightAddStopover = findViewById ( R.id.flight_add_stopover );
        flightRemoveStopover = findViewById ( R.id.flight_remove_stopover );
        flightRemoveStopover.setVisibility(View.GONE);
        flightStopover.setVisibility(View.GONE);
        flightTextStopover.setVisibility(View.GONE);
        //Insert Stop over end

        //Pick date
        flightTextDepartureDate = findViewById ( R.id.flight_text_departure_date );
        final SimpleDateFormat datumsformat = new SimpleDateFormat("dd/MM/yyyy");
        flightTextDepartureDate.setText(datumsformat.format(Calendar.getInstance().getTime()));
        buttonDepartureDate = findViewById ( R.id.button_departure_date );

        buttonDepartureDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        //Pick date end

        //Pick time
        flightTextDepatureTime = findViewById ( R.id.flight_text_departure_time );
        final SimpleDateFormat zeitformat = new SimpleDateFormat("HH:mm");
        flightTextDepatureTime.setText(zeitformat.format(Calendar.getInstance().getTime()));
        buttonDepatureTime = findViewById ( R.id.button_departure_time );
        buttonDepatureTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.setCancelable(false);
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        //pick time end

        //Adjust arrival time
        flightTextArrivalTime = findViewById ( R.id.flight_text_arrival_time );
        //Adjust arrival time end

        //Send notification
        notificationManager = NotificationManagerCompat.from(this);
        //Send notification end

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        price = findViewById ( R.id.price );
        getRoute = findViewById(R.id.search_route_flight);


        if(getIntent().hasExtra("text") == true) {
            String text = getIntent().getExtras().getString("text");
            flightTextTo.setText(text);
        }

        //Insert Stopover
        flightAddStopover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flightAddStopover.setVisibility(View.GONE);
                flightRemoveStopover.setVisibility(View.VISIBLE);
                flightStopover.setVisibility(View.VISIBLE);
                flightTextStopover.setVisibility(View.VISIBLE);

            }
        });

        flightRemoveStopover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopoverIsDemanded = false;
                flightRemoveStopover.setVisibility(View.GONE);
                flightAddStopover.setVisibility(View.VISIBLE);
                flightStopover.setVisibility(View.GONE);
                flightTextStopover.setVisibility(View.GONE);
            }
        });
        //Insert Stopver end
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String to = flightTextTo.getText().toString();
                String from = flightTextFrom.getText().toString();
                flightTextTo.setText(from);
                flightTextFrom.setText(to);

            }
        });
        /*flightBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i ( "NewFlightActivity" , "Go to MainActivity" );
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });*/


        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    User loggedInUser = LoginActivity.loginViewModel.getLoggedInUser ();
                    ArrayList < String > startLocation = new ArrayList < String > ();
                    startLocation.add ( String.valueOf ( latitudeFrom ) );
                    startLocation.add ( String.valueOf ( longitudeFrom ) );

                    ArrayList < String > destinationLocation = new ArrayList < String > ();
                    destinationLocation.add ( String.valueOf ( latitudeTo ) );
                    destinationLocation.add ( String.valueOf ( longitudeTo ) );

                    ArrayList < String > passengers = new ArrayList < String > ();
                    passengers.add ( loggedInUser.get_id ().get$oid () );

                    String startAddressString = startAddress.getAddressLine(0);
                    String destinationAddressString = destAddress.getAddressLine ( 0 );

                    final Order order = new Order(loggedInUser.get_id().get$oid(), startLocation, destinationLocation);
                    order.setPassengers ( passengers );
                    order.setStartAddress ( startAddressString );
                    order.setDestinationAddress ( destinationAddressString );

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OrderDataCreationTarget dataTarget = new OrderDataCreationTarget();
                            dataTarget.doInBackground(order);
                        }
                    }).start();

                    /*String orderId = dataTarget.doInBackground ( order );
                    Log.i ( "NewFlightActivity" , orderId );

                    Constants.CURRENT_ORDER = orderId;

                    UserDataSource dataSource = new UserDataSource ();

                    ArrayList < String > journeys;
                    if ( ( journeys = loggedInUser.getJourneys () ) == null ) {
                        journeys = new ArrayList <> ();
                    }
                    journeys.add ( orderId );
                    loggedInUser.setJourneys ( journeys );

                    UserDataUpdateTarget userDataUpdateTarget = new UserDataUpdateTarget ();
                    userDataUpdateTarget.doInBackground ( loggedInUser );*/
                    
                    String depDate =flightTextDepartureDate.getText().toString();
                    String curDate = datumsformat.format(Calendar.getInstance().getTime());
                    if(curDate.equals(depDate)){
                        String cTime = zeitformat.format(Calendar.getInstance().getTime());
                        String deptime[] = flightTextDepatureTime.getText().toString().split(":");
                        String currentTime[] = cTime.toString().split(":");
                        if( ( Integer.parseInt(deptime[0]) - Integer.parseInt(currentTime[0])) == 0){
                            if((Integer.parseInt(deptime[1]) - Integer.parseInt(currentTime[1])) <= 15){
                                Intent i = new Intent ( view.getContext () , WaitingActivity.class );
                                i.putExtra("startLat", startAddress.getLatitude());
                                i.putExtra("startLong", startAddress.getLongitude());


                                startActivity ( i );
                            }

                        }
                        else if ((Integer.parseInt(deptime[0]) - Integer.parseInt(currentTime[0])) == 1){
                            if(Integer.parseInt(currentTime[1]) > 45){
                                if((Integer.parseInt(deptime[1])- Integer.parseInt(currentTime[1])) >= - 45){
                                    Intent i = new Intent ( view.getContext () , WaitingActivity.class );
                                    i.putExtra("startLat", startAddress.getLatitude());
                                    i.putExtra("startLong", startAddress.getLongitude());


                                    startActivity ( i );
                                }
                            }
                        }

                    }
                    else {


                        Intent i = new Intent(view.getContext(), MainActivity.class);
                        Context context = getApplicationContext();
                        CharSequence text = "Ihre Order wurde gespeichert. Sie werden informiert, wenn ihr Flugtaxi da ist.";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        startActivity(i);

                    }

                } catch ( Exception e ) {
                    e.printStackTrace ();
                }

            }
        });


        getRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i ( "NewFlightActivity" , "Show route startlocation to destination" );

                //Show popup to warn user about the entered start and destination Addresses
                if(flightTextFrom.getText().toString().isEmpty() || flightTextTo.getText().toString().isEmpty()) {
                    showWarningMessage(v);

                    //Show popup to warn user about the entered start and destination Addresses -- end
                }else if(!flightTextFrom.getText().toString().isEmpty() && !flightTextTo.getText().toString().isEmpty()){

                    //Keyboard weg
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    if(flightTextFrom.getText().toString().equals("My location")){
                        //get current location
                        getLastKnownLocation(0);

                    }else {

                        //find the location of the start address
                        geoLocateFrom(flightTextFrom);
                    }
                    //find the location of the destination address
                    if(flightTextTo.getText().toString().equals("My location")){
                        getLastKnownLocation(1);
                    }
                    else {
                        geoLocateTo(flightTextTo);
                    }
                    //Check if a stopover is demanded and if the user entered an address
                    if(flightTextStopover.getVisibility() == View.VISIBLE && !flightTextStopover.toString().isEmpty()){

                        stopoverIsDemanded = true;
                        //get the location of th stopover
                        geoLocateTo(flightTextStopover);

                    }

                }




                if(aRouteIsShown){
                    //Clear the map, if a route is already shown
                    mMap.clear();
                }
                aRouteIsShown = true;
                price.setText ( setDynamicPrice () );

                //Adjust arrival time
                setArrivalTime(flightTextDepatureTime);
                //Adjust arrival time end

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(NewFlightActivity.this);

            }
        });



    }

    //Show popup to warn user about the entered start and destiantion Addresses
    public void showWarningMessage(View view){

        //Show popup to warn user about the entered start and destiantion Addresses
        builder = new AlertDialog.Builder(this);
        //Setting message manually and performing action on button click
        builder.setMessage("Please insert a start and a destination address!")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                                           }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("WARNING!");
        alert.show();
    }
    //Show popup to warn user about the entered start and destiantion Addresses -- end

    //set back button on action bar
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }
    //set back button on action bar end

    //Send notification
    public void startService(View v) {
        Log.d(TAG, "Startservice");
        String input = "Your Delivery item just arrived!" + "\n" + "Please pick it up!";

        Intent serviceIntent = new Intent(this, NotificationService.class);
        serviceIntent.putExtra("inputExtra", input);

        ContextCompat.startForegroundService(this, serviceIntent);

        Log.d(TAG, "Startservice end");
    }

    public void stopService(View v) {
        Log.d(TAG, "Stopservice");
        Intent serviceIntent = new Intent(this, NotificationService.class);
        stopService(serviceIntent);
        Log.d(TAG, "Stopservice end");
    }
    //Send notification end

    //Pick date
    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = "" + dayOfMonth + "/" + month  + "/" + year;
        flightTextDepartureDate.setText(date);
    }
    //Pick date end


    //Pick time
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        String hourText = "";
        String minuteText = "";

        if(hour < 10){
            hourText = "0" + hour;
        }else{
            hourText = "" + hour;
        }

        if(minute < 10){
            minuteText = "0" + minute;
        }else{
            minuteText = "" + minute;
        }

        flightTextDepatureTime.setText("" + hourText + ":" + minuteText);
    }
    //Pick time end


    //search a location
    @WorkerThread
    public void geoLocateFrom(EditText searchText){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = searchText.getText().toString();

        if(!searchString.isEmpty()) {

            /*Geocoder geocoder = new Geocoder(NewFlightActivity.this);
            List<Address> list = new ArrayList<>();
            try{
                list = geocoder.getFromLocationName(searchString, 1);
            }catch (IOException e){
                Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
            }*/
            GeoLocateAsync geoLocateAsync = new GeoLocateAsync();
            List<Address> list = geoLocateAsync.doInBackground(searchString);

            if(list.size() > 0){

                startAddress = list.get ( 0 );

                Log.d ( TAG , "geoLocate: found a location: " + startAddress.toString () );
                this.latitudeFrom = startAddress.getLatitude ();
                this.longitudeFrom = startAddress.getLongitude ();
                //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            }
        }else{

            Toast.makeText(this, "Enter the start address for your order", Toast.LENGTH_SHORT).show();

        }
    }

    @WorkerThread
    public void geoLocateTo(EditText searchText){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = searchText.getText().toString();

        if(!searchString.isEmpty()) {

            /*Geocoder geocoder = new Geocoder(NewFlightActivity.this);
            List<Address> list = new ArrayList<>();
            try{
                list = geocoder.getFromLocationName(searchString, 1);
            }catch (IOException e){
                Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
            }*/
            GeoLocateAsync geoLocateAsync = new GeoLocateAsync();
            List<Address> list = geoLocateAsync.doInBackground(searchString);

            if(list.size() > 0){

                destAddress = list.get ( 0 );

                Log.d ( TAG , "geoLocate: found a location: " + destAddress.toString () );

                //search the location for the given stopover
                if(stopoverIsDemanded){

                    this.latitudeStopover = destAddress.getLatitude ();
                    this.longitudeStopover = destAddress.getLongitude ();

                }else{

                    this.latitudeTo = destAddress.getLatitude ();
                    this.longitudeTo = destAddress.getLongitude ();
                }

                //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            }
        }else{

            Toast.makeText(this, "Enter the destination address for your order", Toast.LENGTH_SHORT).show();

        }

        // FÜR TEST:
       /* this.destAddress = "E"
        this.latitudeTo = 50.7585445;
        this.longitudeTo = 6.0824400;*/
    }

    //Get actual Location
    private void getLastKnownLocation(final int i)  { //0 = Start, 1 = Destination
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
                    if(i == 0) {
                        latitudeFrom = geoPoint.getLatitude(); //myLocationLatitude
                        longitudeFrom = geoPoint.getLongitude(); //myLocatonLongitude
                    }
                    if(i == 1) {
                        latitudeTo = geoPoint.getLatitude(); //myLocationLatitude
                        longitudeTo = geoPoint.getLongitude(); //myLocatonLongitude
                    }
                    try {
                        /*Geocoder geocoder = new Geocoder ( getApplicationContext () , Locale.getDefault () );
                        List < Address > addresses = geocoder.getFromLocation ( latitudeFrom , longitudeFrom , 1 );*/
                        GeoLocateAsyncLL geoLocateAsyncLL = new GeoLocateAsyncLL();
                        List<Address> addresses = geoLocateAsyncLL.doInBackground(latitudeFrom, longitudeFrom);
                        startAddress = addresses.get ( 0 );
                    } catch ( Exception e ) {
                        e.printStackTrace ();
                    }
                    Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude());
                    Log.d(TAG, "onComplete: longitude: " + geoPoint.getLongitude());
                }
            }
        });
    }

    class GeoLocateAsync extends AsyncTask<String, Integer, List<Address>> {
        @Override
        public List<Address> doInBackground(String... params) {
            String searchString = params[0];
            if (!searchString.isEmpty()) {
                Geocoder geocoder = new Geocoder(NewFlightActivity.this);
                List<Address> addresses = new ArrayList<>();
                try {
                    addresses = geocoder.getFromLocationName(searchString, 1);
                } catch (IOException e) {
                    Log.e("NewFlightActivity", e.getMessage());
                }
                return addresses;
            } else return null;
        }
    }

    class GeoLocateAsyncLL extends AsyncTask<Double, Integer, List<Address>> {
        @Override
        public List<Address> doInBackground(Double... params) {
            double latitudeFrom = params[0];
            double longitudeFrom = params[1];

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = new ArrayList<>();
            try {
                addresses = geocoder.getFromLocation(latitudeFrom, longitudeFrom, 1);
            } catch (IOException e) {
                Log.e("NewFlightActivity", e.getMessage());
            }
            return addresses;
        }
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

            //show route from start point to stopover and from stopover to destination
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

        double distanceFromToStopopver = 0.0;
        double distanceStopopverToDestination = 0.0;
        double distanceFromToDestination = 0.0; //in Km

        Location locFrom = new Location ( "from" );
        locFrom .setLatitude ( latitudeFrom );
        locFrom .setLongitude ( longitudeFrom );

        Location locDestination = new Location ( "to" );
        locDestination .setLatitude ( latitudeTo );
        locDestination .setLongitude ( longitudeTo );

        Location locStopover = new Location ( "stopover" );
        if(stopoverIsDemanded){

            locStopover .setLatitude ( latitudeStopover);
            locStopover .setLongitude ( longitudeStopover);

            distanceFromToStopopver = ( locFrom.distanceTo (locStopover) / 1000 );
            distanceStopopverToDestination = ( locStopover.distanceTo (locDestination) / 1000 );
            distanceFromToDestination = distanceFromToStopopver + distanceStopopverToDestination;

        }else{

            distanceFromToDestination = ( locFrom.distanceTo (locDestination) / 1000 );

        }



        Bundle extras = getIntent ().getExtras ();
        String time = extras.getString ( "time" );
        double p;
        if ( time == "Normal" ) {
            p =  distanceFromToDestination * 5.2;

        } else {
            p =  distanceFromToDestination * 6.2;

        }
        p = Math.round ( p * 100.0 ) / 100.0;
        price = p + " €";

        return price;
    }

    //Adjust arrival time
    private void setArrivalTime (EditText depaturetime) {

        double distanceFromToStopopver = 0.0;
        double distanceStopopverToDestination = 0.0;
        double distanceFromToDestination = 0.0; //in Km

        Location locFrom = new Location ( "from" );
        locFrom .setLatitude ( latitudeFrom );
        locFrom .setLongitude ( longitudeFrom );

        Location locDestination = new Location ( "to" );
        locDestination .setLatitude ( latitudeTo );
        locDestination .setLongitude ( longitudeTo );

        Location locStopover = new Location ( "stopover" );
        if(stopoverIsDemanded){

            locStopover .setLatitude ( latitudeStopover);
            locStopover .setLongitude ( longitudeStopover);

            distanceFromToStopopver = ( locFrom.distanceTo (locStopover) / 1000 );
            distanceStopopverToDestination = ( locStopover.distanceTo (locDestination) / 1000 );
            distanceFromToDestination = distanceFromToStopopver + distanceStopopverToDestination;

        }else{

            distanceFromToDestination = ( locFrom.distanceTo (locDestination) / 1000 );

        }

        Log.d(TAG, "distanceFromToDestination : " + distanceFromToDestination);

        //Calculate the flight duration
        String flightDurationText = depaturetime.getText().toString();
        String[] time = flightDurationText.split ( ":" );
        Log.d(TAG, "hour: " + time[0]);
        Log.d(TAG, "hour: " + time[1]);
        int hour = Integer.parseInt(time[0]);
        int min = Integer.parseInt(time[1]);

        int flightDuration = (int)Math.ceil( distanceFromToDestination/(speed/60) ) ;//t = d/speed in min (aufgerundet)

       if( (min + flightDuration) > 59){

           hour = hour + (min + flightDuration)/60;
           min = min - (int)Math.ceil( ((double)(min + flightDuration)%60) );
           Log.d(TAG, "hour: " + hour);
           Log.d(TAG, "min: " + min);

       }else{

           min = min + flightDuration;
           Log.d(TAG, "hour: " + hour);
           Log.d(TAG, "min: " + min);
       }
       //Calculate the flight duration end

        // Adjust the arrival time
        String hourText = "";
        String minText = "";
        if(hour < 10){
            hourText = "0"+ hour;
        }else{
            hourText = ""+ hour;
        }

        if(min < 10){
            minText = "0"+ min;
        }else{
            minText = ""+ min;
        }
        flightTextArrivalTime.setText("" + hourText + ":" + minText);
        //Adjust the arrival time end

    }
}
//Adjust arrival time

