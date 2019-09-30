package com.fhaachen.ip_ritz.prototyp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import java.net.HttpURLConnection;
import java.net.URL;

public class SettingsActivity extends AppCompatActivity {

    private Switch mSwitch;
    private ImageButton mBackButton;
    @Override
    protected void onCreate (Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_settings );

        mSwitch = findViewById(R.id.settingsSimulatorSwitch);
        mBackButton = findViewById(R.id.settingsBackButton);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final boolean tmp = isChecked;
                Constants.SIM_MODE = isChecked;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL server = new URL(Constants.IP + "/sim.php?d=" + tmp);
                            HttpURLConnection connection = (HttpURLConnection) server.openConnection();
                            connection.connect();
                            connection.disconnect();
                        } catch (Exception e) {
                        }
                    }
                }).start();
            }
        });
    }
}
