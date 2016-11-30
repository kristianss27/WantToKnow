package com.codepathgroup5.wanttoknow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepathgroup5.activities.RequestActivity;
import com.codepathgroup5.activities.StartActivity;
import com.codepathgroup5.fragments.QuestionaryDialog;
import com.codepathgroup5.models.FbFriend;
import com.codepathgroup5.parse.UserParse;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends FragmentActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    LoginButton facebookLoginButton;
    TextView tvSignIn;
    FacebookClient facebookClient;

    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.btnLogin) Button loginButton;
    private UserParse userParse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(ParseUser.getCurrentUser()!=null){
            Intent intent = new Intent(MainActivity.this, RequestActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        userParse = new UserParse();

        facebookClient = FacebookClient.newInstance();
        facebookClient.initializeFacebookClient();

        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        tvSignIn = (TextView) findViewById(R.id.tvSignIn);

        LoginManager.getInstance().registerCallback(facebookClient.getCallBackManager(), new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook Login","Result: "+loginResult.getRecentlyGrantedPermissions());
                //Setting up the access token right now
                facebookClient.setAccessToken(loginResult.getAccessToken());
                tvSignIn.setText(R.string.com_facebook_smart_login_confirmation_continue_as);

                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                Log.d("Facebook Login","Cancel");
                Toast.makeText(getApplicationContext(),"You have cancelled the facebook permission",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("Facebook Login","Error: "+exception.toString());
                Toast.makeText(getApplicationContext(),"Facebook connection failed",Toast.LENGTH_LONG).show();
            }
        });

        setUpFacebookLoginButton();
        setUpSignIn();

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
                        //Save the current Installation to Parse.
                        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
                        parseInstallation.put("channel",ParseUser.getCurrentUser().getObjectId());
                        parseInstallation.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    //Connection successful
                                    Log.d("ParseInstallation","Parse Installation successful");
                                    Intent intent = new Intent(MainActivity.this, RequestActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    // Getting error message from the exception
                                    String errorMessage = e.getMessage();
                                    Log.d("ParseInstallation","ERROR"+e.getMessage());
                                }
                            }
                        });

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


    public void setUpFacebookLoginButton() {
        facebookLoginButton.setReadPermissions("public_profile","email","user_friends");

        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback registration
                facebookLoginButton.registerCallback(facebookClient.getCallBackManager(), new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG,"Facebook login success");
                        GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                                loginResult.getAccessToken()
                                ,"/me/friends",
                                null,
                                HttpMethod.GET,
                                new GraphRequest.Callback() {
                                    public void onCompleted(GraphResponse response) {
                                        try {
                                            Log.d(TAG,"response: "+response.toString());
                                            JSONArray jsonArray = response.getJSONObject().getJSONArray("data");
                                            Log.d(TAG,"jsonArray: "+jsonArray.toString());
                                            GsonBuilder gsonBuilder = new GsonBuilder();
                                            Type fbFriendType = new TypeToken<List<FbFriend>>(){}.getType();
                                            Gson gson = gsonBuilder.create();
                                            //In this case we need to get a Tweet List
                                            List<FbFriend> fbFriendList = gson.fromJson(jsonArray.toString(),fbFriendType);
                                            facebookClient.setFbFriendList(fbFriendList);
                                            Intent intent = new Intent(MainActivity.this, StartActivity.class);
                                            startActivity(intent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        ).executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG,"Facebook login cancelled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG,"Facebook login generate an error");
                    }
                });
            }

        });
    }

    public void setUpSignIn(){

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile","email","user_friends"));
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Facebook Login","Results: "+requestCode+","+resultCode+","+data);
        facebookClient.getCallBackManager().onActivityResult(requestCode, resultCode, data);
    }

    public void start(View view) {
        //Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        //startActivity(intent);
        FragmentManager fragmentManager = getSupportFragmentManager();
        QuestionaryDialog questionaryDialog = new QuestionaryDialog();
        questionaryDialog.show(fragmentManager,"frament_edit_name");
    }

}
