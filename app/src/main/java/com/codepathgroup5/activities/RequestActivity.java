package com.codepathgroup5.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepathgroup5.adapters.RequestAdapter;
import com.codepathgroup5.models.MessagePO;
import com.codepathgroup5.utilities.DividerItemDecorator;
import com.codepathgroup5.utilities.NetworkUtil;
import com.codepathgroup5.wanttoknow.MainActivity;
import com.codepathgroup5.wanttoknow.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.LinkedList;
import java.util.List;

public class RequestActivity extends AppCompatActivity implements RequestAdapter.AdapterListener {
    private static final String TAG = "RequestActivity";

    private SwipeRefreshLayout swipeContainer;
    private FloatingActionButton btnSearch;
    private RequestAdapter adapter;
    private LinkedList<MessagePO> listRequest;
    private RecyclerView rvRequest;
    RecyclerView.LayoutManager layoutManager;
    private final int REQUEST_CODE = 200;
    private NetworkUtil networkUtil;
    private Context context;
    private
    long maxId = 0;
    long sinceId = 0;
    boolean refresh = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        //Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Questions");
        setSupportActionBar(toolbar);
        //Declare de list
        listRequest = new LinkedList<MessagePO>();
        //This class allows us to check the internet conection and connecting to the db
        networkUtil = new NetworkUtil();
        btnSearch = (FloatingActionButton) findViewById(R.id.btnSearch);

        setUpActivity();
    }

    // METHODS to set up the tool bar and its menu
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                logOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void logOut(){
        ParseUser.logOut();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void setUpActivity() {
        //Find the Recycler view
        rvRequest = (RecyclerView) findViewById(R.id.rvRequest);
        //We create our linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //Set the Layout Manager in the Recycler View
        rvRequest.setLayoutManager(linearLayoutManager);
        //Creation of ItemDecoration to draw the division line between the tweets
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecorator(this, DividerItemDecorator.VERTICAL_LIST);
        rvRequest.addItemDecoration(itemDecoration);
        //OnScrollListener to the RecyclerView
        /*rvRequest.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d("LISTENER", "page onScroll: " + page);
                Log.d("LISTENER", "totalItemsCount: " + totalItemsCount);
                Log.d("LISTENER", "getItemAcount: " + adapter.getItemCount());
                if (networkUtil.connectionPermitted(context)) {
                    populateRecyclerView();
                } else {

                }

            }
        });*/
        // Create and Set up the adapter into de RecyclerView
        adapter = new RequestAdapter(listRequest, this);
        rvRequest.setAdapter(adapter);
        //SwipeRefresh Layout necesary to refresh the Recycler View with new tweets
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        configTheSwipeRefreshLayout();

        //Check if there is connection
        if (networkUtil.connectionPermitted(this)) {
            populateRecyclerView();
        } else {
            Toast.makeText(this, "You are offline!", Toast.LENGTH_LONG).show();
            /*networkUtil.openDataBase(this);
            Query<TweetEntity> query = networkUtil.getTweetDao().queryBuilder().build();
            listTweetsDb = query.list();
            createList(listTweetsDb);
            adapter.notifyItemRangeInserted(adapter.getItemCount(),listTweets.size()-1);
            networkUtil.closeDataBase();*/
        }

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    //Method to set up our Swipe Refresh Layout
    public void configTheSwipeRefreshLayout() {
        if (networkUtil.connectionPermitted(this)) {
            swipeContainer.setEnabled(true);
            //Getting the SwipeRefreshLayou
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    refresh = true;
                    populateRecyclerView();
                }

            });

            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        } else {
            Toast.makeText(this, "Connection Failed", Toast.LENGTH_LONG).show();
            swipeContainer.setEnabled(false);
        }
    }


    private void populateRecyclerView() {

        //this condition allows us to know if We have network connection.
        // Werther or not We manage what to do in any case
        if (networkUtil.connectionPermitted(this)) {
            // Construct query to execute
            if(ParseUser.getCurrentUser().get("type")!=null && ParseUser.getCurrentUser().get("type").toString().equalsIgnoreCase("yelp_business")){
                ParseQuery<ParseObject> businessRequestQuery = ParseQuery.getQuery("BusinessRequestPO");
                businessRequestQuery.whereEqualTo("business_name",ParseUser.getCurrentUser().getString("username"));
                businessRequestQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> listObjects, ParseException e) {
                        if(listObjects!=null && listObjects.size()>0){
                            for (ParseObject object : listObjects) {
                                Log.d(TAG,"request_id:"+object.getString("request_id"));
                                String requestId = object.getString("request_id");
                                //Get Message Request
                                ParseQuery<MessagePO> query = ParseQuery.getQuery(MessagePO.class);
                                query.orderByDescending("createdAt");
                                query.whereContains("objectId", requestId);
                                //Execute query
                                query.findInBackground(new FindCallback<MessagePO>() {
                                    public void done(List<MessagePO> listObject, ParseException e) {
                                        if (e == null) {
                                            List<MessagePO> newArray = listObject;
                                            final int size = newArray.size();
                                            Log.d("LIST SIZE", " List size start in:" + size);

                                            if (refresh) {
                                                refresh = false;
                                                if (size > 0) {
                                                    listRequest.clear();
                                                    listRequest.addAll(newArray);
                                                    adapter.notifyDataSetChanged();
                                                }
                                                swipeContainer.setRefreshing(refresh);

                                            } else {
                                                refresh = false;
                                                listRequest.addAll(newArray);
                                                adapter.notifyDataSetChanged();
                                                swipeContainer.setRefreshing(refresh);
                                            }

                                        } else {
                                            Log.e("message", "Error Loading Messages" + e);
                                        }

                                    }

                                });
                            }
                        }
                        else{

                        }
                    }
                });

            }
            else {
                ParseQuery<MessagePO> query = ParseQuery.getQuery(MessagePO.class);
                query.orderByDescending("createdAt");
                query.whereEqualTo("owner", ParseUser.getCurrentUser());
                //Execute query
                query.findInBackground(new FindCallback<MessagePO>() {
                    public void done(List<MessagePO> listObject, ParseException e) {
                        if (e == null) {
                            List<MessagePO> newArray = listObject;
                            final int size = newArray.size();
                            Log.d("LIST SIZE", " List size start in:" + size);

                            if (refresh) {
                                refresh = false;
                                if (size > 0) {
                                    listRequest.clear();
                                    listRequest.addAll(newArray);
                                    adapter.notifyDataSetChanged();
                                }
                                swipeContainer.setRefreshing(refresh);

                            } else {
                                refresh = false;
                                listRequest.addAll(newArray);
                                adapter.notifyDataSetChanged();
                                swipeContainer.setRefreshing(refresh);
                            }

                        } else {
                            Log.e("message", "Error Loading Messages" + e);
                        }

                    }

                });
            }
        } else {
            Toast.makeText(this, "The device has not connection", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void goToChat(String idRequest) {

    }
}
