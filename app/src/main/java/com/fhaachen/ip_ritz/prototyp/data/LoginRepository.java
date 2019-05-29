package com.fhaachen.ip_ritz.prototyp.data;

import com.fhaachen.ip_ritz.prototyp.data.model.User;

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
    private User user = null;

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

    public User getLoggedInUser () {
        return this.user;
    }

    public void logout () {
        user = null;
        dataSource.logout ();
        // TODO: cancel timer
        //timer.cancel ();
    }

    private void setLoggedInUser ( User user ) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result < User > login ( String username , String password ) {
        // handle doInBackground
        Result < User > result = dataSource.doInBackground ( username , password );
        if ( result instanceof Result.Success ) {
            setLoggedInUser ( ( ( Result.Success < User > ) result ).getData () );
        }
        return result;
    }
}
