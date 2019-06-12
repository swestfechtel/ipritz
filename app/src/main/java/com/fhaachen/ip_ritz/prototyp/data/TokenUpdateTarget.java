package com.fhaachen.ip_ritz.prototyp.data;

import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TokenUpdateTarget extends AsyncTask < String[], Float, Boolean > {
    @Override
    public Boolean doInBackground ( String[]... params ) {
        try {
            Log.i ( "TokenUpdateTarget" , " " + params[ 0 ][ 0 ] );
            URL server = new URL ( LoginDataSource.serverAddress + "/firebase/" + params[ 0 ][ 0 ] );
            HttpURLConnection connection = ( HttpURLConnection ) server.openConnection ();
            connection.setDoOutput ( true );
            connection.setRequestMethod ( "PUT" );
            connection.setRequestProperty ( "Content-Type" , "application/json" );

            Log.i ( "TokenUpdateTarget" , " " + params[ 0 ][ 1 ] );
            String payload = " { \"firebaseToken\": \"" + params[ 0 ][ 1 ] + "\" }";
            Log.i ( "TokenUpdateTarget" , server.toString () );
            Log.i ( "TokenUpdateTarget" , payload );
            OutputStream os = connection.getOutputStream ();
            os.write ( payload.getBytes () , 0 , payload.getBytes ().length );
            os.flush ();
            os.close ();
            Log.i ( "TokenUpdateTarget" , " " + connection.getResponseCode () );
            connection.disconnect ();
            return true;
        } catch ( Exception e ) {
            e.printStackTrace ();
            Log.e ( "TokenUpdateTarget" , " " + e.getMessage () );
            return false;
        }
    }
}
