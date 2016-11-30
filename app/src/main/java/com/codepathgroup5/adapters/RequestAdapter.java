package com.codepathgroup5.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepathgroup5.activities.ChatActivity;
import com.codepathgroup5.models.MessagePO;
import com.codepathgroup5.wanttoknow.R;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by kristianss27
 */
public class RequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = RequestAdapter.class.getSimpleName();
    private List<MessagePO> requestList;
    private RequestAdapter.AdapterListener adapterListener;
    private Context context;


    //Type of objects we might use
    private final int VIEWTYPE1 = 0, VIEWTYPE2 = 1;


    public interface AdapterListener {
        void goToChat(String idRequest);
    }

    //Constructor
    public RequestAdapter(List<MessagePO> requestList, RequestAdapter.AdapterListener adapterListener) {
        this.requestList = requestList;
        this.adapterListener = adapterListener;
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return requestList.get(position) == null ? VIEWTYPE1 : VIEWTYPE2;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        ParseObject parseObject = requestList.get(position);

        switch (viewHolder.getItemViewType()) {
            case VIEWTYPE1:
                LoadingViewHolder vh1 = (LoadingViewHolder) viewHolder;
                configureViewHolder1(vh1, position);
                break;
            case VIEWTYPE2:
                RequestViewHolder vh2 = (RequestViewHolder) viewHolder;
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

    private void configureViewHolder2(RequestViewHolder requestViewHolder, int position) {
        MessagePO parseObject = requestList.get(position);
        requestViewHolder.tvNumber.setText(String.valueOf(++position));
        requestViewHolder.tvRequestDescripcion.setText(parseObject.getString("description"));
        requestViewHolder.tvEmail.setText(parseObject.getContactEmail());

        if(parseObject.getPermitCalls()){
            requestViewHolder.tvPhone.setText(parseObject.getContactPhone());
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEWTYPE1:
                View v1 = inflater.inflate(R.layout.footer_progress, parent, false);
                viewHolder = new RequestAdapter.LoadingViewHolder(v1);
                break;
            case VIEWTYPE2:
                View v2 = inflater.inflate(R.layout.request_item, parent, false);
                viewHolder = new RequestAdapter.RequestViewHolder(v2);
                break;
            default:
                View v3 = inflater.inflate(R.layout.footer_progress, parent, false);
                viewHolder = new RequestAdapter.LoadingViewHolder(v3);
                break;
        }

        return viewHolder;
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pbFooterLoading);
        }
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView tvNumber;
        protected TextView tvRequestDescripcion;
        protected TextView tvEmail;
        protected TextView tvPhone;
        protected TextView tvPermitCalls;

        public RequestViewHolder(View v) {
            super(v);
            tvNumber = (TextView) v.findViewById(R.id.tvNumber);
            tvRequestDescripcion = (TextView) v.findViewById(R.id.tvRequestDescription);
            tvEmail = (TextView) v.findViewById(R.id.tvEmail);
            tvPhone = (TextView) v.findViewById(R.id.tvPhone);
            tvPermitCalls = (TextView) v.findViewById(R.id.tvCall);
            //v.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                MessagePO messagePO = requestList.get(position);
                // We can access the data within the views
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("id_request", messagePO.getObjectId());
                v.getContext().startActivity(intent);
            }
        }
    }
}