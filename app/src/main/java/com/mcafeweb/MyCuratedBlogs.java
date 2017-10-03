package com.mcafeweb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
import com.mcafeweb.app.AppController;
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

public class MyCuratedBlogs extends AppCompatActivity implements VolleyHelper.VolleyResponse {

    RecyclerView myCuratedBlogsRecyclerView;
    RecyclerView.Adapter myCuratedBlogAdapter;
    List<BlogModel> myCuratedBlogModelList;
    final String TAG = "MyCuratedBlogs";
    Helper helper;
    int blogCount = 0;
    int blog_counter = 0;

    DBHelper dbHelper;
    SQLiteDatabase db;
    VolleyHelper volleyHelper;

    CrystalPreloader crytsalProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_curated_blogs);
        setupDatabase();

        getSupportActionBar().setTitle("My Curated Blogs");

        myCuratedBlogsRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_my_curated_blogs);
        crytsalProgressBar = (CrystalPreloader) findViewById(R.id.crystal_progress_bar_curated_blogs);
        myCuratedBlogModelList = new ArrayList<>();
        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this,this);
        setUpmyCuratedBlogsRecyclerView();
        getMyCuratedBlogCount();
        //getFavBlogCount();
    }

    private void setUpmyCuratedBlogsRecyclerView() {
        myCuratedBlogAdapter = new RecyclerViewAdapterBlog(myCuratedBlogModelList,this);
        Log.d("BLOGS", "Adapter init");
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        myCuratedBlogsRecyclerView.setLayoutManager(manager);
        Log.d("BLOGS", "Manager set");
        myCuratedBlogsRecyclerView.setAdapter(myCuratedBlogAdapter);
        Log.d("BLOGS", "Adapter set");

/*
        blogRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListeners(getContext(), blogRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getContext(), "Long Click on position        :"+position,
                        Toast.LENGTH_SHORT).show();
            }
        }));
*/
    }

    private void getMyCuratedBlogs() {
        /*BlogModel[] rows = dbHelper.getBlogRows(DBHelper.TABLE_MY_CURATED_BLOGS);

        if (rows != null) {
            for (int i = 0; i < rows.length; i++) {
                rows[i].setTitleSet(true);
                myCuratedBlogModelList.add(0, rows[i]);
                myCuratedBlogAdapter.notifyItemInserted(0);
            }
        } else {
            getMyCuratedBlogCount();
            //Toast.makeText(this, "No Favorite Blogs Found", Toast.LENGTH_SHORT).show();
        }
        */
    }

    private void setupDatabase() {
        dbHelper = new DBHelper(this);
        db = openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper.onCreate(db);
    }


    private void getMyCuratedBlogCount() {
        ShowProgressBar(true);
        blogCount = 0;

        String url = helper.baseURL + "getMyCuratedBlogCount.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid",dbHelper.getUserID() +"");
        volleyHelper.makeStringRequest(url,"Get_My_Curated_Blog_Count",params);

    }

    private void getMyCuratedBlogData(int blogID) {
        ShowProgressBar(true);

        String url = helper.baseURL + "getBlogData.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("blogid",blogID +"");
        volleyHelper.makeCachedRequest(url,"Get_Blog_Data_My_Curated_Blogs",params);
    }

    void ShowProgressBar(boolean show) {
        if (show) {
            crytsalProgressBar.setVisibility(View.VISIBLE);
        } else {
            crytsalProgressBar.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onResponse(String result) {
        JSONObject json = helper.getJson(result);

        try {
            if (json.getString(helper.ACTION).toLowerCase().equals("trying to get my curated blog count")) {

                if (json.getString("get_my_curated_blog_count_result").equals(Helper.Instance.SUCCESS)) {
                    blogCount = json.getInt("row_count");


                    int[] blogIDs = new int[blogCount];
                    for (int i = 0; i < blogCount; i++) {
                        blogIDs[i] = Integer.parseInt(json.getString("blog_id_" + i));
                        getMyCuratedBlogData(blogIDs[i]);
                        ShowProgressBar(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Blogs Curated Yet\nPlease Curate some blogs", Toast.LENGTH_LONG).show();
                }
            }
        }
        catch (JSONException jse)
        {
            jse.printStackTrace();
        }
        finally {

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
                    model.setBlogImage(helper.getBitmapFromString(blogImage,"my blog " + blog_ID));
                    model.setCanonicalURL(blogCanonicalLink);
                    model.setSharerName(json.getString("name"));
                    myCuratedBlogModelList.add(model);
                    myCuratedBlogAdapter.notifyItemInserted(blog_counter);
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
}
