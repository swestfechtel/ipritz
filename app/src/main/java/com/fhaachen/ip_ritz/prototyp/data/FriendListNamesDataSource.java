package com.fhaachen.ip_ritz.prototyp.data;

import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FriendListNamesDataSource extends AsyncTask < ArrayList < String >, Integer, ArrayList < String > > {
    @Override
    public ArrayList < String > doInBackground ( ArrayList < String >... params ) {
        final ArrayList < String > names = new ArrayList <> ( 0 );
        for ( String id : params[ 0 ] ) {
            try {
                //URL server = new URL ( LoginDataSource.serverAddress + "/user.php?id=" + id );
                URL server = new URL ( LoginDataSource.serverAddress + "/user/" + id );
                HttpURLConnection connection = ( HttpURLConnection ) server.openConnection ();

                if ( connection.getResponseCode () != 200 ) {
                    throw new RuntimeException ( "Failed: HTTP error code: " + connection.getResponseCode () );
                }

                JsonParser jsonParser = new JsonParser ();
                JsonElement jsonElement = jsonParser.parse ( new InputStreamReader ( ( InputStream ) connection.getContent () ) );
                JsonObject jsonObject = jsonElement.getAsJsonObject ();

                names.add ( jsonObject.get ( "firstName" ).getAsString () + " " + jsonObject.get ( "lastName" ).getAsString () );


                connection.disconnect ();
                //TODO: parse json file
            } catch ( Exception e ) {
                e.printStackTrace ();
                Log.e ( "FriendsActivity" , "URL connection error. " + e.getMessage () );
                return names;
            }
        }
        return names;
    }
}
