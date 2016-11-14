package com.codepathgroup5.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.codepathgroup5.parse.UserParse;
import com.codepathgroup5.wanttoknow.R;

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

            userParse.setUserName(userName);
            userParse.setEmail(email);
            userParse.setPassword(password);
            userParse.setPhone("");

            String response[] = userParse.logIn();
            if(response[1]!=null){
                Toast.makeText(this,response[0]+": "+response[1],Toast.LENGTH_LONG).show();
            }
            else {
                Intent intent = new Intent(StartActivity.this, SearchActivity.class);
                startActivity(intent);
            }
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
            userParse.setUserName(userName);
            userParse.setEmail(email);
            userParse.setPassword(password);
            userParse.setPhone("");


            String response[] = userParse.signUp();
            if(response[1]!=null){
                Toast.makeText(this,response[0]+": "+response[1],Toast.LENGTH_LONG).show();
            }
            else {
                Intent intent = new Intent(StartActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        }
        else{
            Toast.makeText(this,"Warning: "+R.string.warning_signup,Toast.LENGTH_LONG).show();
        }

    }
}
