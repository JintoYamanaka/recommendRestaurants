package com.example.originalapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Latlng {

    /**
     * 場所名から緯度・経度を取得する。
     * @param context
     * @param locateName
     * @return 緯度・経度の配列、または、null
     */
    public static double[] getLocationFromPlaceName(Context context, String locateName) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> location = geocoder.getFromLocationName(locateName, 1);
            if (location == null || location.size() < 1) {
                return null;
            }

            Address address = location.get(0);
            double[] latlng = { address.getLatitude(), address.getLongitude() };
            return latlng;
        }
        catch (IOException e) {
            // 例外処理
            return null;
        }
    }

    /*
     * 2点間の距離（メートル）、方位角（始点、終点）を取得
     * ※配列で返す[距離、始点から見た方位角、終点から見た方位角]
     */
    public static float[] getDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        // 結果を格納するための配列を生成
        float[] distance = new float[3];

        // 距離計算
        Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, distance);

        return distance;
    }
}
