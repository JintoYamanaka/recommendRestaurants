package com.example.originalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements LocationListener, View.OnClickListener{
    private LocationManager mLocationManager;
    private EditText inputTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // パーミッションを確認
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // いずれも得られていない場合はパーミッションのリクエストを要求する
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        inputTextView = findViewById(R.id.serch_input);
        Button presentLocationButton = (Button) findViewById(R.id.button_present_location);
        presentLocationButton.setOnClickListener(this);
        Button searchButton = (Button) findViewById(R.id.button_search);
        searchButton.setOnClickListener(this);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        Log.d("PlaceSample", "plog onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("PlaceSample", "plog onPause");
        super.onPause();
        if(mLocationManager != null) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLocationManager.removeUpdates(this);

        Intent intent = new Intent(getApplicationContext(), RecommendActivity.class);
        intent.putExtra("LATITUDE", location.getLatitude());
        intent.putExtra("LONGITUDE", location.getLongitude());
        startActivity(intent);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        switch (i) {
            case LocationProvider.AVAILABLE:
                Log.d("PlaceSample", "AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("PlaceSample", "OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("PlaceSample", "TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_present_location) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            // まずはGPSで取得
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            // GPSが作動しないとき、上のリクエストでonLocationChangedが呼ばれない。requestLocationUpdatesの2度投げはOKなのでWifiを使ってもう一回呼ぶ。
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } else if(view.getId() == R.id.button_search) {
            String address = inputTextView.getText().toString();
            if(address.length() != 0) {
                double[] latlng = Latlng.getLocationFromPlaceName(this, address);
                Intent intent = new Intent(getApplicationContext(), RecommendActivity.class);
                intent.putExtra("LATITUDE", latlng[0]);
                intent.putExtra("LONGITUDE", latlng[1]);
                startActivity(intent);
            }
        }

    }
}
