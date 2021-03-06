package com.fhaachen.ip_ritz.prototyp.ui.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Patterns;
import com.fhaachen.ip_ritz.prototyp.R;
import com.fhaachen.ip_ritz.prototyp.data.LoginRepository;
import com.fhaachen.ip_ritz.prototyp.data.Result;
import com.fhaachen.ip_ritz.prototyp.data.model.User;

public class LoginViewModel extends ViewModel {

    private MutableLiveData < LoginFormState > loginFormState = new MutableLiveData <> ();
    private MutableLiveData < LoginResult > loginResult = new MutableLiveData <> ();
    private LoginRepository loginRepository;

    LoginViewModel ( LoginRepository loginRepository ) {
        this.loginRepository = loginRepository;
    }

    LiveData < LoginFormState > getLoginFormState () {
        return loginFormState;
    }

    LiveData < LoginResult > getLoginResult () {
        return loginResult;
    }

    public void login ( String username , String password ) {
        // can be launched in a separate asynchronous job
        Result < User > result = loginRepository.login ( username , password );

        if ( result instanceof Result.Success ) {
            User data = ( ( Result.Success < User > ) result ).getData ();
            loginResult.setValue ( new LoginResult ( new LoggedInUserView ( data.getFirstName () ) ) );
        } else {
            loginResult.setValue ( new LoginResult ( R.string.login_failed ) );
        }
    }

    public void logout () {
        loginRepository.logout ();
    }

    public void loginDataChanged ( String username , String password ) {
        if ( !isUserNameValid ( username ) ) {
            loginFormState.setValue ( new LoginFormState ( R.string.invalid_username , null ) );
        } else if ( !isPasswordValid ( password ) ) {
            loginFormState.setValue ( new LoginFormState ( null , R.string.invalid_password ) );
        } else {
            loginFormState.setValue ( new LoginFormState ( true ) );
        }
    }

    public boolean isLoggedIn () {
        return loginRepository.isLoggedIn ();
    }

    public User getLoggedInUser () {
        return loginRepository.getLoggedInUser ();
    }

    // A placeholder username validation check
    private boolean isUserNameValid ( String username ) {
        if ( username == null ) {
            return false;
        }
        if ( username.contains ( "@" ) ) {
            return Patterns.EMAIL_ADDRESS.matcher ( username ).matches ();
        } else {
            return !username.trim ().isEmpty ();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid ( String password ) {
        return password != null && password.trim ().length () > 1;
    }
}
