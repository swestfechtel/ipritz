package com.fhaachen.ip_ritz.prototyp.data;

import android.os.AsyncTask;
import android.util.Log;
import com.fhaachen.ip_ritz.prototyp.data.model.User;
import com.google.gson.Gson;
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
 * Class that handles authentication w/ doInBackground credentials and retrieves user information.
 */
public class LoginDataSource extends AsyncTask < String, Integer, Result > {

    public static String serverAddress = "http://149.201.48.86:8001/app/api";

    @Override
    public Result < User > doInBackground ( String... params ) {

        try {
            String url = serverAddress + "/login/" + params[ 0 ] + "/" + params[ 1 ];
            URL server = new URL ( url );
            HttpURLConnection connection = ( HttpURLConnection ) server.openConnection ();

            if ( connection.getResponseCode () != 200 ) {
                throw new RuntimeException ( "Failed: HTTP error code: " + connection.getResponseCode () );
            }

            BufferedReader br = new BufferedReader ( new InputStreamReader (
                    ( connection.getInputStream () ) ) );

            String userId = "", output = "";
            while ( ( output = br.readLine () ) != null ) {
                userId += output;
            }
            if ( userId.length () == 0 ) throw new RuntimeException ( "Failed: Login returned empty string." );

            connection.disconnect ();

            server = new URL ( serverAddress + "/user/" + userId );
            connection = ( HttpURLConnection ) server.openConnection ();

            if ( connection.getResponseCode () != 200 ) {
                throw new RuntimeException ( "Failed: HTTP error code: " + connection.getResponseCode () );
            }

            JsonParser jsonParser = new JsonParser ();
            JsonElement jsonElement = jsonParser.parse ( new InputStreamReader ( ( InputStream ) connection.getContent () ) );
            JsonObject rootObject = jsonElement.getAsJsonObject ();

            Gson gson = new Gson ();
            User loggedInUser = gson.fromJson ( rootObject , User.class );

            connection.disconnect ();

            return new Result.Success <> ( loggedInUser );

        } catch ( Exception e ) {
            e.printStackTrace ();
            Log.e ( "LoginDataSource" , "Error logging in. " + e.getMessage () );
            return new Result.Error ( new IOException ( "Error logging in" , e ) );
        }
    }

    @Override
    protected void onProgressUpdate ( Integer... progress ) {

    }

    @Override
    protected void onPostExecute ( Result result ) {

    }

    public void logout () {
        // TODO: revoke authentication
    }
}
