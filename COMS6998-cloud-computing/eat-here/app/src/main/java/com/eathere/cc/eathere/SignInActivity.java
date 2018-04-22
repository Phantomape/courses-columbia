package com.eathere.cc.eathere;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eathere.cc.eathere.model.AsyncNetUtils;
import com.eathere.cc.eathere.model.NetworkStatusUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
    private static final int REQUEST_SIGNUP = 0;

    private Button signIn;
    private TextView signUpPrompt;
    private TextView signUpLink;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // display back arrow button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = (EditText) findViewById(R.id.activity_sign_in_input_email);
        password = (EditText) findViewById(R.id.activity_sign_in_input_password);

        signIn = (Button) findViewById(R.id.activity_sign_in_button_sign_in);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        signUpLink = (TextView) findViewById(R.id.activity_sign_in_text_view_account_creation);
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        signUpPrompt = (TextView) findViewById(R.id.activity_sign_in_text_view_account_creation_prompt);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                finish();
            }
        }
    }

    public void signIn() {
        Log.d(TAG, "SignIn");

        if (!validate()) {
            onSignInFailed();
            return;
        }

        signIn.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this, R.style.MyMaterialTheme_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();

        if (NetworkStatusUtils.isNetworkConnected(this)) {
            AsyncNetUtils.post("http://cclb-635335002.us-east-1.elb.amazonaws.com:8080/api/users/login", "email="+emailStr+"&password="+passwordStr, new AsyncNetUtils.Callback() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    if (response != null) { // response code 200
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
                                onSignInSuccess();
                            } else {
                                Log.e(TAG, "Signin failed on false status");
                                onSignInFailed();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Signin failed on JSON problem");
                            onSignInFailed();
                        }
                    } else {
                        Log.e(TAG, "Signin failed on non-200 response");
                        onSignInFailed();
                    }
                }
            });
        } else {
            progressDialog.dismiss();
            onSignInFailedNoInternet();
        }
    }

    public boolean validate() {
        boolean valid = true;

        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();

        if (emailStr.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            email.setError("enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }

        if (passwordStr.isEmpty() || passwordStr.length() < 4 || passwordStr.length() > 30) {
            password.setError("between 4 and 30 alphanumeric characters");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    public void onSignInSuccess() {
        signIn.setEnabled(true);
        finish();
    }

    public void onSignInFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        signIn.setEnabled(true);
    }

    public void onSignInFailedNoInternet() {
        Toast.makeText(getBaseContext(), "No Internet", Toast.LENGTH_LONG).show();
        signIn.setEnabled(true);
    }

}
