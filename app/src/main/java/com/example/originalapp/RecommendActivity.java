package com.example.originalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class RecommendActivity extends AppCompatActivity implements LocationListener, View.OnClickListener {
    private LocationManager locationManager;
    private TextView firstItemTextView;
    private TextView secondItemTextView;
    private TextView thirdItemTextView;
    //    private TextView[] firstRestaurantViews = new TextView[2];
//    private TextView[] secondRestaurantViews = new TextView[2];
//    private TextView[] thirdRestaurantViews = new TextView[2];
    private TextView[] restaurantNameViews = new TextView[3];
    private TextView[] restaurantLocationViews = new TextView[3];
//    private Button[] restaurantLocationButtons = new Button[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

//        mTextView = findViewById(R.id.third_restaurant_locate);
//        mReturnTextView = (TextView) findViewById(R.id.third_restaurant_url);
        firstItemTextView = findViewById(R.id.first_item);
        secondItemTextView = findViewById(R.id.second_item);
        thirdItemTextView = findViewById(R.id.third_item);
//        firstRestaurantViews[0] = findViewById(R.id.first_restaurant_name);
//        firstRestaurantViews[1] = findViewById(R.id.first_restaurant_locate);
        restaurantNameViews[0] = findViewById(R.id.first_restaurant_name);
        restaurantNameViews[1] = findViewById(R.id.second_restaurant_name);
        restaurantNameViews[2] = findViewById(R.id.third_restaurant_name);
//        restaurantLocationButtons[0] = findViewById(R.id.button4);
//        restaurantLocationButtons[1] = findViewById(R.id.button5);
//        restaurantLocationButtons[2] = findViewById(R.id.button6);
//        restaurantLocationButtons[0].setOnClickListener(this);
//        restaurantLocationButtons[1].setOnClickListener(this);
//        restaurantLocationButtons[2].setOnClickListener(this);

        restaurantLocationViews[0] = findViewById(R.id.first_restaurant_locate);
        restaurantLocationViews[1] = findViewById(R.id.second_restaurant_locate);
        restaurantLocationViews[2] = findViewById(R.id.third_restaurant_locate);
        restaurantLocationViews[0].setOnClickListener(this);
        restaurantLocationViews[1].setOnClickListener(this);
        restaurantLocationViews[2].setOnClickListener(this);

//        firstRestaurantViews[1].setOnClickListener((View.OnClickListener) this);

//        secondRestaurantViews[0] = findViewById(R.id.second_restaurant_name);
//        secondRestaurantViews[1] = findViewById(R.id.second_restaurant_locate);

//        secondRestaurantViews[1].setOnClickListener((View.OnClickListener) this);

//        thirdRestaurantViews[0] = findViewById(R.id.third_restaurant_name);
//        thirdRestaurantViews[1] = findViewById(R.id.third_restaurant_locate);

//        thirdRestaurantViews[1].setOnClickListener((View.OnClickListener) this);

        firstItemTextView.setVisibility(View.INVISIBLE);
        secondItemTextView.setVisibility(View.INVISIBLE);
        thirdItemTextView.setVisibility(View.INVISIBLE);
        restaurantNameViews[0].setVisibility(View.INVISIBLE);
        restaurantNameViews[1].setVisibility(View.INVISIBLE);
        restaurantNameViews[2].setVisibility(View.INVISIBLE);
//        restaurantLocationButtons[0].setVisibility(View.INVISIBLE);
//        restaurantLocationButtons[1].setVisibility(View.INVISIBLE);
//        restaurantLocationButtons[2].setVisibility(View.INVISIBLE);
        restaurantLocationViews[0].setVisibility(View.INVISIBLE);
        restaurantLocationViews[1].setVisibility(View.INVISIBLE);
        restaurantLocationViews[2].setVisibility(View.INVISIBLE);

//        firstRestaurantViews[0].setVisibility(View.INVISIBLE);
//        firstRestaurantViews[1].setVisibility(View.INVISIBLE);
//        secondRestaurantViews[0].setVisibility(View.INVISIBLE);
//        secondRestaurantViews[1].setVisibility(View.INVISIBLE);
//        thirdRestaurantViews[0].setVisibility(View.INVISIBLE);
//        thirdRestaurantViews[1].setVisibility(View.INVISIBLE);

        // パーミッションを確認
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // いずれも得られていない場合はパーミッションのリクエストを要求する
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        // 位置情報を管理している LocationManager のインスタンスを生成する
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // まずはGPSで取得
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        /**
         GPSの値が空（GPSが作動していない）ならWifiで取得
         GPSが作動しないとき、上のリクエストでonLocationChangedが呼ばれない
         requestLocationUpdatesの2度投げはOK（公式ドキュメントより）だからWifiを使ってもう一回呼ぶ
         */
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

//        Log.d("location", String.valueOf(location));
    }

    @Override
    public void onLocationChanged(Location location){
//        mTextView.setText(String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()));
        Log.d("location", String.valueOf(location.getLatitude()));
        Log.d("location", String.valueOf(location.getLongitude()));
        locationManager.removeUpdates(this);



//        HttpGetTask task = new HttpGetTask(this, firstRestaurantViews, secondRestaurantViews,
//                thirdRestaurantViews, location.getLatitude(), location.getLongitude());
//        HttpGetTask task = new HttpGetTask(this, restaurantNameViews, restaurantLocationButtons,
//                location.getLatitude(), location.getLongitude());
        HttpGetTask task = new HttpGetTask(this, restaurantNameViews, restaurantLocationViews,
                location.getLatitude(), location.getLongitude());
        task.execute();
        // 店の名前と住所が帰ってくる

        firstItemTextView.setVisibility(View.VISIBLE);
        secondItemTextView.setVisibility(View.VISIBLE);
        thirdItemTextView.setVisibility(View.VISIBLE);
        restaurantNameViews[0].setVisibility(View.VISIBLE);
        restaurantNameViews[1].setVisibility(View.VISIBLE);
        restaurantNameViews[2].setVisibility(View.VISIBLE);
        restaurantLocationViews[0].setVisibility(View.VISIBLE);
        restaurantLocationViews[1].setVisibility(View.VISIBLE);
        restaurantLocationViews[2].setVisibility(View.VISIBLE);
//        restaurantLocationButtons[0].setVisibility(View.VISIBLE);
//        restaurantLocationButtons[1].setVisibility(View.VISIBLE);
//        restaurantLocationButtons[2].setVisibility(View.VISIBLE);
//        firstRestaurantViews[0].setVisibility(View.VISIBLE);
//        firstRestaurantViews[1].setVisibility(View.VISIBLE);
//        secondRestaurantViews[0].setVisibility(View.VISIBLE);
//        secondRestaurantViews[1].setVisibility(View.VISIBLE);
//        thirdRestaurantViews[0].setVisibility(View.VISIBLE);
//        thirdRestaurantViews[1].setVisibility(View.VISIBLE);
//        firstItemTextView.setOnClickListener(this);
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
        if(locationManager != null) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(this);
        }
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
    public void onClick(View view) {
        TextView clickedText = findViewById(view.getId());
//        clickedText.getText();
        Uri uri = Uri.parse("geo:0,0?q=" + clickedText.getText());
        System.out.println(clickedText.getText());

//        Uri uri = Uri.parse("geo:35.681382,139.766084?z=16");

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

//        if(view.getId() == R.id.button_gps) {
//            mLocationType = GPS;
//            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//        } else if(view.getId() == R.id.button_wifi) {
//            mLocationType = WIFI;
//            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//        }
    }
}