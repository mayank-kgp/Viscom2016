package com.example.sanatkumarsaha.viscom2016;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Availability extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private EditText from, to;
    boolean flag2 = true;
    int fromHour = -1, toHour = -1;
    VolleySingleton volleySingleton;
    SharedPreferences sp;
    RelativeLayout bg;
    ProgressBar mProgressView;

    String public_Key = "MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgHTHSaj/S4iuj6oGvUS4zVb++Qio\n" +
            "Nm4/kS+kSducJRbu4McJVPW2ERXyMMCioZhYfByylmv6sahiA8w1/TJtgW/0fgPX\n" +
            "WROngdhuci5ITl0LjHu4h+siiTwVjFidSqQm1g30xpdiNwh7GYIu/nw5TdunAtZU\n" +
            "yRzLGB4qmNsHH0tTAgMBAAE=";
    PublicKey publicKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressView = (ProgressBar)findViewById(R.id.login_progress);
        bg = (RelativeLayout)findViewById(R.id.bg);

        try {
            byte[] encodedKey = Base64.decode(public_Key.getBytes("utf-8"), Base64.DEFAULT);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedKey);
            KeyFactory keyFactory = null;

            keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(spec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        from = (EditText)findViewById(R.id.from);
        to = (EditText)findViewById(R.id.to);
        volleySingleton = VolleySingleton.getInstance();
        sp = getSharedPreferences("Check", Context.MODE_PRIVATE);

        int fromIntHour,fromIntMinute, toIntHour,toIntMinute;
        fromIntHour = Integer.parseInt(sp.getString("from","0")) / 100 ;
        fromIntMinute = Integer.parseInt(sp.getString("from","0")) % 100 ;
        toIntHour = Integer.parseInt(sp.getString("to","0")) / 100 ;
        toIntMinute = Integer.parseInt(sp.getString("to","0")) % 100 ;

        if (fromIntHour<12)
            from.setText(fromIntHour+":"+ (fromIntMinute>=10 ? fromIntMinute : "0"+fromIntMinute) +""+" AM");
        else from.setText(Integer.toString(fromIntHour-12)+":"+ (fromIntMinute>=10 ? fromIntMinute : "0"+fromIntMinute) +""+" PM");


        if (toIntHour<12)
            to.setText(toIntHour+":"+ (toIntMinute>=10 ? toIntMinute : toIntMinute+"0") +""+" AM");
        else to.setText(Integer.toString(toIntHour-12)+":"+ (toIntMinute>=10 ? toIntMinute : toIntMinute+"0") +""+" PM");



    }

    public void pickFrom(View v){

        flag2 = true;

        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                Availability.this,
                now.get(Calendar.HOUR),
                now.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show(getFragmentManager(), "From");

    }

    public void pickTo(View v){

        flag2 = false;

        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                Availability.this,
                now.get(Calendar.HOUR),
                now.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show(getFragmentManager(), "To");

    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

        if (flag2){

            fromHour = hourOfDay*100 + minute;

            if (hourOfDay<12)
                from.setText(hourOfDay+":"+ (minute>=10 ? minute : "0"+minute) +""+" AM");
            else from.setText(Integer.toString(hourOfDay-12)+":"+ (minute>=10 ? minute : "0"+minute) +""+" PM");

        } else {

            toHour = hourOfDay*100 + minute;

            if (hourOfDay<12)
                to.setText(hourOfDay+":"+ (minute>=10 ? minute : minute+"0") +""+" AM");
            else to.setText(Integer.toString(hourOfDay-12)+":"+ (minute>=10 ? minute : minute+"0") +""+" PM");

        }

    }

    public void submit(View v){

        String url = "http://cognitio.co.in/kgp/changetim.php";
        showProgress(true);
        bg.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(Availability.this,response,Toast.LENGTH_LONG).show();


                if (response.equals("Success")){
                    showProgress(false);
                    bg.setVisibility(View.GONE);

                    String str1, str2;

                    str1 = sp.getString("from",""); str2 = sp.getString("to","");

                    SharedPreferences.Editor editor = sp.edit();
                    if (fromHour != -1)
                        editor.putString("from",fromHour+"");
                    else editor.putString("from",str1);

                    if (toHour != -1)
                        editor.putString("to",toHour+"");
                    else editor.putString("to",str2);
                    editor.apply();

                 //   Toast.makeText(Availability.this,"Data Saved Successfully",Toast.LENGTH_LONG).show();

                } else {

                    showProgress(false);
                    bg.setVisibility(View.GONE);

                //    Toast.makeText(Availability.this,"Request Error",Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                showProgress(false);
                bg.setVisibility(View.GONE);
                Toast.makeText(Availability.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",sp.getString("email",""));
                params.put("password",sp.getString("password",""));

                if (fromHour != -1)
                    params.put("from",fromHour+"");
                else params.put("from",sp.getString("from",""));

                if (toHour != -1)
                    params.put("to",toHour+"");
                else params.put("to",sp.getString("toHour",""));

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
        onBackPressed();
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
