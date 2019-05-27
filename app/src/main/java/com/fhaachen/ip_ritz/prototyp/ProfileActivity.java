package com.fhaachen.ip_ritz.prototyp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.fhaachen.ip_ritz.prototyp.data.LoginDataSource;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    ImageButton locationButton;
    TextView profileName, profileLocation, profileAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String profileId = getIntent ().getStringExtra ( "profileId" );

        profileName = findViewById ( R.id.profileName );
        profileLocation = findViewById ( R.id.profileLocation );
        profileAge = findViewById ( R.id.profileAge );

        try {
            URL server = new URL ( LoginDataSource.serverAddress + "/user.php?id=" + profileId );
            Log.i ( "ProfileActivity" , "URL is " + LoginDataSource.serverAddress + "/user.php?id=" + profileId );
            HttpURLConnection connection = ( HttpURLConnection ) server.openConnection ();

            if ( connection.getResponseCode () != 200 ) {
                throw new RuntimeException ( "Failed: HTTP error code: " + connection.getResponseCode () );
            }

            JsonParser jsonParser = new JsonParser ();
            JsonElement jsonElement = jsonParser.parse ( new InputStreamReader ( ( InputStream ) connection.getContent () ) );
            JsonObject jsonObject = jsonElement.getAsJsonObject ();

            String name = "", firstName = jsonObject.get ( "firstName" ).getAsString (), lastName = jsonObject.get ( "lastName" ).getAsString ();
            name = firstName + " " + lastName;
            Log.i ( "ProfileActivity" , jsonObject.get ( "firstName" ).getAsString () );
            Log.i ( "ProfileActivity" , jsonObject.get ( "lastName" ).getAsString () );
            Log.i ( "ProfileActivity" , name );
            Log.i ( "ProfileActivity" , jsonObject.get ( "addresses" ).getAsJsonObject ().get ( "city" ).getAsString () );
            profileName.setText ( name );
            profileLocation.setText ( jsonObject.get ( "addresses" ).getAsJsonObject ().get ( "city" ).getAsString () );
            profileAge.setText ( "TODO" );

        } catch ( Exception e ) {
            e.printStackTrace ();
            Log.e ( "ProfileActivity" , "URL connection error. " + e.getMessage () );
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
