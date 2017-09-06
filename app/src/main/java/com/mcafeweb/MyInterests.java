package com.mcafeweb;

import android.content.Context;
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
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.mcafeweb.Models.InterestModel;
import com.mcafeweb.RecyclerViews.RecyclerViewAdapterInterests;
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

public class MyInterests extends AppCompatActivity implements VolleyHelper.VolleyResponse {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<InterestModel> interestModelList;
    LinearLayout progressLayout;
    List<String> userInterets;
    final String TAG = "MyInterestSelection";
    Helper helper;
    VolleyHelper volleyHelper;

    int submitCounter = 0;
    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_interests);


        getSupportActionBar().setTitle("Your Interests");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_my_interests);
        progressLayout = (LinearLayout) findViewById(R.id.progressLayoutMyInterests);
        userInterets = new ArrayList<String>();
        interestModelList = new ArrayList<>();


        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this, this);
        setupDatabase();
        setUpRecyclerView();
        showProgressLayout(false);

        getInterestsFromDatabase();
    }

    private void setupDatabase() {
        dbHelper = new DBHelper(this);
        db = openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper.onCreate(db);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_interest_selection, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.submit_my_interests:
                dbHelper.removeAllUserInterests();
                removePreviousInterests();
                showProgressLayout(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getInterestsFromDatabase() {
        InterestModel[] interestModels = dbHelper.getInterestList();

        int[] myinterests = dbHelper.getMyInterests();

        for (int i = 0; i < myinterests.length; i++) {
            for (int j = 0; j < interestModels.length; j++) {
                if (interestModels[j].getInterestID() == myinterests[i]) {
                    interestModels[j].setSelected(true);
                }
            }
        }

        for (int i = 0; i < interestModels.length; i++) {
            interestModelList.add(interestModels[i]);
            adapter.notifyItemInserted(i);
        }
    }


    private void SubmitInterests() {
        int s = getSelectedItemCount();
        if (s != 0) {
            submitCounter = s;
            for (int i = 0; i < interestModelList.size(); i++) {
                if (interestModelList.get(i).isSelected()) {

                    String url = helper.baseURL + "submitInterests.php5";
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("interestid", interestModelList.get(i).getInterestID() + "");
                    params.put("userid", dbHelper.getUserID() + "");
                    volleyHelper.makeStringRequest(url, "Submit_Interests_"+interestModelList.get(i).getInterestID(), params);

                    dbHelper.addUserInterest(interestModelList.get(i).getInterestID());
                    Log.v(TAG, "Submitting Interests ");
                }
            }
        } else {
            View view = findViewById(R.id.coordinator_layout);
            Snackbar.make(view, "Please Choose atleast 1 interest", Snackbar.LENGTH_LONG).show();

        }

    }

    private void removePreviousInterests() {

        String url = helper.baseURL + "removeInterests.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid", dbHelper.getUserID() + "");
        volleyHelper.makeStringRequest(url, "Remove_Interests", params);
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
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_my_interests);
        adapter = new RecyclerViewAdapterInterests(interestModelList);
        LinearLayoutManager manager = new LinearLayoutManager(MyInterests.this);
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

    @Override
    public void onResponse(String result) {
        Log.v(TAG, "My Interest Submission Finished");
        if (result == null) {
            Log.v(TAG, "Result is Null");
            showProgressLayout(false);
            Toast.makeText(this, "Some Error Occured\nPlease Try Again after some time", Toast.LENGTH_LONG).show();
        } else {
            JSONObject json = helper.getJson(result);
            try {
                if (json.getString(helper.ACTION).toLowerCase().equals("trying to delete interests")) {
                    if (json.getString("interest_delete_result").equals(helper.SUCCESS)) {
                        SubmitInterests();
                    }
                }
                if (json.getString(helper.ACTION).toLowerCase().equals("trying to submit interests")) {
                    if (json.getString(helper.INTEREST_SUBMIT_RESULT).equals(helper.SUCCESS)) {
                        Log.v(TAG, "Interest Submitted");
                        submitCounter--;
                        if (submitCounter == 0) {
                            Toast.makeText(getApplicationContext(), "Interests Updated", Toast.LENGTH_SHORT).show();
                            showProgressLayout(false);
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Unable to Submit Interest\nPlease Try Again after some time", Toast.LENGTH_LONG).show();
                        showProgressLayout(false);
                    }
                }
            } catch (JSONException jse) {
                Toast.makeText(this, "Some Error Occured\nPlease Try Again after some time", Toast.LENGTH_LONG).show();
                jse.printStackTrace();
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
        try {
            final String jsonString = new String(result.data,
                    HttpHeaderParser.parseCharset(result.headers));
            JSONObject json = new JSONObject(jsonString);

        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(VolleyError error) {
        showProgressLayout(false);
        Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show();
    }
}
