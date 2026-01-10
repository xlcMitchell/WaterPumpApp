package com.example.waterpumpapp;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mqttHelper = new MQTTHelper();
        txtStatus = findViewById(R.id.txtStatus);
        txtDeviceStatus = findViewById(R.id.txtDeviceStatus);

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
                    // update your dot + device text
                    // ONLINE -> green, OFFLINE -> red
                    Toast.makeText(MainActivity.this, "Device: " + payload, Toast.LENGTH_SHORT).show();
                    txtDeviceStatus.setText("Water Pump: " + status);
                } else if (topic.equals("plant/pump/status")) {
                    // update txtStatus
                    txtStatus.setText("Pump Status: " + status);
                }
            });
        });

        Button btnWater = findViewById(R.id.btnWater);

        btnWater.setOnClickListener(v ->
                mqttHelper.publish("on")
        );
    }


    }
