package com.mcafeweb;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.crystal.crystalpreloaders.widgets.CrystalPreloader;
//import com.daimajia.numberprogressbar.NumberProgressBar;
import com.mcafeweb.Models.BlogModel;
import com.mcafeweb.RecyclerViews.RecyclerViewAdapterBlog;
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

public class SearchBlog extends AppCompatActivity implements VolleyHelper.VolleyResponse {
    FloatLabeledEditText searchString;
    SquareImageView searchButton;
    Helper helper;
    VolleyHelper volleyHelper;

    RecyclerView blogRecyclerView;
    RecyclerView.Adapter blogAdapter;
    List<BlogModel> blogModelList;
    int blogCount;
    int blog_counter = 0;

    final String TAG = "Search Blog";
    InputMethodManager inputMethodManager;

    CrystalPreloader crytsalProgressBar;
    List<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_blog);
        getSupportActionBar().setTitle("Search Blogs");

        blogRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_search_blog);
        crytsalProgressBar = (CrystalPreloader) findViewById(R.id.crystal_progress_bar_search_blog);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        blogModelList = new ArrayList<>();
        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this,this);
        ShowProgressBar(false);

        searchString = (FloatLabeledEditText) findViewById(R.id.EditText_Search_Blog);
        searchButton = (SquareImageView) findViewById(R.id.buttonSearchBlog);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftInput();
                getBlogCount();
            }
        });

        setUpBlogRecyclerView();
    }

    void hideSoftInput() {
        inputMethodManager.hideSoftInputFromWindow(searchString.getWindowToken(), 0);
    }

    private void setUpBlogRecyclerView() {

        blogAdapter = new RecyclerViewAdapterBlog(blogModelList,this);
        Log.d(TAG, "Adapter init");
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        blogRecyclerView.setLayoutManager(manager);
        Log.d(TAG, "Manager set");
        blogRecyclerView.setAdapter(blogAdapter);
        Log.d(TAG, "Adapter set");

        blogRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListeners(getApplicationContext(), blogRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    void getBlogCount() {
        tasks.clear();
        tasks.removeAll(tasks);
        ShowProgressBar(true);
        blogModelList.clear();
        blogAdapter.notifyDataSetChanged();
        blog_counter = 0;
        ShowProgressBar(true);

        String url = helper.baseURL + "getBlogCountFromSearch.php5";
        Map<String, String> params = new HashMap<String, String>();
        if(searchString.getEditText().getText().toString().replaceAll("\\s+", "").equals(""))
        {
            params.put("searchValue","");
            params.put("searchType","1");
        }
        else
        {
            params.put("searchValue",searchString.getEditText().getText().toString());
            params.put("searchType","2");
        }
        if(volleyHelper.countRequestsInFlight("Get_Blog_Count_From_Search") == 0)
        {
            volleyHelper.makeStringRequest(url, "Get_Blog_Count_From_Search", params);
        }
    }

    void ShowProgressBar(boolean show) {
        if (show) {
            crytsalProgressBar.setVisibility(View.VISIBLE);
        } else {
            crytsalProgressBar.setVisibility(View.INVISIBLE);
        }
    }


    void getBlogData(int blogID) {

        String url = helper.baseURL + "getBlogData.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("blogid", blogID + "");
        volleyHelper.makeCachedRequest(url, "Get_Blog_Date_From_Search", params);
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
            Toast.makeText(getApplicationContext(), "Unable to Get Blogs\nPlease Try again after some time", Toast.LENGTH_LONG).show();
        } else {
            Log.v(TAG, "Result is Not Null");
            Helper helper = new Helper(getApplicationContext());
            JSONObject json = helper.getJson(result);

            try {
                if (json.getString(helper.ACTION).toLowerCase().equals("trying to get blog count from search")) {

                    if (json.getString("get_blog_count_result").equals(Helper.Instance.SUCCESS)) {
                        blogCount = json.getInt("row_count");
                        int[] blogIDs = new int[blogCount];
                        for (int i = 0; i < blogCount; i++) {
                            blogIDs[i] = json.getInt("blog_id_" + i);
                            getBlogData(blogIDs[i]);
                            ShowProgressBar(true);
                        }
                        ShowProgressBar(false);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Blogs yet", Toast.LENGTH_LONG).show();
                        ShowProgressBar(false);
                    }
                }
            }
            catch (JSONException jse){
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
                 if (json.getString(helper.ACTION).toLowerCase().equals("trying to get blog data")) {
                     int blog_ID = json.getInt("blog_id");
                     String blog_title = json.getString("blog_title");
                     String blogBrief = json.getString("blog_brief");
                     String blogCanonicalLink = json.getString("blog_canonical_link");
                     String blogImage = json.getString("blog_image");
                     int blog_Category = json.getInt("blog_category");
                     String blog_url = json.getString("blog_link");
                     String blog_shared_date = json.getString("blog_shared_date");
                     int blog_shared_by_user = json.getInt("blog_shared_by");
                     int blog_Likes = json.getInt("blog_likes");
                     int blog_Shares = json.getInt("blog_shares");
                     int blog_Views = json.getInt("blog_views");
                     String SharerName = json.getString("name");


                     BlogModel model = new BlogModel();
                     model.setTitle(blog_title);
                     model.setBlogID(blog_ID);
                     model.setBlogUrl(blog_url);
                     model.setBlogSharedByUser(blog_shared_by_user);
                     model.setBlogCategory(blog_Category);
                     model.setBlogLikes(blog_Likes);
                     model.setBlogShares(blog_Shares);
                     model.setBlogViews(blog_Views);
                     model.setBlogSharedDate(blog_shared_date);
                     model.setBlogBrief(blogBrief);
                     model.setSharerName(SharerName);
                     model.setBlogImage(helper.getBitmapFromString(blogImage,"search blog = " + blog_ID));
                     model.setCanonicalURL(blogCanonicalLink);

                    blogModelList.add(model);
                    blogAdapter.notifyItemInserted(blog_counter);
                    blog_counter++;
                }
            } catch (JSONException jse) {
                Toast.makeText(getApplicationContext(), "Unable to Get Blogs\nPlease Try again after some time", Toast.LENGTH_LONG).show();
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
        Toast.makeText(this,"Some error occured",Toast.LENGTH_SHORT).show();
    }
}
