package com.garage.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    private ImageView button1;
    private ImageView button2;
    private ImageView button3;
    private ImageView button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
         final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

// Search
        final Button btn1 = (Button) findViewById(R.id.searchID);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

//                View layout = inflater.inflate(R.layout.activity_search,
//                        (ViewGroup) findViewById(R.id.layout_popup));
//
//                popDialog.setTitle("ค้นหาร้านซ่อม ");
//                popDialog.setView(layout);
//                popDialog.setPositiveButton(android.R.string.ok, new
//                        DialogInterface.OnClickListener() {
//
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//
//                        });
//
//                popDialog.create();
//                popDialog.show();
                Intent i;
                i = new Intent(getApplicationContext(), Search.class);
                startActivity(i);
            }

        });

        button2 = (ImageView) findViewById(R.id.carID);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i;
                i = new Intent(getApplicationContext(), Car.class);
                startActivity(i);
            }
        });

        button1 = (ImageView) findViewById(R.id.neabyID);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i;
                i = new Intent(getApplicationContext(), Nearby.class);
                startActivity(i);
            }
        });


        button3 = (ImageView) findViewById(R.id.motoID);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                i = new Intent(getApplicationContext(), Motorcycle.class);
                startActivity(i);
            }


        });


        button4 = (ImageView) findViewById(R.id.toolsID);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                i = new Intent(getApplicationContext(), OwnerLogin.class);
                startActivity(i);
            }


        });

    }


        //ดักการกดปุ่ม back ก่อนออกจากโปรแกรม
        public void onBackPressed(){

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

            alertDialog.setTitle("Confirm Exit...");
            alertDialog.setMessage("คุณต้องการออกจากโปรแกรมหรือไม่ ?");
            alertDialog.setIcon(R.drawable.wrench);

            alertDialog.setPositiveButton("ใช่",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            //คลิกใช่ ออกจากโปรแกรม
                            finish();
                            MainActivity.super.onBackPressed();
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



