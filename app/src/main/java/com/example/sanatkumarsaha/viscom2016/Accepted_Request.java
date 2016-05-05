package com.example.sanatkumarsaha.viscom2016;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Accepted_Request extends AppCompatActivity {

    RelativeLayout accepted;
    VolleySingleton volleySingleton;
    SharedPreferences sp;
    JSONObject object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted__request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        accepted = (RelativeLayout)findViewById(R.id.accepted);

        volleySingleton = VolleySingleton.getInstance();
        sp = getSharedPreferences("Check", Context.MODE_PRIVATE);

        Bundle bundle = getIntent().getExtras();
        try {
            object = new JSONObject(bundle.getString("name"));


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Message Accepted");
            builder.setMessage("Name : " +object.getString("name")+"\nMobile No : "+object.getString("mobile_no"));
            builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {


                }
            }).setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            builder.show();




        } catch (JSONException e) {

            // Toast.makeText(Requests.this,"Laure Lage",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

         TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        try {
            textView.setText("Name : " +object.getString("name")+"\nMobile No : "+object.getString("mobile_no"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        accepted.addView(textView);




    }

}
