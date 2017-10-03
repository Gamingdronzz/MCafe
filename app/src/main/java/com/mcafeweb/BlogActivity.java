package com.mcafeweb;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.daasuu.ahp.AnimateHorizontalProgressBar;
import com.mcafeweb.Models.BlogModel;
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

public class BlogActivity extends AppCompatActivity implements VolleyHelper.VolleyResponse {

    private WebView webView;
    private WebSettings webSettings;
    List<Intent> targetedShareIntents = new ArrayList<Intent>();

    final String TAG = "BlogActivity";
    Helper helper;
    BlogModel blogModel;

    VolleyHelper volleyHelper;
    DBHelper dbHelper;
    SQLiteDatabase db;
    AnimateHorizontalProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        Bundle bundle = getIntent().getBundleExtra("bundle");
        setupDatabase();
        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this,this);
        blogModel = new BlogModel();

        blogModel.setTitle(bundle.getString("title"));
        blogModel.setBlogBrief(bundle.getString("brief"));
        blogModel.setBlogUrl(bundle.getString("url"));

        blogModel.setBlogID(bundle.getInt("blogid"));
        blogModel.setBlogImage(helper.getBitmapFromString(bundle.getString("image"),"blog model " + blogModel.getBlogID()));
        blogModel.setBlogCategory(bundle.getInt("category"));
        blogModel.setBlogSharedDate(bundle.getString("date"));
        blogModel.setBlogSharedByUser(bundle.getInt("contributorid"));
        blogModel.setBlogLikes(bundle.getInt("likes"));
        blogModel.setBlogViews(bundle.getInt("views"));
        blogModel.setBlogShares(bundle.getInt("shares"));

        getSupportActionBar().setTitle(blogModel.getTitle());
        Log.v(TAG, "URL = " + blogModel.getBlogSharedByUser() + " CONTRIBUTOR ID " + blogModel.getBlogSharedByUser());

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings = webView.getSettings();
        progressBar = (AnimateHorizontalProgressBar) findViewById(R.id.progress_bar_blog_activity);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.setBackgroundResource(R.color.colorPrimary);
        helper = new Helper(this);

        webSettings.setJavaScriptEnabled(true);
        webView.canGoBack();
        webView.loadUrl(blogModel.getBlogUrl());

        webView.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {

                        if (url.length() > 34) {

                            if (url.substring(0, 34).equals("http://www.facebook.com/sharer.php")) {
                                try {
                                    Intent facebookIntent = getShareIntent("facebook", "subject", webView.getUrl());
                                    // subject may not work, but if you have a url place it in text_or_url
                                    if (facebookIntent != null) {
                                        targetedShareIntents.add(facebookIntent);
                                    }
                                    Intent chooser = Intent.createChooser(targetedShareIntents.remove(0), "StateVision");
                                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                                    startActivity(chooser);
                                } catch (Exception e) {
                                    Toast.makeText(getBaseContext(), "Facebook App Not Found", Toast.LENGTH_LONG).show();
                                    view.loadUrl(url);
                                }

                                return true;
                            }
                            if (url.substring(0, 31).equals("http://twitter.com/intent/tweet")) {
                                try {
                                    Intent twitterIntent = getShareIntent("twitter", "subject", webView.getUrl());
                                    // subject may not work, but if you have a url place it in text_or_url
                                    if (twitterIntent != null) {
                                        targetedShareIntents.add(twitterIntent);
                                    }
                                    Intent chooser = Intent.createChooser(targetedShareIntents.remove(0), "StateVision");
                                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                                    startActivity(chooser);
                                } catch (Exception e) {
                                    Toast.makeText(getBaseContext(), "Twitter App Not Found", Toast.LENGTH_LONG).show();
                                    view.loadUrl(url);
                                }
                                return true;
                            }

                        }
                        view.loadUrl(url);
                        Log.v("URL : ", "" + url);
                        return true;
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        //spinner.setVisibility(View.GONE);
                    }

                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        Toast.makeText(getApplicationContext(), "We are getting things fixed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        webView.setWebChromeClient
                (
                        new WebChromeClient() {
                            @Override
                            public void onProgressChanged(WebView view, int newProgress) {
                                if (newProgress > 0)
                                    progressBar.setVisibility(View.VISIBLE);
                                progressBar.setProgress(newProgress);
                                if (newProgress >= 100) {
                                    progressBar.setVisibility(View.GONE);
                                }

                            }
                        }
                );
    }

    private void setupDatabase() {
        dbHelper = new DBHelper(this);
        db = openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper.onCreate(db);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_blog_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.id_add_to_fav_list) {
            addBlogToFavList(blogModel.getBlogID(), dbHelper.getFavBlogListId());
            return true;
        } else if (id == R.id.view_contributor) {
            viewContributorProfile(blogModel.getBlogSharedByUser());
            return true;
        } else if (id == R.id.follow_contributor) {
            followContributor(blogModel.getBlogSharedByUser());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void viewContributorProfile(int contributorID) {
        Intent intent = new Intent(this, MemberProfile.class);
        intent.putExtra("userid", contributorID);
        startActivity(intent);
    }

    private void followContributor(int contributorID) {
        if (contributorID == dbHelper.getUserID()) {
            Snackbar.make(findViewById(R.id.spinner_Blog_Category), "You cannot follow yourself", Snackbar.LENGTH_SHORT);
        } else {

            String url = helper.baseURL + "followContributor.php5";
            Map<String, String> params = new HashMap<String, String>();
            params.put("userid", dbHelper.getUserID() + "");
            params.put("contributorID", contributorID + "");
            volleyHelper.makeStringRequest(url, "Follow_Contributor", params);
        }

    }

    private void addBlogToFavList(int blogID, int favBlogListID) {

        String url = helper.baseURL + "addBlogToFavList.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid", dbHelper.getUserID() + "");
        params.put("blogid", blogID + "");
        params.put("favbloglistid", favBlogListID + "");
        volleyHelper.makeStringRequest(url, "Add_Blog_To_Fav_List", params);
    }

    private Intent getShareIntent(String type, String subject, String text) {
        boolean found = false;
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);
        System.out.println("resinfo: " + resInfo);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                        info.activityInfo.name.toLowerCase().contains(type)) {
                    share.putExtra(Intent.EXTRA_SUBJECT, subject);
                    share.putExtra(Intent.EXTRA_TEXT, text);
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found)
                return null;

            return share;
        }
        return null;
    }

    @Override
    public void onResponse(String result) {
        /*if (result == null || result.equals("")) {
            Log.v(TAG, "Result is Null");
            //showProgressLayout(false);
            Toast.makeText(this, "Unable to Add Blog to Fav List\nPlease Try again after some time", Toast.LENGTH_LONG).show();
        } else {
            Log.v("Login", "Result is Not Null");
            Helper helper = new Helper(this);
            JSONObject json = helper.getJson(result);
            try {
                if (json.getString("add_fav_blog_result").equals(helper.SUCCESS)) {
                    Toast.makeText(this, "Succesfully Added blog to fav list", Toast.LENGTH_LONG).show();
                    //dbHelper.addBlogToTable( blogModel, DBHelper.TABLE_FAV_BLOG_LIST_CONTENT);
                } else {
                    Toast.makeText(this, "This blog is already in your fav list", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException jse) {
                Toast.makeText(this, "Unable to Add Blog to Fav List\nPlease Try again after some time", Toast.LENGTH_LONG).show();
                jse.printStackTrace();
                //showProgressLayout(false);
            }
        }
        */
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
                if (json.getString("add_fav_blog_result").equals(helper.SUCCESS)) {
                    Toast.makeText(this, "Succesfully Added blog to fav list", Toast.LENGTH_LONG).show();
                    //dbHelper.addBlogToTable( blogModel, DBHelper.TABLE_FAV_BLOG_LIST_CONTENT);
                } else {
                    Toast.makeText(this, "This blog is already in your fav list", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException jse) {
                Toast.makeText(this, "Unable to Add Blog to Fav List\nPlease Try again after some time", Toast.LENGTH_LONG).show();
                jse.printStackTrace();
                //showProgressLayout(false);
            }

        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(VolleyError error) {

    }
}
