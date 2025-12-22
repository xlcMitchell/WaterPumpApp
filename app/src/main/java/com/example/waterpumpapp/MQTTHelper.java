package com.example.waterpumpapp;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTHelper {

    private static final String TAG = "MQTT";
    private MqttClient mqttClient;

    public MQTTHelper() {
        connect();
    }

    private void connect() {
        new Thread(() -> {
            try {
                String clientId = "android-" + java.util.UUID.randomUUID().toString();
                mqttClient = new MqttClient(
                        MqttConfig.SERVER_URI,
                        clientId,
                        null
                );

                MqttConnectOptions options = new MqttConnectOptions();
                options.setUserName(MqttConfig.USERNAME);
                options.setPassword(MqttConfig.PASSWORD.toCharArray());
                options.setCleanSession(true);
                options.setAutomaticReconnect(true);

                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        Log.e(TAG, "Connection lost", cause);
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) {
                        Log.d(TAG, "Message arrived: " + message.toString());
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        Log.d(TAG, "Delivery complete");
                    }
                });

                mqttClient.connect(options);
                Log.d(TAG, "Connected to HiveMQ");

            } catch (MqttException e) {
                Log.e(TAG, "MQTT connect error", e);
            }
        }).start();
    }

    public void publish(String payload) {
        new Thread(() -> {
            try {
                if (mqttClient != null && mqttClient.isConnected()) {
                    MqttMessage message = new MqttMessage(payload.getBytes());
                    message.setQos(1);
                    mqttClient.publish(MqttConfig.TOPIC_PUMP, message);
                    Log.d(TAG, "Published: " + payload);
                } else {
                    Log.e(TAG, "MQTT not connected");
                }
            } catch (MqttException e) {
                Log.e(TAG, "Publish error", e);
            }
        }).start();
    }
}