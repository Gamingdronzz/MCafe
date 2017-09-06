package com.mcafeweb.Tabs;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.crystal.crystalpreloaders.widgets.CrystalPreloader;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;


import com.mcafeweb.utils.DBHelper;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.CurateBlog;
import com.mcafeweb.Models.BlogModel;
import com.mcafeweb.MyFavBlogs;
import com.mcafeweb.R;
import com.mcafeweb.RecyclerViews.RecyclerViewAdapterBlog;

import com.mcafeweb.SearchBlog;
import com.mcafeweb.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Balpreet on 15-Mar-17.
 */
public class TabBlogs extends Fragment implements VolleyHelper.VolleyResponse {

    RecyclerView blogRecyclerView;
    RecyclerView.Adapter blogAdapter;
    List<BlogModel> blogModelList;
    final String TAG = "Tab Blogs";

    Helper helper;
    VolleyHelper volleyHelper;

    int originalBlogCount = 0;
    int difference = 0;
    int blogCount = 0;
    int blog_counter = 0;
    FloatingActionMenu fabTabBlog;

    CrystalPreloader crytsalProgressBar;

    DBHelper dbHelper;
    SQLiteDatabase db;

    Context context;

    //Handler mHandler;
    //Runnable mHandlerTask;
    //Runnable runnable;

    int[] blogIDs;

    SwipeRefreshLayout swipeRefreshLayout;

    //private final static int INTERVAL = 1000 * 8; //2 seconds

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tabblogs, container, false);
        context = rootView.getContext();

        blogRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_blogs);
        crytsalProgressBar = (CrystalPreloader) rootView.findViewById(R.id.crystal_progress_bar_tab_blogs);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayoutBlogs);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBlogCount();
            }
        });
        blogModelList = new ArrayList<>();
        helper = new Helper(rootView.getContext());
        volleyHelper = new VolleyHelper(this,getContext());
        setupDatabase();
        setUpBlogRecyclerView(rootView);
        ManageFloatingMenu(rootView);

        /*mHandler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                getBlogCount();
                doTask();
                Log.v(TAG, "Running Handler Task");
            }
        };
        */

        CheckPreviosBlogs();

        return rootView;
    }

    private void CheckPreviosBlogs() {
        /*int previousrows = dbHelper.checkPreviousBlogs();
        if (previousrows == -1) {
            getBlogCount();
        } else {
            Log.v(TAG, "There is content in database");
            BlogModel[] rows = dbHelper.getBlogRows( DBHelper.TABLE_BLOG);

            for (int i = 0; i < previousrows; i++) {
                rows[i].setTitleSet(true);
                rows[i].setTitleSet(true);
                blogModelList.add(0, rows[i]);
                blogAdapter.notifyItemInserted(0);
            }
            originalBlogCount = blogModelList.size();
            //blogRecyclerView.smoothScrollToPosition(blogModelList.size());


        }
        */
    }

    private void setupDatabase() {
        dbHelper = new DBHelper(context);
        db = context.openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper.onCreate(db);
    }

    private void getBlogCount() {
        originalBlogCount = blogModelList.size();
        ShowProgressBar(true);
        String url = helper.baseURL + "getBlogCount.php5";
        int[] interestIds = dbHelper.getMyInterests();
        for (int id :
                interestIds) {

            Map<String, String> params = new HashMap<String, String>();
            params.put("interestid", id + "");
            volleyHelper.makeStringRequest(url,"Get_Blog_Count",params);
        }
        swipeRefreshLayout.setRefreshing(false);

    }

    private void getBlogData(int blogID) {
        ShowProgressBar(true);

        String url = helper.baseURL + "getBlogData.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("blogid", blogID + "");
        volleyHelper.makeStringRequest(url,"Get_Blog_Data",params);
    }

    @Override
    public void onResume() {
        super.onResume();
        getBlogCount();
        blogAdapter.notifyDataSetChanged();
    }

    private void ManageFloatingMenu(View view) {
        fabTabBlog = (FloatingActionMenu) view.findViewById(R.id.fam_blogs);


        FloatingActionButton buttonAddBlog = new FloatingActionButton(view.getContext());
        FloatingActionButton buttonSearchBlog = new FloatingActionButton(view.getContext());
        FloatingActionButton buttonFavBlogs = new FloatingActionButton(view.getContext());

        buttonAddBlog.setLabelText("Add Blog");
        buttonSearchBlog.setLabelText("Search Blog");
        buttonFavBlogs.setLabelText("My Fav Blogs");


        buttonAddBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CurateBlog.class);
                startActivity(intent);
                Log.v(TAG, "Create Blog");
            }
        });


        buttonSearchBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchBlog.class);
                startActivity(intent);
                Log.v(TAG,
                        "Search Blog");

            }
        });

        buttonFavBlogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MyFavBlogs.class);
                startActivity(intent);
                Log.v(TAG,
                        "My fav Blog");

            }
        });

        fabTabBlog.addMenuButton(buttonSearchBlog);
        fabTabBlog.addMenuButton(buttonFavBlogs);
        if (dbHelper.getUserRole().equals(Helper.Instance.ROLE_ADMIN)) {
            fabTabBlog.addMenuButton(buttonAddBlog);
        } else if (dbHelper.getUserRole().equals(Helper.Instance.ROLE_CONTRIBUTOR)) {
            fabTabBlog.addMenuButton(buttonAddBlog);
        }

    }


    private void setUpBlogRecyclerView(View view) {
        blogAdapter = new RecyclerViewAdapterBlog(blogModelList,(AppCompatActivity)getActivity());
        Log.d("BLOGS", "Adapter init");
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        blogRecyclerView.setLayoutManager(manager);
        Log.d("BLOGS", "Manager set");
        blogRecyclerView.setAdapter(blogAdapter);
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

    void ShowProgressBar(boolean show) {
        if (show) {
            crytsalProgressBar.setVisibility(View.VISIBLE);
        } else {
            crytsalProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //mHandler.removeCallbacks(runnable);
    }

    @Override
    public void onResponse(String result) {
        if (result == null || result.equals("")) {
            Log.v(TAG, "Result is Null");
            ShowProgressBar(false);
            Toast.makeText(getContext(), "Unable to Get Blogs\nPlease Try again after some time", Toast.LENGTH_LONG).show();
        } else {
            Log.v(TAG, "Result is Not Null");
            Helper helper = new Helper(getContext());
            JSONObject json = helper.getJson(result);
            try {
                if (json.getString(helper.ACTION).toLowerCase().equals("trying to get blog count")) {

                    if (json.getString("get_blog_count_result").equals(Helper.Instance.SUCCESS)) {
                        blogCount = json.getInt("row_count");

                        blogIDs = new int[blogCount];
                        for (int i = 0; i < blogCount; i++) {
                            blogIDs[i] = json.getInt("blog_id_" + i);
                            if (originalBlogCount == 0) {
                                Log.v(TAG, "Original Blog Count = 0");
                                getBlogData(blogIDs[i]);
                                ShowProgressBar(true);
                            } else {
                                Log.v(TAG, "Original Blog Count = " + originalBlogCount);
                                //if (!dbHelper.checkExistingBlog( blogIDs[i], DBHelper.TABLE_BLOG)) {
                                    getBlogData(blogIDs[i]);
                                    ShowProgressBar(true);
                                //}
                            }
                        }

                        ShowProgressBar(false);
                    } else {
                        ShowProgressBar(false);
                    }
                } else if (json.getString(helper.ACTION).toLowerCase().equals("trying to get blog data")) {
                    int blog_ID = json.getInt("blog_id");
                    String blog_title = json.getString("blog_title");
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

                    blogModelList.add(model);
                    blogAdapter.notifyItemInserted(blog_counter);
                    //dbHelper.addBlogToTable( model, DBHelper.TABLE_BLOG);
                    originalBlogCount = blogModelList.size();
                    blog_counter++;
                    ShowProgressBar(false);

                }
            } catch (JSONException jse) {
                ShowProgressBar(false);
                //Toast.makeText(getContext(), "Unable to Get Blogs\nPlease Try again after some time", Toast.LENGTH_LONG).show();
            } finally {
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

    }

    @Override
    public void onError(VolleyError error) {

    }
}