package com.example.sanatkumarsaha.viscom2016;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class Registration extends AppCompatActivity implements LoaderCallbacks<Cursor>, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:password:example_foo:9933890398", "bar@example.com:password:example_bar:9999999999"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mCPasswordView;
    private EditText mMobileView;
    private EditText mAgeView;
    private Spinner mGenderView;
    private Spinner mBloodView;
    private TextView mSpinnerTextView;
    private TextView mSpinner2TextView;
    private EditText mNameView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox mClient;
    private CheckBox mServiceProvider;
    private LinearLayout optional;
    private EditText from, to;
    boolean flag2 = true;
    private VolleySingleton volleySingleton;
    int fromHour=0, toHour=0;

    String public_Key = "MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgHTHSaj/S4iuj6oGvUS4zVb++Qio\n" +
            "Nm4/kS+kSducJRbu4McJVPW2ERXyMMCioZhYfByylmv6sahiA8w1/TJtgW/0fgPX\n" +
            "WROngdhuci5ITl0LjHu4h+siiTwVjFidSqQm1g30xpdiNwh7GYIu/nw5TdunAtZU\n" +
            "yRzLGB4qmNsHH0tTAgMBAAE=" ;
    PublicKey publicKey;
    byte[] encodedBytes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        // Set up the login form.
        mClient = (CheckBox)findViewById(R.id.client);
        mServiceProvider = (CheckBox)findViewById(R.id.serviceProvider);
        optional = (LinearLayout)findViewById(R.id.optional);
        from = (EditText)findViewById(R.id.from);
        to = (EditText)findViewById(R.id.to);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        mEmailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mEmailView.setError(null);
                    final String email = mEmailView.getText().toString();
                    if (TextUtils.isEmpty(email)) {
                        mEmailView.setError(getString(R.string.error_field_required));
                    } else if (!isEmailValid(email)) {
                        mEmailView.setError(getString(R.string.error_invalid_email2));
                    }
                }
            }
        });

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mPasswordView.setError(null);
                    String password = mPasswordView.getText().toString();
                    if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
                        mPasswordView.setError(getString(R.string.error_invalid_password));
                    }
                }
            }
        });

        mCPasswordView = (EditText) findViewById(R.id.cpassword);
        mCPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mCPasswordView.setError(null);
                    String cpassword = mCPasswordView.getText().toString();
                    if (!isConfirmPasswordValid(mPasswordView.getText().toString(), cpassword)) {
                        mCPasswordView.setError(getString(R.string.error_invalid_cpassword));
                    }

                }
            }
        });

        mNameView = (EditText) findViewById(R.id.name);
        mNameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mNameView.setError(null);
                    String name = mNameView.getText().toString();
                    if (TextUtils.isEmpty(name)) {
                        mNameView.setError(getString(R.string.error_field_required));
                    } else if (!isNameValid(name)) {
                        mNameView.setError(getString(R.string.error_invalid_name));
                    }

                }
            }
        });

        mAgeView = (EditText) findViewById(R.id.age);
        mAgeView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mAgeView.setError(null);
                    String age = mAgeView.getText().toString();
                    if (TextUtils.isEmpty(age)) {
                        mAgeView.setError(getString(R.string.error_field_required));
                    }
                }
            }
        });

        from.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    from.setError(null);
                    String fromTime = from.getText().toString();
                    if (TextUtils.isEmpty(fromTime) && optional.getVisibility() == View.VISIBLE) {
                        from.setError(getString(R.string.error_field_required));
                    }
                }
            }
        });

        to.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    to.setError(null);
                    String toText = to.getText().toString();
                    if (TextUtils.isEmpty(toText) && optional.getVisibility() == View.VISIBLE) {
                        to.setError(getString(R.string.error_field_required));
                    }
                }
            }
        });

        mGenderView = (Spinner) findViewById(R.id.spinner);
        mBloodView = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.custom_list_item_2);
        adapter.setDropDownViewResource(R.layout.custom_list_item_2);
        mGenderView.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.blood_group, R.layout.custom_list_item_2);
        adapter.setDropDownViewResource(R.layout.custom_list_item_2);
        mBloodView.setAdapter(adapter2);

        mMobileView = (EditText) findViewById(R.id.mobile);
        mMobileView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.signup || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mMobileView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mMobileView.setError(null);
                    String mobile = mMobileView.getText().toString();
                    if (TextUtils.isEmpty(mobile)) {
                        mMobileView.setError(getString(R.string.error_field_required));
                    } else if (!isMobileValid(mobile)) {
                        mMobileView.setError(getString(R.string.error_invalid_mobile));
                    }
                }
            }
        });

        mClient = (CheckBox)findViewById(R.id.client);
        mServiceProvider = (CheckBox) findViewById(R.id.serviceProvider);

        Button mEmailSignInButton = (Button) findViewById(R.id.sign_up);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    //Autocomplete email attempt

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {


        //get selected textView from spinner
        mSpinnerTextView = (TextView) mGenderView.getSelectedView();
        mSpinner2TextView = (TextView) mBloodView.getSelectedView();


        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mCPasswordView.setError(null);
        mNameView.setError(null);
        mAgeView.setError(null);
        mMobileView.setError(null);
        mSpinnerTextView.setError(null);
        mSpinner2TextView.setError(null);
        from.setError(null);
        to.setError(null);



        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        String cpassword = mCPasswordView.getText().toString();
        final String mobile = mMobileView.getText().toString();
        final String name = mNameView.getText().toString();
        final String age = mAgeView.getText().toString();
        final String gender = mGenderView.getSelectedItem().toString();
        final String blood_group = mBloodView.getSelectedItem().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email2));
            focusView = mEmailView;
            cancel = true;
        }

        //check for a valid Name
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        } else if (!isNameValid(name)) {
            mNameView.setError(getString(R.string.error_invalid_name));
            focusView = mNameView;
            cancel = true;
        }

        if (!mClient.isChecked() && !mServiceProvider.isChecked()){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please Select at least one of the options : Client or Service Provider");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            cancel = true;
            builder.show();

        }

        //check for correct password confirmation

        if (!isConfirmPasswordValid(password, cpassword)) {
            mCPasswordView.setError(getString(R.string.error_invalid_cpassword));
            focusView = mCPasswordView;
            cancel = true;
        }

        //check for empty username or mobile


        //check for a valid age

        if (TextUtils.isEmpty(age)) {
            mAgeView.setError(getString(R.string.error_field_required));
            focusView = mAgeView;
            cancel = true;
        }

        if (TextUtils.isEmpty(from.getText().toString()) && optional.getVisibility() == View.VISIBLE) {
            from.setError(getString(R.string.error_field_required));
            focusView = from;
            cancel = true;
        }

        if (TextUtils.isEmpty(to.getText().toString()) && optional.getVisibility() == View.VISIBLE) {
            to.setError(getString(R.string.error_field_required));
            focusView = to;
            cancel = true;
        }
        if (fromHour-toHour > 0){
            from.setError("Invalid Input");
            to.setError("Invalid Input");
            focusView = from;
            cancel = true;
        }

        if (TextUtils.isEmpty(age)) {
            mAgeView.setError(getString(R.string.error_field_required));
            focusView = mAgeView;
            cancel = true;
        }

        //check for empty gender

        if (gender.equals("Select Gender")) {
            mSpinnerTextView.setTextColor(Color.RED);
            mSpinnerTextView.setError("");
            mSpinnerTextView.setText(getString(R.string.error_field_required));
            focusView = mSpinnerTextView;
            cancel = true;
        }
        if (blood_group.equals("Select Blood Group")) {
            mSpinner2TextView.setTextColor(Color.RED);
            mSpinner2TextView.setError("");
            mSpinner2TextView.setText(getString(R.string.error_field_required));
            focusView = mSpinner2TextView;
            cancel = true;
        }
        if (TextUtils.isEmpty(mobile)) {
            mMobileView.setError(getString(R.string.error_field_required));
            focusView = mMobileView;
            cancel = true;
        }
        else if (!isMobileValid(mobile)) {
            mMobileView.setError(getString(R.string.error_invalid_mobile));
            focusView = mMobileView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {


            showProgress(true);

            String url = null;


            try {
                Cipher c = Cipher.getInstance("RSA");
                c.init(Cipher.ENCRYPT_MODE, publicKey);
                 encodedBytes = c.doFinal(password.getBytes());
            } catch (Exception e) {
                Log.d("Ram","Wrong");
            }

                url ="http://cognitio.co.in/kgp/register.php";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        showProgress(false);

                        if (!response.contains("Duplicate Email")){
                            Toast.makeText(Registration.this, "Successfully Registered", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(Registration.this, LogIn.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }

                        else{
                            Toast.makeText(Registration.this, "The email is already registered", Toast.LENGTH_LONG).show();
                            mEmailView.setError(getString(R.string.error_email_exists));
                            mEmailView.requestFocus();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Toast.makeText(Registration.this,"Registration Failure",Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("name",name);
                        params.put("email",email);
                        params.put("age", age);
                        params.put("gender",gender);
                        params.put("password",new String(encodedBytes));
                        params.put("mobile",mobile);
                        params.put("bloodGroup",blood_group);
                        params.put("isClient", mClient.isChecked()+"");
                        params.put("isServiceProvider",mServiceProvider.isChecked()+"");
                        params.put("from",fromHour+"");
                        params.put("to",toHour+"");



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
    }

    private boolean isNameValid(String name) {
        return name.length() > 2;
    }

    /*private boolean isAgeValid(String age) {
        return Integer.parseInt(age) >= 1 && Integer.parseInt(age) <= 150;
    }*/

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private boolean isConfirmPasswordValid(String password, String cpassword) {
        return cpassword.equals(password);
    }

    private boolean isMobileValid(String mobile) {
        return mobile.length() == 10;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(Registration.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        mAgeView.setText(dayOfMonth+"-"+new DateFormatSymbols().getMonths()[monthOfYear]+"-"+year);
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


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main2, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public void pick(View v){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                Registration.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "DOB");
    }

    public void serviceProviderClicked(View v){

        if (mServiceProvider.isChecked()){

            optional.setVisibility(View.VISIBLE);

        }else {

            optional.setVisibility(View.GONE);
        }

    }

    public void pickFrom(View v){

        flag2 = true;

        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                Registration.this,
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
                Registration.this,
                now.get(Calendar.HOUR),
                now.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show(getFragmentManager(), "To");

    }


}

