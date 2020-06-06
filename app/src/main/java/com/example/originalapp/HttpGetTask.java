package com.example.originalapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpGetTask extends AsyncTask<Void, Void, String[][]> {

    private Activity mParentActivity;
    private TextView[] mRestaurantNameViews = new TextView[3];
    private TextView[] mRestaurantLocationViews = new TextView[3];

    private double mLatitude;
    private double mLongitude;
    private ProgressDialog mDialog = null;

    public HttpGetTask(Activity parentActivity, TextView[] restaurantNameViews, TextView[] restaurantLocationViews,
                       double latitude, double longitude) {

        this.mParentActivity = parentActivity;
        this.mRestaurantNameViews = restaurantNameViews;
        this.mRestaurantLocationViews = restaurantLocationViews;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    // タスク開始時
    @Override
    protected void onPreExecute() {
        mDialog = new ProgressDialog(mParentActivity);
        mDialog.setMessage("情報を取得しています...");
        mDialog.show();
    }

    // メイン処理
    @Override
    protected String[][] doInBackground(Void... voids) {
        return execGet();
    }

    // タスク終了時
    @Override
        protected void onPostExecute(String[][] stringList) {
        mDialog.dismiss();

        this.mRestaurantNameViews[0].setText(stringList[0][0]);
        this.mRestaurantNameViews[1].setText(stringList[1][0]);
        this.mRestaurantNameViews[2].setText(stringList[2][0]);
        this.mRestaurantLocationViews[0].setText(stringList[0][1]);
        this.mRestaurantLocationViews[1].setText(stringList[1][1]);
        this.mRestaurantLocationViews[2].setText(stringList[2][1]);

        // テキストにアンダーラインをセット
        this.mRestaurantLocationViews[0].setPaintFlags(this.mRestaurantNameViews[0].getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        this.mRestaurantLocationViews[1].setPaintFlags(this.mRestaurantNameViews[1].getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        this.mRestaurantLocationViews[2].setPaintFlags(this.mRestaurantNameViews[2].getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

    }

    private String[][] execGet() {
        HttpURLConnection http = null;
        InputStream in = null;
        float[] top3Distance = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};
        String[][] top3Restaurants = {{"", ""}, {"", ""}, {"", ""}};

        try {
            int pageNum = 1;
            String pageUrl = "https://jitakuizakaya.com/category/tsukuba/page/1";
            while(isExistURL(pageUrl)) {
                Document listDoc = Jsoup.connect(pageUrl).get();
                Elements nameList = listDoc.select(".item-title a"); // 店名とURLの一覧を取得
                for (Element name : nameList) {
                    Document infoDoc = Jsoup.connect(name.absUrl("href")).get(); // お店の詳細ページをスクレイピング
                    Element address = infoDoc.select(".single-contents dd").first(); // 住所を取得

                    double[] latlng = Latlng.getLocationFromPlaceName(mParentActivity, address.ownText());

                    // 現在位置との距離を計算
                    float distance = Latlng.getDistance(mLatitude, mLongitude, latlng[0], latlng[1]);

                    if(top3Distance[2] < distance) {
                        continue;
                    } else if(top3Distance[0] >= distance) {
                        top3Distance[2] = top3Distance[1];
                        top3Distance[1] = top3Distance[0];
                        top3Distance[0] = distance;
                        top3Restaurants[2][0] = top3Restaurants[1][0]; // 店名
                        top3Restaurants[2][1] = top3Restaurants[1][1]; // 住所
                        top3Restaurants[1][0] = top3Restaurants[0][0]; // 店名
                        top3Restaurants[1][1] = top3Restaurants[0][1]; // 住所
                        top3Restaurants[0][0] = name.ownText(); // 店名
                        top3Restaurants[0][1] = address.ownText(); // 住所
                    } else if(top3Distance[1] >= distance) {
                        top3Distance[2] = top3Distance[1];
                        top3Distance[1] = distance;
                        top3Restaurants[2][0] = top3Restaurants[1][0]; // 店名
                        top3Restaurants[2][1] = top3Restaurants[1][1]; // 住所
                        top3Restaurants[1][0] = name.ownText(); // 店名
                        top3Restaurants[1][1] = address.ownText(); // 住所
                    } else if(top3Distance[2] >= distance) {
                        top3Distance[2] = distance;
                        top3Restaurants[2][0] = name.ownText(); // 店名
                        top3Restaurants[2][1] = address.ownText(); // 住所
                    }
                }
                pageNum++;
                pageUrl = "https://jitakuizakaya.com/category/tsukuba/page/" + pageNum;
            }

            System.out.println(top3Restaurants[0][0] + " " + top3Restaurants[0][1]);
            System.out.println(top3Restaurants[1][0] + " " + top3Restaurants[1][1]);
            System.out.println(top3Restaurants[2][0] + " " + top3Restaurants[2][1]);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(http != null) {
                    http.disconnect();
                }
                if(in != null) {
                    in.close();
                }
            } catch (Exception ignored) {

            }
        }
        return top3Restaurants;
    }

    private boolean isExistURL(String urlStr) {
        URL url;
        int status = 0;
        try {
            url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.connect();
            status = conn.getResponseCode();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (status == HttpURLConnection.HTTP_OK) {
            return true;
        } else {
            return false;
        }
    }
}