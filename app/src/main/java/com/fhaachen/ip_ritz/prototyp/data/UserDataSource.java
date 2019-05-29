package com.fhaachen.ip_ritz.prototyp.data;

import android.os.AsyncTask;
import android.util.Log;
import com.fhaachen.ip_ritz.prototyp.data.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserDataSource extends AsyncTask < String, Float, User > {
    @Override
    public User doInBackground ( String... params ) {
        try {
            URL server = new URL ( LoginDataSource.serverAddress + "/user/" + params[ 0 ] );
            HttpURLConnection connection = ( HttpURLConnection ) server.openConnection ();

            if ( connection.getResponseCode () != 200 ) {
                throw new RuntimeException ( "Failed: HTTP error code: " + connection.getResponseCode () );
            }

            JsonParser jsonParser = new JsonParser ();
            JsonElement jsonElement = jsonParser.parse ( new InputStreamReader ( ( InputStream ) connection.getContent () ) );
            JsonObject rootObject = jsonElement.getAsJsonObject ();
            //JsonArray locationArray = rootObject.get ( "currentLocation" ).getAsJsonArray ();
            //JsonObject addressObject = rootObject.get ("addresses").getAsJsonObject ();

            Gson gson = new Gson ();
            User user = gson.fromJson ( rootObject , User.class );

            connection.disconnect ();
            return user;
            //TODO: get location information from friend
        } catch ( Exception e ) {
            e.printStackTrace ();
            Log.e ( "UserDataSource" , "Error: " + e.getMessage () );
            return null;
        }
    }
}
