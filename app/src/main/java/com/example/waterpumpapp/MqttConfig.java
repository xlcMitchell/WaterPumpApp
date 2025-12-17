package com.example.waterpumpapp;

public class MqttConfig {

    public static final String SERVER_URI =
            "ssl://d47b79ce302b4f8ba0475f60bd528a7c.s1.eu.hivemq.cloud:8883";

    public static final String USERNAME = "PumpController";
    public static final String PASSWORD = "PumpController123";

    public static final String CLIENT_ID =
            "android-client-" + System.currentTimeMillis();

    public static final String TOPIC_PUMP = "plant/pump/on";
}