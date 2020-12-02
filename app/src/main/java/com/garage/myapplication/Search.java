package com.garage.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

public class Search extends Activity {

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final Button btn1 = (Button) findViewById(R.id.button1);
        // Perform action on click
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SearchData();
            }
        });

        ImageButton cmdMap = (ImageButton) findViewById(R.id.btnMap);
        cmdMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newActivity = new Intent(Search.this,SearchMap.class);
                startActivity(newActivity);
            }
        });

    }

    public void SearchData()
    {
        // listView1
        final ListView lisView1 = (ListView)findViewById(R.id.listViewM);

        // editText1
        final EditText inputText = (EditText)findViewById(R.id.editsearch);

        String url = "http://projectfinal.esy.es/ProjectApp/Search/Seacrh.php";

        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("txtKeyword", inputText.getText().toString()));

        try {
            JSONArray data = new JSONArray(getJSONUrl(url,params));

            final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;

            for(int i = 0; i < data.length(); i++){
                JSONObject c = data.getJSONObject(i);

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
                MyArrList.add(map);

            }


            SimpleAdapter sAdap;
            sAdap = new SimpleAdapter(Search.this, MyArrList, R.layout.activity_search_colum,
                    new String[] {"NameGarage", "Address", "Telephone", "Type", "Detail"}, new int[] {R.id.NameList, R.id.AddressList,R.id.TelephoneList,R.id.TypeList,R.id.DetailList});
            lisView1.setAdapter(sAdap);

            final AlertDialog.Builder viewDetail = new AlertDialog.Builder(this);
            // OnClick Item
            lisView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> myAdapter, View myView,
                                        int position, long mylng) {

                    String strName = MyArrList.get(position).get("NameGarage")
                            .toString();
                    String strAddress = MyArrList.get(position).get("Address")
                            .toString();
                    String strTelephone = MyArrList.get(position).get("Telephone")
                            .toString();
                    String strType = MyArrList.get(position).get("Type")
                            .toString();
                    String strDetail = MyArrList.get(position).get("Detail")
                            .toString();


                    viewDetail.setTitle("ข้อมูลร้าน");
                    viewDetail.setMessage("ชื่อร้าน : " + strName + "\n"
                            + "ที่อยู่ : " + strAddress + "\n"
                            + "เบอร์โทร : " + strTelephone + "\n"
                            + "ประเภทร้านซ่อม : " + strType + "\n"
                            + "รายละเอียดร้าน : " + strDetail);
                    viewDetail.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    dialog.dismiss();
                                }
                            });
                    viewDetail.show();


                }
            });


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public String getJSONUrl(String url,List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download file..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }
}
