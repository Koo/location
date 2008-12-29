package com.mamezou.android.example;

import android.os.Bundle;

import com.google.android.maps.MapActivity;

public class LocationExampleActivity extends MapActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected boolean isRouteDisplayed() {
    	return false;
    }

    // TODO MyLocationOverlayの表示
    // TODO 現在のLocationの表示
    // TODO 現在のLocationへ移動
}