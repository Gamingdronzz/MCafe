package com.mcafeweb;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.mcafeweb.Models.ProfileItemModel;
import com.mcafeweb.RecyclerViews.RecyclerViewAdapterMemberProfile;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberProfile extends AppCompatActivity implements VolleyHelper.VolleyResponse {

    InputMethodManager inputMethodManager;
    Helper helper;

    List<ProfileItemModel> profileItemModelList;
    RecyclerView recyclerView;
    RecyclerViewAdapterMemberProfile adapter;
    ImageView memberProfilePic;
    VolleyHelper volleyHelper;

    final String TAG = "Member profile";

    int profileItemCounter = 0;

    LinearLayout ProfileLayout;
    RelativeLayout progressLayout;


    int userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profile);
        memberProfilePic = (ImageView) findViewById(R.id.member_profile_Profile_Picture);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ProfileLayout = (LinearLayout) findViewById(R.id.linear_layout_member_profile);
        progressLayout = (RelativeLayout) findViewById(R.id.progress_layout_member_profile);

        userid = getIntent().getIntExtra("userid", -1);
        getSupportActionBar().setTitle("");
        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this, this);
        profileItemModelList = new ArrayList<>();

        setUpRecyclerView();

        getProfile();

        showProgressLayout(true);
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_member_profile);
        adapter = new RecyclerViewAdapterMemberProfile(profileItemModelList);
        LinearLayoutManager manager = new LinearLayoutManager(MemberProfile.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private void getProfile() {


        String url = helper.baseURL + "getProfile.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid", userid + "");
        if (volleyHelper.countRequestsInFlight("Get_Profile_" + userid) == 0)
            volleyHelper.makeStringRequest(url, "Get_Profile_" + userid, params);
    }

    void addprofileItem(String itemname, String value) {
        ProfileItemModel model = new ProfileItemModel();
        model.setProfileItemName(itemname);
        if (value != null && !value.equals("null") && !value.equals("")) {
            model.setProfileItemValue(value);
        } else {
            model.setProfileItemValue("");
        }
        profileItemModelList.add(model);
        adapter.notifyItemInserted(profileItemCounter);


        profileItemCounter++;
    }

    public void showProgressLayout(boolean value) {
        if (value) {
            ProfileLayout.setVisibility(View.INVISIBLE);
            progressLayout.setVisibility(View.VISIBLE);
        } else {
            ProfileLayout.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void onResponse(String result) {
        if (result == null || result.equals("")) {
            Log.v(TAG, "Result is Null");
            Toast.makeText(getApplicationContext(), "Unable to Get Profile\nPlease Try again after some time", Toast.LENGTH_LONG).show();
        } else {
            Log.v(TAG, "Result is Not Null");
            try {
                JSONObject json = helper.getJson(result);

                if (json.getString(helper.ACTION).toLowerCase().equals("trying to get profile")) {
                    profileItemCounter = 0;

                    String image = json.getString("profile_profile_pic");
                    String first_name = json.getString("first_name");
                    String last_name = json.getString("last_name");
                    String role = json.getString("role");
                    String mobile = json.getString("profile_mobile");
                    String city = json.getString("profile_city");
                    String country = json.getString("profile_country");

                    if (image == null || image.equals("")) {
                        Log.d(TAG, "No profile picture");
                    } else {
                        Log.d(TAG, "Setting memeber profile picture");
                        memberProfilePic.setImageBitmap(helper.getBitmapFromString(image,"user " + userid));
                    }


                    addprofileItem("First Name", first_name);
                    addprofileItem("Last Name", last_name);
                    addprofileItem("Mobile", mobile);
                    addprofileItem("City", city);
                    addprofileItem("Country", country);

                    if (role.equals(Helper.Instance.ROLE_MEMBER) ||
                            role.equals(Helper.Instance.ROLE_CONTRIBUTOR) ||
                            role.equals(Helper.Instance.ROLE_MODERATOR) ||
                            role.equals(Helper.Instance.ROLE_ADMIN)) {
                        String bio = json.getString("profile_bio");
                        addprofileItem("Bio", bio);
                    }
                    if (role.equals(Helper.Instance.ROLE_CONTRIBUTOR) ||
                            role.equals(Helper.Instance.ROLE_MODERATOR) ||
                            role.equals(Helper.Instance.ROLE_ADMIN)) {

                        String brief = json.getString("profile_brief");
                        addprofileItem("Brief Profile", brief);

                        String experience = json.getString("profile_experience");
                        addprofileItem("Experience", experience);

                        String certifications = json.getString("profile_certifications");
                        addprofileItem("Certifications and Qualifications", certifications);

                        int followers = json.getInt("followers");
                        addprofileItem("Followers", followers + "");

                    }

                    addprofileItem("Role", role);
                }
            } catch (JSONException jse) {
                Toast.makeText(getApplicationContext(), "Unable to Get Profile\nPlease Try again after some time", Toast.LENGTH_LONG).show();
                finish();
            } finally {
                showProgressLayout(false);
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
