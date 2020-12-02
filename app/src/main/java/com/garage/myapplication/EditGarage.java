package com.garage.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

import static com.garage.myapplication.R.id.editAddressGarage;

public class EditGarage extends Activity implements AdapterView.OnItemSelectedListener{


    EditText Edit3;

    private Spinner spinnerType;
    private ArrayList<String> TypeGarage = new ArrayList<>();
    private ArrayList<String> ProvinseGarage = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_garage);

        Edit3 = (EditText) findViewById(R.id.editAddressGarage);
        spinnerType = (Spinner) findViewById(R.id.spinnerTypeGarage);
        createArrayType();

        //AdapterArrayType
        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, TypeGarage);
        spinnerType.setAdapter(adapterType);
        spinnerType.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);


        showInfo();

        // btnSave
        Button btnSave = (Button) findViewById(R.id.btnSave);
        // Perform action on click
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (SaveData()) {
                    // When Save Complete
                    Intent newActivity = new Intent(EditGarage.this, Owner.class);
                    startActivity(newActivity);
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
                finish();
            }
        });

    }

    //Array ประเภทร้านน่อม
    private void createArrayType() {
        TypeGarage.add("กรุณาเลือกประเภทร้านซ่อม");
        TypeGarage.add("Car");
        TypeGarage.add("Motorcycle");
        TypeGarage.add("Car/Motorcycle");
    }

    public void showInfo(){

        final TextView txtGarageID = (TextView) findViewById(R.id.txtGarageID);
        final TextView txtNameGarage = (TextView) findViewById(R.id.editNameGarage);
        final TextView txtNameOwnerGarage = (TextView) findViewById(R.id.editNameOwnerGarage);
        final TextView txtAddress = (TextView)findViewById(editAddressGarage);
        final TextView txtTelephone = (TextView) findViewById(R.id.editTelephone);
        final TextView txtDetailGarage = (TextView) findViewById(R.id.editDetailGarage);

        Button btnSave = (Button) findViewById(R.id.btnSave);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);

        String url = "http://projectfinal.esy.es/ProjectApp/Garage/getGarageID.php";

        Intent intent= getIntent();
        final String Garage_ID = intent.getStringExtra("Garage_ID");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sGarage_ID", Garage_ID));

        /** Get result from Server (Return the JSON Code)
         *
         * {"MemberID":"2","Username":"adisorn","Password":"adisorn@2","Name":"Adisorn Bunsong","Tel":"021978032","Email":"adisorn@thaicreate.com"}
         */
        String resultServer  = getHttpPost(url,params);

        String strGarageID = "";
        String strNameGarage = "";
        String strAddress = "";
        String strTelephone = "";
        String strGarageOwner = "";
        String strDetail = "";

        JSONObject c;
        try {
            c = new JSONObject(resultServer);
            strGarageID = c.getString("Garage_ID");
            strNameGarage = c.getString("NameGarage");
            strAddress = c.getString("Address");
            strTelephone = c.getString("Telephone");
            strGarageOwner = c.getString("GarageOwner");
            strDetail = c.getString("Detail");


            if(!strGarageID.equals(""))
            {
                txtGarageID.setText(strGarageID);
                txtNameGarage.setText(strNameGarage);
                txtAddress.setText(strAddress);
                txtTelephone.setText(strTelephone);
                txtNameOwnerGarage.setText(strGarageOwner);
                txtDetailGarage.setText(strDetail);

            }
            else
            {
                txtGarageID.setText("-");
                txtNameGarage.setText("-");
                txtAddress.setText("-");
                txtTelephone.setText("-");
                txtNameOwnerGarage.setText("-");
                txtDetailGarage.setText("-");


                btnSave.setEnabled(false);
                btnCancel.requestFocus();
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }









    // Save Garage to database
    private boolean SaveData() {

        final TextView txtGarageID = (TextView)findViewById(R.id.txtGarageID);
        final EditText txtNameGarage = (EditText) findViewById(R.id.editNameGarage);
        final EditText txtNameOwnerGarage = (EditText) findViewById(R.id.editNameOwnerGarage);
        final Spinner txtTypeGarage = (Spinner) findViewById(R.id.spinnerTypeGarage);
        final EditText txtTelephone = (EditText) findViewById(R.id.editTelephone);
//        final Spinner txtProvinseGarage = (Spinner) findViewById(R.id.spinnerProvinseGarage);
        final EditText txtAddress = (EditText) findViewById(editAddressGarage);
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



        String url = "http://projectfinal.esy.es/ProjectApp/Garage/UpdateGarage.php";

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sGarageID", txtGarageID.getText().toString()));
        params.add(new BasicNameValuePair("sNameGarage", txtNameGarage.getText().toString()));
        params.add(new BasicNameValuePair("sGarageOwner", txtNameOwnerGarage.getText().toString()));
        params.add(new BasicNameValuePair("sType", txtTypeGarage.getSelectedItem().toString()));
        params.add(new BasicNameValuePair("sAddress", txtAddress.getText().toString()));
        params.add(new BasicNameValuePair("sTelephone", txtTelephone.getText().toString()));
//        params.add(new BasicNameValuePair("sProvise", txtProvinseGarage.getSelectedItem().toString()));
        params.add(new BasicNameValuePair("sDetail", txtDetailGarage.getText().toString()));

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
            return false;
        }
        else
        {
            Toast.makeText(EditGarage.this, "Update Data Successfully", Toast.LENGTH_SHORT).show();

        }


        return true;
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


    //ดักการกดปุ่ม back ก่อนออกจากโปรแกรม
    public void onBackPressed(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditGarage.this);

        alertDialog.setTitle("Confirm Exit...");
        alertDialog.setMessage("คุณต้องการออกจากหน้านี้หรือไม่ ?");
        alertDialog.setIcon(R.drawable.wrench);

        alertDialog.setPositiveButton("ใช่",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        //คลิกใช่ ออกจากโปรแกรม
                        finish();
                        EditGarage.super.onBackPressed();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
