package com.fhaachen.ip_ritz.prototyp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.fhaachen.ip_ritz.prototyp.data.OrderDataSource;
import com.fhaachen.ip_ritz.prototyp.data.model.Order;
import com.fhaachen.ip_ritz.prototyp.ui.login.LoginActivity;

import java.util.ArrayList;

public class MyBookingsActivity extends AppCompatActivity {

    private ImageButton mBackButton;
    private FloatingActionButton addBooking;
    private Button mCancelButton;

    private ImageButton closePopupButton;
    private Button buttonFlight;
    private Button buttonOrder;
    private Button buttonNormal;
    private Button buttonFast;
    private String bookingChoice;

    RelativeLayout myBookingsLayout;
    PopupWindow popupWindow;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_my_bookings);

        mBackButton = findViewById(R.id.bookingsBackButton);
        mBackButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v) {
                finish ();
            }
        });

        addBooking = findViewById(R.id.add_booking_button);
        addBooking.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v) {
                //show popup window
                showPopupBookingArt();
            }
        });




        /*mCancelButton = findViewById(R.id.bookingItemCancelButton);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cancel a booking
                Log.i("MyBookingsActivity", "Cancel item");
            }
        });*/
    }

    @Override
    public void onResume () {
        super.onResume ();
        final ListView listView = findViewById(R.id.bookingsListView);

        final ArrayList < String > journeys = LoginActivity.loginViewModel.getLoggedInUser ().getJourneys ();
        ArrayList < Order > orders = new ArrayList <> ();
        OrderDataSource orderDataSource = new OrderDataSource ();

        for ( String id : journeys ) {
            orders.add ( orderDataSource.doInBackground ( id ) );
        }

        final CustomArrayAdapter adapter = new CustomArrayAdapter ( getApplicationContext () , orders );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id) {
                Log.i("MyBookingsActivity", "List item + " + position + " selected.");
                String bookingId = journeys.get ( position );
            }
        });
    }

    private void showPopupBookingArt(){

        myBookingsLayout = findViewById ( R.id.my_bookings_layout );


        //instantiate the popup.xml layout file
        LayoutInflater layoutInflater = (LayoutInflater) MyBookingsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.bookingart_popup,null);

        closePopupButton = customView.findViewById ( R.id.close_popup_button );

        //instantiate popup window
        popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //display the popup window
        popupWindow.showAtLocation(myBookingsLayout, Gravity.CENTER, 0, 0);

        //close the popup window on button click
        closePopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        buttonFlight = customView.findViewById(R.id.button_flight);
        buttonOrder = customView.findViewById(R.id.button_order);

        buttonFlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookingChoice = buttonFlight.getText().toString();
                Log.i("MyBookingsActivity", "Ask how fast(normal/fast?). " + bookingChoice );
                showPopupBookingSpeed(bookingChoice);
            }
        });

        buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookingChoice = buttonOrder.getText().toString();
                Log.i("MyBookingsActivity", "Ask how fast(normal/fast?). " + bookingChoice);
                showPopupBookingSpeed(bookingChoice);
            }
        });

    }

    private void showPopupBookingSpeed(final String bookChoice){

        myBookingsLayout = findViewById ( R.id.my_bookings_layout );


        //instantiate the popup.xml layout file
        LayoutInflater layoutInflater = (LayoutInflater) MyBookingsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.bookingspeed_popup,null);

        closePopupButton = customView.findViewById ( R.id.close_popup_button );

        //instantiate popup window
        popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //display the popup window
        popupWindow.showAtLocation(myBookingsLayout, Gravity.CENTER, 0, 0);

        //close the popup window on button click
        closePopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        buttonNormal = customView.findViewById(R.id.button_normal);
        buttonFast = customView.findViewById(R.id.button_fast);

        buttonNormal.setOnClickListener(new View.OnClickListener() {
            String bChoice = bookChoice;
            @Override
            public void onClick(View v) {

                Log.i("MyBookingsActivity", "Go to BookingFlightActivity" + bookingChoice);
                if(bookingChoice.equals("flight")){
                    Log.i("MyBookingsActivity", "Go to BookingFlightActivity"  );
                    Intent i = new Intent(getApplicationContext(), BookingFlightActivity.class);
                    startActivity(i);
                }else if(bookingChoice.equals("order")){
                    Log.i("BookingActivity", "Go to BookingOrderActivity" + bChoice);
                    Intent i = new Intent(getApplicationContext(), BookingOrderActivity.class);
                    startActivity(i);
                }
            }
        });

        buttonFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MyBookingsActivity", "Go to BookingFlightActivity" + bookingChoice);
                if(bookingChoice.equals("flight")){
                    Log.i("MyBookingsActivity", "Go to BookingFlightActivity");
                    Intent i = new Intent(getApplicationContext(), BookingFlightActivity.class);
                    startActivity(i);
                }else if(bookingChoice.equals("order")){
                    Log.i("BookingActivity", "Go to BookingOrderActivity" +bookingChoice);
                    Intent i = new Intent(getApplicationContext(), BookingOrderActivity.class);
                    startActivity(i);
                }
            }
        });

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
