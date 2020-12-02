package com.garage.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.garage.myapplication.SharedPreferences.NetConnect;
import com.garage.myapplication.SharedPreferences.SessionManager;
import com.garage.myapplication.SharedPreferences.UserHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class OwnerLogin extends Activity {
    private Button button1;
    private SessionManager session;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_login);

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Session manager
        session = new SessionManager(getApplicationContext());
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(OwnerLogin.this, Owner.class);
            startActivity(intent);
            finish();
        }
//        *** Session Login
        final UserHelper usrHelper = new UserHelper(this);

        button1 = (Button) findViewById(R.id.btnRegisterOwner);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                i = new Intent(getApplicationContext(),OwnerRegister.class);
                startActivity(i);
            }


        });


        final AlertDialog.Builder ad = new AlertDialog.Builder(this);

        // txtUsername & txtPassword
        final EditText txtUser = (EditText)findViewById(R.id.editOwnerUsername);
        final EditText txtPass = (EditText)findViewById(R.id.editOwnerpassword);

        // btnLogin
        final Button btnLogin = (Button) findViewById(R.id.btnOwnerlogin);
        // Perform action on click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "http://projectfinal.esy.es/ProjectApp/Login/check.php";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("strUser", txtUser.getText().toString()));
                params.add(new BasicNameValuePair("strPass", txtPass.getText().toString()));

                String resultServer  = NetConnect.getHttpPost(url,params);

                /*** Default Value ***/
                String strStatusID = "0";
                String strMemberID = "0";
                String strError = "Unknow Status!";

                JSONObject c;
                try {
                    c = new JSONObject(resultServer);
                    strStatusID = c.getString("StatusID");
                    strMemberID = c.getString("MemberID");
                    strError = c.getString("Error");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }



                // Prepare Login
                if(strStatusID.equals("0"))
                {
                    // Dialog
                    ad.setTitle("รหัสผู้ใช้ไม่ถูกต้อง ");
                    ad.setPositiveButton("Close", null);
                    ad.setMessage(strError);
                    ad.show();
                    txtUser.setText("");
                    txtPass.setText("");
                }
                else
                {
                    session.setLogin(true);
                    Toast.makeText(OwnerLogin.this, "Login OK", Toast.LENGTH_SHORT).show();

                    // Create Session
                    usrHelper.createSession(strMemberID);

//                    Intent newActivity = new Intent(OwnerLogin.this,Owner.class);
//                    newActivity.putExtra("GarageOwner_ID", strMemberID);
//                    startActivity(newActivity);

                    Intent newActivity = new Intent(OwnerLogin.this,Owner.class);
                    newActivity.putExtra("GarageOwner_ID", strMemberID);
                    startActivity(newActivity);
                    finish();
                }
            }
        });
    }
}
