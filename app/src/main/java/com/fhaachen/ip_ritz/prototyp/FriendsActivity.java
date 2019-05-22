package com.fhaachen.ip_ritz.prototyp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

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
                Intent i = new Intent(v.getContext(), MainActivity.class);
                startActivity(i);
            }
        });

        final ListView listViewf = findViewById(R.id.friendsListView);
        /* Hier spaeter json object von rest api parsen */
        String[] friends = new String[]{
                "Burn Marks", "Maria Snow", "Aurelian Salomon"
        };
        /* Jedes Listenelement muss spaeter eine eindeutige Id bekommen,
         * um dann das entsprechende Profil zu laden
         * => arraylist<int>, fuer jedes erhaltene json object die id
         * in die arraylist packen, dann entspricht die position in der
         * arraylist der position in der listview...
         * */
        final Integer[] idf = new Integer[]{
                1, 2, 3
        };

        /*final ArrayList<String> list = new ArrayList<String>(friends.length);
        for (String s : friends)
            list.add(s);*/

        final CustomArrayAdapter adapterf = new CustomArrayAdapter(getApplicationContext(), friends);
        listViewf.setAdapter(adapterf);

        listViewf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("FriendsActivity", "List item + " + position + " selected.");
                int shop = idf[position];
                /* hier dann spaeter das entsprechende Profil laden */
                Intent i = new Intent(view.getContext(), ProfileActivity.class);
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
            TextView firstLine = rowView.findViewById(R.id.ItemFirstLine);
            TextView secondLine = rowView.findViewById(R.id.ItemSecondLine);
            ImageView profileImage = rowView.findViewById(R.id.ItemImage);

            firstLine.setText(values[position]);
            secondLine.setText("Location placeholder");
            profileImage.setImageResource(R.mipmap.ic_launcher_round);

            return rowView;
        }
    }
}
