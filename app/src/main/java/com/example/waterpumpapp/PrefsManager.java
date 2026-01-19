package com.example.waterpumpapp;
/**
 * PrefsManager - Global Persistence Utility
 * * DESIGN PATTERN: Singleton / Utility Class
 * * WHY STATIC?
 * This class uses static methods to provide global access to SharedPreferences
 * without needing to instantiate the class or pass objects between activities.
 * This ensures a "Single Source of Truth" for app settings and MQTT states.
 * * INITIALIZATION:
 * Must call PrefsManager.init(Context) once (ideally in MainActivity or Application class).
 * This provides the class with the necessary Context to access Android's
 * internal filesystem safely.
 * * DATA FLOW:
 * 1. MQTT Message Arrives -> PrefsManager.setLastWatering(time)
 * 2. App Restarts -> PrefsManager.getLatestWatering() -> UI restored
 */
import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PrefsManager {
    private static final String PREF_NAME = "MyProjectPrefs";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    // Keys - All  keys here
    private static final String MOISTURE_LATEST = "moisture";
    private static final String LAST_WATERING = "last";
    private static final String HISTORY = "";


    // Initialize the manager called in main activity
    public static void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    // --- SAVE METHODS ---
    //to save latest moisture reading when moisture sensor added
    public static void set(String value) {
        editor.putString(MOISTURE_LATEST, value);
        editor.apply();
    }

    //save the last watering as a string which will be converted from date format
    public static void setLastWatering(String time){
        editor.putString(LAST_WATERING,time);
        editor.apply();
    }

    public static void setHistory(String dates){
        editor.putString(HISTORY,dates);
        editor.apply();
    }


    // --- GET METHODS ---
    public static String getLatestWatering() {
        return sharedPreferences.getString(LAST_WATERING, "N/A");
    }

    public static String getHistory(){
        return sharedPreferences.getString(HISTORY,"Jan 13,12:37\nJan 12, 12:05\nJan 10, 11:12");
    }

    public static String getLatestMoisture() {
        return sharedPreferences.getString(MOISTURE_LATEST, "0%"); // Default to 0%
    }

    public static void saveCurrentWateringTime() {
        // Format: "MMM dd, HH:mm" -> "Jan 12, 13:23"
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        // Use your existing method to save it
        setLastWatering(currentTime);
    }



    // --- UTILITY ---
    public static void clearAll() {
        editor.clear();
        editor.apply();
    }
}