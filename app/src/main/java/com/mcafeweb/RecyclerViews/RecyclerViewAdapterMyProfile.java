package com.mcafeweb.RecyclerViews;


import android.support.v7.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcafeweb.Models.ProfileItemModel;
import com.mcafeweb.R;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import java.util.List;

/**
 * Created by Balpreet on 17-Mar-17.
 */

public class RecyclerViewAdapterMyProfile extends RecyclerView.Adapter<RecyclerViewAdapterMyProfile.MyViewHolder> {
    private List<ProfileItemModel> profileItemModelList;


    public RecyclerViewAdapterMyProfile(final List<ProfileItemModel> profileItemModelList) {
        this.profileItemModelList = profileItemModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_profile, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view, new CustomEditTextListener());
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.customEditTextListener.updatePosition(position);
        holder.floatLabeledEditText.setHint(profileItemModelList.get(position).getProfileItemName());
        holder.floatLabeledEditText.getEditText().setText(profileItemModelList.get(position).getProfileItemValue());
        if(holder.floatLabeledEditText.getEditText().getText().equals("member"))
        {
            Log.v("RecyclerProfile",holder.floatLabeledEditText.getHint().toString());
            holder.floatLabeledEditText.getEditText().setEnabled(false);
            holder.floatLabeledEditText.getEditText().setFocusable(false);
        }
    }

    @Override
    public int getItemCount() {
        return profileItemModelList == null ? 0 : profileItemModelList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private View view;

        public FloatLabeledEditText floatLabeledEditText;
        public CustomEditTextListener customEditTextListener;

        private MyViewHolder(View itemView, CustomEditTextListener customEditTextListener) {
            super(itemView);
            view = itemView;
            floatLabeledEditText = (FloatLabeledEditText) view.findViewById(R.id.profile_item_value);
            this.customEditTextListener = customEditTextListener;
            this.floatLabeledEditText.getEditText().addTextChangedListener(this.customEditTextListener);
        }
    }

    private class CustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            profileItemModelList.get(position).setProfileItemValue(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }
}
