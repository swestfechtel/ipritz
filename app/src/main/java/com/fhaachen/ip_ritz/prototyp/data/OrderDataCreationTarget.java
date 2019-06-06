package com.fhaachen.ip_ritz.prototyp.data;

import android.os.AsyncTask;
import android.util.Log;
import com.fhaachen.ip_ritz.prototyp.data.model.Order;
import com.google.gson.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OrderDataCreationTarget extends AsyncTask < Order, Float, String > {
    @Override
    public String doInBackground ( Order... params ) {
        try {
            URL server = new URL ( LoginDataSource.serverAddress + "/orders" );
            HttpURLConnection connection = ( HttpURLConnection ) server.openConnection ();
            connection.setDoOutput ( true );
            connection.setRequestMethod ( "POST" );
            connection.setRequestProperty ( "Content-Type" , "application/json" );

            Gson gson = new Gson ();
            String payload = gson.toJson ( params[ 0 ] );

            OutputStream os = connection.getOutputStream ();
            os.write ( payload.getBytes () );
            os.flush ();

            JsonParser jsonParser = new JsonParser ();
            JsonElement jsonElement = jsonParser.parse ( new InputStreamReader ( ( InputStream ) connection.getContent () ) );
            JsonObject rootObject = jsonElement.getAsJsonObject ();

            if ( connection.getResponseCode () != 200 ) {
                throw new RuntimeException ( "Failed : HTTP error code : "
                        + connection.getResponseCode () );
            }

            if ( rootObject.get ( "$oid" ).isJsonNull () ) {
                throw new JsonParseException ( "Error fetching order id: $oid was null." );
            }

            String orderId;

            if ( ( orderId = rootObject.get ( "$oid" ).getAsString () ).length () <= 0 ) {
                throw new JsonParseException ( "Error fetching order id: $oid was empty string." );
            }

            connection.disconnect ();
            return orderId;
        } catch ( Exception e ) {
            e.printStackTrace ();
            Log.e ( "ProfileActivity" , "URL connection error. " + e.getMessage () );
            return null;
        }
    }
}
