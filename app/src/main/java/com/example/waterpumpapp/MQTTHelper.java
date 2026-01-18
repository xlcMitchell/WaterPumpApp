package com.example.waterpumpapp;

import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTHelper {

    private static final String TAG = "MQTT";
    private MqttClient mqttClient;
    private ConnectionListener connectionListener;
    private MessageListener messageListener;
    public MQTTHelper() {
        connect();
    }


    //Interfaces to communicate with main activity
    public interface ConnectionListener {
        void onConnectionLost();
    }

    public interface MessageListener {
        void onMessage(String topic, String payload);
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

                mqttClient.setCallback(new MqttCallbackExtended() {
                    @Override
                    public void connectComplete(boolean reconnect, String serverURI) {
                        subscribe(MqttConfig.TOPIC_ONLINE);
                        subscribe(MqttConfig.TOPIC_STATUS);
                        subscribe(MqttConfig.TOPIC_MOISTURE);
                    }

                    @Override
                    public void connectionLost(Throwable cause) {
                        Log.e(TAG, "Connection lost", cause);

                        if (connectionListener != null) {
                            connectionListener.onConnectionLost();
                        }

                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) {
                        String payload = new String(message.getPayload());
                        Log.d(TAG, "Message arrived: " + message.toString());

                        //on message method will be called in main activity
                        //topic and payload (message) passed through interface
                        if (messageListener != null) {
                            messageListener.onMessage(topic, payload);
                        }
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

    public void publishAuto(String payload) {
        new Thread(() -> {
            try {
                if (mqttClient != null && mqttClient.isConnected()) {
                    MqttMessage message = new MqttMessage(payload.getBytes());
                    message.setQos(1);
                    mqttClient.publish(MqttConfig.TOPIC_AUTO, message);
                    Log.d(TAG, "Published: " + payload);
                } else {
                    Log.e(TAG, "MQTT not connected");
                }
            } catch (MqttException e) {
                Log.e(TAG, "Publish error", e);
            }
        }).start();
    }

    public void subscribe(String topic) {
        new Thread(() -> {
            try {
                if (mqttClient != null && mqttClient.isConnected()) {
                    mqttClient.subscribe(topic, 1);
                    Log.d(TAG, "Subscribed to: " + topic);
                } else {
                    Log.e(TAG, "Subscribe failed (not connected): " + topic);
                }
            } catch (MqttException e) {
                Log.e(TAG, "Subscribe error: " + topic, e);
            }
        }).start();
    }

    //set connection listeners and message listeners in main activity
    public void setConnectionListener(ConnectionListener listener) {
        this.connectionListener = listener;
    }

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }
}