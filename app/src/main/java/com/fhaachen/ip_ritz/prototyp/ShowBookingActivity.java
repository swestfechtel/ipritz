package com.fhaachen.ip_ritz.prototyp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class ShowBookingActivity extends AppCompatActivity {

    private ImageButton mBackButton;
    private TextView BookingButton;
    private TextView OrderButton;
    private ListView bookings;
    private ListView orders;
    private FloatingActionButton newFlight;
    private FloatingActionButton newOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showbooking);

        mBackButton = findViewById(R.id.bookingBackButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MainActivity.class);
                startActivity(i);
            }
        });
        bookings = findViewById(R.id.bookingListView);
        orders = findViewById(R.id.ordersListView);
        newFlight = findViewById(R.id.newBooking);
        newOrder = findViewById(R.id.newOrder);
        BookingButton = findViewById(R.id.bookingHeader);
        BookingButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                orders.setVisibility(View.GONE);
                bookings.setVisibility(View.VISIBLE);
                BookingButton.setTextColor(getColor(R.color.colorPrimary));
                OrderButton.setTextColor(getColor(R.color.colorBlack));
                newOrder.setVisibility(View.GONE);
                newFlight.setVisibility(View.VISIBLE);
            }
        });
        OrderButton = findViewById(R.id.shoppingHeader);
        OrderButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                bookings.setVisibility(View.GONE);
                orders.setVisibility(View.VISIBLE);
                OrderButton.setTextColor(getColor(R.color.colorPrimary));
                BookingButton.setTextColor(getColor(R.color.colorBlack));
                newFlight.setVisibility(View.GONE);
                newOrder.setVisibility(View.VISIBLE);
            }
        });
        newFlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        newOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("NewOrderActivity", "new Order");
                Intent i = new Intent(view.getContext(), NewOrderAcitivity.class);
                startActivity(i);
            }
        });
        final ListView listViewb = findViewById(R.id.bookingListView);
        final ListView listViewo = findViewById(R.id.ordersListView);
        /* Hier spaeter json object von rest api parsen */
        String[] bookings = new String[]{
                "Eupenerstraße 70;Nebenstraße 34", "Hauptstraße 1;Postgasse 4", "Testweg 99;Breucheweg 78"
        };
        String[] orders = new String[]{
                "Media Markt Aachen;Zuhause", "Edeka Zur Heide;Speyerweg 19"
        };
        /* Jedes Listenelement muss spaeter eine eindeutige Id bekommen,
         * um dann das entsprechende Profil zu laden
         * => arraylist<int>, fuer jedes erhaltene json object die id
         * in die arraylist packen, dann entspricht die position in der
         * arraylist der position in der listview...
         * */
        final Integer[] ids = new Integer[]{
                1, 2, 3
        };

        /*final ArrayList<String> list = new ArrayList<String>(friends.length);
        for (String s : friends)
            list.add(s);*/

        final CustomArrayAdapter adapterb = new CustomArrayAdapter(getApplicationContext(), bookings);
        listViewb.setAdapter(adapterb);

        listViewb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("BookingItem", "List item + " + position + " selected.");
                int BookingdId = ids[position];
                /* hier dann spaeter das entsprechende Profil laden */
                Intent i = new Intent(view.getContext(), ShowBookingActivity.class);
                startActivity(i);
            }
        });
        final CustomArrayAdapter adaptero = new CustomArrayAdapter(getApplicationContext(), orders);
        listViewo.setAdapter(adaptero);

        listViewo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("BookingItem", "List item + " + position + " selected.");
                int BookingdId = ids[position];
                /* hier dann spaeter das entsprechende Profil laden */
                Intent i = new Intent(view.getContext(), ShowBookingActivity.class);
                startActivity(i);
            }
        });
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
            View rowView = inflater.inflate(R.layout.booking_list, parent, false);
            TextView start = rowView.findViewById(R.id.ItemStart);
            TextView goal = rowView.findViewById(R.id.ItemEnd);
            String[] g = values[position].split(";");
            start.setText(g[0]);
            goal.setText(g[1]);
            return rowView;
        }
    }
}
