package com.mcafeweb.RecyclerViews;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcafeweb.Models.EventModel;
import com.mcafeweb.R;

import java.util.List;

/**
 * Created by Balpreet on 17-Mar-17.
 */

public class RecyclerViewAdapterEvents extends RecyclerView.Adapter<RecyclerViewAdapterEvents.MyViewHolder> {
    private List<EventModel> EventModelList;

    public RecyclerViewAdapterEvents(List<EventModel> eventModelList) {
        EventModelList = eventModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_events, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final EventModel event_Model = EventModelList.get(position);
        holder.textViewTitle.setText(event_Model.getTitle());
        holder.textViewEventDescription.setText(event_Model.getEvent_Description());
    }

    @Override
    public int getItemCount() {
        return EventModelList == null ? 0 : EventModelList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewMembers;
        private TextView textViewEventDescription;
        private ImageView Image;
        //private ImageView eventBackground;

        private MyViewHolder(View itemView) {
            super(itemView);
            textViewTitle = (TextView) itemView.findViewById(R.id.text_view_event_title);
            textViewTitle.setTypeface(Typeface.DEFAULT_BOLD);
            textViewMembers = (TextView) itemView.findViewById(R.id.text_view_event_members);
            textViewEventDescription = (TextView) itemView.findViewById(R.id.text_view_event_description);
            Image = (ImageView) itemView.findViewById(R.id.imageViewEvent);
            //eventBackground = (ImageView) itemView.findViewById(R.id.event_row_background);
            //eventBackground.setAlpha(.5f);
        }
    }
}

