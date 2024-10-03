package com.example.android.glass.cardsample;

import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class BeaconTracker {
    private final BluetoothCentralManager bleManager;
    private final Map<String, Queue<Integer>> beaconRssiValues = new HashMap<>();
    private List<StrongestBeaconChangeListener> listeners = new ArrayList<>();
    private final int maxSamples;
    private String strongestBeaconId = null;

    public interface StrongestBeaconChangeListener {
        void onStrongestBeaconChanged(String newStrongestBeaconId);
    }

    public void addStrongestBeaconChangeListener(StrongestBeaconChangeListener listener) {
        listeners.add(listener);
    }

    public BeaconTracker(Context context, int maxSamples) {
        this.maxSamples = maxSamples;
        BluetoothCentralManagerCallback bluetoothCentralManagerCallback = new BluetoothCentralManagerCallback() {
            @Override
            public void onDiscoveredPeripheral(BluetoothPeripheral peripheral, @NonNull ScanResult scanResult) {
                if (peripheral.getName().startsWith("bcn")) {
                    updateRssiValue(peripheral.getName(), scanResult.getRssi());
                }
            }
        };
        this.bleManager = new BluetoothCentralManager(
                context.getApplicationContext(),
                bluetoothCentralManagerCallback,
                new Handler(Looper.getMainLooper()));
    }

    public void updateRssiValue(String beaconId, int rssi) {
        Queue<Integer> rssiValues = beaconRssiValues.getOrDefault(beaconId, new LinkedList<>());
        if (rssiValues.size() >= maxSamples) rssiValues.poll(); // Remove the oldest RSSI value
        rssiValues.offer(rssi); // Add the new RSSI value
        beaconRssiValues.put(beaconId, rssiValues);

        // Check if the strongest beacon has changed after the update
        String currentStrongestBeacon = getStrongestBeacon();
        if (currentStrongestBeacon != null && !currentStrongestBeacon.equals(strongestBeaconId)) {
            strongestBeaconId = currentStrongestBeacon;
            notifyStrongestBeaconChanged(strongestBeaconId);
        }
    }

    public double getAverageRssi(String beaconId) {
        Queue<Integer> rssiValues = beaconRssiValues.get(beaconId);
        if (rssiValues == null || rssiValues.isEmpty()) {
            return Double.NaN; // Return Not-a-Number if no data available
        }
        double sum = 0;
        for (int rssi : rssiValues) {
            sum += rssi;
        }
        return sum / rssiValues.size();
    }

    public String getStrongestBeacon() {
        String strongestBeaconId = null;
        double highestRssi = Double.NEGATIVE_INFINITY;
        for (Map.Entry<String, Queue<Integer>> entry : beaconRssiValues.entrySet()) {
            double averageRssi = getAverageRssi(entry.getKey());
            if (averageRssi > highestRssi) {
                highestRssi = averageRssi;
                strongestBeaconId = entry.getKey();
            }
        }
        return strongestBeaconId;
    }

    public void startScanning() {
        bleManager.scanForPeripherals();
    }

    public void stopScanning() {
        bleManager.stopScan();
    }

    private void notifyStrongestBeaconChanged(String newStrongestBeaconId) {
        for (StrongestBeaconChangeListener listener : listeners) {
            listener.onStrongestBeaconChanged(newStrongestBeaconId);
        }
    }
}
