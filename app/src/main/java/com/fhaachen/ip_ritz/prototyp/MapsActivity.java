package com.fhaachen.ip_ritz.prototyp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Button mFetchButton;
    private Button mNormalButton;

    private RelativeLayout mFetchFetchCancelButtonContainer;
    private LinearLayout mFetchOptionButtonContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFetchFetchCancelButtonContainer = findViewById(R.id.fetchFetchCancelButtonContainer);
        mFetchOptionButtonContainer = findViewById(R.id.fetchOptionButtonContainer);

        mFetchButton = findViewById(R.id.fetchFetchButton);
        mFetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFetchFetchCancelButtonContainer.setVisibility(View.GONE);
                mFetchOptionButtonContainer.setVisibility(View.VISIBLE);
            }
        });

        mNormalButton = findViewById(R.id.fetchNormalButton);
        mNormalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* wie schicke ich die map daten an das andere handy? */
                Intent i = new Intent(v.getContext(), AcceptBookingActivity.class);
                startActivity(i);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng westpark = new LatLng(50.771758, 6.068255);
        LatLng freundin = new LatLng(50.785474, 6.052972);

        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        westpark,
                        freundin));
        polyline1.setStartCap(new RoundCap());
        polyline1.setEndCap(new RoundCap());
        polyline1.setColor(R.color.colorPrimary);

        mMap.addMarker(new MarkerOptions().position(westpark).title("Westpark"));
        mMap.addMarker(new MarkerOptions().position(freundin).title("Freundin"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(westpark));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }
}
