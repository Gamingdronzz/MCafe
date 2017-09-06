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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.mcafeweb.Models.InterestModel;
import com.mcafeweb.RecyclerViews.RecyclerViewAdapterInterests;
import com.mcafeweb.utils.DBHelper;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterestSelection extends AppCompatActivity implements VolleyHelper.VolleyResponse {


    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<InterestModel> interestModelList;
    LinearLayout progressLayout;
    TextView progressText;
    List<String> userInterets;
    final String TAG = "InterestSelection";
    Helper helper;

    int submitCounter = 0;
    DBHelper dbHelper;
    SQLiteDatabase db;
    VolleyHelper volleyHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        getSupportActionBar().setTitle("Choose Your Interests");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_categories);
        progressLayout = (LinearLayout) findViewById(R.id.progressLayoutInterests);
        userInterets = new ArrayList<String>();
        progressText = (TextView) findViewById(R.id.progress_text_interests);
        progressText.setText("Getting Interest List...\nPlease Hold on");
        interestModelList = new ArrayList<>();


        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this,this);
        setupDatabase();


        showProgressLayout(true);
        getInterestList();
        setUpRecyclerView();
    }

    private void setupDatabase()
    {
        dbHelper = new DBHelper(this);
        db = openOrCreateDatabase(DBHelper.DATABASE_NAME,Context.MODE_PRIVATE,null);
        dbHelper.onCreate(db);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_category_selection, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.submit_interests:
                SubmitInterests();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getInterestList() {

        String url = helper.baseURL + "getInterestList.php5";
        volleyHelper.makeStringRequest(url,"getInterestList");
    }

    private void SubmitInterests() {
        int s = getSelectedItemCount();
        if (s != 0) {
            dbHelper.removeAllUserInterests();
            showProgressLayout(true);
            for (int i = 0; i < interestModelList.size(); i++) {
                if (interestModelList.get(i).isSelected()) {
                    String url = helper.baseURL + "submitInterests.php5";
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("interestid", interestModelList.get(i).getInterestID() + "");
                    params.put("userid",dbHelper.getUserID() +"");
                    volleyHelper.makeStringRequest(url,"Submit_Interest_"+interestModelList.get(i).getInterestID(),params);
                    dbHelper.addUserInterest(interestModelList.get(i).getInterestID());
                    Log.v(TAG, "Submitting Interests ");
                }
            }
        } else {
            View view = findViewById(R.id.coordinator_layout);
            Snackbar.make(view, "Please Choose atleast 1 interest", Snackbar.LENGTH_LONG).show();

        }

    }

    public int getSelectedItemCount() {
        int selectedItems = 0;
        for (int i = 0; i < interestModelList.size(); i++) {
            if (interestModelList.get(i).isSelected()) {
                userInterets.add(interestModelList.get(i).getInterestName());
                selectedItems++;
            }
        }
        return selectedItems;
    }


    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_categories);
        adapter = new RecyclerViewAdapterInterests(interestModelList);
        LinearLayoutManager manager = new LinearLayoutManager(InterestSelection.this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    public void showProgressLayout(boolean value) {
        if (value) {
            recyclerView.setVisibility(View.INVISIBLE);
            progressLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.INVISIBLE);
        }
    }

    void Login() {
        PreferencesManager manager = new PreferencesManager(this);
        manager.setLoginStatus(true);
        manager.setInterestStatus(true);
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResponse(String result) {

        Log.d(TAG, result);
        if (result == null) {
            Log.v("Login", "Result is Null");
            showProgressLayout(false);
            Toast.makeText(this, "Some Error Occured\nPlease Try Again after some time", Toast.LENGTH_LONG).show();
        } else {
            JSONObject json = helper.getJson(result);
            try {
                if (json.getString(helper.ACTION).toLowerCase().equals("trying to submit interests")) {
                    submitCounter++;

                    if (json.getString(helper.INTEREST_SUBMIT_RESULT).equals(helper.SUCCESS)) {
                        Log.v(TAG, "Interest Submitted");
                        if(submitCounter == userInterets.size())
                        {
                            Login();
                        }
                    } else {
                        Toast.makeText(this, "Unable to Submit Interest\nPlease Try Again after some time", Toast.LENGTH_LONG).show();
                        showProgressLayout(false);
                    }
                } else if (json.getString(helper.ACTION).toLowerCase().equals("trying to get interest list")) {

                    if (json.getString(helper.GET_INTEREST_LIST).equals(helper.SUCCESS)) {
                        int rows = Integer.parseInt(json.getString("row_count"));
                        for (int i = 0; i < rows; i++) {
                            int interestid = json.getInt("interest_id_"+i);
                            String interestName = json.getString("interest_name_" + i);
                            if(dbHelper.addInterestToTable(interestid,interestName) != -1)
                            {
                                Log.v(TAG,"Succesfully Added Interest "+ interestid + " to Table");
                            }
                            else
                            {
                                Log.v(TAG,"Unable to add "+ interestid + " to Table");
                            }
                            InterestModel model = new InterestModel();
                            model.setInterestID(interestid);
                            model.setInterestName(interestName);
                            interestModelList.add(model);
                            adapter.notifyItemInserted(i);

                        }
                        progressText.setText("Submitting your preferences...");
                    } else {
                        Toast.makeText(this, "Unable to Fetch Interest List\nPlease Try Again after some time", Toast.LENGTH_LONG).show();
                    }
                }

            } catch (JSONException jse) {
                Toast.makeText(this, "Some Error Occured\nPlease Try Again after some time", Toast.LENGTH_LONG).show();
                jse.printStackTrace();
            }
            finally {
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
        showProgressLayout(false);
    }
}
