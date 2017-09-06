package com.mcafeweb;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mcafeweb.Models.GroupCommentModel;
import com.mcafeweb.RecyclerViews.RecyclerViewAdapterGroupActivity;
import com.mcafeweb.TouchListeners.RecyclerViewTouchListeners;
import com.mcafeweb.utils.DBHelper;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.utils.VolleyHelper;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupActivity extends AppCompatActivity implements VolleyHelper.VolleyResponse {

    RecyclerView groupActivityRecyclerView;
    RecyclerView.Adapter groupActivityAdapter;
    List<GroupCommentModel> groupCommentModelList;
    final String TAG = "Group Activity";

    Helper helper;

    int groupContentCount = 0;
    int groupID;

    FloatLabeledEditText floatLabeledEditTextComment;
    ImageButton sendComment;

    int originalGroupContentCount = 0;

    InputMethodManager inputMethodManager;

    String[] path;
    String[] keys;
    String[] values;
    String url;
    String data;

    private final static int INTERVAL = 1000 * 7; //2 seconds

    Handler mHandler;
    FloatingActionMenu floatingActionMenu;
    Runnable mHandlerTask;
    Runnable runnable;
    DBHelper dbHelper;
    VolleyHelper volleyHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        String groupName = getIntent().getStringExtra("name");
        getSupportActionBar().setTitle(groupName);
        groupID = getIntent().getIntExtra("groupid", 0);

        setupDatabase();
        mHandler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                getGroupContentCount();
                doTask();
                Log.v(TAG, "Running Handler Task");
            }
        };

        groupActivityRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_group_activity);
        floatLabeledEditTextComment = (FloatLabeledEditText) findViewById(R.id.group_comment_editText);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        sendComment = (ImageButton) findViewById(R.id.sendMessageFromGroup);
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessageToGroup();
                hideSoftInput();
                floatLabeledEditTextComment.getEditText().setText("");

            }
        });

        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this,this);


        groupCommentModelList = new ArrayList<>();

        setUpGroupActivityRecyclerView();
        ManageFloatingMenu();

        CheckPreviosChat();
    }

    private void setupDatabase()
    {
        dbHelper = new DBHelper(this);
        db = openOrCreateDatabase(DBHelper.DATABASE_NAME,Context.MODE_PRIVATE,null);
        dbHelper.onCreate(db);
    }


    private void CheckPreviosChat() {
        int previousrows = dbHelper.checkGroupActivityForData(dbHelper.getUserID(), groupID);
        if (previousrows == 0) {
            getGroupContentCount();
        } else {
            Log.v(TAG, "There is content in database");
            Object[][] rows = dbHelper.getGroupContentRow(dbHelper.getUserID(), groupID);

            for (int i = 0; i < previousrows; i++) {
                GroupCommentModel model = new GroupCommentModel();
                model.setGroupContentID((int) rows[i][0]);
                model.setGroupContent((String) rows[i][1]);
                model.setUserID((int) rows[i][2]);
                model.setGroupID((int) rows[i][3]);
                model.setGroupContentLikes((int) rows[i][4]);
                model.setGroupContentPostTime((String) rows[i][5]);
                model.setUserFirstName((String) rows[i][6]);
                groupCommentModelList.add(0, model);
                groupActivityAdapter.notifyItemInserted(0);
            }
            originalGroupContentCount = groupCommentModelList.size();
            groupActivityRecyclerView.smoothScrollToPosition(groupCommentModelList.size());

            mHandlerTask = runnable;
            mHandlerTask.run();

        }
    }

    private void ManageFloatingMenu() {
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.fam_group_activity);

        FloatingActionButton buttonGroupMembers = new FloatingActionButton(getApplicationContext());
        FloatingActionButton buttonCreateEvent = new FloatingActionButton(getApplicationContext());
        FloatingActionButton buttonAssignModerator = new FloatingActionButton(getApplicationContext());

        buttonGroupMembers.setLabelText("View Group Members");
        buttonCreateEvent.setLabelText("Create Event");
        buttonAssignModerator.setLabelText("Assign Moderator");

        FloatingActionButton buttonTest = new FloatingActionButton(getApplicationContext());
        buttonTest.setLabelText("Refresh");
        //floatingActionMenu.addMenuButton(buttonTest);

        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGroupContentCount();
            }
        });


        buttonGroupMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GroupMembers.class);
                intent.putExtra("groupid", groupID);
                startActivity(intent);
            }
        });

       /* buttonAssignModerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        */

        buttonCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if (dbHelper.getUserRole().equals(Helper.Instance.ROLE_ADMIN)) {

            floatingActionMenu.addMenuButton(buttonGroupMembers);
            floatingActionMenu.addMenuButton(buttonCreateEvent);
            //floatingActionMenu.addMenuButton(buttonAssignModerator);
        } else if (dbHelper.getUserRole().equals(Helper.Instance.ROLE_MODERATOR)) {
            floatingActionMenu.addMenuButton(buttonGroupMembers);
            floatingActionMenu.addMenuButton(buttonCreateEvent);
        } else if (dbHelper.getUserRole().equals(Helper.Instance.ROLE_MEMBER)) {
            floatingActionMenu.addMenuButton(buttonGroupMembers);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    private void setUpGroupActivityRecyclerView() {
        groupActivityAdapter = new RecyclerViewAdapterGroupActivity(groupCommentModelList);
        Log.d(TAG, "Adapter init");
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        groupActivityRecyclerView.setLayoutManager(manager);
        Log.d(TAG, "Manager set");
        groupActivityRecyclerView.setAdapter(groupActivityAdapter);
        Log.d(TAG, "Adapter set");

        groupActivityRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListeners(getApplicationContext(), groupActivityRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                floatLabeledEditTextComment.getEditText().setText("@" + groupCommentModelList.get(position).getUserFirstName() + " ");
                floatLabeledEditTextComment.getEditText().requestFocus();
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "Long Click on position        :" + position,
                        Toast.LENGTH_SHORT).show();
            }
        }));

    }

    private void getGroupContentCount() {


        groupContentCount = 0;

        String url = helper.baseURL + "getGroupContentCount.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("groupid", groupID + "");
        params.put("userid",dbHelper.getUserID() + "");
        volleyHelper.makeStringRequest(url,"Get_Group_Content_Count",params);
    }

    private void doTask() {
        mHandler.postDelayed(mHandlerTask, INTERVAL);
    }

    private void getGroupContentData(int groupContentID, int type) {

        String url = helper.baseURL + "getGroupContentData.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("groupcontentid", groupContentID + "");
        params.put("type",type + "");
        volleyHelper.makeStringRequest(url,"Get_Group_Content_Data",params);
    }

    private void sendMessageToGroup() {


        String message = floatLabeledEditTextComment.getEditText().getText().toString();

        String url = helper.baseURL + "sendMessageToGroup.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid", dbHelper.getUserID() + "");
        params.put("groupid",groupID + "");
        params.put("groupmessage",message);
        volleyHelper.makeStringRequest(url,"Get_Group_Content_Data",params);
    }

    private GroupCommentModel getModel(JSONObject json) throws JSONException {
        int group_id = json.getInt("group_id");
        String group_content = json.getString("group_content");

        try {
            group_content = URLDecoder.decode(group_content, "UTF-8");
        } catch (UnsupportedEncodingException use) {

        }
        int group_content_id = json.getInt("group_content_id");
        int user_id = json.getInt("user_id");
        String userFirstname = json.getString("user_firstname");
        int groupContentLikes = json.getInt("group_content_likes");
        String groupContentPostTime = json.getString("group_content_post_time");
        Log.v(TAG, "Group ID : ");

        GroupCommentModel model = new GroupCommentModel();
        model.setGroupID(group_id);
        model.setGroupContent(group_content);
        model.setGroupContentID(group_content_id);
        model.setUserID(user_id);
        model.setUserFirstName(userFirstname);
        model.setGroupContentLikes(groupContentLikes);

        dbHelper.sendMessageToGroup(group_content_id, user_id, group_id, group_content, groupContentLikes, groupContentPostTime, userFirstname);
        return model;

    }

    void hideSoftInput() {
        inputMethodManager.hideSoftInputFromWindow(floatingActionMenu.getWindowToken(), 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(runnable);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mHandler.postDelayed(runnable, INTERVAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(runnable, INTERVAL);
    }

    @Override
    public void onResponse(String result) {
        if (result == null || result.equals("")) {
            Log.v(TAG, "Result is Null");
            Toast.makeText(getApplicationContext(), "Unable to Get Group Contents\nPlease Try again after some time", Toast.LENGTH_LONG).show();
        } else {
            Log.v(TAG, "Result is Not Null");
            helper = new Helper(getApplicationContext());
            JSONObject json = helper.getJson(result);
            try {
                if (json.getString(helper.ACTION).toLowerCase().equals("trying to get group content count")) {
                    if (json.getString("get_group_content_count_result").equals(Helper.Instance.SUCCESS)) {
                        groupContentCount = json.getInt("row_count");

                        if (originalGroupContentCount < groupContentCount) {
                            int difference = groupContentCount - originalGroupContentCount;
                            int[] groupContentIDs = new int[difference];
                            Log.v(TAG, "Difference : " + difference);
                            originalGroupContentCount = groupContentCount;
                            for (int i = difference - 1; i >= 0; i++) {
                                groupContentIDs[i] = json.getInt("group_content_id_" + i);
                                getGroupContentData(groupContentIDs[i], 2);
                            }
                        }
                    } else {

                    }
                } else if (json.getString(helper.ACTION).toLowerCase().equals("trying to get old group content data")) {
                    GroupCommentModel model = getModel(json);
                    groupCommentModelList.add(0, model);
                    groupActivityAdapter.notifyItemInserted(0);
                    //oldContentCount++;
                } else if (json.getString(helper.ACTION).toLowerCase().equals("trying to get new group content data")) {
                    GroupCommentModel model = getModel(json);
                    int newPosition = groupCommentModelList.size();
                    groupCommentModelList.add(newPosition, model);
                    groupActivityAdapter.notifyItemInserted(newPosition);
                    groupActivityRecyclerView.smoothScrollToPosition(newPosition);
                }
            } catch (JSONException jse) {
                Toast.makeText(getApplicationContext(), "Unable to Get Group Contents\nPlease Try again after some time", Toast.LENGTH_LONG).show();
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
