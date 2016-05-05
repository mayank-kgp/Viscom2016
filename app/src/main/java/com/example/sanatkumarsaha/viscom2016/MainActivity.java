package com.example.sanatkumarsaha.viscom2016;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sp;
    boolean flag2 = true;
    ImageView imageView;
    static final int REQUEST_IMAGE_CAPTURE =1;
    static final int REQUEST_SELECT_FILE =2;
    RelativeLayout medicalLayout,securityLayout,emotionLayout,custom,bg;
    RelativeLayout floating, categories, providerLayout;
    LinearLayout main;
    DiscreteSeekBar stress, urgency;
    EditText message, location;
    int type = 0, b=0;
    String webPage="", data = "" ;
    VolleySingleton volleySingleton;
    CheckBox medical, security, emotion, education;
    String categoryParams = "";
    String emergency_type = null;
    ProgressBar mProgressView;

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

        mProgressView = (ProgressBar)findViewById(R.id.login_progress);
        bg = (RelativeLayout)findViewById(R.id.bg);


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

        medicalLayout = (RelativeLayout)findViewById(R.id.medical);
        securityLayout = (RelativeLayout)findViewById(R.id.security);
        emotionLayout = (RelativeLayout)findViewById(R.id.emotion);
        custom = (RelativeLayout)findViewById(R.id.custom);


        volleySingleton = VolleySingleton.getInstance();



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                medicalLayout.setBackground(getResources().getDrawable(R.drawable.ripple_effect_red));
                securityLayout.setBackground(getResources().getDrawable(R.drawable.ripple_effect));
                emotionLayout.setBackground(getResources().getDrawable(R.drawable.ripple_effect_blue));
                custom.setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));

            }

        else {

                medicalLayout.setBackgroundColor(Color.rgb(198,40,40));
                securityLayout.setBackgroundColor(Color.rgb(46,125,50));
                emotionLayout.setBackgroundColor(Color.rgb(63,81,255));
                custom.setBackgroundColor(Color.rgb(33,33,33));

        }



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
        if (sp.getBoolean("isProvider",false))
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
                        emergency_type = "Education";
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
            showProgress(true);
            bg.setVisibility(View.VISIBLE);

            String url = "http://cognitio.co.in/kgp/logout.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                   showProgress(false);
                    bg.setVisibility(View.GONE);

                    Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();

                    if (response.equals("Success")){

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("LogInStat",false);
                        editor.putString("uri", "");
                        editor.commit();
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                            finishAffinity();
//                        } else {
                            Intent i = new Intent(MainActivity.this,LogIn.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
//                        }

                    }
                    else {
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    bg.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this,"Logout Failure",Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("email",sp.getString("email",""));
                    params.put("password",sp.getString("password",""));
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
            builder.show();
            return;
        } else


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

//        this.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
//        this.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));


        if (location.getText().toString().equals("")){
            location.setError("Field Required");
            return;
        }

        showProgress(true);
        bg.setVisibility(View.VISIBLE);

        String  url = "http://cognitio.co.in/kgp/gcm2.php?push=true";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showProgress(false);
                bg.setVisibility(View.GONE);


            if (response.equals("Success")){

                Toast.makeText(MainActivity.this,"Request Sent Successfully",Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(MainActivity.this,"Request Error",Toast.LENGTH_SHORT).show();

            }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                showProgress(false);
                bg.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                JSONObject object = new JSONObject();

                try {
                    object.put("name",sp.getString("name",""));
                    object.put("mobile_no",sp.getString("mobile_no",""));
                    object.put("stress",stress.getProgress()+"");
                    object.put("urgency",urgency.getProgress()+"");
                    object.put("message",message.getText().toString());
                    object.put("location",location.getText().toString());
                    object.put("email",sp.getString("email",""));
                    object.put("password",sp.getString("password",""));
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());
                    object.put("time",formattedDate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                params.put("email",sp.getString("email",""));
                params.put("category", emergency_type);
                params.put("password",sp.getString("password",""));
                params.put("message",object.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        volleySingleton.getmRequestQueue().add(stringRequest);


        close(floating);

    }

    public void medicalClick(View v){

        type=1;
        emergency_type = "Medical";

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
        emergency_type = "Security";

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
        emergency_type = "Emotion";

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

        categoryParams = "";

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
            builder.show();
            return;
        }


        if (medical.isChecked()) categoryParams += "Medical ";
        if (security.isChecked()) categoryParams += "Security ";
        if (emotion.isChecked()) categoryParams += "Emotion ";
        if (education.isChecked()) categoryParams += "Education ";


        showProgress(true);
        bg.setVisibility(View.VISIBLE);


        String  url = "http://cognitio.co.in/kgp/updatecat.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (response.equals("Success")){


                    showProgress(false);
                    bg.setVisibility(View.GONE);

                    Toast.makeText(MainActivity.this,"Data Saved Successfully",Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("category",categoryParams);
                    editor.apply();

                } else {

                    showProgress(false);
                    bg.setVisibility(View.GONE);

                    Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                showProgress(false);
                bg.setVisibility(View.GONE);
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

        categories.animate().translationY(categories.getHeight()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                categories.setVisibility(View.INVISIBLE);
                main.setEnabled(true);
                flag2=false;
                location.setError(null);
            }
        });

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
