package com.example.sanatkumarsaha.viscom2016;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sp;
    boolean flag2 = true;
    ImageView imageView;
    static final int REQUEST_IMAGE_CAPTURE =1;
    static final int REQUEST_SELECT_FILE =2;
    RelativeLayout custom;
    RelativeLayout floating, categories, providerLayout;
    LinearLayout main;
    DiscreteSeekBar stress, urgency;
    EditText message, location;
    int type = 0, b=0;
    String webPage="", data = "" ;
    VolleySingleton volleySingleton;
    CheckBox medical, security, emotion, education;
    String categoryParams = "";

    String public_Key = "MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgHTHSaj/S4iuj6oGvUS4zVb++Qio\n" +
            "Nm4/kS+kSducJRbu4McJVPW2ERXyMMCioZhYfByylmv6sahiA8w1/TJtgW/0fgPX\n" +
            "WROngdhuci5ITl0LjHu4h+siiTwVjFidSqQm1g30xpdiNwh7GYIu/nw5TdunAtZU\n" +
            "yRzLGB4qmNsHH0tTAgMBAAE=" ;
    PublicKey publicKey;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        floating = (RelativeLayout)findViewById(R.id.floating);
        categories = (RelativeLayout)findViewById(R.id.categories);
        main = (LinearLayout)findViewById(R.id.main);
        stress = (DiscreteSeekBar)findViewById(R.id.stress);
        urgency = (DiscreteSeekBar)findViewById(R.id.urgency);
        floating.setVisibility(View.INVISIBLE);
        message = (EditText)findViewById(R.id.message);
        location = (EditText)findViewById(R.id.location);
        medical = (CheckBox)findViewById(R.id.medicalBox);
        security = (CheckBox)findViewById(R.id.securityBox);
        emotion = (CheckBox)findViewById(R.id.emotionBox);
        education = (CheckBox)findViewById(R.id.educationBox);
        providerLayout = (RelativeLayout)findViewById(R.id.providerLayout);


        volleySingleton = VolleySingleton.getInstance();

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


        sp = getSharedPreferences("Check", Context.MODE_PRIVATE);

        if (sp.getBoolean("isProvider",false)){

            categories.setVisibility(View.VISIBLE);

            if (!sp.getBoolean("isClient",false))
            main.setVisibility(View.GONE);

        }else {
            categories.setVisibility(View.INVISIBLE);
            floating.animate().translationY(floating.getHeight());
        }

        if (sp.getString("category","").contains("Medical"))
            medical.setChecked(true);
        if (sp.getString("category","").contains("Security"))
            security.setChecked(true);
        if (sp.getString("category","").contains("Emotion"))
            emotion.setChecked(true);
        if (sp.getString("category","").contains("Education"))
            education.setChecked(true);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (!sp.getBoolean("isProvider",false))
            navigationView.inflateMenu(R.menu.menu_provider);
        else navigationView.inflateMenu(R.menu.menu_client);
        navigationView.setNavigationItemSelectedListener(this);

        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.nav_header_main, navigationView);
        //Accessing the nav_header_main layout
        custom = (RelativeLayout) findViewById(R.id.custom);

        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        type=4;
                        floating.setVisibility(View.VISIBLE);
                        location.setError(null);

                        floating.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                main.setEnabled(false);
                            }
                        });
                    }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (categories.getVisibility()==View.VISIBLE)
                closeCategories(categories);
        else if (floating.getVisibility()==View.VISIBLE)
            close(floating);

            else
                super.onBackPressed();
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        moveDown();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.logout) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("LogInStat",false);
            editor.putString("uri", "");
            editor.commit();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            } else {
                Intent i = new Intent(this,LogIn.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }

        }
        if (id == R.id.timing) {

            Intent i = new Intent(this,Availability.class);
            startActivity(i);

        }
        if (id == R.id.categories) {
            if (sp.getString("category","").contains("Medical"))
                medical.setChecked(true);
            if (sp.getString("category","").contains("Security"))
                security.setChecked(true);
            if (sp.getString("category","").contains("Emotion"))
                emotion.setChecked(true);
            if (sp.getString("category","").contains("Education"))
                education.setChecked(true);

            categories.setVisibility(View.VISIBLE);
            location.setError(null);

            categories.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    main.setEnabled(false);
                }
            });

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void close(View v){
        floating.animate().translationY(floating.getHeight()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                floating.setVisibility(View.INVISIBLE);
                main.setEnabled(true);
                flag2=false;
                location.setError(null);
            }
        });
    }
    public void closeCategories(View v){

        if (!medical.isChecked() && !security.isChecked() && !emotion.isChecked() && !education.isChecked()){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please Select at least one of the categories");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            return;
        }

        categories.animate().translationY(categories.getHeight()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                categories.setVisibility(View.INVISIBLE);
                main.setEnabled(true);
                flag2=false;
            }
        });
    }


    public void moveDown(){
        floating.animate().translationY(floating.getHeight());
    }

    public void submit(View v){

        if (location.getText().toString().equals("")){
            location.setError("Field Required");
            return;
        }

        String  url = null;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            if (response.equals("Success")){

                Toast.makeText(MainActivity.this,"Request Sent Successfully",Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(MainActivity.this,"Request Error",Toast.LENGTH_LONG).show();

            }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Failure",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name",sp.getString("name",""));
                params.put("email",sp.getString("email",""));
                params.put("type", type+"");
                params.put("stress",stress.getProgress()+"");
                params.put("password",sp.getString("password",""));
                params.put("urgency",urgency.getProgress()+"");
                params.put("message",message.getText().toString());
                params.put("location", location.getText().toString());
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


        close(floating);

    }

    public void medicalClick(View v){

        type=1;

        floating.setVisibility(View.VISIBLE);
        location.setError(null);

        floating.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                main.setEnabled(false);
            }
        });

    }

    public void securityClick(View v){

        type=2;

        floating.setVisibility(View.VISIBLE);
        location.setError(null);

        floating.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                main.setEnabled(false);
            }
        });
    }

    public void emotionClick(View v){

        type=3;

        floating.setVisibility(View.VISIBLE);
        location.setError(null);

        floating.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                main.setEnabled(false);
            }
        });

    }

    public  void  submitCategory(View v){

        if (!medical.isChecked() && !security.isChecked() && !emotion.isChecked() && !education.isChecked()){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please Select at least one of the categories");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            return;
        }


        if (medical.isChecked()) categoryParams += "Medical ";
        if (security.isChecked()) categoryParams += "Security ";
        if (emotion.isChecked()) categoryParams += "Emotion ";
        if (education.isChecked()) categoryParams += "Education ";


        String  url = null;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("Success")){

                    Toast.makeText(MainActivity.this,"Data Saved Successfully",Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("category",categoryParams);
                    editor.apply();

                } else {

                    Toast.makeText(MainActivity.this,"Request Error",Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Failure",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",sp.getString("email",""));
                params.put("password",sp.getString("password",""));
                params.put("category", categoryParams);
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

        floating.animate().translationY(floating.getHeight()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                floating.setVisibility(View.INVISIBLE);
                main.setEnabled(true);
                flag2=false;
                location.setError(null);
            }
        });

    }

}
