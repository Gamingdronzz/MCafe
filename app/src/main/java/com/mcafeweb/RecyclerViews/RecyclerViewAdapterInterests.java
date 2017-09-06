package com.mcafeweb.RecyclerViews;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.mcafeweb.Models.InterestModel;
import com.mcafeweb.R;

import java.util.List;

/**
 * Created by Balpreet on 17-Mar-17.
 */

public class RecyclerViewAdapterInterests extends RecyclerView.Adapter<RecyclerViewAdapterInterests.MyViewHolder> {
    private List<InterestModel> mInterestModelList;
    private Context context;

    public RecyclerViewAdapterInterests(List<InterestModel> interestModelList) {
        mInterestModelList = interestModelList;
    }

    public RecyclerViewAdapterInterests()
    {

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_interests, parent, false);
        context = view.getContext();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final InterestModel interestModel = mInterestModelList.get(position);
        holder.checkedTextView.setText(interestModel.getInterestName());
        holder.cardView.setBackgroundResource(interestModel.isSelected() ? R.drawable.layout_background_selected  : R.drawable.layout_background_unselected);
        holder.checkedTextView.setTextColor(interestModel.isSelected() ? Color.WHITE : Color.BLACK);
        holder.checkedTextView.setTextColor(Color.BLACK);
        holder.checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestModel.setSelected(!interestModel.isSelected());
                holder.checkedTextView.toggle();
                //holder.checkedTextView.setBackgroundResource(interestModel.isSelected() ? R.drawable.layout_background_selected : R.drawable.layout_background_unselected);
                //holder.cardView.setBackgroundColor(interestModel.isSelected() ? Helper.getRandomMaterialColor("400") :Color.WHITE);
                holder.cardView.setBackgroundResource(interestModel.isSelected() ? R.drawable.layout_background_selected  : R.drawable.layout_background_unselected);
                holder.checkedTextView.setTextColor(interestModel.isSelected() ? Color.WHITE  : Color.BLACK);
                Log.v("Recycler", "Value " + interestModel.isSelected());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mInterestModelList == null ? 0 : mInterestModelList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private CheckedTextView checkedTextView;
        private CardView cardView;

        private MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            checkedTextView = (CheckedTextView) itemView.findViewById(R.id.checkedTextView);
            cardView = (CardView) itemView.findViewById(R.id.CardViewInterests);
        }
    }
}
