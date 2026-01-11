package com.example.waterpumpapp;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MQTTHelper mqttHelper;
    private TextView txtStatus;
    private TextView txtDeviceStatus;
    private View viewOnlineDot;
    private View viePumpStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mqttHelper = new MQTTHelper();
        txtStatus = findViewById(R.id.txtStatus);
        txtDeviceStatus = findViewById(R.id.txtDeviceStatus);
        viewOnlineDot = findViewById(R.id.viewOnlineDot);

        //listen to connection
        mqttHelper.setConnectionListener(() -> {
            runOnUiThread(() ->
                    Toast.makeText(MainActivity.this,
                            "MQTT connection lost",
                            Toast.LENGTH_SHORT).show()
            );
        });

        mqttHelper.setMessageListener((topic, payload) -> {
            runOnUiThread(() -> {
                String status = payload.trim();

                if (topic.equals("plant/device/online")) {

                    if (status.equals("ONLINE")) {
                        GradientDrawable dot = (GradientDrawable) viewOnlineDot.getBackground();
                        dot.setColor(getResources().getColor(android.R.color.holo_green_dark));

                    } else {
                        GradientDrawable dot = (GradientDrawable) viewOnlineDot.getBackground();
                        dot.setColor(getResources().getColor(android.R.color.holo_red_dark));


                    }
                    Toast.makeText(MainActivity.this, "Device: " + payload, Toast.LENGTH_SHORT).show();
                    txtDeviceStatus.setText("Water Pump: " + status);
                } else if (topic.equals("plant/pump/status")) {
                    // update txtStatus
                    txtStatus.setText("Pump Status: " + status);
                    if(status.equals("RUNNING")){
                        GradientDrawable dot = (GradientDrawable) txtStatus.getBackground();
                        dot.setColor(getResources().getColor(android.R.color.holo_orange_dark));
                    }else if(status.equals("DONE")){
                        GradientDrawable dot = (GradientDrawable) txtStatus.getBackground();
                        dot.setColor(getResources().getColor(android.R.color.holo_green_dark));
                    }

                }
            });
        });

        Button btnWater = findViewById(R.id.btnWater);

        btnWater.setOnClickListener(v ->
                mqttHelper.publish("on")
        );
    }


    }
