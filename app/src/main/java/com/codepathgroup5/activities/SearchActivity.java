package com.codepathgroup5.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.codepathgroup5.wanttoknow.R;
import com.parse.ParseUser;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void logOut(View view) {
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser==null){
            Intent intent = new Intent(SearchActivity.this,StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }
}
