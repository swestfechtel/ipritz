package com.fhaachen.ip_ritz.prototyp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        final ListView listView = findViewById(R.id.list_view_friends);
        String[] friends = new String[]{
                "Burn Marks", "Maria Snow", "Aurelian Salomon"
        };

        final ArrayList<String> list = new ArrayList<String>(friends.length);
        for (String s : friends)
            list.add(s);

        final CustomArrayAdapter adapter = new CustomArrayAdapter(getApplicationContext(), friends);
        listView.setAdapter(adapter);
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
            TextView firstLine = rowView.findViewById(R.id.firstLine);
            TextView secondLine = rowView.findViewById(R.id.secondLine);
            ImageView profileImage = rowView.findViewById(R.id.profileImage);

            firstLine.setText(values[position]);
            secondLine.setText("Location placeholder");
            profileImage.setImageResource(R.mipmap.ic_launcher_round);

            return rowView;
        }
    }
}
