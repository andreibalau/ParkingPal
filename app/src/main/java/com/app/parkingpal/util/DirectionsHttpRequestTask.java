package com.app.parkingpal.util;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.app.parkingpal.ui.main.GmapMainActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DirectionsHttpRequestTask extends AsyncTask<String, String, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... uri) {
        HttpURLConnection urlConnection = null;
        JSONObject jsonObject = null;
        try {
            URL url = new URL(uri[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream urlResponse = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(urlResponse , "UTF-8"),8 );
            jsonObject = new JSONObject(parseBufferedReader(buffReader));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return jsonObject;
    }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
    protected void onPostExecute(JSONObject result) {//TODO: refactor it
        super.onPostExecute(result);

            try {
                JSONArray jsonArray = result.getJSONArray("routes");
                if(jsonArray.isNull(0)){
                    Log.d("RESPONSE ===>", "--------NULL---------");
                    return;
                }
                JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
                JSONObject overviewPolyline = new JSONObject(jsonObject.getString("overview_polyline"));
                List<LatLng> points = PolyUtil.decode(overviewPolyline.getString("points"));
                Polyline polyline = GmapMainActivity.getGoogleMap().addPolyline(new PolylineOptions().addAll(points));
                polyline.setWidth(17f);
                polyline.setColor(Color.rgb(110, 165, 255));
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    private String parseBufferedReader(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder.toString();
    }
}
