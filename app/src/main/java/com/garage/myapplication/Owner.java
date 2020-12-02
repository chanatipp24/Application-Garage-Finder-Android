package com.garage.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.garage.myapplication.SharedPreferences.NetConnect;
import com.garage.myapplication.SharedPreferences.SessionManager;
import com.garage.myapplication.SharedPreferences.UserHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class Owner extends Activity {
    private Button button1,button2;
    TextView tUsername;
    String strMemberID = "";
    private Button btnLogout;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        btnLogout = (Button) findViewById(R.id.btnLogout);
        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        //*** Get Session Login
        final UserHelper usrHelper = new UserHelper(this);

        //*** Get Login Status
        if(!usrHelper.getLoginStatus())
        {
            Intent newActivity = new Intent(Owner.this, OwnerLogin.class);
            startActivity(newActivity);
        }

        //*** Get Member ID from Session
        strMemberID = usrHelper.getMemberID();

        showInfo();


        button1 = (Button) findViewById(R.id.btnAdd);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                i = new Intent(getApplicationContext(),RegisterGarage.class);
                i.putExtra("username", tUsername.getText().toString());
                startActivity(i);
            }


        });

        // btnBack
//        final Button btnBack = (Button) findViewById(R.id.btnLogout);
        // Perform action on click
        btnLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//
                logoutUser();
            }
        });

        button2 = (Button) findViewById(R.id.btnEditGarage);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                i = new Intent(getApplicationContext(),Garage.class);
                i.putExtra("username", tUsername.getText().toString());
                startActivity(i);
            }


        });

    }


    private void showInfo() {
        // txtMemberID,txtMemberID,txtUsername,txtPassword,txtName,txtEmail,txtTel
//        final TextView tMemberID = (TextView)findViewById(R.id.txtMemberID);
        final TextView tName = (TextView)findViewById(R.id.txtName);
        tUsername = (TextView)findViewById(R.id.txtUsername);
//        final TextView tUsername = (TextView)findViewById(R.id.txtUsername);
//        final TextView tPassword = (TextView)findViewById(R.id.txtPassword);


        String url = "http://projectfinal.esy.es/ProjectApp/Login/getID.php";

//        Intent intent= getIntent();
//        final String MemberID = intent.getStringExtra("GarageOwner_ID");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sMemberID", strMemberID));

        /** Get result from Server (Return the JSON Code)
         *
         * {"MemberID":"2","Username":"adisorn","Password":"adisorn@2","Name":"Adisorn Bunsong","Tel":"021978032","Email":"adisorn@thaicreate.com"}
         */

        String resultServer  = NetConnect.getHttpPost(url,params);

//        String strMemberID = "";
        String strName = "";
        String strUsername = "";
//        String strPassword = "";

        JSONObject c;
        try {
            c = new JSONObject(resultServer);
            strMemberID = c.getString("GarageOwner_ID");
            strName = c.getString("NameGarageOwner");
            strUsername = c.getString("Username");
//            strPassword = c.getString("Password");


            if(!strMemberID.equals(""))
            {
//                tMemberID.setText(strMemberID);
                tName.setText(strName);
                tUsername.setText(strUsername);
//                tPassword.setText(strPassword);
            }
            else
            {
//                tMemberID.setText("-");
                tName.setText("-");
                tUsername.setText("-");
//                tPassword.setText("-");
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void logoutUser() {
        session.setLogin(false);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Owner.this);

                alertDialog.setTitle("Confirm Logout...");
                alertDialog.setMessage("คุณต้องการออกจากระบบหรือไม่ ?");

                alertDialog.setPositiveButton("ใช่",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                //คลิกใช่ ออกจากโปรแกรม
                                session.delSession();
                                // Launching the login activity
                                Intent intent = new Intent(Owner.this, MainActivity.class);
                                startActivity(intent);
                                finish();
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
