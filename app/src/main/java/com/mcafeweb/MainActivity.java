package com.mcafeweb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.crystal.crystalpreloaders.widgets.CrystalPreloader;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mcafeweb.Models.BlogModel;
import com.mcafeweb.RecyclerViews.RecyclerViewAdapterBlog;
import com.mcafeweb.app.AppController;
import com.mcafeweb.utils.DBHelper;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
public class MainActivity extends AppCompatActivity implements VolleyHelper.VolleyResponse {

    //private TabLayout tabLayout;
    //private ViewPager viewPager;
    //int currentPosition = 0;

    final String TAG = "MainActivity";

    RecyclerView blogRecyclerView;
    RecyclerView.Adapter blogAdapter;
    List<BlogModel> blogModelList;

    Helper helper;

    int originalBlogCount = 0;
    //int difference = 0;
    int blogCount = 0;
    int blog_counter = 0;
    FloatingActionMenu fabTabBlog;

    CrystalPreloader crytsalProgressBar;

    DBHelper dbHelper;
    SQLiteDatabase db;
    VolleyHelper volleyHelper;

    //Handler mHandler;
    //Runnable mHandlerTask;
    //Runnable runnable;

    int[] blogIDs;

    SwipeRefreshLayout swipeRefreshLayout;

    //private final static int INTERVAL = 1000 * 8; //2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().setTitle("mCafe");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.app_launcher);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupDatabase();

        //setUpTabLayout();
        blogRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_blogs);
        crytsalProgressBar = (CrystalPreloader) findViewById(R.id.crystal_progress_bar_tab_main);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutBlogs);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBlogCount();
                sortBlogsByDate();

            }
        });
        blogModelList = new ArrayList<>();
        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this, this);
        setupDatabase();
        setUpBlogRecyclerView();
        ManageFloatingMenu();

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

//        CheckPreviosBlogs();

        getBlogCount();

    }

    private void sortBlogsByDate()
    {
        Collections.sort(blogModelList, new Comparator<BlogModel>() {
            @Override
            public int compare(BlogModel model1, BlogModel model2) {
                return helper.getDatefromString(model2.getBlogSharedDate()).compareTo(helper.getDatefromString(model1.getBlogSharedDate()));
            }
        });
        blogAdapter.notifyDataSetChanged();
    }

    private void setUpTabLayout() {
        /*tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout = setUpTablayout(tabLayout);
        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewPagerLayout);

        //Creating our pager blogAdapter
        Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding blogAdapter to pager
        viewPager.setAdapter(adapter);

        //Adding onTabSelectedListener to swipe views
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(this);
        viewPager.getAdapter().notifyDataSetChanged();
        */
    }

    private void setupDatabase() {
        dbHelper = new DBHelper(this);
        db = openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper.onCreate(db);
        db.close();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu); //your file name
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_profile:
                LoadMyProfile();
                break;
            case R.id.logout:
                Logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void LoadMyProfile() {
        Intent intent = new Intent(this, Myprofile.class);
        startActivity(intent);
    }

    private void Logout() {
        if (dbHelper.deleteAllContent()) {
            volleyHelper.removeCache();
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        } else {
            Snackbar.make(findViewById(R.id.main_layout), "Unable to Logout", Snackbar.LENGTH_SHORT);
        }

    }

    @SuppressWarnings("Since15")
    @Override
    protected void onResume() {
        super.onResume();
        getBlogCount();
    }


    /*
    private TabLayout setUpTablayout(TabLayout tabLayout) {
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());


        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(Color.parseColor("#757575"), Color.parseColor("#03A9F4"));

        tabLayout.getTabAt(0).setIcon(R.drawable.group);
        tabLayout.getTabAt(0).setText("GROUPS");
        tabLayout.getTabAt(1).setIcon(R.drawable.blog);
        tabLayout.getTabAt(1).setText("BLOGS");
        tabLayout.getTabAt(2).setIcon(R.drawable.event);
        tabLayout.getTabAt(2).setText("EVENTS");
        return tabLayout;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (viewPager != null) {
            viewPager.setCurrentItem(tab.getPosition());
            tabLayout.getTabAt(tab.getPosition()).select();
            currentPosition = tab.getPosition();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            doExit();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void doExit() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialog.setNegativeButton("No", null);

        alertDialog.setMessage("Do you want to exit?");
        alertDialog.setTitle("Mcafe");
        alertDialog.show();
    }


    private void CheckPreviosBlogs() {
        /*int previousrows = dbHelper.checkPreviousBlogs();
        if (previousrows <=0) {
            getBlogCount();
        } else {
            Log.v(TAG, "There is content in database");
            BlogModel[] rows = dbHelper.getBlogRows( DBHelper.TABLE_BLOG);

            for (int i = 0; i < previousrows; i++) {
                blogModelList.add(0, rows[i]);
                blogAdapter.notifyItemInserted(0);
            }
            originalBlogCount = blogModelList.size();
            //blogRecyclerView.smoothScrollToPosition(blogModelList.size());


        }
        */
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
            if (volleyHelper.countRequestsInFlight("Get_Blog_Count_"+id) == 0) {
                volleyHelper.makeStringRequest(url, "Get_Blog_Count_"+id, params);
            }
        }

    }

    private void getBlogData(int blogID) {
        ShowProgressBar(true);

        String url = helper.baseURL + "getBlogData.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("blogid", blogID + "");


        /*
        if (volleyHelper.checkCachedData(url)) {
            Log.d(TAG, "Blog data Cached");
            volleyHelper.invalidateCachedData(url);
        }
        */
        Log.d(TAG, "Making blog data request");
        volleyHelper.makeCachedRequest(url, "Get_Blog_Data", params);
    }

    private void ManageFloatingMenu() {
        fabTabBlog = (FloatingActionMenu) findViewById(R.id.fam_blogs);


        FloatingActionButton buttonAddBlog = new FloatingActionButton(getApplicationContext());
        FloatingActionButton buttonSearchBlog = new FloatingActionButton(getApplicationContext());
        FloatingActionButton buttonFavBlogs = new FloatingActionButton(getApplicationContext());
        FloatingActionButton buttonMyCuratedBlogs = new FloatingActionButton(getApplicationContext());
        //FloatingActionButton buttonContributorsIFollow = new FloatingActionButton(getApplicationContext());
        FloatingActionButton buttonCreateContributor = new FloatingActionButton(getApplicationContext());

        buttonAddBlog.setLabelText("Add Blog");
        buttonSearchBlog.setLabelText("Search Blog");
        buttonFavBlogs.setLabelText("My Fav Blogs");
        buttonMyCuratedBlogs.setLabelText("My Curated Blogs");
        //buttonContributorsIFollow.setLabelText("Contributors I Follow");
        buttonCreateContributor.setLabelText("Create Contributor");

        buttonAddBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFabMenu();
                Intent intent = new Intent(getApplicationContext(), CurateBlog.class);
                startActivity(intent);
                Log.v(TAG, "Create Blog");

            }
        });


        buttonSearchBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFabMenu();
                Intent intent = new Intent(getApplicationContext(), SearchBlog.class);
                startActivity(intent);
            }
        });

        buttonFavBlogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFabMenu();
                Intent intent = new Intent(getApplicationContext(), MyFavBlogs.class);
                startActivity(intent);
            }
        });

        buttonMyCuratedBlogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFabMenu();
                Intent intent = new Intent(getApplicationContext(), MyCuratedBlogs.class);
                startActivity(intent);
            }
        });
/*
        buttonContributorsIFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFabMenu();
                Intent intent = new Intent(getApplicationContext(), ContributorsIFollow.class);
                startActivity(intent);
            }
        });
*/
        buttonCreateContributor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateContributor.class);
                startActivity(intent);
            }
        });

        fabTabBlog.addMenuButton(buttonSearchBlog);
        fabTabBlog.addMenuButton(buttonFavBlogs);
        //fabTabBlog.addMenuButton(buttonContributorsIFollow);
        if (dbHelper.getUserRole().equals(Helper.Instance.ROLE_ADMIN)) {

            fabTabBlog.addMenuButton(buttonAddBlog);
            fabTabBlog.addMenuButton(buttonMyCuratedBlogs);
            fabTabBlog.addMenuButton(buttonCreateContributor);
        } else if (dbHelper.getUserRole().equals(Helper.Instance.ROLE_CONTRIBUTOR)) {
            fabTabBlog.addMenuButton(buttonMyCuratedBlogs);
            fabTabBlog.addMenuButton(buttonAddBlog);
        }

    }


    private void closeFabMenu()
    {
        if(fabTabBlog.isOpened())
            fabTabBlog.close(true);
    }

    private void setUpBlogRecyclerView() {
        blogAdapter = new RecyclerViewAdapterBlog(blogModelList,this);
        Log.d("BLOGS", "Adapter init");
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
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
                            //if (!dbHelper.checkExistingBlog( blogIDs[i],DBHelper.TABLE_BLOG)) {

                            boolean previouslyAdded = false;
                            for (BlogModel blogmodel :
                                    blogModelList) {
                                if (blogmodel.getBlogID() == blogIDs[i]) {
                                    previouslyAdded = true;
                                }
                            }
                            Log.d(TAG, "Previously Added : " + blogIDs + " = " + previouslyAdded);
                            ShowProgressBar(false);
                            if (!previouslyAdded) {
                                getBlogData(blogIDs[i]);
                                ShowProgressBar(true);
                            }
                            //}
                        }
                    }

                } else {
                    ShowProgressBar(false);

                }
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        } finally {

        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResponse(JSONObject result) {

    }

    @Override
    public void onResponse(JSONArray result) {

    }

    @Override
    public void onResponse(NetworkResponse result) {
        swipeRefreshLayout.setRefreshing(false);
        ShowProgressBar(false);
        try {
            final String jsonString = new String(result.data,
                    HttpHeaderParser.parseCharset(result.headers));
            JSONObject json = new JSONObject(jsonString);
/*
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
                                //if (!dbHelper.checkExistingBlog( blogIDs[i],DBHelper.TABLE_BLOG)) {

                                boolean previouslyAdded = false;
                                for (BlogModel blogmodel :
                                        blogModelList) {
                                    if (blogmodel.getBlogID() == blogIDs[i]) {
                                        previouslyAdded = true;
                                    }
                                }
                                Log.d(TAG, "Previously Added : " + blogIDs + " = " + previouslyAdded);
                                if (!previouslyAdded) {
                                    getBlogData(blogIDs[i]);
                                    ShowProgressBar(true);
                                }
                                //}
                            }
                        }

                        ShowProgressBar(false);
                    } else {
                        ShowProgressBar(false);
                    }
                }
                else
                    */
            if (json.getString(helper.ACTION).toLowerCase().equals("trying to get blog data")) {

                if (json.getString("get_blog_data_result").equals(helper.SUCCESS)) {
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
                    String sharerName = json.getString("name");


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
                    model.setSharerName(sharerName);

                    blogModelList.add(model);
                    blogAdapter.notifyItemInserted(blog_counter);
                    originalBlogCount = blogModelList.size();
                    blog_counter++;
                    //dbHelper.addBlogToTable( model,DBHelper.TABLE_BLOG);
                    /*Collections.sort(blogModelList, new Comparator<BlogModel>() {
                        @Override
                        public int compare(BlogModel blogModel, BlogModel t1) {
                            return blogModel.getBlogID()>t1.getBlogID()?blogModel.getBlogID():t1.getBlogID();
                        }
                    });
                    */

                    /*if(blog_counter == blogIDs.length) {
                        mHandlerTask = runnable;
                        mHandlerTask.run();
                    }
                    */
                }
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
            //Toast.makeText(getContext(), "Unable to Get Blogs\nPlease Try again after some time", Toast.LENGTH_LONG).show();
        } catch (IOException ioe) {
            ioe.printStackTrace();

        } finally {
            ShowProgressBar(false);
        }
    }

    @Override
    public void onError(VolleyError error) {
        Toast.makeText(this, "Connection is slow..", Toast.LENGTH_SHORT).show();
    }
}
