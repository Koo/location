package com.mamezou.android.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class LocationExampleActivity extends MapActivity {
    private MapView mapView;
    private TextView textView;
    private MyLocationOverlay myLocationOverlay;

    private Double longitude;
    private Double latitude;

    private static final int VIEW_GROUP_ID = 1;
    private static final int MOVE_TO_CURRENT_LOCATION_ID = 2;
    private static final int SHOW_CURRENT_LOCATION_ID = 3;
    private static final int ADDRESS_TO_GEOCODE_ID = 4;

    /** GEOCODEの最大結果件数 */
    protected static final int MAX_GEOCODE_RESULT = 10;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.Map);
        textView = (TextView) findViewById(R.id.PosisionView);


        locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                Log.d("LocationExampleActivity", "location changed to "
                        + location.getLongitude() + " "
                        + location.getLatitude());
            }

            public void onProviderDisabled(String provider) {
                Log.d("LocationExampleActivity", "provider changed to "
                        + provider);
            }

            public void onProviderEnabled(String provider) {
                Log.d("LocationExampleActivity", "provider " + provider
                        + " enabled");
            }

            public void onStatusChanged(String provider, int status,
                    Bundle extras) {
                Log.d("LocationExampleActivity", "provider " + provider
                        + "'s status is changed to " + status);

            }

        };

        myLocationOverlay = new MyLocationOverlay(getApplicationContext(),
                mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.onProviderEnabled(LocationManager.GPS_PROVIDER);
        // Android SDK 1.5のemulatorでは、コンパスが提供されないため、コメント
//        myLocationOverlay.enableCompass();
        // 一番初めに現在位置が配信された際に実行されるコールバック
        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                // 中心位置を現在位置に移動する
                mapView.getController().animateTo(
                        myLocationOverlay.getMyLocation());
            }
        });

        mapView.getOverlays().add(myLocationOverlay);

		ViewGroup zoom = (ViewGroup) findViewById(R.id.zoom);
		zoom.addView(mapView.getZoomControls());
		mapView.displayZoomControls(true);

		mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(VIEW_GROUP_ID, MOVE_TO_CURRENT_LOCATION_ID, 1,
                R.string.move_to_current_location);
        menu.add(VIEW_GROUP_ID, SHOW_CURRENT_LOCATION_ID, 2,
                R.string.show_current_location);
        menu.add(VIEW_GROUP_ID, ADDRESS_TO_GEOCODE_ID, 3,
                R.string.address_to_geocode);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        super.onMenuItemSelected(featureId, item);
        switch (item.getItemId()) {

        case SHOW_CURRENT_LOCATION_ID:
            showCurrentLocation();
            break;
        case MOVE_TO_CURRENT_LOCATION_ID:
            moveToCurrentLocation();
            break;
        case ADDRESS_TO_GEOCODE_ID:
            addressToGeocode();
            break;
        }
        return true;
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    private void showCurrentLocation() {
        Location loc = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc != null) {
            textView.setText("latitude = " + loc.getLatitude()
                    + " longigude = " + loc.getLongitude());
        } else {
            // まだ現在位置が一度も特定されていない場合
            textView.setText("location is not provided");
        }
    }

    private void moveToCurrentLocation() {
        Location location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
	        int latitudeE6 = (int) (location.getLatitude() * 1E6);
	        int longitudeE6 = (int) (location.getLongitude() * 1E6);
	        GeoPoint gp = new GeoPoint(latitudeE6, longitudeE6);
	        mapView.getController().animateTo(gp);
        }
    }

    private void addressToGeocode() {
        LayoutInflater factory = LayoutInflater.from(this);
        View view = factory.inflate(R.layout.geocode, null);
        final Button addressToGeocodeButton = (Button) view
                .findViewById(R.id.AddressToGeocodeButton);
        final EditText addressEditText = (EditText) view
                .findViewById(R.id.AddressEditText);
        final EditText longitudeEditText = (EditText) view
                .findViewById(R.id.LongitudeEditText);
        final EditText latitudeEditText = (EditText) view
                .findViewById(R.id.LatitudeEditText);
        final Spinner addressSpinner = (Spinner) view
                .findViewById(R.id.AddressSpiner);

        // 選択せず
        longitude = null;
        latitude = null;

        addressToGeocodeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CharSequence addressText = addressEditText.getText();
                Geocoder coder = new Geocoder(getApplicationContext());
                // TODO 日本語はどうするか？
                // Geocoder coder = new Geocoder(getApplicationContext(),
                // Locale.JAPAN);
                List<Address> addresses = null;
                try {
                    addresses = coder.getFromLocationName(addressText.toString(),
                            MAX_GEOCODE_RESULT);
                } catch (IOException e) {
                    Log.e("LocationExampleActivity", "Geocoder call failed", e);
                    return;
                }
                for (Address address : addresses) {
                	Log.d("LocationExampleActivity", 
                			"latitude = " + address.getLatitude() 
                			+ " longitude = " + address.getLongitude());
                }
                if (addresses.size() > 0) {
                    List<AddressWrapper> wrapperList = new ArrayList<AddressWrapper>();
                    for (Address address : addresses) {
                        wrapperList.add(new AddressWrapper(address));
                    }
                    ArrayAdapter<AddressWrapper> addressAdapter = new ArrayAdapter<AddressWrapper>(
                            getApplicationContext(),
                            android.R.layout.simple_spinner_item, wrapperList);
                    addressSpinner.setAdapter(addressAdapter);
                } else {
                    ArrayAdapter<AddressWrapper> addressAdapter = new ArrayAdapter<AddressWrapper>(
                            getApplicationContext(),
                            android.R.layout.simple_spinner_item,
                            new ArrayList<AddressWrapper>());
                    addressSpinner.setAdapter(addressAdapter);
                    longitude = null;
                    latitude = null;
                    longitudeEditText.setText(null);
                    latitudeEditText.setText(null);
                }
            }
        });
        addressSpinner
                .setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> adapterView,
                            View view, int position, long id) {

                        ArrayAdapter<AddressWrapper> adapter = (ArrayAdapter<AddressWrapper>) adapterView
                                .getAdapter();
                        Address address = adapter.getItem(position)
                                .getAddress();
                        longitude = new Double(address.getLongitude());
                        latitude = new Double(address.getLatitude());
                        longitudeEditText.setText(String.valueOf(longitude));
                        latitudeEditText.setText(String.valueOf(latitude));

                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                        longitude = null;
                        latitude = null;
                        longitudeEditText.setText(null);
                        latitudeEditText.setText(null);
                    }
                });
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view)
                .setPositiveButton(R.string.go_to_there,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                Log.d("LocationExampleActivity", "latitude = "
                                        + latitude);
                                Log.d("LocationExampleActivity", "longitude = "
                                        + longitude);
                                if (latitude != null && longitude != null) {
                                    int latitudeE6 = (int) (latitude * 1E6);
                                    int longitudeE6 = (int) (longitude * 1E6);

                                    mapView.getController().animateTo(
                                            new GeoPoint(latitudeE6,
                                                    longitudeE6));
                                }
                            }
                        }).setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                            }
                        }).create();
        dialog.show();

    }

    private LocationListener locationListener;
    private LocationManager locationManager;

    @Override
    protected void onResume() {
        // アクティビティが全面に復帰した際はLocationListenerを登録
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> allProviders = locationManager.getAllProviders();
        for (int i = 0; i < allProviders.size(); i++) {
            Log.d("LocationExampleActivity", "provider [" + i + "] is "
                    + allProviders.get(i));
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, locationListener);

        // MyLocationOverlayを有効化
        myLocationOverlay.enableMyLocation();
        // Android SDK 1.5のemulatorでは、コンパスが提供されないため、コメント
//        myLocationOverlay.enableCompass();
        mapView.invalidate();
        super.onResume();
    }

    @Override
    protected void onPause() {
    	// アクティビティが全面にない間は、省電力化のためにLocationListenerを解除
    	locationManager.removeUpdates(locationListener);
    	locationManager = null;
        // MyLocationOverlayを無効化
        myLocationOverlay.disableMyLocation();
        myLocationOverlay.disableCompass();
        super.onPause();
    }
}