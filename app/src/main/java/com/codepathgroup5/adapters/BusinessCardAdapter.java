package com.codepathgroup5.adapters;

//import com.yelp.clientlib.entities.Business;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepathgroup5.activities.DetailActivity;
import com.codepathgroup5.wanttoknow.R;
import com.squareup.picasso.Picasso;
import com.yelp.clientlib.entities.Business;

import java.util.List;


public class BusinessCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String LOG_TAG = BusinessCardAdapter.class.getSimpleName();
    private List<Business> businessList;
    private AdapterListener adapterListener;
    private FragmentManager fragmentManager;
    private Fragment fragment;
    private Context context;


    //Type of objects we might use
    private final int VIEWTYPE1 = 0, VIEWTYPE2 = 1;


    public interface AdapterListener{
        void addToPersonalList(Business business);
    }

    //Constructor
    public BusinessCardAdapter(List<Business> businessList,AdapterListener adapterListener) {
        this.businessList = businessList;
        this.adapterListener = adapterListener;
    }
    //Constructor2
    public BusinessCardAdapter(Context context, List<Business> businessList, FragmentManager fragmentManager, Fragment fragment, AdapterListener adapterListener) {
        this.context = context;
        this.businessList = businessList;
        this.adapterListener = adapterListener;
        this.fragmentManager = fragmentManager;
        this.fragment = fragment;
    }


    @Override
    public int getItemCount() {
        return businessList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return businessList.get(position)==null?VIEWTYPE1:VIEWTYPE2;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Business business = businessList.get(position);

        switch (viewHolder.getItemViewType()) {
            case VIEWTYPE1:
                LoadingViewHolder vh1 = (LoadingViewHolder) viewHolder;
                configureViewHolder1(vh1, position);
                break;
            case VIEWTYPE2:
                BusinessViewHolder vh2 = (BusinessViewHolder) viewHolder;
                configureViewHolder2(vh2, position);
                break;
            default:
                LoadingViewHolder vh3 = (LoadingViewHolder) viewHolder;
                configureViewHolder1(vh3, position);
                break;
        }


    }

    private void configureViewHolder1(LoadingViewHolder loadingViewHolder, int position) {
        loadingViewHolder.progressBar.setIndeterminate(true);
    }

    private void configureViewHolder2(BusinessViewHolder businessViewHolder, int position) {
        final Business business = businessList.get(position);
        businessViewHolder.vName.setText(business.name());
        businessViewHolder.vPhone.setText(business.phone());
        if(business.location().address()!=null && business.location().address().size()>0) {
            businessViewHolder.vAddress.setText(business.location().address().get(0));
        }
        else{
            businessViewHolder.vAddress.setText(R.string.no_address);
        }

        Picasso.with(businessViewHolder.vImage.getContext())
                .load(business.imageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .resize(150, 150)
                .centerCrop()
                .into(businessViewHolder.vImage);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEWTYPE1:
                View v1 = inflater.inflate(R.layout.footer_progress, parent, false);
                viewHolder = new LoadingViewHolder(v1);
                break;
            case VIEWTYPE2:
                View v2 = inflater.inflate(R.layout.business_card, parent, false);
                viewHolder = new BusinessViewHolder(v2);
                break;
            default:
                View v3 = inflater.inflate(R.layout.footer_progress, parent, false);
                viewHolder = new LoadingViewHolder(v3);
                break;
        }

        return viewHolder;
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pbFooterLoading);
        }
    }

    public class BusinessViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        protected ImageView vImage;
        protected TextView vName;
        protected TextView vPhone;
        protected TextView vAddress;

        public BusinessViewHolder(View v) {
            super(v);
            vName =  (TextView) v.findViewById(R.id.business_name);
            vPhone = (TextView)  v.findViewById(R.id.business_phone);
            vAddress = (TextView)  v.findViewById(R.id.business_addr);
            vImage = (ImageView) v.findViewById(R.id.business_thumb);
            //v.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(LOG_TAG, "onClick " + getAdapterPosition());
            Intent intent = new Intent(view.getContext(), DetailActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, businessList.get(getAdapterPosition()));
            view.getContext().startActivity(intent);
        }
    }
}

