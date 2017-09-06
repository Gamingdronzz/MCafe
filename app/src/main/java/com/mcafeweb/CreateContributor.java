package com.mcafeweb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.crystal.crystalpreloaders.widgets.CrystalPreloader;
import com.mcafeweb.Models.BlogModel;
import com.mcafeweb.Models.UserModel;
import com.mcafeweb.RecyclerViews.RecyclerViewAdapterBlog;
import com.mcafeweb.RecyclerViews.RecyclerViewAdapterUsers;
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

import static android.icu.lang.UProperty.INT_START;

public class CreateContributor extends AppCompatActivity implements VolleyHelper.VolleyResponse {


    FloatLabeledEditText searchString;
    SquareImageView searchButton;
    Helper helper;
    VolleyHelper volleyHelper;

    RecyclerView userRecyclerView;
    RecyclerView.Adapter userAdapter;
    List<UserModel> userModelList;
    int userCount;
    int userCounter = 0;

    final String TAG = "SearchMember";
    InputMethodManager inputMethodManager;

    CrystalPreloader crytsalProgressBar;
    List<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contributor);

        userRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_search_member);
        crytsalProgressBar = (CrystalPreloader) findViewById(R.id.crystal_progress_bar_search_member);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        userModelList = new ArrayList<>();
        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this, this);
        ShowProgressBar(false);

        searchString = (FloatLabeledEditText) findViewById(R.id.EditText_Search_Member);
        searchButton = (SquareImageView) findViewById(R.id.buttonSearchMember);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftInput();
                getUserCount();
            }
        });

        setUpBlogRecyclerView();
    }

    void hideSoftInput() {
        inputMethodManager.hideSoftInputFromWindow(searchString.getWindowToken(), 0);
    }

    private void setUpBlogRecyclerView() {

        userAdapter = new RecyclerViewAdapterUsers(userModelList);
        Log.d(TAG, "Adapter init");
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        userRecyclerView.setLayoutManager(manager);
        Log.d(TAG, "Manager set");
        userRecyclerView.setAdapter(userAdapter);
        Log.d(TAG, "Adapter set");

        userRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListeners(getApplicationContext(), userRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), MemberProfile.class);
                intent.putExtra("userid", userModelList.get(position).getUserID());
                intent.putExtra("userFirstName", userModelList.get(position).getUserFirstName());
                intent.putExtra("userLastName", userModelList.get(position).getUserLastName());
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {
                makeUserContributor(position);
            }
        }));

    }

    private void makeUserContributor(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                makeContributor(position);
            }
        });

        alertDialog.setNegativeButton("No", null);
        SpannableStringBuilder str = new SpannableStringBuilder("Do you want to make " + userModelList.get(position).getUserFirstName() + " Contributor?");
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 19, str.length()-13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        alertDialog.setMessage(str);
        alertDialog.setTitle("Mcafe");
        alertDialog.show();
    }

    private void makeContributor(int position) {
        String url = helper.baseURL + "makeUserContributor.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid",userModelList.get(position).getUserID()+"");
        if (volleyHelper.countRequestsInFlight("Make_User_Contributor_"+userModelList.get(position).getUserID()) == 0) {
            volleyHelper.makeStringRequest(url, "Make_User_Contributor_"+userModelList.get(position).getUserID(), params);
            Toast.makeText(this, "Making " + userModelList.get(position).getUserFirstName() + " Contributor", Toast.LENGTH_SHORT).show();
        }
    }

    void getUserCount() {
        tasks.clear();
        tasks.removeAll(tasks);
        ShowProgressBar(true);
        userModelList.clear();
        userAdapter.notifyDataSetChanged();
        userCounter = 0;
        ShowProgressBar(true);

        String url = helper.baseURL + "getUserCountFromSearch.php5";
        Map<String, String> params = new HashMap<String, String>();
        if (searchString.getEditText().getText().toString().replaceAll("\\s+", "").equals("")) {
            params.put("searchValue", "");
            params.put("searchType", "1");
        } else {
            params.put("searchValue", searchString.getEditText().getText().toString());
            params.put("searchType", "2");
        }
        if (volleyHelper.countRequestsInFlight("Get_User_Count_From_Search") == 0) {
            volleyHelper.makeStringRequest(url, "Get_User_Count_From_Search", params);
        }
    }


    void ShowProgressBar(boolean show) {
        if (show) {
            crytsalProgressBar.setVisibility(View.VISIBLE);
        } else {
            crytsalProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    void getUserData(int userid) {

        String url = helper.baseURL + "getUserData.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid", userid + "");
        volleyHelper.makeCachedRequest(url, "Get_Blog_Count_From_Search", params);
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getStatus() == AsyncTask.Status.FINISHED) {
                tasks.remove(i);
            } else if (tasks.get(i).getStatus() == AsyncTask.Status.RUNNING) {
                tasks.get(i).cancel(true);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getStatus() == AsyncTask.Status.FINISHED) {
                tasks.remove(i);
            } else if (tasks.get(i).getStatus() == AsyncTask.Status.PENDING) {
                //tasks.get(i).execute()
            }
        }

    }

    @Override
    public void onResponse(String result) {
        if (result == null || result.equals("")) {
            ShowProgressBar(false);
            Log.v(TAG, "Result is Null");
            Toast.makeText(getApplicationContext(), "Unable to Get Users\nPlease Try again after some time", Toast.LENGTH_LONG).show();
        } else {
            Log.v(TAG, "Result is Not Null");
            Helper helper = new Helper(getApplicationContext());
            JSONObject json = helper.getJson(result);

            try {
                if (json.getString(helper.ACTION).toLowerCase().equals("trying to get user count from search")) {

                    if (json.getString("get_user_count_result").equals(Helper.Instance.SUCCESS)) {
                        userCount = json.getInt("row_count");
                        int[] userIDs = new int[userCount];
                        for (int i = 0; i < userCount; i++) {
                            userIDs[i] = json.getInt("user_id_" + i);
                            getUserData(userIDs[i]);
                            ShowProgressBar(true);
                        }
                        ShowProgressBar(false);
                    } else {
                        Toast.makeText(getApplicationContext(), "No users found", Toast.LENGTH_LONG).show();
                        ShowProgressBar(false);
                    }
                }else if (json.getString(helper.ACTION).toLowerCase().equals("trying to make contributor")) {

                    if (json.getString("make_contributor_result").equals(Helper.Instance.SUCCESS)) {
                        Toast.makeText(this,"Succesfully Made Contributor",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to make contributor", Toast.LENGTH_LONG).show();
                    }
                }

            } catch (JSONException jse) {
                jse.printStackTrace();
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
            try {
                if (json.getString(helper.ACTION).toLowerCase().equals("trying to get user data")) {
                    int userID = json.getInt("userid");
                    String userFirstName = json.getString("first_name");
                    String userLastName = json.getString("last_name");


                    UserModel model = new UserModel();
                    model.setUserID(userID);
                    model.setUserFirstName(userFirstName);
                    model.setUserLastName(userLastName);

                    userModelList.add(model);
                    userAdapter.notifyItemInserted(userCounter);
                    userCounter++;
                    if (userCounter == userCount) {
                        ShowProgressBar(false);
                    }
                }
            } catch (JSONException jse) {
                Toast.makeText(getApplicationContext(), "Unable to Get Users\nPlease Try again after some time", Toast.LENGTH_LONG).show();
            } finally {
                ShowProgressBar(false);
            }
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(VolleyError error) {
        ShowProgressBar(false);
        Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show();
    }

}
