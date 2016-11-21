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
import com.codepathgroup5.adapters.BusinessCardAdapter;
import com.codepathgroup5.adapters.ItemTouchHelperAdapter;
import com.codepathgroup5.adapters.ItemTouchHelperCallback;
import com.codepathgroup5.wanttoknow.R;
import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PersonalListFragment extends Fragment implements BusinessCardAdapter.AdapterListener,ItemTouchHelperAdapter {
    private static final String TAG = PersonalListFragment.class.getName();
    private PersonalListActivity listener;
    private Context context;
    private List<Business> listBusinesses;
    private FloatingActionButton btnRequest;
    private RecyclerView rvBusinesses;
    private BusinessCardAdapter adapter;



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
        View v = inflater.inflate(R.layout.fragment_personal_list,parent,false);
        return v;
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //We get all the values sended from the SearchActivity
        listBusinesses = (ArrayList<Business>) listener.getIntent().getSerializableExtra("list");
        btnRequest = (FloatingActionButton) view.findViewById(R.id.btnRequest);
        //Find the Recycler view
        rvBusinesses = (RecyclerView) view.findViewById(R.id.rvBusinesses);
        rvBusinesses.setHasFixedSize(true);

        //We create a LinearLayoutManager and associate this to our RecylerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvBusinesses.setLayoutManager(linearLayoutManager);

        //Create an extra list to use it as a parameter in the adapter constructor
        List<Business> listAux = new ArrayList<Business>();
        listAux.addAll(listBusinesses);
        adapter = new BusinessCardAdapter(context,listAux,getFragmentManager(),this,this);

        rvBusinesses.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvBusinesses);

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                QuestionaryDialog questionaryDialog = QuestionaryDialog.newInstance();
                // SETS the target fragment for use later when sending results
                questionaryDialog.setTargetFragment(PersonalListFragment.this, 300);
                questionaryDialog.show(fm,"Fragment layout");
            }
        });

    }

    @Override
    public void addToPersonalList(Business business) {

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(listBusinesses, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(listBusinesses, i, i - 1);
            }
        }
        adapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position, int direction) {
        listBusinesses.remove(position);
        adapter.notifyItemRemoved(position);
        Log.d(TAG,"DIRECTION: "+direction);
    }
}
