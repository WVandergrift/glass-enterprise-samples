package com.example.android.glass.cardsample;

import android.bluetooth.le.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.example.android.glass.cardsample.fragments.BaseFragment;
import com.example.android.glass.cardsample.fragments.CameraFragment;
import com.example.android.glass.cardsample.fragments.ColumnLayoutFragment;
import com.example.android.glass.cardsample.fragments.LightFragment;
import com.example.android.glass.cardsample.fragments.MainLayoutFragment;
import com.example.android.glass.cardsample.fragments.SwitchFragment;
import com.example.glass.ui.GlassGestureDetector.Gesture;
import com.google.android.material.tabs.TabLayout;
import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity of the application. It provides viewPager to move between fragments.
 */
public class MainActivity extends BaseActivity implements BeaconTracker.StrongestBeaconChangeListener {

    private List<BaseFragment> fragments = new ArrayList<>();
    private ViewPager viewPager;
    private BeaconTracker beaconTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager_layout);

        beaconTracker = new BeaconTracker(this, 5);
        beaconTracker.addStrongestBeaconChangeListener(this);

        final ScreenSlidePagerAdapter screenSlidePagerAdapter = new ScreenSlidePagerAdapter(
            getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(screenSlidePagerAdapter);

        fragments.add(CameraFragment.newInstance("rtsp://192.168.86.105:42413/0e9bde3a380ee522"));
        fragments.add(CameraFragment.newInstance("rtsp://192.168.86.105:58529/b6ae5e979ff260d9"));
        fragments.add(CameraFragment.newInstance("rtsp://192.168.86.105:61098/9fc95d4edc79af7f"));

        fragments.add(LightFragment
                .newInstance(MainActivity.this, R.drawable.ic_lightbulb, getString(R.string.light_card),
                        getString(R.string.footnote_sample), getString(R.string.timestamp_sample)));
        fragments.add(SwitchFragment
                .newInstance(MainActivity.this, "Living Room Lights", "switch.living_room"));
        fragments.add(SwitchFragment
                .newInstance(MainActivity.this, "Kitchen Lights", "switch.kitchen"));
        fragments.add(MainLayoutFragment
            .newInstance(getString(R.string.text_sample), getString(R.string.footnote_sample),
                getString(R.string.timestamp_sample), null));
        fragments.add(MainLayoutFragment
            .newInstance(getString(R.string.different_options), getString(R.string.empty_string),
                getString(R.string.empty_string), R.menu.main_menu));
        fragments.add(ColumnLayoutFragment
            .newInstance(R.drawable.ic_style, getString(R.string.columns_sample),
                getString(R.string.footnote_sample), getString(R.string.timestamp_sample)));
        fragments.add(MainLayoutFragment
            .newInstance(getString(R.string.like_this_sample), getString(R.string.empty_string),
                getString(R.string.empty_string), null));

        screenSlidePagerAdapter.notifyDataSetChanged();

        final TabLayout tabLayout = findViewById(R.id.page_indicator);
        tabLayout.setupWithViewPager(viewPager, true);

        // Start the timer to update fragments list and show a toast after 30 seconds
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Show a toast message
//                Toast.makeText(MainActivity.this, "30 seconds have passed", Toast.LENGTH_SHORT).show();
//
//                fragments.clear();
//                fragments.addAll(FragmentLoader.loadFragmentsFromJson(MainActivity.this,"living-room.json"));
//
//                // Update the fragments list here
//                fragments.add(MainLayoutFragment.newInstance("This is a new card",
//                        getString(R.string.footnote_sample), getString(R.string.timestamp_sample), null));
//
//                // Notify the adapter about the change in the dataset
//                screenSlidePagerAdapter.notifyDataSetChanged();
//                viewPager.setCurrentItem(0, true);
//            }
//        }, 30000); // 30000 milliseconds = 30 seconds
    }

    @Override
    public boolean onGesture(Gesture gesture) {
        switch (gesture) {
            case TAP:
                fragments.get(viewPager.getCurrentItem()).onSingleTapUp();
                return true;
            case SWIPE_UP:
                fragments.get(viewPager.getCurrentItem()).onSwipeUp();
                return true;
            case SWIPE_DOWN:
                fragments.get(viewPager.getCurrentItem()).onSwipeDown();
                return true;
            default:
                return super.onGesture(gesture);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //beaconTracker.startScanning();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStrongestBeaconChanged(String newStrongestBeaconId) {
        Log.d("BLEScan", "Strongest beacon: " + newStrongestBeaconId);
        for (Fragment fragment : fragments) {
            if (fragment instanceof ColumnLayoutFragment) {
                // Update the footnote with the new RSSI value
                ((ColumnLayoutFragment) fragment).updateFootnoteText("Strongest Beacon: " + newStrongestBeaconId);
            }
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
