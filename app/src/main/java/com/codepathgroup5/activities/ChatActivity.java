package com.codepathgroup5.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.codepathgroup5.adapters.ChatListAdapter;
import com.codepathgroup5.models.ChatMessagePO;
import com.codepathgroup5.wanttoknow.R;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    static final String TAG = ChatActivity.class.getSimpleName();
    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";

    EditText etMessage;
    Button btSend;
    ListView lvChat;
    ArrayList<ChatMessagePO> mMessages;
    ChatListAdapter mAdapter;
    String idRequest;

    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;


    static final int POLL_INTERVAL = 1000; // milliseconds
    Handler mHandler = new Handler();  // android.os.Handler
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            refreshMessage();
            mHandler.postDelayed(this, POLL_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        idRequest = getIntent().getStringExtra("id_request");

        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();

        } else { // If not logged in, login as a new anonymous user
            //ParseUser.logInInBackground("kristianss27@gmail.com","1234",new LogInCallback() {
            ParseUser.logInInBackground("bootie-sf-san-francisco","bootie-sf-san-francisco",new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        //Connection successful
                        Log.d("Test Login","Connection successful");
                        startWithCurrentUser();
                    } else {
                        // Getting error message from the exception
                        String errorMessage = e.getMessage();
                        Log.d("Test Login","ERROR"+e.getMessage());
                    }
                }
            });
            //login();

        }
        mHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
    }



    // Get the userId from the cached currentUser object
    void startWithCurrentUser() {
        setupMessagePosting();
    }

    // Setup button event handler which posts the entered message to Parse

    void setupMessagePosting() {
        // Find the text field and button
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (Button) findViewById(R.id.btSend);
        lvChat = (ListView) findViewById(R.id.lvChat);
        mMessages = new ArrayList<>();
        // Automatically scroll to the bottom when a data set change notification is received and only if the last item
        // is already visible on screen.
        // Don't scroll to the bottom otherwise.
        lvChat.setTranscriptMode(1);
        mFirstLoad = true;
        final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new ChatListAdapter(ChatActivity.this, userId, mMessages);
        lvChat.setAdapter(mAdapter);
        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                ChatMessagePO message =  new ChatMessagePO();
                message.setBody(data);
                message.setUserId(ParseUser.getCurrentUser().getObjectId());
                message.put("id_request",idRequest);

                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            Toast.makeText(ChatActivity.this, "Successfully created message on Parse",
                                    Toast.LENGTH_SHORT).show();
                            refreshMessage();
                        } else {
                            Log.e(TAG, "Failed to save message", e);
                        }
                    }
                });
                etMessage.setText(null);
            }
        });
    }

    private void refreshMessage() {
        // Construct query to execute
        ParseQuery<ChatMessagePO> query = ParseQuery.getQuery(ChatMessagePO.class);

        // Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.orderByDescending("createdAt");
        query.whereEqualTo("id_request",idRequest);

        // Execute query to fetch all messages from Parse asynchronously
        query.findInBackground(new FindCallback<ChatMessagePO>() {
            public void done(List<ChatMessagePO> messages, ParseException e) {
                if (e == null) {
                    if(mMessages!=null && mMessages.size()>0) {
                        mMessages.clear();
                    }
                    Collections.reverse(messages);
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        lvChat.setSelection(mAdapter.getCount() - 1);
                        mFirstLoad = false;
                    }

                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }

            }

        });

    }


    // Create an anonymous user using ParseAnonymousUtils and set sUserId

    void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Anonymous login failed: ", e);
                } else {
                    startWithCurrentUser();
                }
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            mHandler.removeCallbacks(mRefreshMessagesRunnable);
        }
        return super.onKeyDown(keyCode, event);
    }

}