package com.fhaachen.ip_ritz.prototyp.data;

import android.util.Log;
import com.fhaachen.ip_ritz.prototyp.data.model.LoggedInUser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result < LoggedInUser > login ( String username , String password ) {

        try {
            Log.i ( "LoginDataSource" , "Trying to log in " + username + ":" + password );
            String url = "http://192.168.178.20/app/api/login.php?email=" + username + "&pw=" + password;
            Log.i ( "LoginDataSource" , "URL is " + url );
            URL server = new URL ( url );
            //URL server = new URL ( "http://192.168.178.20/app/api/user.php" );
            Log.i ( "LoginDataSource" , "URL created. Open connection now.." );
            HttpURLConnection connection = ( HttpURLConnection ) server.openConnection ();
            Log.i ( "LoginDataSource" , "Connection open." );


            if ( connection.getResponseCode () != 200 ) {
                Log.i ( "LoginDataSource" , "HTTP error" );
                throw new RuntimeException ( "Failed: HTTP error code: " + connection.getResponseCode () );
            }

            BufferedReader br = new BufferedReader ( new InputStreamReader (
                    ( connection.getInputStream () ) ) );

            Log.i ( "LoginDataSource" , "Read stream." );

            /*String output;
            while ((output = br.readLine()) != null) {
                Log.i("LoginDataSource", output);
            }*/


            // TODO: handle loggedInUser authentication
            // TODO: set id and display name


            String userId = "", output = "";
            while ( ( output = br.readLine () ) != null ) {
                userId += output;
            }
            Log.i ( "LoginDataSource" , "Got userId. " + userId );
            if ( userId.length () == 0 ) throw new RuntimeException ( "Failed: Login returned empty string." );

            connection.disconnect ();

            server = new URL ( "http://192.168.178.20/app/api/user.php?id=" + userId );
            Log.i ( "LoginDataSource" , "URL created. Open connection now.." );
            connection = ( HttpURLConnection ) server.openConnection ();
            Log.i ( "LoginDataSource" , "Connected." );

            if ( connection.getResponseCode () != 200 ) {
                throw new RuntimeException ( "Failed: HTTP error code: " + connection.getResponseCode () );
            }

            JsonParser jsonParser = new JsonParser ();
            JsonElement jsonElement = jsonParser.parse ( new InputStreamReader ( ( InputStream ) connection.getContent () ) );
            JsonObject jsonObject = jsonElement.getAsJsonObject ();

            LoggedInUser loggedInUser =
                    new LoggedInUser (
                            userId ,
                            jsonObject.get ( "firstName" ).getAsString () );
            return new Result.Success <> ( loggedInUser );

        } catch ( Exception e ) {
            e.printStackTrace ();
            Log.e ( "LoginDataSource" , "Error logging in. " + e.getMessage () );
            return new Result.Error ( new IOException ( "Error logging in" , e ) );
        }
    }

    public void logout () {
        // TODO: revoke authentication
    }
}
