package com.mcafeweb;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.system.ErrnoException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.mcafeweb.Models.ProfileItemModel;
import com.mcafeweb.RecyclerViews.RecyclerViewAdapterMyProfile;
import com.mcafeweb.utils.DBHelper;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.utils.VolleyHelper;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Myprofile extends AppCompatActivity implements VolleyHelper.VolleyResponse {
    public static int CHOOSE_PIC = 10;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<ProfileItemModel> profileItemModelList;

    Button myInterests;
    Button contributorsIFollow;

    ImageView myProfilePic;
    String image = null;



    final String TAG = "Myprofile";

    FloatingActionButton fabRemove;
    FloatingActionButton fabChoose;

    int profileItemCounter = 0;

    InputMethodManager inputMethodManager;

    DBHelper dbHelper;

    SQLiteDatabase db;
    Helper helper;
    VolleyHelper volleyHelper;

    LinearLayout CreateBlogLayout;
    LinearLayout progressLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        setupDatabase();

        getSupportActionBar().setTitle(dbHelper.getUserFirstName() + " " + dbHelper.getUserLastName());
        CreateBlogLayout = (LinearLayout) findViewById(R.id.mail_layout_my_profile);
        progressLayout = (LinearLayout) findViewById(R.id.progress_layout);
        fabChoose = (FloatingActionButton) findViewById(R.id.fab_choose_photo);
        fabRemove = (FloatingActionButton) findViewById(R.id.fab_remove_photo);
        myProfilePic = (ImageView) findViewById(R.id.my_profile_Profile_Picture);
        contributorsIFollow = (Button) findViewById(R.id.button_contributors_i_follow);
        contributorsIFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ContributorsIFollow.class);
                startActivity(intent);
            }
        });

        fabChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),ChooseProfilePicture.class);
                startActivityForResult(intent,CHOOSE_PIC);
            }
        });

        fabRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myProfilePic.setImageBitmap(null);
                image = null;
            }
        });



        myProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        myInterests = (Button) findViewById(R.id.my_interests);
        myInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MyInterests.class);
                startActivity(intent);
            }
        });

        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this, this);
        profileItemModelList = new ArrayList<>();

        setUpRecyclerView();

        getProfile();

        showProgressLayout(true);
    }


    /**
     * Crop the image and set it back to the  cropping view.
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_PIC) {
            if(resultCode == RESULT_OK) {
                image = data.getStringExtra("image");
                myProfilePic.setImageBitmap(helper.getBitmapFromString(image));
            }
        }
    }

    private void setupDatabase() {
        dbHelper = new DBHelper(this);
        db = openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper.onCreate(db);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_profile, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.update_my_profile:
                updateMyProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void hideSoftInput() {
        inputMethodManager.hideSoftInputFromWindow(findViewById(R.id.recycler_view_profile).getWindowToken(), 0);
    }

    private void updateMyProfile() {
        showProgressLayout(true);
        Log.d(TAG, "Updating my profile");
        hideSoftInput();
        ArrayList<String> key = new ArrayList<String>();
        ArrayList<String> value = new ArrayList<String>();


        for (int i = 0; i < profileItemModelList.size(); i++) {
            String role = profileItemModelList.get(i).getProfileItemName();
            if(!role.equals("role")) {
                key.add(profileItemModelList.get(i).getProfileItemName());
                value.add(profileItemModelList.get(i).getProfileItemValue());
            }
        }

        String[] keys = new String[key.size() + 2];
        String[] values = new String[value.size() + 2];

        keys[0] = "userid";
        values[0] = dbHelper.getUserID() + "";
        keys[1] = "profilepic";
        values[1] = image;

        for (int i = 0; i < key.size(); i++) {
            keys[i + 2] = getKey(key.get(i));
            values[i + 2] = getValue(value.get(i));
            Log.d(TAG, keys[i] + " : " + values[i]);
        }

        String url = helper.baseURL + "updateMyProfile.php5";
        Map<String, String> params = new HashMap<String, String>();
        for (int i = 0; i < keys.length; i++) {
            params.put(keys[i], values[i]);
        }

        volleyHelper.makeStringRequest(url, "Update_Profile", params);
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_profile);
        adapter = new RecyclerViewAdapterMyProfile(profileItemModelList);
        LinearLayoutManager manager = new LinearLayoutManager(Myprofile.this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

    }

    String getKey(String value) {
        String result = value.replace(" ", "");
        result = result.toLowerCase();
        return result;
    }

    String getValue(String value) {
        String result = "";
        if (value.equals("") || value == null || value.equals("null")) {
        } else {
            result = value;
        }
        return result;
    }

    private void getProfile() {

        String url = helper.baseURL + "getProfile.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid", dbHelper.getUserID() + "");

        if (volleyHelper.countRequestsInFlight("My_Profile") == 0) {
            if (volleyHelper.checkCachedData(url)) {
                volleyHelper.invalidateCachedData(url);
            }
            volleyHelper.makeStringRequest(url, "My_Profile", params);
        }


    }

    void addprofileItem(String itemname, String value) {
        ProfileItemModel model = new ProfileItemModel();
        model.setProfileItemName(itemname);
        if (value == null || value.equals("null") || value.equals("")) {
            model.setProfileItemValue("");
        } else {
            model.setProfileItemValue(value);
        }
        profileItemModelList.add(model);
        adapter.notifyItemInserted(profileItemCounter);


        profileItemCounter++;
    }

    public void showProgressLayout(boolean value) {
        if (value) {
            CreateBlogLayout.setVisibility(View.INVISIBLE);
            progressLayout.setVisibility(View.VISIBLE);
        } else {
            CreateBlogLayout.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void onResponse(String result) {
        JSONObject json = helper.getJson(result);
        showProgressLayout(false);

        try{
        if (json.getString(helper.ACTION).toLowerCase().equals("trying to get profile")) {
            profileItemCounter = 0;

            String first_name = json.getString("first_name");
            addprofileItem("First Name", first_name);

            String last_name = json.getString("last_name");
            addprofileItem("Last Name", last_name);

            String role = json.getString("role");

            String mobile = json.getString("profile_mobile");
            addprofileItem("Mobile", mobile);

            String city = json.getString("profile_city");
            addprofileItem("City", city);

            String country = json.getString("profile_country");
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
                addprofileItem("Followers",followers+"");
                showProgressLayout(false);

            }

            addprofileItem("Role",role);

            image = json.getString("profile_profile_pic");
            if (image.equals("") || image == null) {
                myProfilePic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.app_launcher));
            } else {
                myProfilePic.setImageBitmap(helper.getBitmapFromString(image));
            }
        } else if (json.getString(helper.ACTION).toLowerCase().equals("trying to update my profile")) {
            if (json.getString("update_my_profile_result").equals(helper.SUCCESS)) {
                Toast.makeText(getApplicationContext(), "Profile Updated Succesfully", Toast.LENGTH_LONG).show();
                showProgressLayout(false);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Unable to Update Profile\nPlease Try again after some time", Toast.LENGTH_LONG).show();
            }
        }
    }
        catch (JSONException jse)
    {
        jse.printStackTrace();
        showProgressLayout(false);
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
        error.printStackTrace();
        showProgressLayout(false);
        Toast.makeText(this, "Try again after some time", Toast.LENGTH_SHORT);
    }
}

