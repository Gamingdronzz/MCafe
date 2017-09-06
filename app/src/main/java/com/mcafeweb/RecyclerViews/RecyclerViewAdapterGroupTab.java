package com.mcafeweb.RecyclerViews;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.mcafeweb.utils.DBHelper;
import com.mcafeweb.GroupActivity;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.Models.GroupModel;
import com.mcafeweb.R;
import com.mcafeweb.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Balpreet on 17-Mar-17.
 */

public class RecyclerViewAdapterGroupTab extends RecyclerView.Adapter<RecyclerViewAdapterGroupTab.MyViewHolder> implements VolleyHelper.VolleyResponse{
    private List<GroupModel> GroupModelList;
    final String TAG = "Group Adapter";

    Helper helper;
    Context context;

    String parentName;

    DBHelper dbHelper;
    SQLiteDatabase db;
    VolleyHelper volleyHelper;


    public RecyclerViewAdapterGroupTab(List<GroupModel> groupModelList) {
        GroupModelList = groupModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_group_tab, parent, false);

        context = parent.getContext();
        helper = new Helper(context);
        volleyHelper = new VolleyHelper(this,context);
        setupDatabase();
        parentName = parent.toString();
        String[] intermediate = parentName.split("id/");
        String s = intermediate[1];
        Log.v(TAG, "Parent Name : " + s);

        MyViewHolder myViewHolder = new MyViewHolder(view, new OnShareListener(), new OnFollowListener(), new OnClickListener());
        return myViewHolder;
    }

    private void setupDatabase() {
        dbHelper = new DBHelper(context);
        db = context.openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper.onCreate(db);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final GroupModel group_Model = GroupModelList.get(position);
        holder.textViewGroupTitle.setText(group_Model.getGroupTitle());
        holder.textViewGroupDescription.setText(group_Model.getGroupDescription());
        holder.Image.setImageBitmap(group_Model.getGroup_Image());

        holder.onShareListener.updatePosition(position);
        holder.onFollowListener.updatePosition(position);
        holder.onClickListener.updatePosition(position);

        holder.textViewShares.setText(helper.getStringNumeric(group_Model.getGroupShares()));
        holder.textViewMembers.setText(helper.getStringNumeric(group_Model.getGroupMembers()));
    }

    @Override
    public int getItemCount() {
        return GroupModelList == null ? 0 : GroupModelList.size();
    }

    private void performShare(int position, View view) {


        String url = helper.baseURL + "shareGroup.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("groupid", GroupModelList.get(position).getGroupID() + "");
        params.put("userid", dbHelper.getUserID() + "");
        volleyHelper.makeStringRequest(url,"Share_Group",params);

    }


    private void performFollow(int position) {

        String url = helper.baseURL + "followGroup.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("groupid", GroupModelList.get(position).getGroupID() + "");
        params.put("userid", dbHelper.getUserID() + "");
        volleyHelper.makeStringRequest(url,"Follow_Group",params);

    }

    private void openGroup(int position) {
        Intent intent = new Intent(context, GroupActivity.class);
        intent.putExtra("name", GroupModelList.get(position).getGroupTitle());
        intent.putExtra("groupid", GroupModelList.get(position).getGroupID());
        context.startActivity(intent);
    }

    @Override
    public void onResponse(String result) {
        if (result == null || result.equals("")) {
            Log.v(TAG, "Result is Null");
            Toast.makeText(context, "Please Try again after some time", Toast.LENGTH_SHORT).show();
        } else {
            Log.v(TAG, "Result is Not Null");
            JSONObject json = helper.getJson(result);
            try {
                if (json.getString(helper.ACTION).toLowerCase().equals("trying to perform group follow")) {
                    if (json.getString("perform_like_result").equals(helper.FAILURE)) {
                        Toast.makeText(context, "You are already a member of this group", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Succesfully Followed Group", Toast.LENGTH_SHORT).show();
                    }
                } else if (json.getString(helper.ACTION).toLowerCase().equals("trying to perform group share")) {
                    if (json.getString("perform_like_result").equals(helper.FAILURE)) {

                    } else {

                    }
                }
            } catch (JSONException jse) {
                Toast.makeText(context, "Please Try again after some time", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResponse(JSONObject result) {

    }

    @Override
    public void onResponse(JSONArray result) {

    }

    @Override
    public void onResponse(NetworkResponse result) {
        try {
            final String jsonString = new String(result.data,
                    HttpHeaderParser.parseCharset(result.headers));
            JSONObject json = new JSONObject(jsonString);

        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(VolleyError error) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewGroupTitle;
        private TextView textViewGroupDescription;

        private ImageView Image;

        ImageButton buttonFollow;
        ImageButton buttonShare;

        private TextView textViewMembers;
        private TextView textViewShares;

        private OnShareListener onShareListener;
        private OnFollowListener onFollowListener;
        private OnClickListener onClickListener;

        private MyViewHolder(View itemView, OnShareListener onShareListener, OnFollowListener onFollowListener, OnClickListener onClickListener) {
            super(itemView);
            textViewGroupTitle = (TextView) itemView.findViewById(R.id.text_view_group_title);
            textViewGroupDescription = (TextView) itemView.findViewById(R.id.text_view_group_description);
            Image = (ImageView) itemView.findViewById(R.id.imageViewGroup);

            buttonFollow = (ImageButton) itemView.findViewById(R.id.group_follow);
            buttonShare = (ImageButton) itemView.findViewById(R.id.group_share);

            textViewMembers = (TextView) itemView.findViewById(R.id.text_view_group_members);
            textViewShares = (TextView) itemView.findViewById(R.id.text_view_group_shares);


            if (parentName.contains("recycler_view_group_tab")) {
                Log.v(TAG, "My View holder : Tab");
                buttonFollow.setImageResource(R.drawable.followed);
            } else if (parentName.contains("recycler_view_search_group")) {
                Log.v(TAG, "My View holder : Group");
                buttonFollow.setImageResource(R.drawable.follow);
            }

            this.onFollowListener = onFollowListener;
            this.onShareListener = onShareListener;
            this.onClickListener = onClickListener;


            this.textViewGroupTitle.setOnClickListener(this.onClickListener);
            this.textViewGroupDescription.setOnClickListener(this.onClickListener);
            this.Image.setOnClickListener(this.onClickListener);


            this.buttonShare.setOnClickListener(onShareListener);
            this.buttonFollow.setOnClickListener(onFollowListener);
            this.textViewShares.setOnClickListener(onShareListener);
            this.textViewMembers.setOnClickListener(onFollowListener);
        }
    }


    class OnShareListener implements View.OnClickListener {
        private int position;

        public void updatePosition(int position) {

            this.position = position;
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "Trying to share group", Toast.LENGTH_SHORT).show();
            GroupModelList.get(position).setGroupShares(GroupModelList.get(position).getGroupShares() + 1);
            RecyclerViewAdapterGroupTab.this.notifyItemChanged(position);
            performShare(position, view);
        }
    }


    class OnFollowListener implements View.OnClickListener {
        private int position;

        public void updatePosition(int position) {

            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (parentName.contains("recycler_view_group_tab")) {
                Log.v(TAG, "My View holder : Tab");
                Toast.makeText(context, "You are already a member of this group", Toast.LENGTH_SHORT).show();

            } else if (parentName.contains("recycler_view_search_group}")) {
                Log.v(TAG, "My View holder : Group");
                Toast.makeText(context, "Trying to follow group", Toast.LENGTH_SHORT).show();
                GroupModelList.get(position).setGroupMembers(GroupModelList.get(position).getGroupMembers() + 1);
                RecyclerViewAdapterGroupTab.this.notifyItemChanged(position);
                performFollow(position);
            }
        }
    }

    class OnClickListener implements View.OnClickListener {
        private int position;

        public void updatePosition(int position) {

            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (parentName.contains("recycler_view_group_tab")) {
                openGroup(position);
            } else if (parentName.contains("recycler_view_search_group}")) {
                Toast.makeText(context, "Groups can only be opened through Timeline", Toast.LENGTH_SHORT).show();
            }
        }
    }


}



