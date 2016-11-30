package com.codepathgroup5.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepathgroup5.listeners.ChildFragmentListener;
import com.codepathgroup5.models.Message;
import com.codepathgroup5.wanttoknow.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GeneralQuestionFragment extends Fragment{
    private static final String TAG = GeneralQuestionFragment.class.getName();
    @BindView(R.id.etDescription) EditText etDescription;
    @BindView(R.id.tvCharacterCounter) TextView tvCharacterCounter;
    @BindView(R.id.btnNext) Button btnNext;
    @BindView(R.id.tvLabel) TextView tvLabel;
    private Unbinder unbinder;
    private String title;
    private int page;
    private Message message;

    // newInstance constructor for creating fragment with arguments
    public static GeneralQuestionFragment newInstance(int page, String title) {
        GeneralQuestionFragment generalQuestionFragment = new GeneralQuestionFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        generalQuestionFragment.setArguments(args);
        return generalQuestionFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        message = new Message();
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        unbinder = ButterKnife.bind(this,view);

        TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel);
        tvLabel.setText(getPage() + " -- " + getTitle());

        return view;
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnNext.setEnabled(false);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"On Click");
                ChildFragmentListener listener = (ChildFragmentListener) getParentFragment();

                //Getting the value from the EditText
                String description = etDescription.getText().toString();
                if(description!=null && !description.equalsIgnoreCase("")){
                    message.setDescription(description);
                }

                listener.nextQuestion(GeneralQuestionFragment.this,message);
            }
        });

        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // this will show characters remaining
                int counter = 140 - s.toString().length();
                tvCharacterCounter.setText(Integer.toString(counter));
                if (counter < 0) {
                    tvCharacterCounter.setTextColor(Color.RED);
                    btnNext.setEnabled(false);
                }
                else {
                    btnNext.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

}
