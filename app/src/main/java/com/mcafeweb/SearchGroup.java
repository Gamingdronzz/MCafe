package com.mcafeweb;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.crystal.crystalpreloaders.widgets.CrystalPreloader;
import com.mcafeweb.Models.GroupModel;
import com.mcafeweb.RecyclerViews.RecyclerViewAdapterGroupTab;
import com.mcafeweb.TouchListeners.RecyclerViewTouchListeners;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.utils.VolleyHelper;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchGroup extends AppCompatActivity implements VolleyHelper.VolleyResponse {

    //NumberProgressBar numberProgressBar;
    FloatLabeledEditText searchString;
    SquareImageView searchButton;
    Helper helper;
    VolleyHelper volleyHelper;

    RecyclerView groupRecyclerView;
    RecyclerView.Adapter groupAdapter;
    List<GroupModel> groupModelList;
    int groupCount;
    int group_counter = 0;

    final String TAG = "Search Group";
    InputMethodManager inputMethodManager;

    CrystalPreloader crytsalProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);

        getSupportActionBar().setTitle("Search Groups");

        groupRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_search_group);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        crytsalProgressBar = (CrystalPreloader) findViewById(R.id.crystal_progress_bar_search_group);


        groupModelList = new ArrayList<>();
        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this,this);

        searchString = (FloatLabeledEditText) findViewById(R.id.EditText_Search_Group);
        searchButton = (SquareImageView) findViewById(R.id.buttonSearchGroup);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftInput();
                getGroupCount();
            }
        });

        setUpGroupRecyclerView();


    }

    void hideSoftInput() {
        inputMethodManager.hideSoftInputFromWindow(searchString.getWindowToken(), 0);
    }

    private void setUpGroupRecyclerView() {

        groupAdapter = new RecyclerViewAdapterGroupTab(groupModelList);
        Log.d(TAG, "Adapter init");
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        groupRecyclerView.setLayoutManager(manager);
        Log.d(TAG, "Manager set");
        groupRecyclerView.setAdapter(groupAdapter);
        Log.d(TAG, "Adapter set");

        groupRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListeners(getApplicationContext(), groupRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    void getGroupCount() {
        ShowProgressBar(true);
        groupModelList.clear();
        groupAdapter.notifyDataSetChanged();
        group_counter = 0;

        String url = helper.baseURL + "getGroupCountFromSearch.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("queryString", searchString.getEditText().getText().toString());
        volleyHelper.makeStringRequest(url, "Share_Blog", params);
    }

    void getGroupData(int groupID) {

        ShowProgressBar(true);

        String url = helper.baseURL + "getGroupData.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("groupid", groupID + "");
        volleyHelper.makeStringRequest(url, "Get_Group_Data", params);
    }

    void ShowProgressBar(boolean show) {
        if (show) {
            crytsalProgressBar.setVisibility(View.VISIBLE);
        } else {
            crytsalProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResponse(String result) {
        if (result == null || result.equals("")) {
            Log.v(TAG, "Result is Null");
            Toast.makeText(getApplicationContext(), "Unable to Get Groups\nPlease Try again after some time", Toast.LENGTH_LONG).show();
        } else {
            Log.v(TAG, "Result is Not Null");
            Helper helper = new Helper(getApplicationContext());
            Log.v(TAG, "Result = " + result);
            JSONObject json = helper.getJson(result);

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
        if(result.notModified) {
            try {
                final String jsonString = new String(result.data,
                        HttpHeaderParser.parseCharset(result.headers));
                JSONObject json = new JSONObject(jsonString);
                try {
                    if (json.getString(helper.ACTION).toLowerCase().equals("trying to get group count from search")) {

                        if (json.getString("get_group_count_result").equals(Helper.Instance.SUCCESS)) {
                            groupCount = json.getInt("row_count");

                            int[] groupIDs = new int[groupCount];
                            for (int i = 0; i < groupCount; i++) {
                                groupIDs[i] = json.getInt("group_id_" + i);
                                getGroupData(groupIDs[i]);
                                ShowProgressBar(true);
                            }
                            ShowProgressBar(false);
                        } else {

                        }
                    } else if (json.getString(helper.ACTION).toLowerCase().equals("trying to get group data")) {
                        int group_ID = json.getInt("group_id");
                        String groupName = json.getString("group_name");
                        int groupMembers = json.getInt("group_members");
                        String groupImage = json.getString("group_image");
                        String groupDescription = json.getString("group_description");
                        int groupCreatedByUserId = json.getInt("group_created_by_user_id");
                        String groupPrivacy = json.getString("group_privacy");
                        int groupShares = json.getInt(("group_shares"));

                        GroupModel model = new GroupModel();
                        model.setGroupID(group_ID);
                        model.setGroupTitle(groupName);
                        model.setGroupMembers(groupMembers);

                        model.setGroupDescription(groupDescription);
                        model.setGroupCreatedByUserID(groupCreatedByUserId);
                        model.setGroupPrivacy(groupPrivacy);
                        model.setGroupShares(groupShares);


                        groupModelList.add(model);
                        groupAdapter.notifyItemInserted(group_counter);
                        group_counter++;
                        ShowProgressBar(false);

                    }
                } catch (JSONException jse) {
                    ShowProgressBar(false);
                    Toast.makeText(getApplicationContext(), "Unable to Get Groups\nPlease Try again after some time", Toast.LENGTH_LONG).show();
                }
            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Log.d(TAG,"modified result found");
        }
    }

    @Override
    public void onError(VolleyError error) {

    }
}
