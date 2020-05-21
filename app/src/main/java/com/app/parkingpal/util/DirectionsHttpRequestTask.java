package com.app.parkingpal.util;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.app.parkingpal.ui.main.GmapMainActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class DirectionsHttpRequestTask extends AsyncTask<String, String, Optional<JSONObject>> {
    private final static String DIRECTIONS_ROUTES = "routes";
    private final static String DIRECTIONS_OVERVIEW_POLYLINE = "overview_polyline";
    private final static String DIRECTIONS_POINTS = "points";
    private HttpURLConnection urlConnection;
    private Polyline polyline;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected Optional<JSONObject> doInBackground(String... uri) {
        Optional<JSONObject> optionalJsonObject = Optional.empty();
        try {
            URL url = new URL(uri[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            JSONObject jsonObject = new JSONObject(parseDirectionsAPIInputStreamResponse(urlConnection.getInputStream()));
            optionalJsonObject = Optional.of(jsonObject);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return optionalJsonObject;
    }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
    protected void onPostExecute(Optional<JSONObject> result) {
        super.onPostExecute(result);
        if(!result.isPresent()){
            return;
        }
        try {
            JSONArray jsonArray = result.get().getJSONArray(DIRECTIONS_ROUTES);
            if(jsonArray.isNull(0)){
                return;
            }
            JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
            JSONObject overviewPolylineJson = new JSONObject(jsonObject.getString(DIRECTIONS_OVERVIEW_POLYLINE));
            List<LatLng> points = PolyUtil.decode(overviewPolylineJson.getString(DIRECTIONS_POINTS));
            polyline = GmapMainActivity.getGmap().addPolyline(new PolylineOptions().addAll(points));
            GmapMainActivity.addToPolylineHistory(polyline);//It lazy adds the polyline, when pressing a new marker it adds the previous polyline into polylineHistory instead of the current , maybe because of the async call
            polyline.setWidth(17f);
            polyline.setColor(Color.rgb(110, 165, 255));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String parseDirectionsAPIInputStreamResponse(InputStream inputStream) throws IOException {
        BufferedReader buffReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8),8 );
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = buffReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder.toString();
    }
}
