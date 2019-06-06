package com.fhaachen.ip_ritz.prototyp.data;

import android.os.AsyncTask;
import android.util.Log;
import com.fhaachen.ip_ritz.prototyp.data.model.User;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserDataUpdateTarget extends AsyncTask < User, Float, Boolean > {
    @Override
    public Boolean doInBackground ( User... params ) {
        for ( User user : params ) {
            try {
                URL server = new URL ( LoginDataSource.serverAddress + "/user/" + user.get_id ().get$oid () );
                HttpURLConnection connection = ( HttpURLConnection ) server.openConnection ();
                connection.setDoOutput ( true );
                connection.setRequestMethod ( "PUT" );
                connection.setRequestProperty ( "Content-Type" , "application/json" );

                Gson gson = new Gson ();
                String payload = gson.toJson ( user );
                Log.i ( "UserDataUpdateTarget" , payload );

                OutputStream os = connection.getOutputStream ();
                os.write ( payload.getBytes () );
                os.flush ();

                if ( connection.getResponseCode () != 200 ) {
                    throw new RuntimeException ( "Failed : HTTP error code : "
                            + connection.getResponseCode () );
                }

                BufferedReader br = new BufferedReader ( new InputStreamReader (
                        ( connection.getInputStream () ) ) );

                int responseCount = 0;
                String output = "";
                while ( ( output = br.readLine () ) != null ) {
                    responseCount += Integer.parseInt ( output );
                }
                if ( responseCount < 1 )
                    throw new RuntimeException ( "Error updating user: getMatchedCount was less than 1." );

                connection.disconnect ();

            } catch ( Exception e ) {
                e.printStackTrace ();
                Log.e ( "ProfileActivity" , "URL connection error. " + e.getMessage () );
                return false;
            }
        }
        return true;
    }
}
