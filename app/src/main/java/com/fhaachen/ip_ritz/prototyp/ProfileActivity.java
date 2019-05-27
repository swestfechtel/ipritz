package com.fhaachen.ip_ritz.prototyp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    ImageButton locationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String profileId = getIntent ().getStringExtra ( "profileId" );

        try {
            URL server = new URL ( "http://149.201.48.86:8001/app/api/user/" + profileId );
            HttpURLConnection connection = ( HttpURLConnection ) server.openConnection ();
            connection.setRequestMethod ( "GET" );
            connection.setRequestProperty ( "Accept" , "application/json" );
            connection.connect ();

            if ( connection.getResponseCode () != 200 ) {
                throw new RuntimeException ( "Failed: HTTP error code: " + connection.getResponseCode () );
            }

            //TODO: load profile information
        } catch ( Exception e ) {
            Log.e ( "ProfileActivity" , "URL connection error" );
            Log.e ( "ProfileActivity" , e.getLocalizedMessage () );
        }

        locationButton = findViewById(R.id.profileLocationButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MapsActivity.class);
                i.putExtra ( "friendId" , profileId );
                startActivity(i);
            }
        });
    }
}
