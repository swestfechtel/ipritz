package com.fhaachen.ip_ritz.prototyp.data;

import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DroneConfirmationTarget extends AsyncTask < String[], Float, Boolean > {
    @Override
    public Boolean doInBackground ( String[]... params ) {
        try {
            URL server = new URL ( LoginDataSource.serverAddress + "/airtaxi/5cdd5a42242a502f28000bea/status" );
            HttpURLConnection connection = ( HttpURLConnection ) server.openConnection ();
            connection.setDoOutput ( true );
            connection.setRequestMethod ( "PUT" );
            connection.setRequestProperty ( "Content-Type" , "application/json" );

            String payload = " { \"status\": \"landed\" }";
            Log.i ( "DroneConfirmationTarget" , server.toString () );
            Log.i ( "DroneConfirmationTarget" , payload );
            OutputStream os = connection.getOutputStream ();
            os.write ( payload.getBytes () , 0 , payload.getBytes ().length );
            os.flush ();
            os.close ();
            Log.i ( "DroneConfirmationTarget" , " " + connection.getResponseCode () );
            connection.disconnect ();
            return true;
        } catch ( Exception e ) {
            e.printStackTrace ();
            Log.e ( "DroneConfirmationTarget" , " " + e.getMessage () );
            return false;
        }
    }
}