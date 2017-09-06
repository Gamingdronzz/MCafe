package com.mcafeweb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.mcafeweb.Models.UserModel;
import com.mcafeweb.RecyclerViews.RecyclerViewAdapterUsers;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupMembers extends AppCompatActivity implements VolleyHelper.VolleyResponse {

    Helper helper;
    final String TAG = "Group Members";
    int groupMembers = 0;
    int[] userIDs;
    int groupID;
    VolleyHelper volleyHelper;


    RecyclerView groupMemberRecyclerView;
    RecyclerView.Adapter groupMemberAdapter;
    List<UserModel> userModelList;

    int groupMemberCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);

        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this,this);
        groupID = getIntent().getIntExtra("groupid", 0);
        Log.v(TAG, "Group ID = " + groupID);
        groupMemberRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_group_members);

        userModelList = new ArrayList<>();

        setUpGroupActivityRecyclerView();

        getGroupMemberCount();
    }


    private void setUpGroupActivityRecyclerView() {
        groupMemberAdapter = new RecyclerViewAdapterUsers(userModelList);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        groupMemberRecyclerView.setLayoutManager(manager);
        groupMemberRecyclerView.setAdapter(groupMemberAdapter);

        /*
        groupMemberRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListeners(getApplicationContext(), groupMemberRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        */
    }

    private void getGroupMemberCount() {

        String url = helper.baseURL + "getGroupMemberCount.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("groupid", groupID + "");
        volleyHelper.makeStringRequest(url,"Get_Group_Member_Count",params);
    }

    private void getGroupMemberData(int userid) {

        String url = helper.baseURL + "getGroupMemberData.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid", userid + "");
        volleyHelper.makeStringRequest(url,"Share_Blog",params);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onResponse(String result) {
        if (result == null || result.equals("")) {
            Log.v(TAG, "Result is Null");
            Toast.makeText(getApplicationContext(), "Unable to Get Group Members\nPlease Try again after some time", Toast.LENGTH_LONG).show();
        } else {
            Log.v(TAG, "Result is Not Null");
            helper = new Helper(getApplicationContext());
            JSONObject json = helper.getJson(result);
            try {
                if (json.getString(helper.ACTION).toLowerCase().equals("trying to get group member count")) {

                    if (json.getString("get_group_member_count_result").equals(Helper.Instance.SUCCESS)) {
                        groupMembers = json.getInt("row_count");
                        userIDs = new int[groupMembers];
                        for (int i = 0; i < groupMembers; i++) {
                            userIDs[i] = json.getInt("user_id_" + i);
                            getGroupMemberData(userIDs[i]);
                        }

                    }
                } else if (json.getString(helper.ACTION).toLowerCase().equals("trying to get group member data")) {
                    if (json.getString("get_group_member_data_result").equals(Helper.Instance.SUCCESS)) {
                        Log.v(TAG, "Fetching details");
                        String first_name = json.getString("first_name");
                        String last_name = json.getString("last_name");
                        int userid = json.getInt("userid");

                        UserModel model = new UserModel();
                        model.setUserFirstName(first_name);
                        model.setUserLastName(last_name);
                        model.setUserID(userid);

                        userModelList.add(groupMemberCounter, model);
                        groupMemberAdapter.notifyItemInserted(groupMemberCounter);
                        groupMemberCounter++;

                        /*
                        String mobile = json.getString("profile_mobile");
                        String city = json.getString("profile_city");
                        String country = json.getString("profile_country");
                        String bio = json.getString("profile_bio");
                        String brief = json.getString("profile_brief");
                        String experience = json.getString("profile_experience");
                        String certifications = json.getString("profile_certifications");

                        MemberProfile memberProfile = new MemberProfile();
                        //memberProfile.setFirstName(firstName);
                        //memberProfile.setLastName(lastname);
                        memberProfile.setMobile(mobile);
                        memberProfile.setCity(city);
                        memberProfile.setCountry(country);
                        memberProfile.setBio(bio);
                        memberProfile.setBriefProfile(brief);
                        memberProfile.setExperience(experience);
                        memberProfile.setCertifications(certifications);

                        */
                    }
                }
            } catch (JSONException jse) {
                Toast.makeText(getApplicationContext(), "Unable to Get Group Members\nPlease Try again after some time", Toast.LENGTH_LONG).show();
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

    }

    @Override
    public void onError(VolleyError error) {

    }
}
