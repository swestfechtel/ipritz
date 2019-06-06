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
import com.fhaachen.ip_ritz.prototyp.data.FriendListIdsDataSource;
import com.fhaachen.ip_ritz.prototyp.data.FriendListNamesDataSource;
import com.fhaachen.ip_ritz.prototyp.data.model.User;
import com.fhaachen.ip_ritz.prototyp.ui.login.LoginActivity;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    private ImageButton mBackButton;

    private ListView friends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        friends = findViewById(R.id.friendsListView);
        mBackButton = findViewById(R.id.friendsBackButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish ();
            }
        });
    }


    @Override
    protected void onResume () {
        super.onResume ();
        final ListView listView = findViewById ( R.id.friendsListView );

        User loggedInUser = LoginActivity.loginViewModel.getLoggedInUser ();
        String userId = loggedInUser.get_id ().get$oid ();

        FriendListIdsDataSource idSource = new FriendListIdsDataSource ();
        FriendListNamesDataSource nameSource = new FriendListNamesDataSource ();
        final ArrayList < String > ids = idSource.doInBackground ( userId );
        final ArrayList < String > names = nameSource.doInBackground ( ids );

        final CustomArrayAdapter adapter = new CustomArrayAdapter ( getApplicationContext () , names );
        listView.setAdapter ( adapter );

        listView.setOnItemClickListener ( new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick ( AdapterView < ? > parent , View view , int position , long id ) {
                Log.i ( "FriendsActivity" , "List item + " + position + " selected." );
                String friendId = ids.get ( position );

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
            TextView firstLine = rowView.findViewById(R.id.ItemFirstLine);
            TextView secondLine = rowView.findViewById(R.id.ItemSecondLine);
            ImageView profileImage = rowView.findViewById(R.id.ItemImage);

            firstLine.setText ( values.get ( position ) );
            secondLine.setText("Location placeholder");
            profileImage.setImageResource(R.mipmap.ic_launcher_round);

            return rowView;
        }
    }
}
