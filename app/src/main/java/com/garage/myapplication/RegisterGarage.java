package com.garage.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

public class RegisterGarage extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapClickListener,
        View.OnClickListener, AdapterView.OnItemSelectedListener{

    //Our Map
    private GoogleMap mMap;

    //To store longitude and latitude from map
    private double longitude;
    private double latitude;

    private Spinner spinnerType;
    private ArrayList<String> TypeGarage = new ArrayList<>();
    String username;

    EditText Edit1, Edit2, Edit3;

    String lll,ggg;

    //Google ApiClient
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_garage);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing googleapi client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Initializing views and adding onclick listeners

//        Edit1 = (EditText) findViewById(R.id.editLat);
//        Edit2 = (EditText) findViewById(R.id.editLong);
        Edit3 = (EditText) findViewById(R.id.editAddressGarage);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");

        spinnerType = (Spinner) findViewById(R.id.spinnerTypeGarage);
        createArrayType();
//        spinnerProvinse = (Spinner) findViewById(R.id.spinnerProvinseGarage);
//        createArrayProvinse();



        //AdapterArrayType
        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, TypeGarage);
        spinnerType.setAdapter(adapterType);
        spinnerType.setOnItemSelectedListener(this);

        //AdapterArrayType
//        ArrayAdapter<String> adapterProvinse = new ArrayAdapter<String>(this,
//                android.R.layout.simple_dropdown_item_1line, ProvinseGarage);
//        spinnerProvinse.setAdapter(adapterProvinse);
//        spinnerProvinse.setOnItemSelectedListener(this);

        // btnSaveGarage
        final Button btnGarage = (Button) findViewById(R.id.btnGarage);
        // Perform action on click
        btnGarage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (SaveGarage()) {

                    // When Save Complete
                    Intent i;
                    i = new Intent(getApplicationContext(), Owner.class);
                    startActivity(i);
                    finish();
                }
            }
        });


        // btnCancel
        final Button btnCancel = (Button) findViewById(R.id.btnCancel);
        // Perform action on click
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent newActivity = new Intent(EditGarage.this,Garage.class);
//                startActivity(newActivity);
                onBackPressed();
            }
        });

    }

    //Array ประเภทร้านน่อม
    private void createArrayType() {
        TypeGarage.add("กรุณาเลือกประเภทร้านซ่อม");
        TypeGarage.add("Car");
        TypeGarage.add("Motorcycle");
        TypeGarage.add("Car/Motorcycle");
        TypeGarage.add("Other");
    }


    // Save Garage to database
    private boolean SaveGarage() {

        final EditText txtNameGarage = (EditText) findViewById(R.id.editNameGarage);
        final EditText txtNameOwnerGarage = (EditText) findViewById(R.id.editNameOwnerGarage);
        final Spinner txtTypeGarage = (Spinner) findViewById(R.id.spinnerTypeGarage);
//        final EditText txtLat = (EditText) findViewById(R.id.editLat);
//        final EditText txtLong = (EditText) findViewById(R.id.editLong);
        final EditText txtAddressGarage = (EditText) findViewById(R.id.editAddressGarage);
        final EditText txtTelephone = (EditText) findViewById(R.id.editTelephone);
//        final Spinner txtProvinseGarage = (Spinner) findViewById(R.id.spinnerProvinseGarage);
        final EditText txtDetailGarage = (EditText) findViewById(R.id.editDetailGarage);

        // Dialog
        final AlertDialog.Builder ad = new AlertDialog.Builder(this);

        ad.setTitle("กรอกข้อมูลไม่ครบ ");
        ad.setPositiveButton("Close", null);

        // Check Name garage
        if (txtNameGarage.getText().length() == 0) {
            ad.setMessage("กรุณากรอกชื่อร้าน ");
            ad.show();
            txtNameGarage.requestFocus();
            return false;
        }

        // Check name owner
        if (txtNameOwnerGarage.getText().length() == 0) {
            ad.setMessage("กรูณากรอกชื่อเจ้าของร้าน ");
            ad.show();
            txtNameOwnerGarage.requestFocus();
            return false;
        }

        if (txtTypeGarage.getSelectedItem().equals("กรุณาเลือกประเภทร้านซ่อม")){
            ad.setMessage("กรูณาเลือกประเภทร้าน ");
            ad.show();
            txtTelephone.requestFocus();
            return false;
        }

        // Check telephone
        if (txtTelephone.getText().length() == 0) {
            ad.setMessage("กรูณากรอกเบอร์โทรศัพท์ร้าน ");
            ad.show();
            txtTelephone.requestFocus();
            return false;
        }


//        if (txtProvinseGarage.getSelectedItem().equals("กรุณาเลือกจังหวัด")){
//            ad.setMessage("กรูณาเลือกจังหวัด ");
//            ad.show();
//            txtTelephone.requestFocus();
//            return false;
//        }



        String url = "http://projectfinal.esy.es/ProjectApp/SaveGarage.php";

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sNameGarage", txtNameGarage.getText().toString()));
        params.add(new BasicNameValuePair("sGarageOwner", txtNameOwnerGarage.getText().toString()));
        params.add(new BasicNameValuePair("sType", txtTypeGarage.getSelectedItem().toString()));
//        params.add(new BasicNameValuePair("sGarage_Lat", txtLat.getText().toString()));
//        params.add(new BasicNameValuePair("sGarage_Long", txtLong.getText().toString()));
        params.add(new BasicNameValuePair("sGarage_Lat", lll));
        params.add(new BasicNameValuePair("sGarage_Long", ggg));
        params.add(new BasicNameValuePair("sAddress", txtAddressGarage.getText().toString()));
        params.add(new BasicNameValuePair("sTelephone", txtTelephone.getText().toString()));
//        params.add(new BasicNameValuePair("sProvise", txtProvinseGarage.getSelectedItem().toString()));
        params.add(new BasicNameValuePair("sDetail", txtDetailGarage.getText().toString()));
        params.add(new BasicNameValuePair("susername", username));
        /** Get result from Server (Return the JSON Code)
         * StatusID = ? [0=Failed,1=Complete]
         * Error	= ?	[On case error return custom error message]
         *
         * Eg Save Failed = {"StatusID":"0","Error":"Email Exists!"}
         * Eg Save Complete = {"StatusID":"1","Error":""}
         */

        String resultServer = getHttpPost(url, params);

        /*** Default Value ***/
        String strStatusID = "0";
        String strError = "Unknow Status!";

        JSONObject c;
        try {
            c = new JSONObject(resultServer);
            strStatusID = c.getString("StatusID");
            strError = c.getString("Error");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Prepare Save Data
        if (strStatusID.equals("0")) {
            ad.setMessage(strError);
            ad.show();
        } else {
            Toast.makeText(RegisterGarage.this, "บันทึกร้านสำเร็จ", Toast.LENGTH_SHORT).show();
            txtNameGarage.setText("");
            txtNameOwnerGarage.setText("");
            txtTypeGarage.setSelected(Boolean.parseBoolean(""));
//            txtLat.setText("");
//            txtLong.setText("");
            txtAddressGarage.setText("");
            txtTelephone.setText("");
//            txtProvinseGarage.setSelected(Boolean.parseBoolean(""));
            txtDetailGarage.setText("");

        }


        return true;
    }




















    // Maker googlemap
    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(13.72917, 100.52389);
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapClickListener(this);
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
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    //Getting current location
    private void getCurrentLocation() {
        //Creating a location object
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            //moving the map to location
            moveMap();
        }
    }

    //Function to move the map
    private void moveMap() {
        //String to display current latitude and longitude
        String msg = latitude + ", "+longitude;
        double el = latitude;
        double elg = longitude;

        //Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(latitude, longitude);

        //Adding marker to map
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude)) //setting position
                .draggable(true) //Making the marker draggable
                .title("แตะที่หมุดค้างเพื่อทำการลากหมุด")).showInfoWindow(); //Adding a title


        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //Displaying current coordinates in toast
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
//        Edit1.setText("" + el);
//        Edit2.setText("" + elg);

        lll = String.valueOf(el);
        ggg = String.valueOf(elg);
        Edit3.setText(getAddress(el,elg));
    }

    @Override
    public void onClick(View v) {
            getCurrentLocation();
            moveMap();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)  {
        getCurrentLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        //Clearing all the markers
        mMap.clear();

        //Adding a new marker to the current pressed position
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("แตะที่หมุดค้างเพื่อทำการลากหมุด")).showInfoWindow();
    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //Getting the coordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        mMap.clear();
        //Moving the map
        moveMap();
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + " " + obj.getLocality();
            add = add + " " + obj.getAdminArea();
            add = add + " " + obj.getPostalCode();

            return add;
            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
























    private String getHttpPost(String url, List<NameValuePair> params) {
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //ดักการกดปุ่ม back ก่อนออกจากโปรแกรม
    public void onBackPressed(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterGarage.this);

        alertDialog.setTitle("Confirm Exit...");
        alertDialog.setMessage("คุณต้องการออกจากหน้านี้หรือไม่ ?");
        alertDialog.setIcon(R.drawable.wrench);

        alertDialog.setPositiveButton("ใช่",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        //คลิกใช่ ออกจากโปรแกรม
                        finish();
                        RegisterGarage.super.onBackPressed();
                    }
                });

        alertDialog.setNegativeButton("ไม่",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,	int which) {
                        //คลิกไม่ cancel dialog
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }

}
