package com.eathere.cc.eathere;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eathere.cc.eathere.model.AsyncNetUtils;
import com.eathere.cc.eathere.model.NetworkStatusUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    private Button signUp;
    private EditText fname;
    private EditText lname;
    private EditText username;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // display back arrow button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = (EditText) findViewById(R.id.activity_sign_up_input_username);
        email = (EditText) findViewById(R.id.activity_sign_up_input_email);
        password = (EditText) findViewById(R.id.activity_sign_up_input_password);

        signUp = (Button) findViewById(R.id.activity_sign_up_button_sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // Let back arrow button to go back to previous activity
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void signUp() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignUpFailed();
            return;
        }

        signUp.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this, R.style.MyMaterialTheme_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String usernameStr = username.getText().toString();
        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();

        // TODO: Implement your own signup logic here.

        if (NetworkStatusUtils.isNetworkConnected(this)) {
            AsyncNetUtils.post("http://cclb-635335002.us-east-1.elb.amazonaws.com:8080/api/users/register", "username="+usernameStr+"&email="+emailStr+"&password="+passwordStr, new AsyncNetUtils.Callback() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("status") == true) {
                            JSONObject userProfile = jsonResponse.getJSONObject("user_profile");
                            String uid = userProfile.getString("uid");
                            String username = userProfile.getString("username");
                            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("uid", uid);
                            editor.putString("username", username);
                            editor.commit();
                            onSignUpSuccess();
                        } else {
                            onSignUpFailed();
                        }
                    } catch (JSONException e) {
                        onSignUpFailed();
                    }
                }
            });
        } else {
            progressDialog.dismiss();
            onSignUpFailedNoInternet();
        }
    }

    public void onSignUpSuccess() {
        signUp.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignUpFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();
        signUp.setEnabled(true);
    }

    public void onSignUpFailedNoInternet() {
        Toast.makeText(getBaseContext(), "No Internet", Toast.LENGTH_LONG).show();
        signUp.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String usernameStr = username.getText().toString();
        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();

        if (usernameStr.isEmpty() || usernameStr.length() < 3) {
            username.setError("at least 3 characters");
            valid = false;
        } else {
            username.setError(null);
        }

        if (emailStr.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            email.setError("enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }

        if (passwordStr.isEmpty() || passwordStr.length() < 4 || passwordStr.length() > 10) {
            password.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            password.setError(null);
        }
        return valid;
    }

}
