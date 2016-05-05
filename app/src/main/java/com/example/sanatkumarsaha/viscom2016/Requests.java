package com.example.sanatkumarsaha.viscom2016;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class Requests extends AppCompatActivity {

    VolleySingleton volleySingleton;
    SharedPreferences sp;
    JSONObject object;
    LinearLayout requests;
    StringRequest stringRequest;
    ProgressBar mProgressView;
    RelativeLayout bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        volleySingleton = VolleySingleton.getInstance();
        sp = getSharedPreferences("Check", Context.MODE_PRIVATE);
        requests = (LinearLayout)findViewById(R.id.requests);
        bg = (RelativeLayout)findViewById(R.id.bg);
        mProgressView = (ProgressBar)findViewById(R.id.login_progress);

        Bundle bundle = getIntent().getExtras();
        try {
            object = new JSONObject(bundle.getString("name"));


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(object.getString("message"));
        builder.setMessage("Name : " +object.getString("name")+"\nMobile No : "+object.getString("mobile_no")+"\nStress : "+object.getString("stress")+"/10\nUrgency : "+object.getString("urgency")+"/10\nMessage : "+object.getString("message")+"\nLocation : " +object.getString("location") +"\nTime : "+ object.getString("time") );
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                showProgress(true);
                bg.setVisibility(View.GONE);

                String url = "http://cognitio.co.in/kgp/backnoti.php";

                stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        showProgress(false);
                        bg.setVisibility(View.VISIBLE);


                        if (response.contains("Success") && response.contains("yes") ){

                            Toast.makeText(Requests.this,"Success ",Toast.LENGTH_LONG).show();

                        } else if(response.contains("no")) {

                            Toast.makeText(Requests.this,"The request is already accepted",Toast.LENGTH_LONG).show();

                        } else {

                        }

                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        showProgress(false);
                        bg.setVisibility(View.VISIBLE);
                        Toast.makeText(Requests.this,"Failure",Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
                        try {
                            params.put("email",object.getString("email"));
                            params.put("password",object.getString("password"));

                            JSONObject jsonObject = new JSONObject();

                            jsonObject.put("name",sp.getString("name",""));
                            jsonObject.put("mobile_no",sp.getString("mobile_no",""));

                            params.put("message",jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("Content-Type","application/x-www-form-urlencoded");
                        return params;
                    }
                };

                volleySingleton.getmRequestQueue().add(stringRequest);

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
            textView.setText("Name : " +object.getString("name")+"\nMobile No : "+object.getString("mobile_no")+"\nStress : "+object.getString("stress")+"/10\nUrgency : "+object.getString("urgency")+"/10\nMessage : "+object.getString("message")+"\nLocation : " +object.getString("location") +"\nTime : "+ object.getString("time"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        requests.addView(textView);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }


}
