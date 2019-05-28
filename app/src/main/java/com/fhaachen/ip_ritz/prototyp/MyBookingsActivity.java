package com.fhaachen.ip_ritz.prototyp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_my_bookings);

        mBackButton = findViewById(R.id.bookingsBackButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MainActivity.class);
                startActivity(i);
            }
        });

        addBooking = findViewById(R.id.add_booking_button);
        addBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show popup window
                showPopupBookingArt();
            }
        });


        final ListView listView = findViewById(R.id.bookingsListView);

        /* Hier spaeter json object von rest api parsen */
        String[] bookings = new String[]{
                "Booking 1", "Booking 2", "Booking 3", "Booking 4"
        };
        /* Jedes Listenelement muss spaeter eine eindeutige Id bekommen,
         * um dann das entsprechende Profil zu laden
         * => arraylist<int>, fuer jedes erhaltene json object die id
         * in die arraylist packen, dann entspricht die position in der
         * arraylist der position in der listview...
         * */
        final Integer[] ids = new Integer[]{
                1, 2, 3, 4
        };

        /*final ArrayList<String> list = new ArrayList<String>(friends.length);
        for (String s : friends)
            list.add(s);*/

        final CustomArrayAdapter adapter = new CustomArrayAdapter(getApplicationContext(), bookings);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("MyBookingsActivity", "List item + " + position + " selected.");
                int bookingId = ids[position];

                //hier später info zum Booking anzeigen
                /* hier dann spaeter das entsprechende Profil laden */
                //Intent i = new Intent(view.getContext(), ProfileActivity.class);
                //startActivity(i);
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

    class CustomArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public CustomArrayAdapter(Context context, String[] values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.bookings_list, parent, false);
            TextView firstLine = rowView.findViewById(R.id.bookingsItemFirstLine);
            TextView secondLine = rowView.findViewById(R.id.bookingsItemSecondLine);
            //ImageView profileImage = rowView.findViewById(R.id.friendsItemImage);

            firstLine.setText(values[position]);
            secondLine.setText("Location placeholder");
           // profileImage.setImageResource(R.mipmap.ic_launcher_round);

            return rowView;
        }
    }

    private void showPopupBookingArt(){

        myBookingsLayout = (RelativeLayout) findViewById(R.id.my_bookings_layout);


        //instantiate the popup.xml layout file
        LayoutInflater layoutInflater = (LayoutInflater) MyBookingsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.bookingart_popup,null);

        closePopupButton = (ImageButton) customView.findViewById(R.id.close_popup_button);

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

        myBookingsLayout = (RelativeLayout) findViewById(R.id.my_bookings_layout);


        //instantiate the popup.xml layout file
        LayoutInflater layoutInflater = (LayoutInflater) MyBookingsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.bookingspeed_popup,null);

        closePopupButton = (ImageButton) customView.findViewById(R.id.close_popup_button);

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

}
