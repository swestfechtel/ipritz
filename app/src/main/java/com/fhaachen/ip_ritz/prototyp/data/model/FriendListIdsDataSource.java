package com.fhaachen.ip_ritz.prototyp.data.model;

import android.os.AsyncTask;
import android.util.Log;
import com.fhaachen.ip_ritz.prototyp.data.LoginDataSource;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FriendListIdsDataSource extends AsyncTask < String, Integer, ArrayList < String > > {
    @Override
    public ArrayList < String > doInBackground ( String... params ) {
        String userId = params[ 0 ];
        final ArrayList < String > ids = new ArrayList <> ( 0 );

        ids.add ( "5cea970061ba485d14002da9" );
        ids.add ( "5ce7c9d6b84ce6277400640b" ); // stub weil noch keine Daten eingepflegt...

        try {
            URL server = new URL ( LoginDataSource.serverAddress + "/user.php?id=" + userId );
            HttpURLConnection connection = ( HttpURLConnection ) server.openConnection ();

            if ( connection.getResponseCode () != 200 ) {
                throw new RuntimeException ( "Failed: HTTP error code: " + connection.getResponseCode () );
            }

            JsonParser jsonParser = new JsonParser ();
            JsonElement jsonElement = jsonParser.parse ( new InputStreamReader ( ( InputStream ) connection.getContent () ) );
            JsonObject jsonObject = jsonElement.getAsJsonObject ();
            JsonArray friendsArray = jsonObject.get ( "friends" ).getAsJsonArray ();

            for ( JsonElement element : friendsArray ) {
                //JsonObject id = element.getAsJsonObject ();
                ids.add ( element.toString () );
                Log.i ( "FriendsActivity" , "Added id to arraylist: " + element.toString () );
            }
            connection.disconnect ();

            return ids;

            //TODO: parse json file
        } catch ( Exception e ) {
            e.printStackTrace ();
            Log.e ( "FriendsActivity" , "URL connection error. " + e.getMessage () );
            return ids;
        }
    }
}
