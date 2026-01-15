package com.example.waterpumpapp;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MQTTHelper mqttHelper;
    private TextView txtStatus;
    private TextView txtDeviceStatus;
    private TextView txtLastRun;
    private View viewOnlineDot;
    private View viePumpStatus;
    private Button btnWater;
    private TextView txtHistoryPreview;
    private final int HISTORY_MAX = 5;

    private  List <String> history = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mqttHelper = new MQTTHelper();
        txtStatus = findViewById(R.id.txtStatus);
        txtDeviceStatus = findViewById(R.id.txtDeviceStatus);
        viewOnlineDot = findViewById(R.id.viewOnlineDot);
        txtLastRun = findViewById(R.id.txtLastRun);
        btnWater = findViewById(R.id.btnWater);
        txtHistoryPreview = findViewById(R.id.txtHistoryPreview);

        PrefsManager.init(this);
        txtLastRun.setText("Last Watered: " + PrefsManager.getLatestWatering());

        history = parseHistory(PrefsManager.getHistory());
        displayHistory();
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
                        btnWater.setEnabled(true);
                    } else {
                        GradientDrawable dot = (GradientDrawable) viewOnlineDot.getBackground();
                        dot.setColor(getResources().getColor(android.R.color.holo_red_dark));
                        btnWater.setEnabled(false);

                    }
                    Toast.makeText(MainActivity.this, "Device: " + payload, Toast.LENGTH_SHORT).show();
                    txtDeviceStatus.setText("Water Pump: " + status);
                } else if (topic.equals("plant/pump/status")) {
                    // update txtStatus
                    txtStatus.setText("Pump Status: " + status);
                    if(status.equals("RUNNING")){
                        btnWater.setEnabled(false);
                        GradientDrawable dot = (GradientDrawable) txtStatus.getBackground();
                        dot.setColor(getResources().getColor(android.R.color.holo_orange_dark));
                    }else if(status.equals("DONE")){
                        GradientDrawable dot = (GradientDrawable) txtStatus.getBackground();
                        dot.setColor(getResources().getColor(android.R.color.holo_green_dark));
                        PrefsManager.saveCurrentWateringTime();
                        txtLastRun.setText(PrefsManager.getLatestWatering());
                        updateHistory(PrefsManager.getLatestWatering());
                        PrefsManager.setHistory(updateHistoryString());
                        displayHistory(); //update the history on UI
                        btnWater.setEnabled(true);
                    }

                }
            });
        });



        btnWater.setOnClickListener(v ->
                mqttHelper.publish("on")
        );
    }

    private List<String> parseHistory(String history){
        List<String> list = new ArrayList<>();
        if(history == null || history.trim().isEmpty()){
            return list;
        }

        String [] lines = history.split("\n");
        for(String line : lines){
            if(!line.trim().isEmpty())
              list.add(line);
        }

        return list;
    }

    private void updateHistory(String element) {
        history.add(0,element); //adds newest date to the first index
        if(history.size() > HISTORY_MAX){
            history.remove(history.size() - 1); //remove last index
        }
    }

    private String updateHistoryString(){
        StringBuilder sb = new StringBuilder();
        for(String entry : history){
            sb.append(entry).append("\n");
        }
        return sb.toString().trim();
    }

    private void displayHistory() {
        if (history.isEmpty()) {
            txtHistoryPreview.setText("No history yet");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String entry : history) {
            sb.append("• ").append(entry).append("\n");
        }
        txtHistoryPreview.setText(sb.toString().trim());
    }


    }
