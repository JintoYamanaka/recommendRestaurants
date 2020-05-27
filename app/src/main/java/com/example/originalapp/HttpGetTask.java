package com.example.originalapp;

import android.app.Activity;
import android.app.ProgressDialog;
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

//    private TextView mTextView;
    private Activity mParentActivity;
    private TextView[] mRestaurantNameViews = new TextView[3];
    private TextView[] mRestaurantLocationViews = new TextView[3];
//    private Button[] mRestaurantLocationButtons = new Button[3];
//    private TextView[] mFirstRestaurantViews = new TextView[2];
//    private TextView[] mSecondRestaurantViews = new TextView[2];
//    private TextView[] mThirdRestaurantViews = new TextView[2];

    private double mLatitude;
    private double mLongitude;
    private ProgressDialog mDialog = null;

    // 実行するURL
//    private String mUri = "https://jitakuizakaya.com/category/tsukuba/";
//    private String mUri = "https://jitakuizakaya.com/category/tsukuba/page/2/";

//    public HttpGetTask(Activity parentActivity, TextView[] firstRestaurantViews, TextView[] secondRestaurantViews,
//                       TextView[] thirdRestaurantViews, double latitude, double longitude) {
//
    public HttpGetTask(Activity parentActivity, TextView[] restaurantNameViews, TextView[] restaurantLocationViews,
                       double latitude, double longitude) {

        this.mParentActivity = parentActivity;
        this.mRestaurantNameViews = restaurantNameViews;
        this.mRestaurantLocationViews = restaurantLocationViews;
//        this.mRestaurantLocationButtons = restaurantLocationButtons;
//        this.mFirstRestaurantViews = firstRestaurantViews;
//        this.mSecondRestaurantViews = secondRestaurantViews;
//        this.mThirdRestaurantViews = thirdRestaurantViews;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    // タスク開始時
    @Override
    protected void onPreExecute() {
        mDialog = new ProgressDialog(mParentActivity);
        mDialog.setMessage("取得中...");
        mDialog.show();
    }

    // メイン処理
    @Override
    protected String[][] doInBackground(Void... voids) {
        return exec_get();
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

//        this.mRestaurantLocationButtons[0].setText(stringList[0][1]);
//        this.mRestaurantLocationButtons[1].setText(stringList[1][1]);
//        this.mRestaurantLocationButtons[2].setText(stringList[2][1]);

//        this.mFirstRestaurantViews[0].setText(stringList[0][0]);
//        this.mFirstRestaurantViews[1].setText(stringList[0][1]);
//        this.mSecondRestaurantViews[0].setText(stringList[1][0]);
//        this.mSecondRestaurantViews[1].setText(stringList[1][1]);
//        this.mThirdRestaurantViews[0].setText(stringList[2][0]);
//        this.mThirdRestaurantViews[1].setText(stringList[2][1]);
    }

    private String[][] exec_get() {
        HttpURLConnection http = null;
        InputStream in = null;
//        ArrayList<ArrayList<String>> restaurants = new ArrayList<>();
        float[] top3Distance = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};
//        String[][] top3Restaurants = {{"", "", ""}, {"", "", ""}, {"", "", ""}};
        String[][] top3Restaurants = {{"", ""}, {"", ""}, {"", ""}};
//        ArrayList<String[]> restaurants;
        String src = "";

        try {

            int pageNum = 1;
            String pageUrl = "https://jitakuizakaya.com/category/tsukuba/page/1";
            while(isExistURL(pageUrl)) {
                Document listDoc = Jsoup.connect(pageUrl).get();
                Elements nameList = listDoc.select(".item-title a"); // 店名とURLの一覧を取得
                for (Element name : nameList) {
//                    System.out.println("title: " + name.ownText() + ",  href: " + name.absUrl("href"));
                    Document infoDoc = Jsoup.connect(name.absUrl("href")).get(); // お店の詳細ページをスクレイピング
                    Element address = infoDoc.select(".single-contents dd").first(); // 住所を取得
//                    Element url = infoDoc.select(".single-contents dd a").get(1); // URLを取得
                    System.out.println("名前: " + name.ownText());
                    System.out.println("住所: " + address.ownText());
//                    System.out.println("URL: " + url.ownText());

                    double[] latlng = Latlng.getLocationFromPlaceName(mParentActivity, address.ownText());
//                    double[] latlng = Location.getLocationFromPlaceName(mParentActivity, "つくば市桜2-30-4");
//                    System.out.println("緯度: " + String.valueOf(latlng[0]));
//                    System.out.println("経度: " + String.valueOf(latlng[1]));


                    // 現在位置との距離を計算
                    float[] distance = Latlng.getDistance(mLatitude, mLongitude, latlng[0], latlng[1]);

                    if(top3Distance[2] < distance[0]) {
                        continue;
                    } else if(top3Distance[0] >= distance[0]) {
                        top3Distance[2] = top3Distance[1];
                        top3Distance[1] = top3Distance[0];
                        top3Distance[0] = distance[0];

                        top3Restaurants[2][0] = top3Restaurants[1][0]; // 店名
                        top3Restaurants[2][1] = top3Restaurants[1][1]; // 住所
//                        top3Restaurants[2][2] = top3Restaurants[1][2]; // URL
                        top3Restaurants[1][0] = top3Restaurants[0][0]; // 店名
                        top3Restaurants[1][1] = top3Restaurants[0][1]; // 住所
                        top3Restaurants[0][0] = name.ownText(); // 店名
                        top3Restaurants[0][1] = address.ownText(); // 住所
                    } else if(top3Distance[1] >= distance[0]) {
                        top3Distance[2] = top3Distance[1];
                        top3Distance[1] = distance[0];

                        top3Restaurants[2][0] = top3Restaurants[1][0]; // 店名
                        top3Restaurants[2][1] = top3Restaurants[1][1]; // 住所
                        top3Restaurants[1][0] = name.ownText(); // 店名
                        top3Restaurants[1][1] = address.ownText(); // 住所
                    } else if(top3Distance[2] >= distance[0]) {
                        top3Distance[2] = distance[0];

                        top3Restaurants[2][0] = name.ownText(); // 店名
                        top3Restaurants[2][1] = address.ownText(); // 住所
                    }

//                    for (int i = 0; i < top3Distance.length; i++){
//                        System.out.println(top3Distance[i]);
//                        if(top3Distance[i] >= distance[0]) {
//                            top3Distance[i] = distance[0];
//                            top3Restaurants[i][0] = name.ownText(); // 店名
//                            top3Restaurants[i][1] = address.ownText(); // 住所
//                            break;
//                        }
//                    }

//                    ArrayList<String> restaurant = new ArrayList<>();
//                    restaurant.add(name.ownText());
//                    restaurant.add(address.ownText());
//                    restaurant.add(String.valueOf(distance[0]));
//                    restaurants.add(restaurant);

                }

                pageNum++;
                pageUrl = "https://jitakuizakaya.com/category/tsukuba/page/" + String.valueOf(pageNum);
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
