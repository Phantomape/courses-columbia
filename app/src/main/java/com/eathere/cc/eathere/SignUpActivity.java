package com.eathere.cc.eathere;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    private Button signIn;
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

        fname = (EditText) findViewById(R.id.activity_sign_up_input_fname);
        lname = (EditText) findViewById(R.id.activity_sign_up_input_lname);
        username = (EditText) findViewById(R.id.activity_sign_up_input_username);
        email = (EditText) findViewById(R.id.activity_sign_up_input_email);
        password = (EditText) findViewById(R.id.activity_sign_up_input_password);

        signUp = (Button) findViewById(R.id.activity_sign_up_button_sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
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

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignUpFailed();
            return;
        }

        signUp.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this, android.R.style.Theme_Material_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String fnameStr = fname.getText().toString();
        String lnameStr = lname.getText().toString();
        String usernameStr = username.getText().toString();
        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();

        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignUpSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void onSignUpSuccess() {
        signUp.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignUpFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        signUp.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String fnameStr = fname.getText().toString();
        String lnameStr = lname.getText().toString();
        String usernameStr = username.getText().toString();
        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();

        if (fnameStr.isEmpty() || fnameStr.length() < 3) {
            fname.setError("at least 3 characters");
            valid = false;
        } else {
            fname.setError(null);
        }

        if (lnameStr.isEmpty() || lnameStr.length() < 3) {
            lname.setError("at least 3 characters");
            valid = false;
        } else {
            lname.setError(null);
        }

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
