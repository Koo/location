package com.mamezou.android.example;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class LocationExampleActivity extends MapActivity {
	private MapView mapView;
	private TextView textView;
	private LocationManager locationManager;
	private MyLocationOverlay myLocationOverlay;

	private static final int VIEW_GROUP_ID = 1;
	private static final int ZOOM_UP_ID = 2;
	private static final int ZOOM_DOWN_ID = 3;
	private static final int MOVE_TO_CURRENT_LOCATION_ID = 4;
	private static final int SHOW_CURRENT_LOCATION_ID = 5;
	

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.map);
        textView = (TextView) findViewById(R.id.posisionView);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> allProviders = locationManager.getAllProviders();
        for (int i = 0; i < allProviders.size(); i++) {
        	Log.d("LocationExampleActivity", "provider [" + i + "] is " + allProviders.get(i));
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {

			public void onLocationChanged(Location location) {
	        	Log.d("LocationExampleActivity", 
	        			"location changed to " 
	        			+ location.getLongitude() 
	        			+ " "
	        			+ location.getLatitude());
			}

			public void onProviderDisabled(String provider) {
	        	Log.d("LocationExampleActivity", 
	        			"provider changed to " 
	        			+ provider);
			}

			public void onProviderEnabled(String provider) {
	        	Log.d("LocationExampleActivity", 
	        			"provider " + provider + " enabled");
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
	        	Log.d("LocationExampleActivity", 
	        			"provider " 
	        			+ provider
	        			+ "'s status is changed to "
	        			+ status);
				
			}
        	
        });

        myLocationOverlay = new MyLocationOverlay(getApplicationContext(), mapView);
        
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.onProviderEnabled(LocationManager.GPS_PROVIDER);
    	myLocationOverlay.enableCompass();
    	// 一番初めに現在位置が配信された際に実行されるコールバック
        myLocationOverlay.runOnFirstFix(new Runnable() { public void run() {
        	// 中心位置を現在位置に移動する
            mapView.getController().animateTo(myLocationOverlay.getMyLocation());
        }});

        mapView.getOverlays().add(myLocationOverlay);
        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(VIEW_GROUP_ID, ZOOM_UP_ID, 0, R.string.zoom_up);
		menu.add(VIEW_GROUP_ID, ZOOM_DOWN_ID, 1, R.string.zoom_down);
		menu.add(VIEW_GROUP_ID, MOVE_TO_CURRENT_LOCATION_ID, 2, R.string.move_to_current_location);
		menu.add(VIEW_GROUP_ID, SHOW_CURRENT_LOCATION_ID, 3, R.string.show_current_location);
    	return super.onCreateOptionsMenu(menu);
    }
 
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	super.onMenuItemSelected(featureId, item);
		switch (item.getItemId()) {
	
		case ZOOM_UP_ID:
			zoomIn();
			break;
		case ZOOM_DOWN_ID:
			zoomOut();
			break;
		case SHOW_CURRENT_LOCATION_ID:
			showCurrentLocation();
			break;
		case MOVE_TO_CURRENT_LOCATION_ID:
			moveToCurrentLocation();
			break;
		}
    	return true;
    }

	private void zoomIn() {
		mapView.getController().zoomIn();
	}

	private void zoomOut() {
		mapView.getController().zoomOut();
	}
	@Override
    protected boolean isRouteDisplayed() {
    	return false;
    }

    private void showCurrentLocation() {
    	Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	if (loc != null) {
    		textView.setText(
    				"latitude = " + loc.getLatitude()
    				+ " longigude = " + loc.getLongitude()
    		);
    	} else {
    		// まだ現在位置が一度も特定されていない場合
    		textView.setText(
    				"location is not provided"
    		);
    	}
    }

    private void moveToCurrentLocation() {
    	Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	int latitudeE6 = (int) (location.getLatitude() * 1E6);
    	int longitudeE6 = (int) (location.getLongitude() * 1E6);
    	GeoPoint gp = new GeoPoint(
    			latitudeE6, longitudeE6);
    	mapView.getController().animateTo(gp);

    }

    // TODO GeoCoderの利用
    @Override
    protected void onPause() {
    	// MyLocationOverlayを無効化
    	myLocationOverlay.disableMyLocation();
    	myLocationOverlay.disableCompass();
        super.onPause();
    }

    @Override
    protected void onResume() {
    	// MyLocationOverlayを有効化
    	myLocationOverlay.enableMyLocation();
    	myLocationOverlay.enableCompass();
        mapView.invalidate();
        super.onResume();
   }
}