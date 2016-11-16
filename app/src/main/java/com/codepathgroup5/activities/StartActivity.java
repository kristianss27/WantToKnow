package com.codepathgroup5.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.codepathgroup5.parse.UserParse;
import com.codepathgroup5.wanttoknow.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etPassword) EditText etPassword;

    private UserParse userParse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        userParse = new UserParse();
    }

    public void logIn(View view) {
        if(etEmail.getText()!=null && etPassword!=null){
            String userName = etEmail.getText().toString().toLowerCase();
            String email = userName;
            String password = etPassword.getText().toString().toLowerCase();

            ParseUser.logInInBackground(userName,password,new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        //Connection successful
                        Log.d(TAG,"Connection successful");
                        Intent intent = new Intent(StartActivity.this, SearchActivity.class);
                        startActivity(intent);
                    } else {
                        // Getting error message from the exception
                        String errorMessage = e.getMessage();
                        Log.d(TAG,"ERROR"+e.getMessage());
                        Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
        else{
            Toast.makeText(this,"Warning: "+R.string.warning_login,Toast.LENGTH_LONG).show();
        }

    }


    public void signUp(View view) {

        if(etEmail.getText().toString()!=null && etPassword.getText().toString()!=null){
            String userName = etEmail.getText().toString().toLowerCase();
            String email = userName;
            String password = etPassword.getText().toString().toLowerCase();

            //Initializes the ParseUser Object using its properties
            ParseUser user = new ParseUser();
            //User name is required
            user.setUsername(userName);
            //Password is required on signup
            user.setPassword(password);
            //The emaill user is optional
            user.setEmail(email);

            //other fields can be set just like with ParseObject
            user.put("phone", "123");

            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        //Connection successful
                        Log.d(TAG,"Connection successful");
                        Intent intent = new Intent(StartActivity.this, SearchActivity.class);
                        startActivity(intent);
                    } else {
                        // Getting error message from the exception
                        String errorMessage = e.getMessage();
                        Log.d(TAG,"ERROR"+e.getMessage());
                        Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(this,R.string.warning_signup,Toast.LENGTH_LONG).show();
        }

    }
}
