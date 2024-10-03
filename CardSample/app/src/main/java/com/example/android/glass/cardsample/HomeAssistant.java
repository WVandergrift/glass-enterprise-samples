package com.example.android.glass.cardsample;

import static android.provider.Settings.System.getString;

import android.content.Context;
import android.os.AsyncTask;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeAssistant {

    private static HomeAssistant instance;
    private String baseUrl;
    private String token;


    private HomeAssistant(Context context) {
        this.baseUrl = context.getString(R.string.home_assistant_base_url);
        this.token = context.getString(R.string.home_assistant_token);
    }

    public static synchronized HomeAssistant getInstance(Context context) {
        if (instance == null) {
            instance = new HomeAssistant(context);
        }
        return instance;
    }

    // Toggle a light's state
    public void toggleLight(String entityId) {
        String endpoint = "/api/services/light/toggle";
        String body = "{\"entity_id\": \"" + entityId + "\"}";
        makeRequest(endpoint, "POST", body);
    }

    public void toggleSwitch(String entityId) {
        String endpoint = "/api/services/switch/toggle";
        String body = "{\"entity_id\": \"" + entityId + "\"}";
        makeRequest(endpoint, "POST", body);
    }

    // Make a generic request to the Home Assistant API
    private void makeRequest(final String endpoint, final String method, final String body) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL(baseUrl + endpoint);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod(method);
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(body.getBytes("UTF-8"));
                    conn.connect();

                    // Check the response code, e.g., 200 indicates success
                    int responseCode = conn.getResponseCode();
                    System.out.println("Response Code: " + responseCode);

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
