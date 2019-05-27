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
import com.fhaachen.ip_ritz.prototyp.data.model.LoggedInUser;
import com.fhaachen.ip_ritz.prototyp.ui.login.LoginActivity;

import java.net.HttpURLConnection;
import java.net.URL;

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
                Intent i = new Intent(v.getContext(), MainActivity.class);
                startActivity(i);
            }
        });

        final ListView listView = findViewById(R.id.friendsListView);

        /* Hier spaeter json object von rest api parsen */
        LoggedInUser loggedInUser = LoginActivity.loginViewModel.getLoggedInUser ();
        String userId = loggedInUser.getUserId ();
        try {
            URL server = new URL ( "http://149.201.48.86:8001/app/api/user/" + userId + "/friends" );
            HttpURLConnection connection = ( HttpURLConnection ) server.openConnection ();
            connection.setRequestMethod ( "GET" );
            connection.setRequestProperty ( "Accept" , "application/json" );
            connection.connect ();

            if ( connection.getResponseCode () != 200 ) {
                throw new RuntimeException ( "Failed: HTTP error code: " + connection.getResponseCode () );
            }

            //TODO: parse json file
        } catch ( Exception e ) {
            Log.e ( "FriendsActivity" , "URL connection error" );
            Log.e ( "FriendsActivity" , e.getLocalizedMessage () );
        }

        String[] friends = new String[]{
                "Burn Marks", "Maria Snow", "Aurelian Salomon"
        };
        /* Jedes Listenelement muss spaeter eine eindeutige Id bekommen,
         * um dann das entsprechende Profil zu laden
         * => arraylist<int>, fuer jedes erhaltene json object die id
         * in die arraylist packen, dann entspricht die position in der
         * arraylist der position in der listview...
         * */
        final Integer[] ids = new Integer[]{
                1, 2, 3
        };

        /*final ArrayList<String> list = new ArrayList<String>(friends.length);
        for (String s : friends)
            list.add(s);*/

        final CustomArrayAdapter adapter = new CustomArrayAdapter(getApplicationContext(), friends);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("FriendsActivity", "List item + " + position + " selected.");
                int friendId = ids[position];
                /* hier dann spaeter das entsprechende Profil laden */
                Intent i = new Intent(view.getContext(), ProfileActivity.class);
                i.putExtra ( "profileId" , friendId );
                startActivity(i);
            }
        });
    }

    class CustomArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public CustomArrayAdapter(Context context, String[] values) {
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

            firstLine.setText(values[position]);
            secondLine.setText("Location placeholder");
            profileImage.setImageResource(R.mipmap.ic_launcher_round);

            return rowView;
        }
    }
}
