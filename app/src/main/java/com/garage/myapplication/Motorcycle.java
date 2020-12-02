package com.garage.myapplication;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.garage.myapplication.PermissionUtils.PermissionUtilsMotorcycle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class Motorcycle extends MapsActivity implements GoogleMap.OnMyLocationButtonClickListener, AdapterView.OnItemSelectedListener {

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    GoogleMap mGoogleMap;
    public static final int DIALOG_DOWNLOAD_JSON_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private Double Latitude = 0.00;
    private Double Longitude = 0.00;

    private int LOCATION_PERMISSION_REQUEST_CODE;
    double lblLat = 0;
    double lblLon = 0;

    String Lat;
    String Lon;
    private ImageView Route1,Report;

    double dLat;
    double dLon;
    private static LatLng Camera = null;


    protected LocationManager locationManager;
    ArrayList<HashMap<String, Object>> location1 = new ArrayList<HashMap<String, Object>>();
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    private LatLng CameraLocation;

    ListView MylistView;

    private Button btndis1;
    public Spinner spinnerProvinse;
    private ArrayList<String> ProvinseGarage = new ArrayList<>();

    private MarkerOptions marker1;
    String strID;
    String strName;


    protected int getLayoutId() {
        return R.layout.activity_motorcycle;
    }

    public void startMaps() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                MINIMUM_TIME_BETWEEN_UPDATES,
                new MyLocationListener()
        );
        mGoogleMap = getMap();

        mGoogleMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        MylistView = (ListView) findViewById(R.id.listViewMotorcycle);
        btndis1 = (Button) findViewById(R.id.button1);
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_JSON_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Detecting current location...");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtilsMotorcycle.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mGoogleMap != null) {
            // Access to the location has been granted to the app.
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    // Show All Content
    public void ShowAllContent()
    {
        // listView1
        MylistView.setAdapter(new ImageAdapter(getApplicationContext(),MyArrList,lblLat,lblLon));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        public void onProviderDisabled(String s) {

        }

        @Override
        public void onLocationChanged(Location location) {

            lblLat = location.getLatitude();
            lblLon = location.getLongitude();

            Lat = String.valueOf(lblLat);
            Lon = String.valueOf(lblLon);

            Log.i("testSearch", Lat);
            Log.i("testSearch", Lon);


            locationManager.removeUpdates(this);
            locationManager = null;

            new DownloadJSONFileAsync1000M().execute();

            btndis1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SearchData().execute();

                }
            });
        }
        public void onProviderEnabled(String s) {

        }
    }

    // Download JSON in Background

    public class DownloadJSONFileAsync1000M extends AsyncTask<String, Void, Void> {
        //
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);
        }
        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            location1.clear();
            MyArrList.clear();

            Lat = String.valueOf(lblLat);
            Lon = String.valueOf(lblLon);

            String url = "http://projectfinal.esy.es/ProjectApp/Motorcycle.php";
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("strA", Lat));
            params1.add(new BasicNameValuePair("strB", Lon));
            String resultServer = getHttpPost(url, params1);

            try {
                JSONArray data = new JSONArray(resultServer);

                HashMap<String, String> map;
                HashMap<String, Object> map1;

                for(int i = 0; i < data.length(); i++){
                    JSONObject c = data.getJSONObject(i);
                    map1 = new HashMap<String, Object>();
                    map = new HashMap<String, String>();

                    map.put("Garage_ID", c.getString("Garage_ID"));
                    map.put("NameGarage", c.getString("NameGarage"));
                    map.put("Address", c.getString("Address"));
                    map.put("Telephone", c.getString("Telephone"));
                    map.put("Garage_Lat", c.getString("Garage_Lat"));
                    map.put("Garage_Long", c.getString("Garage_Long"));
                    map.put("Provise", c.getString("Provise"));
                    map.put("Type", c.getString("Type"));
                    map.put("GarageOwner", c.getString("GarageOwner"));
                    map.put("Detail", c.getString("Detail"));
                    map.put("distance", c.getString("distance"));

                    map1.put("Garage_ID", c.getString("Garage_ID"));
                    map1.put("NameGarage", c.getString("NameGarage"));
                    map1.put("Address", c.getString("Address"));
                    map1.put("Garage_Lat", c.getString("Garage_Lat"));
                    map1.put("Garage_Long", c.getString("Garage_Long"));
                    map1.put("Type", c.getString("Type"));
                    map1.put("Telephone", c.getString("Telephone"));
                    map1.put("Detail", c.getString("Detail"));
                    map1.put("distance", c.getString("distance"));

                    MyArrList.add(map);
                    location1.add(map1);

                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void unused) {

            CameraLocation = new LatLng(lblLat, lblLon);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(CameraLocation)
                    .zoom(12)                   // Sets the zoom
//                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            MylistView.setAdapter(null);

            ShowAllContent();

            mGoogleMap.clear();

            for (int i = 0; i < location1.size(); i++) {
                Latitude = Double.parseDouble(location1.get(i).get("Garage_Lat").toString());
                Longitude = Double.parseDouble(location1.get(i).get("Garage_Long").toString());

                String name = location1.get(i).get("NameGarage").toString();
                String description =  location1.get(i).get("Address").toString();

                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                marker1 = new MarkerOptions().position(new LatLng(Latitude, Longitude))
                        .title(name)
                        .snippet(description)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_ORANGE));

                mGoogleMap.addMarker(marker1);
            }
            if(marker1 == null){
                Toast.makeText(getApplicationContext(),"ไม่มีร้านซ่อมในบริเวณนี้",Toast.LENGTH_LONG).show();
            }
            MylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Lat = MyArrList.get(position).get("Garage_Lat").toString();
                    Lon = MyArrList.get(position).get("Garage_Long").toString();
                    dLat = Double.valueOf(Lat.trim()).doubleValue();
                    dLon = Double.valueOf(Lon.trim()).doubleValue();

                    Camera = new LatLng(dLat, dLon);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(Camera)
                            .zoom(17)                   // Sets the zoom
//                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    Latitude = Double.parseDouble(location1.get(position).get("Garage_Lat").toString());
                    Longitude = Double.parseDouble(location1.get(position).get("Garage_Long").toString());
                    String name = location1.get(position).get("NameGarage").toString();
                    String description = MyArrList.get(position).get("Address").toString();
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Latitude, Longitude))
                            .title(name)
                            .snippet(description)
                            .icon(BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_ORANGE)));

                    marker.showInfoWindow();

                }
            });
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            dismissDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);
            removeDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);

        }
    }

    public class SearchData extends AsyncTask<String, Void, Void> {
        //
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);
        }
        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            location1.clear();
            MyArrList.clear();

            Lat = String.valueOf(lblLat);
            Lon = String.valueOf(lblLon);
//            Spinner txtProvinseGarage = (Spinner) findViewById(R.id.spinnerProvinseGarage);
            EditText inputText = (EditText)findViewById(R.id.editsearch);

            String url = "http://projectfinal.esy.es/ProjectApp/SearchMapMotorcycle.php";
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
//            params1.add(new BasicNameValuePair("txtKeyword", txtProvinseGarage.getSelectedItem().toString()));
            params1.add(new BasicNameValuePair("txtKeyword", inputText.getText().toString()));
            params1.add(new BasicNameValuePair("strA", Lat));
            params1.add(new BasicNameValuePair("strB", Lon));

            String resultServer = getHttpPost(url, params1);

            try {
                JSONArray data = new JSONArray(resultServer);

                HashMap<String, String> map;
                HashMap<String, Object> map1;

                for(int i = 0; i < data.length(); i++){
                    JSONObject c = data.getJSONObject(i);
                    map1 = new HashMap<String, Object>();
                    map = new HashMap<String, String>();
                    Log.i("testSearch", String.valueOf(inputText));

                    map.put("Garage_ID", c.getString("Garage_ID"));
                    map.put("NameGarage", c.getString("NameGarage"));
                    map.put("Address", c.getString("Address"));
                    map.put("Telephone", c.getString("Telephone"));
                    map.put("Garage_Lat", c.getString("Garage_Lat"));
                    map.put("Garage_Long", c.getString("Garage_Long"));
                    map.put("Provise", c.getString("Provise"));
                    map.put("Type", c.getString("Type"));
                    map.put("GarageOwner", c.getString("GarageOwner"));
                    map.put("Detail", c.getString("Detail"));
                    map.put("distance", c.getString("distance"));

                    map1.put("Garage_ID", c.getString("Garage_ID"));
                    map1.put("NameGarage", c.getString("NameGarage"));
                    map1.put("Address", c.getString("Address"));
                    map1.put("Garage_Lat", c.getString("Garage_Lat"));
                    map1.put("Garage_Long", c.getString("Garage_Long"));
                    map1.put("Type", c.getString("Type"));
                    map1.put("Telephone", c.getString("Telephone"));
                    map1.put("Detail", c.getString("Detail"));
                    map1.put("distance", c.getString("distance"));

                    MyArrList.add(map);
                    location1.add(map1);

                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            CameraLocation = new LatLng(lblLat, lblLon);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(CameraLocation)
                    .zoom(5)                   // Sets the zoom
//                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            MylistView.setAdapter(null);

            ShowAllContent();

            mGoogleMap.clear();

            for (int i = 0; i < location1.size(); i++) {
                Latitude = Double.parseDouble(location1.get(i).get("Garage_Lat").toString());
                Longitude = Double.parseDouble(location1.get(i).get("Garage_Long").toString());

                String name = location1.get(i).get("NameGarage").toString();
                String description =  location1.get(i).get("Address").toString();

                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                marker1 = new MarkerOptions()
                        .position(new LatLng(Latitude, Longitude))
                        .title(name)
                        .snippet(description)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_ORANGE));

                mGoogleMap.addMarker(marker1);
            }

            if(marker1 == null){
                Toast.makeText(getApplicationContext(),"ไม่มีร้านซ่อมในบริเวณนี้",Toast.LENGTH_LONG).show();
            }

            MylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Lat = MyArrList.get(position).get("Garage_Lat").toString();
                    Lon = MyArrList.get(position).get("Garage_Long").toString();
                    dLat = Double.valueOf(Lat.trim()).doubleValue();
                    dLon = Double.valueOf(Lon.trim()).doubleValue();

                    Camera = new LatLng(dLat, dLon);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(Camera)
                            .zoom(17)                   // Sets the zoom
//                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    Latitude = Double.parseDouble(location1.get(position).get("Garage_Lat").toString());
                    Longitude = Double.parseDouble(location1.get(position).get("Garage_Long").toString());
                    String name = location1.get(position).get("NameGarage").toString();
                    String description = MyArrList.get(position).get("Address").toString();
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Latitude, Longitude))
                            .title(name)
                            .snippet(description)
                            .icon(BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_ORANGE)));

                    marker.showInfoWindow();

                }
            });
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            dismissDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);
            removeDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);

        }
    }

    public class ImageAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<HashMap<String, String>> MyArr = new ArrayList<HashMap<String, String>>();
        private Double lblLat;
        private Double lblLon;
        public ImageAdapter(Context c , ArrayList<HashMap<String, String>> myArrList , Double Lat , Double Lon)
        {
            // TODO Auto-generated method stub
            context = c;
            MyArr = myArrList;
            lblLat = Lat;
            lblLon = Lon;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return MyArr.size();

        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_order_list_view, null);
            }

            TextView txtLocat = (TextView) convertView.findViewById(R.id.NameList);
            txtLocat.setText(MyArr.get(position).get("NameGarage"));

            TextView txtAddress = (TextView) convertView.findViewById(R.id.AddressList);
            txtAddress.setText(MyArr.get(position).get("Address"));
            TextView txtType = (TextView) convertView.findViewById(R.id.TypeList);
            txtType.setText(MyArr.get(position).get("Type"));
            TextView txtTele = (TextView) convertView.findViewById(R.id.TelephoneList);
            txtTele.setText(MyArr.get(position).get("Telephone"));
            TextView txtDetail = (TextView) convertView.findViewById(R.id.DetailList);
            txtDetail.setText(MyArr.get(position).get("Detail"));
            TextView txtDistance = (TextView) convertView.findViewById(R.id.DistanceList);
            txtDistance.setText(MyArr.get(position).get("distance")+" กิโลเมตร");

            Route1 = (ImageView) convertView.findViewById(R.id.RouteList);
            Route1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(String.valueOf(getUrl(lblLat,lblLon,dLat,dLon))));
                    startActivity(intent);
                }
            });

            final String number = txtTele.getText().toString();
            ImageView makecall = (ImageView) convertView.findViewById(R.id.CallBtn);
            makecall.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(number.length() == 0){
                        Toast.makeText(getApplicationContext(),"ร้านนี้ไม่มีเบอร์ติดต่อ",Toast.LENGTH_LONG).show();
                    }
                    else {

                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + number));
                        startActivity(intent);
                    }
                }
            });

            Report = (ImageView) convertView.findViewById(R.id.ReportBtn);
            Report.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    strID = MyArr.get(position).get("Garage_ID").toString();
                    strName = MyArr.get(position).get("NameGarage").toString();

                    Intent i = new Intent(Motorcycle.this,ReportGarage.class);
                    i.putExtra("Garage_ID", strID);
                    i.putExtra("NameGarage", strName);
                    startActivity(i);
                    finish();
                }
            });

            return convertView;
        }
    }

    public String getHttpPost(String url,List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Status OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }
    public StringBuilder getUrl(Double src1, Double src2, Double dest1, Double dest2){

        StringBuilder urlString = new StringBuilder();

        urlString.append("http://maps.google.com/maps?f=d&hl=en");
        urlString.append("&saddr=");
        urlString.append(src1.toString());
        urlString.append(",");
        urlString.append(src2.toString());
        urlString.append("&daddr=");// to
        urlString.append(dest1.toString());
        urlString.append(",");
        urlString.append(dest2.toString());
        urlString.append("&ie=UTF8&0&om=0&output=kml");

        return urlString;
    }

}
