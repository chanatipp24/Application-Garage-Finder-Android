package com.garage.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class Garage extends Activity  {

    ArrayList<HashMap<String, String>> MyArrList;
//
//    String[] Cmd = {"Update","Delete"};
    private String username;


    @SuppressLint("NewApi")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage);

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        ShowData();


//        // btnSearch
//        final Button btnSearch = (Button) findViewById(R.id.btnSearch);
//        //btnSearch.setBackgroundColor(Color.TRANSPARENT);
//        // Perform action on click
//        btnSearch.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                ShowData();
//            }
//        });


    }

    public void ShowData() {
        // listView1
        ListView lisView1 = (ListView)findViewById(R.id.listView1);

        // keySearch
//        EditText strKeySearch = (EditText)findViewById(R.id.txtKeySearch);
//
//        // Disbled Keyboard auto focus
//        InputMethodManager imm = (InputMethodManager)getSystemService(
//                Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(strKeySearch.getWindowToken(), 0);


        String url = "http://projectfinal.esy.es/ProjectApp/Garage/showGarage.php";

        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("txtKeyword", username.toString()));
        String resultServer  = getHttpPost(url,params);
        try {
            JSONArray data = new JSONArray(resultServer);


            MyArrList = new ArrayList<HashMap<String, String>>();
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

            lisView1.setAdapter(new ImageAdapter(this));

//            registerForContextMenu(lisView1);
//            SimpleAdapter sAdap;
//            sAdap = new SimpleAdapter(Garage.this, MyArrList, R.layout.activity_search_colum,
////                    new String[] {"NameGarage", "Provise"}, new int[] {R.id.TextGarageName, R.id.textProvinseGarage});
//            lisView1.setAdapter(sAdap);

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
                    String strProvise = MyArrList.get(position).get("Provise")
                            .toString();
                    String strType = MyArrList.get(position).get("Type")
                            .toString();
                    String strDetail = MyArrList.get(position).get("Detail")
                            .toString();

                    viewDetail.setTitle("ข้อมูลร้าน");
                    viewDetail.setMessage("ชื่อร้าน : " + strName + "\n"
                            + "ที่อยู่ : " + strAddress + "\n"
                            + "เบอร์โทร : " + strTelephone + "\n"
                            + "จังหวัด : " + strProvise + "\n"
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

//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//
//        menu.setHeaderTitle("แก้ไขข้อมูล");
//        String[] menuItems = Cmd;
//        for (int i = 0; i<menuItems.length; i++) {
//            menu.add(Menu.NONE, i, i, menuItems[i]);
//        }
//    }



//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
//        int menuItemIndex = item.getItemId();
//        String[] menuItems = Cmd;
//        String CmdName = menuItems[menuItemIndex];
//
//        if ("Update".equals(CmdName)) {
//            Toast.makeText(Garage.this,"Your Selected Update",Toast.LENGTH_LONG).show();
//
//            String sGarageID = MyArrList.get(info.position).get("Garage_ID").toString();
//
//            Intent newActivity = new Intent(Garage.this,EditGarage.class);
//            newActivity.putExtra("Garage_ID", sGarageID);
//            startActivity(newActivity);
//
//        } else if ("Delete".equals(CmdName)) {
//            Toast.makeText(Garage.this,"Your Selected Delete",Toast.LENGTH_LONG).show();
//            /**
//             * Call the mthod
//             */
//        }
//        return true;
//    }



    public class ImageAdapter extends BaseAdapter
    {
        private Context context;

        public ImageAdapter(Context c)
        {
            // TODO Auto-generated method stub
            context = c;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return MyArrList.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_column, null);
            }



            //imgCmdEdit
            ImageButton cmdEdit = (ImageButton) convertView.findViewById(R.id.btnEdit);
            cmdEdit.setBackgroundColor(Color.TRANSPARENT);
            cmdEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Garage.this,"Your Selected Update",Toast.LENGTH_LONG).show();

                    String sGarageID = MyArrList.get(position).get("Garage_ID").toString();

                    Intent newActivity = new Intent(Garage.this,EditGarage.class);
                    newActivity.putExtra("Garage_ID", sGarageID);
                    startActivity(newActivity);
                }
            });



            ImageButton cmdMap = (ImageButton) convertView.findViewById(R.id.btnMap);
            cmdMap.setBackgroundColor(Color.TRANSPARENT);
            cmdMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Garage.this,"Your Selected Map Edit",Toast.LENGTH_LONG).show();

                    String sGarageID = MyArrList.get(position).get("Garage_ID").toString();

                    Intent newActivity = new Intent(Garage.this,EditMaps.class);
                    newActivity.putExtra("Garage_ID", sGarageID);
                    startActivity(newActivity);
                }
            });





            // imgCmdDelete
            ImageButton cmdDelete = (ImageButton) convertView.findViewById(R.id.btnDelete);
            cmdDelete.setBackgroundColor(Color.TRANSPARENT);

            final AlertDialog.Builder adb1 = new AlertDialog.Builder(Garage.this);
            final AlertDialog.Builder adb2 = new AlertDialog.Builder(Garage.this);

            cmdDelete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    adb1.setTitle("Delete?");
                    adb1.setMessage("คุณแน่ใจที่จะลบข้อมูลของร้าน [" + MyArrList.get(position).get("NameGarage") +"]");
                    adb1.setNegativeButton("Cancel", null);
                    adb1.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            // Request to Delete data.
                            String url = "http://projectfinal.esy.es/ProjectApp/Garage/DeleteGarage.php";
                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair("sMemberID", MyArrList.get(position).get("Garage_ID")));

                            String resultServer  = getHttpPost(url,params);

                            /** Get result delete data from Server (Return the JSON Code)
                             * StatusID = ? [0=Failed,1=Complete]
                             * Error	= ?	[On case error return custom error message]
                             *
                             * Eg Login Failed = {"StatusID":"0","Error":"Cannot delete data!"}
                             * Eg Login Complete = {"StatusID":"1","Error":""}
                             */
                            String strStatusID = "0";
                            String strError = "Unknow Status";

                            try {
                                JSONObject c = new JSONObject(resultServer);
                                strStatusID = c.getString("StatusID");
                                strError = c.getString("Error");
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            // Prepare Delete
                            if(strStatusID.equals("0"))
                            {
                                // Dialog
                                adb2.setTitle("Error! ");
                                adb2.setPositiveButton("Close", null);
                                adb2.setMessage(strError);
                                adb2.show();
                            }
                            else
                            {
                                Toast.makeText(Garage.this, "Delete data successfully.", Toast.LENGTH_SHORT).show();
                                ShowData(); // reload data again
                            }

                        }});
                    adb1.show();
                }
            });

//            // R.id.TextGarageID
//            TextView txtMemberID = (TextView) convertView.findViewById(R.id.TextGarageID);
//            txtMemberID.setPadding(10, 0, 0, 0);
//            txtMemberID.setText(MyArrList.get(position).get("Garage_ID") +".");

            // R.id.TextGarageName
            TextView txtGarageName = (TextView) convertView.findViewById(R.id.TextGarageName);
            txtGarageName.setPadding(5, 0, 0, 0);
            txtGarageName.setText(MyArrList.get(position).get("NameGarage"));

            // R.id.TextOwnerName
            TextView txtOwner = (TextView) convertView.findViewById(R.id.TextOwnerName);
            txtOwner.setPadding(5, 0, 0, 0);
            txtOwner.setText(MyArrList.get(position).get("GarageOwner"));


            return convertView;

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
}
