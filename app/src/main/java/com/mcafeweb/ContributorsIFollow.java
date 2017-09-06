package com.mcafeweb;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.crystal.crystalpreloaders.widgets.CrystalPreloader;
import com.mcafeweb.Models.UserModel;
import com.mcafeweb.RecyclerViews.RecyclerViewAdapterUsers;
import com.mcafeweb.TouchListeners.RecyclerViewTouchListeners;
import com.mcafeweb.app.AppController;
import com.mcafeweb.utils.DBHelper;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContributorsIFollow extends AppCompatActivity implements VolleyHelper.VolleyResponse {
    Helper helper;
    final String TAG = "Contributors";
    int contributors = 0;
    int[] userIDs;

    VolleyHelper volleyHelper;


    RecyclerView contributorRecyclerView;
    RecyclerView.Adapter contributorAdapter;
    List<UserModel> contributorModelList;

    int contributorCounter = 0;

    DBHelper dbHelper;
    SQLiteDatabase db;
    int originalContributorCount = 0;
    CrystalPreloader crytsalProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contributors_ifollow);

        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this,this);
        contributorRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_contributos_i_follow);
        crytsalProgressBar = (CrystalPreloader) findViewById(R.id.crystal_progress_bar_contributors);

        contributorModelList = new ArrayList<>();

        setUpContributorRecyclerView();
        setupDatabase();
        getContributorFollowCount();
    }

    private void setupDatabase() {
        dbHelper = new DBHelper(this);
        db = openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper.onCreate(db);
    }

    private void setUpContributorRecyclerView() {
        contributorAdapter = new RecyclerViewAdapterUsers(contributorModelList);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        contributorRecyclerView.setLayoutManager(manager);
        contributorRecyclerView.setAdapter(contributorAdapter);


        contributorRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListeners(getApplicationContext(), contributorRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), MemberProfile.class);
                intent.putExtra("userid", contributorModelList.get(position).getUserID());
                intent.putExtra("userFirstName", contributorModelList.get(position).getUserFirstName());
                intent.putExtra("userLastName", contributorModelList.get(position).getUserLastName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void getContributorFollowCount() {
        ShowProgressBar(true);

        String url = helper.baseURL + "getContributorFollowCount.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid", dbHelper.getUserID() + "");
        if (volleyHelper.countRequestsInFlight("Get_Contributor_Follow_Count") == 0)
            volleyHelper.makeStringRequest(url, "Get_Contributor_Follow_Count", params);
        else
            ShowProgressBar(false);

    }

    private void getUserData(int userid) {

        String url = helper.baseURL + "getUserData.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid", userid + "");
        volleyHelper.makeCachedRequest(url, "Get_User_data", params);
        ShowProgressBar(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onResponse(String result) {
        JSONObject json = helper.getJson(result);
        try {
            if (json.getString(helper.ACTION).toLowerCase().equals("trying to get contributor count")) {

                if (json.getString("get_contributor_count_result").equals(Helper.Instance.SUCCESS)) {
                    contributors = json.getInt("row_count");
                    userIDs = new int[contributors];
                    for (int i = 0; i < contributors; i++) {
                        userIDs[i] = json.getInt("user_id_" + i);
                        getUserData(userIDs[i]);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Not Following any contributors yet", Toast.LENGTH_SHORT);
                    ShowProgressBar(false);
                }
            }
        } catch (JSONException jse) {
            Toast.makeText(getApplicationContext(), "Unable to Get Contributors you follow\nPlease Try again after some time", Toast.LENGTH_LONG).show();
            ShowProgressBar(false);
        }
        finally {
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

            try {
                if (json.getString(helper.ACTION).toLowerCase().equals("trying to get user data")) {
                    if (json.getString("get_user_data_result").equals(Helper.Instance.SUCCESS)) {
                        String first_name = json.getString("first_name");
                        String last_name = json.getString("last_name");
                        int userid = json.getInt("userid");

                        UserModel model = new UserModel();
                        model.setUserFirstName(first_name);
                        model.setUserLastName(last_name);
                        model.setUserID(userid);

                        contributorModelList.add(contributorCounter, model);
                        contributorAdapter.notifyItemInserted(contributorCounter);
                        contributorCounter++;
                    }
                }
            } catch (JSONException jse) {
                Toast.makeText(getApplicationContext(), "Unable to Get Group Members\nPlease Try again after some time", Toast.LENGTH_LONG).show();
            }

        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
        finally {
            ShowProgressBar(false);
        }
    }

    @Override
    public void onError(VolleyError error) {
        ShowProgressBar(false);
    }

    void ShowProgressBar(boolean show) {
        if (show) {
            crytsalProgressBar.setVisibility(View.VISIBLE);
        } else {
            crytsalProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
