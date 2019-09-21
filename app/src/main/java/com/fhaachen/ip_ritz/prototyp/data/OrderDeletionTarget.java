package com.fhaachen.ip_ritz.prototyp.data;

import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OrderDeletionTarget extends AsyncTask < String, Float, Boolean > {
    @Override
    public Boolean doInBackground ( String... params ) {
        try {
            URL server = new URL ( LoginDataSource.serverAddress + "/orders" );
            HttpURLConnection connection = ( HttpURLConnection ) server.openConnection ();
            connection.setDoOutput ( true );
            connection.setRequestMethod ( "DELETE" );
            connection.setRequestProperty ( "Content-Type" , "application/json" );

            String payload = " { \"id\": \" " + params[ 0 ] + "\" }";
            Log.i ( "OrderDeletionTarget" , server.toString () );
            Log.i ( "OrderDeletionTarget" , payload );
            OutputStream os = connection.getOutputStream ();
            os.write ( payload.getBytes () , 0 , payload.getBytes ().length );
            os.flush ();
            os.close ();
            Log.i ( "OrderDeletionTarget" , " " + connection.getResponseCode () );
            connection.disconnect ();
            return true;
        } catch ( Exception e ) {
            e.printStackTrace ();
            Log.e ( "OrderDeletionTarget" , "URL connection error. " + e.getMessage () );
            return null;
        }
    }
}
