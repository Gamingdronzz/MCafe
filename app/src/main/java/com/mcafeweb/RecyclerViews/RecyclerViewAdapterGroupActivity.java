package com.mcafeweb.RecyclerViews;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.mcafeweb.utils.DBHelper;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.Models.GroupCommentModel;
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

public class RecyclerViewAdapterGroupActivity extends RecyclerView.Adapter<RecyclerViewAdapterGroupActivity.MyViewHolder> implements VolleyHelper.VolleyResponse {
    private List<GroupCommentModel> groupCommentModelList;
    final String TAG = "Group Adapter";
    Helper helper;
    Context context;

    DBHelper dbHelper;
    SQLiteDatabase db;
    VolleyHelper volleyHelper;

    public RecyclerViewAdapterGroupActivity(List<GroupCommentModel> groupModelList) {
        groupCommentModelList = groupModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_group_activity, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        context = parent.getContext();
        helper = new Helper(context);
        volleyHelper = new VolleyHelper(this,context);
        setupDatabase();
        return myViewHolder;
    }

    private void setupDatabase()
    {
        dbHelper = new DBHelper(context);
        db = context.openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE,null);
        dbHelper.onCreate(db);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final GroupCommentModel group_Model = groupCommentModelList.get(position);
        holder.textViewContent.setText(groupCommentModelList.get(position).getGroupContent());
        holder.textViewUserName.setText(groupCommentModelList.get(position).getUserFirstName());
    }

    private void performLike(int position)
    {

        String url = helper.baseURL + "performLike.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("groupcontentid", groupCommentModelList.get(position).getGroupContentID()+"");
        params.put("groupid", groupCommentModelList.get(position).getGroupID()+"");
        params.put("userid",dbHelper.getUserID() +"");
        volleyHelper.makeStringRequest(url,"Perform_Like",params);

    }

    @Override
    public int getItemCount() {
        return groupCommentModelList == null ? 0 : groupCommentModelList.size();
    }

    @Override
    public void onResponse(String result) {

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


    public class MyViewHolder extends RecyclerView.ViewHolder  {

        private TextView textViewContent;
        private TextView textViewUserName;

        private MyViewHolder(View itemView) {
            super(itemView);
            textViewContent = (TextView) itemView.findViewById(R.id.text_view_group_activity_content);
            textViewUserName = (TextView) itemView.findViewById(R.id.text_view_group_activity_username);
        }
    }

    class OnLikeListener implements View.OnClickListener {
        private int position;

        public void updatePosition(int position) {

            this.position = position;
        }

        @Override
        public void onClick(View view) {
            groupCommentModelList.get(position).setGroupContentLikes(groupCommentModelList.get(position).getGroupContentLikes() + 1);
            RecyclerViewAdapterGroupActivity.this.notifyItemChanged(position);
            performLike(position);
        }
    }
}

