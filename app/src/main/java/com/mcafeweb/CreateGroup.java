package com.mcafeweb;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mcafeweb.Models.InterestModel;
import com.mcafeweb.utils.DBHelper;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.utils.VolleyHelper;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroup extends AppCompatActivity implements VolleyHelper.VolleyResponse{
    private FloatLabeledEditText EditTextGroupName;
    private FloatLabeledEditText EditTextGroupDescription;
    private Spinner GroupPrivacy;
    private Spinner GroupCategory;

    NestedScrollView CreateGroupLayout;
    LinearLayout progressLayout;

    private Button buttonChooseImage;
    private Button buttonCreateGroup;

    private int PICK_IMAGE_REQUEST = 1;
    String image = null;

    int selectedCategory;
    String selectedPrivacy;

    DBHelper dbHelper;
    SQLiteDatabase db;
    VolleyHelper volleyHelper;
    Helper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        volleyHelper = new VolleyHelper(this,this);
        setupDatabase();
        bindViews();
        init();


    }

    private void setupDatabase() {
        dbHelper = new DBHelper(this);
        db = openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE,null);
        dbHelper.onCreate(db);
    }

    private void bindViews() {
        CreateGroupLayout = (NestedScrollView) findViewById(R.id.ScrollViewCreateGroupLayout);
        progressLayout = (LinearLayout) findViewById(R.id.progressLayout);
        EditTextGroupName = (FloatLabeledEditText) findViewById(R.id.create_Group_Group_Name);
        EditTextGroupDescription = (FloatLabeledEditText) findViewById(R.id.groupDescription);
        GroupCategory = (Spinner) findViewById(R.id.spinner_Group_Category);
        GroupPrivacy = (Spinner) findViewById(R.id.spinner_Group_Privacy);
        buttonChooseImage = (Button) findViewById(R.id.chooseImageButton);
        buttonCreateGroup = (Button) findViewById(R.id.createGroupButton);
    }

    private void init(){
        helper = new Helper(this);
        showProgressLayout(false);

        List<String> Categories = new ArrayList<>();

        InterestModel[] interestList = dbHelper.getInterestList();

        for (int i = 0; i < interestList.length; i++) {
            Categories.add(interestList[i].getInterestName());
        }

        List<String> PrivacyModes = new ArrayList<>();
        PrivacyModes.add("Private");
        PrivacyModes.add("Public");

        ArrayAdapter<String> categoryDataAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, Categories);

        ArrayAdapter<String> privacyDataAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, PrivacyModes);
        categoryDataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        GroupCategory.setAdapter(categoryDataAdapter);
        GroupPrivacy.setAdapter(privacyDataAdapter);


        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = helper.getImageIntent();
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });


        buttonCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateGroup();
            }
        });
    }
    /*
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        selectedCategory = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + selectedCategory, Toast.LENGTH_LONG).show();

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }
    */


    private void CreateGroup() {
        if (checkInput()) {
            showProgressLayout(true);
            selectedCategory = dbHelper.getInterestID(GroupCategory.getSelectedItem().toString());
            selectedPrivacy = GroupPrivacy.getSelectedItem().toString();

            String groupName = EditTextGroupName.getEditText().getText().toString();
            String groupDescription = EditTextGroupDescription.getEditText().getText().toString();

            String url = helper.baseURL + "createGroup.php5";
            Map<String, String> params = new HashMap<String, String>();
            params.put("group_name", groupName);
            params.put("group_description", groupDescription);
            params.put("group_category",selectedCategory+"");
            params.put("group_image",image);
            params.put("group_created_by",dbHelper.getUserID()+"");
            params.put("group_privacy",selectedPrivacy);

            volleyHelper.makeStringRequest(url,"Create_Group",params);
        } else {
            Toast.makeText(this, "Please fill all Details", Toast.LENGTH_SHORT).show();
        }
    }

    boolean checkInput() {
        String groupName = EditTextGroupName.getEditText().getText().toString();
        groupName = groupName.replaceAll("\\s+", "");
        EditTextGroupName.getEditText().setText(groupName);

        String groupDescription = EditTextGroupDescription.getEditText().getText().toString();
        groupDescription = groupDescription.replaceAll("\\s+", "");
        EditTextGroupDescription.getEditText().setText(groupDescription);


        if (groupName.length() == 0) {
            Toast.makeText(this, "Enter Group Name", Toast.LENGTH_SHORT).show();
            EditTextGroupName.requestFocus();
            return false;
        }
        if (groupDescription.length() == 0) {
            Toast.makeText(this, "Enter Group Description", Toast.LENGTH_SHORT).show();
            EditTextGroupDescription.requestFocus();
            return false;
        }

        if (image == null) {
            Toast.makeText(this, "Please select a group image", Toast.LENGTH_SHORT).show();
            buttonChooseImage.performClick();
            return false;
        }
        return true;
    }

    public void showProgressLayout(boolean value) {
        if (value) {
            CreateGroupLayout.setVisibility(View.INVISIBLE);
            progressLayout.setVisibility(View.VISIBLE);
        } else {
            CreateGroupLayout.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.INVISIBLE);
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                image = helper.getStringFromBitmap(bitmap);
                // Log.d(TAG, String.valueOf(bitmap));

                //ImageView imageView = (ImageView) findViewById(R.id.imageView);
                //imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResponse(String result) {
        showProgressLayout(false);
        Helper helper = new Helper(this);
        JSONObject json = helper.getJson(result);
        try {
            if (json.getString(helper.CREATE_GROUP_RESULT).equals(helper.SUCCESS)) {
                Toast.makeText(this, "Successfully Created Group", Toast.LENGTH_LONG).show();
                finish();
            } else if (json.getString(helper.CREATE_GROUP_RESULT).equals(helper.FAILURE)) {
                Toast.makeText(this, "Unable to Create Group\nPlease Try again after some time", Toast.LENGTH_LONG).show();
            } else if (json.getString(helper.CREATE_GROUP_RESULT).equals("Group Exists")) {
                Toast.makeText(this, "There is already a group with the same name\nPlease enter a different name", Toast.LENGTH_LONG).show();
                EditTextGroupName.requestFocus();
            }
        } catch (JSONException jse) {
            Toast.makeText(this, "Unable to Create Group\nPlease Try again after some time", Toast.LENGTH_LONG).show();
            jse.printStackTrace();
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
