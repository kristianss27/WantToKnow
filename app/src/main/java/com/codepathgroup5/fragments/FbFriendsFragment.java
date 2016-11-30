package com.codepathgroup5.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepathgroup5.activities.PersonalListActivity;
import com.codepathgroup5.adapters.FbFriendsAdapter;
import com.codepathgroup5.adapters.ItemTouchHelperAdapter;
import com.codepathgroup5.adapters.ItemTouchHelperCallback;
import com.codepathgroup5.models.FbFriend;
import com.codepathgroup5.wanttoknow.FacebookClient;
import com.codepathgroup5.wanttoknow.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FbFriendsFragment extends Fragment implements FbFriendsAdapter.AdapterListener,ItemTouchHelperAdapter {
    private static final String TAG = FbFriendsFragment.class.getName();
    private Context context;
    private PersonalListActivity listener;
    private List<FbFriend> listFriends;
    private FloatingActionButton btnRequest;
    private RecyclerView rvFbFriends;
    private FbFriendsAdapter adapter;
    private FacebookClient facebookClient;



    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof PersonalListActivity){
            this.listener = (PersonalListActivity) context;
        }
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fbfriends_list,parent,false);
        return v;
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //Facebook client
        facebookClient = FacebookClient.facebookClient;

        btnRequest = (FloatingActionButton) view.findViewById(R.id.btnRequest);
        //Find the Recycler view
        rvFbFriends = (RecyclerView) view.findViewById(R.id.rvFbFriends);
        rvFbFriends.setHasFixedSize(true);

        //We create a LinearLayoutManager and associate this to our RecylerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvFbFriends.setLayoutManager(linearLayoutManager);

        if(facebookClient!=null) {
            listFriends = facebookClient.getFbFriendList();
        }

        //Create an extra list to use it as a parameter in the adapter constructor
        List<FbFriend> listAux = new ArrayList<FbFriend>();
        if(listFriends!=null && listFriends.size()>0) {
            listAux.addAll(listFriends);
        }
        adapter = new FbFriendsAdapter(context,listAux,getFragmentManager(),this,this);

        rvFbFriends.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvFbFriends);

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                QuestionaryDialog questionaryDialog = QuestionaryDialog.newInstance(null);
                // SETS the target fragment for use later when sending results
                questionaryDialog.setTargetFragment(FbFriendsFragment.this, 300);
                questionaryDialog.show(fm,"Fragment layout");
            }
        });

    }

    @Override
    public void addToPersonalList(FbFriend friend) {

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(listFriends, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(listFriends, i, i - 1);
            }
        }
        adapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position, int direction) {
        listFriends.remove(position);
        adapter.notifyItemRemoved(position);
        Log.d(TAG,"DIRECTION: "+direction);
    }
}
