package com.example.waterpumpapp;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MQTTHelper mqttHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mqttHelper = new MQTTHelper();

        Button btnWater = findViewById(R.id.btnWater);

        btnWater.setOnClickListener(v ->
                mqttHelper.publish("on")
        );
    }
}