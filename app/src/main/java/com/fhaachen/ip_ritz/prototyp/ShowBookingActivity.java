package com.fhaachen.ip_ritz.prototyp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.fhaachen.ip_ritz.prototyp.data.OrderDataSource;
import com.fhaachen.ip_ritz.prototyp.data.model.Order;
import com.fhaachen.ip_ritz.prototyp.ui.login.LoginActivity;

import java.util.ArrayList;

public class ShowBookingActivity extends AppCompatActivity {

    private ImageButton mBackButton;
    private TextView BookingButton;
    private TextView OrderButton;
    private ListView bookings;
    private ListView orders;
    private FloatingActionButton newFlight;
    private FloatingActionButton newOrder;

    protected String chosenTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_showbooking );

        mBackButton = findViewById ( R.id.bookingBackButton );
        mBackButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick ( View v ) {
                finish ();
            }
        } );
        bookings = findViewById ( R.id.bookingListView );
        orders = findViewById ( R.id.ordersListView );
        newFlight = findViewById ( R.id.newBooking );
        newOrder = findViewById ( R.id.newOrder );
        BookingButton = findViewById ( R.id.bookingHeader );
        BookingButton.setOnClickListener ( new View.OnClickListener () {
            @SuppressLint( "RestrictedApi" )
            @RequiresApi( api = Build.VERSION_CODES.M )
            @Override
            public void onClick ( View view ) {
                orders.setVisibility ( View.GONE );
                bookings.setVisibility ( View.VISIBLE );
                BookingButton.setTextColor ( getColor ( R.color.colorPrimary ) );
                OrderButton.setTextColor ( getColor ( R.color.colorBlack ) );
                newOrder.setVisibility ( View.GONE );
                newFlight.setVisibility ( View.VISIBLE );
            }
        } );
        OrderButton = findViewById ( R.id.shoppingHeader );
        OrderButton.setOnClickListener ( new View.OnClickListener () {
            @SuppressLint( "RestrictedApi" )
            @RequiresApi( api = Build.VERSION_CODES.M )
            @Override
            public void onClick ( View view ) {
                bookings.setVisibility ( View.GONE );
                orders.setVisibility ( View.VISIBLE );
                OrderButton.setTextColor ( getColor ( R.color.colorPrimary ) );
                BookingButton.setTextColor ( getColor ( R.color.colorBlack ) );
                newFlight.setVisibility ( View.GONE );
                newOrder.setVisibility ( View.VISIBLE );
            }
        } );
        newFlight.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick ( View view ) {
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

                                startActivity ( i );
                            }
                        } )
                        .setNeutralButton ( "Normal" , new DialogInterface.OnClickListener () {
                            public void onClick ( DialogInterface dialog , int which ) {
                                chosenTime = "Normal";
                                Log.i ( "NewFlightActivity" , "Flight is pressed" );
                                Intent i = new Intent ( getApplicationContext () , NewFlightActivity.class );
                                i.putExtra ( "type" , chosenTime );

                                startActivity ( i );
                            }
                        } )
                        .show ();
            }
        } );
        newOrder.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick ( View view ) {
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
                                startActivity ( i );
                            }
                        } )
                        .setNeutralButton ( "Normal" , new DialogInterface.OnClickListener () {
                            public void onClick ( DialogInterface dialog , int which ) {
                                chosenTime = "Normal";
                                Log.i ( "NewFlightActivity" , "Flight is pressed" );
                                Intent i = new Intent ( getApplicationContext () , NewFlightActivity.class );
                                i.putExtra ( "type" , chosenTime );
                                startActivity ( i );
                            }
                        } )
                        .show ();
            }
        } );

    }

    @Override
    public void onResume () {
        super.onResume ();
        final ListView listView = findViewById ( R.id.bookingListView );

        final ArrayList < String > journeys = LoginActivity.loginViewModel.getLoggedInUser ().getJourneys ();
        ArrayList < Order > orders = new ArrayList <> ();
        OrderDataSource orderDataSource = new OrderDataSource ();

        for ( String id : journeys ) {
            if ( id != null ) orders.add ( orderDataSource.doInBackground ( id ) );
        }
        final ShowBookingActivity.CustomArrayAdapter adapter = new ShowBookingActivity.CustomArrayAdapter ( getApplicationContext () , orders );
        listView.setAdapter ( adapter );

        listView.setOnItemClickListener ( new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick ( AdapterView < ? > parent , View view , int position , long id ) {
                Log.i ( "MyBookingsActivity" , "List item + " + position + " selected." );
                String bookingId = journeys.get ( position );
            }
        } );
    }

    class CustomArrayAdapter extends ArrayAdapter < Order > {
        private final Context context;
        private final ArrayList < Order > values;

        public CustomArrayAdapter ( Context context , ArrayList < Order > values ) {
            super ( context , -1 , values );
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView ( int position , View convertView , ViewGroup parent ) {
            LayoutInflater inflater = ( LayoutInflater ) context.getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
            View rowView = inflater.inflate ( R.layout.booking_list , parent , false );
            TextView firstLine = rowView.findViewById ( R.id.ItemStart );
            TextView secondLine = rowView.findViewById ( R.id.ItemEnd );

            firstLine.setText ( values.get ( position ).getStartAddress () );
            secondLine.setText ( values.get ( position ).getDestinationAddress () );

            return rowView;
        }
    }
}
