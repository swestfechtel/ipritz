package com.fhaachen.ip_ritz.prototyp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.fhaachen.ip_ritz.prototyp.data.LoginDataSource;
import com.fhaachen.ip_ritz.prototyp.data.model.LoggedInUser;
import com.fhaachen.ip_ritz.prototyp.ui.login.LoginActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    private ImageButton mBackButton;
    private Button mUnfollowButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mBackButton = findViewById(R.id.friendsBackButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(v.getContext(), MainActivity.class);
                startActivity(i);*/
                finish ();
            }
        });
    }

    @Override
    protected void onResume () {
        super.onResume ();
        final ListView listView = findViewById ( R.id.friendsListView );

        /* Hier spaeter json object von rest api parsen */
        LoggedInUser loggedInUser = LoginActivity.loginViewModel.getLoggedInUser ();
        String userId = loggedInUser.getUserId ();
        final ArrayList < String > ids = new ArrayList <> ( 0 );
        final ArrayList < String > names = new ArrayList <> ( 0 );
        ids.add ( "5cea970061ba485d14002da9" );
        ids.add ( "5ce7c9d6b84ce6277400640b" ); // stub weil noch keine Daten eingepflegt...

        try {
            URL server = new URL ( LoginDataSource.serverAddress + "/user.php?id=" + userId );
            HttpURLConnection connection = ( HttpURLConnection ) server.openConnection ();

            if ( connection.getResponseCode () != 200 ) {
                throw new RuntimeException ( "Failed: HTTP error code: " + connection.getResponseCode () );
            }

            JsonParser jsonParser = new JsonParser ();
            JsonElement jsonElement = jsonParser.parse ( new InputStreamReader ( ( InputStream ) connection.getContent () ) );
            JsonObject jsonObject = jsonElement.getAsJsonObject ();
            JsonArray friendsArray = jsonObject.get ( "friends" ).getAsJsonArray ();

            for ( JsonElement element : friendsArray ) {
                //JsonObject id = element.getAsJsonObject ();
                ids.add ( element.toString () );
                Log.i ( "FriendsActivity" , "Added id to arraylist: " + element.toString () );
            }

            connection.disconnect ();
            //TODO: parse json file
        } catch ( Exception e ) {
            e.printStackTrace ();
            Log.e ( "FriendsActivity" , "URL connection error. " + e.getMessage () );
        }

        for ( String id : ids ) {
            try {
                URL server = new URL ( LoginDataSource.serverAddress + "/user.php?id=" + id );
                HttpURLConnection connection = ( HttpURLConnection ) server.openConnection ();

                if ( connection.getResponseCode () != 200 ) {
                    throw new RuntimeException ( "Failed: HTTP error code: " + connection.getResponseCode () );
                }

                JsonParser jsonParser = new JsonParser ();
                JsonElement jsonElement = jsonParser.parse ( new InputStreamReader ( ( InputStream ) connection.getContent () ) );
                JsonObject jsonObject = jsonElement.getAsJsonObject ();

                names.add ( jsonObject.get ( "firstName" ).getAsString () + " " + jsonObject.get ( "lastName" ).getAsString () );

                connection.disconnect ();
                //TODO: parse json file
            } catch ( Exception e ) {
                e.printStackTrace ();
                Log.e ( "FriendsActivity" , "URL connection error. " + e.getMessage () );
            }
        }




        /* Jedes Listenelement muss spaeter eine eindeutige Id bekommen,
         * um dann das entsprechende Profil zu laden
         * => arraylist<int>, fuer jedes erhaltene json object die id
         * in die arraylist packen, dann entspricht die position in der
         * arraylist der position in der listview...
         * */
        /*final Integer[] ids = new Integer[]{
                1, 2, 3
        };*/

        /*final ArrayList<String> list = new ArrayList<String>(friends.length);
        for (String s : friends)
            list.add(s);*/

        final CustomArrayAdapter adapter = new CustomArrayAdapter ( getApplicationContext () , names );
        listView.setAdapter ( adapter );

        listView.setOnItemClickListener ( new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick ( AdapterView < ? > parent , View view , int position , long id ) {
                Log.i ( "FriendsActivity" , "List item + " + position + " selected." );
                String friendId = ids.get ( position );
                /* hier dann spaeter das entsprechende Profil laden */
                Intent i = new Intent ( view.getContext () , ProfileActivity.class );
                i.putExtra ( "profileId" , friendId );
                startActivity ( i );
            }
        } );
    }

    class CustomArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        //private final String[] values;
        private final ArrayList < String > values;

        public CustomArrayAdapter ( Context context , ArrayList < String > values ) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.friends_list, parent, false);
            TextView firstLine = rowView.findViewById(R.id.friendsItemFirstLine);
            TextView secondLine = rowView.findViewById(R.id.friendsItemSecondLine);
            ImageView profileImage = rowView.findViewById(R.id.friendsItemImage);

            firstLine.setText ( values.get ( position ) );
            secondLine.setText("Location placeholder");
            profileImage.setImageResource(R.mipmap.ic_launcher_round);

            return rowView;
        }
    }
}
