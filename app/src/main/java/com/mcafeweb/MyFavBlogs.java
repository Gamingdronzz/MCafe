package com.mcafeweb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.mcafeweb.Models.BlogModel;
import com.mcafeweb.RecyclerViews.RecyclerViewAdapterBlog;
import com.mcafeweb.utils.DBHelper;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFavBlogs extends AppCompatActivity implements VolleyHelper.VolleyResponse {

    RecyclerView favBlogRecyclerView;
    RecyclerView.Adapter favBlogAdapter;
    List<BlogModel> favBlogModelList;
    final String TAG = "TabFavBlogs";
    Helper helper;
    int blogCount = 0;
    int blog_counter = 0;

    SwipeRefreshLayout swipeRefreshLayout;
    DBHelper dbHelper;
    SQLiteDatabase db;
    VolleyHelper volleyHelper;

    CrystalPreloader crystalPreloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_fav_blogs);
        setupDatabase();

        getSupportActionBar().setTitle("My Fav Blogs");

        favBlogRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_fav_blogs);
        crystalPreloader = (CrystalPreloader) findViewById(R.id.crystal_progress_bar_fav_blogs);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayouFavBlogs);
        favBlogModelList = new ArrayList<>();
        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this,this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sortBlogsByDate();
            }
        });
        setUpFavBlogRecyclerView();
        getFavBlogCount();
    }

    private void setUpFavBlogRecyclerView() {
        favBlogAdapter = new RecyclerViewAdapterBlog(favBlogModelList,this);
        Log.d("BLOGS", "Adapter init");
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        favBlogRecyclerView.setLayoutManager(manager);
        Log.d("BLOGS", "Manager set");
        favBlogRecyclerView.setAdapter(favBlogAdapter);
        Log.d("BLOGS", "Adapter set");
    }

    private void setupDatabase() {
        dbHelper = new DBHelper(this);
        db = openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper.onCreate(db);
    }


    private void getFavBlogCount() {
        ShowProgressBar(true);
        blogCount = 0;

        String url = helper.baseURL + "getFavBlogCount.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("favbloglistid", dbHelper.getFavBlogListId()+"");
        params.put("userid",dbHelper.getUserID() +"");
        volleyHelper.makeStringRequest(url,"Get_Fav_Blog_Count",params);
    }

    private void getFavBlogData(int blogID) {
        ShowProgressBar(true);

        String url = helper.baseURL + "getBlogData.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("blogid", blogID+"");
        volleyHelper.makeCachedRequest(url,"Get_Blog_Data",params);
    }


    void ShowProgressBar(boolean show) {
        if (show) {
            crystalPreloader.setVisibility(View.VISIBLE);
        } else {
            crystalPreloader.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onResponse(String result) {
        if (result == null || result.equals("")) {
            Log.v(TAG, "Result is Null");
            ShowProgressBar(false);
            Toast.makeText(getApplicationContext(), "Unable to Get Blogs\nPlease Try again after some time", Toast.LENGTH_LONG).show();
        } else {
            Log.v(TAG, "Result is Not Null");
            JSONObject json = helper.getJson(result);

            try {
                if (json.getString(helper.ACTION).toLowerCase().equals("trying to get fav blog count")) {

                    if (json.getString("get_fav_blog_count_result").equals(Helper.Instance.SUCCESS)) {
                        blogCount = json.getInt("row_count");


                        int[] blogIDs = new int[blogCount];
                        for (int i = 0; i < blogCount; i++) {
                            blogIDs[i] = Integer.parseInt(json.getString("blog_id_" + i));
                            getFavBlogData(blogIDs[i]);
                            ShowProgressBar(true);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No Blogs Favorited yet", Toast.LENGTH_LONG).show();
                    }
                }
            }
        catch (JSONException jse)
            {
                jse.printStackTrace();
            }
        finally {
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
        ShowProgressBar(false);
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
                    int blog_shared_by_user = Integer.parseInt(json.getString("blog_shared_by"));
                    int blog_Likes = Integer.parseInt(json.getString("blog_likes"));
                    int blog_Shares = Integer.parseInt(json.getString("blog_shares"));
                    int blog_Views = Integer.parseInt(json.getString("blog_views"));


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
                    model.setBlogImage(helper.getBitmapFromString(blogImage));
                    model.setCanonicalURL(blogCanonicalLink);
                    model.setSharerName(json.getString("name"));
                    favBlogModelList.add(model);
                    favBlogAdapter.notifyItemInserted(blog_counter);
                    Log.d(TAG,"Inserted " + model.getBlogID() + " at " + blog_counter);
                    blog_counter++;

                }
            } catch (JSONException jse) {
                Toast.makeText(getApplicationContext(), "Unable to Get Curated Blogs\nPlease Try again after some time", Toast.LENGTH_LONG).show();
            } finally {
                ShowProgressBar(false);
            }
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(VolleyError error) {
        //helper.callError(error);
    }

    private void sortBlogsByDate()
    {
        Collections.sort(favBlogModelList, new Comparator<BlogModel>() {
            @Override
            public int compare(BlogModel model1, BlogModel model2) {
                return helper.getDatefromString(model2.getBlogSharedDate()).compareTo(helper.getDatefromString(model1.getBlogSharedDate()));
            }
        });
        favBlogAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
}
