package com.example.originalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class RecommendActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView[] restaurantNameViews = new TextView[3];
    private TextView[] restaurantLocationViews = new TextView[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        restaurantNameViews[0] = findViewById(R.id.first_restaurant_name);
        restaurantNameViews[1] = findViewById(R.id.second_restaurant_name);
        restaurantNameViews[2] = findViewById(R.id.third_restaurant_name);
        restaurantLocationViews[0] = findViewById(R.id.first_restaurant_locate);
        restaurantLocationViews[1] = findViewById(R.id.second_restaurant_locate);
        restaurantLocationViews[2] = findViewById(R.id.third_restaurant_locate);
        restaurantLocationViews[0].setOnClickListener(this);
        restaurantLocationViews[1].setOnClickListener(this);
        restaurantLocationViews[2].setOnClickListener(this);

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("LATITUDE", 0);
        double longitude = intent.getDoubleExtra("LONGITUDE", 0);
        Log.d("get_location", String.valueOf(latitude));
        Log.d("get_location", String.valueOf(longitude));

        HttpGetTask task = new HttpGetTask(this, restaurantNameViews, restaurantLocationViews, latitude, longitude);
        task.execute();
    }

    @Override
    public void onClick(View view) {
        TextView clickedText = findViewById(view.getId());
        Uri uri = Uri.parse("geo:0,0?q=" + clickedText.getText());
        System.out.println(clickedText.getText());

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}