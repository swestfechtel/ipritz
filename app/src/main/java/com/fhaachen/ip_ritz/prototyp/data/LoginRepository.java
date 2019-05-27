package com.fhaachen.ip_ritz.prototyp.data;

import com.fhaachen.ip_ritz.prototyp.data.model.LoggedInUser;

import java.util.Timer;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of doInBackground status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;
    private Timer timer;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository ( LoginDataSource dataSource ) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance ( LoginDataSource dataSource ) {
        if ( instance == null ) {
            instance = new LoginRepository ( dataSource );
        }
        return instance;
    }

    public boolean isLoggedIn () {
        return this.user != null;
    }

    public LoggedInUser getLoggedInUser () {
        return this.user;
    }

    public void logout () {
        user = null;
        dataSource.logout ();
        // TODO: cancel timer
        //timer.cancel ();
    }

    private void setLoggedInUser ( LoggedInUser user ) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result < LoggedInUser > login ( String username , String password ) {
        // handle doInBackground
        Result < LoggedInUser > result = dataSource.doInBackground ( username , password );
        if ( result instanceof Result.Success ) {
            setLoggedInUser ( ( ( Result.Success < LoggedInUser > ) result ).getData () );

            // TODO: get current loc and write to database
            /*timer = new Timer("updateLocationTimer");

            timer.scheduleAtFixedRate ( new TimerTask () {
                                            @Override
                                            public void run () {
                                                try {
                                                    URL server = new URL ( LoginDataSource.serverAddress + "/user.php?id=" + user.getUserId ());
                                                    HttpURLConnection connection = (HttpURLConnection)server.openConnection ();
                                                    connection.setDoOutput ( true );
                                                    connection.setRequestMethod ( "PUT" );
                                                    connection.setRequestProperty ( "Content-Type" , "application/json" );

                                                    String payload = "{\"_id\": null," +
                                                            "\"latitude\": " + currentLocLat + "," +
                                                            "\"longitude\": " + currentLocLong + "}";

                                                    OutputStream os = connection.getOutputStream ();
                                                    os.write ( payload.getBytes () );
                                                    os.flush ();

                                                    if ( connection.getResponseCode () != 200 ) {
                                                        throw new RuntimeException ( "Failed : HTTP error code : "
                                                                + connection.getResponseCode () );
                                                    }

                                                    connection.disconnect ();
                                                } catch (Exception e){}

                                            }
                                        }
                    , 30 , 1000 * 60 );*/
        }
        return result;
    }
}
