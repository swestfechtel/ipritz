package com.fhaachen.ip_ritz.prototyp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.fhaachen.ip_ritz.prototyp.data.UserDataSource;
import com.fhaachen.ip_ritz.prototyp.data.model.User;

public class ProfileActivity extends AppCompatActivity {

    ImageButton locationButton;
    TextView profileName, profileLocation, profileAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    @Override
    protected void onResume () {
        super.onResume ();
        final String profileId = getIntent ().getStringExtra ( "profileId" );
        Log.i ( "ProfileActivity" , "Got profileId " + profileId );

        profileName = findViewById ( R.id.profileName );
        profileLocation = findViewById ( R.id.profileLocation );
        profileAge = findViewById ( R.id.profileAge );

        UserDataSource dataSource = new UserDataSource ();
        User user = dataSource.doInBackground ( profileId );
        profileName.setText ( user.getFirstName () + " " + user.getLastName () );
        profileLocation.setText ( user.getAddresses ().getCity () );
            profileAge.setText ( "TODO" );


        locationButton = findViewById ( R.id.profileLocationButton );
        locationButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick ( View v ) {
                Intent i = new Intent ( v.getContext () , MapsActivity.class );
                i.putExtra ( "friendId" , profileId );
                startActivity ( i );
            }
        } );
    }
}
