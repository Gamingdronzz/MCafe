package com.mcafeweb.RecyclerViews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcafeweb.utils.Helper;
import com.mcafeweb.Models.UserModel;
import com.mcafeweb.R;

import java.util.List;

/**
 * Created by Balpreet on 17-Mar-17.
 */

public class RecyclerViewAdapterUsers extends RecyclerView.Adapter<RecyclerViewAdapterUsers.MyViewHolder>{
    private List<UserModel> userModelList;
    final String TAG = "User Adapter";
    Helper helper;
    Context context;

    public RecyclerViewAdapterUsers(List<UserModel> userModelList) {
        this.userModelList = userModelList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_members, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        context = parent.getContext();
        helper = new Helper(context);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.textViewUserName.setText(userModelList.get(position).getUserFirstName() + " " + userModelList.get(position).getUserLastName());
    }


    @Override
    public int getItemCount() {
        return userModelList == null ? 0 : userModelList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewUserName;

        private MyViewHolder(View itemView) {
            super(itemView);
            textViewUserName = (TextView) itemView.findViewById(R.id.text_view_user_name);

        }
    }
}

