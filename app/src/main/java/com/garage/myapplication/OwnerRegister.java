package com.garage.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class OwnerRegister extends Activity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_register);


        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // btnSave
        final Button btnSave = (Button) findViewById(R.id.btnRegis);
        // Perform action on click
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(SaveData())
                {
                    // When Save Complete
                    Intent i;
                    i = new Intent(getApplicationContext(),OwnerLogin.class);
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


    private boolean SaveData() {

        // txtUsername,txtPassword,txtName
        final EditText txtName = (EditText)findViewById(R.id.txtName);
        final EditText txtUsername = (EditText)findViewById(R.id.txtUsername);
        final EditText txtPassword = (EditText)findViewById(R.id.txtPassword);
        final EditText txtConPassword = (EditText)findViewById(R.id.txtConPassword);

        // Dialog
        final AlertDialog.Builder ad = new AlertDialog.Builder(this);

        ad.setTitle("กรอกข้อมูลไม่ครบ ");
        ad.setPositiveButton("Close", null);

        // Check Name
        if(txtName.getText().length() == 0 )
        {
            ad.setMessage("Please input Name ");
            ad.show();
            txtName.requestFocus();
            return false;
        }

        // Check UserName
        if(txtUsername.getText().length() < 4 )
        {
            ad.setMessage("Username at least 4 characters ");
            ad.show();
            txtName.requestFocus();
            return false;
        }

        // Check UserName
        if(txtPassword.getText().length() < 4 )
        {
            ad.setMessage("Password at least 4 characters ");
            ad.show();
            txtName.requestFocus();
            return false;
        }

        // Check Username
        if(txtUsername.getText().length() == 0)
        {
            ad.setMessage("Please input Username ");
            ad.show();
            txtUsername.requestFocus();
            return false;
        }
        // Check Password
        if(txtPassword.getText().length() == 0 || txtConPassword.getText().length() == 0 )
        {
            ad.setMessage("Please input Password/Confirm Password ");
            ad.show();
            txtPassword.requestFocus();
            return false;
        }
        // Check Password and Confirm Password (Match)
        if(!txtPassword.getText().toString().equals(txtConPassword.getText().toString()))
        {
            ad.setMessage("Password and Confirm Password Not Match! ");
            ad.show();
            txtConPassword.requestFocus();
            return false;
        }

        String url = "http://projectfinal.esy.es/ProjectApp/Login/save.php";

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sName", txtName.getText().toString()));
        params.add(new BasicNameValuePair("sUsername", txtUsername.getText().toString()));
        params.add(new BasicNameValuePair("sPassword", txtPassword.getText().toString()));

        /** Get result from Server (Return the JSON Code)
         * StatusID = ? [0=Failed,1=Complete]
         * Error	= ?	[On case error return custom error message]
         *
         * Eg Save Failed = {"StatusID":"0","Error":"Email Exists!"}
         * Eg Save Complete = {"StatusID":"1","Error":""}
         */

        String resultServer  = getHttpPost(url,params);

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
        if(strStatusID.equals("0"))
        {
            ad.setMessage(strError);
            ad.show();
        }
        else
        {
            Toast.makeText(OwnerRegister.this, "สมัครสมาชิกสำเร็จ", Toast.LENGTH_SHORT).show();
            txtUsername.setText("");
            txtPassword.setText("");
            txtConPassword.setText("");
            txtName.setText("");
        }


        return true;
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

    //ดักการกดปุ่ม back ก่อนออกจากโปรแกรม
    public void onBackPressed(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OwnerRegister.this);

        alertDialog.setTitle("Confirm Exit...");
        alertDialog.setMessage("คุณต้องการออกจากหน้านี้หรือไม่ ?");
        alertDialog.setIcon(R.drawable.wrench);

        alertDialog.setPositiveButton("ใช่",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        //คลิกใช่ ออกจากโปรแกรม
                        finish();
                        OwnerRegister.super.onBackPressed();
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
