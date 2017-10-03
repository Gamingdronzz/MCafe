package com.mcafeweb.Tabs;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.crystal.crystalpreloaders.widgets.CrystalPreloader;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mcafeweb.ClickListener;
import com.mcafeweb.CreateGroup;
import com.mcafeweb.utils.DBHelper;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.Models.GroupModel;
import com.mcafeweb.R;
import com.mcafeweb.RecyclerViews.RecyclerViewAdapterGroupTab;
import com.mcafeweb.SearchGroup;
import com.mcafeweb.TouchListeners.RecyclerViewTouchListeners;
import com.mcafeweb.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Balpreet on 15-Mar-17.
 */
public class TabGroups extends Fragment implements VolleyHelper.VolleyResponse {
    RecyclerView groupRecyclerView;
    RecyclerView.Adapter groupAdapter;
    List<GroupModel> groupModelList;
    final String TAG = "Tab Group";
    Helper helper;
    int originalGroupCount = 0;
    int groupCount;
    int difference = 0;
    int group_counter = 0;

    FloatingActionMenu fabTabGroup;
    CrystalPreloader crytsalProgressBar;

    DBHelper dbHelper;
    SQLiteDatabase db;
    VolleyHelper volleyHelper;

    Context context;


    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        View rootView = inflater.inflate(R.layout.tabgroups, container, false);
        context = rootView.getContext();

        groupRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_group_tab);
        crytsalProgressBar = (CrystalPreloader) rootView.findViewById(R.id.crystal_progress_bar_tab_group);

        ShowProgressBar(true);

        setupDatabase();
        helper = new Helper(getContext());
        volleyHelper = new VolleyHelper(this,getContext());
        groupModelList = new ArrayList<>();

        ManageFloatingMenu(rootView);

        getGroupCount();

        setUpGroupRecyclerView(rootView);
        return rootView;
    }

    private void setupDatabase() {
        dbHelper = new DBHelper(context);
        db = context.openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper.onCreate(db);
    }

    void getGroupCount() {
        ShowProgressBar(true);

        String url = helper.baseURL + "getGroupCount.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid", dbHelper.getUserID() + "");
        volleyHelper.makeStringRequest(url, "Share_Blog", params);
    }

    void getGroupData(int groupID) {
        ShowProgressBar(true);

        String url = helper.baseURL + "getGroupData.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("groupid", groupID + "");
        volleyHelper.makeStringRequest(url, "Share_Blog", params);
    }

    @Override
    public void onResume() {
        super.onResume();
        getGroupCount();
        groupAdapter.notifyDataSetChanged();
    }


    private void ManageFloatingMenu(View view) {
        fabTabGroup = (FloatingActionMenu) view.findViewById(R.id.fam_groups);
        FloatingActionButton buttonSearchGroup = new FloatingActionButton(view.getContext());
        //buttonSearchGroup = setUpFloatingActionButton(buttonSearchGroup);

        buttonSearchGroup.setLabelText("Search Group");
        FloatingActionButton buttonCreateGroup = new FloatingActionButton(view.getContext());
//        buttonCreateGroup = setUpFloatingActionButton(buttonCreateGroup);
        buttonCreateGroup.setLabelText("Create Group");

        buttonSearchGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchGroup.class);
                startActivity(intent);
                Log.v(TAG, "Search Group");

            }
        });

        buttonCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateGroup.class);
                startActivity(intent);
                Log.v(TAG, "Create Group");
            }
        });

        fabTabGroup.addMenuButton(buttonSearchGroup);
        String userrole = dbHelper.getUserRole();

        if (dbHelper.getUserRole().equals(Helper.Instance.ROLE_ADMIN)) {
            fabTabGroup.addMenuButton(buttonCreateGroup);
        } else if (dbHelper.getUserRole().equals(Helper.Instance.ROLE_MODERATOR)) {

        }
    }


    private FloatingActionButton setUpFloatingActionButton(FloatingActionButton fab) {
        fab.setColorNormal(R.color.colorAccent);
        fab.setColorPressed(R.color.colorAccentDark);
        return fab;
    }


    private void setUpGroupRecyclerView(View view) {
        groupAdapter = new RecyclerViewAdapterGroupTab(groupModelList);
        Log.d(TAG, "Adapter init");
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        groupRecyclerView.setLayoutManager(manager);
        Log.d(TAG, "Manager set");
        groupRecyclerView.setAdapter(groupAdapter);
        Log.d(TAG, "Adapter set");

        groupRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListeners(getContext(), groupRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Intent intent = new Intent(getContext(), GroupActivity.class);
                //intent.putExtra("name", groupModelList.get(position).getGroupTitle());
                //startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getContext(), "Long Click on position        :" + position,
                        Toast.LENGTH_SHORT).show();
            }
        }));

    }

    void ShowProgressBar(boolean show) {
        if (show) {
            crytsalProgressBar.setVisibility(View.VISIBLE);
        } else {
            crytsalProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResponse(String result) {
        if (result == null || result.equals("")) {
            Log.v(TAG, "Result is Null");
            Toast.makeText(getContext(), "Unable to Get Groups\nPlease Try again after some time", Toast.LENGTH_LONG).show();
            ShowProgressBar(false);
        } else {
            Log.v(TAG, "Result is Not Null");
            Helper helper = new Helper(getContext());
            JSONObject json = helper.getJson(result);
            try {
                if (json.getString(helper.ACTION).toLowerCase().equals("trying to get group count")) {

                    if (json.getString("get_group_count_result").equals(Helper.Instance.SUCCESS)) {
                        groupCount = json.getInt("row_count");
                        //Log.v(TAG, "Orgianl Group count : " + originalGroupCount);
                        //Log.v(TAG, "New Group count : " + groupCount);

                        if (groupCount > originalGroupCount) {

                            difference = groupCount - originalGroupCount;

                            originalGroupCount = groupCount;
                            int[] groupIDs = new int[difference];
                            for (int i = 0; i < difference; i++) {
                                groupIDs[i] = Integer.parseInt(json.getString("group_id_" + i));
                                getGroupData(groupIDs[i]);
                                ShowProgressBar(true);
                            }
                        }
                        ShowProgressBar(false);
                    } else {
                        ShowProgressBar(false);
                    }
                } else if (json.getString(helper.ACTION).toLowerCase().equals("trying to get group data")) {
                    group_counter = groupModelList.size();
                    int group_ID = json.getInt("group_id");
                    String groupName = json.getString("group_name");
                    int groupMembers = json.getInt("group_members");
                    String groupImage = json.getString("group_image");
                    String groupDescription = json.getString("group_description");
                    int groupCreatedByUserId = Integer.parseInt(json.getString("group_created_by_user_id"));
                    String groupPrivacy = json.getString("group_privacy");
                    int groupShares = Integer.parseInt(json.getString(("group_shares")));
                    String groupCreatedAt = json.getString("group_created_at");
                    int groupCategory = json.getInt("group_category");

                    GroupModel model = new GroupModel();

                    model.setGroup_Image(helper.getBitmapFromString(groupImage,"group"));
                    model.setGroupID(group_ID);
                    model.setGroupTitle(groupName);
                    model.setGroupMembers(groupMembers);

                    model.setGroupDescription(groupDescription);
                    model.setGroupCreatedByUserID(groupCreatedByUserId);
                    model.setGroupPrivacy(groupPrivacy);
                    model.setGroupShares(groupShares);
                    model.setGroupCategory(groupCategory);
                    try {
                        model.setGroupCreatedDate(DateFormat.getDateTimeInstance().parse(groupCreatedAt));
                    } catch (ParseException pe) {
                        ShowProgressBar(false);
                    }

                    groupModelList.add(model);
                    groupAdapter.notifyItemInserted(group_counter);
                    originalGroupCount = groupModelList.size();
                    group_counter++;
                    ShowProgressBar(false);

                }
            } catch (JSONException jse) {
                ShowProgressBar(false);
                Toast.makeText(getContext(), "Unable to Get Groups\nPlease Try again after some time", Toast.LENGTH_LONG).show();
            } finally {
                ShowProgressBar(false);
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