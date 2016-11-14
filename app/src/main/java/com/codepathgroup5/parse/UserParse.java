package com.codepathgroup5.parse;

import android.util.Log;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class UserParse {
    private String userName;
    private String email;
    private String password;
    private String phone;


    public UserParse(){
        super();
    }

    public UserParse(String userName, String email, String password, String phone) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    //This method allows sign up a new user on the app
    public String[] signUp(){
        //Declare a variable to get the response
        final String[] result = new String[2];
        //Initializes the ParseUser Object using its properties
        ParseUser user = new ParseUser();
        //User name is required
        user.setUsername(getEmail());
        //Password is required on signup
        user.setPassword(getPassword());
        //The emaill user is optional
        user.setEmail(getEmail());

        //other fields can be set just like with ParseObject
        user.put("phone", getPhone());

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    result[0] = "SUCCESS";
                    Log.d("Sign Up",result[0]);
                } else {
                    // Sign up didn't succeed. We use the ParseException to figure out what went wrong
                    //Pass as first value
                    result[0] = "ERROR";
                    result[1] = e.getMessage();
                    Log.d("Sign Up",result[0]+result[1]);
                }
            }
        });
        return result;
    }

    public ParseUser getCurrentUser(){
        return ParseUser.getCurrentUser();
    }

    public void logOut(){
        ParseUser.logOut();
    }

    public String[] logIn(){
        final String[] result = new String[2];
        ParseUser.logInInBackground(getUserName(),getPassword(),new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    result[0] = "SUCCESS";
                    Log.d("Log In",result[0]);
                } else {
                    // Sign up didn't succeed. We use the ParseException to figure out what went wrong
                    //Pass as first value
                    result[0] = "ERROR";
                    result[1] = e.getMessage();
                    Log.d("Log In",result[0]+result[1]);
                }
            }
        });
        return result;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
