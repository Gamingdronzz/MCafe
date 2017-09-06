package com.mcafeweb.RecyclerViews;


import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcafeweb.Models.ProfileItemModel;
import com.mcafeweb.R;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import java.util.List;

/**
 * Created by Balpreet on 17-Mar-17.
 */

public class RecyclerViewAdapterMemberProfile extends RecyclerView.Adapter<RecyclerViewAdapterMemberProfile.MyViewHolder> {
    private List<ProfileItemModel> profileItemModelList;


    public RecyclerViewAdapterMemberProfile(final List<ProfileItemModel> profileItemModelList) {
        this.profileItemModelList = profileItemModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_member_profile, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.textViewItemName.setText(profileItemModelList.get(position).getProfileItemName());
        holder.textViewItemValue.setText(profileItemModelList.get(position).getProfileItemValue());
    }

    @Override
    public int getItemCount() {
        return profileItemModelList == null ? 0 : profileItemModelList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private View view;

        private TextView textViewItemValue;
        private TextView textViewItemName;

        private MyViewHolder(View itemView){
            super(itemView);
            view = itemView;
            textViewItemValue = (TextView) view.findViewById(R.id.textView_member_profile_item_value);
            textViewItemName = (TextView) view.findViewById(R.id.textView_member_profile_item_name);
        }
    }
}
