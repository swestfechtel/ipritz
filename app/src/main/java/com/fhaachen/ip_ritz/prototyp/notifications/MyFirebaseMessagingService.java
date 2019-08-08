package com.fhaachen.ip_ritz.prototyp.notifications;

//import com.google.firebase.*;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.support.constraint.Constraints.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static String token;
    @Override
    public void onMessageReceived ( RemoteMessage remoteMessage ) {
        // ...
        Log.i("Message", "Got new message");
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d ( TAG , "From: " + remoteMessage.getFrom () );

        // Check if message contains a data payload.
        if ( remoteMessage.getData ().size () > 0 ) {
            Log.d ( TAG , "Message data payload: " + remoteMessage.getData () );

            new AlertDialog.Builder(this)
                    .setTitle("Your drone has arrived!")
                    .setMessage("Your drone has arrived at your location. Please confirm its arrival to resume the ride.")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Confirmed", Toast.LENGTH_SHORT);
                        }
                    })

                    .setNeutralButton("Deny", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Denied", Toast.LENGTH_SHORT);
                        }
                    })
                    .show();

        }
        try {
            String url = remoteMessage.getData().get("click_action");
            Log.i("Firebase", " "+url);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check if message contains a notification payload.
        if ( remoteMessage.getNotification () != null ) {
            Log.d ( TAG , "Message Notification Body: " + remoteMessage.getNotification ().getBody () );
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);*/
    }

    @Override
    public void onNewToken ( String s ) {
        //super.onNewToken ( s );
        Log.d ( TAG , "New token: " + s );
        // app/api/firebase/userid/token

        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences ( this );
        SharedPreferences.Editor editor = preferences.edit ();

        editor.putString ( "token", s );
        editor.apply ();*/
        token = s;
    }
}
